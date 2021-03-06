package sample;

import com.jfoenix.controls.JFXSlider;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.Infrastructure.DB;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The Media Player Controller.
 * Given the implementation of Initializable we can on Runtime
 */
public class Controller implements Initializable {

    /**
     * The Media viewer.
     */
    @FXML
    private MediaView mediaViewer;
    /**
     * The MediaPlayer.
     */
    private MediaPlayer mp;
    /**
     * The Slider for Volume.
     */
    @FXML
    private JFXSlider slider;
    /**
     * The Video scroll Slider.
     */
    @FXML
    private Slider vidScroller;
    /**
     * The File path.
     */
    private String filePath;

    private MediaPlayer currentPlayer = null;

    private MediaPlayer nextPlayer = null;

    /**
     * The scene for the Playlist manager.
     */
    private Stage playlistManager;


    private Controller secondController;

    public Controller(Controller secondController)
    {
        this.secondController = secondController;
    }

    private Media me, me2;

    public ObservableList<MediaPlay> getPlaylistentries() {
        return playlistentries;
    }

    public void setPlaylistentries(ObservableList<MediaPlay> playlistentries) {
        this.playlistentries = playlistentries;
    }

    //Maincontroller.play(nextplayer) FOr implementing a new playbutton with other controller

    public Controller() {
        this.playlistentries = playlistentries;
    }

    /**
     * The Playlistentries stored in an Obeservable List
     *
     * @see #reloadPlaylist further use.
     */
    private ObservableList<MediaPlay> playlistentries = FXCollections.observableArrayList();

