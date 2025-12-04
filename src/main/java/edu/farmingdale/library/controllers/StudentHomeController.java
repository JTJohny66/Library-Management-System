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
    @FXML private TableColumn<Book, String> colAvailableRating;
    @FXML private TableColumn<Book, Void> colAvailableAction;

    @FXML private TableView<Book> allBooksTable;
    @FXML private TableColumn<Book, Integer> colAllID;
    @FXML private TableColumn<Book, String> colAllTitle;
    @FXML private TableColumn<Book, String> colAllAuthor;
    @FXML private TableColumn<Book, String> colAllRating;
    @FXML private TableColumn<Book, String> colAllStatus;
    @FXML private TableColumn<Book, Void> colAllAction;

    @FXML private TableView<Book> myBooksTable;
    @FXML private TableColumn<Book, Integer> colMyID;
    @FXML private TableColumn<Book, String> colMyTitle;
    @FXML private TableColumn<Book, String> colMyAuthor;
    @FXML private TableColumn<Book, String> colMyDueDate;
    @FXML private TableColumn<Book, Void> colMyReturn;

    @FXML private ComboBox<String> searchTypeBox;
    @FXML private TextField searchField;

    @FXML private ComboBox<String> searchTypeBoxAll;
    @FXML private TextField searchFieldAll;

    @FXML
    private void initialize() {
        // Available books columns
        colAvailableID.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getID()).asObject());
        colAvailableTitle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getBookTitle()));
        colAvailableAuthor.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));
        colAvailableRating.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRatingDisplay()));

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

        // All books columns
        colAllID.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getID()).asObject());
        colAllTitle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getBookTitle()));
        colAllAuthor.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));
        colAllRating.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRatingDisplay()));
        colAllStatus.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getInLibrary() ? "Available" : "Checked Out"));

        // Add borrow button column for all books
        colAllAction.setCellFactory(param -> new TableCell<>() {
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


        searchTypeBox.setValue("Title");
        searchTypeBoxAll.setValue("Title");
    }

    private void borrowBook(Book book) {
        if (book.getInLibrary()) {
            book.setInLibrary(false);
            book.setPossesion(student);
            LocalDate dueDate = LocalDate.now().plusWeeks(2);
            student.addBook(book.getISBN(), dueDate);

            Library.getInstance().updateStudentInFirebase(student);
            Library.getInstance().updateBook(book);

            refreshTables();
            showAlert("Success", "Book borrowed successfully! Due date: " + dueDate, Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "This book is already checked out.", Alert.AlertType.ERROR);
        }
    }

    private void returnBook(Book book) {

        Dialog<Integer> ratingDialog = new Dialog<>();
        ratingDialog.setTitle("Rate This Book");
        ratingDialog.setHeaderText("How would you rate \"" + book.getBookTitle() + "\"?");

        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        ButtonType skipButtonType = new ButtonType("Skip", ButtonBar.ButtonData.CANCEL_CLOSE);
        ratingDialog.getDialogPane().getButtonTypes().addAll(submitButtonType, skipButtonType);


        javafx.scene.layout.HBox starBox = new javafx.scene.layout.HBox(10);
        starBox.setAlignment(javafx.geometry.Pos.CENTER);

        ToggleGroup ratingGroup = new ToggleGroup();
        for (int i = 1; i <= 5; i++) {
            RadioButton star = new RadioButton(i + " ⭐");
            star.setUserData(i);
            star.setToggleGroup(ratingGroup);
            star.setStyle("-fx-font-size: 16px;");
            starBox.getChildren().add(star);
        }

        ratingDialog.getDialogPane().setContent(starBox);

        ratingDialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                RadioButton selected = (RadioButton) ratingGroup.getSelectedToggle();
                if (selected != null) {
                    return (Integer) selected.getUserData();
                }
            }
            return null;
        });


        ratingDialog.showAndWait().ifPresent(rating -> {
            book.addRating(rating);
        });


        book.setInLibrary(true);
        book.setPossesion(null);
        student.removeBook(book.getISBN());

        // Save both student AND book to Firebase
        Library.getInstance().updateStudentInFirebase(student);
        Library.getInstance().updateBook(book);

        refreshTables();
        showAlert("Success", "Book returned successfully! Thanks for your rating.", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Set student whos homepage accessing
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


        allBooksTable.setItems(FXCollections.observableArrayList(lib.getAllBooks()));


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

    @FXML
    private void onSearchAll() {
        Library lib = Library.getInstance();
        String type = searchTypeBoxAll.getValue();
        String query = searchFieldAll.getText().trim();

        if (query.isEmpty()) {
            refreshTables();
            return;
        }

        switch (type) {
            case "Title" -> {
                var results = lib.searchByTitle(query);
                allBooksTable.setItems(FXCollections.observableArrayList(results));
            }
            case "Author" -> {
                var results = lib.searchByAuthor(query);
                allBooksTable.setItems(FXCollections.observableArrayList(results));
            }
            case "ID" -> {
                try {
                    int id = Integer.parseInt(query);
                    Book result = lib.searchById(id);
                    if (result != null) {
                        allBooksTable.setItems(FXCollections.observableArrayList(result));
                    } else {
                        allBooksTable.setItems(FXCollections.observableArrayList());
                    }
                } catch (Exception e) {
                    allBooksTable.setItems(FXCollections.observableArrayList());
                }
            }
        }
    }
}