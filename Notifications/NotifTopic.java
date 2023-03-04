package Notifications;

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
