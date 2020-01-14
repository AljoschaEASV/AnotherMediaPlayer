package sample;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sample.Infrastructure.DB;
import sample.Infrastructure.OrderStruct;

/**
 * The type Controller second.
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
     * The Title 2.
     */
    @FXML
    TableColumn<MediaPlay, String> title2;

    @FXML
    TableColumn<MediaPlay, String> number2;

    @FXML
    TableColumn<MediaPlay, String> playlist;

    @FXML
    JFXComboBox comboPlaylist;

    private ObservableList<MediaFile> mediaFile = FXCollections.observableArrayList();
    private ObservableList<MediaPlay> mediaPlay = FXCollections.observableArrayList();
    private ObservableList<String> playlists = FXCollections.observableArrayList();
    TableColumn<MediaFile, String> category2;
    @FXML
    TextField filterField;

    private Stage mediaPlayer;



    /**
     * This method initializes the Cell values.
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
     * Method to select and move a row from table containing the songs to a playlist
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
                    DB.insertSQL("insert into tblVideoOrder (Video, PlaylistName, OrderNo) values ('" + selection.getTitle() + "', '" + playlistName + "', " + Integer.toString(playNo) + ")");
                } else {
                    playNo = 1;
                    mediaPlay.add(new MediaPlay(selection.getTitle(), Integer.toString(playNo), playlistName));
                    playlists.add(playlistName);
                    DB.manualDisconnect();
                    DB.insertSQL("insert into tblVideoOrder (Video, PlaylistName, OrderNo) values ('" + selection.getTitle() + "', '" + playlistName + "', " + Integer.toString(playNo) + ")");
                }
            } else {
                DB.selectSQL("select OrderNo from tblVideoOrder where PlaylistName='" + playlistName + "' order by orderNo desc");
                entry=DB.getData();
                if(!entry.equals("|ND|")) {
                    playNo = Integer.parseInt(entry) + 1;
                    mediaPlay.add(new MediaPlay(selection.getTitle(), Integer.toString(playNo), playlistName));
                    DB.manualDisconnect();
                    DB.insertSQL("insert into tblVideoOrder (Video, PlaylistName, OrderNo) values ('" + selection.getTitle() + "', '" + playlistName + "', " + Integer.toString(playNo) + ")");
                } else {
                    playNo = 1;
                    mediaPlay.add(new MediaPlay(selection.getTitle(), Integer.toString(playNo), playlistName));
                    playlists.add(playlistName);
                    DB.manualDisconnect();
                    DB.insertSQL("insert into tblVideoOrder (Video, PlaylistName, OrderNo) values ('" + selection.getTitle() + "', '" + playlistName + "', " + Integer.toString(playNo) + ")");
                }
            }
        }
    }

    public void deleteEntry(){
        MediaPlay selection = tbPlaylist.getSelectionModel().getSelectedItem();
        if(selection != null) {
            String OrderNo = selection.getOrderNo();
            String playlistName = selection.getPlaylist();
            DB.manualDisconnect();
            DB.deleteSQL("delete from tblVideoOrder where OrderNo=" + OrderNo + " and PlaylistName='" + playlistName + "'");
            Main.arrangeVideoOrder();
            loadPlaylist();
        }
    }

    public void deletePlaylist(){
        String playlistName = String.valueOf(comboPlaylist.getSelectionModel().getSelectedItem());
        if(!playlistName.equals("")) {
            DB.deleteSQL("delete from tblVideoOrder where PlaylistName='" + playlistName + "'");
            loadPlaylist();
            loadComboBox();
            comboPlaylist.getSelectionModel().select("default");
        }
    }

    public void moveUp(){
        MediaPlay selection = tbPlaylist.getSelectionModel().getSelectedItem();
        if(selection != null){
            String video = selection.getTitle();
            int orderNo = Integer.parseInt(selection.getOrderNo());
            String playlistName = selection.getPlaylist();
            DB.selectSQL("select ID from tblVideoOrder where PlaylistName='" + playlistName + "' and OrderNo=" + orderNo);
            String ID = DB.getData();
            DB.manualDisconnect();
            if(!ID.equals("|ND|") && orderNo>1) {
                DB.manualDisconnect();
                DB.updateSQL("update tblVideoOrder set OrderNo=" + orderNo + " where PlaylistName='" + playlistName + "' and OrderNo=" + (orderNo - 1));
                DB.updateSQL("update tblVideoOrder set OrderNo=" + (orderNo - 1) + " where PlaylistName='" + playlistName + "' and OrderNo=" + orderNo + " and ID=" + ID);
                loadPlaylist();
            }
        }
    }

    public void moveDown(){
        MediaPlay selection = tbPlaylist.getSelectionModel().getSelectedItem();
        if(selection != null){
            String video = selection.getTitle();
            int orderNo = Integer.parseInt(selection.getOrderNo());
            String playlistName = selection.getPlaylist();
            DB.selectSQL("select ID from tblVideoOrder where PlaylistName='" + playlistName + "' and OrderNo=" + orderNo);
            String ID = DB.getData();
            DB.manualDisconnect();
            DB.selectSQL("select OrderNo from tblVideoOrder where PlaylistName='" + playlistName + "' order by OrderNo desc");
            int maxOrderNo = Integer.parseInt(DB.getData());
            if(!ID.equals("|ND|") && orderNo<maxOrderNo) {
                DB.manualDisconnect();
                DB.updateSQL("update tblVideoOrder set OrderNo=" + orderNo + " where PlaylistName='" + playlistName + "' and OrderNo=" + (orderNo + 1));
                DB.updateSQL("update tblVideoOrder set OrderNo=" + (orderNo + 1) + " where PlaylistName='" + playlistName + "' and OrderNo=" + orderNo + " and ID=" + ID);
                loadPlaylist();
            }
        }
    }

    public void loadVideoList(){
        String entry="";
        String entryTitle = "";
        String entryCategory = "";
        DB.selectSQL("select Title, Category from tblVideos");

        do{
            entry=DB.getData();
            if(!entry.equals("|ND|"))entryTitle=entry; else break;
            entry=DB.getData();
            if(!entry.equals("|ND|"))entryCategory=entry; else break;

            mediaFile.add(new MediaFile(entryTitle, entryCategory));
        }while(true);
    }

    public void filter(){
        FilteredList<MediaFile> filteredData = new FilteredList<>(mediaFile, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(mediaFile -> {
                // If filter text is empty, display all
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare title and category with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (mediaFile.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter with title.
                } else if (mediaFile.getCategory().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter with category.
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<MediaFile> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        // 	  Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tbData.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tbData.setItems(sortedData);
    }

    public void loadPlaylist(){
        String playlistName = String.valueOf(comboPlaylist.getSelectionModel().getSelectedItem());
        String entry ="";
        String entryTitle="";
        String entryPlayNo="";
        mediaPlay.clear();

        if(playlistName.equals("") || playlistName.equals("default")) {
            DB.selectSQL("select Video, OrderNo from tblVideoOrder where PlaylistName='default' order by OrderNo asc");
            playlistName = "default";
            do {
                entry = DB.getData();
                if (!entry.equals("|ND|")) entryTitle = entry; else break;
                entry = DB.getData();
                if (!entry.equals("|ND|")) entryPlayNo = entry; else break;

                mediaPlay.add(new MediaPlay(entryTitle, entryPlayNo, playlistName));
            } while (true);
        } else {
            DB.selectSQL("select Video, OrderNo from tblVideoOrder where PlaylistName='" + playlistName + "' order by OrderNo asc");
            do {
                entry = DB.getData();
                if (!entry.equals("|ND|")) entryTitle = entry; else break;
                entry = DB.getData();
                if (!entry.equals("|ND|")) entryPlayNo = entry; else break;

                mediaPlay.add(new MediaPlay(entryTitle, entryPlayNo, playlistName));
            } while (true);
        }
    }

    public void loadComboBox(){
        playlists.clear();
        DB.manualDisconnect();
        DB.selectSQL("select distinct PlaylistName from tblVideoOrder");
        String entry = DB.getData();
        while(!entry.equals("|ND|")){
            playlists.add(entry);
            entry=DB.getData();
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
}