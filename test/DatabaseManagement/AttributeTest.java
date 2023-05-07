/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package DatabaseManagement;

import DatabaseManagement.Exceptions.DBManagementException;
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
public class AttributeTest {

    public AttributeTest() {
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
    public void testConstructor() {
        Table t = Table.APPOINTMENTS;

        assertThrows(IllegalArgumentException.class, () -> {
            new Attribute(null, "A5", t);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A5", null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, null, null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Attribute(null, "A5", null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Attribute(null, null, t);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Attribute(null, null, null);
        });

        new Attribute(Attribute.Name.POTENTIAL_TENANT_ID, "A1", Table.APPOINTMENTS);
    }

}
