/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package General;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.DatabaseManager;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.sql.ResultSet;
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
     * Test of retrieve method, of class LoginUser.
     */
    @Test
    public void testRetrieve() throws Exception {
        String userID = "";
        Controller controller = new Controller();

        // CASE 1A
        userID = "alksdjflkasjdf;ljsadf;lja;sldkfjas;lkdfja;slkdfj;laksdfj;klasdf";
        assertEquals(LoginUser.retrieve(userID), null);

        // CASE 2A
        userID = null;
        assertEquals(LoginUser.retrieve(userID), null);

        // CASE 2B
        userID = "";
        assertEquals(LoginUser.retrieve(userID), null);

        // CASE 3A
        userID = "A222";
        Filters filter1 = new Filters();
        filter1.addEqual(new Attribute(Name.USER_ID, userID, Table.USERS));
        QueryResult retrievalResult = controller.retrieve(Table.USERS, filter1);

        if (retrievalResult.getRowsAffected() > 0) {
            throw new Exception("CASE 3A FAILED BECAUSE USER ALREADY EXISTS");

        }
        assertEquals(LoginUser.retrieve(userID), null);

        // CASE 4A
        userID = "A55";
        AttributeCollection collection = new AttributeCollection();
        String role_id = "CT";
        String phone_number = "0509998888";
        String email_address = "Doe@RealEstate.edu";
        String fname = "John";
        String lname = "Doe";

        collection.add(new Attribute(Name.USER_ID, userID, Table.USERS));
        collection.add(new Attribute(Name.ROLE_ID, role_id, Table.USERS));
        collection.add(new Attribute(Name.PHONE_NUMBER, phone_number, Table.USERS));
        collection.add(new Attribute(Name.EMAIL_ADDRESS, email_address, Table.USERS));
        collection.add(new Attribute(Name.FNAME, fname, Table.USERS));
        collection.add(new Attribute(Name.LNAME, lname, Table.USERS));

        QueryResult insertionResult = controller.insert(Table.USERS, collection, true);
        if (!insertionResult.noErrors()) {
            throw new Exception("CASE 4A FAILED DUE TO FAILED INSERTION OF NEW USER");
        }
        LoginUser user = LoginUser.retrieve(userID);

        assertNotNull(user);
        assertEquals(user.getUserID(), userID);
        assertEquals(user.getRoleID(), role_id);
        assertEquals(user.getPhoneNumber(), phone_number);
        assertEquals(user.getEmailAddress(), email_address);
        assertEquals(user.getFirstName(), fname);
        assertEquals(user.getLastName(), lname);

        Filters filters = new Filters();
        filters.addEqual(new Attribute(Name.USER_ID, userID, Table.USERS));
        controller.delete(Table.USERS, filters, true);

    }

}
