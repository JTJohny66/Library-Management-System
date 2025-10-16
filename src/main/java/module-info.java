module com.example.javademofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens edu.farmingdale.library to javafx.fxml;
    exports edu.farmingdale.library;
}