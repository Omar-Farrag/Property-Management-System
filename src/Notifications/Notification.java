package Notifications;

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
    private String senderID;
    private String dateSent;
    private NotifTopic topic;
    private String message;

    public Notification(String senderID, String dateSent, NotifTopic topic, String message) {
        this.senderID = senderID;
        this.dateSent = dateSent;
        this.topic = topic;
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public NotifTopic getTopic() {
        return topic;
    }

    public String getMessage() {
        return message;
    }

    public String getDateSent() {
        return dateSent;
    }

}
