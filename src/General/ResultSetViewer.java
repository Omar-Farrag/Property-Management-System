
import DatabaseManagement.DatabaseManager;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ResultSetViewer extends JFrame {

    private JTable table;

    public ResultSetViewer(ResultSet resultSet) throws SQLException {
        // Get metadata from result set
        int columnCount = resultSet.getMetaData().getColumnCount();
        Vector<String> columnNames = new Vector<>();
        for (int i = 0; i < columnCount; i++) {
            columnNames.add(resultSet.getMetaData().getColumnLabel(i + 1));
        }

        // Populate table model with result set data
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (resultSet.next()) {
            Vector<Object> row = new Vector<Object>();
            for (int i = 0; i < columnCount; i++) {
                row.add(resultSet.getObject(i + 1));
            }
            data.add(row);
        }
        TableModel model = new DefaultTableModel(data, columnNames);

        // Create table with model and add to scroll pane
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Add table to frame and set properties
        add(scrollPane);
        setTitle("Result Set Viewer");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Add sorting functionality to table
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
        table.setRowSorter(sorter);
    }

    public static void main(String[] args) throws Exception {
        // Connect to database
        QueryResult result = DatabaseManager.getInstance().retrieve(Table.USERS);

        // Display result set in new window
        ResultSetViewer viewer = new ResultSetViewer(result.getResult());
        viewer.setVisible(true);
    }

}
