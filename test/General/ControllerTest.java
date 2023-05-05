/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package General;

import DataEntryInterface.InsertForm;
import DataEntryInterface.ModificationForm;
import DatabaseManagement.Attribute;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import GUI.TableViewer;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.swing.JFrame;
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

    /**
     * Test of browseProperties method, of class Controller.
     */
    @Test
    public void testBrowseProperties() {
//       TableViewer =
        Controller instance = new Controller();
        instance.browseProperties();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of GetSelectedStore method, of class Controller.
     */
    @Test
    public void testGetSelectedStore() {
        System.out.println("browseProperties");
        Controller instance = new Controller();
        instance.browseProperties();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of DisplayAgentAvailability method, of class Controller.
     */
    @Test
    public void testDisplayAgentAvailability() {
        System.out.println("browseProperties");
        Controller instance = new Controller();
        instance.browseProperties();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of GetAgentAvailability method, of class Controller.
     */
    @Test
    public void testGetAgentAvailability() {
        System.out.println("browseProperties");
        Controller instance = new Controller();
        instance.browseProperties();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ConfirmAppointment method, of class Controller.
     */
    @Test
    public void testConfirmAppointment() {
        System.out.println("browseProperties");
        Controller instance = new Controller();
        instance.browseProperties();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of DoubleClickingStore method, of class Controller.
     */
    @Test
    public void testDoubleClickingStore() {
        System.out.println("browseProperties");
        Controller instance = new Controller();
        instance.browseProperties();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of DoubleClickingTiming method, of class Controller.
     */
    @Test
    public void testDoubleClickingTiming() {
        System.out.println("browseProperties");
        Controller instance = new Controller();
        instance.browseProperties();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findLocationNum method, of class Controller.
     */
    @Test
    public void testFindLocationNum() {
        System.out.println("findLocationNum");
        AttributeCollection collection = null;
        Controller instance = new Controller();
        String expResult = "";
        String result = instance.findLocationNum(collection);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
