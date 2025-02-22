package org.taskmanager.taskmanager.repository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.taskmanager.taskmanager.model.*;
import org.taskmanager.taskmanager.utils.TaskJsonUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.taskmanager.taskmanager.utils.TaskStateUtils;

/**
 * This class is a repository of all the tasks as well as
 * the notifications. It contains all the tasks, and by extend
 * the notifications, in memory when the application is running.
 * It contains all methods for addition, update and delete for tasks
 * and notifications. Furthermore, it returns the tasks list, but also operates
 * as the connector between the controllers and the models and JsonUtils.
 * Notifications were included here as they are a list inside each task,
 * and it was considered more concise than creating a new repository for it.
 */
public class TaskRepository {

    private static final String TASK_FILE = "medialab/tasks.json";

    private static final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Constructor of the Task Repository, only called in MediaLabController
     * and then passed on in the apps it is needed to avoid having duplicate repositories.
     * It essentially loads all the tasks (and notifications) from the Json file.
     */
    public TaskRepository() {
        loadTasks();
    }

    /**
     * The actual loading happens in this method that reads the tasks from
     * the JSON tasks file and then sets the tasks list properly.
     */
    private void loadTasks() {
        try {
            List<Task> loadedTasks = TaskJsonUtils.readTaskListFromFile(TASK_FILE);
            tasks.setAll(loadedTasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method creates a new task and adds it to the repository.
     * @param taskName The name of the task.
     * @param description The description of the task.
     * @param deadline The deadline of the task.
     * @param categoryId The id of the category to which the task belongs to.
     * @param PriorityId The id of the priority to which the task belongs to.
     * @param state The state of the task.
     */
    public void add(String taskName, String description, String deadline, int categoryId, int PriorityId, TaskStateUtils state) {

        Task newTask = new Task(taskName, description, deadline, categoryId, PriorityId, state);
        tasks.add(newTask);
    }

    /**
     * This method updates a task that already exists in the repository.
     * Based on the id we find the task that corresponds to it from the
     * tasks list. After it finds the task, it sets the new and updated
     * parameters, which are directly passed to the repository. This was
     * in order to keep the task id the same after the update.
     * Furthermore, in case that the state is set to Completed,
     * all notifications are deleted for this task and the window
     * closes if it is open.
     * @param selectedTask The task selected for update.
     * @param taskName The updated name of the task.
     * @param description The updated description of the task.
     * @param dueDate The updated due date of the task.
     * @param categoryId The updated category id of the task.
     * @param priorityId The updated priority id of the task.
     * @param state The updated state of the task.
     */
    public void update(Task selectedTask, String taskName, String description, String dueDate, int categoryId, int priorityId, TaskStateUtils state) {

        Optional<Task> existingTask = tasks.stream()
                .filter(task -> task.getTaskId() == selectedTask.getTaskId())
                .findFirst();

        existingTask.ifPresent(task -> {

            task.setName(taskName);
            task.setDescription(description);
            task.setDeadline(dueDate);
            task.setCategoryId(categoryId);
            task.setPriorityId(priorityId);
            task.setState(state);

            // In case the state is set to completed, delete all notifications.
            if (state == TaskStateUtils.COMPLETED) {
                task.getNotifications().clear();
            }
            else {
                /*
                    If it's not completed, check if the change of due date makes notifications irrelevant.
                    This was added as it wouldn't make sense to have notifications after the updated due date.
                */
                for (Notification notification : task.getNotifications()) {
                    if (LocalDate.parse(dueDate, formatter).isBefore(LocalDate.parse(notification.getNotificationDate(), formatter))) {
                        deleteNotification(task, notification);
                    }
                }
            }
        });
    }

    /**
     * This method deletes a task from the repository based on its id.
     * Basically if the id of a task matches the id of the selected task,
     * the task is deleted. removeIf method makes this easier to do.
     * @param taskID The id of the to-be-deleted task.
     */
    public void delete(int taskID) {
        tasks.removeIf(task -> task.getTaskId() == taskID);
    }

    /**
     * Include a new notification to the task's notification list.
     * The method is here since it changes the task, and we wanted
     * changes to happen mainly in the repositories.
     * @param task The task to which the notification will be added.
     * @param message The message of the new notification.
     * @param notificationDate The date of the new notification.
     */
    public void addNotification(Task task, String message, String  notificationDate) {
        Notification newNotification = new Notification(message, notificationDate);
        task.getNotifications().add(newNotification);
    }

    /**
     * This method updates a notification. It sets the message and the
     * date the notification will appear to the user. It finds the notification
     * from the list of notifications and then properly updates the notification.
     * @param task The task to which the notification belongs to.
     * @param selectedNotification The notification the user selected to be updated.
     * @param message The updated message of the notification.
     * @param notificationDate The date of the notification.
     */
    public void updateNotification(Task task, Notification selectedNotification, String message, String notificationDate){

        Optional<Notification> existingNotification = task.getNotifications().stream()
                .filter(notification -> notification.getNotificationId() == selectedNotification.getNotificationId())
                .findFirst();

        existingNotification.ifPresent(notification -> {
            selectedNotification.setMessage(message);
            selectedNotification.setNotificationDate(notificationDate);
        });
    }

    /**
     * This method deletes the selected notification from the notifications
     * list of the task to which it belongs to.
     * @param task The task to which the notification belongs to.
     * @param selectedNotification The selected notification to be deleted.
     */
    public void deleteNotification(Task task, Notification selectedNotification){
        task.getNotifications().remove(selectedNotification);
    }

    /**
     * This method filters tasks based on the search inputs of the user
     * and returns a list of the remaining tasks. in the case that there
     * is no input for the name and "Any" is selected for category and priority,
     * all tasks appear and no tasks are filtered. For the name specifically
     * it doesn't have to match perfectly, but it could be contained in the task name.
     * @param name The name the user wants to search for.
     * @param categoryId The id of the category that was selected. -2 represents "Any".
     * @param priorityId The id of the priority that was selected. -2 represents "Any".
     * @return The filtered tasks that match the search requirements of the user.
     */
    public List<Task> searchTasks(String name, int categoryId, int priorityId) {
        return tasks.stream()
                .filter(task -> (name == null || name.isEmpty() || task.getName().toLowerCase().contains(name.toLowerCase())))
                .filter(task -> (categoryId == -2 || task.getCategoryId() == categoryId))
                .filter(task -> (priorityId == -2 || task.getPriorityId() == priorityId))
                .collect(Collectors.toList());
    }

    /**
     * This method is utilised when a priority is deleted. It goes to all
     * tasks which had the priority level that was deleted and changes their
     * priority to "Default" with an id of -1.
     * @param priorityId The id of the recently deleted priority.
     */
    public void ChangeTaskPriority(int priorityId) {
        for (Task task : tasks) {
            if (task.getPriorityId() == priorityId) {
                task.setPriorityId(-1);
            }
        }
    }

    /**
     * This method handles the deletion of a category. When a category
     * is deleted, all the tasks which belong to said category need to
     * be deleted as well. This cannot happen for uncategorized tasks
     * as uncategorized cannot be deleted.
     * @param categoryID The id of the recently deleted category.
     */
    public void deleteTasksByCategory(int categoryID) {
        tasks.removeIf(task -> task.getCategoryId() == categoryID);
    }

    /**
     * This method checks whether at least one delayed task exists
     * in the tasks list. This is used when the application initiates
     * and checks whether the delayedTasksPopUpWindow will be required
     * or not.
     * @return True if there is at least 1 delayed task, False otherwise.
     */
    public boolean hasDelayedTasks() {
        for (Task task : tasks) {
            if (task.getState() == TaskStateUtils.DELAYED) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method shows whether any task has any application
     * to be shown to the user or not. If there is at least one
     * notification with a date before or same with the current date
     * when the application opens, it returns true, otherwise false.
     * @return True if at least one notification needs to be shown to the user, False otherwise.
     */
    public boolean hasNotifications(){

        for (Task task : tasks) {
            for (Notification notification : task.getNotifications()){

                LocalDate notificationDate = LocalDate.parse(notification.getNotificationDate(), formatter);
                LocalDate now = LocalDate.now();

                if (notificationDate.isBefore(now) || notificationDate.isEqual(now)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method loads all the notifications that exist in the application
     * into a NotificationWrapper list which is used for the user to have
     * an overview of the notification on the first page. It is needed
     * mainly so that the user has the necessary information about the task
     * along with the information of the notification itself.
     * @return A list of all notifications along with the id and name of the task they belong to.
     */
    public List<NotificationWrapper> loadNotifications() {

        List<NotificationWrapper> notificationWrappers = new ArrayList<>();

        for (Task task : tasks) {
            for (Notification notification : task.getNotifications()) {
                notificationWrappers.add(new NotificationWrapper(notification, task.getTaskId(), task.getName()));
            }
        }
        return notificationWrappers;
    }

    /**
     * Returns the list of all the tasks in the repository.
     *
     * @return The list of all tasks in the repository.
     */
    public ObservableList<Task> findAll() {
        return tasks;
    }

    /**
     * Calls the writeTaskListToFile method from TaskJsonUtils. It is used only
     * when the MediaLabAssistant window is closed, as we want to save to
     * JSON files only when the whole application terminates.
     */
    public void saveAll() {
        System.out.println("Saving all tasks");
        try {
            TaskJsonUtils.writeTaskListToFile(TASK_FILE, tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
