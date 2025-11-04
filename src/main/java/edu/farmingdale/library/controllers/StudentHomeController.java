package edu.farmingdale.library.controllers;

import edu.farmingdale.library.model.Book;
import edu.farmingdale.library.model.Library;
import edu.farmingdale.library.model.Student;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;

public class StudentHomeController {

    private Student student;

    @FXML private Label welcomeLabel;
    @FXML private TableView<Book> availableBooksTable;
    @FXML private TableColumn<Book, Integer> colAvailableID;
    @FXML private TableColumn<Book, String> colAvailableTitle;
    @FXML private TableColumn<Book, String> colAvailableAuthor;

    @FXML private TableView<Book> myBooksTable;
    @FXML private TableColumn<Book, Integer> colMyID;
    @FXML private TableColumn<Book, String> colMyTitle;
    @FXML private TableColumn<Book, String> colMyAuthor;
    @FXML private TableColumn<Book, String> colMyDueDate;

    @FXML private ComboBox<String> searchTypeBox;
    @FXML private TextField searchField;

    @FXML
    private void initialize() {

        // Available books
        colAvailableID.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getID()).asObject());
        colAvailableTitle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBookTitle()));
        colAvailableAuthor.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));

        // My books
        colMyID.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getID()).asObject());
        colMyTitle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBookTitle()));
        colMyAuthor.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));
        colMyDueDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty("Soon"));

        refreshTables();
    }

    public void setStudent(Student student) {
        this.student = student;
        welcomeLabel.setText("Welcome " + student.getFirstName() + "!");
        refreshTables();
    }

    @FXML
    private void logOut() throws IOException {
        switchScene("/edu/farmingdale/library/welcome.fxml");
    }

    private void switchScene(String fxmlPath) throws IOException {
        Parent newRoot = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = welcomeLabel.getScene();
        scene.setRoot(newRoot);
    }

    private void refreshTables() {
        Library lib = Library.getInstance();
        availableBooksTable.setItems(FXCollections.observableArrayList(lib.getAllBooks()));
        if (student != null)
            myBooksTable.setItems(FXCollections.observableArrayList(student.getCurrentBooks()));
    }

    @FXML
    private void onSearch() {
        Library lib = Library.getInstance();
        String type = searchTypeBox.getValue();
        String query = searchField.getText().trim().toLowerCase();

        switch (type) {
            case "Title" -> availableBooksTable.setItems(
                    FXCollections.observableArrayList(lib.searchByTitle(query)));
            case "Author" -> availableBooksTable.setItems(
                    FXCollections.observableArrayList(lib.searchByAuthor(query)));
            case "ID" -> {
                try {
                    int id = Integer.parseInt(query);
                    Book result = lib.searchById(id);
                    availableBooksTable.setItems(FXCollections.observableArrayList(result));
                } catch (Exception ignored) {}
            }
        }
    }
}
