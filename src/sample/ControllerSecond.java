package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * The type Controller second.
 */
public class ControllerSecond {
    /**
     * The Tb data.
     */
// Tableview instances
    @FXML
    TableView<Search> tbData, /**
     * The Tb data 2.
     */
    tbData2;
    /**
     * The Title.
     */
    @FXML
    TableColumn<Search, String> title;

    /**
     * The Category.
     */
    @FXML
    TableColumn<Search, String> category;

    /**
     * The Title 2.
     */
    @FXML
    TableColumn<Search, String> title2;

    /**
     * The Category 2.
     */
    @FXML
    TableColumn<Search, String> category2;

    /**
     * This method initializes the Cell values.
     */
    public void initialize() {
        title.setCellValueFactory(new PropertyValueFactory<>("Title"));
        category.setCellValueFactory(new PropertyValueFactory<>("Category"));

        title2.setCellValueFactory(new PropertyValueFactory<>("Title"));
        category2.setCellValueFactory(new PropertyValueFactory<>("Category"));

        // Here we make use of the observable list data and inputs it into the table.
        tbData.setItems(search);
    }


    /**
     * Observablelist with data called search
     */
    private ObservableList<Search> search = FXCollections.observableArrayList(
            new Search("Chandalier","Music"),
            new Search("Hello", "Music")
    );

    /**
     * Method to select and move a row from table containing the songs to a playlist
     */
    public void select() {
        Search selection = tbData.getSelectionModel().getSelectedItem();

        if (selection != null) {

            tbData2.getItems().add(new Search(selection.getTitle(), selection.getCategory()));
        }
    }
}