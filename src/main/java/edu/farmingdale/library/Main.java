package edu.farmingdale.library;

import edu.farmingdale.library.model.Admin;
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
                new FXMLLoader(Main.class.getResource("/edu/farmingdale/library/welcome.fxml"));
        Scene loginScene = new Scene(loader.load(), 420, 260);
        stage.setTitle("Login • Library");
        stage.setScene(loginScene);
        stage.setMinWidth(550);
        stage.setMinHeight(550);
        stage.show();


    }

    public static void main(String[] args) {

        //Initiating the Singleton Library
        Library library = Library.getInstance();

        //HardCoded Admin Class So They cannot be created or accessed in the program.
        Admin admin = new Admin("willjt7@farmingdale.edu","Hello123!","Jonathan Williams");
        library.addAdmin(admin);

        launch();
    }

}