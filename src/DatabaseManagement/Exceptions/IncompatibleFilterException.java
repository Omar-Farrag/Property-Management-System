package DatabaseManagement.Exceptions;

import java.util.Map;

import DatabaseManagement.Attribute;
import DatabaseManagement.Filters.FilterType;

public class IncompatibleFilterException extends DBManagementException {

    private Map.Entry<Attribute, FilterType> filter;

    public IncompatibleFilterException(Map.Entry<Attribute, FilterType> filter) {
        this.filter = filter;
    }

    @Override
    public String getMessage() {
        return "The operator " + filter.getValue() + " cannot be used to filter the attribute "
                + filter.getKey().getStringName();
    }

}
