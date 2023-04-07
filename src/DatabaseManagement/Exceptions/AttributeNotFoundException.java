package DatabaseManagement.Exceptions;

public class AttributeNotFoundException extends DBManagementException {

    private String attributeName;
    private String tableName;

    public AttributeNotFoundException(String tableName, String attributeName) {
        this.attributeName = attributeName;
        this.tableName = tableName;
    }

    @Override
    public String getMessage() {
        return "Could not find the attribute " + attributeName + " in table " + tableName;
    }

}
