package org.taskmanager.taskmanager.controller;

import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import org.taskmanager.taskmanager.model.*;
import org.taskmanager.taskmanager.repository.*;

/**
 * This class is used to control the delayedTask pop up window.
 * It has 3 methods, one for initializing, one for loading tasks,
 * only called in the initialize method, and one for closing.
 */
public class DelayedTaskPopUpController {

    // Elements used for displaying the tasks and their count on the window.
    @FXML private ListView<String> delayedTasksView;
    @FXML private Label delayedTasksCount;

    // Elements used to help with the display elements.
    private final ObservableList<String> delayedTasks = FXCollections.observableArrayList();

    /**
     * The constructor of the DelayedTaskPopUpController which is never used or called.
     */
    public DelayedTaskPopUpController(){}

    /**
     * This method initializes the controller, calls the loadDelayedTasks method
     * and sets properly the FXML elements.
     * @param taskRepository The task repository of the application, used to get all tasks.
     * @param categoryRepository The category repository of the application,
     *                           used to get the category map to translate id to name.
     * @param priorityRepository The priority repository of the application,
     *                           used to get the priority map to translate id to level.
     */
    public void initialize(TaskRepository taskRepository, CategoryRepository categoryRepository, PriorityRepository priorityRepository) {

        // Load delayed tasks and count how many there are.
        int delayedTasksCountInt = loadDelayedTasks(
                taskRepository.findAll(),
                categoryRepository.getCategoryMapReverse(),
                priorityRepository.getPriorityMapReverse()
        );

        // Set the display elements properly.
        delayedTasksCount.setText(String.valueOf(delayedTasksCountInt));
        delayedTasksView.setItems(delayedTasks);
    }

    /**
     * This method contains the logic of creating the message that will appear per delayed task
     * to the user and counts how many delayed tasks there were.
     * @param tasks All the tasks from the task repository.
     * @param categoryMapReverse The map to translate the category ID to the category name.
     * @param priorityMapReverse The map to translate the priority ID to the priority level/name.
     * @return The number of delayed tasks that were found.
     */
    private int loadDelayedTasks(List<Task> tasks, Map<Integer, String> categoryMapReverse, Map<Integer, String> priorityMapReverse) {

        int delayedTasksCountInt = 0;
        delayedTasks.clear();

        // Go to every task and if it is delayed create a message of it to be displayed.
        for (Task task : tasks) {
            if (task.isDelayed()) {

                StringBuilder taskString = new StringBuilder();

                // Add all information to a StringBuilder.
                taskString.append("Task: ").append(task.getName()).append("\n")
                        .append("Description: ").append(task.getDescription()).append("\n")
                        .append("Deadline: ").append(task.getDeadline()).append("\n")
                        .append("Category: ").append(categoryMapReverse.get(task.getCategoryId()))
                        .append(" | Priority: ").append(priorityMapReverse.get(task.getPriorityId())).append("\n")
                        .append("----------------------------------\n");

                // If there are notifications show them as well, if not write there are no notifications.
                if (!task.getNotifications().isEmpty()) {
                    taskString.append("🔔 Notifications:\n");
                    for (Notification notification : task.getNotifications()) {
                        taskString.append("   - ").append(notification.getMessage()).append("\n")
                                .append("     📅 Notification Date: ").append(notification.getNotificationDate()).append("\n");
                    }
                } else {
                    taskString.append("🔔 No notifications.\n");
                }

                String result = taskString.toString();
                delayedTasks.add(result);
                delayedTasksCountInt++;
            }
        }

        return delayedTasksCountInt;
    }

    /**
     * Called when the "OK" button is pressed on the window and closes the window.
     */
    @FXML
    private void onClose() {
        Stage stage = (Stage) delayedTasksView.getScene().getWindow();
        stage.close();
    }

}
