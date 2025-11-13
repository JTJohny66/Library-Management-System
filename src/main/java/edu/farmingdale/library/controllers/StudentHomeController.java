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
import java.time.LocalDate;

public class StudentHomeController {

    private Student student;

    @FXML private Label welcomeLabel;
    @FXML private TableView<Book> availableBooksTable;
    @FXML private TableColumn<Book, Integer> colAvailableID;
    @FXML private TableColumn<Book, String> colAvailableTitle;
    @FXML private TableColumn<Book, String> colAvailableAuthor;
    @FXML private TableColumn<Book, Void> colAvailableAction;

    @FXML private TableView<Book> myBooksTable;
    @FXML private TableColumn<Book, Integer> colMyID;
    @FXML private TableColumn<Book, String> colMyTitle;
    @FXML private TableColumn<Book, String> colMyAuthor;
    @FXML private TableColumn<Book, String> colMyDueDate;
    @FXML private TableColumn<Book, Void> colMyReturn;

    @FXML private ComboBox<String> searchTypeBox;
    @FXML private TextField searchField;

    @FXML
    private void initialize() {
        // Available books columns
        colAvailableID.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getID()).asObject());
        colAvailableTitle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getBookTitle()));
        colAvailableAuthor.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));

        // Add borrow button column
        colAvailableAction.setCellFactory(param -> new TableCell<>() {
            private final Button borrowBtn = new Button("Borrow");

            {
                borrowBtn.getStyleClass().add("primary");
                borrowBtn.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    borrowBook(book);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableView().getItems().get(getIndex());
                    if (book.getInLibrary()) {
                        setGraphic(borrowBtn);
                    } else {
                        setGraphic(new Label("Checked Out"));
                    }
                }
            }
        });

        // My books columns
        colMyID.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getID()).asObject());
        colMyTitle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getBookTitle()));
        colMyAuthor.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));
        colMyDueDate.setCellValueFactory(data -> {
            LocalDate dueDate = Library.getInstance().getDueDate(data.getValue());
            String dueDateStr = (dueDate != null) ? dueDate.toString() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(dueDateStr);
        });

        // Add return button column
        colMyReturn.setCellFactory(param -> new TableCell<>() {
            private final Button returnBtn = new Button("Return");

            {
                returnBtn.getStyleClass().add("primary");
                returnBtn.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    returnBook(book);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(returnBtn);
                }
            }
        });

        // Initialize search type combo box
        searchTypeBox.setValue("Title");
    }

    private void borrowBook(Book book) {
        if (book.getInLibrary()) {
            book.setInLibrary(false);
            book.setPossesion(student);
            student.addBook(book.getISBN());
            Library.getInstance().setDueDate(book, LocalDate.now().plusWeeks(2));

            // 🆕 Save to Firebase
            Library.getInstance().updateStudentInFirebase(student);

            refreshTables();
            showAlert("Success", "Book borrowed successfully!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "This book is already checked out.", Alert.AlertType.ERROR);
        }
    }

    private void returnBook(Book book) {
        book.setInLibrary(true);
        book.setPossesion(null);
        student.removeBook(book.getISBN());
        Library.getInstance().setDueDate(book, null);

        // 🆕 Save to Firebase
        Library.getInstance().updateStudentInFirebase(student);

        refreshTables();
        showAlert("Success", "Book returned successfully!", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

        var availableBooks = lib.getAllBooks().stream()
                .filter(Book::getInLibrary)
                .toList();
        availableBooksTable.setItems(FXCollections.observableArrayList(availableBooks));

        if (student != null) {
            var bookList = student.getCurrentBooks()
                    .stream()
                    .map(isbn -> lib.getBookByIsbn(isbn))
                    .filter(b -> b != null)
                    .toList();

            myBooksTable.setItems(FXCollections.observableArrayList(bookList));
        }
    }

    @FXML
    private void onSearch() {
        Library lib = Library.getInstance();
        String type = searchTypeBox.getValue();
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            refreshTables();
            return;
        }

        switch (type) {
            case "Title" -> {
                var results = lib.searchByTitle(query).stream()
                        .filter(Book::getInLibrary)
                        .toList();
                availableBooksTable.setItems(FXCollections.observableArrayList(results));
            }
            case "Author" -> {
                var results = lib.searchByAuthor(query).stream()
                        .filter(Book::getInLibrary)
                        .toList();
                availableBooksTable.setItems(FXCollections.observableArrayList(results));
            }
            case "ID" -> {
                try {
                    int id = Integer.parseInt(query);
                    Book result = lib.searchById(id);
                    if (result != null && result.getInLibrary()) {
                        availableBooksTable.setItems(FXCollections.observableArrayList(result));
                    } else {
                        availableBooksTable.setItems(FXCollections.observableArrayList());
                    }
                } catch (Exception e) {
                    availableBooksTable.setItems(FXCollections.observableArrayList());
                }
            }
        }
    }
}