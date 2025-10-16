package edu.farmingdale.library;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println(Main.class.getResource("/edu/farmingdale/library/login-screen.fxml"));

        FXMLLoader loader =
                new FXMLLoader(Main.class.getResource("/edu/farmingdale/library/login-screen.fxml"));
        Scene scene = new Scene(loader.load(), 420, 260);
        stage.setTitle("Login • Library");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}


