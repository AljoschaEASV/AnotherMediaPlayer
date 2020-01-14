package sample;

/**
 * This class is used to make the TableView and to be able to edit them
 */
public class MediaPlay {
    /**
     * The Title.
     */
    private String title;
    /**
     * The Category.
     */
    private String orderNo;

    private String playlist;

    MediaPlay(){
        this.title="";
        this.orderNo="";
        this.playlist="";
    }

    MediaPlay(String title, String orderNo, String playlist){
        this.title=title;
        this.orderNo=orderNo;
        this.playlist=playlist;
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
     * @return the playOrder2
     */
    public String getOrderNo() {
        return this.orderNo;
    }

    /**
     * Sets category.
     *
     * @param orderNo the category
     */
    public void setOrderNo(String orderNo) {
        this.orderNo=orderNo;
    }

    public String getPlaylist(){
        return this.playlist;
    }

    public void setPlaylist(String playlist){
        this.playlist=playlist;
    }
}
