package TableViewer;

import DatabaseManagement.DatabaseManager;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import static javax.swing.GroupLayout.Alignment.CENTER;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ResultSetViewer extends JFrame {

    private JTable table;
    private String title;

    public ResultSetViewer(String title, ResultSet resultSet) throws SQLException {
        this.title = title;
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
        Color violet = new Color(218, 112, 214);
        TableModel model = new DefaultTableModel(data, columnNames);

        table = new JTable(model);
        table.setBackground(Color.WHITE);
        table.setOpaque(true);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setBackground(violet);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.white);
        scrollPane.setOpaque(true);

        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel(title);
        label.setFont(new Font("Verdana 24 Bold", Font.BOLD, 24));
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JButton button = new JButton("Filter");
        button.setBackground(Color.ORANGE);

        panel.add(label, BorderLayout.NORTH);
        panel.add(button, BorderLayout.SOUTH);

        add(panel, BorderLayout.NORTH);
        add(scrollPane);

        setTitle(title);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.white);
        setBackground(Color.white);
//        setOpaque(true);

        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
        table.setRowSorter(sorter);
    }

    public static void main(String[] args) throws Exception {
        QueryResult result = DatabaseManager.getInstance().retrieve(Table.USERS);

        ResultSetViewer viewer = new ResultSetViewer("Testing", result.getResult());
        viewer.setVisible(true);
    }

}
