package org.taskmanager.taskmanager.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.taskmanager.taskmanager.model.Task;
import org.taskmanager.taskmanager.repository.CategoryRepository;
import org.taskmanager.taskmanager.repository.PriorityRepository;
import org.taskmanager.taskmanager.repository.TaskRepository;
import org.taskmanager.taskmanager.utils.TaskStateUtils;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * This class represents the controller for the task management window.
 * It is responsible for the communication with the task repository and for the addition,
 * update and deletion of tasks, as well as leads to the notification management window.
 * It also includes 2 more methods to open properly the notification management window and
 * also 7 helpful methods to properly update the task management window inputs and table.
 */
public class TaskController {

    // FXML input elements
    @FXML private TextField taskField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker deadlinePicker;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private ComboBox<TaskStateUtils> stateComboBox;

    // Task table elements
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, Integer> taskIDColumn;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> descriptionColumn;
    @FXML private TableColumn<Task, String> deadlineColumn;
    @FXML private TableColumn<Task, String> categoryColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, Void> actionsColumn;
    @FXML private TableColumn<Task, String> stateColumn;

    // The formatter turns a date from string to LocalDate and vice versa.
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // The repositories needed for the controller to work properly.
    private TaskRepository taskRepository;
    private CategoryRepository categoryRepository;
    private PriorityRepository priorityRepository;

    // The controllers needed for the controller to work properly.
    private MediaLabController mediaLabController;
    private final List<NotificationController> notificationControllers = new ArrayList<>();

    // All the stages in the app.
    private List<Stage> stageList;

    /**
     * The constructor of the TaskController which is never used or called.
     */
    public TaskController(){}

