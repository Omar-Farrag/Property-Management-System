package General;

import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import Properties.Mall;
import Properties.Store;
import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class Controller {

    /**
     * @return List of all mall names in the database
     */
    public ArrayList<String> getListOfMalls() {
        return Mall.getListOfMalls();
    }

    /**
     * Returns the number of floors in a given mall
     *
     * @param mallName Name of the malls whose floors are to be retrieved
     * @return number of floors in the mall
     */
    public int getFloors(String mallName) {
        return Mall.getFloors(mallName);
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
        JOptionPane.showMessageDialog(null, text, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays given error message in an error dialog box
     *
     * @param message error message to be displayed;
     */
    public void displayErrors(String message) {
        javax.swing.JLabel label = new javax.swing.JLabel(message);
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        JOptionPane.showMessageDialog(null, label, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    public void displaySQLError() {
        javax.swing.JLabel label = new javax.swing.JLabel("Something went wrong...Please Try Again Later");
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        JOptionPane.showMessageDialog(null, label, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays a success message in a separate dialog window
     *
     * @param message Message to be displayed in window
     */
    public void displaySuccessMessage(String message) {
        javax.swing.JLabel label = new javax.swing.JLabel(message);
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        JOptionPane.showMessageDialog(null, label, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

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
                form.clearFields();
            } else {
                displayErrors(result);
            }
        } catch (SQLException ex) {
            displaySQLError();
        } catch (DBManagementException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Modifies a store in the database based on the input in the received form
     *
     * @param form The form that called this function
     *
     */
    public void modifyStore(ModificationForm form) {

    }

    /**
     * Deletes a store from database based on the input in the received form
     *
     * @param form The form that called this function
     */
    public void deleteStore(ModificationForm form) {

    }

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
                form.clearFields();
            } else {
                displayErrors(result);
            }
        } catch (SQLException ex) {
            displaySQLError();
        } catch (DBManagementException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Modifies a mall in the database based on the input in the received form
     *
     * @param form The form that called this function
     */
    public void modifyMall(ModificationForm form) {

    }

    /**
     * Deletes a mall from database based on the input in the received form
     *
     * @param form The form that called this function
     */
    public void deleteMall(ModificationForm form) {

    }

}
