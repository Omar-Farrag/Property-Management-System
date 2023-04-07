package Notifications;

import DatabaseManagement.*;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Exceptions.InvalidAttributeValueException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Notifications Manager implements the NotificationManagement interface. An
 * instance of this class can be created whenever operations involving
 * notifications are made.
 */
public class NotificationsManager implements NotificationManagement {

    @Override
    public int notifyUser(String receiverID, Notification notification) {
        AttributeCollection collection = new AttributeCollection();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        String formattedDate = notification.getDateSent().format(formatter);

        collection.add(new Attribute(Name.SENDER_ID, notification.getSenderID(), Table.NOTIFICATIONS));
        collection.add(new Attribute(Name.RECEIVER_ID,receiverID, Table.NOTIFICATIONS));
        collection.add(new Attribute(Name.DATE_SENT,formattedDate, Table.NOTIFICATIONS));
        collection.add(new Attribute(Name.MESSAGE,notification.getMessage(), Table.NOTIFICATIONS));

        try {
            QueryResult result = DatabaseManager.getInstance().insert(Table.NOTIFICATIONS,collection);
            return result.getRowsAffected();
        } catch (SQLException | DBManagementException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public ArrayList<Notification> retrieveNotifications(String userID) throws SQLException, DBManagementException {
        Filters filters = new Filters();
        Attribute toFilterBy = new Attribute(Name.RECEIVER_ID, userID,Table.NOTIFICATIONS);
        filters.addEqual(toFilterBy);

        AttributeCollection collection = new AttributeCollection();
        collection.add(new Attribute(Name.SENDER_ID, Table.NOTIFICATIONS));
        collection.add(new Attribute(Name.DATE_SENT, Table.NOTIFICATIONS));
        collection.add(new Attribute(Name.MESSAGE, Table.NOTIFICATIONS));
        collection.add(new Attribute(Name.FNAME, Table.USERS));
        collection.add(new Attribute(Name.LNAME, Table.USERS));

       QueryResult result = DatabaseManager.getInstance().retrieve(collection,filters);

       ResultSet rs = result.getResult();
       ArrayList<Notification> notifications = new ArrayList<>();

       while(rs.next()){
           String senderID = rs.getString(Name.SENDER_ID.getName());
           String senderName = rs.getString(Name.FNAME.getName()) + " " + rs.getString(Name.LNAME.getName());

           String message = rs.getString(Name.MESSAGE.getName());
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
           LocalDateTime date = LocalDateTime.parse(rs.getString(Name.DATE_SENT.getName()),formatter);
           notifications.add(new Notification(senderID,date,message,senderName));

       }
       return notifications;

    }


    @Override
    public int dismissNotification(Notification notification) {
        /*
         * Query notifications table in database for the record having the same sender
         * and date as in the given notification
         * 
         * Delete that record
         * 
         * Return 1 if deletion is successful, 0 otherwise
         */
        throw new UnsupportedOperationException("Unimplemented method 'dismissNotification'");
    }
    public static void main(String[] args) {
        Notification notif = new Notification("A1", LocalDateTime.now(), Notification.NotifTopic.STATUS_UPDATE,
                "THIRD'S A CHARM");
        NotificationsManager manager = new NotificationsManager();
        int result = manager.notifyUser("A2",notif);
        System.out.println(result);

        try {
            ArrayList<Notification> notifications = manager.retrieveNotifications("A2");
            for(Notification notification : notifications){
                System.out.println(notification.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DBManagementException e) {
            throw new RuntimeException(e);
        }
    }
}
