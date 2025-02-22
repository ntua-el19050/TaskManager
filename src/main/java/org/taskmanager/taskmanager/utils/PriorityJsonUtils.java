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
 *  Priority Repository and the JSON document. It includes
 *  2 methods, one for reading a Priority List from a JSON
 *  file and one for writing a Priority List on a JSON file.
 */

public class PriorityJsonUtils {

    /**
     * The constructor of the PriorityJsonUtils which is never used or called.
     */
    public PriorityJsonUtils(){}

    /**
     * This method is used to read the JSON file with the
     * Priorities stored in it. The priorities are then
     * stored in the Priority Repository and used for all
     * the CRUD methods in the application revolving around
     * priorities.
     *
     * @param filePath The file path to the JSON file.
     * @return The list of all priorities from the JSON file.
     * @throws IOException Returns default priority in case of an error.
     */
    public static List<Priority> readPriorityListFromFile(String filePath) throws IOException {

        File file = new File(filePath);
        List<Priority> priorities = new ArrayList<>();

        /* 
            Checking whether the file exists. If it doesn't exist, it simply returns
            The default priority only. 
        */
        if (!file.exists() || file.length() == 0) {
            Priority priority = new Priority(-1,"Default");
            priorities.add(priority);
            return priorities;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNodes = objectMapper.readTree(file);
            
            // Checks whether the default priority is already included.
            boolean includesDefaultPriority = false;

            for (JsonNode jsonNode : jsonNodes) {
                
                // From each jsonNode create a priority.
                Priority priority = new Priority(
                        jsonNode.get("priorityId").asInt(),
                        jsonNode.get("name").asText()
                );

                // Check if the default priority exists and has the correct name.
                if (priority.getPriorityId() == -1 && priority.getLevel().equals("Default")) {
                    includesDefaultPriority = true;
                }
                else if  (priority.getPriorityId() == -1 && !priority.getLevel().equals("Default")) {
                    continue;
                }
                priorities.add(priority);
            }

            // If the default priority is not included properly, include it.
            if (!includesDefaultPriority) {
                Priority defaultPriority = new Priority(-1, "Default");
                priorities.add(defaultPriority);
            }
            return priorities;
            
        // In the case of invalid JSON format or any error, return only the default priority.
        } catch (JsonParseException | JsonMappingException e) {
            System.err.println("Invalid JSON format. Returning only the default priority.");
            Priority priority = new Priority(-1,"Default");
            priorities.add(priority);
            return priorities;
        } catch (IOException e) {
            e.printStackTrace();
            Priority priority = new Priority(-1,"Default");
            priorities.add(priority);
            return priorities;
        }
    }

    /**
     * This method is used to write in the JSON file with the
     * Priorities stored in the application memory, inside the priority Repository. 
     * The Priorities are then stored in the JSON file with the proper name and 
     * will be used again once we open the application.
     *
     * @param filePath The file path to the JSON file.
     * @param priorities The list of priorities that were used in the application and will be stored.
     * @throws IOException In case of an error nothing is saved and the JSON file remains untouched.
     */
    public static void writePriorityListToFile(String filePath, List<Priority> priorities) throws IOException {

        File file = new File(filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<ObjectNode> jsonNodes = new ArrayList<>();

        try {

            /* 
                In this for-loop we create jsonNodes based on the priorities we have
                and create an ArrayList of jsonNodes to write in our file.
            */
            for (Priority priority : priorities) {
                ObjectNode jsonNode = objectMapper.createObjectNode();
                jsonNode.put("priorityId", priority.getPriorityId());
                jsonNode.put("name", priority.getLevel());

                jsonNodes.add(jsonNode);
            }
            // The objectMapper is used to properly write the Nodes on the file.
            objectMapper.writeValue(file, jsonNodes);

        } catch (IOException e) {
            // In case of failure, the previous file still exists.
            System.err.println("Failed to save priorities: " + e.getMessage());
        }
    }
}
