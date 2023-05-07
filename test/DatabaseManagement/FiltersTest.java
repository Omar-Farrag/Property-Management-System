/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package DatabaseManagement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Dell
 */
public class FiltersTest {

    public FiltersTest() {
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
     * Test of addEqual method, of class Filters.
     */
    @Test
    public void testAddEqual() {
        //Case 1A
        Filters instance = new Filters();
        assertThrows(IllegalArgumentException.class, () -> {
            instance.addEqual(null);
        });

        //Case 2A
        Attribute attribute = new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "", Table.APPOINTMENTS);
        instance.addEqual(attribute);
        Set<Attribute> filterAttributes = instance.getAttributes();
        assertTrue(filterAttributes.contains(attribute));
    }

    /**
     * Test of getAttributes method, of class Filters.
     */
    @Test
    public void testGetAttributes() {
        // Case 1A
        {
            Table t = Table.APPOINTMENTS;
            Filters filter = new Filters();

            Attribute att1 = new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t);
            Attribute att2 = new Attribute(Attribute.Name.LOCATION_NUM, "5", t);
            Attribute att3 = new Attribute(Attribute.Name.APPOINTMENT_SLOT, "SDF", t);

            filter.addEqual(att1);
            filter.addEqual(att2);
            filter.addEqual(att3);

            Set<Attribute> atts = new HashSet<>();

            atts.add(att1);
            atts.add(att2);
            atts.add(att3);

            Set<Attribute> filterAtts = filter.getAttributes();
            assertEquals(atts, filterAtts);
        }
        // Case 2A
        {
            Filters filter = new Filters();

            Set<Attribute> atts = new HashSet<>();

            Set<Attribute> filterAtts = filter.getAttributes();
            assertEquals(atts, filterAtts);
        }
    }

    /**
     * Test of equals method, of class Filters.
     */
    @Test
    public void testEquals() {
        Table t = Table.APPOINTMENTS;
        // Case 1A
        {
            Filters collection1 = new Filters();

            Attribute att11 = new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t);
            Attribute att12 = new Attribute(Attribute.Name.LOCATION_NUM, "5", t);
            Attribute att13 = new Attribute(Attribute.Name.APPOINTMENT_SLOT, "SDF", t);

            collection1.addEqual(att11);
            collection1.addEqual(att12);
            collection1.addEqual(att13);

            Filters collection2 = new Filters();

            Attribute att21 = new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t);
            Attribute att22 = new Attribute(Attribute.Name.LOCATION_NUM, "5", t);
            Attribute att23 = new Attribute(Attribute.Name.APPOINTMENT_SLOT, "SDF", t);

            collection2.addEqual(att21);
            collection2.addEqual(att22);
            collection2.addEqual(att23);

            assertTrue(collection1.equals(collection2));
        }
        {

            Filters collection1 = new Filters();
            Filters collection2 = new Filters();

            assertTrue(collection1.equals(collection2));
        }
    }

}
