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

    //Toggle Password Visibility using overlapped fields
    @FXML private void toggle(){
        if(toggleButton.isSelected()){
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.toFront();
        }
        if(!toggleButton.isSelected()){
            passwordField.setText(visiblePasswordField.getText());
            passwordField.toFront();
        }
    }

    @FXML private void login()throws IOException{
        Library lib = Library.getInstance();
        if(lib.getStudentByEmail(emailField.getText())!=null
                && lib.getStudentByEmail(emailField.getText()).isPassword(getPasswordInput())){
                    Student student = lib.getStudentByEmail(emailField.getText());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/farmingdale/library/student-homepage.fxml"));
            Parent root = loader.load();

            StudentHomeController controller = loader.getController();
            controller.setStudent(student);

            Scene scene = loginButton.getScene();
            scene.setRoot(root);
        }
        else{
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
        Parent newRoot = FXMLLoader.load(getClass().getResource("/edu/farmingdale/library/sign-up-screen.fxml"));
        Scene scene = loginButton.getScene();
        scene.setRoot(newRoot);
    }

    private String getPasswordInput() {
        return toggleButton.isSelected()
                ? visiblePasswordField.getText()
                : passwordField.getText();
    }


}
