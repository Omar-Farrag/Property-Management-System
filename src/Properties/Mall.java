package Properties;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.DatabaseManager;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;

public class Mall {

    private String name;
    private String address;
    private int numFloors;
    private String mallNum;

    private Mall instance;

    private Mall(String name, String address, int numFloors, String mallNum) {
        this.name = name;
        this.address = address;
        this.numFloors = numFloors;
        this.mallNum = mallNum;
    }

    public static ArrayList<String> getListOfMalls() {
        ArrayList<String> malls = new ArrayList<>();

        try {

            AttributeCollection toGet = new AttributeCollection();
            toGet.add(new Attribute(Name.NAME, Table.MALLS));

            QueryResult result = DatabaseManager.getInstance().retrieve(toGet);

            if (result.noErrors()) {
                ResultSet rs = result.getResult();
                while (rs.next()) {
                    malls.add(rs.getString(Name.NAME.getName()));
                }
            }
        } catch (SQLException | DBManagementException ex) {
            ex.printStackTrace();
        }

        return malls;

    }

    public static int getFloors(String mallName) {

        int numFloors = 0;
        try {

            AttributeCollection toGet = new AttributeCollection();
            toGet.add(new Attribute(Name.NUM_FLOORS, Table.MALLS));

            Filters filters = new Filters();
            filters.addEqual(new Attribute(Name.NAME, mallName, Table.MALLS));
            QueryResult result = DatabaseManager.getInstance().retrieve(toGet, filters);

            if (result.noErrors()) {
                ResultSet rs = result.getResult();
                rs.next();
                numFloors = rs.getInt(Name.NUM_FLOORS.getName());

            }
        } catch (SQLException | DBManagementException ex) {
            ex.printStackTrace();
        }
        return numFloors;
    }

    public static QueryResult insert(AttributeCollection toInsert) throws SQLException, DBManagementException {
        toInsert = toInsert.filter(Table.MALLS);
        toInsert.add(new Attribute(Name.MALL_NUM, String.valueOf(generateMallNum()), Table.MALLS));
        return DatabaseManager.getInstance().insert(Table.MALLS, toInsert);
    }

    public static QueryResult modify(AttributeCollection newValues, Filters toModify) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static QueryResult delete(Filters toDelete) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static Mall retrieve(String mallNum) {
        throw new UnsupportedOperationException();
    }

    private static int generateMallNum() throws SQLException {
        DatabaseManager DB = DatabaseManager.getInstance();
        QueryResult result = DB.retrieveMax(new Attribute(Name.MALL_NUM, Table.MALLS));
        ResultSet max = result.getResult();
        max.next();
        if (max.getInt(1) == 0) {
            return 1;
        } else {
            return max.getInt(1) + 1;
        }
    }

}
