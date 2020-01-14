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


public class Main extends Application {




    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{

        preloader();

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("BitPusher MediaPlayer");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void preloader() {
        File[] videos = new File("src/sample/media/").listFiles();
        ArrayList<String> titles = new ArrayList<String>();
        boolean deletion = false;

        //adding existing videos that are missing inside database to the database
        for (File file : videos) {
            if (file.isFile()) {
                DB.selectSQL("SELECT title FROM tblVideos WHERE Title='" + file.getName() + "'");
                if(!DB.getData().equals(file.getName())) {
                    DB.insertSQL("INSERT INTO tblVideos (Title, Category, AbsolutePath) VALUES ('" + file.getName() + "' , 'Default', '" + file.getAbsolutePath() + "')");
                }
            }
        }

        //deleting video entries from database that are not actually existing
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
                deletion=true;
            }
        }

        //rearranging videoorder table if deletion happened
        if(deletion) arrangeVideoOrder();
    }

    public static void arrangeVideoOrder(){
        ArrayList<String> playLists = new ArrayList<>();
        ArrayList<OrderStruct> structs = new ArrayList<>();
        String entry="";
        int noStructs=0;
        int cnt=1;

        //selecting all playlist names and writing them in arraylist
        DB.selectSQL("select distinct PlaylistName from tblVideoOrder");
        while(!entry.equals("|ND|")) {
            entry=DB.getData();
            if(!entry.equals("|ND|")){
                playLists.add(entry);
            }
        }
        DB.manualDisconnect();
        entry="";

        //for each playlist name, search for all videos with that playlistname in playorder and close gaps
        for(String playlistName : playLists){
            DB.selectSQL("select Video, PlaylistName, OrderNo from tblVideoOrder where PlaylistName='" + playlistName + "' order by OrderNo asc");
            entry=DB.getData();

            //filling orderstructcollection with data rows from videoorder table
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

            //assigning ascending ordernumbers to videoentries in struct collection, starting from one
            for(OrderStruct struct : structs){
                if(struct.orderNo==cnt) cnt++;
                else {
                    struct.orderNo=cnt;
                    cnt++;
                }
            }

            //updating videoorder numbers on titles in databse according to built structcollection
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

