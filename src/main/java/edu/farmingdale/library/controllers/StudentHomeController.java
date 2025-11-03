package edu.farmingdale.library.controllers;

import edu.farmingdale.library.model.Student;
import edu.farmingdale.library.model.Library;
import edu.farmingdale.library.model.Book;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class StudentHomeController {

    private Student student;

    // UI References
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;

    @FXML private ComboBox<String> searchTypeBox;
    @FXML private TextField searchField;
    @FXML private Button searchButton;

    @FXML private TableView<Book> availableBooksTable;
    @FXML private TableColumn<Book, String> colAvailableTitle;
    @FXML private TableColumn<Book, String> colAvailableAuthor;
    @FXML private TableColumn<Book, String> colAvailableStatus;
    @FXML private TableColumn<Book, Void> colAvailableAction;

    @FXML private TableView<Book> myBooksTable;
    @FXML private TableColumn<Book, String> colMyTitle;
    @FXML private TableColumn<Book, String> colMyAuthor;
    @FXML private TableColumn<Book, String> colMyDueDate;
    @FXML private TableColumn<Book, Void> colMyReturn;

    @FXML
    private void initialize() {
        // Show search input only after user selects a type
        searchTypeBox.setOnAction(e -> {
            boolean selected = searchTypeBox.getValue() != null;
            searchField.setVisible(selected);
            searchButton.setVisible(selected);
        });
    }

    public void setStudent(Student student) {
        this.student = student;
        welcomeLabel.setText("Welcome " + student.getFirstName() + "!");
    }

    @FXML
    private void onSearch() {
        String type = searchTypeBox.getValue();
        String query = searchField.getText().toLowerCase();
        Library lib = Library.getInstance();

        switch (type) {
            case "Title" -> availableBooksTable.setItems(
                    FXCollections.observableArrayList(lib.searchByTitle(query)));

            case "Author" -> availableBooksTable.setItems(
                    FXCollections.observableArrayList(lib.searchByAuthor(query)));

            case "ID" -> {
                try {
                    int id = Integer.parseInt(query);
                    var result = lib.searchById(id);
                    availableBooksTable.setItems(
                            FXCollections.observableArrayList(result != null ? java.util.List.of(result) : java.util.List.of()));
                } catch (NumberFormatException ignored) {}
            }
        }
    }
}

