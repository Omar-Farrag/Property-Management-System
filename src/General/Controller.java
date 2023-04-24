package General;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.DatabaseManager;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import Leases.Lease;
import LeasingAgentInterface.AppointmentSlot;
import Properties.Mall;
import Properties.Store;
import TableViewer.TableViewer;
import TenantInterface.PropertyBrowser;
import java.awt.Color;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {

    //Just testing a feature
    private static User loggedInUser;

    private DatabaseManager DB = DatabaseManager.getInstance();

    public void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public String getUserID() {
        return loggedInUser.getUserID();
    }

    /**
     * Displays all errors in a database operation in a separate dialog window
     *
     * @param result The returned QueryResult from the database operation
     */
    public void displayErrors(QueryResult result) {
        HashSet<String> errors = result.getAllErrors();
        String messageToDisplay = "";
        for (String error : errors) {
            messageToDisplay += "\u2022 " + error + "\n";
        }
        JTextArea text = new JTextArea(messageToDisplay);
//        javax.swing.JLabel label = new javax.swing.JLabel(messageToDisplay);
        text.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        Color bgColor = UIManager.getColor("OptionPane.background");
        text.setBackground(bgColor);
        JOptionPane pane = new JOptionPane(text, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog("ERROR");
        dialog.setAlwaysOnTop(true); // make the dialog always on top
        dialog.setVisible(true);
    }

    /**
     * Displays given error message in an error dialog box
     *
     * @param message error message to be displayed;
     */
    public void displayErrors(String message) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog("ERROR");
        dialog.setAlwaysOnTop(true); // make the dialog always on top
        dialog.setVisible(true);
    }

    /**
     * Displays the SQL error message whenever there is a SQL error
     */
    public void displaySQLError(SQLException e) {
        javax.swing.JLabel label = new javax.swing.JLabel("Something went wrong...Please Try Again Later");
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));

        JOptionPane pane = new JOptionPane(label, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog("ERROR");
        dialog.setAlwaysOnTop(true); // make the dialog always on top
        dialog.setVisible(true);

        e.printStackTrace();
    }

    /**
     * Displays a success message in a separate dialog window
     *
     * @param message Message to be displayed in window
     */
    public void displaySuccessMessage(String message) {
        javax.swing.JLabel label = new javax.swing.JLabel(message);
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        JOptionPane pane = new JOptionPane(label, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog("ERROR");
        dialog.setAlwaysOnTop(true); // make the dialog always on top
        dialog.setVisible(true);
    }

    /**
     *******************************************************************************************************************
     *******************************************************STORES******************************************************
     * *****************************************************************************************************************
     */
    /**
     * Insert in database a new store based on the input in the received form
     * collection
     *
     * @param form The form that called this function
     */
    public void insertStore(InsertForm form) {

        try {
            QueryResult result = Store.insert(form.getAttributes());
            if (result == null) {
                displayErrors("Store already exists");
            } else if (result.noErrors()) {
                displaySuccessMessage("Store created successfully!");
                form.resetFields();
            } else {
                displayErrors(result);
            }
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Modifies the given store in the database on the input in the received
     * form
     *
     * @param store store to be modified
     * @param form The form that called this function
     *
     */
    public void modifyStore(Store store, ModificationForm form) {
        try {
            QueryResult modificationResult = store.modify(form.getAttributes());
            if (modificationResult.noErrors()) {
                displaySuccessMessage("Store modified successfully");
            } else {
                displayErrors(modificationResult);
            }
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Could not modify the store");
        }
    }

    /**
     * Deletes the given store from database based on the input in the received
     * form
     *
     * @param store Store to be deleted
     * @param form The form that called this function
     */
    public void deleteStore(Store store, ModificationForm form) {
        try {
            QueryResult deletionResult = store.delete();
            if (!deletionResult.noErrors()) {
                displayErrors(deletionResult);
            } else {
                displaySuccessMessage("Store deleted successfully");
                form.resetFields();
            }
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Could not delete the store");
        }
    }

    /**
     * @return The list of classes used in the store classification system Right
     * now, it returns 'A','B','C', 'D'
     */
    public ArrayList<String> getClasses() {
        return Store.getClasses();
    }

    /**
     * @return The list of purposes that a store can be used for
     */
    public ArrayList<String> getPurposes() {
        return Store.getPurposes();
    }

    /**
     * Opens a new window displaying a list of all available stores for
     * browsing.
     */
    public void browseProperties() {
        try {
            AttributeCollection toShow = Store.getVisibleAttributes();
            Filters filters = new Filters();
            filters.addEqual(new Attribute(Name.STATUS, "Available", Table.PROPERTIES));
            TableViewer viewer = new TableViewer("Properties", toShow, filters, new PropertyBrowser(), true);
            viewer.setVisible(true);

        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Something went wrong while browsing properties...Try again later");
        }
    }

    /**
     *******************************************************************************************************************
     *******************************************************MALLS*******************************************************
     * *****************************************************************************************************************
     */
    /**
     * Insert in database a new mall based on the input in the received form
     * collection
     *
     * @param form The form that called this function
     */
    public void insertMall(InsertForm form) {
        try {
            QueryResult result = Mall.insert(form.getAttributes());
            if (result.noErrors()) {
                displaySuccessMessage("Mall created successfully!");
                form.resetFields();
            } else {
                displayErrors(result);
            }
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Modifies the given mall in the database based on the input in the
     * received form
     *
     * @param mall Mall to be modified
     * @param form The form that called this function
     */
    public void modifyMall(Mall mall, ModificationForm form) {
        try {
            QueryResult modificationResult = mall.modify(form.getAttributes());

            if (modificationResult.noErrors()) {
                displaySuccessMessage("Mall modified successfully");
                form.resetFields();
            } else {
                displayErrors(modificationResult);
            }

        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Could not modify the mall");
        }
    }

    /**
     * Deletes the given mall from the database based on the input in the
     * received form
     *
     * @param mall Mall to be deleted
     * @param form The form that called this function
     */
    public void deleteMall(Mall mall, ModificationForm form) {
        try {
            QueryResult deletionResult = mall.delete();
            if (!deletionResult.noErrors()) {
                displayErrors(deletionResult);
            } else {
                displaySuccessMessage("Mall deleted successfully");
                form.resetFields();
            }
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Could not delete the mall");
        }

    }

    /**
     * Gets the list of stores in the given mall
     *
     * @param mallName name of mall whose stores are to be retrieved
     * @return list of stores in mall [mallName]
     */
    public ArrayList<Store> getListOfStores(String mallName) {
        try {
            Mall mall = Mall.retrieve(mallName);
            return mall.getStores();
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Could not get list of stores in mall " + mallName);
        }
        return new ArrayList<Store>();
    }

    /**
     * @return List of all mall names in the database
     */
    public ArrayList<Mall> getListOfMalls() {
        try {
            return Mall.getListOfMalls();
        } catch (SQLException ex) {
            displaySQLError(ex);
        }
        return new ArrayList<>();
    }

    /**
     * Returns mall with the given mallName
     *
     * @param mallName Name of the mall to be retrieved
     * @return the mall itself with all its attributes initialized as per
     * database. Returns null if no such mall is found
     */
    public Mall getMall(String mallName) {
        try {
            return Mall.retrieve(mallName);
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Could not retrieve mall " + mallName);
        }
        return null;
    }

    /**
     ********************************************************************************************************************
     *******************************************************LEASES*******************************************************
     * ******************************************************************************************************************
     */
    public void viewLeases() {
        try {
            AttributeCollection toShow = Lease.getVisibleAttributes();
            Filters filters = new Filters();
            filters.addEqual(new Attribute(Name.LEASER_ID, loggedInUser.getUserID(), Table.LEASES));
            TableViewer viewer = new TableViewer("LEASES", toShow, filters, null, true);
            viewer.setVisible(true);
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Something went wrong while viewing leasess...Try again later");
        }
    }

    /**
     ********************************************************************************************************************
     ****************************************************LEASING_AGENT*************************************************
     * ******************************************************************************************************************
     */
    public void uploadAvailability() {
        try {
            AttributeCollection toShow = DB.getAttributes(Table.APPOINTMENT_SLOTS);
            Filters filters = new Filters();
            filters.addEqual(new Attribute(Name.AGENT_ID, "A1", Table.APPOINTMENT_SLOTS));
            TableViewer viewer = new TableViewer("APPOINTMENT SLOTS", toShow, filters, new AppointmentSlot(), false);
            viewer.setVisible(true);
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Something went wrong while viewing leasess...Try again later");
        }
    }

    public void viewAgentAppointments() {

    }

    /**
     ********************************************************************************************************************
     ****************************************************DATABASE_ACCESS*************************************************
     * ******************************************************************************************************************
     */
    /**
     * Updates the values of certain rows in the given table
     *
     * @param t Table whose rows are to be modified
     * @param filters Conditions that a row must satisfy to be updated
     * @param newValues Attribute collection containing the attributes to be
     * modified and their new values
     *
     * @return Result of modification operation
     *
     */
    public QueryResult modify(Table t, AttributeCollection newValues, Filters filters) {
        try {
            QueryResult result = DB.modify(t, filters, newValues, true);
            if (result.noErrors()) {
                displaySuccessMessage("Entry Modified Successfully!");
            } else {
                displayErrors(result);
            }
            return result;
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            ex.printStackTrace();
            displayErrors("Something went wrong while modifying " + t.getTableName().toUpperCase());
        }
        return null;
    }

    /**
     * Inserts the given attribute collection to the given table. The attributes
     * in the collection must match the attributes in the table, but the order
     * is irrelevant. If you want to set one of the attributes in the table to
     * null, then set the value of that attribute in the attribute collection to
     * null, pass an empty string to the attribute's value.
     *
     * @param newValues list of attributes forming the tuple to be inserted
     * @param t Table where the tuple will be inserted
     * @return The result of the insertion operation
     */
    public QueryResult insert(Table t, AttributeCollection newValues) {
        try {
            QueryResult result = DB.insert(t, newValues);
            if (result.noErrors()) {
                displaySuccessMessage("Entry Inserted Successfully!");
            } else {
                displayErrors(result);
            }
            return result;
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Something went wrong while inserting an entry into  " + t.getTableName().toUpperCase());
        }
        return null;
    }

    /**
     * Joins the tables containing the attributes in the given attribute
     * collection and filters then retrieves rows that satisfy certain
     * conditions. Only the attributes in the collection are selected from the
     * rows. Bear in mind that the tables containing the attributes in the
     * attribute collection and filters must be eligible for joining, otherwise
     * an exception is thrown.
     *
     * @param toShow Attributes to be retrieved
     * @param filters Conditions that a row must satisfy to be part of the
     * retrieved set of rows
     * @return QueryResult containing the result set of the retrieval operation
     */
    public QueryResult retrieve(AttributeCollection toShow, Filters filters) {
        try {
            QueryResult result = DB.retrieve(toShow, filters);
            if (!result.noErrors()) {
                displayErrors(result);
            }

            return result;
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Something went wrong while retrieving data from database");
        }
        return null;
    }

    /**
     * Retrieves specific rows from a given table
     *
     * @param t Table containing the rows to be retrieved
     * @param filters Conditions that a row must satisfy to be part of the
     * retrieved set of rows
     * @return QueryResult containing a result set of the retrieved rows
     *
     */
    public QueryResult retrieve(Table t, Filters filters) {
        try {
            QueryResult result = DB.retrieve(t, filters);
            if (!result.noErrors()) {
                displayErrors(result);
            }

            return result;
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Something went wrong while retrieving data from database");
        }
        return null;
    }

    /**
     * Deletes rows in the given table that satisfy all the given filters
     * provided these rows are not referenced by other rows. Passing an empty
     * filters object will delete the whole table.
     *
     * @param t Table whose entries are to be deleted.
     * @param filters Conditions that a row must satisfy to be deleted
     * @return Query result of the delete operation
     */
    public QueryResult delete(Table t, Filters filters) {
        try {
            QueryResult result = DB.delete(t, filters);
            if (result.noErrors()) {
                displaySuccessMessage("Entry Deleted Successfully!");
            } else {
                displayErrors(result);
            }
            return result;
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Something went wrong while deleting an entry from " + t.getTableName().toUpperCase());
        }
        return null;
    }

    /**
     * Retrieves all rows from a specific table
     *
     * @param t Table whose rows are to be retrieved
     * @return QueryResult containing the result set of the retrieved table
     *
     */
    public QueryResult retrieve(Table t) {
        try {
            QueryResult result = DB.retrieve(t);
            if (!result.noErrors()) {
                displayErrors(result);
            }
            return result;
        } catch (SQLException ex) {
            displaySQLError(ex);
        }
        return null;
    }

    /**
     * Retrieves all rows from the tables containing the attributes in the given
     * collection. Only the attribute values are retrieved
     *
     * @param toGet Table whose rows are to be retrieved
     * @return QueryResult containing the result set of the retrieved e
     */
    public QueryResult retrieve(AttributeCollection toGet) {
        try {
            QueryResult result = DB.retrieve(toGet);
            if (!result.noErrors()) {
                displayErrors(result);
            }
            return result;
        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Something went wrong while retrieving data from database");
        }
        return null;
    }

    /**
     * Retrieves the max value of the given attribute in its table in the
     * database
     *
     * @param attribute
     * @return max value of attribute
     */
    public QueryResult retrieveMax(Attribute attribute) {
        try {

            QueryResult result = DB.retrieveMax(attribute);
            if (!result.noErrors()) {
                displayErrors(result);
            }
            return result;
        } catch (SQLException ex) {
            displaySQLError(ex);
        }
        return null;
    }

    /**
     * Converts a given date to SQL Developer timestamp format
     *
     * @param date Date in format "DD-MMM-YYYY HH:MM:SS a"
     * @return timestamp format of the date;
     */
    public String getTimestamp(String date) {
        String formatted = "";
        try {
            String statement = "SELECT TO_TIMESTAMP(?, 'DD-MON-YY HH:MI:SS.FF9 AM') FROM DUAL";
            statement = statement.replaceFirst("\\?", "'" + date + "'");
            ResultSet rs = DatabaseManager.getInstance().executeStatement(statement);
            rs.next();
            Timestamp stamp = rs.getTimestamp(1);
            formatted = formatTimeStamp(stamp);
            System.out.println(stamp.toString());
        } catch (SQLException ex) {
//            displaySQLError(ex);
        }
        return formatted;
    }

    public String formatTimeStamp(Timestamp stamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy hh:mm:ss.SSSSSSSSS a");
        return formatter.format(stamp);
    }
}
