package TableViewer;

import DatabaseManagement.AttributeCollection;
import DatabaseManagement.DatabaseManager;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import General.Controller;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class TableViewer extends JFrame {

    private JTable table;
    private String title;
    private DefaultTableModel model;
    private final Color violet = new Color(218, 112, 214);
    private JPanel panel;
    private JScrollPane scrollPane;
    private FilterPane filterPane;
    private AttributeCollection toShow;
    private Table toDisplay;
    private DatabaseManager DB = DatabaseManager.getInstance();
    private JPopupMenu contextMenu;

    TableRowSorter<DefaultTableModel> sorter;

    /**
     * Creates a new window displaying the given attribute collection in table
     * format
     *
     * @param title Title of the window
     * @param toShow collection to be retrieved from database and displayed
     * @param filterPane A filter pane containing the scrollPane component to be
     * displayed when filtering. Users will interact with that scroll pane to
     * set the filters as they please. Simply create a new JPanel and make it
     * implement the FilterPane interface. Checkout PropertyBrowsingFilters for
     * more clarity.
     *
     * @throws SQLException
     */
    public TableViewer(String title, AttributeCollection toShow, FilterPane filterPane) throws SQLException, DBManagementException {
        this.toShow = toShow;
        init(title, DB.retrieve(toShow).getResult(), filterPane);
    }

    /**
     * Creates a new window displaying the given table
     *
     * @param title Title of the window
     * @param toDisplay table to be retrieved from database and displayed
     * @param filterPane A filter pane containing the scrollPane component to be
     * displayed when filtering. Users will interact with that scroll pane to
     * set the filters as they please. Simply create a new JPanel and make it
     * implement the FilterPane interface. Checkout PropertyBrowsingFilters for
     * more clarity.
     *
     * @throws SQLException
     */
    public TableViewer(String title, Table toDisplay, FilterPane filterPane) throws SQLException, DBManagementException {
        this.toDisplay = toDisplay;
        init(title, DB.retrieve(toDisplay).getResult(), filterPane);
    }

    /**
     * Displays a context menu when any row is right clicked;
     *
     * @param contextMenu Menu displayed upon right-clicking a row in the table.
     * If the menu items need to perform certain actions, appropriate event
     * listeners must be added to them before passing the popup menu to the
     * table viewer.
     */
    public void setContextMenu(JPopupMenu contextMenu) {
        this.contextMenu = contextMenu;
    }

    /**
     * Gets the values at the given row number
     *
     * @param rowNum number of row to be retrieved
     * @return Table entry for row [rowNum]
     */
    public ArrayList<String> getRow(int rowNum) {
        ArrayList<String> row = new ArrayList<>();
        for (int col = 0; col < table.getColumnCount(); col++) {
            row.add(table.getValueAt(rowNum, col).toString());
        }
        return row;
    }

    /**
     *
     * @return An array of all selected row numbers
     */
    public int[] getSelectedRows() {
        return table.getSelectedRows();
    }

    public void applyFilters() {
        try {
            Filters filters = filterPane.getFilters();
            if (toShow != null) {
                initModel(DB.retrieve(toShow, filters).getResult());
            } else {
                initModel(DB.retrieve(toDisplay, filters).getResult());
            }

            initTable();
            scrollPane.setViewportView(table);
        } catch (SQLException ex) {
            new Controller().displaySQLError(ex);
        } catch (DBManagementException ex) {
            new Controller().displayErrors("Something went wrong while viewing the table");
        }
    }

    private void init(String title, ResultSet resultSet, FilterPane filterPane) throws SQLException {
        this.title = title;
        this.filterPane = filterPane;

        initModel(resultSet);

        initTable();

        initInnerPanels();

        setTitle(title);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.white);
        setBackground(Color.white);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
    }

    private void initModel(ResultSet resultSet) throws SQLException {
        int columnCount = resultSet.getMetaData().getColumnCount();
        Vector<String> columnNames = new Vector<>();
        for (int i = 0; i < columnCount; i++) {
            columnNames.add(resultSet.getMetaData().getColumnLabel(i + 1));
        }

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (resultSet.next()) {
            Vector<Object> row = new Vector<Object>();
            for (int i = 0; i < columnCount; i++) {
                row.add(resultSet.getObject(i + 1));
            }
            data.add(row);
        }
        model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void initTable() {
        table = new JTable(model);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (contextMenu == null) {
                        return;
                    }
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < table.getRowCount()) {
                        table.setRowSelectionInterval(row, row);
                        contextMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
        table.setBackground(Color.WHITE);
        table.setOpaque(true);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setBackground(violet);
    }

    private void initInnerPanels() {
        panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title);
        label.setFont(new Font("Verdana 24 Bold", Font.BOLD, 24));
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JButton button = new JButton("Filter");

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new UIFilterBox(TableViewer.this, filterPane).setVisible(true);
            }
        });
        button.setBackground(Color.ORANGE);

        panel.add(label, BorderLayout.NORTH);
        panel.add(button, BorderLayout.SOUTH);

        scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.white);
        scrollPane.setOpaque(true);

        add(panel, BorderLayout.NORTH);
        add(scrollPane);
    }

}
