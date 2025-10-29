module com.example.javademofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens edu.farmingdale.library to javafx.fxml;
    exports edu.farmingdale.library;
    exports edu.farmingdale.library.controllers;
    opens edu.farmingdale.library.controllers to javafx.fxml;
    exports edu.farmingdale.library.model;
    opens edu.farmingdale.library.model to javafx.fxml;
}