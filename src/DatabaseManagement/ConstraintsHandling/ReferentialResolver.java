package DatabaseManagement.ConstraintsHandling;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Filters;
import DatabaseManagement.Table;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.Map.Entry;

public class ReferentialResolver {

    private HashMap<Key, String> column_to_P_Constraint;
    private HashMap<DetailedKey, ArrayList<DetailedKey>> referenced_to_referencers;
    private ArrayList<DetailedKey> primaryKeys;
    private ArrayList<DetailedKey> foreignKeys;
    private ArrayList<DetailedKey> uniqueKeys;
    private boolean initialized;

    private static ReferentialResolver instance;

    private ReferentialResolver() {
        column_to_P_Constraint = new HashMap<>();
        referenced_to_referencers = new HashMap<>();
        primaryKeys = new ArrayList<>();
        foreignKeys = new ArrayList<>();
        uniqueKeys = new ArrayList<>();
        initialized = false;
    }

    public static ReferentialResolver getInstance() {
        return instance == null ? instance = new ReferentialResolver() : instance;
    }

    public DetailedKey getReferencedTable(String constraintName) {
        DetailedKey toFind = new DetailedKey(null, null, constraintName, "", "");
        for (DetailedKey key : referenced_to_referencers.keySet()) {
            if (key.equals(toFind)) {
                return key;
            }
        }

        throw new NullPointerException();
    }

    public AttributeCollection getReferencedAttributes(Table t) {
        AttributeCollection collection = new AttributeCollection();
        JSONObject attributes = MetaDataExtractor.getInstance().getTableAttributes(t);

        for (var attribute : attributes.keySet()) {
            Attribute.Name column = Attribute.Name.valueOf(attribute.toString());
            String constraintName = column_to_P_Constraint.get(new Key(t, column));

            if (constraintName != null && referenced_to_referencers.containsKey(new DetailedKey(t, column, constraintName, "", ""))) {
                collection.add(new Attribute(column, t));
            }
        }

        return collection;
    }

    public HashMap<Table, Filters> getReferencingAttributes(Table t, Attribute attribute) {
        ArrayList<DetailedKey> references = getReferences(t, attribute.getAttributeName());
        HashMap<Table, Filters> table_to_filters = new HashMap<>();

        for (DetailedKey key : references) {

            if (key.deleteRule.equals("NO ACTION")) {

                Attribute toFilterBy = new Attribute(key.column, attribute.getValue(), key.t);
                if (table_to_filters.containsKey(key.t)) {
                    table_to_filters.get(key.t).addEqual(toFilterBy);
                } else {
                    Filters filter = new Filters();
                    filter.addEqual(toFilterBy);
                    table_to_filters.put(key.t, filter);
                }
            }
        }

        return table_to_filters;
    }

    public HashMap<Table, Filters> getLenientReferencingAttributes(Table t, Attribute attribute) {
        ArrayList<DetailedKey> references = getReferences(t, attribute.getAttributeName());
        HashMap<Table, Filters> table_to_filters = new HashMap<>();

        for (DetailedKey key : references) {

            Attribute toFilterBy = new Attribute(key.column, attribute.getValue(), key.t);
            if (table_to_filters.containsKey(key.t)) {
                table_to_filters.get(key.t).addEqual(toFilterBy);
            } else {
                Filters filter = new Filters();
                filter.addEqual(toFilterBy);
                table_to_filters.put(key.t, filter);
            }
        }

        return table_to_filters;
    }

    public ArrayList<DetailedKey> getPrimaryKeys(Table t) {
        ArrayList<DetailedKey> PK = new ArrayList<>();

        for (DetailedKey key : primaryKeys) {
            if (key.t == t) {
                PK.add(key);
            }
        }
        return PK;
    }

    public AttributeCollection getReferencingAttributes(AttributeCollection PK) {
        AttributeCollection collection = new AttributeCollection();

        for (Attribute attribute : PK.attributes()) {

            ArrayList<DetailedKey> references = getReferences(attribute.getT(), attribute.getAttributeName());
            for (DetailedKey key : references) {
                collection.add(new Attribute(key.column, key.t));
            }
        }
        return collection;

    }

