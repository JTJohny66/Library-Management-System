package edu.farmingdale.library.controllers;

import edu.farmingdale.library.model.Admin;
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
import javafx.scene.control.CheckBox;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private ToggleButton toggleButton;
    @FXML private CheckBox adminCheckBox;
    @FXML private Button loginButton;
    @FXML private Button signUpButton;
    @FXML private Label errorLabel;
    @FXML private Label emailLabel;

    @FXML
    private void initialize() {
        // Update label text when admin checkbox is toggled
        if (adminCheckBox != null) {
            adminCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    emailLabel.setText("Username:");
                    emailField.setPromptText("Enter admin username");
                } else {
                    emailLabel.setText("Email:");
                    emailField.setPromptText("Enter your email");
                }
            });
        }
    }

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
        String credential = emailField.getText();
        String password = getPasswordInput();

        // Check if admin login
        if (adminCheckBox != null && adminCheckBox.isSelected()) {
            Admin admin = lib.getAdminByUsername(credential);

            if (admin != null && admin.isPassword(password)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/farmingdale/library/admin-homepage.fxml"));
                Parent root = loader.load();

                AdminHomeController controller = loader.getController();
                controller.setAdmin(admin);

                Scene scene = loginButton.getScene();
                scene.setRoot(root);
            } else {
                errorLabel.setText("Incorrect username or password");
                errorLabel.setVisible(true);
            }
        } else {
            // Student login
            Student student = lib.getStudentByEmail(credential);

            if (student != null && student.isPassword(password)) {
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
    }

    @FXML
    private void updateCheck() {
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
        Scene scene = loginButton.getScene();
        scene.setRoot(newRoot);
    }

    private String getPasswordInput() {
        return toggleButton.isSelected()
                ? visiblePasswordField.getText()
                : passwordField.getText();
    }
}