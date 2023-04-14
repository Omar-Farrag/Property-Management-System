package DatabaseManagement.ConstraintsHandling;

import DatabaseManagement.*;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.ConstraintsHandling.MetaDataExtractor.Key;
import DatabaseManagement.ConstraintsHandling.ValidationParameters.OperationType;
import DatabaseManagement.Exceptions.ConstraintNotFoundException;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Exceptions.MissingValidatorException;
import DatabaseManagement.ConstraintsHandling.ReferentialResolver.DetailedKey;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    private ArrayList<Constraint> constraints;

    public Validator() {
        initConstraintsToValidatorMap();
    }

    public String validate(ValidationParameters parameters) throws DBManagementException {
        ValidationFunction validationFunc = find(parameters.getConstraint());
        return validationFunc.validate(parameters);

    }

    private ValidationFunction find(String constraintToSearchFor)
            throws ConstraintNotFoundException, MissingValidatorException {
        for (Constraint constraint : constraints) {
            if (constraint.equals(constraintToSearchFor)) {
                return constraint.getValidationFunction();
            }
        }
        throw new ConstraintNotFoundException(constraintToSearchFor);
    }

    public String validatePRIMARY(ValidationParameters parameters) {
        String errorMessage = validateNOT_NULL(parameters);
        if (!errorMessage.isEmpty()) {
            return errorMessage;
        }

        OperationType operationType = parameters.getOperationType();

        try {
            if (operationType.equals(OperationType.UPDATE)) {
                if (!validPKUpdate(parameters)) {
                    return parameters.getToValidate().getStringName() + " cannot be used "
                            + "because it would duplicate an existing primary key";
                }
            } else if (operationType.equals(OperationType.INSERT)) {
                if (!validPKInsert(parameters)) {
                    return parameters.getToValidate().getStringName() + " cannot be used "
                            + "because it would duplicate an existing primary key";
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return "";
    }

    private boolean validPKInsert(ValidationParameters parameters) throws SQLException {
        Attribute toValidate = parameters.getToValidate();
        AttributeCollection allAttributes = parameters.getAllAttributes();

        Key primaryKeys = MetaDataExtractor.getInstance().getPrimaryKeys(toValidate.getT());

        Filters filters = new Filters();

        for (Attribute key : primaryKeys.getKeyAttributes()) {
            Name name = key.getAttributeName();
            String value = allAttributes.getValue(key);
            Table t = key.getT();
            Attribute valuedAttribute = new Attribute(name, value, t);
            filters.addEqual(valuedAttribute);
        }
        String query = "Select * from " + toValidate.getT().getAliasedName() + " "
                + filters.getFilterClause();

        ResultSet result = DatabaseManager.getInstance().executeStatement(query);
        return !result.next();

    }

    private boolean validPKUpdate(ValidationParameters parameters) throws SQLException {
        Attribute toValidate = parameters.getToValidate();
        Filters filters = parameters.getFilters();
        Key primaryKeys = MetaDataExtractor.getInstance().getPrimaryKeys(toValidate.getT());

        if (primaryKeys.getKeyAttributes().size() <= 1) {
            String query = "Select * from " + toValidate.getT().getAliasedName()
                    + " where " + toValidate.getStringName() + " = " + toValidate.getStringValue();
            ResultSet result = DatabaseManager.getInstance().executeStatement(query);
            return !result.next();
        }

        String query = "Select * from " + toValidate.getT().getAliasedName() + " " + filters.getFilterClause();
        ResultSet result = DatabaseManager.getInstance().executeStatement(query);

        HashSet<Key> keys = new HashSet<>();

        while (result.next()) {
            Key key = new Key();
            for (Attribute attribute : primaryKeys.getKeyAttributes()) {
                Name name = attribute.getAttributeName();
                String value = result.getString(name.getName());
                Table t = attribute.getT();
                Attribute valuedAttribute = new Attribute(name, value, t);
                key.add(valuedAttribute);
            }
            if (keys.contains(key)) {
                return false;
            } else {
                keys.add(key);
            }
        }
        return true;
    }

    public String validateUNIQUE(ValidationParameters parameters) {
        String errorMessage = validateNOT_NULL(parameters);
        if (!errorMessage.isEmpty()) {
            return errorMessage;
        }

        OperationType operationType = parameters.getOperationType();

        try {
            if (operationType.equals(OperationType.UPDATE)) {
                if (!validUKUpdate(parameters)) {
                    return parameters.getToValidate().getStringName() + " cannot be used "
                            + "because it would duplicate an existing primary key";
                }
            } else if (operationType.equals(OperationType.INSERT)) {
                if (!validUKInsert(parameters)) {
                    return parameters.getToValidate().getStringName() + " cannot be used "
                            + "because it would duplicate an existing primary key";
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return "";
    }

    private boolean validUKInsert(ValidationParameters parameters) throws SQLException {
        Attribute toValidate = parameters.getToValidate();
        AttributeCollection allAttributes = parameters.getAllAttributes();

        ArrayList<Key> uniqueKeys = MetaDataExtractor.getInstance().getUniqueKeys(toValidate.getT());

        Filters filters = new Filters();

        for (Key uniqueKey : uniqueKeys) {
            for (Attribute key : uniqueKey.getKeyAttributes()) {
                Name name = key.getAttributeName();
                String value = allAttributes.getValue(key);
                Table t = key.getT();
                Attribute valuedAttribute = new Attribute(name, value, t);
                filters.addEqual(valuedAttribute);
            }
            String query = "Select * from " + toValidate.getT().getAliasedName() + " "
                    + filters.getFilterClause();

            ResultSet result = DatabaseManager.getInstance().executeStatement(query);
            if (result.next()) {
                return false;
            }
        }
        return true;
    }

    private boolean validUKUpdate(ValidationParameters parameters) throws SQLException {
        Attribute toValidate = parameters.getToValidate();
        Filters filters = parameters.getFilters();
        ArrayList<Key> uniqueKeys = MetaDataExtractor.getInstance().getUniqueKeys(toValidate.getT());

//        if (uniqueKeys.getKeyAttributes().size() <= 1) {
//            String query = "Select * from " + toValidate.getT().getAliasedName()
//                    + " where " + toValidate.getStringName() + " = " + toValidate.getStringValue();
//            ResultSet result = DatabaseManager.getInstance().executeStatement(query);
//            return !result.next();
//        }
        String query = "Select * from " + toValidate.getT().getAliasedName() + " " + filters.getFilterClause();
        ResultSet result = DatabaseManager.getInstance().executeStatement(query);

        HashSet<Key> keys = new HashSet<>();
        while (result.next()) {

            for (Key uniqueKey : uniqueKeys) {

                Key key = new Key();
                for (Attribute attribute : uniqueKey.getKeyAttributes()) {
                    Name name = attribute.getAttributeName();
                    String value = result.getString(name.getName());
                    Table t = attribute.getT();
                    Attribute valuedAttribute = new Attribute(name, value, t);
                    key.add(valuedAttribute);
                }
                if (keys.contains(key)) {
                    return false;
                } else {
                    keys.add(key);
                }
            }
        }
        return true;
    }

    public String validateFOREIGN(ValidationParameters parameters) {

        String constraint = parameters.getConstraint();
        Attribute toValidate = parameters.getToValidate();

        constraint = constraint.substring(2).trim();
        DetailedKey referenced = ReferentialResolver.getInstance().getReferencedTable(constraint);

        String query
                = "Select * from " + referenced.t.getTableName() + " where " + referenced.column.getName() + " = " + toValidate.getStringValue();
        ResultSet result = null;
        try {
            result = DatabaseManager.getInstance().executeStatement(query);
            if (!result.next()) {
                return toValidate.getStringName() + " = " + toValidate.getStringValue() + ": This value is referencing a non-existent entry";
            } else {
                return "";
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return "Something went wrong";
        }
    }

    public String validateLESS_THAN(ValidationParameters parameters) {
        ComparisonResult comparisonResult = compare(parameters, "<");

        if (comparisonResult.fieldIsNull || comparisonResult.result == -1) {
            return "";
        } else if (comparisonResult.testFailed) {
            return parameters.getToValidate().getStringName() + ": Value you entered could not be compared with"
                    + " stored ranges";
        } else {
            return comparisonResult.leftOperand + " must be less than " + comparisonResult.rightOperand;
        }
    }

    public String validateGREATER_THAN(ValidationParameters parameters) {
        ComparisonResult comparisonResult = compare(parameters, ">");

        if (comparisonResult.fieldIsNull || comparisonResult.result == 1) {
            return "";
        } else if (comparisonResult.testFailed) {
            return parameters.getToValidate().getStringName() + ": Value you entered could not be compared with"
                    + " stored ranges";
        } else {
            return comparisonResult.leftOperand + " must be greater than " + comparisonResult.rightOperand;
        }
    }

    public String validateEQUAL(ValidationParameters parameters) {
        ComparisonResult comparisonResult = compare(parameters, "=");

        if (comparisonResult.fieldIsNull || comparisonResult.result == 0) {
            return "";
        } else if (comparisonResult.testFailed) {
            return parameters.getToValidate().getStringName() + ": Value you entered could not be compared with"
                    + " stored ranges";
        } else {
            return comparisonResult.leftOperand + " must be equal to " + comparisonResult.rightOperand;
        }
    }

    public String validateNOT_EQUAL(ValidationParameters parameters) {
        ComparisonResult comparisonResult = compare(parameters, "!=");

        if (comparisonResult.fieldIsNull || comparisonResult.result != 0) {
            return "";
        } else if (comparisonResult.testFailed) {
            return parameters.getToValidate().getStringName() + ": Value you entered could not be compared with"
                    + " stored ranges";
        } else {
            return comparisonResult.leftOperand + " must not be equal to " + comparisonResult.rightOperand;
        }
    }

    public String validateLESS_EQUAL(ValidationParameters parameters) {
        ComparisonResult comparisonResult = compare(parameters, "<=");

        if (comparisonResult.fieldIsNull || comparisonResult.result <= 0) {
            return "";
        } else if (comparisonResult.testFailed) {
            return parameters.getToValidate().getStringName() + ": Value you entered could not be compared with"
                    + " stored ranges";
        } else {
            return comparisonResult.leftOperand + " must be less than or equal to " + comparisonResult.rightOperand;
        }
    }

    public String validateGREATER_EQUAL(ValidationParameters parameters) {
        ComparisonResult comparisonResult = compare(parameters, ">=");

        if (comparisonResult.fieldIsNull || comparisonResult.result >= 0) {
            return "";
        } else if (comparisonResult.testFailed) {
            return parameters.getToValidate().getStringName() + ": Value you entered could not be compared with"
                    + " stored ranges";
        } else {
            return comparisonResult.leftOperand + " must be greater than or equal to " + comparisonResult.rightOperand;
        }
    }

    private ComparisonResult compare(ValidationParameters parameters, String operator) {
        try {
            String constraint = parameters.getConstraint();
            Attribute toValidate = parameters.getToValidate();
            AttributeCollection allAttributes = parameters.getAllAttributes();

            constraint = constraint.replace("C_", "");
            String[] operands = constraint.split(operator);
            operands[0] = operands[0].trim();
            operands[1] = operands[1].trim();

            Operands resolvedOperands = new Operands(operands, toValidate, allAttributes);
            return new ComparisonResult(operands[0], operands[1], resolvedOperands.compare(),
                    false, false);

        } catch (ParseException | NumberFormatException e) {
            return new ComparisonResult(null, null, -2, false, true);
        } catch (NullPointerException e) {
            return new ComparisonResult(null, null, -2, true, false);

        }
    }

    private Comparable<? extends Comparable<?>> convert(Attribute attribute) throws ParseException, NumberFormatException, NullPointerException {
        switch (attribute.getType()) {
            case DATE -> {
                String dateFormat = "dd-MMM-yyyy";
                SimpleDateFormat simpleFormat = new SimpleDateFormat(dateFormat);
                simpleFormat.setLenient(false);
                return simpleFormat.parse(attribute.getValue());
            }
            case NUMBER -> {
                return Double.parseDouble(attribute.getValue());
            }
            default -> {
                return attribute.getValue();
            }
        }

    }

    public String validateNOT_NULL(ValidationParameters parameters) {
        Attribute toValidate = parameters.getToValidate();

        if (toValidate.getValue() == null || toValidate.getValue().isEmpty()) {
            return toValidate.getStringName() + ": Field cannot be null";
        } else {
            return "";
        }
    }

    public String validateBETWEEN(ValidationParameters parameters) {
        try {
            String constraint = parameters.getConstraint();
            Attribute toValidate = parameters.getToValidate();
            AttributeCollection allAttributes = parameters.getAllAttributes();

            String[] range
                    = constraint.split("BETWEEN")[1].split("AND");

            range[0] = range[0].trim();
            range[1] = range[1].trim();

            Operands firstOperation = new Operands(new String[]{toValidate.getStringName(), range[0]}, toValidate,
                    allAttributes);

            Operands secondOperation = new Operands(new String[]{toValidate.getStringName(), range[1]}, toValidate,
                    allAttributes);

            int result1 = firstOperation.compare();
            int result2 = secondOperation.compare();

            if (result1 >= 0 && result2 <= 0) {
                return "";
            }

            for (String value : range) {
                value = value.replace("'", "");
                if (value.equals(toValidate.getValue())) {
                    return "";
                }
            }
            return toValidate.getStringName() + ": Must be between " + range[0] + " and " + range[1];
        } catch (ParseException | NumberFormatException e) {
            return parameters.getToValidate().getStringName() + ": Value entered could not be compared with stored ranges";
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String validateIN(ValidationParameters parameters) {
        String constraint = parameters.getConstraint();
        Attribute toValidate = parameters.getToValidate();

        String[] acceptedValues
                = constraint.split("IN")[1].replace("(", "").replace(")", "").trim().split(",");

        for (String value : acceptedValues) {
            value = value.replace("'", "");
            if (value.equals(toValidate.getValue())) {
                return "";
            }
        }
        return toValidate.getStringName() + ": must be in one of these values: " + String.join(",",
                acceptedValues);
    }

    public String validateLIKE(ValidationParameters parameters) {

        Attribute toValidate = parameters.getToValidate();
        String constraint = parameters.getConstraint();

        String value = toValidate.getValue();
        String pattern = constraint.split("LIKE")[1].trim().replace("'", "");

        if (value == null || value.isEmpty() || regexMatch(value, pattern)) {
            return "";
        } else {
            return toValidate.getStringName() + ": must be in the following format: " + pattern;
        }
    }

    public String validateREGEXP_LIKE(ValidationParameters parameters) {
        Attribute toValidate = parameters.getToValidate();
        String constraint = parameters.getConstraint();

        String value = toValidate.getValue();
        int firstCommaIndex = constraint.indexOf(",");

        String pattern = constraint.substring(firstCommaIndex + 1);
        pattern = pattern.substring(2, pattern.length() - 2).trim();

        if (value == null || value.isEmpty() || regexMatch(value, pattern)) {
            return "";
        } else {
            return toValidate.getStringName() + ": must be in the following format: " + pattern;
        }
    }

    private boolean regexMatch(String value, String regex) {
        regex = regex.replace("%", ".*").replace("_", ".");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public String validateNUMBER(ValidationParameters parameters) {
        Attribute toValidate = parameters.getToValidate();
        String constraint = parameters.getConstraint();

        try {
            // tries converting userInput to a BigDecimal
            // throws an exception if it can't
            // An exception means userInput is not a valid NUMBER(precision, scale)

            BigDecimal number = new BigDecimal(toValidate.getValue());

            String[] decomposedConstraint = constraint.split("_");

            int precision = Integer.parseInt(decomposedConstraint[1]);
            int scale = Integer.parseInt(decomposedConstraint[2]);

            if (number.scale() > scale) {
                return toValidate.getStringName() + ": Number must not have more than " + scale + " digits after the decimal "
                        + "point";
            }

            // If number's precision is less than the maximum, yet its precision
            // prevents it from having the required scale, userInput is invalid
            // Example : precision = 7, scale = 2, number = 123456
            // number's precision is 6. However, adding two digits after the decimal point
            // (number = 123456.00) to achieve a scale of 2 results in number's precision
            // becoming 8, which exceeds the maximum allowed precision of 7
            if (number.precision() + scale > precision) {
                return toValidate.getStringName() + ": Number must not have more than " + (precision - scale) + " digits before "
                        + "the decimal point";
            }

            if (number.precision() > precision) {
                return toValidate.getStringName() + ": Number must not exceed " + precision + " digits";
            }

            return "";
        } catch (NumberFormatException e) {
            if (toValidate.getValue().isEmpty()) {
                return "";
            } else {
                return toValidate.getStringName() + ": Value entered is not a number";
            }
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String validateFLOAT(ValidationParameters parameters) {
        Attribute toValidate = parameters.getToValidate();
        try {
            Float.parseFloat(toValidate.getValue());
            return "";
        } catch (NumberFormatException e) {
            if (toValidate.getValue().isEmpty()) {
                return "";
            } else {
                return toValidate.getStringName() + ": Value entered is not a floating point number";
            }
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String validateCHAR(ValidationParameters parameters) {
        String constraint = parameters.getConstraint();
        Attribute toValidate = parameters.getToValidate();

        int length = Integer.parseInt(constraint.split("_")[1]);
        try {
            if (toValidate.getValue().isEmpty()) {
                return "";
            }
            if (toValidate.getValue().length() != length) {
                return toValidate.getStringName() + ": Value must have " + length + " characters exactly";
            } else {
                return "";
            }
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String validateVARCHAR2(ValidationParameters parameters) {
        String constraint = parameters.getConstraint();
        Attribute toValidate = parameters.getToValidate();

        int maxLength = Integer.parseInt(constraint.split("_")[1]);
        try {
            if (toValidate.getValue().length() > maxLength) {
                return toValidate.getStringName() + ": Value too long. A maximum of " + maxLength + " characters is allowed";
            } else {
                return "";
            }
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String validateDATE(ValidationParameters parameters) {

        Attribute toValidate = parameters.getToValidate();

        // The main logic of this function, is to match the userInput with the
        // dateFormat specified below. If SimpleDateFormat object can parse a date with
        // the specified dateFormat from the userInput, then the userInput is a valid
        // DATE
        // For some reason, the above fails to recognize a date like '15-feb-202222' as
        // invalid. Therefore, as an extra check before performing the above, regex
        // matching is done to ensure that the format of the userInput is precisely
        // dd-mon-yyyy;
        try {
            String regex = "\\d{1,2}-.{3}-\\d{1,4}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(toValidate.getValue());
            if (!matcher.matches()) {
                return toValidate.getStringName() + ": Invalid Date Format. Date must be in this format: dd-MMM-yyyy (eg. "
                        + "01-JAN-1970)";
            }

            String dateFormat = "dd-MMM-yyyy";
            SimpleDateFormat simpleFormat = new SimpleDateFormat(dateFormat);

            try {
                simpleFormat.setLenient(false);
                // if the date cannot be parsed from userInput, an exception is thrown
                // indicating userInput is invalid Date
                simpleFormat.parse(toValidate.getValue());
                return "";
            } catch (ParseException e) {
                if (toValidate.getValue().isEmpty()) {
                    return "";
                } else {
                    return toValidate.getStringName() + ": Invalid Date Format. Date must be in this format: dd-MMM-yyyy (eg. "
                            + "01-JAN-1970)";
                }
            }
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String validateTIMESTAMP(ValidationParameters parameters) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm:ss a");
        String inputDate = parameters.getToValidate().getValue();
        try {
            LocalDateTime.parse(inputDate, formatter);
            return "";
        } catch (DateTimeException e) {
            String validAsDate = validateDATE(parameters);
            if (validAsDate.isEmpty()) {
                return "";
            } else {
                return parameters.getToValidate().getStringName() + ": Invalid Timestamp. Must be in format dd-MMM-yyyy hh:mm:ss a.";
            }
        }
    }

    private void initConstraintsToValidatorMap() {
        constraints = new ArrayList<>();

        constraints.add(new Constraint(ConstraintEnum.PRIMARY, this::validatePRIMARY));
        constraints.add(new Constraint(ConstraintEnum.UNIQUE, this::validateUNIQUE));
        constraints.add(new Constraint(ConstraintEnum.FOREIGN, this::validateFOREIGN));
        constraints.add(new Constraint(ConstraintEnum.LESS_THAN, this::validateLESS_THAN));
        constraints.add(new Constraint(ConstraintEnum.GREATER_THAN, this::validateGREATER_THAN));
        constraints.add(new Constraint(ConstraintEnum.EQUAL, this::validateEQUAL));
        constraints.add(new Constraint(ConstraintEnum.NOT_EQUAL, this::validateNOT_EQUAL));
        constraints.add(new Constraint(ConstraintEnum.LESS_EQUAL, this::validateLESS_EQUAL));
        constraints.add(new Constraint(ConstraintEnum.GREATER_EQUAL, this::validateGREATER_EQUAL));
        constraints.add(new Constraint(ConstraintEnum.NOT_NULL, this::validateNOT_NULL));
        constraints.add(new Constraint(ConstraintEnum.LIKE, this::validateLIKE));
        constraints.add(new Constraint(ConstraintEnum.BETWEEN, this::validateBETWEEN));
        constraints.add(new Constraint(ConstraintEnum.IN, this::validateIN));
        constraints.add(new Constraint(ConstraintEnum.REGEXP_LIKE, this::validateREGEXP_LIKE));
        constraints.add(new Constraint(ConstraintEnum.NUMBER, this::validateNUMBER));
        constraints.add(new Constraint(ConstraintEnum.FLOAT, this::validateFLOAT));
        constraints.add(new Constraint(ConstraintEnum.CHAR, this::validateCHAR));
        constraints.add(new Constraint(ConstraintEnum.VARCHAR2, this::validateVARCHAR2));
        constraints.add(new Constraint(ConstraintEnum.DATE, this::validateDATE));
        constraints.add(new Constraint(ConstraintEnum.TIMESTAMP, this::validateTIMESTAMP));
    }

    private class ComparisonResult {

        private String leftOperand;
        private String rightOperand;
        private int result;
        private boolean fieldIsNull;
        private boolean testFailed;

        public ComparisonResult(String leftOperand, String rightOperand, int result,
                boolean fieldIsNull, boolean testFailed) {
            this.leftOperand = leftOperand;
            this.rightOperand = rightOperand;
            this.result = result;
            this.fieldIsNull = fieldIsNull;
            this.testFailed = testFailed;
        }
    }

    private class Operands {

        private Comparable lvalue = null;
        private Comparable rvalue = null;

        private Operands(String[] operands,
                Attribute toValidate, AttributeCollection allAttributes) throws ParseException, NumberFormatException, NullPointerException {
            prepareOperands(operands, toValidate, allAttributes);
        }

        private void prepareOperands(String[] operands,
                Attribute toValidate, AttributeCollection allAttributes) throws ParseException, NumberFormatException, NullPointerException {
            for (Attribute attribute : allAttributes.attributes()) {
                String attributeName = attribute.getStringName();
                if (attributeName.equals(operands[0])) {
                    lvalue = convert(attribute);
                } else if (attributeName.equals(operands[1])) {
                    rvalue = convert(attribute);
                }
            }

            if (lvalue == null) {
                lvalue = convert(new Attribute(toValidate.getAttributeName(),
                        operands[0], toValidate.getT()));
            }

            if (rvalue == null) {
                rvalue = convert(new Attribute(toValidate.getAttributeName(),
                        operands[1], toValidate.getT()));
            }
        }

        private int compare() {
            return lvalue.compareTo(rvalue);
        }
    }

}
