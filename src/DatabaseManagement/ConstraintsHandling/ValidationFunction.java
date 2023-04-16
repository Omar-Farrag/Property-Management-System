package DatabaseManagement.ConstraintsHandling;

import DatabaseManagement.Attribute;
import DatabaseManagement.AttributeCollection;

@FunctionalInterface
public interface ValidationFunction {
    public String validate(ValidationParameters parameters);
}
