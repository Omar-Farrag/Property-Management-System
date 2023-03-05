package Notifications;

import java.util.ArrayList;

/**
 * Notifications Manager implements the NotificationManagement interface. An
 * instance of this class can be created whenever operations involving
 * notifications are made.
 */
public class NotificationsManager implements NotificationManagement {

    @Override
    public int notifyUser(String receiverID, Notification notification) {
        /*
         * Notification contains all necessary information
         * 
         * Just insert into the Notifications table in the database an entry with the
         * following order
         * 
         * <Sender_ID,Receiver_ID, DateSent, Messgae>
         * 
         * Return 1 if insertion successful, 0 otherwise
         */
        throw new UnsupportedOperationException("Unimplemented method 'notifyUser'");
    }

    @Override
    public ArrayList<Notification> retrieveNotifications(String userID) {
        /*
         * Query database for all records in Notifications table with a receiverId =
         * given user ID
         * 
         * Create a notification object for each record and add it to an arraylist
         * 
         * Return the arraylist of notifications
         */
        throw new UnsupportedOperationException("Unimplemented method 'retrieveNotifications'");
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

}
