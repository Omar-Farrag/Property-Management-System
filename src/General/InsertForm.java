package General;

import DatabaseManagement.AttributeCollection;
import DatabaseManagement.ConstraintsHandling.ConstraintChecker.Errors;

public interface InsertForm {

    public AttributeCollection getAttributes();

    public void clearErrors();

    public void displayErrors(Errors errors);

    public void displayActionStatus(boolean successful);
}
