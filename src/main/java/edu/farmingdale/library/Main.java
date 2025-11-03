package edu.farmingdale.library;

import edu.farmingdale.library.model.Library;
import edu.farmingdale.library.model.Student;
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
        Scene loginScene = new Scene(loader.load(), 420, 260);
        stage.setTitle("Login • Library");
        stage.setScene(loginScene);
        stage.setMinWidth(550);
        stage.setMinHeight(550);
        stage.show();

        //temporary manual sign up for debugging
        Student s1 = new Student("Hello123!", "willjt6@farmingdale.edu", "Williams", "Jonathan");
        Library.getInstance().addStudent(s1);

    }

    public static void main(String[] args) {
        launch();

    }

}


