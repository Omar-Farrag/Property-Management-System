/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package General;

import GUI.LogInScreen;
import DatabaseManagement.ConstraintsHandling.ConstraintChecker;
import DatabaseManagement.DatabaseManager;

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
        (new LogInScreen()).setVisible(true);

    }

}
