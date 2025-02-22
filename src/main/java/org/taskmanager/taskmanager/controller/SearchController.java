package org.taskmanager.taskmanager.controller;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import org.taskmanager.taskmanager.model.Task;
import org.taskmanager.taskmanager.repository.*;

/**
 * This class is the controller of the Search window. It is used to show
 * the results of the search and call the taskRepository to make the change.
 * It has 3 methods, one to initialize it, one for searching tasks when the search
 * button is pressed and one to update the table and the drop-down menus
 * when a change happens to the application.
 */
public class SearchController {

    // Input elements for the name, category and priority.
    @FXML private TextField searchNameField;
    @FXML private ComboBox<String> searchCategoryComboBox;
    @FXML private ComboBox<String> searchPriorityComboBox;

    // The task table shown as specifications requested.
    @FXML private TableView<Task> searchResultsTable;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> dueDateColumn;
    @FXML private TableColumn<Task, String> categoryColumn;
    @FXML private TableColumn<Task, String> priorityColumn;

    // The repositories are kept for up to date
    private TaskRepository taskRepository;
    private CategoryRepository categoryRepository;
    private PriorityRepository priorityRepository;

    // The tasks that are shown to the table at each time.
    private List<Task> shownTasks;

    /**
     * The constructor of the SearchController which is never used or called.
     */
    public SearchController(){}

    /**
     * This method initializes the controller with the proper repositories,
     * sets the table cells to show what we want. Then we choose to show all the tasks
     * and update it to have the current state of the application.
     * @param taskRepository The repository that has all the tasks.
     * @param categoryRepository The repository that has all the categories, as well as their maps.
     * @param priorityRepository The repository that has all the priorities, as well as their maps.
     */
    public void initialize(TaskRepository taskRepository, CategoryRepository categoryRepository, PriorityRepository priorityRepository) {

        // Set the repositories.
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.priorityRepository = priorityRepository;

        // Set the columns of the table.
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        dueDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeadline()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(categoryRepository.getCategoryMapReverse().get(cellData.getValue().getCategoryId())));
        priorityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(priorityRepository.getPriorityMapReverse().get(cellData.getValue().getPriorityId())));

        // Set to show all the tasks and update.
        shownTasks = taskRepository.findAll();
        update();
    }

    /**
     * Updates the category and priority drop-down methods and presets them to any,
     * and shows the previously shown tasks and sorts them based on the category.
     */
    public void update(){

        // Update the category drop-down list and set it to Any.
        ObservableList<String> categoryNames = FXCollections.observableArrayList(categoryRepository.getCategoryMap().keySet());
        categoryNames.add("Any");
        searchCategoryComboBox.setItems(categoryNames);
        searchCategoryComboBox.setValue("Any");

        // Update the priority drop-down list and set it to Any.
        ObservableList<String> priorityNames = FXCollections.observableArrayList(priorityRepository.getPriorityMap().keySet());
        priorityNames.add("Any");
        searchPriorityComboBox.setItems(priorityNames);
        searchPriorityComboBox.setValue("Any");

        // Set the table to the tasks the user wants shown and sort based on the category column.
        searchResultsTable.setItems(FXCollections.observableArrayList(shownTasks));
        searchResultsTable.getSortOrder().add(categoryColumn);
    }

    /**
     * This method is used to give to the search method in taskRepository
     * what it requires. It provides the category and priority ids as well
     * as the name the user wants the task to contain. Finally, it sets the
     * table to the tasks that were filtered out.
     */
    @FXML
    private void searchTasks() {

        // The name the user wants to search for.
        String name = searchNameField.getText().toLowerCase();

        // Get the name of the category chosen and translate it to its id.
        String selectedCategory = searchCategoryComboBox.getValue();
        int selectedCategoryId;
        if(selectedCategory.equals("Any")) {
            selectedCategoryId = -2;
        }
        else {
            selectedCategoryId = categoryRepository.getCategoryMap().get(selectedCategory);
        }

        // Get the name of the priority chosen and translate it to its id.
        String selectedPriority = searchPriorityComboBox.getValue();
        int selectedPriorityId;
        if(selectedPriority.equals("Any")) {
            selectedPriorityId = -2;
        }
        else {
            selectedPriorityId = priorityRepository.getPriorityMap().get(selectedPriority);
        }

        // Get the filtered tasks and update and sort the table.
        shownTasks = taskRepository.searchTasks(name, selectedCategoryId, selectedPriorityId);
        searchResultsTable.setItems(FXCollections.observableArrayList(shownTasks));
        searchResultsTable.getSortOrder().add(categoryColumn);
    }
}
