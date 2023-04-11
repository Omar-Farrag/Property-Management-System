package Properties;

import DatabaseManagement.AttributeCollection;
import DatabaseManagement.DatabaseManager;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import java.sql.ResultSet;
import java.util.ArrayList;

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

    public static QueryResult insert(AttributeCollection toInsert) {
        toInsert = toInsert.filter(Table.PROPERTIES);
        return DatabaseManager.getInstance().insert(Table.PROPERTIES, toInsert);

    }

    public static QueryResult modify(AttributeCollection newValues, Filters toModify) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static QueryResult delete(Filters toDelete) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
