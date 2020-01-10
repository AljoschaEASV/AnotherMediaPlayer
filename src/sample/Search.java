package sample;

import javafx.beans.property.SimpleStringProperty;

/**
 * This class is used to make the TableView and to be able to edit them
 */
public class Search {


    /**
     * The Title.
     */
    private SimpleStringProperty title;
    /**
     * The Category.
     */
    private SimpleStringProperty category;

    /**
     * Instantiates a new Search.
     *
     * @param title    the title
     * @param category the category
     */
    public Search(String title, String category){
        this.title = new SimpleStringProperty(title);
        this.category = new SimpleStringProperty(category);



    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title.get();
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title.set(title);
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    public String getCategory() {
        return category.get();
    }

    /**
     * Sets category.
     *
     * @param category the category
     */
    public void setCategory(String category) {
        this.category.set(category);
    }

}
