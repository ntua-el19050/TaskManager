package org.taskmanager.taskmanager.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import org.taskmanager.taskmanager.model.*;
import org.taskmanager.taskmanager.repository.CategoryRepository;
import org.taskmanager.taskmanager.repository.PriorityRepository;
import org.taskmanager.taskmanager.repository.TaskRepository;

/**
 * This class is used to control the notifications pop up window.
 * It has 3 methods, one for initializing, one for loading notifications,
 * only called in the initialize method, and one for closing.
 */
public class NotificationDisplayController {

    // Elements used for displaying the notifications on the window.
    @FXML private ListView<Notification> notificationListView;
    private final ObservableList<Notification> notifications = FXCollections.observableArrayList();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * The constructor of the NotificationDisplayController which is never used or called.
     */
    public NotificationDisplayController() {}

    /**
     * This method initializes the controller, calls the loadNotifications method
     * and sets properly the FXML elements.It also builds what it will show, which
     * is only the message of the notification as it is remade in the loadNotifications method.
     * @param taskRepository The task repository of the application, used to get all tasks and
     *                       by extend the notifications.
     * @param categoryRepository The category repository of the application,
     *                           used to get the category map to translate id to name.
     * @param priorityRepository The priority repository of the application,
     *                           used to get the priority map to translate id to level.
     */
    public void initialize(TaskRepository taskRepository, CategoryRepository categoryRepository, PriorityRepository priorityRepository) {

        // Load all notifications.
        loadAllNotifications(taskRepository.findAll(), categoryRepository.getCategoryMapReverse(), priorityRepository.getPriorityMapReverse());

        // Properly build the list view so that it shows only the notification's remade message, if it is not empty.
        notificationListView.setCellFactory(param -> new javafx.scene.control.ListCell<Notification>() {

            // Override the updateItem so that it only shows the notification message properly.
            @Override
            protected void updateItem(Notification notification, boolean empty) {
                super.updateItem(notification, empty);
                if (empty || notification == null) {
                    setText(null);
                } else {
                    setText(notification.getMessage());
                }
            }
        });

        // Set the notification list view with the notifications.
        notificationListView.setItems(notifications);
    }

    /**
     * This method contains the logic of creating the message that
     * will appear for each notification to the user.
     * @param tasks All the tasks from the task repository.
     * @param categoryMapReverse The map to translate the category ID to the category name.
     * @param priorityMapReverse The map to translate the priority ID to the priority level/name.
     */
    private void loadAllNotifications(List<Task> tasks, Map<Integer, String> categoryMapReverse, Map<Integer, String> priorityMapReverse) {

        notifications.clear();

        List<Notification> toDisplay = new ArrayList<>();

        // Go to every task and every notification
        for (Task task : tasks) {

            List<Notification> taskNotifications = task.getNotifications();
            // After the notifications are displayed they will be removed as they are no longer needed.
            List<Notification> toRemove = new ArrayList<>();

            for (Notification notification : taskNotifications) {

                LocalDate notificationDate = LocalDate.parse(notification.getNotificationDate(), formatter);

                // Show the notifications that are either for today or were supposed to be shown a previous day.
                if (notificationDate.isBefore(LocalDate.now()) || notificationDate.isEqual(LocalDate.now())) {

                    // Remake the notification message to show the user.
                    notification.setMessage(
                            "Task: " + task.getName() + "\n" +
                                    "Description: " + task.getDescription() + "\n" +
                                    "Deadline: " + task.getDeadline() + "\n" +
                                    "Category: " + categoryMapReverse.get(task.getCategoryId()) + " | Priority: " + priorityMapReverse.get(task.getPriorityId()) + "\n" +
                                    "----------------------------------\n" +
                                    "🔔 Notification: " + notification.getMessage() + "\n" +
                                    "📅 Notification Date: " + notification.getNotificationDate()
                    );

                    toDisplay.add(notification);
                    toRemove.add(notification);
                }
            }

            // Remove the notifications of this task that were already shown.
            taskNotifications.removeAll(toRemove);
        }

        // Set the notifications to be displayed
        notifications.setAll(toDisplay);
    }

    /**
     * Called when the "OK" button is pressed on the window and closes the window.
     */
    @FXML
    private void onClose() {
        Stage stage = (Stage) notificationListView.getScene().getWindow();
        stage.close();
    }
}
