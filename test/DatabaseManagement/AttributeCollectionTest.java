/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package DatabaseManagement;

import DatabaseManagement.Exceptions.DBManagementException;
import java.sql.SQLException;
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
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author Dell
 */
public class AttributeCollectionTest {

    public AttributeCollectionTest() {
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

    @Test
    public void testAttributes() {
        // Case 1A
        {
            Table t = Table.APPOINTMENTS;
            AttributeCollection collection = new AttributeCollection();

            Attribute att1 = new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t);
            Attribute att2 = new Attribute(Attribute.Name.LOCATION_NUM, "5", t);
            Attribute att3 = new Attribute(Attribute.Name.APPOINTMENT_SLOT, "SDF", t);

            collection.add(att1);
            collection.add(att2);
            collection.add(att3);

            Set<Attribute> atts = new HashSet<>();

            atts.add(att1);
            atts.add(att2);
            atts.add(att3);

            Set<Attribute> collectionAtts = collection.attributes();
            assertEquals(atts, collectionAtts);
        }
        // Case 2A
        {
            AttributeCollection collection = new AttributeCollection();

            Set<Attribute> atts = new HashSet<>();

            Set<Attribute> collectionAtts = collection.attributes();
            assertEquals(atts, collectionAtts);
        }
    }

    /**
     * Test of add method, of class AttributeCollection.
     */
    @Test
    public void testAdd() {
        //Case 1A
        AttributeCollection instance = new AttributeCollection();
        assertThrows(IllegalArgumentException.class, () -> {
            instance.add(null);
        });

        //Case 2A
        Attribute attribute = new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "", Table.APPOINTMENTS);
        instance.add(attribute);
        Set<Attribute> collectionAttributes = instance.attributes();
        assertTrue(collectionAttributes.contains(attribute));
    }

    /**
     * Test of equals method, of class AttributeCollection.
     */
    @Test
    public void testEquals() {
        Table t = Table.APPOINTMENTS;
        // Case 1A
        {
            AttributeCollection collection1 = new AttributeCollection();

            Attribute att11 = new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t);
            Attribute att12 = new Attribute(Attribute.Name.LOCATION_NUM, "5", t);
            Attribute att13 = new Attribute(Attribute.Name.APPOINTMENT_SLOT, "SDF", t);

            collection1.add(att11);
            collection1.add(att12);
            collection1.add(att13);

            AttributeCollection collection2 = new AttributeCollection();

            Attribute att21 = new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", t);
            Attribute att22 = new Attribute(Attribute.Name.LOCATION_NUM, "5", t);
            Attribute att23 = new Attribute(Attribute.Name.APPOINTMENT_SLOT, "SDF", t);

            collection2.add(att21);
            collection2.add(att22);
            collection2.add(att23);

            assertTrue(collection1.equals(collection2));
        }
        {

            AttributeCollection collection1 = new AttributeCollection();
            AttributeCollection collection2 = new AttributeCollection();

            assertTrue(collection1.equals(collection2));
        }

    }

}
