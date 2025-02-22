package org.taskmanager.taskmanager.model;

/**
 * This is a Class representing a Category of the application.
 * A category is essentially a name that the user can provide to
 * a task, and it organises tasks of the same category them together.
 * It has 2 Constructors, one for creating a new Category
 * through the application and one for creating a new Category
 * from the JSON file (which has already been given an id).
 * There are 4 methods, 2 setters and 2 getters, one for each parameter
 * of the Class so that the application can interact properly with categories.
 */
public class Category {

    private int categoryID;
    private String name;

    /* 
        ID numbering starts from 0, and it goes up one value per category, it is static. 
        Uncategorized has ID -1.
     */
    private static int idNum = 0;

    /**
     * Constructor for new Category that the User can
     * add to the application. The id is preset by idNum.
     * @param name The name of the new Category is required.
     */
    public Category(String name) {
        this.categoryID = idNum++;
        this.name = name;
    }

    /**
     * Constructor used by the CategoryJsonUtils to recreate categories
     * upon loading the application, created in the previous runs of the application.
     * idNum changes according to the highest value of id found.
     * @param id The id that was provided to the Category.
     * @param name The name that was given to the Category.
     */
    public Category(int id, String name) {
        this.categoryID = id;
        this.name = name;

        /* 
            idNum has a value of idMax + 1, where idMax is the 
            highest ID value found in the Category JSON file.
        */
        if (idNum < id + 1){
            idNum = id + 1;
        }
    }

    /**
     * Method that returns the ID of the category
     * @return int categoryID
     */
    public int getCategoryId() {
        return categoryID;
    }

    /**
     * Method that sets a new value for the category ID, never used
     * @param categoryId The new ID for the category.
     */
    public void setCategoryId(int categoryId) {
        this.categoryID = categoryId;
    }

    /**
     * Method that returns the name of the category
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * Method that sets a new value for the category name
     * @param name The new name for the category.
     */
    public void setName(String name) {
        this.name = name;
    }
}
