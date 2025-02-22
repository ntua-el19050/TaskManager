package org.taskmanager.taskmanager.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import org.taskmanager.taskmanager.model.*;
import org.taskmanager.taskmanager.repository.TaskRepository;
import org.taskmanager.taskmanager.utils.TaskStateUtils;

/**
 * This is a class that controls the notification Management window of the
 * application. It has methods for initialization, addition, update, deletion.
 * Furthermore, it has 4 more helpful methods for clear, presets, updates and alerts as
 * the other controllers, but also 5 more methods it requires for better usage and updates
 * of the notifications.
 */
public class NotificationController {

    // Showing elements, the task deadline.
    @FXML private Label deadlineLabel;
    @FXML private Label notificationCount;

    // The input elements of the Notification management window.
    @FXML private TextField notificationField;
    @FXML private ComboBox<String> presetComboBox;
    @FXML private DatePicker customDatePicker;

    // The table elements of the Notification management window.
    @FXML private TableView<Notification> notificationTable;
    @FXML private TableColumn<Notification, String> messageColumn;
    @FXML private TableColumn<Notification, String> dateColumn;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Stage notificationStage;

    // Controllers and repositories needed.
    private MediaLabController mediaLabController;
    private TaskRepository taskRepository;

    // The task to which the notification management window belongs to.
    private Task task;

    /**
     * The constructor of the NotificationController which is never used or called.
     */
    public NotificationController() {}

