package DatabaseManagement.ConstraintsHandling;

import DatabaseManagement.Exceptions.MissingValidatorException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constraint {

    private ConstraintEnum constraintEnum;
    private ValidationFunction vFunction;

    public Constraint(ConstraintEnum constraintEnum, ValidationFunction vFunction) {
        this.constraintEnum = constraintEnum;
        this.vFunction = vFunction;
    }

    public Constraint(ConstraintEnum constraintEnum) {
        this.constraintEnum = constraintEnum;
    }

    public ValidationFunction getValidationFunction() throws MissingValidatorException {
        if (vFunction == null) throw new MissingValidatorException();
        else return vFunction;
    }

    @Override
    public boolean equals(Object obj) {
        String constraintString = (String) obj;
        String patternToMatch = constraintEnum.getRegex();

        Pattern pattern = Pattern.compile(patternToMatch);
        Matcher match = pattern.matcher(constraintString);

        return match.matches();
    }

    public static void main(String[] args) {

        // Constraint conPRIMARY = new Constraint(ConstraintEnum.PRIMARY);
        // Constraint conUNIQUE = new Constraint(ConstraintEnum.UNIQUE);
        // Constraint conFOREIGN = new Constraint(ConstraintEnum.FOREIGN);
        // Constraint conLESS_THAN = new Constraint(ConstraintEnum.LESS_THAN);
        // Constraint conGREATER_THAN = new Constraint(ConstraintEnum.GREATER_THAN);
        // Constraint conEQUAL = new Constraint(ConstraintEnum.EQUAL);
        // Constraint conNOT_EQUAL = new Constraint(ConstraintEnum.NOT_EQUAL);
        // Constraint conLESS_EQUAL = new Constraint(ConstraintEnum.LESS_EQUAL);
        // Constraint conGREATER_EQUAL = new Constraint(ConstraintEnum.GREATER_EQUAL);
        // Constraint conNOT_NULL = new Constraint(ConstraintEnum.NOT_NULL);
        // Constraint conLIKE = new Constraint(ConstraintEnum.LIKE);
        // Constraint conBETWEEN = new Constraint(ConstraintEnum.BETWEEN);
        // Constraint conIN = new Constraint(ConstraintEnum.IN);
        // Constraint conNUMBER = new Constraint(ConstraintEnum.NUMBER);
        // Constraint conFLOAT = new Constraint(ConstraintEnum.FLOAT);
        // Constraint conCHAR = new Constraint(ConstraintEnum.CHAR);
        // Constraint conVARCHAR2 = new Constraint(ConstraintEnum.VARCHAR2);
        // Constraint conDATE = new Constraint(ConstraintEnum.DATE);
        // Constraint conREGEXP_LIKE = new Constraint(ConstraintEnum.REGEXP_LIKE);

        // System.out.println("Testing conPRIMARY with " + "P" + " " +
        // conPRIMARY.equals("P"));
        // System.out.println("Testing conUNIQUE with " + "U" + " " +
        // conUNIQUE.equals("U"));
        // System.out.println(
        // "Testing conFOREIGN with " + "R_NAMEOFPRIMARYKEY" + " " +
        // conFOREIGN.equals("R_NAMEOFPRIMARYKEY"));
        // System.out.println("Testing conLESS_THAN with " + "C_X < 4" + " " +
        // conLESS_THAN.equals("C_X < 4"));
        // System.out.println("Testing conGREATER_THAN with " + "C_X > 4" + " " +
        // conGREATER_THAN.equals("C_X > 4"));
        // System.out.println("Testing conEQUAL with " + "C_X = 4" + " " +
        // conEQUAL.equals("C_X = 4"));
        // System.out.println("Testing conNOT_EQUAL with " + "C_X != 4" + " " +
        // conNOT_EQUAL.equals("C_X != 4"));
        // System.out.println("Testing conLESS_EQUAL with " + "C_X <= 4" + " " +
        // conLESS_EQUAL.equals("C_X <= 4"));
        // System.out.println("Testing conGREATER_EQUAL with " + "C_X >= 4" + " " +
        // conGREATER_EQUAL.equals("C_X >= 4"));
        // System.out
        // .println("Testing conNOT_NULL with " + "C_X IS NOT NULL" + " " +
        // conNOT_NULL.equals("C_MALL_NUM IS NOT NULL"));
        // System.out.println("Testing conLIKE with " + "C_X LIKE WHATVER" + " " +
        // conLIKE.equals("C_X LIKE WHATVER"));
        // System.out.println(
        // "Testing conBETWEEN with " + "C_X BETWEEN 5 AND 3" + " " +
        // conBETWEEN.equals("C_X BETWEEN 5 AND 3"));
        // System.out.println(
        // "Testing conIN with " + "C_X IN ('A','B','C','D')" + " " + conIN.equals("C_X
        // IN ('A','B','C','D')"));
        // System.out.println("Testing conNUMBER with " + "NUMBER_5_2" + " " +
        // conNUMBER.equals("NUMBER_5_2"));
        // System.out.println("Testing conFLOAT with " + "FLOAT" + " " +
        // conFLOAT.equals("FLOAT"));
        // System.out.println("Testing conCHAR with " + "CHAR_3" + " " +
        // conCHAR.equals("CHAR_3"));
        // System.out.println("Testing conVARCHAR2 with " + "VARCHAR2_10" + " " +
        // conVARCHAR2.equals("VARCHAR2_10"));
        // System.out.println("Testing conDATE with " + "DATE" + " " +
        // conDATE.equals("DATE"));
        // System.out.println("Testing conREGEXP_LIKE with " + "C_REGEXP_LIKE(X,'SDF')"
        // +
        // " "
        // + conREGEXP_LIKE.equals("C_REGEXP_LIKE(X,'SDF')"));
    }

}
