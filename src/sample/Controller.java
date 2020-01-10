package sample;

import com.jfoenix.controls.JFXSlider;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The Media Player Controller.
 */
public class Controller implements Initializable {

    /**
     * The Media viewer.
     */
    @FXML
        private MediaView mediaViewer;
    /**
     * The Mp.
     */
    private MediaPlayer mp;
    /**
     * The Mv.
     */
    private MediaView mv;
    /**
     * The Slider.
     */
    @FXML
        private JFXSlider slider;
    /**
     * The Vid scroller.
     */
    @FXML
        private Slider vidScroller;
    /**
     * The File path.
     */
    private String filePath;

    /**
     * This method is gonna make a new window
     *
     * @param event the event
     */
    public void openNewWIndow(javafx.event.ActionEvent event) {

    try
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("secondWindow.fxml"));
        Parent root1 = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("BitPusher Playlist Manager");
        stage.setScene(new Scene(root1));



        stage.show();

    }catch (Exception e)
    {
        System.out.println("Can't load the window");
    }
    }

    /**
     * todo the actual Project has to have the filePath from the Database / Playlists.
     *
     * @param actionEvent the action event
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



                if(filePath != null) {
                    //Making the logic for the Media itself.
                    Media media = new Media(filePath);
                    //Using the privately created media Player to create a new mediaplayer, where we now initialize the choosen
                    //Media File into.
                    mp = new MediaPlayer(media);
                    mediaViewer.setMediaPlayer(mp);
                    //Getting the image to fit the width and height!
                    DoubleProperty width = mediaViewer.fitWidthProperty();
                    DoubleProperty height = mediaViewer.fitHeightProperty();

                    setVolume();
                    //Binding them to the width and height
                    width.bind(Bindings.selectDouble(mediaViewer.sceneProperty(), "width"));
                    height.bind(Bindings.selectDouble(mediaViewer.sceneProperty(), "height"));


                    //Making the Video Slider more dynamic depending on the Vid Length


                    //Set the Slider vidScroller to the MediaPlayer
                    mp.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                        @Override
                        public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                            vidScroller.setMin(0.0);

                            vidScroller.setMax(mp.getTotalDuration().toSeconds());

                            vidScroller.setValue(newValue.toSeconds());
                        }
                    });
                    vidScroller.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            mp.seek(Duration.seconds(vidScroller.getValue()));


                        }
                    });


                    mp.play();

                }

            } catch (Exception e) {
                System.out.println(" ");

            }
    }

    /**
     * Set the volume Sliders functions.
     */
    private void setVolume() {
        //Setting the Function
        slider.setValue(mp.getVolume() * 100);
        slider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                //The set Value supports number from 0.1 - 1 Therefore we divide by 100
                mp.setVolume(slider.getValue() / 100);

            }
        });
    }

    /**
     * Pause video.
     *
     * @param event the event
     */
    @FXML
    private void pauseVideo(javafx.event.ActionEvent event) {
        mp.pause();

        }

    /**
     * Play video.
     *
     * @param event the event
     */
    @FXML
        private void playVideo(javafx.event.ActionEvent event){
            mp.play();

        }

    /**
     * Stop video.
     *
     * @param event the event
     */
    @FXML
        private void stopVideo(javafx.event.ActionEvent event) {
        try {
            mp.dispose();

        } catch (Exception e) {
            System.out.println(" ");
        }
    }

    /**
     * To start.
     *
     * @param event the event
     */
    @FXML
        private void toStart(javafx.event.ActionEvent event){
            mp.stop();

        }

    /**
     * Exit.
     *
     * @param event the event
     */
    @FXML
        private void exit(javafx.event.ActionEvent event){
            System.exit(0);

        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            //todo
        }



    }




