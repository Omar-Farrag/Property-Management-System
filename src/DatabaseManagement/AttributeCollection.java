package DatabaseManagement;

import DatabaseManagement.Attribute.Name;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
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
     * Creates a new attribute collection with the given attributes already
     * added
     *
     * @param attributes attributes to add to the collection
     */
    public AttributeCollection(Set<Attribute> attributes) {
        this.attributes = new LinkedHashSet<>(attributes);
    }

    /**
     * Creates an attribute collection containing all attributes that were added
     * to the given filters object
     *
     * @param filters
     */
    public AttributeCollection(Filters filters) {
        attributes = new LinkedHashSet<>();
        for (Attribute att : filters.getAttributes()) {
            add(att);
        }
    }

    /**
     * Adds all attributes in the given attribute collection to this attribute
     * collection
     *
     * @param ac Attribute collection to be added
     * @return This attribute collection after adding all attributes in given
     * collection
     */
    public AttributeCollection append(AttributeCollection ac) {
        if (ac != null) {
            attributes.addAll(ac.attributes);
        }

        return this;
    }

    /**
     * Adds an attribute to the attribute collection
     *
     * @param attribute Attribute to be added to the collection
     * @return The current attribute collection with the new attribute added.
     * Useful for cascading the adds;
     */
    public AttributeCollection add(Attribute attribute) throws IllegalArgumentException {
        if (attribute == null) {
            throw new IllegalArgumentException("Cannot add null to this collection");
        }
        attributes.add(attribute);
        return this;
    }

    /**
     * Removes the given attribute from the attribute collection if present
     *
     * @param attribute Attribute to be removed
     */
    public void remove(Attribute attribute) {
        attributes.remove(attribute);
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

    /**
     * Finds and gets the attribute called [name] in the collection that is in
     * table [t]
     *
     * @param t
     * @param name
     * @return Attribute that matches the table and name. Returns null if no
     * such attribute is found
     */
    public Attribute getAttribute(Table t, Name name) {
        Attribute toMatch = new Attribute(name, t);
        for (Attribute attribute : attributes) {
            if (attribute.equals(toMatch)) {
                return attribute;
            }
        }
        return null;
    }

    /**
     * Filters the attribute collection where only the attributes in the given
     * table are kept
     *
     * @param t Table which an attribute must be in to remain in the collection
     * @return New attribute collection containing only the filtered attributes.
     * The original attributeCollection is unaffected
     */
    public AttributeCollection filter(Table t) {
        Set<Attribute> toKeep = new LinkedHashSet<>(attributes);
        for (Attribute attribute : attributes) {
            if (attribute.getT() != t) {
                toKeep.remove(attribute);
            }
        }
        return new AttributeCollection(toKeep);
    }

    public String getAliasedFormattedAtt() {
        ArrayList<String> attributes_as_string = new ArrayList<>();

        for (Attribute att : attributes) {
            attributes_as_string.add(att.getAliasedStringName());
        }

        return String.join(" , ", attributes_as_string);
    }

    public String getFormattedAtt() {
        ArrayList<String> attributes_as_string = new ArrayList<>();

        for (Attribute att : attributes) {
            attributes_as_string.add(att.getStringName());
        }

        return String.join(" , ", attributes_as_string);
    }

    public String getFormattedValues() {
        ArrayList<String> values_as_string = new ArrayList<>();

        for (Attribute att : attributes) {
            values_as_string.add(att.getStringValue());
        }

        return String.join(" , ", values_as_string);
    }

    public ArrayList<String> getValues() {
        ArrayList<String> values = new ArrayList<>();
        for (Attribute att : attributes) {
            values.add(att.getValue());
        }
        return values;
    }

    public String getValue(Attribute attribute) {
        for (Attribute att : attributes) {
            if (att.equals(attribute)) {
                return att.getValue();
            }
        }
        return "";
    }

    public String getStringValue(Attribute attribute) {
        for (Attribute att : attributes) {
            if (att.equals(attribute)) {
                return att.getStringValue();
            }
        }
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AttributeCollection)) {
            return false;
        }
        AttributeCollection other = (AttributeCollection) obj;

        return attributes.equals(other.attributes);
    }

}
