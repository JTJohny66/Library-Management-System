package edu.farmingdale.library.controllers;

import edu.farmingdale.library.model.Library;
import edu.farmingdale.library.model.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class SignUpController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private CheckBox termsCheckBox;

    @FXML private Label errorLabel;
    @FXML private Button signUpButton;
    @FXML private Button backToLoginButton;

    @FXML
    private void createAccount() {

        if (fullNameField.getText().isEmpty() || emailField.getText().isEmpty()
                || passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            showError("Please fill all fields.");
            return;
        }

        if (!nameIsValid(fullNameField.getText())) {
            showError("Please enter a first and last name.");
            return;
        }

        if (!emailIsValid(fullNameField.getText(), emailField.getText())) {
            showError("Must use personal farmingdale.edu Email.");
            return;
        }

        if (!termsCheckBox.isSelected()) {
            showError("Please accept terms and conditions.");
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Passwords do not match.");
            return;
        }

        if (!includesSigns(passwordField.getText())) {
            showError("Password must contain a symbol, number, uppercase, and lowercase.");
            return;
        }

        if (Library.getInstance().emailExists(emailField.getText())) {
            showError("Email already exists.");
            return;
        }

        String[] parts = fullNameField.getText().trim().split("\\s+");
        Library.getInstance().addStudent(
                new Student(passwordField.getText(), emailField.getText(), parts[1], parts[0])
        );

        errorLabel.setText("✅ Account Created Successfully!");
        errorLabel.setVisible(true);

        fullNameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        termsCheckBox.setSelected(false);
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    @FXML
    private void updateCheck(){
        errorLabel.setVisible(false);
    }

    @FXML
    private void goToLogin() throws IOException {
        switchScene("/edu/farmingdale/library/login-screen.fxml");
    }

    @FXML
    private void goToWelcome() throws IOException {
        switchScene("/edu/farmingdale/library/welcome.fxml");
    }

    private void switchScene(String fxmlPath) throws IOException {
        Parent newRoot = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = signUpButton.getScene();
        scene.setRoot(newRoot);
    }

    private boolean includesSigns(String str){
        return str != null && str.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$");
    }

    private boolean nameIsValid(String str){
        return str != null && str.matches("^[A-Za-z]+\\s+[A-Za-z]+$");
    }

    public static boolean emailIsValid(String name, String email) {
        if (name == null || email == null) return false;
        name = name.trim();
        email = email.trim().toLowerCase();
        if (!name.matches("^[A-Za-z]+\\s+[A-Za-z]+$")) return false;

        String[] parts = name.split("\\s+");
        char initial = Character.toLowerCase(parts[1].charAt(0));
        return email.startsWith(String.valueOf(initial)) && email.endsWith("@farmingdale.edu");
    }
}