    /**
     * This method initializes the notification management window. It gets the variables it needs,
     * sets the notification table, adds a listener for selected notifications, handles the notification
     * date selection, sets and shows the deadline of the task to the window, checks and removes notifications
     * from the preset combo box and updates the notification table.
     * @param task The task to which the notification window belongs to.
     * @param stage The stage of the notification window.
     * @param mediaLabController The main window controller.
     * @param taskRepository The task repository, called for addition, update and deletion.
     */
    public void initialize(Task task,Stage stage, MediaLabController mediaLabController, TaskRepository taskRepository) {

        // Set the variables.
        this.task = task;
        this.mediaLabController = mediaLabController;
        this.notificationStage = stage;
        this.taskRepository = taskRepository;

        // Set the notification table cells.
        messageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMessage()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNotificationDate()));

        // Add a listener to the table.
        notificationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                presetInputs(newSelection);
            } else {
                clearInputs();
            }
        });

        presetComboBox.setValue("Any");
        customDatePicker.setValue(LocalDate.now().plusDays(1));

        // Set the task deadline to be shown.
        String deadline = task.getDeadline();
        deadlineLabel.setText(deadline);

        // Check what notifications it makes sense to show in the drop-down menu and then update the notification table.
        checkAndRemoveNotifications();
        updateNotificationTable();
        customDatePicker.setDisable(false);
    }

    /**
     * This method adds a notification to the notifications list of its task.
     * For the deadline it checks the presetComboBox and sets it properly, and
     * it checks that the date makes sense. Then it adds the Notification
     * to its task through the task repository. Finally, it
     * calls for an update to occur to the application.
     */
    @FXML
    private void addNotification() {

        String selectedPreset = presetComboBox.getValue();
        String message = notificationField.getText().trim();
        LocalDate notificationDate = LocalDate.now();

        // Use the proper date for the notification date based on what the user selected.
        if (selectedPreset.equals("Any")) {
            notificationDate = customDatePicker.getValue();
        }
        else {
            switch (selectedPreset) {
                case "1 Day Before": {
                    notificationDate = LocalDate.parse(task.getDeadline(), formatter).minusDays(1);
                    break;
                }
                case "1 Week Before": {
                    notificationDate = LocalDate.parse(task.getDeadline(), formatter).minusWeeks(1);
                    break;
                }
                case "1 Month Before":
                    notificationDate = LocalDate.parse(task.getDeadline(), formatter).minusMonths(1);
                    break;
            }
        }

        // Check if the notification date makes sense based on the deadline and the current date.
        if (notificationDate.isAfter(LocalDate.parse(task.getDeadline(), formatter)) || notificationDate.isBefore(LocalDate.now())) {
            showAlert("Wrong Notification Date","Please choose a date before or up to the Task Deadline");
            clearInputs();
            return;
        }

        // Add the notification to its task and update the application.
        taskRepository.addNotification(task, message, notificationDate.format(formatter));
        update();
    }

    /**
     * Update a selected notification
     */
    @FXML
    private void updateNotification(){

        Notification selectedNotification = notificationTable.getSelectionModel().getSelectedItem();

        if (selectedNotification != null) {

            String selectedPreset = presetComboBox.getValue();
            String message = notificationField.getText().trim();
            LocalDate notificationDate = LocalDate.now();

            // Use the proper date for the notification date based on what the user selected.
            if (selectedPreset.equals("Any")) {
                notificationDate = customDatePicker.getValue();
            } else {
                switch (selectedPreset) {
                    case "1 Day Before": {
                        notificationDate = LocalDate.parse(task.getDeadline(), formatter).minusDays(1);
                        break;
                    }
                    case "1 Week Before": {
                        notificationDate = LocalDate.parse(task.getDeadline(), formatter).minusWeeks(1);
                        break;
                    }
                    case "1 Month Before":
                        notificationDate = LocalDate.parse(task.getDeadline(), formatter).minusMonths(1);
                        break;
                }
            }

            // Check if the notification date makes sense based on the deadline and the current date.
            if (notificationDate.isAfter(LocalDate.parse(task.getDeadline(), formatter)) || notificationDate.isBefore(LocalDate.now())) {
                showAlert("Wrong Notification Date","Please choose a date before or up to the Task Deadline");
                clearInputs();
                return;
            }

            // Update the notification through the task repository and update the application.
            taskRepository.updateNotification(task, selectedNotification, message, notificationDate.format(formatter));
            update();
        }
        else {
            showAlert("Select a Notification", "You need to select a notification before updating it.");
        }
    }

    /**
     * This method deletes a notification from its task's notification list.
     */
    @FXML
    private void deleteNotification(){

        Notification selectedNotification = notificationTable.getSelectionModel().getSelectedItem();

        if (selectedNotification != null) {
            taskRepository.deleteNotification(task, selectedNotification);
            update();
        }
        else {
            showAlert("Select a Notification", "You need to select a notification to delete.");
        }
    }

    /**
     * This method checks whether the preset selection drop-down menu is set
     * to "Any" or not. If it is not, then the customDatePicker is disabled.
     */
    @FXML
    private void handlePresetSelection() {
        String selectedPreset = presetComboBox.getValue();
        customDatePicker.setDisable(!"Any".equals(selectedPreset));
    }

    /**
     * This method updates the notification table.
     */
    private void updateNotificationTable(){
        notificationTable.getItems().clear();
        notificationTable.setItems(FXCollections.observableArrayList(task.getNotifications()));
        notificationCount.setText(String.valueOf(task.getNotifications().size()));
        notificationTable.refresh();
    }

    /**
     * This method updates the deadline shown in the notification management window.
     * Based on the new deadline it also updates the rest of the window and the application.
     * @param deadline The deadline of the task to which this notification window
     *                 belongs to.
     */
    public void updateDeadline(String deadline) {
        deadlineLabel.setText(deadline);
        update();
    }

    /**
     * This method checks which notifications it makes sense for the preset combo box to show.
     * It is called after every addition and update of the notifications.
     */
    private void checkAndRemoveNotifications() {

        ObservableList<String> items = FXCollections.observableArrayList(
                "1 Day Before", "1 Week Before", "1 Month Before", "Any"
        );

        LocalDate oneDay = LocalDate.parse(task.getDeadline(), formatter).minusDays(1);
        LocalDate oneWeek = LocalDate.parse(task.getDeadline(), formatter).minusWeeks(1);
        LocalDate oneMonth = LocalDate.parse(task.getDeadline(), formatter).minusMonths(1);

        // Check one by one which notifications don't exist and make sense to show.
        if (notificationExists(oneDay.format(formatter)) || oneDay.isBefore(LocalDate.now())) {
            items.remove("1 Day Before");
        }
        if (notificationExists(oneWeek.format(formatter)) || oneWeek.isBefore(LocalDate.now())) {
            items.remove("1 Week Before");
        }
        if (notificationExists(oneMonth.format(formatter)) ||  oneMonth.isBefore(LocalDate.now())) {
            items.remove("1 Month Before");
        }

        presetComboBox.setItems(items);
    }

    /**
     * Checks whether a notification already exists.
     * @param notificationDate The date of the notification we want to see if it exists.
     * @return True if the notification date exists already, False otherwise.
     */
    private boolean notificationExists(String notificationDate) {
        for (Notification notification : task.getNotifications()) {
            if (notification.getNotificationDate().equals(notificationDate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method is used to make sure the update of deadline happens to the proper notification controller
     * @param task The task that is selected to be updated.
     * @return True if it is the same task, False otherwise.
     */
    public boolean sameTask(Task task) {
        return this.task.getTaskId() == task.getTaskId();
    }

    /**
     * Clear the FXML input elements. Preset of custom date to 1 day after
     * the current date so that it can exist. Then refresh the notification table.
     */
    private void clearInputs() {
        notificationField.clear();
        presetComboBox.getSelectionModel().clearSelection();
        presetComboBox.setValue("Any");
        customDatePicker.setValue(LocalDate.now().plusDays(1));
        notificationTable.refresh();
    }

    /**
     * Preset the input elements to the ones of the selected notification.
     * @param newSelection The notification last selected from the table.
     */
    private void presetInputs(Notification newSelection) {
        notificationField.setText(newSelection.getMessage());
        presetComboBox.getSelectionModel().clearSelection();
        presetComboBox.setValue("Any");
        customDatePicker.setValue(LocalDate.parse(newSelection.getNotificationDate(), formatter));
    }

    /**
     * This method makes the necessary updates to the notification management window.
     * It updates the notification table, checks and removes the notifications that do not make sense
     * after the update, clears the input elements, calls the mediaLab controller to update and,
     * in the case that a task was set to completed or delayed, this window closes.
     */
    private void update(){
        updateNotificationTable();
        checkAndRemoveNotifications();
        clearInputs();
        mediaLabController.updateMediaLabTables();

        // If the task state is set to completed or delayed, close the window.
        if (task.getState() == TaskStateUtils.COMPLETED || task.getState() == TaskStateUtils.DELAYED) {
            closeNotificationWindow();
        }
    }

    /**
     * Used to show an alert to the user.
     * @param title The title of the alert notification.
     * @param message The message of the alert notification.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * This method closes the notification window.
     */
    protected void closeNotificationWindow(){
        notificationStage.close();
    }
}
