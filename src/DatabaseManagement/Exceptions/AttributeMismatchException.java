package DatabaseManagement.Exceptions;

import DatabaseManagement.Attribute;

public class AttributeMismatchException extends DBManagementException {
    Attribute attr1;
    Attribute attr2;

    public AttributeMismatchException(Attribute attr1, Attribute attr2) {
        this.attr1 = attr1;
        this.attr2 = attr2;
    }

    @Override
    public String getMessage() {
        return "The attributes " + attr1.getStringName() + " and " + attr2.getStringName() + " do not match for a 'Between' filtration";
    }
}
