package org.taskmanager.taskmanager.controller;

import java.io.IOException;

import java.util.*;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.taskmanager.taskmanager.model.*;
import org.taskmanager.taskmanager.repository.*;

/**
 * This class controls the MediaLab Assistant window. It has an
 * initialize method to initialize the controller. It also has
 * 6 methods for opening other windows, for notifications that need to be displayed,
 * for delayed tasks that need to be shown, and for the buttons that
 * open the task, category and priority management windows respectively, as well
 * as for the search window. Additionally to these, it includes 4 methods
 * for updating the tables and the other windows, but also 1 method
 * for when the window closes in order to close all windows of the application that
 * may be open.
 */
public class MediaLabController {

    // Task table and its columns.
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, Integer> taskIDColumn;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> descriptionColumn;
    @FXML private TableColumn<Task, String> deadlineColumn;
    @FXML private TableColumn<Task, String> categoryColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, Integer> notificationNumberColumn;
    @FXML private TableColumn<Task, String> stateColumn;

    // Notification table and its columns.
    @FXML private TableView<NotificationWrapper> notificationTable;
    @FXML private TableColumn<NotificationWrapper, Integer> notificationIDColumn;
    @FXML private TableColumn<NotificationWrapper, Integer> notificationTaskIDColumn;
    @FXML private TableColumn<NotificationWrapper, String> notificationTaskNameColumn;
    @FXML private TableColumn<NotificationWrapper, String> notificationMessageColumn;
    @FXML private TableColumn<NotificationWrapper, String> notificationDateColumn;

    // The labels for the header of the window.
    @FXML private Label totalTasksLabel;
    @FXML private Label completedTasksLabel;
    @FXML private Label delayedTasksLabel;
    @FXML private Label dueSoonTasksLabel;

    /* 
        In this part the repositories are initialized, and they are final, 
        meaning the location they show in memory cannot change
    */
    private final TaskRepository taskRepository = new TaskRepository();
    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final PriorityRepository priorityRepository = new PriorityRepository();

    // Controllers help us know when a window is up and running and if so to perform specific updates to it.
    private TaskController taskController;
    private NotificationDisplayController  notificationDisplayController;
    private DelayedTaskPopUpController delayedTaskPopUpController;
    private SearchController searchController;

    // Stages list helps us keep track of all windows and close them along with the MediaLab Assistant window.
    private final List<Stage> stagesList = new ArrayList<>();

    /**
     * The constructor of the MediaLabController which is never used or called.
     */
    public MediaLabController(){}

