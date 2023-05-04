package DatabaseManagement;

import DatabaseManagement.Attribute.Type;
import java.util.*;
import java.util.Map.Entry;

public class Filters {

    private HashMap<Attribute, ArrayList<Filter>> filters;
    private HashMap<Attribute, ArrayList<String>> filters_IN_Type;

    /**
     * Creates a new empty filter collection
     */
    public Filters() {
        filters = new HashMap<>();
        filters_IN_Type = new HashMap<>();
    }

    private Filters(HashMap<Attribute, ArrayList<Filter>> filters, HashMap<Attribute, ArrayList<String>> filters_IN_Type) {
        this.filters = filters;
        this.filters_IN_Type = filters_IN_Type;
    }

    /**
     * Creates a new filter collection initialized with the attributes in the
     * given collection
     *
     * @param collection collection whose attributes are to be added as filters.
     * All attribute filters as ' equal filters '
     */
    public Filters(AttributeCollection collection) {
        filters = new HashMap<>();
        filters_IN_Type = new HashMap<>();
        for (Attribute attribute : collection.attributes()) {
            addEqual(attribute);
        }
    }

    /**
     * Filters the filter collection where only the filters in the given table
     * are kept
     *
     * @param t Table which a filter must be in to remain in the collection
     * @return New filter collection containing only the selected filters. The
     * original Filters is unaffected
     */
    public Filters filter(Table t) {
        HashMap<Attribute, ArrayList<Filter>> filtersToKeep = new HashMap<>(filters);
        HashMap<Attribute, ArrayList<String>> inFiltersToKeep = new HashMap<>(filters_IN_Type);
        for (Attribute attribute : filters.keySet()) {
            if (attribute.getT() != t) {
                filtersToKeep.remove(attribute);
            }
        }
        for (Attribute attribute : filters_IN_Type.keySet()) {
            if (attribute.getT() != t) {
                inFiltersToKeep.remove(attribute);
            }
        }
        return new Filters(filtersToKeep, inFiltersToKeep);
    }

