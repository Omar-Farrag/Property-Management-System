package DatabaseManagement.Exceptions;

public class TableNotFoundException extends DBManagementException {

    @Override
    public String getMessage() {
        return "Could not find the table in the meta data";
    }

}
