/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package DataEntryInterface;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import General.Controller;
import java.util.ArrayList;
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
public class StoreTest {

    public StoreTest() {
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
     * Test of retrieve method, of class Store.
     */
    @Test
    public void testRetrieve() throws Exception {

        // CASE 1A
        assertThrows(IllegalArgumentException.class, () -> {
            Store.retrieve("asdfasdfad");
        });

        // CASE 1B
        assertThrows(IllegalArgumentException.class, () -> {
            Store.retrieve(null);
        });

        // CASE 1C
        assertThrows(IllegalArgumentException.class, () -> {
            Store.retrieve("");
        });

        // CASE 2A
        assertThrows(IllegalArgumentException.class, () -> {
            Store.retrieve("1632478124371208349612437018293418237480912374912374917230498712309487129347102938470129347910283470129374912834091283740912379127349812374091273491273947");
        });

        // Case 3A
        Controller controller = new Controller();

        Filters filters1 = new Filters();
        filters1.addEqual(new Attribute(Name.LOCATION_NUM, "999", Table.PROPERTIES));
        QueryResult retrievalResult = controller.retrieve(Table.PROPERTIES, filters1);
        if (retrievalResult.getRowsAffected() > 0) {
            throw new Exception("CASE 3A FAILED BECAUSE VALUE ALREADY EXISTS");
        }

        // Case 4A
        AttributeCollection collection = new AttributeCollection();
        collection.add(new Attribute(Name.LOCATION_NUM, "55", Table.PROPERTIES));
        collection.add(new Attribute(Name.NAME, "Maras Turka", Table.PROPERTIES));
        collection.add(new Attribute(Name.CLASS, "A", Table.PROPERTIES));
        collection.add(new Attribute(Name.SPACE, "255", Table.PROPERTIES));
        collection.add(new Attribute(Name.PURPOSE, "Cafe", Table.PROPERTIES));
        collection.add(new Attribute(Name.MONTHLY_RATE, "500", Table.PROPERTIES));
        collection.add(new Attribute(Name.QUARTERLY_RATE, "5000", Table.PROPERTIES));
        collection.add(new Attribute(Name.BI_ANNUAL_RATE, "2000", Table.PROPERTIES));
        collection.add(new Attribute(Name.ANNUAL_RATE, "3000", Table.PROPERTIES));
        collection.add(new Attribute(Name.STATUS, "Available", Table.PROPERTIES));

        QueryResult insertionResult = controller.insert(Table.PROPERTIES, collection, false);
        if (!insertionResult.noErrors()) {
            throw new Exception("INSERTION FAILED IN CASE 4A");
        }

        Store store = Store.retrieve("55");
        assertEquals(store.getLocationNum(), 55);
        assertEquals(store.getName(), "Maras Turka");
        assertEquals(store.getStoreClass(), "A");
        assertEquals(store.getPurpose(), "Cafe");
        assertEquals(store.getSpace(), 255, 0);
        assertEquals(store.getMonthlyRate(), 500, 0);
        assertEquals(store.getQuarterlyRate(), 5000, 0);
        assertEquals(store.getBiAnnualRate(), 2000, 0);
        assertEquals(store.getAnnualRate(), 3000, 0);

        Filters filters = new Filters();
        filters.addEqual(new Attribute(Name.LOCATION_NUM, "55", Table.PROPERTIES));
        controller.delete(Table.PROPERTIES, filters, true);
    }

}
