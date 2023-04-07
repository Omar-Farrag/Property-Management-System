package DatabaseManagement.ConstraintsHandling;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import DatabaseManagement.*;

import DatabaseManagement.ConstraintsHandling.ValidationParameters.OperationType;
import DatabaseManagement.Exceptions.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class ConstraintChecker {

    private static ConstraintChecker instance;

    private Validator validator;
    private ReferentialResolver resolver;
    private DatabaseManager DB;
    private MetaDataExtractor metaData;

    private ConstraintChecker() {

        resolver = ReferentialResolver.getInstance();
        DB = DatabaseManager.getInstance();
        metaData = MetaDataExtractor.getInstance();
        validator = new Validator();


    }

    public static ConstraintChecker getInstance() {
        return instance == null ? instance = new ConstraintChecker() : instance;
    }


    public Errors checkInsertion(Table t, AttributeCollection toInsert)
            throws DBManagementException {
        checkAttributeExistence(t, toInsert);
        int numAttributes = metaData.getTableAttributes(t).size();
        if (numAttributes != toInsert.size())
            throw new InsufficientAttributesException(t, numAttributes, toInsert.size());
        return checkConstraints(t, toInsert, OperationType.INSERT, new Filters());

    }


    public Errors checkRetrieval(Filters filters, AttributeCollection toGet)
            throws DBManagementException {

        checkAttributeExistence(toGet);
        AttributeCollection filterCollection = new AttributeCollection(filters);
        checkAttributeExistence(filterCollection);
        return new Errors();

    }


    public Errors checkRetrieval(Table t, Filters filters) throws DBManagementException {
        AttributeCollection filterCollection = new AttributeCollection(filters);
        checkAttributeExistence(t, filterCollection);
        return new Errors();
    }


    public Errors checkRetrieval(AttributeCollection toGet) throws DBManagementException {

        checkAttributeExistence(toGet);
        return new Errors();
    }


    public Errors checkDeletion(Table t, Filters filters) throws SQLException, DBManagementException {
        AttributeCollection filterCollection = new AttributeCollection(filters);
        checkAttributeExistence(t, filterCollection);
        return checkReferencingTables(t, filters);
    }


    public Errors checkUpdate(Table t, Filters filters, AttributeCollection newValues, boolean cascade)
            throws SQLException, DBManagementException {

        checkAttributeExistence(t, new AttributeCollection(filters));
        checkAttributeExistence(t, newValues);
        Errors constraintErrorsNew = checkConstraints(t, newValues, OperationType.UPDATE, filters);
//        Errors constraintErrorsFilters = checkConstraints(t, new AttributeCollection(filters),OperationType.UPDATE,
//                filters);
        if (cascade) {
            if(!constraintErrorsNew.noErrors()){
                return constraintErrorsNew;
            }
//            if (!constraintErrorsNew.noErrors() || !constraintErrorsFilters.noErrors())
//                return constraintErrorsNew.append(constraintErrorsFilters);
        } else {
            Errors referentialErrors = checkReferencingTables(t, filters);
//            return constraintErrorsNew.append(constraintErrorsFilters).append(referentialErrors);
            return constraintErrorsNew.append(referentialErrors);
        }
        return new Errors();
    }


    private Errors checkReferencingTables(Table t, Filters f) throws TableNotFoundException, AttributeNotFoundException, SQLException, IncompatibleFilterException, ConstraintNotFoundException {
        Errors errors = new Errors();
        ResultSet toDelete = null;
        try {
            toDelete = DB.retrieve(t, f).getResult();
        } catch (DBManagementException e) {
            throw new RuntimeException(e);
        }
        AttributeCollection referencedAttributes = resolver.getReferencedAttributes(t);

        while (toDelete.next()) {
            for (Attribute attribute : referencedAttributes.attributes()) {
                String toDeleteValue = toDelete.getString(attribute.getStringName());
                Attribute toFindReferences = new Attribute(attribute.getAttributeName(),
                        toDeleteValue, attribute.getT());

                HashMap<Table, Filters> referencingAttributes = resolver.getReferencingAttributes(t, toFindReferences);

                for (Map.Entry<Table, Filters> entry : referencingAttributes.entrySet()) {
                    try {
                        if (DB.retrieve(entry.getKey(), entry.getValue()).getRowsAffected() > 0) {
                            String errorMessage =
                                    "Cannot Delete/Modify Entry With " + attribute.getStringName() +
                                            " = " +
                                            toDeleteValue + " because it is referenced by table " + entry.getKey().getTableName();

                            errors.add(attribute, errorMessage);
                        }
                    } catch (DBManagementException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return errors;
    }

    private Errors checkConstraints(Table t, AttributeCollection allAttributes, OperationType type,Filters filters) throws TableNotFoundException,
            ConstraintNotFoundException {
        JSONObject table = metaData.getTableInfoFromMetaData(t);
        JSONObject tableAttributes = (JSONObject) table.get("Attributes");
        Errors errors = new Errors();

        for (Attribute attribute : allAttributes.attributes()) {
            JSONArray attributeConstraints = (JSONArray) tableAttributes.get(attribute.getStringName());
            for (Object obj : attributeConstraints) {
                String constraint = (String) obj;
                try {
                    ValidationParameters parameters = new ValidationParameters(constraint,attribute, allAttributes,
                            type, filters);
                    errors.add(attribute, validator.validate(parameters));
                } catch (
                        MissingValidatorException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                } catch (DBManagementException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return errors;
    }

//    private Errors checkConstraints(AttributeCollection toValidate,String operationType) throws TableNotFoundException,
//            ConstraintNotFoundException {
//        Errors errors = new Errors();
//
//        for (Attribute attribute : toValidate.attributes()) {
//            JSONObject table = metaData.getTableInfoFromMetaData(attribute.getT());
//            JSONObject tableAttributes = (JSONObject) table.get("Attributes");
//            JSONArray attributeConstraints = (JSONArray) tableAttributes.get(attribute.getStringName());
//
//            for (Object obj : attributeConstraints) {
//                String constraint = (String) obj;
//                try {
//                    errors.add(attribute, validator.validate(constraint, attribute, toValidate,operationType));
//                } catch (
//                        MissingValidatorException e) {
//                    System.out.println(e.getMessage());
//                    e.printStackTrace();
//                } catch (DBManagementException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//        return errors;
//    }

    private void checkAttributeExistence(Table t, AttributeCollection toValidate) throws AttributeNotFoundException, TableNotFoundException {
        JSONObject table = metaData.getTableInfoFromMetaData(t);
        JSONObject tableAttributes = (JSONObject) table.get("Attributes");

        for (Attribute attribute : toValidate.attributes()) {
            if (!tableAttributes.containsKey(attribute.getStringName()))
                throw new AttributeNotFoundException(t.getTableName(), attribute.getStringName());
        }
    }

    private void checkAttributeExistence(AttributeCollection toGet) throws AttributeNotFoundException, TableNotFoundException {
        for (Attribute attribute : toGet.attributes()) {
            JSONObject table = metaData.getTableInfoFromMetaData(attribute.getT());
            JSONObject tableAttributes = (JSONObject) table.get("Attributes");
            if (!tableAttributes.containsKey(attribute.getStringName()))
                throw new AttributeNotFoundException(attribute.getT().getTableName(), attribute.getStringName());
        }
    }


    public class Errors {
        private final HashMap<Attribute, ArrayList<String>> attribute_to_errors;

        private Errors() {
            attribute_to_errors = new HashMap<>();
        }

        private void add(Attribute attribute, String errorMessage) {
            if (errorMessage.isEmpty()) {
                return;
            }

            if (!attribute_to_errors.containsKey(attribute))
                attribute_to_errors.put(attribute, new ArrayList<String>());

            attribute_to_errors.get(attribute).add(errorMessage);


        }

        public Errors append(Errors error) {
            for (Map.Entry<Attribute, ArrayList<String>> entry : error.attribute_to_errors.entrySet())
                for (String message : entry.getValue())
                    add(entry.getKey(), message);
            return this;
        }

        public boolean noErrors() {
            return attribute_to_errors.isEmpty();
        }

        public ArrayList<String> getErrorByAttribute(Attribute attribute) throws UnvalidatedAttributeException {
            if (!attribute_to_errors.containsKey(attribute))
//                throw new UnvalidatedAttributeException(attribute);
                return new ArrayList<>();
            else
                return attribute_to_errors.get(attribute);
        }

    }
}
