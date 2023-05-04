package General;

import DataEntryInterface.DataEntryUserInterface;
import DataEntryInterface.ModificationForm;
import DataEntryInterface.InsertForm;
import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.DatabaseManager;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import LeasingAgentInterface.AppointmentSlotForm;
import LeasingAgentInterface.LeasingAgentUserInterface;
import Notifications.Notification;
import Notifications.NotificationsManager;
import TableViewer.TableViewer;
import TenantInterface.PropertyBrowser;
import TenantInterface.TenantUserInterface;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class Controller {

    private static LoginUser loggedInUser;
    private static HashMap<String, JFrame> role_to_interface;

    public Controller() {

    }

    private DatabaseManager DB = DatabaseManager.getInstance();

    /**
     *******************************************************************************************************************
     *******************************************************LOGIN*******************************************************
     * *****************************************************************************************************************
     */
    /**
     * Tries to log in to the system using the given credentials. If the
     * credentials match a user, the GUI for that user type will be displayed to
     * the screen, else an error message
     *
     * @param loginForm The form displaying the login fields
     * @param username Input username
     * @param password Input password
     */
    public void login(JFrame loginForm, String username, String password) {
        try {
            password = PasswordManager.encrypt(password);
            Filters filters = new Filters();
            filters.addEqual(new Attribute(Name.USER_ID, username, Table.CREDENTIALS));
            filters.addEqual(new Attribute(Name.PASSWORD, password, Table.CREDENTIALS));
            QueryResult result = DB.retrieve(Table.CREDENTIALS, filters);

            if (result.getResult().next()) {
                loggedInUser = LoginUser.retrieve(username);
                displayWindow();
                loginForm.dispose();
            } else {
                displayErrors("Invalid login information");
            }
        } catch (DBManagementException ex) {
            displayErrors("Something went wrong while logging in");
        } catch (SQLException ex) {
            displaySQLError(ex);
        }
    }

    /**
     * Sets the current logged in user to the given user
     *
     * @param user
     */
    public void setLoggedInUser(LoginUser user) {
        loggedInUser = user;
    }

    /**
     *
     * @return Current logged in user's id
     */
    public String getUserID() {
        return loggedInUser.getUserID();
    }

    /**
     *
     * @return Current logged in user
     */
    public LoginUser getLoginUser() {
        return loggedInUser;
    }

    /**
     *******************************************************************************************************************
     ***********************************************ERROR/SUCCESS_MESSAGES**********************************************
     * *****************************************************************************************************************
     */
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
     ***********************************************APPOINTMENT_BOOKING**********************************************
     * *****************************************************************************************************************
     */
    /**
     * Allows the user to book an appointment after selecting a store from the
     * given viewer. A new window will appear prompting the user to choose the
     * appointment slot. Upon selecting the slot, the user will be asked for
     * confirmation and confirmed, the appointment will be created
     *
     * @param viewer TableViewer that contains
     * @throws SQLException
     * @throws DBManagementException
     */
    public void bookAppointment(TableViewer viewer) throws SQLException, DBManagementException {

        AttributeCollection storeInformation = viewer.getSelectedRow();
        String locationNum = findLocationNum(storeInformation);
        Store selectedStore = Store.retrieve(locationNum);

        TableViewer agentAvailability = new AgentAvailability().getAgentAvailability();

        agentAvailability.overrideClickListener(() -> {

            try {
                boolean confirmed = confirmAppointment();
                if (!confirmed) {
                    return;
                }
                AttributeCollection selectedTimeSlot = agentAvailability.getSelectedRow();

                String agentID = selectedTimeSlot.getValue(new Attribute(Name.AGENT_ID, Table.APPOINTMENT_SLOTS));
                LoginUser agent = LoginUser.retrieve(agentID);

                Appointment appointment = Appointment.create(loggedInUser, selectedStore, agent, selectedTimeSlot);

                if (appointment != null) {
                    displaySuccessMessage("Appointment Created Successfully...SUUUIIIIIII");
                    Notification notification = new Notification(getUserID(), LocalDateTime.now(), Notification.NotifTopic.APPOINTMENT_CREATED, "Below are the appointment details:\n" + appointment.toString());
                    new NotificationsManager().notifyUser(getUserID(), notification);
                } else {
                    displayErrors("Something went wrong wile creating appointment...Try again later");
                }
            } catch (SQLException ex) {
                displaySQLError(ex);
            } catch (DBManagementException ex) {
                displayErrors("Something went wrong wile creating appointment...Try again later");
            }

        });
    }

    /**
     * Displays a small pop up window prompting the user to confirm the
     * appointment
     *
     * @return True if the user confirmed, false otherwise
     */
    private boolean confirmAppointment() {
        int option = JOptionPane.showConfirmDialog(null, "Confirm appointment innit?", "Appointment Confirmation", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.NO_OPTION || option == JOptionPane.CLOSED_OPTION || option == JOptionPane.CLOSED_OPTION) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Displays a new form where a leasing agent can manage his/her
     * availability. The agent can insert, modify, delete, and filter
     * appointment slots using the form.
     */
    public void uploadAvailability() {
        try {
            AttributeCollection toShow = DB.getAttributes(Table.APPOINTMENT_SLOTS);
            toShow.remove(new Attribute(Name.BOOKED, Table.APPOINTMENT_SLOTS));
            toShow.remove(new Attribute(Name.AGENT_ID, Table.APPOINTMENT_SLOTS));

            Filters filters = new Filters();
            filters.addEqual(new Attribute(Name.AGENT_ID, getUserID(), Table.APPOINTMENT_SLOTS));
            filters.addEqual(new Attribute(Name.BOOKED, "0", Table.APPOINTMENT_SLOTS));
            TableViewer viewer = new TableViewer("APPOINTMENT SLOTS", toShow, filters, new AppointmentSlotForm(), false);
            viewer.setVisible(true);

        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Something went wrong while viewing Appointment Slots...Try again later");
        }
    }

    public void viewAgentAppointments() {
        try {

            ResultSet result = DB.executeStatement("Select H.SLOT_NUM , H.DAY , H.START_DATE , H.END_DATE,\n"
                    + "  M.FNAME AS TENANT_NAME , M.PHONE_NUMBER , M.EMAIL_ADDRESS, J.NAME AS STORE_NAME, L.NAME AS MALL_NAME \n"
                    + " from  USERS M \n"
                    + " join APPOINTMENT_SLOTS H on M.USER_ID = H.AGENT_ID\n"
                    + " join LEASES F on M.USER_ID = F.LEASER_ID\n"
                    + " join LOCS K on F.LOCATION_NUM = K.LOCATION_NUM \n"
                    + " join PROPERTIES J on K.LOCATION_NUM = J.LOCATION_NUM \n"
                    + " join MALLS L on K.MALL_NUM = L.MALL_NUM \n"
                    + " join APPOINTMENTS I on M.USER_ID = I.POTENTIAL_TENANT_ID AND I.APPOINTMENT_SLOT = H.SLOT_NUM\n"
                    + " where H.AGENT_ID = '" + getUserID()
                    + "'");
            TableViewer viewer = new TableViewer("APPOINTMENTS", result);
            viewer.setVisible(true);

        } catch (SQLException ex) {
            displaySQLError(ex);
        }

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
            viewer.overrideClickListener(() -> {
                try {
                    bookAppointment(viewer);
                } catch (SQLException ex) {
                    displaySQLError(ex);
                } catch (DBManagementException ex) {
                    displayErrors("Something went wrong while booking appointment");
                }

            });
            viewer.setVisible(true);

        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Something went wrong while browsing properties...Try again later");
        }

    }

    /**
     * Extracts a store's location number from the given attribute collection.
     *
     * @param collection Collection containing the mallName, mallNumber, and
     * storeNumber
     * @return
     */
    public String findLocationNum(AttributeCollection collection) {
        try {
            String mallName = collection.getAttribute(Table.MALLS, Name.NAME).getValue();
            Mall mall = Mall.retrieve(mallName);

            String mallNum = String.valueOf(mall.getMallNum());
            String storeNum = collection.getAttribute(Table.LOCS, Name.STORE_NUM).getValue();

            Filters filters = new Filters();
            filters.addEqual(new Attribute(Name.MALL_NUM, mallNum, Table.LOCS));
            filters.addEqual(new Attribute(Name.STORE_NUM, storeNum, Table.LOCS));

            QueryResult result = DB.retrieve(Table.LOCS, filters);
            if (result.noErrors()) {
                ResultSet store = result.getResult();
                store.next();
                return store.getString(Name.LOCATION_NUM.getName());
            }

        } catch (SQLException ex) {
            displaySQLError(ex);
        } catch (DBManagementException ex) {
            displayErrors("Something went wrong when getting store location number...Try again later");
        }
        return "";

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
     * @param quiet Specifies whether a message indicating the success of the
     * operation should be displayed on the screen or not
     *
     * @return Result of modification operation
     *
     */
    public QueryResult modify(Table t, AttributeCollection newValues, Filters filters, boolean quiet) {
        try {
            QueryResult result = DB.modify(t, filters, newValues, true);

            if (!result.noErrors()) {
                displayErrors(result);
            }

            if (quiet) {
                return result;
            }

            displaySuccessMessage("Entry Modified Successfully");

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
    public QueryResult insert(Table t, AttributeCollection newValues, boolean quiet) {
        try {
            QueryResult result = DB.insert(t, newValues);

            if (!result.noErrors()) {
                displayErrors(result);
            }

            if (quiet) {
                return result;
            }

            displaySuccessMessage("Entry Inserted Successfully");

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
            ex.printStackTrace();
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
    public QueryResult delete(Table t, Filters filters, boolean quiet) {
        try {
            QueryResult result = DB.delete(t, filters);

            if (!result.noErrors()) {
                displayErrors(result);
            }

            if (quiet) {
                return result;
            }

            displaySuccessMessage("Entry Deleted Successfully");
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

    private void displayWindow() {
        if (role_to_interface == null) {
            role_to_interface = new HashMap<>();
            role_to_interface.put("DE", new DataEntryUserInterface());
            role_to_interface.put("LA", new LeasingAgentUserInterface());
            role_to_interface.put("CT", new TenantUserInterface());
            role_to_interface.put("PT", new TenantUserInterface());
//        role_to_interface.put("IT",);
        }

        String role = loggedInUser.getRoleID();
        role_to_interface.get(role).setVisible(true);
    }

    AttributeCollection getAttributes(Table table) {
        return DB.getAttributes(table);
    }
}
