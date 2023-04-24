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
        DatabaseManager.getInstance();
        ConstraintChecker.getInstance();
        Controller controller = new Controller();
        (new LogInScreen()).setVisible(true);

    }

}
