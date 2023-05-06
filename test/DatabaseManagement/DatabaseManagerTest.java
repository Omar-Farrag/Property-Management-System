/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package DatabaseManagement;

import DatabaseManagement.Exceptions.AttributeNotFoundException;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Exceptions.InsufficientAttributesException;
import DatabaseManagement.Exceptions.TableNotFoundException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author Dell
 */
public class DatabaseManagerTest {

    public DatabaseManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of insert method, of class DatabaseManager.
     */
    @Test
    public void testInsert() {
        //CASE 1A
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = null;
            DatabaseManager.getInstance().insert(t, toInsert);
            fail("PASSED WHEN IT SHOULD HAVE THROWN ILLEGAL ARGUMENT EXCEPTION");

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        //CASE 1B
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(null);
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            DatabaseManager.getInstance().insert(t, toInsert);
            fail("PASSED WHEN IT SHOULD HAVE THROWN ILLEGAL ARGUMENT EXCEPTION");

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        //CASE 1E
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(null, "A5", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            DatabaseManager.getInstance().insert(t, toInsert);
            fail("PASSED WHEN IT SHOULD HAVE THROWN ILLEGAL ARGUMENT EXCEPTION");

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        //CASE 1G
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", null));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            DatabaseManager.getInstance().insert(t, toInsert);
            fail("PASSED WHEN IT SHOULD HAVE THROWN ILLEGAL ARGUMENT EXCEPTION");

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        //CASE 1H
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, null, null));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            DatabaseManager.getInstance().insert(t, toInsert);
            fail("PASSED WHEN IT SHOULD HAVE THROWN ILLEGAL ARGUMENT EXCEPTION");

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        //CASE 1I
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(null, "A5", null));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            DatabaseManager.getInstance().insert(t, toInsert);
            fail("PASSED WHEN IT SHOULD HAVE THROWN ILLEGAL ARGUMENT EXCEPTION");

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        //CASE 1J
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(null, null, t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            DatabaseManager.getInstance().insert(t, toInsert);
            fail("PASSED WHEN IT SHOULD HAVE THROWN ILLEGAL ARGUMENT EXCEPTION");

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        //CASE 1K
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(null, null, null));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            DatabaseManager.getInstance().insert(t, toInsert);
            fail("PASSED WHEN IT SHOULD HAVE THROWN ILLEGAL ARGUMENT EXCEPTION");

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        // CASE 2B
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "B2", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);
            assertFalse(result.noErrors());

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentExceotion Thrown");
        }
        // CASE 2C
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "-2", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);
            assertFalse(result.noErrors());

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentExceotion Thrown");
        }
        // CASE 2D
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "B5", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "-5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);
            assertFalse(result.noErrors());

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentExceotion Thrown");
        }
        // CASE 2E
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "B5", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "-6", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);
            assertFalse(result.noErrors());

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentExceotion Thrown");
        }
        // CASE 2F
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "-5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "-6", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);
            assertFalse(result.noErrors());

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentExceotion Thrown");
        }
        // CASE 2G
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "B5", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "-5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "-6", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);
            assertFalse(result.noErrors());

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentExceotion Thrown");
        }
        // CASE 3A
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID,
                    "GHFJFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);
            assertFalse(result.noErrors());

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentExceotion Thrown");
        }
        // CASE 3B
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "SDS", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);
            assertFalse(result.noErrors());

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentExceotion Thrown");
        }
        // CASE 3C
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "SDF", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);
            assertFalse(result.noErrors());

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentExceotion Thrown");
        }
        // CASE 3D
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID,
                    "GHFJFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "SDF5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);
            assertFalse(result.noErrors());

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentExceotion Thrown");
        }
        // CASE 3E
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID,
                    "GHFJFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6WWQ", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);
            assertFalse(result.noErrors());

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentExceotion Thrown");
        }
        // CASE 3F
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5SD23DS", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6WWQ", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);
            assertFalse(result.noErrors());

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentExceotion Thrown");
        }
        // CASE 3G
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID,
                    "ASDFSDFESDFASDFASE FAECASDFCASFASDFASERCAERSVHFDSDSSSDVGHJKKJGGDSSG VASCASDACCAS5", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5SD23DS", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6WWQ", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);
            assertFalse(result.noErrors());

        } catch (SQLException e) {
            fail("SQL Exception Thrown");
        } catch (DBManagementException e) {
            fail("DB Management Exception Thrown");
        } catch (IllegalArgumentException e) {
            fail("IllegalArgumentExceotion Thrown");
        }
        // CASE 4A
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);

        } catch (Exception e) {
            assertTrue(true);
        }
        // CASE 4B
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);

        } catch (Exception e) {
            assertTrue(true);
        }
        // CASE 4C
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);

        } catch (Exception e) {
            assertTrue(true);
        }
        // CASE 4D
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);

        } catch (Exception e) {
            assertTrue(true);
        }
        // CASE 4E
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);

        } catch (Exception e) {
            assertTrue(true);
        }
        // CASE 4F
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);

        } catch (Exception e) {
            assertTrue(true);
        }
        // CASE 4G
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);

        } catch (Exception e) {
            assertTrue(true);
        }
        // CASE 5A
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.PASSWORD, "A5", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);

        } catch (Exception e) {
            assertTrue(true);
        }
        // CASE 5B
        try {
            Table t = Table.MY_ROLES;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);

        } catch (Exception e) {
            assertTrue(true);
        }
        // CASE 6A
        try {
            Table t = Table.APPOINTMENTS;
            AttributeCollection toInsert = new AttributeCollection();
            toInsert.add(new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t));
            toInsert.add(new Attribute(Attribute.Name.LOCATION_NUM, "5", t));
            toInsert.add(new Attribute(Attribute.Name.APPOINTMENT_SLOT, "6", t));

            QueryResult result = DatabaseManager.getInstance().insert(t, toInsert);
            assertTrue(result.noErrors());
            assertTrue(result.getRowsAffected() == 1);

        } catch (Exception e) {
            fail("Threw an exception when it shouldn't");
        }

    }

}

