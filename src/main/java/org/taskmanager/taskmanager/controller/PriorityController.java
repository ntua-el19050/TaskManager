package org.taskmanager.taskmanager.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.taskmanager.taskmanager.model.Priority;
import org.taskmanager.taskmanager.repository.*;

import java.util.List;

/**
 * This is a class that controls the priority Management window of the
 * application. It has methods for initialization, addition, update, deletion.
 * Furthermore, it has 4 more helpful methods for clear, presets, updates and alerts.
 */
public class PriorityController {

    // The input elements needed.
    @FXML private TextField priorityField;

    // The table elements needed.
    @FXML private TableView<Priority> priorityTable;
    @FXML private TableColumn<Priority, Integer> priorityIDColumn;
    @FXML private TableColumn<Priority, String> levelColumn;

    // The controllers and repositories that are required by this controller.
    private MediaLabController mediaLabController;
    private PriorityRepository priorityRepository;
    private TaskRepository taskRepository;

    /**
     * The constructor of the PriorityController which is never used or called.
     */
    public PriorityController(){}

    /**
     * This method is used to initialize properly the controller. The MediaLab controller and
     * task and priority repositories are required. Then it sets the cells of the table to show
     * the values we want. Finally, a listener is added in order to change the value of the 
     * input elements to the ones of the priority selected for easier updates and additions.
     * @param mediaLabController The controller of the main (MediaLab Assistant) window.
     * @param taskRepository The task repository.
     * @param priorityRepository The priority repository.
     */
    @FXML
    public void initialize(MediaLabController mediaLabController, TaskRepository taskRepository, PriorityRepository priorityRepository) {

        // Set the controllers and repositories.
        this.mediaLabController = mediaLabController;
        this.taskRepository = taskRepository;
        this.priorityRepository = priorityRepository;

        // Properly start the table cells.
        priorityIDColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPriorityId()).asObject());
        levelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLevel()));

        // Set the items of the table and create the listener.
        priorityTable.setItems(FXCollections.observableArrayList(priorityRepository.findAll()));
        priorityTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                presetInputs(newSelection);
            } else {
                clearInputs();
            }
        });
    }

    /**
     * This method is used to add a new priority to the application.
     * It checks whether the level is not empty and whether it is not the
     * same as the already existing levels of the priorities, then calls
     * the priority repository to add the new priority that it created and
     * the priority is added to the priorityTable. Finally, we update the app.
     */
    @FXML
    private void addPriority() {

        String priorityLevel = priorityField.getText().trim();
        List<Priority> priorities = priorityRepository.findAll();

        // Check that there is a level to add.
        if (priorityLevel.isEmpty()) {
            showAlert("Error", "Please enter a priority level.");
            return;
        }

        // Make sure there are no duplicate levels in the priorities.
        for (Priority priority:priorities){
            if (priorityLevel.equals(priority.getLevel())){
                showAlert("Error", "Please enter a priority level that doesn't already exist.");
                return;
            }
        }

        // Create the new priority, add it to the repository and to the priority Table.
        priorityRepository.add(priorityLevel);
        update();
    }

    /**
     * Update a selected priority. First check that a priority is selected
     * and that it isn't the Default priority. If so, check that the
     * updated level is not empty and that it will not create a duplicated priority.
     * If not, then call the priority repository to update the repository and update
     * the application.
     */
    @FXML
    private void updatePriority() {

        Priority selectedPriority = priorityTable.getSelectionModel().getSelectedItem();

        // Check that there is a priority selected and that it isn't the default priority.
        if (selectedPriority == null) {
            showAlert("Warning", "Please select a priority to delete.");
        }
        else if (selectedPriority.getPriorityId() == -1) {
            showAlert("Error", "You cannot update the default priority.");
        }
        else {

            String priorityLevel = priorityField.getText().trim();

            List<Priority> priorities = priorityRepository.findAll();

            // Check that there is a level and that it doesn't already exist.
            if (priorityLevel.isEmpty()) {
                showAlert("Error", "Please enter a priority level.");
                return;
            }
            for (Priority priority:priorities){
                if (priorityLevel.equals(priority.getLevel())){
                    showAlert("Error", "Please enter a priority level that doesn't already exist.");
                    return;
                }
            }

            priorityRepository.update(selectedPriority, priorityLevel);
            update();
        }
    }

    /**
     * This method deletes a priority. It checks that there is a priority
     * selected and that it isn't the Default priority. If it isn't,
     * it deletes the tasks associated with it, the priority from the
     * repository and the priority from the priority table.
     */
    @FXML
    private void deletePriority() {

        Priority selectedPriority = priorityTable.getSelectionModel().getSelectedItem();

        // Check that there is a priority selected and that it isn't the default priority.
        if (selectedPriority == null) {
            showAlert("Warning", "Please select a priority to delete.");
        }
        else if (selectedPriority.getPriorityId() == -1) {
            showAlert("Error", "You cannot delete the default priority.");
        }
        else {
            // Delete the tasks with the to-be-deleted priority.
            taskRepository.ChangeTaskPriority(selectedPriority.getPriorityId());
            // Delete the priority from the priority repository.
            priorityRepository.delete(selectedPriority.getPriorityId());
            // Delete the priority from the priority table.
            priorityTable.getItems().remove(selectedPriority);
            
            update();
        }
    }

    /**
     * Clear the input elements.
     */
    private void clearInputs() {
        priorityField.clear();
    }

    /**
     * preset the input elements.
     * @param newSelection The selected priority from the priority table.
     */
    private void presetInputs(Priority newSelection) {
        priorityField.setText(newSelection.getLevel());
    }

    /**
     * update the priority table and call the mediaLab Controller to update
     * the application based on the updates in the priorities.
     */
    private void update(){
        mediaLabController.updateMediaLabTables();
        priorityTable.getItems().clear();
        priorityTable.setItems(FXCollections.observableArrayList(priorityRepository.findAll()));
        priorityTable.refresh();
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


}
