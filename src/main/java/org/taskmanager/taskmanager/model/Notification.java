package org.taskmanager.taskmanager.model;

/**
 * This Class represents the Notifications of the application.
 * Notifications are essentially a message that will be shown on
 * specific date. Along with it, the details of the task it corresponds
 * to will be shown.
 * The Class has 3 parameters, the id, the message and the date.
 * There are 2 Constructors, one for creating a new Notification
 * and one for adding one from the JSON file (which has already
 * been given an id).
 * There are 6 more methods, 3 getters and 3 setters, one for
 * each of the parameters of the class.
 */
public class Notification {

    private int notificationId;
    private String message;
    private String notificationDate;

    /*
        ID numbering starts from 0, and it goes up one
        value per Notification, it is static. The id in
        Notifications is used solely for them to be unique.
     */
    private static int idNum = 0;

    /**
     * Constructor for a new Notification that the User can
     * add to the application.
     * @param message The message that will be shown when the
     *                notification appears to the user.
     * @param notificationDate The date when the Notification
     *                         will appear.
     */
    public  Notification(String message, String notificationDate) {
        this.notificationId = idNum++;
        this.message = message;
        this.notificationDate = notificationDate;
    }

    /**
     * Constructor used by the TaskJsonUtils to recreate tasks and notifications
     * upon loading the application, created in the previous runs of the application.
     * idNum changes according to the highest value of id found.
     * @param notificationId The id that was provided to the Notification.
     * @param message The message that will appear when the Notification is shown.
     * @param notificationDate The date when the Notification will appear.
     */
    public  Notification(int notificationId, String message, String notificationDate) {
        this.notificationId = notificationId;
        this.message = message;
        this.notificationDate = notificationDate;

        if (idNum < notificationId + 1) {
            idNum = notificationId + 1;}
    }

    /**
     * Method that returns the Notification id.
     * @return The id of the Notification.
     */
    public int getNotificationId() {
        return this.notificationId;
    }

    /**
     * Method that returns the message of the Notification.
     * @return The message of the Notification.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Method that returns the date of the Notification.
     * @return The date when the notification will appear.
     */
    public String getNotificationDate() {
        return this.notificationDate;
    }

    /**
     * Method that sets a new value for the Notification id, never used.
     * @param notificationId The new id for the Notification.
     */
    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * Method that sets a new value for the Notification Message.
     * @param message The new message for the notification.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Method that sets a new value for the Notification date.
     * @param notificationDate The new date for when the Notification will appear.
     */
    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }
}
