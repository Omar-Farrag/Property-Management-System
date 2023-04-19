package DatabaseManagement;

import java.util.*;

public class Filters {

    private HashMap<Attribute, FilterType> filters;
    private HashMap<Attribute, String[]> filters_IN_Type;

    /**
     * Creates a new empty filter collection
     */
    public Filters() {
        filters = new HashMap<>();
        filters_IN_Type = new HashMap<>();
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
        filters.put(attribute, FilterType.GREATER);
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
        filters.put(attribute, FilterType.GREATER_EQUAL);
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
        filters.put(attribute, FilterType.EQUAL);
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
        filters.put(attribute, FilterType.LIKE);
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
        filters.put(attribute, FilterType.LESS);
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
        filters.put(attribute, FilterType.LESS_EQUAL);
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
        filters.put(attribute, FilterType.NOT_EQUAL);
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
        filters.put(minAtt, FilterType.GREATER_EQUAL);
        filters.put(maxAtt, FilterType.LESS_EQUAL);
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
        filters_IN_Type.put(attribute, acceptedValues);
    }

    /**
     * Removes all filters from this object
     */
    public void clear() {
        filters.clear();
    }

    public String getFilterClause() {
        if (filters.isEmpty() && filters_IN_Type.isEmpty()) {
            return "";
        }

        String clause = "where ";
        ArrayList<String> conditions = new ArrayList<>();

        for (Map.Entry<Attribute, FilterType> entry : filters.entrySet()) {
            String condition
                    = entry.getKey().getAliasedStringName() + " " + entry.getValue().getOperator() + " " + entry.getKey().getStringValue();
            conditions.add(condition);
        }

        for (Map.Entry<Attribute, String[]> entry : filters_IN_Type.entrySet()) {
            String condition = entry.getKey().getAliasedStringName() + " IN ";
            ArrayList<String> acceptedInValues = new ArrayList<>();

            if (entry.getKey().getType() == Attribute.Type.STRING) {
                for (String value : entry.getValue()) {
                    acceptedInValues.add("'" + value + "'");
                }
            } else {
                Collections.addAll(acceptedInValues, entry.getValue());
            }

            condition += "(" + String.join(" , ", acceptedInValues) + ")";
            conditions.add(condition);
        }

        return clause + String.join(" AND ", conditions);
    }

    public Set<Attribute> getAttributes() {
        return filters.keySet();
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
