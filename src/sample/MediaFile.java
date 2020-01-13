package sample;

/**
 * This class is used to make the TableView and to be able to edit them
 */
public class MediaFile {
    /**
     * The Title.
     */
    private String title;
    /**
     * The Category.
     */
    private String category;

    MediaFile(){
        this.title="";
        this.category="";
    }

    MediaFile(String title, String category){
        this.title=title;
        this.category=category;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title=title;
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * Sets category.
     *
     * @param category the category
     */
    public void setCategory(String category) {
        this.category=category;
    }

}
