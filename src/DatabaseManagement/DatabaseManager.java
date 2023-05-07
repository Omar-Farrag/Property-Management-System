package DatabaseManagement;

import DatabaseManagement.Attribute.Name;
import java.sql.*;

import DatabaseManagement.ConstraintsHandling.ConstraintChecker;
import DatabaseManagement.ConstraintsHandling.ConstraintChecker.Errors;
import DatabaseManagement.ConstraintsHandling.MetaDataExtractor;
import DatabaseManagement.Exceptions.*;
import DatabaseManagement.QueryGeneration.*;
import DatabaseManagement.QueryGeneration.Graph.Node;
import General.Controller;
import General.LoginUser;
import java.util.HashSet;
import java.util.Set;

public class DatabaseManager {

    private final String URL = "jdbc:oracle:thin:@coeoracle.aus.edu:1521:orcl";
    private final String username = "b00087320";
    private final String password = "b00087320";
    private Connection conn;
    private static DatabaseManager instance;

    private DatabaseManager() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(URL, username, password);
        } catch (SQLException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConn() {
        return conn;
    }

    public String getUsername() {
        return username.toUpperCase();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return conn.getMetaData();
    }

    /**
     * Inserts the given attribute collection to the given table. The attributes
     * in the collection must match the attributes in the table, but the order
     * is irrelevant. If you want to set one of the attributes in the table to
     * null, then set the value of that attribute in the attribute collection to
     * null, pass an empty string to the attribute's value.
     *
     * @param toInsert list of attributes forming the tuple to be inserted
     * @param t Table where the tuple will be inserted
     * @return The result of the insertion operation
     * @throws SQLException If an error occurs while inserting the data into the
     * DB.
     * @throws DBManagementException If the attributes in toInsert do not match
     * the attributes in table T
     */
    public QueryResult insert(Table t, AttributeCollection toInsert) throws SQLException, DBManagementException, IllegalArgumentException {
        if (t == null || toInsert == null) {
            throw new IllegalArgumentException("Neither table nor collection to insert can be null");
        }
        Errors error = null;

        error = ConstraintChecker.getInstance().checkInsertion(t, toInsert);

        String query = "Insert into " + t.getTableName() + "(" + toInsert.getFormattedAtt() + ") "
                + "values(" + toInsert.getFormattedValues() + ")";

        return handleDBOperation(error, query,
                true);
    }

    /**
     * Deletes rows in the given table that satisfy all the given filters
     * provided these rows are not referenced by other rows. Passing an empty
     * filters object will delete the whole table.
     *
     * @param t Table whose entries are to be deleted.
     * @param filters Conditions that a row must satisfy to be deleted
     * @return Query result of the delete operation
     * @throws SQLException If an error occurs while data the data from the DB.
     * @throws DBManagementException Print the message to know why the exception
     * was thrown
     */
    public QueryResult delete(Table t, Filters filters) throws SQLException, DBManagementException {
        Errors error = null;
        try {
            error = ConstraintChecker.getInstance().checkDeletion(t, filters);
        } catch (DBManagementException e) {
            throw new RuntimeException(e);
        }
        String query = "Delete From " + t.getAliasedName();
        if (!filters.getFilterClause().isEmpty()) {
            query += " WHERE " + filters.getFilterClause();
        }

        return handleDBOperation(error, query, true);
    }

    /**
     * Updates the values of certain rows in the given table
     *
     * @param t Table whose rows are to be modified
     * @param filters Conditions that a row must satisfy to be updated
     * @param toModify Attribute collection containing the attributes to be
     * modified and their new values
     * @param cascade A flag determining whether the changes in the given table
     * must be cascaded to the referencing foreign keys. If they are to be
     * cascaded, then the foreign key attributes referencing them must have the
     * on Update Cascade option in the DBMS. Otherwise, an exception is thrown.
     * @return Result of modification operation
     * @throws SQLException If an error occurs while updating the data in the
     * DB.
     * @throws DBManagementException Print the message to know why the exception
     * was thrown
     */
    public QueryResult modify(Table t, Filters filters, AttributeCollection toModify, boolean cascade) throws SQLException, DBManagementException {

        if (toModify.isEmpty()) {
            throw new MissingUpdatedValuesException(t);
        }

        Errors error = null;
        try {
            error = ConstraintChecker.getInstance().checkUpdate(t, filters, toModify, cascade);
        } catch (DBManagementException e) {
            throw new RuntimeException(e);
        }
        String query
                = "Update " + t.getAliasedName() + " " + QueryGenerator.getSetClause(toModify);

        if (!filters.getFilterClause().isEmpty()) {
            query += " WHERE " + filters.getFilterClause();
        }

        return handleDBOperation(error, query, true);

    }

    /**
     * Retrieves all rows from a specific table
     *
     * @param t Table whose rows are to be retrieved
     * @return QueryResult containing the result set of the retrieved table
     * @throws SQLException If an error occurs while retrieving the data from
     * the DB.
     */
    public QueryResult retrieve(Table t) throws SQLException {

        String query = "Select * from " + t.getAliasedName();
        return handleDBOperation(null, query, false);

    }

    /**
     * Retrieves specific rows from a given table
     *
     * @param t Table containing the rows to be retrieved
     * @param filters Conditions that a row must satisfy to be part of the
     * retrieved set of rows
     * @return QueryResult containing a result set of the retrieved rows
     * @throws SQLException If an error occurs while retrieving the data from
     * the DB.
     * @throws DBManagementException Print the message to know why the exception
     * was thrown
     *
     */
    public QueryResult retrieve(Table t, Filters filters) throws SQLException, DBManagementException {

        Errors error = null;
        try {
            error = ConstraintChecker.getInstance().checkRetrieval(t, filters);
        } catch (DBManagementException e) {
            throw new RuntimeException(e);
        }
        String query = "Select * from " + t.getAliasedName();
        if (!filters.getFilterClause().isEmpty()) {
            query += " WHERE " + filters.getFilterClause();
        }
        return handleDBOperation(error, query, false);
    }

    /**
     * Joins the tables containing the attributes of the given filter and
     * retrieves the rows that satisfy the conditions in the filter. All
     * attributes in those tables are selected. Bear in mind that the tables
     * containing the attributes must be eligible for joining, otherwise an
     * exception is thrown
     *
     * @param filters conditions that a row must satisfy to be retrieved
     * @return QueryResult containing the result set of the retrieval operation
     * @throws SQLException If an error occurs while retrieving the data from
     * the DB.
     * @throws DBManagementException Print the message to know why the exception
     * was thrown
     *
     * @deprecated use {@link #retrieve(Table t, Filters filters)} instead.
     *
     */
    @Deprecated
    public QueryResult retrieve(Filters filters) throws SQLException, DBManagementException {
        Errors error = null;
        try {
            error = ConstraintChecker.getInstance().checkRetrieval(filters);
        } catch (DBManagementException e) {
            throw new RuntimeException(e);
        }
        String query = new QueryGenerator(new AttributeCollection(filters)).generateQuery();
        return handleDBOperation(error, query, false);
    }

    /**
     * Joins the tables containing the attributes in the given attribute
     * collection and retrieves all their rows. Only the attributes in the
     * collection are selected from the rows. Bear in mind that the tables
     * containing the attributes must be eligible for joining, otherwise an
     * exception is thrown.
     *
     * @param toGet Collection of attributes to be retrieved
     * @return QueryResult containing the result set of the retrieval operation
     * @throws SQLException If an error occurs while retrieving the data from
     * the DB.
     * @throws DBManagementException Print the message to know why the exception
     * was thrown
     */
    public QueryResult retrieve(AttributeCollection toGet) throws SQLException, DBManagementException {

        Errors error = null;
        try {
            error = ConstraintChecker.getInstance().checkRetrieval(toGet);
        } catch (DBManagementException e) {
            throw new RuntimeException(e);
        }
        String query = new QueryGenerator(toGet).generateQuery();
        return handleDBOperation(error, query, false);

    }

    /**
     * Joins the tables containing the attributes in the given attribute
     * collection and filters then retrieves rows that satisfy certain
     * conditions. Only the attributes in the collection are selected from the
     * rows. Bear in mind that the tables containing the attributes in the
     * attribute collection and filters must be eligible for joining, otherwise
     * an exception is thrown.
     *
     * @param toGet Attributes to be retrieved
     * @param filters Conditions that a row must satisfy to be part of the
     * retrieved set of rows
     * @return QueryResult containing the result set of the retrieval operation
     * @throws SQLException If an error occurs while retrieving the data from
     * the DB.
     * @throws DBManagementException Print the message to know why the exception
     * was thrown
     */
    public QueryResult retrieve(AttributeCollection toGet, Filters filters) throws SQLException, DBManagementException {
        Errors error = ConstraintChecker.getInstance().checkRetrieval(filters, toGet);

        String query = new QueryGenerator(toGet, filters).generateQuery();
        return handleDBOperation(error, query, false);
    }

    /**
     * Retrieves the maximum value of the given attribute in its table. The
     * result set in the QueryResult contains only one column and one row which
     * is the maximum value
     *
     * @param attribute Attribute whose maximum is to be retrieved
     * @return max value of the attribute
     */
    public QueryResult retrieveMax(Attribute attribute) throws SQLException {
        String query = "Select max(" + attribute.getAliasedStringName() + ") as MAX from " + attribute.getT().getAliasedName();
        return handleDBOperation(null, query, false);
    }

    /**
     * Retrieves the set of attributes in a given table
     *
     * @param t Table whose attributes are to be retrieved
     * @return An attributeCollection consisting of the attributes making up the
     * given table. The returned attribute have no values
     */
    public AttributeCollection getAttributes(Table t) {
        return MetaDataExtractor.getInstance().getAttributeCollection(t);
    }

    public Set<Table> getNeighbors(Table t) {
        Node table = new Node(t);
        Set<Node> neighboringNodes = Graph.getInstance().getNeighbors(table);
        Set<Table> tables = new HashSet<>();
        for (Node node : neighboringNodes) {
            tables.add(node.getTable());
        }
        return tables;
    }

    /**
     * Executes the given SQL statement. Only Select SQL statements allowed
     *
     * @param sqlStatement Statement to be executed
     * @return Result set of the executed statement
     * @throws SQLException If an error occurs while executing the SQL statement
     * in the DBMS
     */
    public ResultSet executeStatement(String sqlStatement) throws SQLException, NullPointerException {

        System.out.println(sqlStatement);
        if (conn == null) {
            throw new NullPointerException("Cant perform any database operations database because the connection was not established");
        }
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return stmt.executeQuery(sqlStatement);
    }

    /**
     * Executes the given SQL statement. Only Insert, Update, & Delete SQL
     * statements are allowed
     *
     * @param sqlPreparedStatement Statement to be executed
     * @return Number of rows affected by the SQL operation
     * @throws SQLException If an error occurs while executing SQL Prepared
     * Statement in the DBMS.
     */
    private int executePreparedStatement(String sqlPreparedStatement) throws SQLException, NullPointerException {
        System.out.println(sqlPreparedStatement);
        if (conn == null) {
            throw new NullPointerException("Cant perform any database operations database because the connection was not established");
        }
        PreparedStatement prep = conn.prepareStatement(sqlPreparedStatement);
        return prep.executeUpdate();
    }

    private QueryResult handleDBOperation(Errors error, String query, boolean isUpdate) throws SQLException {
        ResultSet rs = null;
        int rows = 0;
        if (error == null || error.noErrors()) {
            if (!isUpdate) {
                rs = executeStatement(query);
                rs.last();
                rows = rs.getRow();
                rs.beforeFirst();
            } else {
                rows = executePreparedStatement(query);
            }
        }
        return new QueryResult(rs, rows, error, query);
    }

}
