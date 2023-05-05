/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package TableViewer;

import GUI.TableViewer;
import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Filters;
import DatabaseManagement.Table;
import General.Function;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
//import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;

/**
 *
 * @author Dell
 */
public class TableViewerTest {

    /**
     * Test of getSelectedRow method, of class TableViewer.
     */
    @Test
    public void testGetSelectedRow() {
        try {
            AttributeCollection collection = new AttributeCollection();

            Attribute agentID = new Attribute(Name.AGENT_ID, Table.APPOINTMENT_SLOTS);
            Attribute startDate = new Attribute(Name.START_DATE, Table.APPOINTMENT_SLOTS);
            Attribute booked = new Attribute(Name.BOOKED, Table.APPOINTMENT_SLOTS);

            collection.add(agentID);
            collection.add(startDate);
            collection.add(booked);

            Filters filters = new Filters();

            TableViewer instance = new TableViewer("Testing", collection, filters, null, false);
            JTable table = instance.getTable();

            table.setRowSelectionInterval(1, 1);

            Set<Attribute> selected = instance.getSelectedRow().attributes();

            assertEquals(selected.size(), 3);
            assertEquals(selected.contains(agentID), true);
            assertEquals(selected.contains(startDate), true);
            assertEquals(selected.contains(booked), true);

            // TODO review the generated test code and remove the default call to fail.
        } catch (SQLException ex) {
            fail("Failed due to a SQL Exception.");
        }
    }

    /**
     * Test of overrideClickListener method, of class TableViewer.
     */
    @Test
    public void testOverrideClickListener() {
        try {
            AttributeCollection collection = new AttributeCollection();

            Attribute agentID = new Attribute(Name.AGENT_ID, Table.APPOINTMENT_SLOTS);
            Attribute startDate = new Attribute(Name.START_DATE, Table.APPOINTMENT_SLOTS);
            Attribute booked = new Attribute(Name.BOOKED, Table.APPOINTMENT_SLOTS);

            collection.add(agentID);
            collection.add(startDate);
            collection.add(booked);

            Filters filters = new Filters();

            TableViewer instance = new TableViewer("Testing", collection, filters, null, false);
            CountDownLatch cdLatch = new CountDownLatch(1);
            instance.overrideClickListener(() -> {
                cdLatch.countDown();
            });

            JTable table = instance.getTable();

            table.setRowSelectionInterval(1, 1);
            Rectangle cellRect = table.getCellRect(1, 1, false); // get the rectangle that bounds the cell
            MouseEvent doubleClick = new MouseEvent(table, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), MouseEvent.BUTTON1, cellRect.x + 5, cellRect.y + 5, 2, false, MouseEvent.BUTTON1);
            table.dispatchEvent(doubleClick);
            try {
                assertTrue(cdLatch.await(1, TimeUnit.SECONDS));
            } catch (InterruptedException ex) {
                Logger.getLogger(TableViewerTest.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            fail("Failed due to a SQL Exception.");
        }
    }

}
