package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Infrastructure.DB;
import sample.Infrastructure.OrderStruct;

import java.io.File;
import java.util.ArrayList;


/**
 * The type Main.
 */
public class Main extends Application {

 @Override
    public void start(Stage primaryStage) throws Exception{


        preloader();

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("BitPusher MediaPlayer");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(600);
 }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Preloader - Adding all songs into the Playlist, when they are added to the media Folder if missing.
     * Requires a restart.
     * Also checks if songs was moved out from the specified Media Folder (rc/sample/media) in our case.
     *
     * @see #arrangeVideoOrder() rearranging videoorder table if deletion happened
     */
    public static void preloader() {
        File[] videos = new File("src/sample/media/").listFiles();
        ArrayList<String> titles = new ArrayList<String>();
        boolean deletion = false;


        for (File file : videos) {
            if (file.isFile()) {
                DB.selectSQL("SELECT title FROM tblVideos WHERE Title='" + file.getName() + "'");
                if (!DB.getData().equals(file.getName())) {
                    DB.insertSQL("INSERT INTO tblVideos (Title, Category, AbsolutePath) VALUES ('" + file.getName() + "' , 'Default', '" + file.getAbsolutePath() + "')");
                }
            }
        }


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
                if(title.equals(file.getName())){
                    found=true;
                }
            }
            if(!found){
                DB.deleteSQL("delete from tblVideos where Title='" + title + "'");
                DB.deleteSQL("delete from tblVideoOrder where Video='" + title + "'");
                deletion = true;
            }
        }


        if (deletion) arrangeVideoOrder();
    }

    /**
     * Arrange video order doing the following in order:
     * 1st selecting all playlist names and writing them in arraylist
     * 2nd for each playlist name, search for all videos with that playlistname in playorder and close gaps
     * 3rd filling orderstructcollection with data rows from videoorder table
     * 4th assigning ascending ordernumbers to videoentries in struct collection, starting from one
     * 5th updating videoorder numbers on titles in databse according to built structcollection
     */
    public static void arrangeVideoOrder() {
        ArrayList<String> playLists = new ArrayList<>();
        ArrayList<OrderStruct> structs = new ArrayList<>();
        String entry = "";
        int noStructs = 0;
        int cnt = 1;


        DB.selectSQL("select distinct PlaylistName from tblVideoOrder");
        while (!entry.equals("|ND|")) {
            entry=DB.getData();
            if(!entry.equals("|ND|")){
                playLists.add(entry);
            }
        }
        DB.manualDisconnect();
        entry="";


        for(String playlistName : playLists){
            DB.selectSQL("select Video, PlaylistName, OrderNo from tblVideoOrder where PlaylistName='" + playlistName + "' order by OrderNo asc");
            entry=DB.getData();


            while(!entry.equals("|ND|")) {
                structs.add(new OrderStruct());
                structs.get(noStructs).videoName = entry;
                entry=DB.getData();
                if(!entry.equals("|ND|")) structs.get(noStructs).playlistName = entry;
                entry=DB.getData();
                if(!entry.equals("|ND|")) structs.get(noStructs).orderNo = Integer.parseInt(entry);
                entry=DB.getData();
                noStructs++;
            }


            for(OrderStruct struct : structs){
                if(struct.orderNo==cnt) cnt++;
                else {
                    struct.orderNo=cnt;
                    cnt++;
                }
            }


            for(OrderStruct struct : structs){
                DB.updateSQL("update tblVideoOrder set OrderNo=" + struct.orderNo + " where  Video='" + struct.videoName + "' and PlaylistName='" + playlistName + "'");
            }

            structs.clear();
            entry="";
            cnt=1;
            noStructs=0;
        }
    }
}

