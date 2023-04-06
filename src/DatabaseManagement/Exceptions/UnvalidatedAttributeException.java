package DatabaseManagement.Exceptions;

import DatabaseManagement.Attribute;

public class UnvalidatedAttributeException extends DBManagementException {
    private Attribute attribute;

    public UnvalidatedAttributeException(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getMessage() {
        return "Cannot get error for attribute " + attribute.getStringName()
                + ": attribute was not validated in the first place";
    }
}