//    @Test
//    public void testModify() throws Exception {
//        System.out.println("modify");
//        Table t = null;
//        Filters filters = null;
//        AttributeCollection toModify = null;
//        boolean cascade = false;
//        DatabaseManager instance = null;
//        QueryResult expResult = null;
//        QueryResult result = instance.modify(t, filters, toModify, cascade);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of retrieve method, of class DatabaseManager.
//     */
//    @Test
//    public void testRetrieve_Table() throws Exception {
//        System.out.println("retrieve");
//        Table t = null;
//        DatabaseManager instance = null;
//        QueryResult expResult = null;
//        QueryResult result = instance.retrieve(t);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of retrieve method, of class DatabaseManager.
//     */
//    @Test
//    public void testRetrieve_Table_Filters() throws Exception {
//        System.out.println("retrieve");
//        Table t = null;
//        Filters filters = null;
//        DatabaseManager instance = null;
//        QueryResult expResult = null;
//        QueryResult result = instance.retrieve(t, filters);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
/**
 * Test of retrieve method, of class DatabaseManager.
 */
//    @Test
//    public void testRetrieve_AttributeCollection_Filters() throws Exception {
//        System.out.println("retrieve");
//        AttributeCollection toGet = null;
//        Filters filters = null;
//        DatabaseManager instance = null;
//        QueryResult expResult = null;
//        QueryResult result = instance.retrieve(toGet, filters);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of executeStatement method, of class DatabaseManager.
//     */
//    @Test
//    public void testExecuteStatement() throws Exception {
//        System.out.println("executeStatement");
//        String sqlStatement = "";
//        DatabaseManager instance = null;
//        ResultSet expResult = null;
//        ResultSet result = instance.executeStatement(sqlStatement);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
