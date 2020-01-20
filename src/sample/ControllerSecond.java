package sample;

import com.jfoenix.controls.JFXComboBox;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import sample.Infrastructure.DB;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

/**
 * The second controller for the playlist Viewer.
 */
public class ControllerSecond {

    /**
     * The Tb data.
     */
// Tableview instances
    @FXML
    TableView<MediaFile> tbData;
    /**
     * The Tb data 2.
     */
    @FXML
    TableView<MediaPlay> tbPlaylist;
    /**
     * The Title.
     */
    @FXML
    TableColumn<MediaFile, String> title;

    /**
     * The Category.
     */
    @FXML
    TableColumn<MediaFile, String> category;

    /**
     * The Title inside the playlist view.
     */
    @FXML
    TableColumn<MediaPlay, String> title2;

    /**
     * the PlaylistOrdernumber.
     */

    @FXML
    TableColumn<MediaPlay, String> number2;

    /**
     * The Playlist.
     */
    @FXML
    TableColumn<MediaPlay, String> playlist;

    /**
     * The Combo playlist.
     */
    @FXML
    JFXComboBox comboPlaylist;
    /**
     * The Filter field.
     */
    @FXML
    TextField filterField;
    /**
     * The Media file.
     */
    private ObservableList<MediaFile> mediaFile = FXCollections.observableArrayList();
    private ArrayList<MediaPlayer> videosForListPlay;
    /**
     * The Media play. Contains the Media Files
     */

    private ObservableList<MediaPlay> mediaPlay = FXCollections.observableArrayList();
    /**
     * The Playlists.
     */
    private ObservableList<String> playlists = FXCollections.observableArrayList();
    /**
     * The Media player Stage for the go back button.
     */
    private Stage mediaPlayer;


    private Controller mainController;

    public ControllerSecond(Controller mainController) {
        this.mainController = mainController;
    }

    /**
     * This method initializes the Cell values inside the playlistView stage.
     * Using the different Observeable Lists and inputs it into the table.
     */
    public void initialize() {

        title.setCellValueFactory(new PropertyValueFactory<>("Title"));
        category.setCellValueFactory(new PropertyValueFactory<>("Category"));

        title2.setCellValueFactory(new PropertyValueFactory<>("Title"));
        number2.setCellValueFactory(new PropertyValueFactory<>("OrderNo"));
        playlist.setCellValueFactory(new PropertyValueFactory<>("Playlist"));

        // Here we make use of the observable list data and inputs it into the table.
        tbData.setItems(mediaFile);
        tbPlaylist.setItems(mediaPlay);
        comboPlaylist.setItems(playlists);

        loadVideoList();
        loadComboBox();
        comboPlaylist.getSelectionModel().select("default");
        loadPlaylist();
    }

    /**
     * Method to select and move a row from the mediaFiles table containing the songs to a playlist. "add song button"
     */
    public void select() {

        MediaFile selection = tbData.getSelectionModel().getSelectedItem();
        String playlistName = String.valueOf(comboPlaylist.getSelectionModel().getSelectedItem());
        String entry;
        int playNo;

        if (selection != null) {
            if (playlistName.equals("") || playlistName.equals("default")) {
                playlistName="default";
                DB.selectSQL("select OrderNo from tblVideoOrder where PlaylistName='default' order by OrderNo desc");
                entry = DB.getData();
                if (!entry.equals("|ND|")) {
                    playNo = Integer.parseInt(entry) + 1;
                    mediaPlay.add(new MediaPlay(selection.getTitle(), Integer.toString(playNo), playlistName));
                    DB.manualDisconnect();
                    DB.insertSQL("insert into tblVideoOrder (Video, PlaylistName, OrderNo) values ('" + selection.getTitle() + "', '" + playlistName + "', " + playNo + ")");
                } else {
                    playNo = 1;
                    mediaPlay.add(new MediaPlay(selection.getTitle(), Integer.toString(playNo), playlistName));
                    playlists.add(playlistName);
                    DB.manualDisconnect();
                    DB.insertSQL("insert into tblVideoOrder (Video, PlaylistName, OrderNo) values ('" + selection.getTitle() + "', '" + playlistName + "', " + playNo + ")");
                }
            } else {
                DB.selectSQL("select OrderNo from tblVideoOrder where PlaylistName='" + playlistName + "' order by orderNo desc");
                entry=DB.getData();
                if(!entry.equals("|ND|")) {
                    playNo = Integer.parseInt(entry) + 1;
                    mediaPlay.add(new MediaPlay(selection.getTitle(), Integer.toString(playNo), playlistName));
                    DB.manualDisconnect();
                    DB.insertSQL("insert into tblVideoOrder (Video, PlaylistName, OrderNo) values ('" + selection.getTitle() + "', '" + playlistName + "', " + playNo + ")");
                } else {
                    playNo = 1;
                    mediaPlay.add(new MediaPlay(selection.getTitle(), Integer.toString(playNo), playlistName));
                    playlists.add(playlistName);
                    DB.manualDisconnect();
                    DB.insertSQL("insert into tblVideoOrder (Video, PlaylistName, OrderNo) values ('" + selection.getTitle() + "', '" + playlistName + "', " + playNo + ")");
                }
            }
        }
    }

