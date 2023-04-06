package DatabaseManagement.Exceptions;

public class ConstraintNotFoundException extends DBManagementException {

    private String constraint;

    public ConstraintNotFoundException(String constraint) {
        this.constraint = constraint;
    }

    @Override
    public String getMessage() {
        return "Could not map from the constraint " + constraint + " to a validator function";
    }

}
