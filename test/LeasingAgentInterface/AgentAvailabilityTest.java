/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package LeasingAgentInterface;

import DatabaseManagement.Attribute;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.DBParameters;
import DatabaseManagement.Filters;
import DatabaseManagement.Table;
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
public class AgentAvailabilityTest {

    public AgentAvailabilityTest() {
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
     * Test of getAgentAvailability method, of class AgentAvailability.
     */
    @Test
    public void testGetAgentAvailability() throws Exception {
        AttributeCollection toShow = new AttributeCollection();

        toShow.add(new Attribute(Attribute.Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));
        toShow.add(new Attribute(Attribute.Name.DAY, Table.APPOINTMENT_SLOTS));
        toShow.add(new Attribute(Attribute.Name.START_DATE, Table.APPOINTMENT_SLOTS));
        toShow.add(new Attribute(Attribute.Name.END_DATE, Table.APPOINTMENT_SLOTS));
        toShow.add(new Attribute(Attribute.Name.AGENT_ID, Table.APPOINTMENT_SLOTS));
        toShow.add(new Attribute(Attribute.Name.FNAME, Table.USERS));
        toShow.add(new Attribute(Attribute.Name.PHONE_NUMBER, Table.USERS));
        toShow.add(new Attribute(Attribute.Name.EMAIL_ADDRESS, Table.USERS));

        Filters filters = new Filters();

        filters.addEqual(new Attribute(Attribute.Name.BOOKED, "0", Table.APPOINTMENT_SLOTS));

        AgentAvailability aa = new AgentAvailability();
        DBParameters params = aa.getAgentAvailability();

        assertEquals(params.getCollection(), toShow);
        assertEquals(params.getFilters(), filters);
    }

}
