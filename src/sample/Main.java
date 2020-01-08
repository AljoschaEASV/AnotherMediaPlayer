package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Infrastructure.DB;

import java.io.File;
import java.util.ArrayList;

/**
 * The type Main.
 */
public class Main extends Application {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Preloader.
     */
    public static void preloader() {
        File[] videos = new File("src/sample/videos/").listFiles();
        ArrayList<String> titles = new ArrayList<String>();

        //adding existing videos that are missing inside database to the database
        for (File file : videos) {
            if (file.isFile()) {
                System.out.println("Searching for " + file.getName() + " in Database...");
                DB.selectSQL("SELECT title FROM tblVideos WHERE Title='" + file.getName() + "'");
                if(DB.getData().equals(file.getName())){
                    System.out.println("Video with title: " + file.getName() + " has been FOUND.");
                }
                else{
                    System.out.println("Video with title: " + file.getName() + " has been NOT FOUND, adding it to database...");
                    DB.insertSQL("INSERT INTO tblVideos (Title, AbsolutePath) VALUES ('" + file.getName() + "' , '" + file.getAbsolutePath() + "')");
                }
            }
        }

        //deleting video entries from database that are not existing
        System.out.println("Checking for nonexisting entries...");

        DB.selectSQL("SELECT Title from tblVideos");
        String select = "";
        boolean found;

        while(!select.equals("|ND|")){
            select=DB.getData();
            if(!select.equals("|ND|"))titles.add(select);
        }

        for(String title : titles){
            found=false;
            for(File file : videos){
                if(title.equals(file.getName()))found=true;
            }
            if(!found){
                DB.deleteSQL("delete from tblVideos where Title='" + title + "'");
                DB.deleteSQL("delete from tblVideoOrder where Video='" + title + "'");
            }
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        //preloader();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Rainbow Media LtD.");

      /*  //Adding the DoubleClick Function for FullScreen Mode
        Scene scene = new Scene(root);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent doubleClicker) {
                if(doubleClicker.getClickCount() == 2){
                    primaryStage.setFullScreen(true);
                }
            }
        });

       */


        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
    }
}

