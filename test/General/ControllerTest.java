/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package General;

import DataEntryInterface.InsertForm;
import DataEntryInterface.Mall;
import DataEntryInterface.ModificationForm;
import DataEntryInterface.Store;
import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Exceptions.MissingAttributeException;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import GUI.TableViewer;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Dell
 */
public class ControllerTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of login method, of class Controller.
     */
    @org.junit.Test
    public void testLogin() {
//        System.out.println("login");
//        JFrame loginForm = null;
//        String username = "";
//        String password = "";
//        Controller instance = new Controller();
//        instance.login(loginForm, username, password);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of bookAppointment method, of class Controller.
     */
    @org.junit.Test
    public void testBookAppointment() throws Exception {
        Controller controller = new Controller();
        assertThrows(IllegalArgumentException.class, () -> {
            controller.bookAppointment(null);
        });

        try {
            controller.setLoggedInUser(LoginUser.retrieve("A6"));
        } catch (SQLException ex) {
            controller.displaySQLError(ex);
        }
        controller.executeStatement("insert into appointment_slots VALUES ('A4','05-APR-27 04.02.00.000000000 PM',	'05-APR-27 04.04.00.000000000 PM',	'FRIDAY',	83,0)");

        TableViewer propertyViewer = controller.browseProperties();

        JTable table1 = propertyViewer.getTable();
        table1.setRowSelectionInterval(0, 0);

        TableViewer availabilityViewer = controller.bookAppointment(propertyViewer);

        JTable table = availabilityViewer.getTable();

        table.setRowSelectionInterval(0, 0);
        Rectangle cellRect = table.getCellRect(0, 0, false); // get the rectangle that bounds the cell
        MouseEvent doubleClick = new MouseEvent(table, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), MouseEvent.BUTTON1, cellRect.x + 5, cellRect.y + 5, 2, false, MouseEvent.BUTTON1);

        String slotNum = availabilityViewer.getSelectedRow().getValue(new Attribute(Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));

        Filters filters1 = new Filters();
        filters1.addEqual(new Attribute(Name.APPOINTMENT_SLOT, slotNum, Table.APPOINTMENTS));

        Filters filters2 = new Filters();
        filters2.addEqual(new Attribute(Name.SLOT_NUM, slotNum, Table.APPOINTMENT_SLOTS));

        table.dispatchEvent(doubleClick);

        QueryResult result = controller.retrieve(Table.APPOINTMENTS, filters1);
        assertTrue(result.getRowsAffected() > 0);

        controller.delete(Table.APPOINTMENTS, filters1, true);
        controller.delete(Table.APPOINTMENT_SLOTS, filters2, true);
    }

}
