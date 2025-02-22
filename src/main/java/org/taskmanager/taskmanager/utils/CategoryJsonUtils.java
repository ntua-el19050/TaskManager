package org.taskmanager.taskmanager.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.taskmanager.taskmanager.model.*;

/**
 *  This class is used for the communication between the
 *  Category Repository and the JSON document. It includes
 *  2 methods, one for reading a Category List from a JSON
 *  file and one for writing a Category List on a JSON file.
 */

public class CategoryJsonUtils {

    /**
     * The constructor of the CategoryJsonUtils which is never used or called.
     */
    public CategoryJsonUtils(){}

    /**
     * This method is used to read the JSON file with the
     * Categories stored in it. The categories are then
     * stored in the Category Repository and used for all
     * the CRUD methods in the application revolving around
     * Categories.
     *
     * @param filePath The file path to the JSON file.
     * @return The list of all categories from the JSON file.
     * @throws IOException Returns default category in case of an error.
     */
    public static List<Category> readCategoryListFromFile(String filePath) throws IOException {

        File file = new File(filePath);
        List<Category> categories = new ArrayList<>();

        /*
            Checking whether the file exists. If it doesn't exist, it simply returns
            The Uncategorized category only.
        */
        if (!file.exists() || file.length() == 0) {
            Category category = new Category(-1,"Uncategorized");
            categories.add(category);
            return categories;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNodes = objectMapper.readTree(file);

            // Checks whether the Uncategorized category is already included.
            boolean includesDefaultCategory = false;

            for (JsonNode jsonNode : jsonNodes) {

                // From each jsonNode create a Category.
                Category category = new Category(
                        jsonNode.get("categoryID").asInt(),
                        jsonNode.get("name").asText()
                );

                // Check if the Uncategorized category exists and has the correct name.
                if (category.getCategoryId() == -1 &&  category.getName().equals("Uncategorized")) {
                    includesDefaultCategory = true;
                }
                else if(category.getCategoryId() == -1 && !category.getName().equals("Uncategorized")) {
                    continue;
                }

                categories.add(category);
            }

            // If the Uncategorized category is not included properly, include it.
            if (!includesDefaultCategory) {
                Category defaultCategory = new Category(-1, "Uncategorized");
                categories.add(defaultCategory);
            }
            return categories;

        // In the case of invalid JSON format or any error, return only the default category.
        } catch (JsonParseException | JsonMappingException e) {
            System.err.println("Invalid JSON format. Returning only default category.");
            Category category = new Category(-1,"Uncategorized");
            categories.add(category);
            return categories;
        } catch (IOException e) {
            e.printStackTrace();
            Category category = new Category(-1,"Uncategorized");
            categories.add(category);
            return categories;
        }
    }

    /**
     * This method is used to write in the JSON file with the
     * Categories stored in the application memory, inside the Category Repository.
     * The categories are then stored in the JSON file with the proper name and
     * will be used again once we open the application.
     *
     * @param filePath The filepath to the JSON file.
     * @param categories The list of categories from the application memory.
     * @throws IOException Returns default category in case of an error.
     */
    public static void writeCategoryListToFile(String filePath, List<Category> categories) throws IOException {

        File file = new File(filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<ObjectNode> jsonNodes = new ArrayList<>();

        try {
            /*
                In this for-loop we create jsonNodes based on the categories we have
                and create an ArrayList of jsonNodes to write in our file.
            */
            for (Category category : categories) {
                ObjectNode jsonNode = objectMapper.createObjectNode();
                jsonNode.put("categoryID", category.getCategoryId());
                jsonNode.put("name", category.getName());

                jsonNodes.add(jsonNode);
            }
            // The objectMapper is used to properly write the Nodes on the file.
            objectMapper.writeValue(file, jsonNodes);

        } catch (IOException e) {
            // In case of failure, the previous file still exists.
            System.err.println("Failed to save categories: " + e.getMessage());
        }
    }
}
