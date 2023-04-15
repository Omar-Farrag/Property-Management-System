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
    private int mallNum;

    private final static DatabaseManager DB = DatabaseManager.getInstance();

    /**
     * Leave the constructor as private. If you want an instance of the mall,
     * call the retrieve function to retrieve it from Database. This is done to
     * ensure that any instance of the mall class represents an actual mall in
     * the database, not just some random mall that we created in the program
     *
     * @param mall result set pointing to the row containing the mall
     * information
     * @throws SQLException
     */
    private Mall(ResultSet mall) throws SQLException {
        setValues(mall);
    }

    /**
     * @return List of all malls that the real estate company owns
     */
    public static ArrayList<Mall> getListOfMalls() throws SQLException {
        ArrayList<Mall> malls = new ArrayList<>();

        QueryResult result = DB.retrieve(Table.MALLS);

        if (result.noErrors()) {
            ResultSet rs = result.getResult();
            while (rs.next()) {
                malls.add(new Mall(rs));
            }
        }
        return malls;

    }

    /**
     * Inserts a new mall into the database
     *
     * @param toInsert
     * @return QueryResult of the insertion operation. Returns null if the mall
     * already exists
     *
     * @throws SQLException
     * @throws DBManagementException
     */
    public static QueryResult insert(AttributeCollection toInsert) throws SQLException, DBManagementException {
        toInsert = toInsert.filter(Table.MALLS);
        toInsert.add(new Attribute(Name.MALL_NUM, String.valueOf(generateMallNum()), Table.MALLS));
        return DB.insert(Table.MALLS, toInsert);
    }

    /**
     * Modifies the mall in the program and in database
     *
     * @param newValues New values for the attributes of the mall
     * @return result of the modification operation
     * @throws SQLException
     * @throws DBManagementException
     */
    public QueryResult modify(AttributeCollection newValues) throws SQLException, DBManagementException {
        Filters filters = new Filters();
        filters.addEqual(new Attribute(Name.MALL_NUM, String.valueOf(mallNum), Table.MALLS));
        QueryResult result = DB.modify(Table.MALLS, filters, newValues, true);

        if (!result.noErrors()) {
            return result;
        }

        filters.clear();
        filters.addEqual(newValues.getAttribute(Table.MALLS, Name.MALL_NUM));

        QueryResult modifiedMall = DB.retrieve(Table.MALLS, filters);
        setValues(modifiedMall.getResult());

        return result;
    }

    /**
     * Deletes a mall from the database and nullifies clears this mall object
     *
     * @return result of the deletion operation
     * @throws SQLException
     * @throws DBManagementException
     */
    public QueryResult delete() throws SQLException, DBManagementException {

        Filters filters = new Filters();
        filters.addEqual(new Attribute(Name.MALL_NUM, String.valueOf(mallNum), Table.MALLS));
        QueryResult result = DB.delete(Table.MALLS, filters);
        if (result.noErrors()) {
            clear();
        }
        return result;
    }

    /**
     * @return List of all stores in this mall
     */
    public ArrayList<Store> getStores() throws SQLException, DBManagementException {
        Filters filter = new Filters();
        filter.addEqual(new Attribute(Name.MALL_NUM, String.valueOf(mallNum), Table.LOCS));

        AttributeCollection toGet = new AttributeCollection().add(new Attribute(Name.LOCATION_NUM, Table.LOCS));

        //Retrieve location numbers where the mall number is equal to this mall's mall number
        QueryResult retrievalResult = DB.retrieve(toGet, filter);
        ResultSet locationNumbers = retrievalResult.getResult();

        ArrayList<Store> stores = new ArrayList<>();

        while (locationNumbers.next()) {
            Store store = Store.retrieve(locationNumbers.getString(1));
            stores.add(store);
        }
        return stores;
    }

    /**
     * Retrieves the mall with the given mall number from the database.
     *
     * @param mallNum mall number of the mall to be retrieved
     * @return A fully initialized Mall instance containing the same attributes
     * as those in Database. If there is no mall with the given mall number,
     * function returns null.
     *
     * @throws SQLException
     * @throws DBManagementException
     */
    public static Mall retrieve(int mallNum) throws SQLException, DBManagementException {
        Filters filters = new Filters();
        filters.addEqual(new Attribute(Name.MALL_NUM, String.valueOf(mallNum), Table.MALLS));
        QueryResult mall = DB.retrieve(Table.MALLS, filters);

        if (mall.getRowsAffected() < 1) {
            return null;
        }
        return new Mall(mall.getResult());

    }

    /**
     * Retrieves the mall with the given mall number from the database.
     *
     * @param mallNum mall number of the mall to be retrieved
     * @return A fully initialized Mall instance containing the same attributes
     * as those in Database. If there is no mall with the given mall number,
     * function returns null.
     *
     * @throws SQLException
     * @throws DBManagementException
     */
    public static Mall retrieve(String mallName) throws SQLException, DBManagementException {
        Filters filters = new Filters();
        filters.addEqual(new Attribute(Name.NAME, mallName, Table.MALLS));
        QueryResult mall = DB.retrieve(Table.MALLS, filters);

        if (mall.getRowsAffected() < 1) {
            return null;
        }
        ResultSet newMall = mall.getResult();
        newMall.next();
        return new Mall(newMall);

    }

    private static int generateMallNum() throws SQLException {
        QueryResult result = DB.retrieveMax(new Attribute(Name.MALL_NUM, Table.MALLS));
        ResultSet max = result.getResult();
        max.next();
        if (max.getInt(1) == 0) {
            return 1;
        } else {
            return max.getInt(1) + 1;
        }
    }

    private void setValues(ResultSet mall) throws SQLException {
        name = mall.getString(Name.NAME.getName());
        address = mall.getString(Name.ADDRESS.getName());
        numFloors = mall.getInt(Name.NUM_FLOORS.getName());
        mallNum = mall.getInt(Name.MALL_NUM.getName());
    }

    private void clear() {
        name = null;
        address = null;
        numFloors = 0;
        mallNum = 0;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getNumFloors() {
        return numFloors;
    }

    public int getMallNum() {
        return mallNum;
    }
}
