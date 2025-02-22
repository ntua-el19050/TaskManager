package org.taskmanager.taskmanager.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.taskmanager.taskmanager.model.Category;
import org.taskmanager.taskmanager.repository.*;

import java.util.List;

/**
 * This is a class that controls the category Management window of the
 * application. It has methods for initialization, addition, update, deletion.
 * Furthermore, it has 4 more helpful methods for clear, presets, updates and alerts.
 */
public class CategoryController {

    // The input elements needed.
    @FXML private TextField categoryField;

    // The table elements needed.
    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, Integer> categoryIDColumn;
    @FXML private TableColumn<Category, String> nameColumn;

    // The controllers and repositories that are required by this controller.
    private MediaLabController mediaLabController;
    private CategoryRepository categoryRepository;
    private TaskRepository taskRepository;

    /**
     * The constructor of the CategoryController which is never used or called.
     */
    public CategoryController(){}

    /**
     * This method is used to initialize properly the controller. The MediaLab controller and
     * task and category repositories are required. Then it sets the cells of the table to show
     * the values we want. Finally, a listener is added in order to change the value of the
     * input elements to the ones of the category selected for easier updates and additions.
     * @param mediaLabController The controller of the main (MediaLab Assistant) window.
     * @param taskRepository The task repository.
     * @param categoryRepository The category repository.
     */
    public void initialize(MediaLabController mediaLabController, TaskRepository taskRepository, CategoryRepository categoryRepository) {

        // Set the controllers and repositories.
        this.mediaLabController = mediaLabController;
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;

        // Properly start the table cells.
        categoryIDColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCategoryId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        // Set the items of the table and create the listener.
        categoryTable.setItems(FXCollections.observableArrayList(categoryRepository.findAll()));
        categoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                presetInputs(newSelection);
            } else {
                clearInputs();
            }
        });
    }

    /**
     * This method is used to add a new category to the application.
     * It checks whether the name is not empty and whether it is not the
     * same as the already existing names of the categories, then calls
     * the category repository to add the new category that it created and
     * the category is added to the categoryTable. Finally, we update the app.
     */
    @FXML
    private void addCategory() {

        String categoryName = categoryField.getText().trim();
        List<Category> categories = categoryRepository.findAll();

        // Check that there is a name to add.
        if (categoryName.isEmpty()) {
            showAlert("Error", "Please enter a category name.");
            return;
        }

        // Make sure there are no duplicate names in the categories.
        for (Category category:categories){
            if (categoryName.equals(category.getName())){
                showAlert("Error", "Please enter a category name that doesn't already exist.");
                return;
            }
        }

        // Create the new category, add it to the repository and to the category Table.
        categoryRepository.add(categoryName);

        update();
    }

    /**
     * Update a selected category. First check that a category is selected
     * and that it isn't the uncategorized category. If so, check that the
     * updated name is not empty and that it will not create a duplicated category.
     * If not, then call the category repository to update the repository and update
     * the application.
     */
    @FXML
    private void updateCategory() {

        Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();

        // Check that there is a category selected and that it isn't the default category.
        if (selectedCategory == null) {
            showAlert("Warning", "Please select a category to update.");
        }
        else if (selectedCategory.getCategoryId() == -1) {
            showAlert("Error", "You cannot update the default category.");
        }
        else {

            String categoryName = categoryField.getText().trim();
            List<Category> categories = categoryRepository.findAll();

            // Check that there is a name and that it doesn't already exist.
            if (categoryName.isEmpty()) {
                showAlert("Error", "Please enter a category name.");
                return;
            }

            for (Category category:categories){
                if (categoryName.equals(category.getName())){
                    showAlert("Error", "Please enter a category name that doesn't already exist.");
                    return;
                }
            }

            categoryRepository.update(selectedCategory, categoryName);
            update();
        }
    }

    /**
     * This method deletes a category. It checks that there is a category
     * selected and that it isn't the default category. If it isn't,
     * it deletes the tasks associated with it, the category from the
     * repository and the category from the category table.
     */
    @FXML
    private void deleteCategory() {

        Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();

        // Check that there is a category selected and that it isn't the default category.
        if (selectedCategory == null) {
            showAlert("Warning", "Please select a category to delete.");
        }
        else if (selectedCategory.getCategoryId() == -1) {
            showAlert("Error", "You cannot delete the default category.");
        }
        else {

            // Delete the tasks with the to-be-deleted category.
            taskRepository.deleteTasksByCategory(selectedCategory.getCategoryId());
            // Delete the category from the category repository.
            categoryRepository.delete(selectedCategory.getCategoryId());
            // Delete the category from the category table.
            categoryTable.getItems().remove(selectedCategory);

            update();
        }
    }

    /**
     * Clear the input elements.
     */
    private void clearInputs() {
        categoryField.clear();
    }

    /**
     * preset the input elements.
     * @param newSelection The selected category from the category table.
     */
    private void presetInputs(Category newSelection) {
        categoryField.setText(newSelection.getName());
    }

    /**
     * update the category table and call the mediaLab Controller to update
     * the application based on the updates in the categories.
     */
    private void update(){
        mediaLabController.updateMediaLabTables();
        categoryTable.getItems().clear();
        categoryTable.setItems(FXCollections.observableArrayList(categoryRepository.findAll()));
        categoryTable.refresh();
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
