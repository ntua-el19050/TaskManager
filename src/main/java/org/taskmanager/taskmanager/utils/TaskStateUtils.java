package org.taskmanager.taskmanager.utils;

/**
 * This is an enumeration used to organise properly
 * the state of the tasks in the application. There
 * are 5 different states:
 * Open - Task was just opened
 * In Progress - Task is in progress, has started
 * Postponed - Task is postponed and possibly the deadline goes later as well.
 * Completed - Task is completed.
 * Delayed - Task has been delayed, the current date is after the Task due date.
 * It contains methods for displaying TaskStateUtils.
 */
public enum TaskStateUtils {

    /**
     * Represents an open task.
     */
    OPEN("Open"),
    /**
     * Represents a task in progress.
     */
    IN_PROGRESS("In Progress"),
    /**
     * Represents a postponed task.
     */
    POSTPONED("Postponed"),
    /**
     * Represents a completed task.
     */
    COMPLETED("Completed"),
    /**
     * Represents a delayed task, can only be set by the application itself.
     */
    DELAYED("Delayed");

    private final String displayName;

    /**
     * Constructor of the enumerator.
     * @param displayName The name that is displayed when we need to display it in String.
     */
    TaskStateUtils(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Overrides the toString() method and returns the displayName.
     * @return String returns the display name as String.
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Method used to turn String state to TaskStateUtils state,
     * only by the readTaskListFromFile method in TaskJsonUtils.
     * @param text Receives a String of text that will be turned into one
     *             of the 5 different states.
     * @return TaskStateUtils Returns the state that the text corresponds to, or
     *         Open if it is empty or corresponds to invalid state.
     */
    public static TaskStateUtils fromString(String text) {

        if (text == null) {
            return TaskStateUtils.OPEN;
        }
        for (TaskStateUtils state : TaskStateUtils.values()) {
            if (state.displayName.equalsIgnoreCase(text)) {
                return state;
            }
        }
        System.out.println("No matching TaskState for: " + text + ", returning Open State");
        return TaskStateUtils.OPEN;
    }
}
