/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package General;

import DataEntryInterface.DataEntryUserInterface;
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
        System.out.println("\u001B[31m" + "Hello, world!" + "\u001B[0m");

        DatabaseManager.getInstance();
//        (new LogInScreen()).setVisible(true);
//        (new GeneralUserInterface()).setVisible(true);

        (new DataEntryUserInterface()).setVisible(true);
    }

}
