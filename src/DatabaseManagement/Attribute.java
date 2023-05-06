package DatabaseManagement;

public class Attribute {

    private final Name attributeName;
    private final String attributeValue;
    private final Type type;
    private final Table t;

    /**
     * Creates an instance of the attribute object. Use this constructor for
     * operations where the value of the attribute is needed, such as performing
     * Update, Insert, filtering, etc.
     *
     * @param attributeName Name of the Attribute as written in the database
     * @param value Value of the attribute as a string
     * @param t The table that the attribute belongs to
     */
    public Attribute(Name attributeName, String value, Table t) throws IllegalArgumentException {
        if (attributeName == null || t == null) {
            throw new IllegalArgumentException("Neither attribute name nor table can be null");
        }
        this.attributeName = attributeName;
        this.attributeValue = value.replace("'", "''");
        this.type = attributeName.type;
        this.t = t;

    }

    /**
     * Creates an instance of the attribute object. Use this constructor for
     * operations where the value of the attribute is not needed such as when
     * retrieving specific attributes from a table.
     *
     * @param attribute Name of the attribute as written in the database
     * @param t The table that the attribute belongs to
     */
    public Attribute(Name attribute, Table t) {
        if (attribute == null || t == null) {
            throw new IllegalArgumentException("Neither attribute name nor table can be null");
        }
        this.attributeName = attribute;
        this.attributeValue = "";
        this.type = attribute.type;
        this.t = t;

    }

    public String getStringName() {
        return attributeName.getName();
    }

    public Name getAttributeName() {
        return attributeName;
    }

    public String getAliasedStringName() {
        return t.getAlias() + "." + getStringName();
    }

    public String getValue() {
        return attributeValue;
    }

    public String getStringValue() {
        if (!type.equals(Type.NUMBER)) {
            return "'" + attributeValue + "'";
        } else if (attributeValue == null) {
            return "NULL";
        } else {
            return attributeValue;
        }

    }

    public Type getType() {
        return type;
    }

    public Table getT() {
        return t;
    }

    @Override
    public boolean equals(Object obj) {
        Attribute other = (Attribute) obj;
        return attributeName.equals(other.attributeName) && t.equals(other.t);
    }

    @Override
    public int hashCode() {
        return (attributeName.getName()).hashCode();
    }

    /**
     * Known problem: if two attributes have the same name but different types,
     * bad things will happen Solution: make the user specify the type in the
     * Attribute Constructor. Moreover, in the validator class, in the
     * validation functions for the data types, add an extra step where the type
     * is checked to make sure that it is correct. For example in the validation
     * function, for VARCHAR2, check that the type of attribute is STRING. If it
     * is not, throw an exception
     * <p>
     * <p>
     * In our case, no two attributes have the same name and different types so
     * we're good
     */
    public enum Name {
        ELECHARGE("ELECHARGE", Type.NUMBER),
        WATCONS("WATCONS", Type.NUMBER),
        ELECONS("ELECONS", Type.NUMBER),
        WASTEDISPOSED("WASTEDISPOSED", Type.NUMBER),
        WASTECHARGE("WASTECHARGE", Type.NUMBER),
        WATCHARGE("WATCHARGE", Type.NUMBER),
        UTILITY_ID("UTILITY_ID", Type.STRING),
        REQUEST_NUM("REQUEST_NUM", Type.STRING),
        STATUS("STATUS", Type.STRING),
        LEASE_NUM("LEASE_NUM", Type.STRING),
        ASSIGNED_TECH("ASSIGNED_TECH", Type.STRING),
        DESCRIPTION("DESCRIPTION", Type.STRING),
        BILL_NUM("BILL_NUM", Type.STRING),
        CHARGE("CHARGE", Type.NUMBER),
        DUE_DATE("DUE_DATE", Type.DATE),
        PAID("PAID", Type.NUMBER),
        TOTAL_AMOUNT("TOTAL_AMOUNT", Type.NUMBER),
        PAYER_ID("PAYER_ID", Type.STRING),
        RECEIPT_NUM("RECEIPT_NUM", Type.STRING),
        DATE_PAID("DATE_PAID", Type.DATE),
        UTILITY_PERCENTAGE("UTILITY_PERCENTAGE", Type.NUMBER),
        MAINTENANCE_PERCENTAGE("MAINTENANCE_PERCENTAGE", Type.NUMBER),
        LEASE_PERCENTAGE("LEASE_PERCENTAGE", Type.NUMBER),
        DISCOUNT_NUM("DISCOUNT_NUM", Type.STRING),
        LOCATION_NUM("LOCATION_NUM", Type.NUMBER),
        END_DATE("END_DATE", Type.TIMESTAMP),
        START_DATE("START_DATE", Type.TIMESTAMP),
        PAYMENT_OPTION("PAYMENT_OPTION", Type.STRING),
        LEASER_ID("LEASER_ID", Type.STRING),
        APPLICANT_ID("APPLICANT_ID", Type.STRING),
        LEASE_END("LEASE_END", Type.DATE),
        LEASE_START("LEASE_START", Type.DATE),
        LEASE_REQUEST_NUM("LEASE_REQUEST_NUM", Type.STRING),
        AGENT_ID("AGENT_ID", Type.STRING),
        POTENTIAL_TENANT_ID("POTENTIAL_TENANT_ID", Type.STRING),
        SPACE("SPACE", Type.NUMBER),
        BI_ANNUAL_RATE("BI_ANNUAL_RATE", Type.NUMBER),
        ANNUAL_RATE("ANNUAL_RATE", Type.NUMBER),
        MONTHLY_RATE("MONTHLY_RATE", Type.NUMBER),
        PURPOSE("PURPOSE", Type.STRING),
        QUARTERLY_RATE("QUARTERLY_RATE", Type.NUMBER),
        CLASS("CLASS", Type.STRING),
        NAME("NAME", Type.STRING),
        MALL_NUM("MALL_NUM", Type.STRING),
        STORE_NUM("STORE_NUM", Type.STRING),
        ADDRESS("ADDRESS", Type.STRING),
        LNAME("LNAME", Type.STRING),
        EMAIL_ADDRESS("EMAIL_ADDRESS", Type.STRING),
        USER_ID("USER_ID", Type.STRING),
        ROLE_ID("ROLE_ID", Type.STRING),
        PHONE_NUMBER("PHONE_NUMBER", Type.STRING),
        FNAME("FNAME", Type.STRING),
        PASSWORD("PASSWORD", Type.STRING),
        MESSAGE("MESSAGE", Type.STRING),
        SENDER_ID("SENDER_ID", Type.STRING),
        RECEIVER_ID("RECEIVER_ID", Type.STRING),
        DATE_SENT("DATE_SENT", Type.TIMESTAMP),
        NUM_FLOORS("NUM_FLOORS", Type.NUMBER),
        RATE("RATE", Type.NUMBER),
        DAY("DAY", Type.STRING),
        SLOT_NUM("SLOT_NUM", Type.NUMBER),
        APPOINTMENT_SLOT("APPOINTMENT_SLOT", Type.NUMBER),
        BOOKED("BOOKED", Type.STRING);

        private final String name;
        private final Type type;

        Name(String name, Type type
        ) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }
    }

    public enum Type {
        NUMBER, STRING, DATE, TIMESTAMP;
    }

    public static void main(String[] args) {
        String x = "3.23";
        System.out.println(Integer.parseInt(x));
    }
}
