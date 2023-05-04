package DatabaseManagement.ConstraintsHandling;

import DatabaseManagement.*;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.Exceptions.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MetaDataExtractor {

    private final String metaDataFile = "Metadata.json";
    private final ArrayList<String> currentApplicationTables;
    private ResultSet constraintsTable;
    private ResultSet tableDataTypes;
    private final HashMap<Table, JSONObject> table_to_object;
    private final ReferentialResolver resolver;
    private static MetaDataExtractor instance;

    public static MetaDataExtractor getInstance() {
        return instance == null ? instance = new MetaDataExtractor() : instance;
    }

    private MetaDataExtractor() {
        table_to_object = new HashMap<>();
        resolver = ReferentialResolver.getInstance();

        currentApplicationTables = Table.getApplicationTables();
        if (!new File(metaDataFile).exists()) {
            initMetaDataFile();
        }

        readMetaDataFromFile();

        resolver.initResolver();
    }

    public JSONObject getTableAttributes(Table t) {
        return (JSONObject) table_to_object.get(t).get("Attributes");
    }

    private void readMetaDataFromFile() {
        try {
            FileInputStream fin = new FileInputStream(metaDataFile);
            InputStreamReader in = new InputStreamReader(fin);
            BufferedReader bin = new BufferedReader(in);

            String fileContent = "";
            String line;
            while ((line = bin.readLine()) != null) {
                fileContent += line;
            }

            JSONArray metaData = (JSONArray) new JSONParser().parse(fileContent);
            if (metaData.isEmpty()) {
                throw new EmptyFileException(metaDataFile);
            }

            for (Object table : metaData) {
                JSONObject tableJSONObject = (JSONObject) table;
                String tableName = tableJSONObject.get("TableName").toString();

                table_to_object.put(Table.valueOf(tableName), tableJSONObject);
                JSONObject attributes = (JSONObject) tableJSONObject.get("Attributes");
                for (Object attribute : attributes.keySet()) {
                    JSONArray constraints = (JSONArray) attributes.get(attribute);
                    for (int i = 0; i < constraints.size(); i++) {

                        Table t = Table.valueOf(tableName);
                        Name attName = Name.valueOf(attribute.toString());
                        String constraintName = constraints.get(i).toString();
                        String position = constraintName.substring(2, 3);

                        if (constraintName.startsWith("P")) {
                            resolver.insertPrimary(t, attName, constraintName.substring(4), position);
                        } else if (constraintName.startsWith("U")) {
                            resolver.insertUnique(t, attName, constraintName.substring(4), position);
                        } else if (constraintName.startsWith("R")) {
                            int leftParenIndex = constraintName.indexOf('(');
                            int rightParenIndex = constraintName.indexOf(')');
                            String deleteRule = constraintName.substring(leftParenIndex + 1, rightParenIndex).trim();

                            int leftBracIndex = constraintName.indexOf("[");
                            int rightBracIndex = constraintName.indexOf("]");
                            String FKName = constraintName.substring(leftBracIndex, rightBracIndex);

                            constraintName = constraintName.substring(4, leftParenIndex);
                            resolver.insertForeign(t, attName, constraintName, deleteRule, position, FKName);
                        }
                    }
                }
            }
            bin.close();
            System.out.println("Done writing the metadata file...You're good to go now");
        } catch (IOException
                | ParseException
                | EmptyFileException e) {
            System.out.println("Hello There...It seems like the metadata file is empty...Just give me a sec and I'll rewrite it for you.");
            initMetaDataFile();
            readMetaDataFromFile();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    public JSONObject getTableInfoFromMetaData(Table t) throws TableNotFoundException {
        if (!table_to_object.containsKey(t)) {
            throw new TableNotFoundException();
        } else {
            return table_to_object.get(t);
        }
    }

    public AttributeCollection getAttributeCollection(Table t) {
        JSONObject attributes = getTableAttributes(t);
        AttributeCollection collection = new AttributeCollection();

        for (Object jsonAtt : attributes.keySet()) {
            collection.add(new Attribute(Name.valueOf(jsonAtt.toString()), t));
        }
        return collection;
    }

    private Boolean initMetaDataFile() {
        try {
            FileWriter fout = new FileWriter(metaDataFile);
            JSONArray metaData = new JSONArray();

            String formattedTableNames = "'" + String.join(",", currentApplicationTables).replace(",", "','") + "'";
            constraintsTable = DatabaseManager.getInstance().executeStatement(
                    " SELECT U.TABLE_NAME,"
                    + "U.COLUMN_NAME,"
                    + "CONSTRAINT_TYPE,"
                    + " SEARCH_CONDITION,"
                    + " U.CONSTRAINT_NAME,"
                    + " R_CONSTRAINT_NAME,"
                    + " U.POSITION,"
                    + " DELETE_RULE"
                    + " FROM USER_CONS_COLUMNS U"
                    + " JOIN ALL_CONSTRAINTS A"
                    + " ON ( U.TABLE_NAME = A.TABLE_NAME"
                    + " AND U.CONSTRAINT_NAME = A.CONSTRAINT_NAME )"
                    + " WHERE U.OWNER = '" + DatabaseManager.getInstance().getUsername() + "'"
                    + " AND A.OWNER = '" + DatabaseManager.getInstance().getUsername() + "'"
                    + " AND U.TABLE_NAME in (" + formattedTableNames + ") ORDER BY CONSTRAINT_TYPE");

            tableDataTypes = DatabaseManager.getInstance().executeStatement(
                    " select *"
                    + " from user_tab_columns"
                    + " order by TABLE_NAME,column_id"
            );

            extractTables(metaData);

            fout.write(metaData.toJSONString());
            fout.flush();

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private void extractTables(JSONArray metaData) {

        try {
            DatabaseMetaData meta = DatabaseManager.getInstance().getMetaData();

            for (String tableName : currentApplicationTables) {
                JSONObject table = new JSONObject();
                table.put("TableName", tableName);

                JSONObject attributes = extractAttributesForTable(tableName, meta);

                table.put("Attributes", attributes);
                metaData.add(table);
//                table_to_object.put(tableName, table);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    private JSONObject extractAttributesForTable(String tableName, DatabaseMetaData meta) throws SQLException {
        ResultSet columns = meta.getColumns(null, DatabaseManager.getInstance().getUsername(), tableName, null);
        JSONObject attributes = new JSONObject();

        while (columns.next()) {

            String attributeName = columns.getString("COLUMN_NAME");
            JSONArray constraints = new JSONArray();

            ArrayList<String> constraintStrings = extractConstraints(tableName, attributeName);
            constraints.add(extractDataType(columns.getRow(), tableName));

            for (String constraint : constraintStrings) {
                constraints.add(constraint);
            }

            attributes.put(attributeName, constraints);
        }

        return attributes;
    }

    private String extractDataType(int rowNumber, String tableName) {
        try {

            tableDataTypes.beforeFirst();
            while (tableDataTypes.next()) {
                if (tableDataTypes.getString("TABLE_NAME").equals(tableName)) {

                    tableDataTypes.absolute(tableDataTypes.getRow() + rowNumber - 1);
                    String dataType = tableDataTypes.getString("DATA_TYPE");
                    if (dataType.toLowerCase().equals("number")) {
                        dataType += extractPrecisionAndScale(tableDataTypes);
                    } else if (dataType.toLowerCase().equals("varchar2") || dataType.toLowerCase().equals("char")) {
                        dataType += extractMaxLength(tableDataTypes);
                    }
                    return dataType.toUpperCase();
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";

    }

    private String extractMaxLength(ResultSet tableDescription) throws SQLException {
        return "_" + tableDescription.getString("CHAR_COL_DECL_LENGTH");
    }

    private String extractPrecisionAndScale(ResultSet tableDescription) throws SQLException {
        String precision = tableDescription.getString("DATA_PRECISION");
        return "_" + (precision == null ? "38" : precision) + "_" + tableDescription.getString("DATA_SCALE");
    }

    private ArrayList<String> extractConstraints(String tableName, String columnName) throws SQLException {

        ArrayList<String> constraints = new ArrayList<>();
        constraintsTable.first();
        while (constraintsTable.next()) {
            if (constraintsTable.getString("TABLE_NAME").equalsIgnoreCase(tableName)
                    && constraintsTable.getString("COLUMN_NAME").equalsIgnoreCase(columnName)) {

                String constraint = constraintsTable.getString("CONSTRAINT_TYPE").toUpperCase();

                switch (constraint) {
                    case "C" ->
                        constraint += "_"
                                + constraintsTable.getString("SEARCH_CONDITION").replace("\"", "").replace("\n", "")
                                        .replace("   ", "");
                    case "R" -> {
                        String referencedKey = constraintsTable.getString("R_CONSTRAINT_NAME");
                        String deleteRule = "(" + constraintsTable.getString("DELETE_RULE") + ")";
                        String position = constraintsTable.getString("POSITION");
                        String referencingKey = "[" + constraintsTable.getString("CONSTRAINT_NAME") + "]";
                        constraint += "_" + position + "_" + referencedKey + deleteRule + referencingKey;
                    }
                    case "P" -> {
                        String position = constraintsTable.getString("POSITION");
                        constraint += "_" + position + "_" + constraintsTable.getString("CONSTRAINT_NAME");
                    }
                    case "U" -> {
                        String position = constraintsTable.getString("POSITION");
                        constraint += "_" + position + "_" + constraintsTable.getString("CONSTRAINT_NAME");
                    }
                    default -> {
                    }
                }

                constraints.add(constraint.toUpperCase());
            }
        }
        return constraints;
    }

    public Key getPrimaryKeys(Table t) {
        Key key = new Key();
        JSONObject attributes = getTableAttributes(t);

        for (Object att : attributes.keySet()) {
            String attributeName = att.toString();
            if (isKey((JSONArray) attributes.get(attributeName), "P_")) {
                key.add(new Attribute(Name.valueOf(attributeName), t));
            }
        }
        return key;
    }

    public ArrayList<Key> getUniqueKeys(Table t) {
        HashMap<String, Key> keys = new HashMap<>();

        JSONObject attributes = getTableAttributes(t);

        for (Object att : attributes.keySet()) {
            String attributeName = att.toString();
            String keyConstraintName = getKeyConstraintName((JSONArray) attributes.get(attributeName), "U_");

            if (!keyConstraintName.isEmpty()) {
                Key existingKey = keys.get(keyConstraintName);
                if (existingKey != null) {
                    existingKey.add(new Attribute(Name.valueOf(attributeName), t));
                } else {
                    Key newKey = new Key();
                    newKey.add(new Attribute(Name.valueOf(attributeName), t));
                    keys.put(keyConstraintName, newKey);
                }
            }
        }
        return new ArrayList<>(keys.values());
    }

    private boolean isKey(JSONArray constraints, String toStartWith) {
        for (Object constraint : constraints) {
            if (constraint.toString().startsWith(toStartWith)) {
                return true;
            }
        }
        return false;
    }

    private String getKeyConstraintName(JSONArray constraints, String toStartWith) {
        for (Object constraint : constraints) {
            if (constraint.toString().startsWith(toStartWith)) {
                return constraint.toString();
            }
        }
        return "";
    }

    public static class Key {

        private final ArrayList<Attribute> keyAttributes;

        public Key() {
            keyAttributes = new ArrayList<>();
        }

        public void add(Attribute attribute) {
            keyAttributes.add(attribute);
        }

        public ArrayList<Attribute> getKeyAttributes() {
            return keyAttributes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Key that = (Key) o;

            for (Attribute thisAttribute : keyAttributes) {
                boolean found = false;
                for (Attribute thatAttribute : that.keyAttributes) {
                    if (thisAttribute.equals(thatAttribute) && thisAttribute.getValue().equals(thatAttribute.getValue())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            return true;

        }

        @Override
        public int hashCode() {
            ArrayList<String> values = new ArrayList<>();
            for (Attribute attribute
                    : keyAttributes) {
                values.add(attribute.getStringName() + attribute.getValue() + attribute.getT().getTableName());
            }
            Collections.sort(values);
            return Objects.hash(values);
        }
    }
}
