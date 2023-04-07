package Notifications;

import DatabaseManagement.*;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.Exceptions.DBManagementException;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * An encapsulation of all of the notification's relevant information.
 * Represents notifications that are shared between users on the application for
 * events such as late payments, application status updates, etc.
 * 
 * @Attributes senderID - ID of individual who sent the notification
 *             <li>dateSent - Exact date the notificaiton was sent</li>
 *             <li>topic - Overall description of what the notification is
 *             about</li>
 *             <li>message - Detailed message for the notification</li>
 */
public class Notification {
    private final String senderID;
    private String senderName;
    private final LocalDateTime dateSent;
    private final String message;

    public Notification(String senderID, LocalDateTime dateSent, NotifTopic topic, String message) {
        this.senderID = senderID;
        this.senderName=  "";
        this.dateSent = dateSent;
        this.message = topic.getTopic() + ": " + message.replace("'","''") ;
    }

    public Notification(String senderID, LocalDateTime dateSent, String message,String senderName) {
        this.senderID = senderID;
        this.dateSent = dateSent;
        this.message =  message ;
        this.senderName = senderName;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateSent() {
        return dateSent;
    }

    @Override
    public String toString() {
        return "FROM: " + senderName
                + "\nDate Sent: " + DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss").format(dateSent)
                + "\nMessage: " + message;
    }

    /**
     * The NotifTopic enum class contains a list of standard notification topics to
     * be used when sending notifications to other users. Each NotifTopic instance
     * represents a specific matching string that will be used when crafting
     * notification messages. This ensures that all message topics are uniformly
     * throughout the application
     */
    public enum NotifTopic {

        // An example
        STATUS_UPDATE("STATUS UPDATE");

        private String topic;

        public String getTopic() {
            return topic;
        }

        private NotifTopic(String topic) {
            this.topic = topic;
        }

    }


}
