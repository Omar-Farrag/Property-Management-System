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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.ResultSet;

public class Mall implements PropertyManagementInterface {

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

    @Override
    public QueryResult insert() {

        return null;
    }

    @Override
    public QueryResult modify() {

        return null;
    }

    @Override
    public QueryResult delete() {

        return null;
    }
}
