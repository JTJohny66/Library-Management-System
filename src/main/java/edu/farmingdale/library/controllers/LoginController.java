package edu.farmingdale.library.controllers;

import edu.farmingdale.library.model.Library;
import edu.farmingdale.library.model.Student;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private ToggleButton toggleButton;
    @FXML private Button loginButton;
    @FXML private Button signUpButton;
    @FXML private Label errorLabel;

    @FXML
    private void toggle() {
        if (toggleButton.isSelected()) {
            toggleButton.setText("Hide");
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            toggleButton.setText("Show");
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
    }

    @FXML
    private void login() throws IOException {
        Library lib = Library.getInstance();
        Student student = lib.getStudentByEmail(emailField.getText());

        if(student != null && student.isPassword(getPasswordInput())) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/farmingdale/library/student-homepage.fxml"));
            Parent root = loader.load();

            StudentHomeController controller = loader.getController();
            controller.setStudent(student);

            Scene scene = loginButton.getScene();
            scene.setRoot(root);
        } else {
            errorLabel.setText("Incorrect email or password");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void updateCheck(){
        errorLabel.setVisible(false);
    }

    @FXML
    private void signUp() throws IOException {
        switchScene("/edu/farmingdale/library/sign-up-screen.fxml");
    }

    @FXML
    private void goToWelcome() throws IOException {
        switchScene("/edu/farmingdale/library/welcome.fxml");
    }

    private void switchScene(String fxmlPath) throws IOException {
        Parent newRoot = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = loginButton.getScene(); // or any control in that screen
        scene.setRoot(newRoot);
    }

    private String getPasswordInput() {
        return toggleButton.isSelected()
                ? visiblePasswordField.getText()
                : passwordField.getText();
    }
}