    public Collection<HashMap<Attribute, Attribute>> get_referencer_to_referenced(String PKConstraintName, Table referencingTable) {
        HashMap<String, HashMap<Attribute, Attribute>> referencer_to_referenced = new HashMap<>();

        DetailedKey referencedKey = getReferencedTable(PKConstraintName);

        Table referencedTable = referencedKey.t;
        ArrayList<DetailedKey> referencedPK = getPrimaryKeys(referencedTable);

        ArrayList<HashMap<Attribute, Attribute>> groups = new ArrayList<>();
        for (DetailedKey referenced : referencedPK) {

            ArrayList<DetailedKey> references = getReferences(referenced.t, referenced.column);
            for (DetailedKey reference : references) {
                if (reference.t == referencingTable && reference.position.equals(referenced.position)) {
                    Attribute referencerAtt = new Attribute(reference.column, reference.t);
                    Attribute referencedAtt = new Attribute(referenced.column, referenced.t);
                    String FKName = reference.FKName;

                    if (referencer_to_referenced.containsKey(FKName)) {
                        referencer_to_referenced.get(FKName).put(referencerAtt, referencedAtt);
                    } else {
                        HashMap<Attribute, Attribute> group = new HashMap<>();
                        group.put(referencerAtt, referencedAtt);
                        referencer_to_referenced.put(FKName, group);
                    }
                }
            }

        }
        return referencer_to_referenced.values();
    }

    private ArrayList<DetailedKey> getReferences(Table t, Attribute.Name column) {
        String constraintName = column_to_P_Constraint.get(new Key(t, column));

        String position = "";
        for (DetailedKey pk : primaryKeys) {
            if (pk.t == t && pk.column == column) {
                position = pk.position;
            }
        }
        DetailedKey key = new DetailedKey(t, column, constraintName, "", position);

        if (constraintName == null || !referenced_to_referencers.containsKey(key)) {
            return new ArrayList<>();
        } else {
            ArrayList<DetailedKey> allReferences = referenced_to_referencers.get(key);
            ArrayList<DetailedKey> toReturn = new ArrayList<>();
            for (DetailedKey ref : allReferences) {
                if (ref.position.equals(key.position)) {
                    toReturn.add(ref);
                }
            }
            return toReturn;
        }
    }

    public void insertPrimary(Table t, Attribute.Name column, String constraintName, String position) {
        primaryKeys.add(new DetailedKey(t, column, constraintName, "", position));
        column_to_P_Constraint.put(new Key(t, column), constraintName);
    }

    public void insertForeign(Table t, Attribute.Name column, String constraintName, String deleteRule, String position, String FKName) {
        foreignKeys.add(new DetailedKey(t, column, constraintName, deleteRule, position, FKName));

    }

    public void insertUnique(Table t, Attribute.Name column, String constraintName, String position) {
        uniqueKeys.add(new DetailedKey(t, column, constraintName, "", position));
        column_to_P_Constraint.put(new Key(t, column), constraintName);
    }

    public void initResolver() {
        if (initialized) {
            return;
        }
        for (DetailedKey key : primaryKeys) {
            referenced_to_referencers.put(key, new ArrayList<>());
        }

        for (DetailedKey key : uniqueKeys) {
            referenced_to_referencers.put(key, new ArrayList<>());
        }

        for (DetailedKey key : foreignKeys) {
            referenced_to_referencers.get(key).add(key);
        }
        initialized = true;

    }

    private class Key {

        protected Table t;
        protected Attribute.Name column;
        protected String position;

        public Key(Table t, Attribute.Name column) {
            this.t = t;
            this.column = column;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            Key key = (Key) o;
            return t == key.t && column == key.column;
        }

        @Override
        public int hashCode() {
            return Objects.hash(t, column);
        }
    }

    public class DetailedKey extends Key {

        private String constraintName;
        private String deleteRule;
        private String FKName;

        public DetailedKey(Table table, Attribute.Name column, String constraintName, String deleteRule, String position) {
            super(table, column);
            this.constraintName = constraintName;
            this.deleteRule = deleteRule;
            this.position = position;
            this.FKName = "";
        }

        public DetailedKey(Table table, Attribute.Name column, String constraintName, String deleteRule, String position, String FKName) {
            super(table, column);
            this.constraintName = constraintName;
            this.deleteRule = deleteRule;
            this.position = position;
            this.FKName = FKName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            DetailedKey key = (DetailedKey) o;
            return constraintName.equals(key.constraintName);
        }

        @Override
        public int hashCode() {
            return constraintName.hashCode();
        }

    }
}
