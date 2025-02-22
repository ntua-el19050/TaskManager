package org.taskmanager.taskmanager.model;

/**
 * This is a Class representing a Priority of the application.
 * A priority is essentially a level that the user can provide to
 * a task to show the priority level it has.
 * It has 2 Constructors, one for creating a new Priority
 * through the application and one for creating a new Priority
 * from the JSON file (which has already been given an id).
 * There are 4 methods, 2 setters and 2 getters, one for each parameter
 * of the Class so that the application can interact properly with priorities.
 */
public class Priority {

    private int priorityID;
    private String level;

    /*
        ID numbering starts from 0, and it goes up one value per priority, it is static.
        Default has ID -1.
     */
    private static int idNum = 0;

    /**
     * Constructor for new Priority that the User can
     * add to the application.
     * @param level The level of the new Priority is required,
     *             the ID is preset from idNum.
     */
    public Priority(String level) {
        this.priorityID = idNum++;
        this.level = level;
    }

    /**
     * Constructor used by the PriorityJsonUtils to recreate priorities
     * upon loading the application, created in the previous runs of the application.
     * idNum changes according to the highest value of id found.
     * @param id The id that was provided to the Priority.
     * @param level The level that was given to the Priority.
     */
    public Priority(int id, String level) {
        this.priorityID = id;
        this.level = level;

        /*
            idNum has a value of idMax + 1, where idMax is the
            highest ID value found in the Priority JSON file.
        */
        if (idNum < id + 1){
            idNum = id + 1;
        }
    }

    /**
     * Method that returns the ID of the priority
     * @return int priorityID
     */
    public int getPriorityId() {
        return priorityID;
    }

    /**
     * Method that sets a new value for the priority ID, never used
     * @param priorityID The new ID for the priority.
     */
    public void setCategoryId(int priorityID) {
        this.priorityID = priorityID;
    }

    /**
     * Method that returns the level of the priority
     * @return String level
     */
    public String getLevel() {
        return level;
    }

    /**
     * Method that sets a new value for the priority level
     * @param level The new level for the priority.
     */
    public void setLevel(String level) {
        this.level = level;
    }
}
