package TableViewer;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import FormManipulationStrategies.FilterForm;
import FormManipulationStrategies.InsertForm;
import FormManipulationStrategies.ModifyForm;
import General.Controller;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
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
import java.sql.Timestamp;

public class TableViewer extends JFrame {

    private JTable table;
    private String title;
    private DefaultTableModel model;
    private final Color violet = new Color(218, 112, 214);
    private JPanel panel;
    private JScrollPane scrollPane;
    private AttributeCollection toShow;
    private final Controller controller = new Controller();
    private JPopupMenu contextMenu;
    private Form form;
    private boolean readOnly;
    private Filters originalFilters;

    TableRowSorter<DefaultTableModel> sorter;

    /**
     * Creates a new window displaying the given attribute collection in table
     * format
     *
     * @param title Title of the window
     * @param toShow collection to be retrieved from database and displayed
     * @param form A form containing the frame to be displayed when filtering.
     * Users will interact with that frame to set the filters as they please.
     * Simply create a new JFrame and make it implement the Form interface.
     * Checkout Account for more clarity.
     * @param readOnly Specifies that the user cannot perform modifications to
     * the data shown in the table
     *
     * @throws SQLException
     */
    public TableViewer(String title, AttributeCollection toShow, Filters originalFilters, Form form, boolean readOnly) throws SQLException, DBManagementException {
        this.toShow = toShow;
        this.readOnly = readOnly;
        this.originalFilters = originalFilters;
        init(title, controller.retrieve(toShow, originalFilters).getResult(), form);
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
    public AttributeCollection getRow(int rowNum) {
        AttributeCollection collection = new AttributeCollection();
        int col = 0;
        for (Attribute attribute : toShow.attributes()) {
            Name columnName = attribute.getAttributeName();

            Object value = table.getValueAt(rowNum, col++);
            if (value == null) {
                value = "";
            }
            if (attribute.getType() == Attribute.Type.TIMESTAMP) {
                value = controller.formatTimeStamp(Timestamp.valueOf(value.toString()));
            }
            collection.add(new Attribute(columnName, value.toString().trim(), attribute.getT()));
        }
        return collection;
    }

    /**
     *
     * @return An array of all selected row numbers
     */
    public int[] getSelectedRows() {
        return table.getSelectedRows();
    }

    public void applyBrowsingFilters() throws SQLException {
        Controller controller = new Controller();
        Filters filters = form.getBrowsingFilters();
        filters.append(originalFilters);

        QueryResult result = controller.retrieve(toShow, filters);

        if (!result.noErrors()) {
            return;
        }
        initModel(result.getResult());

        initTable();

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        scrollPane.setViewportView(table);
        form.getFrame().dispose();
    }

    //For each table filter the filterCollection such that filters belong to one table only
    //Do same thing for other apply functions
    public void applyModification() throws SQLException {
        Controller controller = new Controller();
        Filters filters = form.getPKFilter();
        filters.append(originalFilters);
        AttributeCollection newValues = form.getAllAttributes();
        Table[] tables = form.getTables();
//        ArrayList<QueryResult> results;

        for (Table t : tables) {
            Filters filtsForT = filters.filter(t);
            AttributeCollection valsForT = newValues.filter(t);
            controller.modify(t, valsForT, filtsForT);

        }

//        if (!result.noErrors()) {
//            return;
//        }
        initModel(controller.retrieve(toShow, originalFilters).getResult());

        initTable();

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        scrollPane.setViewportView(table);
        form.getFrame().dispose();

    }

    public void applyInsertion() throws SQLException {
        Controller controller = new Controller();

        AttributeCollection newValues = form.getAllAttributes();
        Table[] tables = form.getTables();

        for (Table t : tables) {
            AttributeCollection valsForT = newValues.filter(t);
            controller.insert(t, valsForT);

        }
//        if (!result.noErrors()) {
//            return;
//        }
        initModel(controller.retrieve(toShow, originalFilters).getResult());

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        initTable();
        scrollPane.setViewportView(table);
        form.getFrame().dispose();
    }

    public void applyDeletion() throws SQLException {
        Controller controller = new Controller();
        Table[] tables = form.getTables();

        int[] selectedRows = table.getSelectedRows();
        for (int row : selectedRows) {
            Filters filters = new Filters(getRow(row));
            filters.append(originalFilters);

            for (Table t : tables) {
                Filters filtsForT = filters.filter(t);
                controller.delete(t, filtsForT);

            }
//            if (!result.noErrors()) {
//                break;
//            }
        }

        initModel(controller.retrieve(toShow, originalFilters).getResult());
        initTable();
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        scrollPane.setViewportView(table);
        form.getFrame().dispose();

    }

    private void init(String title, ResultSet resultSet, Form form) throws SQLException {
        this.title = title;
        this.form = form;
        if (form != null) {

            form.getFrame().setVisible(false);
            form.setViewer(this);
        }

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
        setVisible(true);
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

        if (!readOnly && form != null) {
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int row = table.getSelectedRow();
                        AttributeCollection collection = getRow(row);
                        form.setInitStrategy(new ModifyForm(collection));
                        form.applyInitStrategy();
                        form.getFrame().setVisible(true);
                    }
                }
            }
            );
        }
    }

    private void initInnerPanels() {
        panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title);
        label.setFont(new Font("Verdana 24 Bold", Font.BOLD, 24));
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));

        if (form != null) {

            JButton filterButton = new JButton("Filter");

            filterButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    form.setInitStrategy(new FilterForm());
                    form.applyInitStrategy();
                    form.getFrame().setVisible(true);
                }
            });
            filterButton.setBackground(Color.ORANGE);
            filterButton.setPreferredSize(new Dimension(100, 30));

            JButton deleteButton = new JButton("Delete");
            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        applyDeletion();
                    } catch (SQLException ex) {
                        new Controller().displaySQLError(ex);
                    }
                }
            });
            deleteButton.setBackground(Color.ORANGE);
            deleteButton.setPreferredSize(new Dimension(100, 30));

            JButton insertButton = new JButton("Insert");
            insertButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    form.setInitStrategy(new InsertForm());
                    form.applyInitStrategy();
                    form.getFrame().setVisible(true);
                }
            });
            insertButton.setBackground(Color.ORANGE);
            insertButton.setPreferredSize(new Dimension(100, 30));

            if (readOnly) {
                insertButton.setVisible(false);
                deleteButton.setVisible(false);
            }
            buttonsPanel.add(filterButton, BorderLayout.WEST);
            buttonsPanel.add(deleteButton, BorderLayout.CENTER);
            buttonsPanel.add(insertButton, BorderLayout.EAST);
        }

        panel.add(label, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.white);
        scrollPane.setOpaque(true);

        add(panel, BorderLayout.NORTH);
        add(scrollPane);
    }

}
