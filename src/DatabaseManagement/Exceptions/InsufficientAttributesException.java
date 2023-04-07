package DatabaseManagement.Exceptions;

import DatabaseManagement.Table;

public class InsufficientAttributesException extends DBManagementException {
    private Table t;
    private int numTableAttributes;
    private int sizeInserted;

    public InsufficientAttributesException(Table t, int numTableAttributes, int sizeInserted) {
        this.t = t;
        this.numTableAttributes = numTableAttributes;
        this.sizeInserted = sizeInserted;
    }

    @Override
    public String getMessage() {
        return "Insufficient Attributes Exception: Table " + t.getTableName() + " has " + numTableAttributes + " but you only provided " + sizeInserted + " attributes";
    }
}