    /**
     * Delete entries from the playlist selecting a song inside a playlist and pressing the delete button.
     * The song has to be choosen
     */
    public void deleteEntry() {

        MediaPlay selection = tbPlaylist.getSelectionModel().getSelectedItem();
        if (selection != null) {
            String OrderNo = selection.getOrderNo();
            String playlistName = selection.getPlaylist();
            String ID;
            DB.selectSQL("select ID from tblVideoOrder where OrderNo=" + OrderNo + " and PlaylistName='" + playlistName + "' and Video='" + selection.getTitle() + "'");
            ID=DB.getData();
            DB.manualDisconnect();
            DB.deleteSQL("delete from tblVideoOrder where ID=" + ID);
            Main.arrangeVideoOrder();
            loadPlaylist();
        }
    }

    /**
     * Delete playlist. Use the dumpster Button
     */
    public void deletePlaylist() {
        String playlistName = String.valueOf(comboPlaylist.getSelectionModel().getSelectedItem());
        if (!playlistName.equals("")) {
            DB.deleteSQL("delete from tblVideoOrder where PlaylistName='" + playlistName + "'");
            loadPlaylist();
            loadComboBox();
            comboPlaylist.getSelectionModel().select("default");
        }
    }

    /**
     * MoveUp enables the button on playlist Stage to move the selected song up inside the Playlist.
     * And thereby change the order of the playlist
     *
     * @see #loadPlaylist()
     */
    public void moveUp() {

        MediaPlay selection = tbPlaylist.getSelectionModel().getSelectedItem();
        if (selection != null) {
            String video = selection.getTitle();
            int orderNo = Integer.parseInt(selection.getOrderNo());
            String playlistName = selection.getPlaylist();
            DB.selectSQL("select ID from tblVideoOrder where PlaylistName='" + playlistName + "' and OrderNo=" + orderNo);
            String ID = DB.getData();
            DB.manualDisconnect();
            if (!ID.equals("|ND|") && orderNo > 1) {
                DB.manualDisconnect();
                DB.updateSQL("update tblVideoOrder set OrderNo=" + orderNo + " where PlaylistName='" + playlistName + "' and OrderNo=" + (orderNo - 1));
                DB.updateSQL("update tblVideoOrder set OrderNo=" + (orderNo - 1) + " where PlaylistName='" + playlistName + "' and OrderNo=" + orderNo + " and ID=" + ID);
                loadPlaylist();
            }
        }
    }

    /**
     * @see #moveUp()
     */
    public void moveDown() {
        MediaPlay selection = tbPlaylist.getSelectionModel().getSelectedItem();
        if (selection != null) {
            String video = selection.getTitle();
            int orderNo = Integer.parseInt(selection.getOrderNo());
            String playlistName = selection.getPlaylist();
            DB.selectSQL("select ID from tblVideoOrder where PlaylistName='" + playlistName + "' and OrderNo=" + orderNo);
            String ID = DB.getData();
            DB.manualDisconnect();
            DB.selectSQL("select OrderNo from tblVideoOrder where PlaylistName='" + playlistName + "' order by OrderNo desc");
            int maxOrderNo = Integer.parseInt(DB.getData());
            if (!ID.equals("|ND|") && orderNo < maxOrderNo) {
                DB.manualDisconnect();
                DB.updateSQL("update tblVideoOrder set OrderNo=" + orderNo + " where PlaylistName='" + playlistName + "' and OrderNo=" + (orderNo + 1));
                DB.updateSQL("update tblVideoOrder set OrderNo=" + (orderNo + 1) + " where PlaylistName='" + playlistName + "' and OrderNo=" + orderNo + " and ID=" + ID);
                loadPlaylist();
            }
        }
    }

    /**
     * (Re-)Load video list.
     */
    public void loadVideoList() {
        String entry = "";
        String entryTitle = "";
        String entryCategory = "";
        DB.selectSQL("select Title, Category from tblVideos");

        do {
            entry = DB.getData();
            if (!entry.equals("|ND|")) entryTitle = entry;
            else break;
            entry = DB.getData();
            if (!entry.equals("|ND|")) entryCategory = entry;
            else break;

            mediaFile.add(new MediaFile(entryTitle, entryCategory));
        } while (true);
    }

