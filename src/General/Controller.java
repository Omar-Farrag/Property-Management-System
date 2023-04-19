package General;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import Properties.Mall;
import Properties.Store;
import TableViewer.PropertyBrowsingFilters;
import TableViewer.TableViewer;
import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class Controller {

    /**
     * Displays all errors in a database operation in a separate dialog window
     *
     * @param result The returned QueryResult from the database operation
     */
    public void displayErrors(QueryResult result) {
        ArrayList<String> errors = result.getAllErrors();
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
     * ******************************************************************************************************************
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
            AttributeCollection toShow = Store.getBrowsingAttributes();
            TableViewer viewer = new TableViewer("Properties", toShow, new PropertyBrowsingFilters());
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
     * ******************************************************************************************************************
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

}
