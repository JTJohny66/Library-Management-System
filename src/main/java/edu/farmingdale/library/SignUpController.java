package edu.farmingdale.library;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class SignUpController {

    // Form fields
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField studentIdField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private CheckBox termsCheckBox;

    // Labels and buttons
    @FXML private Label errorLabel;
    @FXML private Button signUpButton;
    @FXML private Button backToLoginButton;
    private boolean fullscreen;

    // Called when user clicks "Sign Up"
    @FXML
    private void createAccount() {
        // TODO: Add sign-up validation and account creation logic here
        System.out.println("Create account button clicked");
    }

    // Called when user clicks "Log in"
    @FXML
    private void goToLogin() throws IOException {
        Parent newRoot = FXMLLoader.load(getClass().getResource("/edu/farmingdale/library/login-screen.fxml"));
        Scene scene = backToLoginButton.getScene();
        scene.setRoot(newRoot);
    }
}
