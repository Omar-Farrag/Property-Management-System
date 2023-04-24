/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package General;

import DataEntryInterface.DataEntryUserInterface;
import DatabaseManagement.ConstraintsHandling.ConstraintChecker;
import DatabaseManagement.ConstraintsHandling.MetaDataExtractor;
import DatabaseManagement.DatabaseManager;
import LeasingAgentInterface.LeasingAgentUserInterface;
import TenantInterface.TenantUserInterface;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Layth (edu)
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            DatabaseManager.getInstance();
            ConstraintChecker.getInstance();
            Controller controller = new Controller();
            controller.setLoggedInUser(LoginUser.retrieve("A1"));
//        (new LogInScreen()).setVisible(true);
//        (new GeneralUserInterface()).setVisible(true);
            new TenantUserInterface().setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