    /**
     * This method is used to initialize properly the controller and the window.
     * It creates properly all the cells of the task and notification table.
     * Then it checks whether there are notifications and delayed tasks
     * to be displayed and calls the windows for what exists. Finally, we update
     * the MediaLab Assistant tables at the end to make sure it is updated with the
     * current state.
     */
    @FXML
    public void initialize() {

        // Creates properly the task table columns  to get the value we want from the Task Class.
        taskIDColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTaskId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        deadlineColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeadline()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(categoryRepository.getCategoryMapReverse().get(cellData.getValue().getCategoryId())));
        priorityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(priorityRepository.getPriorityMapReverse().get(cellData.getValue().getPriorityId())));
        notificationNumberColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNotifications().size()).asObject());
        stateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getState().toString()));

        // Creates properly the notification table columns  to get the value we want from the NotificationWrapper Class.
        notificationIDColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNotificationId()).asObject());
        notificationTaskIDColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTaskId()).asObject());
        notificationTaskNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTaskName()));
        notificationMessageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNotificationMessage()));
        notificationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNotificationDate()));

        // If there is not a notification display window open and there are notifications to be displayed, display them.
        if (notificationDisplayController == null && taskRepository.hasNotifications()) {
            notificationDisplay();
        }

        // If there is not a delayed task window open and there are tasks to be displayed, display them.
        if (delayedTaskPopUpController == null && taskRepository.hasDelayedTasks()) {
            delayedPopUpWindow();
        }

        updateMediaLabTables();
    }

    /**
     * This method loads the NotificationDisplay FXML file, its controller,
     * initializes it with the repositories and shows it.
     */
    public void notificationDisplay() {

        try{

            // Load the FXML file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/taskmanager/taskmanager/views/NotificationDisplay.fxml"));
            VBox root = loader.load();

            // Load the controller responding to the FXML file.
            NotificationDisplayController controller = loader.getController();
            controller.initialize(taskRepository, categoryRepository, priorityRepository);
            notificationDisplayController = controller;

            // Create the stage, the scene, give it a title, add it to the stagesList, show it.
            Stage notificationStage = new Stage();
            notificationStage.setTitle("Notifications Display");
            notificationStage.setScene(new Scene(root));

            stagesList.add(notificationStage);
            notificationStage.show();

        } catch (IOException e) {
            System.err.println("Failed to load notifications: " + e.getMessage());
        }
    }

    /**
     * This method loads the delayedPopUp FXML file, its controller,
     * initializes it with the repositories and shows it.
     */
    private void delayedPopUpWindow(){

        try{

            // Load the FXML file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/taskmanager/taskmanager/views/DelayedTasksPopUp.fxml"));
            VBox root = loader.load();

            // Load the controller responding to the FXML file.
            DelayedTaskPopUpController controller = loader.getController();
            controller.initialize(taskRepository, categoryRepository, priorityRepository);
            delayedTaskPopUpController = controller;

            // Create the stage, the scene, give it a title, add it to the stagesList, show it.
            Stage delayedPopUpStage = new Stage();
            delayedPopUpStage.setTitle("Delayed  Tasks");
            delayedPopUpStage.setScene(new Scene(root));

            stagesList.add(delayedPopUpStage);
            delayedPopUpStage.show();

        } catch (IOException e) {
            System.err.println("Failed to load delayed tasks window: " + e.getMessage());
        }
    }

    /**
     * This method loads the TaskManager FXML file, its controller,
     * initializes it with the repositories and the MediaLabController, and shows it.
     */
    @FXML
    public void goToTasksWindow() {

        try{

            // Load the FXML file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/taskmanager/taskmanager/views/TaskManagement.fxml"));
            VBox root = loader.load();

            // Load the controller responding to the FXML file.
            TaskController controller = loader.getController();
            taskController = controller;
            controller.initialize(this, taskRepository, categoryRepository, priorityRepository, stagesList);

            // Create the stage, the scene, give it a title, give it a style sheet, add it to the stagesList, show it.
            Stage taskStage = new Stage();
            taskStage.setTitle("Tasks Manager");

            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/org/taskmanager/taskmanager/style/TaskManagement.css")).toExternalForm());
            taskStage.setScene(scene);
            taskStage.setOnCloseRequest(event -> controller.closeOnRequest(taskStage));

            stagesList.add(taskStage);
            taskStage.show();

        } catch (IOException e) {
            System.err.println("Failed to load Task Management window: " + e.getMessage());
        }
    }

    /**
     * This method loads the CategoryManagement FXML file, its controller,
     * initializes it with the repositories and the MediaLabController, and shows it.
     */
    @FXML
    public void goToCategoriesWindow() {

        try{

            // Load the FXML file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/taskmanager/taskmanager/views/CategoryManagement.fxml"));
            VBox root = loader.load();

            // Load the controller responding to the FXML file.
            CategoryController controller = loader.getController();
            controller.initialize(this, taskRepository, categoryRepository);

            // Create the stage, the scene, give it a title, give it a style sheet, add it to the stagesList, show it.
            Stage categoryStage = new Stage();
            categoryStage.setTitle("Category Manager");

            Scene scene = new Scene(root, 600, 400);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/org/taskmanager/taskmanager/style/CategoryManagement.css")).toExternalForm());
            categoryStage.setScene(scene);

            stagesList.add(categoryStage);
            categoryStage.show();

        } catch (IOException e) {
            System.err.println("Failed to load Category Management window: " + e.getMessage());
        }
    }

    /**
     * This method loads the PriorityManagement FXML file, its controller,
     * initializes it with the repositories and the MediaLabController, and shows it.
     */
    @FXML
    public void goToPrioritiesWindow() {

        try{

            // Load the FXML file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/taskmanager/taskmanager/views/PriorityManagement.fxml"));
            VBox root = loader.load();

            // Load the controller responding to the FXML file.
            PriorityController controller = loader.getController();
            controller.initialize(this, taskRepository, priorityRepository);

            // Create the stage, the scene, give it a title, give it a style sheet, add it to the stagesList, show it.
            Stage priorityStage = new Stage();
            priorityStage.setTitle("Priority Manager");

            Scene scene = new Scene(root, 600, 400);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/org/taskmanager/taskmanager/style/PriorityManagement.css")).toExternalForm());
            priorityStage.setScene(scene);

            stagesList.add(priorityStage);
            priorityStage.show();

        } catch (IOException e) {
            System.err.println("Failed to load Priority Manager window: " + e.getMessage());
        }
    }

    /**
     * This method loads the SearchWindow FXML file, its controller,
     * initializes it with the repositories and shows it.
     */
    @FXML
    private void goToSearchWindow() {

        try {

            // Load the FXML file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/taskmanager/taskmanager/views/SearchWindow.fxml"));
            VBox root = loader.load();

            // Load the controller responding to the FXML file.
            SearchController controller = loader.getController();
            searchController = controller;
            controller.initialize(taskRepository, categoryRepository, priorityRepository);

            // Create the stage, the scene, give it a title, give it a style sheet, add it to the stagesList, show it.
            Stage searchStage = new Stage();
            searchStage.setTitle("Search Window");

            Scene scene = new Scene(root, 600, 500);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/org/taskmanager/taskmanager/style/SearchWindow.css")).toExternalForm());
            searchStage.setScene(scene);

            stagesList.add(searchStage);
            searchStage.show();

        } catch (IOException e) {
            System.err.println("Failed to load Search Window: " + e.getMessage());
        }
    }

    /**
     * This method is used to update properly the tables
     * in MediaLabAssistant, but to also update tasks and priorities
     * in the task management window. Finally, it also updates the summary.
     * Not all updates need to happen all the time, but it was considered
     * more efficient to update the whole application with each change.
     */
    public void updateMediaLabTables(){

        /*
            Update categories and priorities in the task controller, if the
            window is already open. The task table does not require something
            more than a refresh as we call the latest version of the maps at all times.
        */
        if (taskController != null) {
            taskController.updateCategories();
            taskController.updatePriorities();
        }

        /*
            Update categories and priorities in the search controller, if the
            window is already open.
        */
        if (searchController != null) {
            searchController.update();
        }

        // Update the task table items, refresh it and sort it.
        taskTable.getItems().clear();
        taskTable.setItems(FXCollections.observableArrayList(taskRepository.findAll()));
        taskTable.refresh();

        taskTable.getSortOrder().add(categoryColumn);

        // Update the notification table items, refresh it and sort it.
        notificationTable.getItems().clear();
        notificationTable.setItems(FXCollections.observableArrayList(taskRepository.loadNotifications()));
        notificationTable.refresh();

        notificationTable.getSortOrder().add(notificationDateColumn);

        updateSummary();
    }

    /**
     * This method updates the summary at the header of the window.
     * It finds directly from the repository the number of tasks,
     * the number of completed tasks, the number of delayed tasks,
     * and the number of tasks due in 7 days.
     */
    private void updateSummary() {
        totalTasksLabel.setText("Total Tasks: " + taskRepository.findAll().size());
        completedTasksLabel.setText("Completed: " + taskRepository.findAll().stream().filter(Task::isCompleted).count());
        delayedTasksLabel.setText("Delayed: " + taskRepository.findAll().stream().filter(Task::isDelayed).count());
        dueSoonTasksLabel.setText("Due in 7 Days: " + taskRepository.findAll().stream().filter(Task::isDueInSevenDays).count());
    }

    /**
     * This method makes sure that when we close the application all open windows are closed,
     * but it also calls the saveAll() methods from the repositories to update the JSON files.
     * @param primaryStage The MediaLab Controller stage.
     */
    @FXML
    public void onClose(Stage primaryStage) {

        for (Stage stage : stagesList) {
            stage.close();
        }
        primaryStage.close();

        categoryRepository.saveAll();
        priorityRepository.saveAll();
        taskRepository.saveAll();
    }
}