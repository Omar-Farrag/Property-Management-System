package DatabaseManagement;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class AttributeCollection {
    private final Set<Attribute> attributes;

    /**
     * Creates an empty AttributeCollection
     */
    public AttributeCollection() {
        attributes = new LinkedHashSet<>();
    }


    /**
     * Creates an attribute collection containing all attributes that were added to the given filters object
     *
     * @param filters
     */
    public AttributeCollection(Filters filters) {
        attributes = new LinkedHashSet<>();
        for (Attribute att : filters.getAttributes())
            add(att);
    }

    /**
     * Adds all attributes in the given attribute collection to this attribute collection
     *
     * @param ac Attribute collection to be added
     * @return This attribute collection after adding all attributes in given collection
     */
    public AttributeCollection append(AttributeCollection ac) {
        if (ac != null)
            attributes.addAll(ac.attributes);

        return this;
    }

    /**
     * Adds an attribute to the attribute collection
     *
     * @param attribute Attribute to be added to the collection
     */
    public void add(Attribute attribute) {
        attributes.add(attribute);
    }

    /**
     * @return Set of attributes added to the collection so far
     */
    public Set<Attribute> attributes() {
        return attributes;
    }

    /**
     * Checks if the attribute collection is empty or not
     *
     * @return True if the collection contains no attributes, false otherwise
     */
    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    /**
     * @return The current number of attributes in the attribute collection
     */
    public int size() {
        return attributes.size();
    }

    /**
     * Clears all attributes in the attribute collection
     */
    public void clear() {
        attributes.clear();
    }

    public String getAliasedFormattedAtt() {
        ArrayList<String> attributes_as_string = new ArrayList<>();

        for (Attribute att : attributes)
            attributes_as_string.add(att.getAliasedStringName());

        return String.join(" , ", attributes_as_string);
    }

    public String getFormattedAtt() {
        ArrayList<String> attributes_as_string = new ArrayList<>();

        for (Attribute att : attributes)
            attributes_as_string.add(att.getStringName());

        return String.join(" , ", attributes_as_string);
    }

    public String getFormattedValues() {
        ArrayList<String> values_as_string = new ArrayList<>();

        for (Attribute att : attributes) {
            values_as_string.add(att.getStringValue());
        }

        return String.join(" , ", values_as_string);
    }

}