    /**
     * This method is gonna make a new window
     *
     * @param event the event to open and check if opened the second Stage aka PlaylistManager.
     */
    public void openPlayListManager(javafx.event.ActionEvent event) {
        try {
            if (playlistManager == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("secondWindow.fxml"));
                fxmlLoader.setController(new ControllerSecond(this));
                Parent root1 = fxmlLoader.load();
                playlistManager = new Stage();
                playlistManager.setTitle("Playlist Manager");
                playlistManager.setScene(new Scene(root1));
                playlistManager.setResizable(false);

                playlistManager.show();
            } else if (playlistManager.isShowing()) {
                playlistManager.toFront();
            } else {
                playlistManager.show();
            }
        } catch (Exception e) {
            System.out.println("Can't load the window");
        }
    }

    /**
     * todo the actual Project has to have the filePath from the Database / Playlists.
     * <p>
     * The getFile is attached to the rightmost button left from the soundbar.
     * It will open the FileExplorer and enable the user to open a mp4 File.
     *
     * @param actionEvent the event on buttonPress to be able to choose a mp4 File and play it through the MediaPlayer.
     * @see #screenAdjuster() #screenAdjuster()
     * @see #setVolume() #setVolume()
     * @see #videoScrollBar() #videoScrollBar()
     */

    /**
     * todo the actual Project has to have the filePath from the Database / Playlists.
     *This will play one song when this is executed
     *
     */
    @FXML
    public void runSingleChoice(MediaFile selection) {
        MediaPlayer temp = new MediaPlayer(new Media(new File(new File("src/sample/media/" + selection.getTitle()).getAbsolutePath()).toURI().toString()));
        mediaViewer.setMediaPlayer(temp);
        mediaViewFullScreen();
        setVolume();
        videoScrollBar();
        mediaViewer.getMediaPlayer().play();
    }

    /**
     * This is the property for the video scrollbar that will show the current playtime of a video
     */
    private void videoScrollBarForPlaylist() {
        mp.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration currentPlayTime) {
                //Making the Video Slider more dynamic depending on the Vid Length
                vidScroller.setMin(0.0);
                vidScroller.setMax(nextPlayer.getTotalDuration().toSeconds());
                vidScroller.setValue(currentPlayTime.toSeconds());
            }
        });
    }

    /**
     * This is for choosing a single mp4 video
     * @param actionEvent
     */
    @FXML
    public void getFile(javafx.event.ActionEvent actionEvent) {
        try {
            FileChooser fc = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter
                    ("Please select a File (*.mp4) ", "*.mp4");
            //This will get the mp4 File given the Filter as a File.
            fc.getExtensionFilters().add(filter);
            File file = fc.showOpenDialog(null);
            filePath = file.toURI().toString();

            if (filePath != null) {
                screenAdjuster();
                setVolume();
                //Set the Slider vidScroller to the MediaPlayer
                videoScrollBar();

                mp.play();
            }
        } catch (Exception e) {
            System.out.println(" Wrong File / No File chosen");

        }
    }

    /**
     * Sets the function for the video Scrollbar, which then enables the user to
     * choose a specific playtime double clicking on the slider.
     */
    private void videoScrollBar() {
        mediaViewer.getMediaPlayer().currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration currentPlayTime) {
                //Making the Video Slider more dynamic depending on the Vid Length
                vidScroller.setMin(0.0);
                vidScroller.setMax(mediaViewer.getMediaPlayer().getTotalDuration().toSeconds());
                vidScroller.setValue(currentPlayTime.toSeconds());
            }
        });

        vidScroller.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaViewer.getMediaPlayer().seek(Duration.seconds(vidScroller.getValue()));
            }
        });
    }

    /**
     * Setting the Media file into the mediaPlayer and therefrom into the mediaViewer inside our application
     * Also binds the mediaViewer to the size of the BorderPane
     */
    private void screenAdjuster() {
        Media media = new Media(filePath);
        mp = new MediaPlayer(media);
        mediaViewer.setMediaPlayer(mp);
        //Getting the image to fit the width and height!
        mediaViewFullScreen();

    }


    /**
     * Media view auto Adjust to fullscreenSize
     */
    public void mediaViewFullScreen() {
        DoubleProperty width = mediaViewer.fitWidthProperty();
       DoubleProperty height = mediaViewer.fitHeightProperty();
        width.bind(Bindings.selectDouble(mediaViewer.sceneProperty(), "width"));
       height.bind(Bindings.selectDouble(mediaViewer.sceneProperty(), "height"));


    }

    /**
     * Set the volume Sliders functions - Using the Slider through this method enables to controll the volume of
     * the program.
     */
    private void setVolume() {
        //used to get a useable number for the called methods. Either 1-100 or 0-1.
        int sizeChanger = 100;
        //Setting the Function
        slider.setValue(mediaViewer.getMediaPlayer().getVolume() * sizeChanger);
        slider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                //The set Value supports number from 0.1 - 1 Therefore we divide by 100
                mediaViewer.getMediaPlayer().setVolume(slider.getValue() / sizeChanger);

            }
        });
    }

    /**
     * This is for the volume adjuster
     * @param mpl
     */
    private void setSpecificVolume(MediaPlayer mpl){
        int sizeChanger = 100;
        slider.setValue(mpl.getVolume() * sizeChanger);
        slider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                //The set Value supports number from 0.1 - 1 Therefore we divide by 100
                mpl.setVolume(slider.getValue() / sizeChanger);
            }
        });
    }

    /**
     * Pause video.
     *
     * @param event the event to pause the video
     */
    @FXML
    private void pauseVideo(javafx.event.ActionEvent event) {
        try {
            mediaViewer.getMediaPlayer().pause();

        } catch (Exception e) {
            System.out.println("Please insert a file to pause it. ");

        }
    }

    /**
     * Play video.
     *
     * @param event the event to play the video.
     */
    @FXML
    private void playVideo(javafx.event.ActionEvent event) {
        try {
            mediaViewer.getMediaPlayer().play();

        } catch (Exception e) {
            System.out.println("Please insert a file to play from. ");

        }
    }

    /**
     * Stop video.
     *
     * @param event the event to dispose the media File from the Viewer & player
     */
    @FXML
    private void stopVideo(javafx.event.ActionEvent event) {
        try {
            mediaViewer.getMediaPlayer().stop();

        } catch (Exception e) {
            System.out.println(" No file found");
        }
    }

    /**
     * To start.
     *
     * @param event the event to stop the player
     */
    @FXML
    private void toStart(javafx.event.ActionEvent event) {
        try {
            mediaViewer.getMediaPlayer().stop();
            mediaViewer.getMediaPlayer().play();

        } catch (Exception e) {
            System.out.println(" No file found");
        }
    }

    /**
     * Exit.
     *
     * @param event the event to exit the program
     */
    @FXML
    private void exit(javafx.event.ActionEvent event) {
        System.exit(0);

    }

    @Override
    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location
     * The location used to resolve relative paths for the root object, or
     * <tt>null</tt> if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or <tt>null</tt> if
     * the root object was not localized.
     */
    public void initialize(URL location, ResourceBundle resources) {
    }


    /**
     * Enables the user to fetch the file in the given order from the playlist.
     *
     * @param playlistname the specifier to get the absolute path from mediaFiles inside a specific playlist.
     */
    public void reloadPlaylist(String playlistname) {
        playlistentries.clear();
        DB.selectSQL("select Video, OrderNo from tblVideoOrder where PlaylistName='" + playlistname + "' order by OrderNo asc");
        String entry = "";
        String video = "";
        String orderNo = "";
        do {
            entry = DB.getData();
            if (!entry.equals("|ND|")) video = entry;
            entry = DB.getData();
            if (!entry.equals("|ND|")) {
                orderNo = entry;
                playlistentries.add(new MediaPlay(video, orderNo, playlistname));
            }
            entry = DB.getData();
        } while (!entry.equals("|ND|"));

        /*
        DB.selectSQL("select AbsolutePath from tblVideos where Title='" + playlistentries.get(0).getTitle() + "'");
        String entry = DB.getData();
        play(entry);

         */


    }

    /**
     * This method is controlling the media entry's and creating media objects that will get iterated through and playing next video in the order
     * @param selection
     */
    public void runListChoice(MediaPlay selection){

        ArrayList<MediaPlayer> videos = new ArrayList<>();
        videos.add(new MediaPlayer(new Media(new File(new File("src/sample/media/" + selection.getTitle()).getAbsolutePath()).toURI().toString())));
        DB.selectSQL("select Video from tblVideoOrder where PlaylistName='" + selection.getPlaylist() + "' and OrderNo>" + selection.getOrderNo() + " order by OrderNo asc");
        String entry = DB.getData();
        while(!entry.equals("|ND|")){
            videos.add(new MediaPlayer(new Media(new File(new File("src/sample/media/" + entry).getAbsolutePath()).toURI().toString())));
            entry=DB.getData();
        }

        for (int i = 0; i < videos.size()-1 ; i++) {
            final MediaPlayer currentPlayer = videos.get(i);
            final MediaPlayer nextPlayer = videos.get(i+1);

            currentPlayer.setOnEndOfMedia(() -> {
                currentPlayer.stop();
                mediaViewer.setMediaPlayer(nextPlayer);
                mediaViewFullScreen();
                setVolume();
                videoScrollBar();
                nextPlayer.play();
            });
        }

        mediaViewer.setMediaPlayer(videos.get(0));
        mediaViewFullScreen();
        setVolume();
        videoScrollBar();
        mediaViewer.getMediaPlayer().play();
    }
}




