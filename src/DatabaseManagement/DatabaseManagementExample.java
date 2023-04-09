package DatabaseManagement;

import DatabaseManagement.Attribute.Name;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class DatabaseManagementExample {

    public static void main(String[] args) {
        DatabaseManager DB = DatabaseManager.getInstance();

        try {
            //Let's first initialize an empty attribute collection, filters, and query result
            AttributeCollection collection = new AttributeCollection();
            Filters filters = new Filters();
            QueryResult result;
            Scanner ins = new Scanner(System.in);

            //Let's say we want to retrieve all rows from table Users"
            result = DB.retrieve(Table.USERS);

            //First check that there is no errors, then get the result set.
            //Do whatever you want with the result set. In this case we are going to print it to the console
            System.out.println("DB.retrieve(Table.USERS);");
            if (result.noErrors()) printTable(result.getResult());

            System.out.println();
            System.out.println("Type anything to continue");
            ins.nextLine();
            System.out.println();

            //Let' say we want to show only the ID and Email Address"
            //We will add it to an attribute collection first"
            //Then we will retrieve the table"
            collection.clear();
            collection.add(new Attribute(Name.USER_ID, Table.USERS));
            collection.add(new Attribute(Name.EMAIL_ADDRESS, Table.USERS));
            result = DB.retrieve(collection);
            System.out.println("DB.retrieve(collection)");
            if (result.noErrors()) printTable(result.getResult());

            System.out.println();
            System.out.println("Type anything to continue");
            ins.nextLine();
            System.out.println();

            //To get rows that match specific conditions, add them to a filters object
            filters.clear();
            filters.addEqual(new Attribute(Name.USER_ID, "A4", Table.USERS));
            filters.addEqual(new Attribute(Name.FNAME, "Hassan", Table.USERS));
            result = DB.retrieve(Table.USERS, filters);
            System.out.println("DB.retrieve(Table.Users,filters)");
            if (result.noErrors()) printTable(result.getResult());

            System.out.println();
            System.out.println("Type anything to continue");
            ins.nextLine();
            System.out.println();

            //You can also do both retrieve specific attributes and filter the rows
            result = DB.retrieve(collection, filters);
            System.out.println("DB.retrieve(collection,filters");
            if (result.noErrors()) printTable(result.getResult());

            System.out.println();
            System.out.println("Type anything to continue");
            ins.nextLine();
            System.out.println();

            //Let's try inserting the following tuple into the table
            //Change the USER_ID when you run cuz it has to be unique in the table
            //A40  Karim Benzema 0554321234 karim@benzema.edu PT
            collection.clear();
            collection.add(new Attribute(Name.USER_ID, "A4", Table.USERS));
            collection.add(new Attribute(Name.FNAME, "Karim", Table.USERS));
            collection.add(new Attribute(Name.LNAME, "Benzema", Table.USERS));
            collection.add(new Attribute(Name.PHONE_NUMBER, "0554321234", Table.USERS));
            collection.add(new Attribute(Name.EMAIL_ADDRESS, "karim@benzema.edu", Table.USERS));
            collection.add(new Attribute(Name.ROLE_ID, "PT", Table.USERS));

            result = DB.insert(Table.USERS, collection);
            System.out.println("DB.insert(Table.USERS, collection);");
            if (result.noErrors()) {
                System.out.println(result.getRowsAffected());
                printTable(DB.retrieve(Table.USERS).getResult());
            }
            else{
                Name[] values = {Name.USER_ID,Name.FNAME,Name.LNAME, Name.PHONE_NUMBER, Name.EMAIL_ADDRESS,
                        Name.ROLE_ID};
                 for(Name att : values){
                    ArrayList<String> errors = result.getErrorByAttribute(new Attribute(att,Table.USERS));
                    for(String error : errors){
                        System.out.println(error);
                    }
                 }
            }

            System.out.println();
            System.out.println("Type anything to continue");
            ins.nextLine();
            System.out.println();

            //Now let's try deleting that same entry
            //But first we must specify that we want to delete only the row that has USER_ID = 'A40'
            filters.clear();
            filters.addEqual(new Attribute(Name.USER_ID, "A40", Table.USERS));
            result = DB.delete(Table.USERS, filters);
            System.out.println("DB.delete(Table.USERS, collection);");
            if (result.noErrors()) {
                System.out.println(result.getRowsAffected());
                printTable(DB.retrieve(Table.USERS).getResult());
            }

            System.out.println();
            System.out.println("Type anything to continue");
            ins.nextLine();
            System.out.println();

            //Finally, let's try updating the table where LNAME = 'Kareem' is replaced with 'Montasir'
            //True is there to signal that we want to cascade this change in LNAME to the tables referencing it
            filters.clear();
            filters.addEqual(new Attribute(Name.LNAME, "Kareem", Table.USERS));
            collection.clear();
            collection.add(new Attribute(Name.LNAME, "Montasir", Table.USERS));
            result = DB.modify(Table.USERS, filters, collection, true);

            if (result.noErrors()) {
                System.out.println(result.getRowsAffected());
                printTable(DB.retrieve(Table.USERS).getResult());
            }

            System.out.println();
            System.out.println("Type anything to continue");
            ins.nextLine();
            System.out.println();

            //Finally, Let's change it back to Kareem cuz why not
            filters.clear();
            filters.addEqual(new Attribute(Name.LNAME, "Montasir", Table.USERS));
            collection.clear();
            collection.add(new Attribute(Name.LNAME, "Kareem", Table.USERS));
            result = DB.modify(Table.USERS, filters, collection, true);
            if (result.noErrors()) {
                System.out.println(result.getRowsAffected());
                printTable(DB.retrieve(Table.USERS).getResult());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void printTable(ResultSet table) throws SQLException {
//        System.out.println();

        // Printing column headers first
        ResultSetMetaData meta = table.getMetaData();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            System.out.printf("%-22s", meta.getColumnName(i));
        }
        System.out.println();

        // Printing table's tuples
        while (table.next())
            printRow(table);
    }

    private static void printRow(ResultSet table) throws SQLException {

        for (int i = 1; i <= table.getMetaData().getColumnCount(); i++) {

            // Special processing for date objects to display only the date without the time
            if (table.getMetaData().getColumnName(i).equals("HIREDATE")) {
                System.out.printf("%-22s", formatDate(table, i));
            } else
                System.out.printf("%-22s", table.getString(i));
        }
        System.out.println();
    }

    private static String formatDate(ResultSet row, int column) throws SQLException {
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            return dateFormat.format(row.getDate(column));
        } catch (
                NullPointerException e) {
            return "null";
        }
    }

}
