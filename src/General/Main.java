/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package General;

import DataEntryInterface.DataEntryUserInterface;
import DatabaseManagement.DatabaseManager;
import TenantInterface.TenantUserInterface;

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
//        (new LogInScreen()).setVisible(true);
//        (new GeneralUserInterface()).setVisible(true);
        new TenantUserInterface().setVisible(true);

    }

}
