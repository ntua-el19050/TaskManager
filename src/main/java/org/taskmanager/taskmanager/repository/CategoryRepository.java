package org.taskmanager.taskmanager.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.taskmanager.taskmanager.model.Category;
import org.taskmanager.taskmanager.utils.CategoryJsonUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class is a repository of all the categories as well as
 * the category maps (from name to id and reverse). It contains
 * all the categories in memory when the application is running.
 * It contains all methods for addition, update and delete. Furthermore,
 * it returns the category maps and the categories, but also operates
 * as the connector between the controllers and the models and JsonUtils.
 */
public class CategoryRepository {

    private static final String CATEGORY_FILE = "medialab/categories.json";

    private static final ObservableList<Category> categories = FXCollections.observableArrayList();

    /*
        The maps basically translate the ids that tasks have to names in the drop-down
        boxes and in the table views.
        categoryMap - Name-to-ID, used to add properly the category to a task.
        categoryMapReverse - ID-to-Name, used to show the category with its name to the user.
     */
    private static final Map<String, Integer> categoryMap = new HashMap<>();
    private static final Map<Integer, String> categoryMapReverse = new HashMap<>();

    /**
     * Constructor of the Category Repository, only called in MediaLabController
     * and then passed on in the apps it is needed to avoid having duplicate repositories.
     * It essentially loads all the categories from the Json file.
     */
    public CategoryRepository() {
        loadCategories();
    }

    /**
     * The actual loading happens in this method that reads the categories from
     * the JSON categories file and then sets the categories list as well as
     * the category Maps properly.
     */
    private void loadCategories() {

        try {

            List<Category> loadedCategories = CategoryJsonUtils.readCategoryListFromFile(CATEGORY_FILE);
            categories.setAll(loadedCategories);
            // Clears the maps in case they are not empty.
            categoryMap.clear();
            categoryMapReverse.clear();

            // Properly set the category maps.
            for (Category category : categories) {
                categoryMap.put(category.getName(), category.getCategoryId()); // Store name-ID mapping
                categoryMapReverse.put(category.getCategoryId(), category.getName());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method adds a category to the categories list and
     * adds the new category in the category maps as well
     * @param categoryName The name of the new category.
     */
    public void add(String categoryName) {

        Category newCategory = new Category(categoryName);
        categories.add(newCategory);

        categoryMap.put(newCategory.getName(), newCategory.getCategoryId());
        categoryMapReverse.put(newCategory.getCategoryId(), newCategory.getName());
    }

    /**
     * This method updates a category that already exists. Based on the
     * id we find the category that corresponds to it from the categories list.
     * After it finds the category, it deletes its entries from the map,
     * then sets the new name and then re-adds them to the map.
     * @param selectedCategory The category the user selected from the table to update.
     * @param name The updated name of the category.
     */
    public void update(Category selectedCategory, String name) {

        Optional<Category> existingCategory = categories.stream()
                .filter(category -> category.getCategoryId() == selectedCategory.getCategoryId())
                .findFirst();

        existingCategory.ifPresent(category -> {

            // Remove old entries from the maps.
            categoryMap.remove(category.getName());
            categoryMapReverse.remove(category.getCategoryId());

            // Update the category.
            category.setName(name);

            // Add the new entries to the maps.
            categoryMap.put(category.getName(), category.getCategoryId());
            categoryMapReverse.put(category.getCategoryId(), category.getName());
        });
    }

    /**
     * This method deletes a category from the repository based on its id.
     * It finds the category and then, if it exists, removes the entries from the
     * category maps and removes the category from the categories list as well.
     * The id is used because it is unique for each Category.
     * @param categoryID The id of the to-be-deleted category.
     */
    public void delete(int categoryID) {

        // Find the category from the list of categories based on its id.
        Category categoryToRemove = categories.stream()
                .filter(category -> category.getCategoryId() == categoryID)
                .findFirst()
                .orElse(null);

        // If the category exists, remove it from the list and the maps.
        if (categoryToRemove != null) {
            categories.remove(categoryToRemove);
            categoryMap.remove(categoryToRemove.getName());
            categoryMapReverse.remove(categoryToRemove.getCategoryId());
        }
    }

    /**
     * Returns the category map that is given the name of a category and returns its id.
     *
     * @return The category map that is given the name of a category and returns its id.
     */
    public Map<String, Integer> getCategoryMap() {
        return categoryMap;
    }

    /**
     * Returns the category map reverse that is given the id of a category and returns its name.
     *
     * @return The category map reverse that is given the id of a category and returns its name.
     */
    public Map<Integer, String> getCategoryMapReverse() {
        return categoryMapReverse;
    }

    /**
     * Returns the list of all categories in the repository.
     *
     * @return The list of all categories in the repository.
     */
    public ObservableList<Category> findAll() {
        return categories;
    }

    /**
     * Calls the writeCategoryListToFile method from CategoryJsonUtils. It is
     * used only when the MediaLabAssistant window is closed, as we want to save to
     * JSON files only when the whole application terminates.
     */
    public void saveAll() {
        try {
            CategoryJsonUtils.writeCategoryListToFile(CATEGORY_FILE, categories);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}