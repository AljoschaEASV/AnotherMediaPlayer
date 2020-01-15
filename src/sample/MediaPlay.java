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

    /**
     * The Playlist.
     */
    private String playlist;

    /**
     * Instantiates a new Media play.
     */
    MediaPlay() {
        this.title = "";
        this.orderNo = "";
        this.playlist = "";
    }

    /**
     * Instantiates a new Media play.
     *
     * @param title    the title
     * @param orderNo  the order no
     * @param playlist the playlist
     */
    MediaPlay(String title, String orderNo, String playlist) {
        this.title = title;
        this.orderNo = orderNo;
        this.playlist = playlist;
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
        this.orderNo = orderNo;
    }

    /**
     * Get playlist string.
     *
     * @return the string
     */
    public String getPlaylist() {
        return this.playlist;
    }

    /**
     * Set playlist.
     *
     * @param playlist the playlist
     */
    public void setPlaylist(String playlist) {
        this.playlist = playlist;
    }
}
