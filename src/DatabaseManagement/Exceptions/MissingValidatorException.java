package DatabaseManagement.Exceptions;

public class MissingValidatorException extends DBManagementException {

    @Override
    public String getMessage() {
        return "Missing Validator Function: You tried calling the validate function on a constraint without a validation function";
    }
}
