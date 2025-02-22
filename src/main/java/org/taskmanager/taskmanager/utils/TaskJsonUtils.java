package org.taskmanager.taskmanager.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.taskmanager.taskmanager.model.*;

/**
 *  This class is used for the communication between the
 *  Task Repository and the JSON document. It includes
 *  2 methods, one for reading a Task List from a JSON
 *  file and one for writing a Task List on a JSON file.
 */

public class TaskJsonUtils {

    /**
     * The constructor of the TaskJsonUtils which is never used or called.
     */
    public  TaskJsonUtils(){}

    /**
     * This method is used to read the JSON file with the
     * Tasks stored in it. The Tasks are then
     * stored in the Task Repository and used for all
     * the CRUD methods in the application revolving around
     * Tasks.
     *
     * @param filePath The file path to the JSON file.
     * @return The list of all Tasks from the JSON file.
     * @throws IOException Returns an empty list in case of an error.
     */
    public static List<Task> readTaskListFromFile(String filePath) throws IOException {

        File file = new File(filePath);

        /* 
            Checking whether the file exists. If it doesn't exist, it simply returns
            an empty list.
        */
        if (!file.exists() || file.length() == 0) {
            System.out.println("No tasks found. Returning an empty list.");
            return new ArrayList<>();
        }

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNodes = objectMapper.readTree(file);
            List<Task> tasks = new ArrayList<>();

            for (JsonNode jsonNode : jsonNodes) {

                if (jsonNode.isEmpty()) {
                    return new ArrayList<>();
                }

                List<Notification> notifications = new ArrayList<>();
                JsonNode notificationsNode = jsonNode.get("notifications");

                /*
                    From each jsonNode create a notification. Notifications are
                    stored in JsonNodes inside the task json node.
                */
                if (notificationsNode != null && notificationsNode.isArray()) {
                    for (JsonNode notificationNode : notificationsNode) {
                        String message = notificationNode.get("message").asText();
                        String date = notificationNode.get("date").asText();

                        notifications.add(new Notification(message, date));
                    }
                }

                // From each jsonNode create a task.
                Task task = new Task(jsonNode.get("taskID").asInt(),
                        jsonNode.get("name").asText(),
                        jsonNode.get("description").asText(),
                        jsonNode.get("dueDate").asText(),
                        jsonNode.get("categoryID").asInt(),
                        jsonNode.get("priorityID").asInt(),
                        notifications,
                        TaskStateUtils.fromString(jsonNode.get("state").asText())
                );

                /*
                    Checking directly here if the task is delayed in order to
                    directly add it to the application with the correct state.
                */
                task.checkIfDelayed();
                tasks.add(task);
            }
            return tasks;

            // In the case of invalid JSON format or any error, return an empty list.
        } catch (JsonParseException | JsonMappingException e) {
            System.err.println("Invalid JSON format. Returning an empty list.");
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * This method is used to write in the JSON file with the
     * Tasks stored in the application memory, inside the Task Repository.
     * The Tasks are then stored in the JSON file with the proper name and
     * will be used again once we open the application.
     *
     * @param filePath The file path to the JSON file.
     * @param tasks    The list of Tasks that were used in the application and will be stored.
     * @throws IOException In case of an error nothing is saved and the JSON file remains untouched.
     */
    public static void writeTaskListToFile(String filePath, List<Task> tasks) throws IOException {

        File file = new File(filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<ObjectNode> jsonNodes = new ArrayList<>();

        try {
            
            /* 
                In this for-loop we create jsonNodes based on the Tasks we have
                and create an ArrayList of jsonNodes to write in our file.
            */
            for (Task task : tasks) {

                ObjectNode jsonNode = objectMapper.createObjectNode();

                ArrayNode notificationsArray = objectMapper.createArrayNode();

                /*
                    First we need to create a Json Node of notifications in order
                    to store them properly inside the task.
                 */
                for (Notification notification : task.getNotifications()) {
                    ObjectNode notificationNode = objectMapper.createObjectNode();
                    notificationNode.put("notificationId", notification.getNotificationId());
                    notificationNode.put("message", notification.getMessage());
                    notificationNode.put("date", notification.getNotificationDate());
                    notificationsArray.add(notificationNode);
                }

                // Then we create the Task JsonNode and we include notifications there.
                jsonNode.put("taskID", task.getTaskId());
                jsonNode.put("name", task.getName());
                jsonNode.put("description", task.getDescription());
                jsonNode.put("dueDate", task.getDeadline());
                jsonNode.put("categoryID", task.getCategoryId());
                jsonNode.put("priorityID", task.getPriorityId());
                jsonNode.set("notifications", notificationsArray);
                jsonNode.put("state", task.getState().toString());
                jsonNodes.add(jsonNode);
            }

            // The objectMapper is used to properly write the Nodes on the file.
            objectMapper.writeValue(file, jsonNodes);
        } catch (IOException e) {
            // In case of failure, the previous file still exists.
            System.err.println("Failed to save tasks: " + e.getMessage());
        }
    }
}