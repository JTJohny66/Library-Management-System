module com.example.javademofx {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.farmingdale.library to javafx.fxml;
    exports edu.farmingdale.library;
}