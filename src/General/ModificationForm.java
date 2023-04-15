package General;

import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Filters;

public interface ModificationForm {

    public AttributeCollection getAttributes();

    public Filters getFilters();

    public void populateFields();

    public void clearFields();

}
