package edu.farmingdale.library.controllers;

import edu.farmingdale.library.model.Admin;
import edu.farmingdale.library.model.Book;
import edu.farmingdale.library.model.Library;
import edu.farmingdale.library.model.Student;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class AdminHomeController {

    private Admin admin;

    @FXML private Label welcomeLabel;
    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, Integer> colID;
    @FXML private TableColumn<Book, String> colISBN;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, String> colRating;
    @FXML private TableColumn<Book, Boolean> colAvailable;
    @FXML private TableColumn<Book, String> colBorrowedBy;
    @FXML private TableColumn<Book, Void> colActions;

    @FXML private TableView<Student> overdueTable;
    @FXML private TableColumn<Student, String> colStudentName;
    @FXML private TableColumn<Student, String> colStudentEmail;
    @FXML private TableColumn<Student, Integer> colOverdueBooks;
    @FXML private TableColumn<Student, Long> colMaxDaysOverdue;

    @FXML private ComboBox<String> searchTypeBox;
    @FXML private TextField searchField;

    @FXML
    private void initialize() {
        // Books table columns
        colID.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getID()).asObject());
        colISBN.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getISBN()));
        colTitle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getBookTitle()));
        colAuthor.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));
        colRating.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRatingDisplay()));
        colAvailable.setCellValueFactory(data ->
                new javafx.beans.property.SimpleBooleanProperty(data.getValue().getInLibrary()).asObject());
        colBorrowedBy.setCellValueFactory(data -> {
            Student student = data.getValue().getPossesion();
            String borrower = (student != null) ? student.getFirstName() + " " + student.getLastName() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(borrower);
        });

        // Action buttons column
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                editBtn.getStyleClass().add("primary");
                deleteBtn.getStyleClass().add("danger");

                editBtn.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    editBook(book);
                });

                deleteBtn.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    deleteBook(book);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });

        // Overdue table columns
        colStudentName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getFirstName() + " " + data.getValue().getLastName()));
        colStudentEmail.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colOverdueBooks.setCellValueFactory(data -> {
            Student student = data.getValue();
            long overdueCount = student.getCurrentBooks().stream()
                    .filter(isbn -> {
                        Book book = Library.getInstance().getBookByIsbn(isbn);
                        return book != null && Library.getInstance().isOverdue(book);
                    })
                    .count();
            return new javafx.beans.property.SimpleIntegerProperty((int) overdueCount).asObject();
        });
        colMaxDaysOverdue.setCellValueFactory(data -> {
            Student student = data.getValue();
            long maxDays = student.getCurrentBooks().stream()
                    .mapToLong(isbn -> {
                        Book book = Library.getInstance().getBookByIsbn(isbn);
                        return book != null ? Library.getInstance().getDaysOverdue(book) : 0;
                    })
                    .max()
                    .orElse(0);
            return new javafx.beans.property.SimpleLongProperty(maxDays).asObject();
        });

        // Initialize search and sort boxes
        if (searchTypeBox != null) {
            searchTypeBox.setValue("Title");
        }
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        welcomeLabel.setText("Admin Dashboard - " + admin.getFullName());
        refreshTables();
    }

    @FXML
    private void addBook() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter book details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField authorField = new TextField();
        authorField.setPromptText("Author");

        grid.add(new Label("ISBN:"), 0, 0);
        grid.add(isbnField, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Author:"), 0, 2);
        grid.add(authorField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                if (!isbnField.getText().isEmpty() && !titleField.getText().isEmpty() && !authorField.getText().isEmpty()) {
                    return new Book(isbnField.getText(), titleField.getText(), authorField.getText(), true, null);
                }
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(book -> {
            Library.getInstance().addNewBook(book);
            refreshTables();
            showAlert("Success", "Book added successfully!", Alert.AlertType.INFORMATION);
        });
    }

    private void editBook(Book book) {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Edit Book");
        dialog.setHeaderText("Edit book details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField titleField = new TextField(book.getBookTitle());
        TextField authorField = new TextField(book.getAuthor());
        CheckBox availableCheck = new CheckBox();
        availableCheck.setSelected(book.getInLibrary());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Available:"), 0, 2);
        grid.add(availableCheck, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Create updated book with SAME ID and ISBN (won't create duplicate)
                Book updatedBook = new Book(
                        book.getID(),  // Keep existing ID
                        book.getISBN(),
                        titleField.getText(),
                        authorField.getText(),
                        availableCheck.isSelected(),
                        book.getPossesion()
                );
                return updatedBook;
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(updatedBook -> {
            Library.getInstance().updateBook(updatedBook);
            refreshTables();
            showAlert("Success", "Book updated successfully!", Alert.AlertType.INFORMATION);
        });
    }

    private void deleteBook(Book book) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Book");
        alert.setHeaderText("Are you sure you want to delete this book?");
        alert.setContentText(book.getBookTitle() + " by " + book.getAuthor());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Library.getInstance().deleteBook(book.getID());
            refreshTables();
            showAlert("Success", "Book deleted successfully!", Alert.AlertType.INFORMATION);
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
                var results = lib.searchByTitle(query);
                booksTable.setItems(FXCollections.observableArrayList(results));
            }
            case "Author" -> {
                var results = lib.searchByAuthor(query);
                booksTable.setItems(FXCollections.observableArrayList(results));
            }
            case "ISBN" -> {
                Book result = lib.getBookByIsbn(query);
                if (result != null) {
                    booksTable.setItems(FXCollections.observableArrayList(result));
                } else {
                    booksTable.setItems(FXCollections.observableArrayList());
                }
            }
            case "ID" -> {
                try {
                    int id = Integer.parseInt(query);
                    Book result = lib.searchById(id);
                    if (result != null) {
                        booksTable.setItems(FXCollections.observableArrayList(result));
                    } else {
                        booksTable.setItems(FXCollections.observableArrayList());
                    }
                } catch (Exception e) {
                    booksTable.setItems(FXCollections.observableArrayList());
                }
            }
        }
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

        // Refresh books table
        booksTable.setItems(FXCollections.observableArrayList(lib.getAllBooks()));

        // Refresh overdue table
        overdueTable.setItems(FXCollections.observableArrayList(lib.getStudentsWithOverdueBooks()));
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}