    /**
     * This method initializes the task controller.
     * It sets the repositories and controllers, as well as the maps,
     * then presets the category, priority and state drop-down menus
     * properly and prepares the task table. We have also included a
     * listener that presets the input elements
     * @param mediaLabController The main window controller that updates the whole app.
     * @param taskRepository The repository for all tasks.
     * @param categoryRepository The repository for all categories.
     * @param priorityRepository The repository for all priorities.
     * @param stageList All the stages in the application.
     */
    @FXML
    public void initialize(MediaLabController  mediaLabController, TaskRepository taskRepository, CategoryRepository categoryRepository, PriorityRepository priorityRepository, List<Stage> stageList) {

        // Set repositories and controllers.
        this.stageList = stageList;
        this.categoryRepository = categoryRepository;
        this.priorityRepository = priorityRepository;
        this.mediaLabController = mediaLabController;
        this.taskRepository = taskRepository;

        // Set category combo box and preset it to Uncategorized.
        categoryComboBox.setItems(FXCollections.observableArrayList(categoryRepository.getCategoryMap().keySet()));
        categoryComboBox.setValue("Uncategorized");

        // Set priority combo box and preset it to Default.
        priorityComboBox.setItems(FXCollections.observableArrayList(priorityRepository.getPriorityMap().keySet()));
        priorityComboBox.setValue("Default");

        // Set the state with all available options and preset it to open.
        ObservableList<TaskStateUtils> stateOptions = FXCollections.observableArrayList(
                TaskStateUtils.OPEN,
                TaskStateUtils.IN_PROGRESS,
                TaskStateUtils.POSTPONED,
                TaskStateUtils.COMPLETED);
        stateComboBox.setItems(FXCollections.observableArrayList(stateOptions));
        stateComboBox.setValue(TaskStateUtils.OPEN);

        // prepare the task table cells properly.
        taskIDColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTaskId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        deadlineColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeadline()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(categoryRepository.getCategoryMapReverse().get(cellData.getValue().getCategoryId())));
        priorityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(priorityRepository.getPriorityMapReverse().get(cellData.getValue().getPriorityId())));
        stateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getState().toString()));

        // Show a date which is 1 week after today.
        deadlinePicker.setValue(LocalDate.now().plusWeeks(1));
        // Adds the notification button to the task table.
        addNotificationButtonToTable();

        // Set the task table items and set the listener with the presets.
        taskTable.setItems(FXCollections.observableArrayList(taskRepository.findAll()));

        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                presetInputs(newSelection);
            } else {
                clearInputs();
            }
        });
    }

    /**
     * This method adds a task to the task repository after
     * making the proper checks (everything is selected and not
     * empty). Then it creates a new Task and sends it to the task
     * repository.
     */
    @FXML
    private void addTask() {

        // Check that category is not null.
        String selectedCategory = categoryComboBox.getValue();
        if (selectedCategory == null) {
            showAlert("Select a Category","Please select a category.");
            return;
        }

        // Check that priority is not null.
        String selectedPriority = priorityComboBox.getValue();
        if (selectedPriority == null) {
            showAlert("Select a Priority","Please select a priority.");
            return;
        }

        // Check that state it not null.
        TaskStateUtils selectedState = stateComboBox.getValue();
        if (selectedState.toString() == null) {
            showAlert("Select a State","Please select a state.");
            return;
        }

        // Check that the name is not null and that the date is not null or in the past.
        String taskName = taskField.getText().trim();
        String description = descriptionField.getText().trim();
        LocalDate deadline = deadlinePicker.getValue();
        if (taskName.isEmpty() || deadline == null) {
            showAlert("Error", "Please enter a task name and select a deadline.");
            return;
        }
        if (deadline.isBefore(LocalDate.now())) {
            showAlert("Error", "Please select a deadline after the current date.");
            return;
        }

        // Add a task to the repository.
        taskRepository.add(taskName, description, deadline.format(formatter), categoryRepository.getCategoryMap().get(selectedCategory), priorityRepository.getPriorityMap().get(selectedPriority), selectedState);

        update();
    }

    /**
     * This method updates an already existing task. Once a task is
     * selected then it only checks that the name and deadline make sense.
     */
    @FXML
    private void updateTask() {

        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();

        if (selectedTask != null) {

            String taskName = taskField.getText().trim();
            String description = descriptionField.getText().trim();
            LocalDate deadline = deadlinePicker.getValue();
            String selectedCategory = categoryComboBox.getValue();
            String selectedPriority = priorityComboBox.getValue();
            TaskStateUtils selectedState = stateComboBox.getValue();

            // Check if task or deadline are empty and whether the deadline makes sense.
            if (taskName.isEmpty() || deadline == null) {
                showAlert("Error", "Please enter a task name and select a deadline.");
                return;
            }

            if (deadline.isBefore(LocalDate.now())) {
                showAlert("Error", "Please select a deadline after the current date.");
                return;
            }

            // Update the task and the system.
            taskRepository.update(selectedTask, taskName, description, deadline.format(formatter), categoryRepository.getCategoryMap().get(selectedCategory), priorityRepository.getPriorityMap().get(selectedPriority), selectedState);
            update();

            // If there is a notification window , and it corresponds to this task,
            // update its deadline.
            for (NotificationController controller:notificationControllers){
                if (controller != null && controller.sameTask(selectedTask)) {
                    controller.updateDeadline(selectedTask.getDeadline());
                }
            }

        } else {
            showAlert("Warning", "Please select a task to update.");
        }
    }

    /**
     * This method calls the task repository to delete the selected task
     * from it. It also closes the window of the controller corresponding
     * to the deleted task.
     */
    @FXML
    private void deleteTask() {

        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();

        if (selectedTask != null) {

            // Call task repository to delete task based on its id.
            taskRepository.delete(selectedTask.getTaskId());
            update();

            // Close the notification window the task corresponds to.
            for (NotificationController controller:notificationControllers){
                if (controller != null && controller.sameTask(selectedTask)) {
                    controller.closeNotificationWindow();
                }
            }

        } else {
            showAlert("Warning", "Please select a task to delete.");
        }
    }

    /**
     * This method properly adds a notification button to each task in the
     * task table. Each button opens a notification management window
     * specifically for that task.
     */
    @FXML
    private void addNotificationButtonToTable() {

        actionsColumn.setCellFactory(column -> new TableCell<Task, Void>() {
            private final Button btn = new Button("Manage Notifications");

            {
                btn.setStyle("-fx-background-color: #64B5F6; -fx-text-fill: white; -fx-font-weight: bold;");

                btn.setOnAction(event -> {
                    // Get the task
                    Task task = getTableView().getItems().get(getIndex());
                    // Set the button as disabled if it exists and the task has the state of Completed
                    // or Delayed (not need for more tasks for completed, unable to set tasks after DL for delayed)
                    btn.setDisable(task != null && (task.getState() == TaskStateUtils.COMPLETED || task.getState() == TaskStateUtils.DELAYED));
                    openNotificationManager(task);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Task task = getTableView().getItems().get(getIndex());
                    if (task != null) {
                        // the task has the state of Completed or Delayed
                        // (not need for more tasks for completed, unable to set tasks after DL for delayed)
                        btn.setDisable(task.getState() == TaskStateUtils.COMPLETED || task.getState() == TaskStateUtils.DELAYED);
                    }
                    setGraphic(btn);
                }
            }
        });
    }

    /**
     * This method opens the notification management window and
     * updates the notification controllers list.
     * @param task The task to which this notification management window
     *             belongs to.
     */
    private void openNotificationManager(Task task) {

        try {

            // Load the FXML file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/taskmanager/taskmanager/views/NotificationManagement.fxml"));
            Parent root = loader.load();

            // Load the controller of the FXML file.
            NotificationController controller = loader.getController();
            notificationControllers.add(controller);

            // Create a stage, initialise it, add it to the stageList
            Stage stage = new Stage();
            controller.initialize(task, stage, this.mediaLabController, taskRepository);
            stageList.add(stage);

            // Set title, scene and styleSheet and then open the window.
            stage.setTitle("Manage Notifications");
            Scene scene = new Scene(root, 600, 600);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/org/taskmanager/taskmanager/style/NotificationManagement.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method updates the categories combo box as well as the
     * category maps. Then it resets the combo box and sets the
     * category to uncategorized if it didn't have anything else or the
     * previous category selected was deleted.
     */
    public void updateCategories() {

        categoryComboBox.getItems().clear();

        categoryComboBox.setItems(FXCollections.observableArrayList(categoryRepository.getCategoryMap().keySet()));
        categoryComboBox.getSelectionModel().clearSelection();

        // In case the category selected was deleted, set it to Uncategorized.
        if (!categoryRepository.getCategoryMap().containsKey(categoryComboBox.getValue())) {
            categoryComboBox.setValue("Uncategorized");
        }

        taskTable.refresh();
    }

    /**
     * This method updates the priorities combo box as well as the
     * priority maps. Then it resets the combo box and sets the
     * priority to Default if it didn't have anything else or the
     * previous priority selected was deleted.
     */
    public void updatePriorities() {

        priorityComboBox.getItems().clear();

        priorityComboBox.setItems(FXCollections.observableArrayList(priorityRepository.getPriorityMap().keySet()));
        priorityComboBox.getSelectionModel().clearSelection();

        // In case the priority selected was deleted, set it to Default.
        if (!priorityRepository.getPriorityMap().containsKey(priorityComboBox.getValue())) {
           priorityComboBox.setValue("Default");
        }

        taskTable.refresh();
    }

    /**
     * This method informs the mediaLabController it should update its tables,
     * then resets the task table.
     */
    public void updateTasks(){
        mediaLabController.updateMediaLabTables();
        taskTable.getItems().clear();
        taskTable.setItems(FXCollections.observableArrayList(taskRepository.findAll()));
        taskTable.refresh();
    }

    /**
     * Clear the input elements. Preset the drop-down menus
     * to default.
     */
    private void clearInputs() {

        taskField.clear();
        descriptionField.clear();
        deadlinePicker.setValue(LocalDate.now().plusWeeks(1));
        categoryComboBox.setValue("Uncategorized");
        priorityComboBox.setValue("Default");
        stateComboBox.setValue(TaskStateUtils.OPEN);
    }

    /**
     * preset the input elements, happens based on the listener.
     * @param newSelection The selected task from the task table.
     */
    private void presetInputs(Task newSelection) {

        taskField.setText(newSelection.getName());
        descriptionField.setText(newSelection.getDescription());
        deadlinePicker.setValue(LocalDate.parse(newSelection.getDeadline(), formatter));
        categoryComboBox.setValue(categoryRepository.getCategoryMapReverse().get(newSelection.getCategoryId()));
        priorityComboBox.setValue(priorityRepository.getPriorityMapReverse().get(newSelection.getPriorityId()));

        /*
            If the state of the task is delayed, display open, as delayed can only be provided by the
            application on its initialization.
        */
        if (newSelection.getState() != TaskStateUtils.DELAYED) {
            stateComboBox.setValue(newSelection.getState());
        }
        else {
            stateComboBox.setValue(TaskStateUtils.OPEN);
        }
    }

    /**
     * This method calls all the above update methods and clears
     * the inputs from the input elements.
     */
    private void update(){
        updateCategories();
        updatePriorities();
        updateTasks();
        taskTable.refresh();
        clearInputs();
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
     * This method makes sure all notification management windows
     * close along with the task management window.
     * @param stage The stage of the task management window.
     */
    protected void closeOnRequest(Stage stage){
        for (NotificationController controller: notificationControllers) {
            controller.closeNotificationWindow();
        }
        stage.close();
    }
}
