package Properties;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.DatabaseManager;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import TableViewer.PropertyBrowsingFilters;
import TableViewer.TableViewer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Store {

    private int locationNum;
    private float space;
    private float monthlyRate;
    private float quarterlyRate;
    private float biAnnualRate;
    private float annualRate;
    private String purpose;
    private String storeClass;
    private String name;
    private int storeNumber;
    private int floor;

    private HashMap<String, Integer> charFloor_to_intFloor = new HashMap<>();
    private final static DatabaseManager DB = DatabaseManager.getInstance();

    /**
     * Leave the constructor as private. If you want an instance of the store,
     * call the retrieve function to retrieve it from Database. This is done to
     * ensure that any instance of the store class represents an actual store in
     * the database, not just some random store that we created in the program
     *
     * @param store Result set pointing to the row containing the store
     * information
     * @throws SQLException
     */
    private Store(ResultSet store) throws SQLException, DBManagementException {
        charFloor_to_intFloor = new HashMap<>();
        charFloor_to_intFloor.put("G", 0);
        charFloor_to_intFloor.put("F", 1);
        charFloor_to_intFloor.put("S", 2);
        charFloor_to_intFloor.put("H", 3);
        charFloor_to_intFloor.put("O", 4);

        setValues(store);
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
            QueryResult storeInsertion = DatabaseManager.getInstance().insert(Table.PROPERTIES, toInsert);
            if (!storeInsertion.noErrors()) {
                Filters deletionFilter = new Filters();
                deletionFilter.addEqual(new Attribute(Name.LOCATION_NUM, String.valueOf(generatedLocationNum), Table.LOCS));
                DB.delete(Table.LOCS, deletionFilter);
            }
            return storeInsertion;
        }

    }

    /**
     * Retrieves the store at the given locationNum from the database.
     *
     * @param locationNum Location number of store to be retrieved
     * @return A fully initialized Store instance containing the same attributes
     * as those in Database. If there is no store at the given locationNum,
     * function returns null.
     *
     * @throws SQLException
     * @throws DBManagementException
     */
    public static Store retrieve(String locationNum) throws SQLException, DBManagementException {
        Filters filters = new Filters();
        filters.addEqual(new Attribute(Name.LOCATION_NUM, String.valueOf(locationNum), Table.PROPERTIES));
        QueryResult store = DB.retrieve(Table.PROPERTIES, filters);

        if (store.getRowsAffected() < 1) {
            return null;
        }
        ResultSet storeRow = store.getResult();
        storeRow.next();
        return new Store(storeRow);

    }

    /**
     * @return List of all store classes as described by the real estate
     * company's classification system
     */
    public static ArrayList<String> getClasses() {
        ArrayList<String> classes = new ArrayList<String>();
        classes.add("A");
        classes.add("B");
        classes.add("C");
        classes.add("D");

        return classes;
    }

    /**
     *
     * @return List of all purposes a store can be used for
     */
    public static ArrayList<String> getPurposes() {
        ArrayList<String> purposes = new ArrayList<>();
        purposes.add("Supermarket");
        purposes.add("Cinema");
        purposes.add("Cafe");
        purposes.add("Arcade");
        return purposes;

    }

    /**
     *
     * @return An attribute collection consisting of the attributes that will be
     * displayed to the user when browsing through properties
     */
    public static AttributeCollection getBrowsingAttributes() {
        AttributeCollection toShow = new AttributeCollection();

        toShow.add(new Attribute(Name.NAME, Table.PROPERTIES));
        toShow.add(new Attribute(Name.NAME, Table.MALLS));
        toShow.add(new Attribute(Name.STORE_NUM, Table.LOCS));
        toShow.add(new Attribute(Name.CLASS, Table.PROPERTIES));
        toShow.add(new Attribute(Name.SPACE, Table.PROPERTIES));
        toShow.add(new Attribute(Name.PURPOSE, Table.PROPERTIES));
        toShow.add(new Attribute(Name.MONTHLY_RATE, Table.PROPERTIES));
        toShow.add(new Attribute(Name.QUARTERLY_RATE, Table.PROPERTIES));
        toShow.add(new Attribute(Name.BI_ANNUAL_RATE, Table.PROPERTIES));
        toShow.add(new Attribute(Name.ANNUAL_RATE, Table.PROPERTIES));
        return toShow;
    }

    /**
     * Modifies the store in the program and in database
     *
     * @param newValues New values for the attributes of the store
     * @return result of the modification operation
     * @throws SQLException
     * @throws DBManagementException
     */
    public QueryResult modify(AttributeCollection newValues) throws SQLException, DBManagementException {

        Filters filters = new Filters();
        filters.addEqual(new Attribute(Name.LOCATION_NUM, String.valueOf(locationNum), Table.PROPERTIES));

        QueryResult result = DB.modify(Table.PROPERTIES, filters, newValues.filter(Table.PROPERTIES), true);

        filters.clear();
        filters.addEqual(new Attribute(Name.LOCATION_NUM, String.valueOf(locationNum), Table.LOCS));
        QueryResult result2 = DB.modify(Table.LOCS, filters, newValues.filter(Table.LOCS), true);

        if (!result.noErrors()) {
            return result;
        }
        if (!result2.noErrors()) {
            return result2;
        }

        filters.clear();
        filters.addEqual(new Attribute(Name.LOCATION_NUM, String.valueOf(locationNum), Table.PROPERTIES));

        QueryResult modifiedStore = DB.retrieve(Table.PROPERTIES, filters);
        modifiedStore.getResult().next();
        setValues(modifiedStore.getResult());

        return result;

    }

    /**
     * Deletes a store from the database and nullifies clears this store object
     *
     * @return result of the deletion operation
     * @throws SQLException
     * @throws DBManagementException
     */
    public QueryResult delete() throws SQLException, DBManagementException {

        Filters filters = new Filters();
        filters.addEqual(new Attribute(Name.LOCATION_NUM, String.valueOf(locationNum), Table.PROPERTIES));
        QueryResult result = DB.delete(Table.PROPERTIES, filters);

        filters.clear();
        filters.addEqual(new Attribute(Name.LOCATION_NUM, String.valueOf(locationNum), Table.LOCS));
        QueryResult result2 = DB.delete(Table.LOCS, filters);

        if (!result.noErrors()) {
            return result;
        }
        if (!result2.noErrors()) {
            return result2;
        }

        clear();
        return result;
    }

    public int getLocationNum() {
        return locationNum;
    }

    public float getSpace() {
        return space;
    }

    public float getMonthlyRate() {
        return monthlyRate;
    }

    public float getQuarterlyRate() {
        return quarterlyRate;
    }

    public float getBiAnnualRate() {
        return biAnnualRate;
    }

    public float getAnnualRate() {
        return annualRate;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStoreClass() {
        return storeClass;
    }

    public String getName() {
        return name;
    }

    public int getStoreNumber() {
        return storeNumber;
    }

    public int getFloor() {
        return floor;
    }

    private static int generateLocationNum() throws SQLException {
        QueryResult result = DB.retrieveMax(new Attribute(Name.LOCATION_NUM, Table.LOCS));
        ResultSet max = result.getResult();
        max.next();
        if (max.getInt(1) == 0) {
            return 1;
        } else {
            return max.getInt(1) + 1;
        }
    }

    private void setValues(ResultSet store) throws SQLException, DBManagementException {
        locationNum = store.getInt(Name.LOCATION_NUM.name());
        space = store.getFloat(Name.SPACE.name());
        monthlyRate = store.getFloat(Name.MONTHLY_RATE.name());
        quarterlyRate = store.getFloat(Name.QUARTERLY_RATE.name());
        biAnnualRate = store.getFloat(Name.BI_ANNUAL_RATE.name());
        annualRate = store.getFloat(Name.ANNUAL_RATE.name());
        purpose = store.getString(Name.PURPOSE.name());
        storeClass = store.getString(Name.CLASS.name());
        name = store.getString(Name.NAME.name());

        Filters filters = new Filters();
        filters.addEqual(new Attribute(Name.LOCATION_NUM, String.valueOf(locationNum), Table.LOCS));
        QueryResult result = DB.retrieve(Table.LOCS, filters);

        ResultSet locInfo = result.getResult();
        locInfo.next();

        String storeNUM = locInfo.getString(Name.STORE_NUM.getName());

        String c = storeNUM.substring(0, 1);
        floor = charFloor_to_intFloor.get(c);
        storeNumber = Integer.parseInt(storeNUM.substring(1));

    }

    private void clear() {
        locationNum = 0;
        space = 0;
        monthlyRate = 0;
        quarterlyRate = 0;
        biAnnualRate = 0;
        annualRate = 0;
        purpose = null;
        storeClass = null;
        name = null;
    }

}
