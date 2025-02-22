package org.taskmanager.taskmanager.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;

import org.taskmanager.taskmanager.utils.TaskStateUtils;

/**
 * This class represents a Task, the main element of the Task manager.
 * It has 8 parameters that consist of the id, name, description, deadline,
 * categoryId, priorityId (both translated to the name of their respectful id),
 * Notification list and state (enumeration). There are 2 Constructors, one
 * for when a new Task is added to the application by the user, and one for when tasks
 * are loaded to the application from the task Json file.
 * There are 7 setters and 8 getters in this class, as well as 4 more methods
 * that assist with properly running the application and returning information about
 * the task.
 * Notifications were added as a list inside each task as they belong to a
 * specific task every time and this implementation added less complexity to the application
 * code.
 */
public class Task {

    private int taskId;
    private String name;
    private String description;
    private String deadline;
    private int categoryId;
    private int priorityId;
    private List<Notification> notifications = new ArrayList<>();
    private TaskStateUtils state;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    /*
         ID numbering starts from 0, and it goes up one value per task, it is static.
      */
    private static int idNum = 0;

    /**
     * Constructor used when the user is directly adding a task to the application.
     * The id is preset by idNum.
     * @param name The name of the application.
     * @param description The description of the application.
     * @param deadline The deadline of the application.
     * @param categoryId The id of the category this task belongs to.
     * @param priorityId The id of the priority level this task belongs to.
     * @param state The state of the task, belongs to TaskStateUtils.
     */
    public Task(String name, String description, String deadline, int categoryId, int priorityId, TaskStateUtils state) {
        this.taskId = idNum++;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.categoryId = categoryId;
        this.priorityId = priorityId;
        this.state = state;
    }

    /**
     * Constructor used by the TaskJsonUtils to recreate tasks
     * upon loading the application, created in the previous runs of the application.
     * idNum changes according to the highest value of id found.
     * @param pre_id The previous id of the Task.
     * @param name The name of the Task.
     * @param description The Description of the Task.
     * @param deadline The deadline of the Task.
     * @param categoryId The id of the task's category.
     * @param priorityId The id of the task's priority.
     * @param notifications The list of notifications for the task.
     * @param state The state of the task, belongs to TaskStateUtils.
     */
    public Task(int pre_id, String name, String description, String deadline, int categoryId, int priorityId, List<Notification> notifications, TaskStateUtils state) {
        this.taskId = pre_id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.categoryId = categoryId;
        this.priorityId = priorityId;
        this.notifications = notifications;
        this.state = state;

        /*
            idNum has a value of idMax + 1, where idMax is the
            highest ID value found in the Category JSON file.
        */
        if (idNum < pre_id + 1){
            idNum = pre_id + 1;
        }
    }

    /**
     * Returns the id of the task.
     * @return The id of the task.
     */
    public int getTaskId() {
        return taskId;
    }

    /**
     * Sets the new id of the task.
     * @param taskId The new task id.
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    /**
     * Returns the name of the task.
     * @return The name of the task.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the new name of the task.
     * @param name The new Task name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of the task.
     * @return The description of the task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the new description of the task.
     * @param description The new Task description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the deadline of the task.
     * @return The deadline of the task.
     */
    public String getDeadline() {
        return deadline;
    }

    /**
     * Sets the new deadline of the task.
     * @param deadline The new Task deadline.
     */
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    /**
     * Returns the category id the task belongs to.
     * @return The category id the task belongs to.
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the new category id of the task.
     * @param categoryId The new category id of the Task.
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Returns the priority id the task belongs to.
     * @return The priority id the task belongs to.
     */
    public int getPriorityId() {
        return priorityId;
    }

    /**
     * Sets the new priority id of the task.
     * @param priorityId The new priority id of the Task.
     */
    public void setPriorityId(int priorityId) {
        this.priorityId = priorityId;
    }

    /**
     * Returns the list of the notifications of the task.
     * @return The list of the notifications of the task.
     */
    public List<Notification> getNotifications() {
        return this.notifications;
    }

    /**
     * Returns the state of the task.
     * @return The state of the task.
     */
    public TaskStateUtils getState() {
        return this.state;
    }

    /**
     * Sets the new state of the task.
     * @param state The new Task state.
     */
    public void setState(TaskStateUtils state) {
        this.state = state;
    }

    /**
     * This method is used when we first open the application and
     * check whether one of the old tasks is delayed and changes its state.
     * It checks if the deadline is before the current date when
     * the user opens the application. The check happens only for
     * tasks that are not already completed and directly inside the
     * readTaskListFromFile in order to save them properly to the repository
     * and avoid a second for-loop.
     */
    public void checkIfDelayed(){

        if (state != TaskStateUtils.COMPLETED) {

            LocalDate dueDate = LocalDate.parse(this.deadline, formatter);
            LocalDate today = LocalDate.now();

            if (dueDate.isBefore(today)) {
                setState(TaskStateUtils.DELAYED);
            }
        }
    }

    /**
     * Shows directly whether the state of the task is delayed,
     * it is used for the summary in the main application.
     * @return boolean True if the state is Delayed, else false.
     */
    public boolean isDelayed() {
        return this.state == TaskStateUtils.DELAYED;
    }

    /**
     * Shows directly whether the state of the task is completed,
     * it is used for the summary in the main application.
     * @return boolean True if the state is Completed, else false.
     */
    public boolean isCompleted() {
        return this.state == TaskStateUtils.COMPLETED;
    }

    /**
     * Shows directly whether the state of the task is due in 7 days,
     * it is used for the summary in the main application.
     * @return boolean True if the state is due in 7 days, else false.
     */
    public boolean isDueInSevenDays() {
        LocalDate dueDateHelp = LocalDate.parse(this.deadline, this.formatter);
        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(today, dueDateHelp);
        return daysBetween >= 0 && daysBetween <= 7;
    }
}
