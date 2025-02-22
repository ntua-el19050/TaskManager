package org.taskmanager.taskmanager.model;

/**
 * This is a Class that assists us in showing the notification table
 * in the MediaLabAssistant window and was created for ease of usage.
 * It essentially engulfs each Notification along with the id and the name of
 * the Task the Notification is corresponding to. No other information
 * was included as it was not considered essential.
 * It includes one constructor and 6 getters only, as setters were not required for
 * this Class. The getters return the Notification, the Task id or the Task Name, but 3 more
 * getters that return the NotificationId, the Notification message and the
 * Notification Date separately were included, since this was the information
 * we require from this class.
 */
public class NotificationWrapper{

    private final Notification notification;
    private final int taskId;
    private final String taskName;

    /**
     * Constructor for the NotificationWrapper.
     * @param notification The Notification to which the wrapper belongs.
     * @param taskId The id of the Task that the Notification belongs to.
     * @param taskName The name of the Task that the Notification belongs to.
     */
    public NotificationWrapper(Notification notification,int taskId, String taskName) {
        this.notification = notification;
        this.taskId = taskId;
        this.taskName = taskName;
    }

    /**
     * Returns the notification object.
     * @return The Notification of this Notification Wrapper.
     */
    public Notification getNotification() {
        return notification;
    }

    /**
     * Returns the notification id.
     * @return The id of the notification.
     */
    public int getNotificationId(){
        return notification.getNotificationId();
    }

    /**
     * Returns the message of the Notification.
     * @return The message of the Notification.
     */
    public String getNotificationMessage(){
        return notification.getMessage();
    }

    /**
     * Returns the date of the notification.
     * @return The Date the notification will show a reminder.
     */
    public String getNotificationDate(){
        return notification.getNotificationDate();
    }

    /**
     * Returns the id of the task to which the Notification belongs.
     * @return The id of the Task to which the Notification belongs.
     */
    public int getTaskId(){return taskId;}

    /**
     * Returns the name of the task to which the notification belongs
     * @return String The name of the Task to which the notification belongs.
     */
    public String getTaskName() {
        return taskName;
    }
}

