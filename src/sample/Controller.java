package sample;

import com.jfoenix.controls.JFXSlider;
import com.sun.org.apache.xml.internal.security.Init;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private Button button;

    /**
     * This method is gonna make a new window
     * @param event
     */
    public void openNewWIndow(javafx.event.ActionEvent event) {

    try
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("secondWindow.fxml"));
        Parent root1 = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("New Window");
        stage.setScene(new Scene(root1));
        stage.show();



    }catch (Exception e)
    {
        System.out.println("Can't load the window");
    }



    }






        @FXML
        private MediaView mediaViewer;

        private MediaPlayer mp;
        private MediaView mv;
        @FXML
        private JFXSlider slider;
        @FXML
        private Slider videoSlide;


        private String filePath;

        /**
         *todo the actual Project has to have the filePath from the Database / Playlists.
         */
        @FXML
        public void handleButtonAction(javafx.event.ActionEvent actionEvent) {
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

                    //Binding them to the width and height
                    width.bind(Bindings.selectDouble(mediaViewer.sceneProperty(), "width"));
                    height.bind(Bindings.selectDouble(mediaViewer.sceneProperty(), "height"));
                    slider.setValue(mp.getVolume() * 100);
                    slider.valueProperty().addListener(new InvalidationListener() {
                        @Override
                        public void invalidated(Observable observable) {
                            //The set Value supports number from 0.1 - 1 Therefore we divide by 100
                            mp.setVolume(slider.getValue() / 100);

                        }
                    });


                    mp.play();

                }

            }
            catch(Exception e) {

            }
        }

        @FXML
        private void pauseVideo(javafx.event.ActionEvent event){
            mp.pause();

        }
        @FXML
        private void playVideo(javafx.event.ActionEvent event){
            mp.play();

        }
        @FXML
        private void stopVideo(javafx.event.ActionEvent event){
            mp.dispose();

        }
        @FXML
        private void toStart(javafx.event.ActionEvent event){
            mp.stop();

        }
        @FXML
        private void openPlaylist(javafx.event.ActionEvent event){
            //Todo The button is given. Let's find the Logic! :D

        }
        @FXML
        private void exit(javafx.event.ActionEvent event){
            System.exit(0);

        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            //todo
        }



    }




