package Properties;

import DatabaseManagement.QueryResult;
import java.util.ArrayList;

public class Store implements PropertyManagementInterface {

    public static ArrayList<String> getClasses() {
        ArrayList<String> classes = new ArrayList<String>();
        classes.add("A");
        classes.add("B");
        classes.add("C");
        classes.add("D");

        return classes;
    }

    public static ArrayList<String> getPurposes() {
        ArrayList<String> purposes = new ArrayList<String>();
        purposes.add("Supermarket");
        purposes.add("Cinema");
        purposes.add("Cafe");
        purposes.add("Arcade");
        return purposes;

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
