/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package General;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dell
 */
public class LoginUserTest {

    public LoginUserTest() {
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
     * Test of getFirstName method, of class LoginUser.
     */
    @Test
    public void testGetFirstName() {
        System.out.println("getFirstName");
        LoginUser instance = null;
        String expResult = "";
        String result = instance.getFirstName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLastName method, of class LoginUser.
     */
    @Test
    public void testGetLastName() {
        System.out.println("getLastName");
        LoginUser instance = null;
        String expResult = "";
        String result = instance.getLastName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPhoneNumber method, of class LoginUser.
     */
    @Test
    public void testGetPhoneNumber() {
        System.out.println("getPhoneNumber");
        LoginUser instance = null;
        String expResult = "";
        String result = instance.getPhoneNumber();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEmailAddress method, of class LoginUser.
     */
    @Test
    public void testGetEmailAddress() {
        System.out.println("getEmailAddress");
        LoginUser instance = null;
        String expResult = "";
        String result = instance.getEmailAddress();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRoleID method, of class LoginUser.
     */
    @Test
    public void testGetRoleID() {
        System.out.println("getRoleID");
        LoginUser instance = null;
        String expResult = "";
        String result = instance.getRoleID();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of retrieve method, of class LoginUser.
     */
    @Test
    public void testRetrieve() throws Exception {
        System.out.println("retrieve");
        String userID = "";
        LoginUser expResult = null;
        LoginUser result = LoginUser.retrieve(userID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUserID method, of class LoginUser.
     */
    @Test
    public void testGetUserID() {
        System.out.println("getUserID");
        LoginUser instance = null;
        String expResult = "";
        String result = instance.getUserID();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
