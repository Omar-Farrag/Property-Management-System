package Properties;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.DatabaseManager;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Store {

    private String locationNum;
    private float space;
    private float monthlyRate;
    private float quarterlyRate;
    private float biAnnualRate;
    private float annualRate;
    private String purpose;
    private char storeClass;
    private String name;

    private Store instance;

    private Store(ResultSet store) {

    }

    public Store() {
    }

    public static ArrayList<String> getClasses() {
        ArrayList<String> classes = new ArrayList<String>();
        classes.add("A");
        classes.add("B");
        classes.add("C");
        classes.add("D");

        return classes;
    }

    public static ArrayList<String> getPurposes() {
        ArrayList<String> purposes = new ArrayList<>();
        purposes.add("Supermarket");
        purposes.add("Cinema");
        purposes.add("Cafe");
        purposes.add("Arcade");
        return purposes;

    }

    /**
     * Inserts a new store into the database
     *
     * @param toInsert
     * @return QueryResult of the insertion operation. Null if the store already
     * exists
     * @throws SQLException
     * @throws DBManagementException
     */
    public static QueryResult insert(AttributeCollection toInsert) throws SQLException, DBManagementException {
        DatabaseManager DB = DatabaseManager.getInstance();
        Filters filters = new Filters();
        AttributeCollection collection = new AttributeCollection();

        //Find the mall number based on the given mall name
        Attribute mallName = toInsert.getAttribute(Table.MALLS, Name.NAME);
        filters.addEqual(mallName);
        collection.add(new Attribute(Name.MALL_NUM, Table.MALLS));
        QueryResult result = DB.retrieve(collection, filters);
        ResultSet mallNumbers = result.getResult();
        mallNumbers.next();
        String mallNumber = mallNumbers.getString(Name.MALL_NUM.getName());

        //Look in the locs table if there is an exisiting mall/store combination
        Attribute mallNum = new Attribute(Name.MALL_NUM, mallNumber, Table.LOCS);
        Attribute storeNum = toInsert.getAttribute(Table.LOCS, Name.STORE_NUM);

        filters.clear();
        filters.addEqual(mallNum);
        filters.addEqual(storeNum);

        QueryResult locationNum = DB.retrieve(Table.LOCS, filters);
        if (locationNum.getRowsAffected() > 0) {
            return null;
        } else {
            AttributeCollection newLocationEntry = new AttributeCollection(filters);
            int generatedLocationNum = generateLocationNum();
            Attribute newLocationNum = new Attribute(Name.LOCATION_NUM, String.valueOf(generatedLocationNum), Table.LOCS);
            newLocationEntry.add(newLocationNum);
            QueryResult locationInsertion = DB.insert(Table.LOCS, newLocationEntry);

            if (!locationInsertion.noErrors()) {
                return locationInsertion;
            }

            newLocationNum = new Attribute(Name.LOCATION_NUM, String.valueOf(generatedLocationNum), Table.PROPERTIES);
            toInsert.add(newLocationNum);
            toInsert = toInsert.filter(Table.PROPERTIES);
            return DatabaseManager.getInstance().insert(Table.PROPERTIES, toInsert);
        }

    }

    public static QueryResult modify(AttributeCollection newValues, Filters toModify) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static QueryResult delete(Filters toDelete) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static Store retrieve(String locationNum) {
        throw new UnsupportedOperationException();
    }

    private static int generateLocationNum() throws SQLException {
        DatabaseManager DB = DatabaseManager.getInstance();
        QueryResult result = DB.retrieveMax(new Attribute(Name.LOCATION_NUM, Table.LOCS));
        ResultSet max = result.getResult();
        max.next();
        return max.getInt(1) + 1;
    }

}
