package Notifications;

import DatabaseManagement.*;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Exceptions.InvalidAttributeValueException;
import General.Controller;

import javax.xml.crypto.Data;
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

    private Controller controller = new Controller();

    @Override
    public int notifyUser(String receiverID, Notification notification) {
        AttributeCollection collection = new AttributeCollection();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm:ss a");
        String formattedDate = notification.getDateSent().format(formatter);

        formattedDate = controller.getTimestamp(formattedDate);
        collection.add(new Attribute(Name.SENDER_ID, notification.getSenderID(), Table.NOTIFICATIONS));
        collection.add(new Attribute(Name.RECEIVER_ID, receiverID, Table.NOTIFICATIONS));
        collection.add(new Attribute(Name.DATE_SENT, formattedDate, Table.NOTIFICATIONS));
        collection.add(new Attribute(Name.MESSAGE, notification.getMessage(), Table.NOTIFICATIONS));

        QueryResult result = controller.insert(Table.NOTIFICATIONS, collection, true);
        return result.getRowsAffected();
    }

    @Override
    public ArrayList<Notification> retrieveNotifications(String userID) throws SQLException, DBManagementException {
        Filters filters = new Filters();
        Attribute toFilterBy = new Attribute(Name.RECEIVER_ID, userID, Table.NOTIFICATIONS);
        filters.addEqual(toFilterBy);

        AttributeCollection collection = new AttributeCollection();
        collection.add(new Attribute(Name.SENDER_ID, Table.NOTIFICATIONS));
        collection.add(new Attribute(Name.DATE_SENT, Table.NOTIFICATIONS));
        collection.add(new Attribute(Name.MESSAGE, Table.NOTIFICATIONS));
        collection.add(new Attribute(Name.FNAME, Table.USERS));
        collection.add(new Attribute(Name.LNAME, Table.USERS));

        QueryResult result = controller.retrieve(collection, filters);

        ResultSet rs = result.getResult();
        ArrayList<Notification> notifications = new ArrayList<>();

        while (rs.next()) {
            String senderID = rs.getString(Name.SENDER_ID.getName());
            String senderName = rs.getString(Name.FNAME.getName()) + " " + rs.getString(Name.LNAME.getName());

            String message = rs.getString(Name.MESSAGE.getName());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime date = LocalDateTime.parse(rs.getString(Name.DATE_SENT.getName()), formatter);
            notifications.add(new Notification(senderID, date, message, senderName));

        }
        return notifications;

    }

    @Override
    public int dismissNotification(Notification notification) {

        Filters filters = new Filters();
        String date = notification.getDateSent().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm:ss a"));
        filters.addEqual(new Attribute(Name.SENDER_ID, notification.getSenderID(), Table.NOTIFICATIONS));
        filters.addEqual(new Attribute(Name.DATE_SENT, date, Table.NOTIFICATIONS));
        QueryResult result = controller.delete(Table.NOTIFICATIONS, filters, false);
        return result.getRowsAffected();
    }

    public static void main(String[] args) {
//        Notification notif = new Notification("A1", LocalDateTime.now(), Notification.NotifTopic.STATUS_UPDATE,
//                "FOURTH'S A CHARM");
        NotificationsManager manager = new NotificationsManager();
        //        int result = manager.notifyUser("A2",notif);
        //        System.out.println(result);
        ;

        try {
            ArrayList<Notification> notifications = manager.retrieveNotifications("A2");
            Notification notif = null;
            for (Notification notification : notifications) {
                if (notification.getMessage().equals("STATUS UPDATE: FOURTH'S A CHARM")) {
                    notif = notification;
                }
                System.out.println(notification.toString());
                System.out.println();
            }
            System.out.println();

            int result = manager.dismissNotification(notif);

            System.out.println(result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DBManagementException e) {
            throw new RuntimeException(e);
        }
    }
}
