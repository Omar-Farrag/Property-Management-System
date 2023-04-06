package DatabaseManagement.ConstraintsHandling;

public enum ConstraintEnum {
    // Names of constraints come here.
    PRIMARY("^P_\\w+"),
    UNIQUE("^U_\\w+"),
    FOREIGN("^R_\\w+"),
    LESS_THAN("^C_\\s*\\w+\\s*<\\s*\\w+"),
    GREATER_THAN("^C_\\s*\\w+\\s*>\\s*\\w+"),
    EQUAL("^C_\\s*\\w+\\s*=\\s*\\w+"),
    NOT_EQUAL("^C_\\s*\\w+\\s*!=\\s*\\w+"),
    LESS_EQUAL("^C_\\s*\\w+\\s*<=\\s*\\w+"),
    GREATER_EQUAL("^C_\\s*\\w+\\s*>=\\s*\\w+"),
    NOT_NULL("^C_\\s*\\w+\\s+IS NOT NULL"),
    LIKE("^C_\\s*\\w+\\s+LIKE\\s+'[^']*'$"),
    BETWEEN("^C_\\s*\\w+\\s+BETWEEN\\s+\\w+\\s+AND\\s+\\w+\\s*$"),
    IN("^C_\\s+\\w+\\s+IN\\s*\\(\\s*('[^']*'\\s*(,\\s*'[^']*')*\\s*)\\s*\\)\\s*$"),
    REGEXP_LIKE("^C_\\s*REGEXP_LIKE\\(\\w+, '[^']*'\\)$"),
    NUMBER("NUMBER_\\d{1,}_\\d{1,}"),
    FLOAT("FLOAT"),
    CHAR("CHAR_\\d{1,}"),
    VARCHAR2("VARCHAR2_\\d{1,}"),
    DATE("DATE");

    private String name;

    private ConstraintEnum(String name) {
        this.name = name;
    }

    public String getRegex() {
        return name;
    }

}
