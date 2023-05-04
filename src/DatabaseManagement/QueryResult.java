package DatabaseManagement;

import DatabaseManagement.ConstraintsHandling.ConstraintChecker.Errors;
import DatabaseManagement.Exceptions.DBManagementException;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;

public class QueryResult {

    private ResultSet result;
    private int rows;
    private Errors errors;

    public QueryResult(ResultSet result, int rows, Errors errors) {
        this.result = result;
        this.rows = rows;
        this.errors = errors;
    }

    /**
     * @return result set of a retrieval operation or null for other
     * insertion/deletion/modification
     */
    public ResultSet getResult() {
        return result;
    }

    /**
     * @return number of rows inserted/deleted/modified. For retrieval
     * operations, it returns number of rows retrieved
     */
    public int getRowsAffected() {
        return rows;
    }

    /**
     * @return True if all input data was valid and that the database operation
     * was successful. Returns false otherwise
     */
    public boolean noErrors() {
        return errors == null || errors.noErrors();
    }

    /**
     * @param attribute Attribute whose validation errors are to be retrieved
     * @return The list of validation errors on the given attribute
     * @throws DBManagementException Print the message to know why it was thrown
     */
    public ArrayList<String> getErrorByAttribute(Attribute attribute) throws DBManagementException {
        return errors.getErrorByAttribute(attribute);
    }

    /**
     * @return The list of all validation errors on all attributes involved in
     * an insertion/modification
     */
    public HashSet<String> getAllErrors() {
        return errors.getAllErrors();
    }
}
