package DatabaseManagement.ConstraintsHandling;

import DatabaseManagement.*;
import DatabaseManagement.Exceptions.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MetaDataExtractor {

    private final String metaDataFile = "Metadata.json";
    private final ArrayList<String> currentApplicationTables;
    private ResultSet constraintsTable;
    private ResultSet tableDataTypes;
    private final HashMap<String, JSONObject> table_to_object;
    private final ReferentialResolver resolver;
    private static MetaDataExtractor instance;

    public static MetaDataExtractor getInstance() {
        return instance == null ? instance = new MetaDataExtractor() : instance;
    }

    private MetaDataExtractor() {
        table_to_object = new HashMap<>();
        resolver = ReferentialResolver.getInstance();

        currentApplicationTables = Table.getApplicationTables();
        if (!new File(metaDataFile).exists())
            initMetaDataFile();

        readMetaDataFromFile();

        resolver.initResolver();
    }

    public JSONObject getTableAttributes(Table t) {
        return (JSONObject) table_to_object.get(t.getTableName()).get("Attributes");
    }

    private void readMetaDataFromFile() {
        try {
            FileInputStream fin = new FileInputStream(metaDataFile);
            InputStreamReader in = new InputStreamReader(fin);
            BufferedReader bin = new BufferedReader(in);

            String fileContent = "";
            String line;
            while ((line = bin.readLine()) != null)
                fileContent += line;

            JSONArray metaData = (JSONArray) new JSONParser().parse(fileContent);
            if (metaData.isEmpty())
                throw new EmptyFileException(metaDataFile);

            for (Object table : metaData) {
                JSONObject tableJSONObject = (JSONObject) table;
                String tableName = tableJSONObject.get("TableName").toString();

                table_to_object.put(tableName, tableJSONObject);
                JSONObject attributes = (JSONObject) tableJSONObject.get("Attributes");
                for (Object attribute : attributes.keySet()) {
                    JSONArray constraints = (JSONArray) attributes.get(attribute);
                    for (int i = 0; i < constraints.size(); i++) {

                        Table t = Table.valueOf(tableName);
                        Attribute.Name attName = Attribute.Name.valueOf(attribute.toString());
                        String constraintName = constraints.get(i).toString();

                        if (constraintName.startsWith("P"))
                            resolver.insertPrimary(t, attName, constraintName.substring(2));
                        else if (constraintName.startsWith("U"))
                            resolver.insertUnique(t, attName, constraintName.substring(2));
                        else if (constraintName.startsWith("R"))
                            resolver.insertForeign(t, attName, constraintName.substring(2));
                    }
                }
            }
            bin.close();
        } catch (IOException |
                 ParseException |
                 EmptyFileException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            initMetaDataFile();
            readMetaDataFromFile();
        }
    }

    public JSONObject getTableInfoFromMetaData(Table t) throws TableNotFoundException {
        if (!table_to_object.containsKey(t.getTableName()))
            throw new TableNotFoundException();
        else
            return table_to_object.get(t.getTableName());
    }

    private Boolean initMetaDataFile() {
        try {
            FileWriter fout = new FileWriter(metaDataFile);
            JSONArray metaData = new JSONArray();

            String formattedTableNames = "'" + String.join(",", currentApplicationTables).replace(",", "','") + "'";
            constraintsTable = DatabaseManager.getInstance().executeStatement(
                    " SELECT U.TABLE_NAME," +
                            "U.COLUMN_NAME," +
                            "CONSTRAINT_TYPE," +
                            " SEARCH_CONDITION," +
                            " U.CONSTRAINT_NAME," +
                            " R_CONSTRAINT_NAME" +
                            " FROM USER_CONS_COLUMNS U" +
                            " JOIN ALL_CONSTRAINTS A" +
                            " ON ( U.TABLE_NAME = A.TABLE_NAME" +
                            " AND U.CONSTRAINT_NAME = A.CONSTRAINT_NAME )" +
                            " WHERE U.OWNER = '" + DatabaseManager.getInstance().getUsername() + "'" +
                            " AND A.OWNER = '" + DatabaseManager.getInstance().getUsername() + "'" +
                            " AND U.TABLE_NAME in (" + formattedTableNames + ") ORDER BY CONSTRAINT_TYPE");

            tableDataTypes = DatabaseManager.getInstance().executeStatement(
                    " select *" +
                            " from user_tab_columns" +
                            " order by TABLE_NAME,column_id"

            );

            extractTables(metaData);

            fout.write(metaData.toJSONString());
            fout.flush();

            return true;
        } catch (
                Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    private void extractTables(JSONArray metaData) {

        try {
            DatabaseMetaData meta = DatabaseManager.getInstance().getMetaData();

            for (String tableName : currentApplicationTables) {
                JSONObject table = new JSONObject();
                table.put("TableName", tableName);

                JSONObject attributes = extractAttributesForTable(tableName, meta);

                table.put("Attributes", attributes);
                metaData.add(table);
//                table_to_object.put(tableName, table);
            }
        } catch (
                SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    private JSONObject extractAttributesForTable(String tableName, DatabaseMetaData meta) throws SQLException {
        ResultSet columns = meta.getColumns(null, DatabaseManager.getInstance().getUsername(), tableName, null);
        JSONObject attributes = new JSONObject();

        while (columns.next()) {

            String attributeName = columns.getString("COLUMN_NAME");
            JSONArray constraints = new JSONArray();

            ArrayList<String> constraintStrings = extractConstraints(tableName, attributeName);
            constraints.add(extractDataType(columns.getRow(), tableName));

            for (String constraint : constraintStrings)
                constraints.add(constraint);

            attributes.put(attributeName, constraints);
        }

        return attributes;
    }

    private String extractDataType(int rowNumber, String tableName) {
        try {

            tableDataTypes.beforeFirst();
            while (tableDataTypes.next()) {
                if (tableDataTypes.getString("TABLE_NAME").equals(tableName)) {

                    tableDataTypes.absolute(tableDataTypes.getRow() + rowNumber - 1);
                    String dataType = tableDataTypes.getString("DATA_TYPE");
                    if (dataType.toLowerCase().equals("number"))
                        dataType += extractPrecisionAndScale(tableDataTypes);
                    else if (dataType.toLowerCase().equals("varchar2") || dataType.toLowerCase().equals("char"))
                        dataType += extractMaxLength(tableDataTypes);
                    return dataType;
                }

            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return "";

    }

    private String extractMaxLength(ResultSet tableDescription) throws SQLException {
        return "_" + tableDescription.getString("CHAR_COL_DECL_LENGTH");
    }

    private String extractPrecisionAndScale(ResultSet tableDescription) throws SQLException {
        return "_" + tableDescription.getString("DATA_PRECISION") + "_" + tableDescription.getString("DATA_SCALE");
    }

    private ArrayList<String> extractConstraints(String tableName, String columnName) throws SQLException {

        ArrayList<String> constraints = new ArrayList<>();
        constraintsTable.first();
        while (constraintsTable.next()) {
            if (constraintsTable.getString("TABLE_NAME").equals(tableName)
                    && constraintsTable.getString("COLUMN_NAME").equals(columnName)) {

                String constraint = constraintsTable.getString("CONSTRAINT_TYPE").toUpperCase();

                if (constraint.equals("C"))
                    constraint += "_"
                            + constraintsTable.getString("SEARCH_CONDITION").replace("\"", "").replace("\n", "")
                            .replace("   ", "");
                else if (constraint.equals("R"))
                    constraint += "_" + constraintsTable.getString("R_CONSTRAINT_NAME");
                else if (constraint.equals("P"))
                    constraint += "_" + constraintsTable.getString("CONSTRAINT_NAME");
                else if (constraint.equals("U"))
                    constraint += "_" + constraintsTable.getString("CONSTRAINT_NAME");


                constraints.add(constraint);
            }
        }
        return constraints;
    }
}