    /**
     * Adds the search function for the left textField to find a specific song inside the media Files.
     * Set the filter Predicate whenever the filter changes.
     * Wraps the FilteredList in a SortedList.
     * Compares title and category with filter text.
     * Wrap the FilteredList in a SortedList.
     * Add sorted (and filtered) data to the table.
     */

    public void filter() {
        FilteredList<MediaFile> filteredData = new FilteredList<>(mediaFile, p -> true);


        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(mediaFile -> {
                // If filter text is empty, display all
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (mediaFile.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter with title.
                } else return mediaFile.getCategory().toLowerCase().contains(lowerCaseFilter); // Filter with category.
                // Does not match.
            });
        });

        SortedList<MediaFile> sortedData = new SortedList<>(filteredData);
        //Bind the SortedList comparator to the TableView comparator.
        //Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tbData.comparatorProperty());
        tbData.setItems(sortedData);
    }

    /**
     * Load playlist - Adds the functionality into the Combobox showing the Playlists inside the dropDown menu.
     */
    public void loadPlaylist() {
        String playlistName = String.valueOf(comboPlaylist.getSelectionModel().getSelectedItem());
        String entry = "";
        String entryTitle = "";
        String entryPlayNo = "";
        mediaPlay.clear();

        if (playlistName.equals("") || playlistName.equals("default")) {
            DB.selectSQL("select Video, OrderNo from tblVideoOrder where PlaylistName='default' order by OrderNo asc");
            playlistName = "default";
            do {
                entry = DB.getData();
                if (!entry.equals("|ND|")) entryTitle = entry;
                else break;
                entry = DB.getData();
                if (!entry.equals("|ND|")) entryPlayNo = entry;
                else break;

                mediaPlay.add(new MediaPlay(entryTitle, entryPlayNo, playlistName));
            } while (true);
        } else {
            DB.selectSQL("select Video, OrderNo from tblVideoOrder where PlaylistName='" + playlistName + "' order by OrderNo asc");
            do {
                entry = DB.getData();
                if (!entry.equals("|ND|")) entryTitle = entry;
                else break;
                entry = DB.getData();
                if (!entry.equals("|ND|")) entryPlayNo = entry;
                else break;

                mediaPlay.add(new MediaPlay(entryTitle, entryPlayNo, playlistName));
            } while (true);
        }
    }

    /**
     * Load combo box.
     */

    public void loadComboBox() {
        playlists.clear();
        DB.manualDisconnect();
        DB.selectSQL("select distinct PlaylistName from tblVideoOrder");
        String entry = DB.getData();
        while (!entry.equals("|ND|")) {
            playlists.add(entry);
            entry = DB.getData();
        }
    }

    public void openPlayListManager(ActionEvent event) {

        try
        {
            if (mediaPlayer == null)
            {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));

                Parent root2 = fxmlLoader.load();

                mediaPlayer = new Stage();

                mediaPlayer.setScene(new Scene(root2));
                mediaPlayer.show();
            }else if (mediaPlayer.isShowing())
            {
                mediaPlayer.toFront();
            }else
            {
                mediaPlayer.show();
            }





        }catch (Exception e)
        {
            System.out.println("Can't load the window");
        }

    }
/*
    public void chooseMusic() {
        tbData.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Controller entries = new Controller();
                tbPlaylist.getItems();

                DB.selectSQL("select AbsolutePath from tblVideos where Title='" + entries.getPlaylistentries() + "");
            }
        });

    }
 */

    @FXML

   public void runSingleChoice(MouseEvent event) {

    try {
    tbData.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    MediaFile selection = tbData.getSelectionModel().getSelectedItem();

                    mainController.runSingleChoice(selection);
                }

            }
        }
    });
    }catch (NullPointerException e)
    {
        System.out.println("Error");
    }
    }

    public void runListChoice(MouseEvent event) {
        try {
            tbPlaylist.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(event.getButton().equals(MouseButton.PRIMARY)) {
                        if (event.getClickCount() == 2) {
                            MediaPlay selection = tbPlaylist.getSelectionModel().getSelectedItem();

                            mainController.runListChoice(selection);
                        }

                    }
                }
            });
        }catch (NullPointerException e)
        {
            System.out.println("Error");
        }
    }

    public void addCategory(){
        MediaFile selection = tbData.getSelectionModel().getSelectedItem();

        TextInputDialog dialog = new TextInputDialog("Tran");

        dialog.setTitle("Category");
        dialog.setHeaderText("Please enter new categories");
        dialog.setContentText("Come on do it now:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            tbData.getSelectionModel().getSelectedItem().setCategory(result.get());

            DB.updateSQL("update tblVideos set Category='" + result.get() + "' where Title='" + selection.getTitle() + "'");
            DB.manualDisconnect();
            loadVideoList();
        });
    }
}