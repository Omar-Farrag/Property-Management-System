package Notifications;

import DatabaseManagement.Exceptions.DBManagementException;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Declares a few functions for sending and retrieving notifications from the
 * database
 */

public interface NotificationManagement {

    /**
     * Sends a notification to a specific user
     * 
     * @param notification A notification object containing the details of the
     *                     notification to be sent
     * @param receiverID   The user ID of the notification's recipient
     * @return success code. (1 means the notification was added to given user's
     *         notifications and 0 means it wasn't)
     */
    public int notifyUser(String receiverID, Notification notification);

    /**
     * Retrieves all notifications sent to a specific user
     * 
     * @param userID The user ID whose notifications are to be retrieved
     * @return An arraylist of all the user's notifications
     */
    public ArrayList<Notification> retrieveNotifications(String userID) throws SQLException, DBManagementException;

    /**
     * Removes a given notification from a user's list of notifications
     * 
     * @param notification The notification that is to be dismissed
     * @return number of notifications deleted
     */
    public int dismissNotification(Notification notification);
}