    public Filters append(Filters other) {
        for (Entry<Attribute, ArrayList<Filter>> entry : other.filters.entrySet()) {
            if (filters.containsKey(entry.getKey())) {
                filters.get(entry.getKey()).addAll(other.filters.get(entry.getKey()));
            } else {
                filters.put(entry.getKey(), entry.getValue());
            }
        }
        for (Entry<Attribute, ArrayList<String>> entry : other.filters_IN_Type.entrySet()) {
            if (filters_IN_Type.containsKey(entry.getKey())) {
                filters_IN_Type.get(entry.getKey()).addAll(other.filters_IN_Type.get(entry.getKey()));
            } else {
                filters_IN_Type.put(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    /**
     * Adds a condition where the value of the given attribute must be greater
     * than a specific value
     *
     * @param attribute Attribute where the attribute's name is the name of the
     * attribute to place the condition on and attribute's value is the value
     * that the attribute must be greater than
     */
    public void addGreater(Attribute attribute) {
        if (!filters.containsKey(attribute)) {
            filters.put(attribute, new ArrayList<Filter>());
        }
        filters.get(attribute).add(new Filter(attribute.getType(), attribute.getValue(), FilterType.GREATER));
    }

    /**
     * Adds a condition where the value of the given attribute must be greater
     * than or equal to a specific value
     *
     * @param attribute Attribute where the attribute's name is the name of the
     * attribute to place the condition on and attribute's value is the value
     * that the attribute must be greater than or equal
     */
    public void addGreaterEqual(Attribute attribute) {
        if (!filters.containsKey(attribute)) {
            filters.put(attribute, new ArrayList<Filter>());
        }
        filters.get(attribute).add(new Filter(attribute.getType(), attribute.getValue(), FilterType.GREATER_EQUAL));
    }

    /**
     * Adds a condition where the value of the given attribute must be equal to
     * a specific value
     *
     * @param attribute Attribute where the attribute's name is the name of the
     * attribute to place the condition on and attribute's value is the value
     * that the attribute must be equal to
     */
    public void addEqual(Attribute attribute) {
        if (!filters.containsKey(attribute)) {
            filters.put(attribute, new ArrayList<Filter>());
        }
        filters.get(attribute).add(new Filter(attribute.getType(), attribute.getValue(), FilterType.EQUAL));
    }

    /**
     * Adds a condition where the value of the given attribute must match a
     * given regular expression
     *
     * @param attribute Attribute where the attribute's name is the name of the
     * attribute to place the condition on and attribute's value is the regex
     * that the attribute must match
     */
    public void addLike(Attribute attribute) {
        if (!filters.containsKey(attribute)) {
            filters.put(attribute, new ArrayList<Filter>());
        }
        filters.get(attribute).add(new Filter(attribute.getType(), attribute.getValue(), FilterType.LIKE));
    }

    /**
     * Adds a condition where the value of the given attribute must be less than
     * a specific value
     *
     * @param attribute Attribute where the attribute's name is the name of the
     * attribute to place the condition on and attribute's value is the value
     * that the attribute must be less than
     */
    public void addLess(Attribute attribute) {
        if (!filters.containsKey(attribute)) {
            filters.put(attribute, new ArrayList<Filter>());
        }
        filters.get(attribute).add(new Filter(attribute.getType(), attribute.getValue(), FilterType.LESS));
    }

    /**
     * Adds a condition where the value of the given attribute must be less than
     * or equal to a specific value
     *
     * @param attribute Attribute where the attribute's name is the name of the
     * attribute to place the condition on and attribute's value is the value
     * that the attribute must be less than or equal
     */
    public void addLessEqual(Attribute attribute) {
        if (!filters.containsKey(attribute)) {
            filters.put(attribute, new ArrayList<Filter>());
        }
        filters.get(attribute).add(new Filter(attribute.getType(), attribute.getValue(), FilterType.LESS_EQUAL));
    }

    /**
     * Adds a condition where the value of the given attribute must be unequal
     * to a specific value
     *
     * @param attribute Attribute where the attribute's name is the name of the
     * attribute to place the condition on and attribute's value is the value
     * that the attribute must be unequal to
     */
    public void addNotEqual(Attribute attribute) {
        if (!filters.containsKey(attribute)) {
            filters.put(attribute, new ArrayList<Filter>());
        }
        filters.get(attribute).add(new Filter(attribute.getType(), attribute.getValue(), FilterType.NOT_EQUAL));
    }

    /**
     * Adds a condition where the value of a given attribute must be in between
     * two given values (inclusive)
     *
     * @param min Lower bound on the attribute value
     * @param max Upper bound on the attribute value
     */
    public void addBetween(Attribute attribute, String min, String max) {
        Attribute minAtt = new Attribute(attribute.getAttributeName(), min, attribute.getT());
        Attribute maxAtt = new Attribute(attribute.getAttributeName(), max, attribute.getT());

        if (!filters.containsKey(attribute)) {
            filters.put(attribute, new ArrayList<Filter>());
        }
        filters.get(attribute).add(new Filter(attribute.getType(), minAtt.getValue(), FilterType.GREATER_EQUAL));
        filters.get(attribute).add(new Filter(attribute.getType(), maxAtt.getValue(), FilterType.LESS_EQUAL));
    }

    /**
     * Adds a condition where an attribute's value must be in the given list of
     * values
     *
     * @param attribute Attribute to place the condition on. Only the attribute
     * name and table are needed
     * @param acceptedValues List of values that are acceptable for this
     * attribute
     */
    public void addIn(Attribute attribute, String[] acceptedValues) {
        ArrayList<String> toAdd = new ArrayList<>();
        for (String value : acceptedValues) {
            toAdd.add(value);
        }
        filters_IN_Type.put(attribute, toAdd);
    }

    /**
     * Removes all filters from this object
     */
    public void clear() {
        filters.clear();
    }

    public ArrayList<String> getFilterValues(Attribute attribute) {
        ArrayList<String> values = new ArrayList<>();

        ArrayList<Filter> filtsArray = filters.get(attribute);
        if (filtsArray != null) {
            for (Filter filt : filtsArray) {
                values.add(filt.value);
            }
        }
        ArrayList<String> filtsINArray = filters_IN_Type.get(attribute);
        if (filtsINArray != null) {
            values.addAll(filtsINArray);
        }
        return values;
    }

    public String getFilterClause() {
        if (filters.isEmpty() && filters_IN_Type.isEmpty()) {
            return "";
        }

        String clause = "";
        ArrayList<String> conditions = new ArrayList<>();

        for (Map.Entry<Attribute, ArrayList<Filter>> entry : filters.entrySet()) {
            for (Filter filter : entry.getValue()) {
                String attName = entry.getKey().getAliasedStringName();
                String value = filter.getStringValue();
                String operator = filter.getOperator();

                if (filter.filterType == FilterType.EQUAL && value.equalsIgnoreCase("null")) {
                    operator = "IS";
                }
                String condition = attName + " " + operator + " " + value;
                conditions.add(condition);
            }
        }

        for (Map.Entry<Attribute, ArrayList<String>> entry : filters_IN_Type.entrySet()) {
            String condition = entry.getKey().getAliasedStringName() + " IN ";
            ArrayList<String> acceptedInValues = new ArrayList<>();

            if (entry.getKey().getType() == Attribute.Type.STRING) {
                for (String value : entry.getValue()) {
                    acceptedInValues.add("'" + value + "'");
                }
            } else {
                acceptedInValues.addAll(entry.getValue());
            }

            condition += "(" + String.join(" , ", acceptedInValues) + ")";
            conditions.add(condition);
        }

        return clause + String.join(" AND ", conditions);
    }

    public Set<Attribute> getAttributes() {
        return filters.keySet();
    }

    private class Filter {

        private Type type;
        private String value;
        private FilterType filterType;

        private Filter(Type type, String value, FilterType filterType) {
            this.type = type;
            this.value = value;
            this.filterType = filterType;
        }

        private String getOperator() {
            return filterType.getOperator();
        }

        private String getStringValue() {
            if (!type.equals(Type.NUMBER)) {
                return "'" + value + "'";
            } else {
                return value;
            }
        }

    }

    public enum FilterType {
        EQUAL("="),
        NOT_EQUAL("!="),
        LESS("<"),
        LESS_EQUAL("<="),
        GREATER(">"),
        GREATER_EQUAL(">="),
        BETWEEN("BETWEEN"),
        LIKE("LIKE"),
        IN("IN");

        private String operator;

        private FilterType(String operator) {
            this.operator = operator;
        }

        public String getOperator() {
            return operator;
        }
    }
}
