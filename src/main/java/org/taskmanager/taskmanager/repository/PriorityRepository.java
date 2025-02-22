package org.taskmanager.taskmanager.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.taskmanager.taskmanager.model.Priority;
import org.taskmanager.taskmanager.utils.PriorityJsonUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class is a repository of all the priorities as well as
 * the priority maps (from name to id and reverse). It contains
 * all the priorities in memory when the application is running.
 * It contains all methods for addition, update and delete. Furthermore,
 * it returns the priority maps and the priorities, but also operates
 * as the connector between the controllers and the models and JsonUtils.
 * It contains a method to load the priorities from the previous session as
 * saved in the JSON file, and one to save all the priorities from 
 * the current session to the JSON file.
 */
public class PriorityRepository {

    private static final String PRIORITY_FILE = "medialab/priorities.json";

    private static final ObservableList<Priority> priorities = FXCollections.observableArrayList();

    /*
        The maps basically translate the ids that tasks have to names in the drop-down
        boxes and in the table views.
        priorityMap - Name-to-ID, used to add properly the priority to a task.
        priorityMapReverse - ID-to-Name, used to show the priority with its name to the user.
     */
    private static final Map<String, Integer> priorityMap = new HashMap<>();
    private static final Map<Integer, String> priorityMapReverse = new HashMap<>();

    /**
     * Constructor of the Priority Repository, only called in MediaLabController
     * and then passed on in the apps it is needed to avoid having duplicate repositories.
     * It essentially loads all the priorities from the Json file.
     */
    public PriorityRepository() {
        loadPriorities();
    }

    /**
     * The actual loading happens in this method that reads the priorities from
     * the JSON priorities file and then sets the priorities list as well as
     * the priority Maps properly.
     */
    private void loadPriorities() {

        try {

            List<Priority> loadedPriorities = PriorityJsonUtils.readPriorityListFromFile(PRIORITY_FILE);
            priorities.setAll(loadedPriorities);
            // Clears the maps in case they are not empty.
            priorityMap.clear();
            priorityMapReverse.clear();

            // Properly set the priority maps
            for (Priority priority : priorities) {
                priorityMap.put(priority.getLevel(), priority.getPriorityId());
                priorityMapReverse.put(priority.getPriorityId(), priority.getLevel());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method adds a priority to the priorities list and
     * adds the new priority in the priority maps as well
     * @param priorityLevel The level of the new priority.
     */
    public void add(String priorityLevel) {

        Priority newPriority = new Priority(priorityLevel);
        priorities.add(newPriority);
        
        priorityMap.put(newPriority.getLevel(), newPriority.getPriorityId());
        priorityMapReverse.put(newPriority.getPriorityId(), newPriority.getLevel());
    }

    /**
     * This method updates a priority that already exists. Based on the
     * id we find the priority that corresponds to it from the priorities list.
     * After it finds the priority, it deletes its entries from the map,
     * then sets the new name and then re-adds them to the map.
     * @param selectedPriority The priority the user selected from the table to update.
     * @param name The updated name of the priority.
     */
    public void update(Priority selectedPriority, String name) {

        Optional<Priority> existingPriority = priorities.stream()
                .filter(priority -> priority.getPriorityId() == selectedPriority.getPriorityId())
                .findFirst();

        existingPriority.ifPresent(priority -> {

            // Remove old entries from the maps.
            priorityMap.remove(priority.getLevel());
            priorityMapReverse.remove(priority.getPriorityId());

            // update the priority
            priority.setLevel(name);

            // Add the new entries to the maps.
            priorityMap.put(priority.getLevel(), priority.getPriorityId());
            priorityMapReverse.put(priority.getPriorityId(), priority.getLevel());
        });
    }

    /**
     * This method deletes a priority from the repository based on its id.
     * It finds the priority and then, if it exists, removes the entries from the
     * priority maps and removes the priority from the priorities list as well.
     * The id is used because it is unique for each priority.
     * @param priorityID The id of the to-be-deleted priority.
     */
    public void delete(int priorityID) {

        // Find the priority from the list of priorities based on its id.
        Priority priorityToRemove = priorities.stream()
                .filter(priority -> priority.getPriorityId() == priorityID)
                .findFirst()
                .orElse(null);

        // If the priority exists, remove it from the list and the maps.
        if (priorityToRemove != null) {
            priorities.remove(priorityToRemove);
            priorityMap.remove(priorityToRemove.getLevel());
            priorityMapReverse.remove(priorityToRemove.getPriorityId());
        }
    }

    /**
     * Returns the priority map that is given the name of a priority and returns its id.
     *
     * @return The priority map that is given the name of a priority and returns its id.
     */
    public Map<String, Integer> getPriorityMap() {
        return priorityMap;
    }

    /**
     * Returns the priority map reverse that is given the id of a priority and returns its name.
     *
     * @return The priority map reverse that is given the id of a priority and returns its name.
     */
    public Map<Integer, String> getPriorityMapReverse() {
        return priorityMapReverse;
    }
    
    /**
     * Returns the list of all priorities in the repository.
     *
     * @return The list of all priorities in the repository.
     */
    public ObservableList<Priority> findAll() {
        return priorities;
    }

    /**
     * Calls the writePriorityListToFile method from PriorityJsonUtils. It is
     * used only when the MediaLabAssistant window is closed, as we want to save to
     * JSON files only when the whole application terminates.
     */
    public void saveAll() {
        try {
            PriorityJsonUtils.writePriorityListToFile(PRIORITY_FILE, priorities);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
