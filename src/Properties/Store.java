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
    private String storeClass;
    private String name;

    private final static DatabaseManager DB = DatabaseManager.getInstance();

    /**
     * Leave the constructor as private. If you want an instance of the store,
     * call the retrieve function to retrieve it from Database. This is done to
     * ensure that any instance of the store class represents an actual store in
     * the database, not just some random store that we created in the program
     *
     * @param store
     * @throws SQLException
     */
    private Store(ResultSet store) throws SQLException {
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
        filters.addEqual(new Attribute(Name.LOCATION_NUM, locationNum, Table.PROPERTIES));
        QueryResult store = DB.retrieve(Table.PROPERTIES, filters);

        if (store.getRowsAffected() < 1) {
            return null;
        }
        return new Store(store.getResult());

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
     * Modifies the store in the program and in database
     *
     * @param newValues New values for the attributes of the store
     * @return result of the modification operation
     * @throws SQLException
     * @throws DBManagementException
     */
    public QueryResult modify(AttributeCollection newValues) throws SQLException, DBManagementException {

        Filters filters = new Filters();
        filters.addEqual(new Attribute(Name.LOCATION_NUM, locationNum, Table.PROPERTIES));
        QueryResult result = DB.modify(Table.PROPERTIES, filters, newValues, true);

        if (!result.noErrors()) {
            return result;
        }

        filters.clear();
        filters.addEqual(newValues.getAttribute(Table.PROPERTIES, Name.LOCATION_NUM));

        QueryResult modifiedStore = DB.retrieve(Table.PROPERTIES, filters);
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
        filters.addEqual(new Attribute(Name.LOCATION_NUM, locationNum, Table.PROPERTIES));
        QueryResult result = DB.delete(Table.PROPERTIES, filters);
        if (result.noErrors()) {
            clear();
        }
        return result;
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

    private void setValues(ResultSet store) throws SQLException {
        locationNum = store.getString(Name.LOCATION_NUM.name());
        space = store.getFloat(Name.SPACE.name());
        monthlyRate = store.getFloat(Name.MONTHLY_RATE.name());
        quarterlyRate = store.getFloat(Name.QUARTERLY_RATE.name());
        biAnnualRate = store.getFloat(Name.BI_ANNUAL_RATE.name());
        annualRate = store.getFloat(Name.ANNUAL_RATE.name());
        purpose = store.getString(Name.PURPOSE.name());
        storeClass = store.getString(Name.CLASS.name());
        name = store.getString(Name.NAME.name());
    }

    private void clear() {
        locationNum = null;
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
