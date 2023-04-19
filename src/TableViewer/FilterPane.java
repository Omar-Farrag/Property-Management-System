/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package TableViewer;

import DatabaseManagement.Filters;
import DatabaseManagement.Table;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Dell
 */
public interface FilterPane {

    /**
     *
     * @return ScrollPane containing the UI for the user to enter the filters
     */
    public JScrollPane getScrollPane();

    /**
     *
     * @return A Filters object containing all the filters that the user has
     * selected in the UI
     */
    public Filters getFilters();
}
