/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package General;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import GUI.LogInScreen;
import GUI.TableViewer;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JTable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
        Controller controller = new Controller();

        // CASE 1A
        {
            JFrame frame = null;
            String userID = "A1";
            String password = "A1";

            assertThrows(IllegalArgumentException.class, () -> {
                controller.login(frame, userID, password);
            });
        }
        // CASE 1B
        {
            JFrame frame = new LogInScreen();
            frame.setVisible(false);
            String userID = null;
            String password = "A1";

            assertThrows(IllegalArgumentException.class, () -> {
                controller.login(frame, userID, password);
            });
        }
        // CASE 1C
        {
            JFrame frame = new LogInScreen();
            frame.setVisible(false);
            String userID = "A1";
            String password = null;

            assertThrows(IllegalArgumentException.class, () -> {
                controller.login(frame, userID, password);
            });
        }
        // CASE 2A
        {
            JFrame frame = new LogInScreen();
            frame.setVisible(false);
            String userID = "A1AKJSDGFKASJGDSAGDFGSADKFJGSAKJFGSAKJSDGAGGSDFKJSDSAHDKSGADKJASKJDGAKSJFD";
            String password = "A1";

            assertTrue(controller.login(frame, userID, password).getRowsAffected() == 0);

        }

        // CASE 2B
        {
            JFrame frame = new LogInScreen();
            frame.setVisible(false);
            String userID = "A1";
            String password = "ASFSDFASDFSDFASDFASDFSAFASDFHJSDFLKAHSLDKJFHALSKDHFAKSLJDFHLAKJSFLKASDHFKALJSDHFLKJASHDKJFAHSKDJFHAKLSJDFL";

            assertTrue(controller.login(frame, userID, password).getRowsAffected() == 0);
        }
        // CASE 3A
        {
            JFrame frame = new LogInScreen();
            frame.setVisible(false);
            String userID = "ASDF";
            String password = "A1";

            assertTrue(controller.login(frame, userID, password).getRowsAffected() == 0);
        }
        // CASE 3B
        {
            JFrame frame = new LogInScreen();
            frame.setVisible(false);
            String userID = "A1";
            String password = "AASDFS1";

            assertTrue(controller.login(frame, userID, password).getRowsAffected() == 0);
        }
        // CASE 3C
        {
            JFrame frame = new LogInScreen();
            frame.setVisible(false);
            String userID = "ASDF";
            String password = "AASDF1";

            assertTrue(controller.login(frame, userID, password).getRowsAffected() == 0);
        }
        // CASE 4A
        {
            JFrame frame = new LogInScreen();
            frame.setVisible(false);
            String userID = "A2";
            String password = "A1";
            assertTrue(controller.login(frame, userID, password).getRowsAffected() == 0);
        }
        // CASE 5A
        {
            JFrame frame = new LogInScreen();
            frame.setVisible(false);
            String userID = "A1";
            String password = "A1";

            assertTrue(controller.login(frame, userID, password).getRowsAffected() > 0);
            frame.dispose();
        }
    }

    /**
     * Test of bookAppointment method, of class Controller.
     */
    @org.junit.Test
    public void testBookAppointment() throws Exception {
        Controller controller = new Controller();
        assertThrows(IllegalArgumentException.class,
                () -> {
                    controller.bookAppointment(
                            null);
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
