/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package LeasingAgentInterface;

import DataEntryInterface.Store;
import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Exceptions.MissingAttributeException;
import DatabaseManagement.Table;
import General.LoginUser;
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
public class AppointmentTest {

    public AppointmentTest() {
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
     * Test of create method, of class Appointment.
     */
    @Test
    public void testCreate() throws Exception {
        LoginUser tenant = LoginUser.retrieve("A1");
        LoginUser agent = LoginUser.retrieve("A2");
        Store store = Store.retrieve("5");

        {
            AttributeCollection appointmentInfo = new AttributeCollection();
            appointmentInfo.add(new Attribute(Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.START_DATE, Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.END_DATE, Table.APPOINTMENT_SLOTS));

            assertThrows(IllegalArgumentException.class, () -> {
                Appointment.create(null, store, agent, appointmentInfo);
            });
        }
        {
            AttributeCollection appointmentInfo = new AttributeCollection();
            appointmentInfo.add(new Attribute(Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.START_DATE, Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.END_DATE, Table.APPOINTMENT_SLOTS));

            assertThrows(IllegalArgumentException.class, () -> {
                Appointment.create(tenant, null, agent, appointmentInfo);
            });
        }
        {
            AttributeCollection appointmentInfo = new AttributeCollection();
            appointmentInfo.add(new Attribute(Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.START_DATE, Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.END_DATE, Table.APPOINTMENT_SLOTS));

            assertThrows(IllegalArgumentException.class, () -> {
                Appointment.create(tenant, store, null, appointmentInfo);
            });
        }
        {
            AttributeCollection appointmentInfo = new AttributeCollection();
            appointmentInfo.add(new Attribute(Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.START_DATE, Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.END_DATE, Table.APPOINTMENT_SLOTS));

            assertThrows(IllegalArgumentException.class, () -> {
                Appointment.create(tenant, store, agent, null);
            });
        }

        {
            AttributeCollection appointmentInfo = new AttributeCollection();
            appointmentInfo.add(new Attribute(Name.START_DATE, Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.END_DATE, Table.APPOINTMENT_SLOTS));

            assertThrows(MissingAttributeException.class, () -> {
                Appointment.create(tenant, store, agent, appointmentInfo);
            });
        }
        {
            AttributeCollection appointmentInfo = new AttributeCollection();
            appointmentInfo.add(new Attribute(Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.END_DATE, Table.APPOINTMENT_SLOTS));

            assertThrows(MissingAttributeException.class, () -> {
                Appointment.create(tenant, store, agent, appointmentInfo);
            });
        }
        {
            AttributeCollection appointmentInfo = new AttributeCollection();
            appointmentInfo.add(new Attribute(Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.START_DATE, Table.APPOINTMENT_SLOTS));

            assertThrows(MissingAttributeException.class, () -> {
                Appointment.create(tenant, store, agent, appointmentInfo);
            });
        }
        {
            AttributeCollection appointmentInfo = new AttributeCollection();
            appointmentInfo.add(new Attribute(Name.END_DATE, Table.APPOINTMENT_SLOTS));

            assertThrows(MissingAttributeException.class, () -> {
                Appointment.create(tenant, store, agent, appointmentInfo);
            });
        }
        {
            AttributeCollection appointmentInfo = new AttributeCollection();
            appointmentInfo.add(new Attribute(Name.START_DATE, Table.APPOINTMENT_SLOTS));

            assertThrows(MissingAttributeException.class, () -> {
                Appointment.create(tenant, store, agent, appointmentInfo);
            });
        }
        {
            AttributeCollection appointmentInfo = new AttributeCollection();
            appointmentInfo.add(new Attribute(Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));

            assertThrows(MissingAttributeException.class, () -> {
                Appointment.create(tenant, store, agent, appointmentInfo);
            });
        }
        {
            AttributeCollection appointmentInfo = new AttributeCollection();

            assertThrows(MissingAttributeException.class, () -> {
                Appointment.create(tenant, store, agent, appointmentInfo);
            });
        }
        {
            AttributeCollection appointmentInfo = new AttributeCollection();
            appointmentInfo.add(new Attribute(Name.SLOT_NUM, "A323ASDFAS5", Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.START_DATE, "02-MAY-2023", Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.END_DATE, "02-MAY-2024", Table.APPOINTMENT_SLOTS));

            assertNull(Appointment.create(tenant, store, agent, appointmentInfo));
        }
        {
            AttributeCollection appointmentInfo = new AttributeCollection();
            appointmentInfo.add(new Attribute(Name.SLOT_NUM, "5", Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.START_DATE, "02-MAY-2023", Table.APPOINTMENT_SLOTS));
            appointmentInfo.add(new Attribute(Name.END_DATE, "02-MAY-2024", Table.APPOINTMENT_SLOTS));

            assertNotNull(Appointment.create(tenant, store, agent, appointmentInfo));
        }

    }
}
