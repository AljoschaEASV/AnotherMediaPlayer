package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
    TableView<MediaFile> tbData2;
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
    TableColumn<MediaFile, String> title2;

    /**
     * The Category 2.
     */
    @FXML
    TableColumn<MediaFile, String> category2;
    @FXML
    TextField filterField;

    private ObservableList<MediaFile> mediaFile = FXCollections.observableArrayList();
    /**
     * This method initializes the Cell values.
     */
    public void initialize() {

        title.setCellValueFactory(new PropertyValueFactory<>("Title"));
        category.setCellValueFactory(new PropertyValueFactory<>("Category"));

        title2.setCellValueFactory(new PropertyValueFactory<>("Title"));
        category2.setCellValueFactory(new PropertyValueFactory<>("Category"));

        // Here we make use of the observable list data and inputs it into the table.
        tbData.setItems(mediaFile);

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
/*
    /**
     * Observablelist with data called search
     */


    /**
     * Method to select and move a row from table containing the songs to a playlist
     */
    public void select() {
        MediaFile selection = tbData.getSelectionModel().getSelectedItem();

        if (selection != null) {

            tbData2.getItems().add(new MediaFile(selection.getTitle(), selection.getCategory()));
        }
    }

}