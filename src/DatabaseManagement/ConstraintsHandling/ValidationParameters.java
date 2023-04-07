package DatabaseManagement.ConstraintsHandling;

import DatabaseManagement.Attribute;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Filters;

public class ValidationParameters {
    private final String constraint;
    private final Attribute toValidate;
    private final AttributeCollection allAttributes;
    private final OperationType type;
    private final Filters filters;

    public ValidationParameters(String constraint, Attribute toValidate, AttributeCollection allAttributes,
                                OperationType type, Filters filters) {
        this.constraint = constraint;
        this.toValidate = toValidate;
        this.allAttributes = allAttributes;
        this.type = type;
        this.filters = filters;
    }

    public String getConstraint() {
        return constraint;
    }

    public Attribute getToValidate() {
        return toValidate;
    }

    public AttributeCollection getAllAttributes() {
        return allAttributes;
    }

    public OperationType getOperationType() {
        return type;
    }

    public Filters getFilters() {
        return filters;
    }

    public enum OperationType{
        INSERT, UPDATE;
    }
}
