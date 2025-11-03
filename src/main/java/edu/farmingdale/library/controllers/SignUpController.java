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

    // Form fields
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private CheckBox termsCheckBox;

    // Labels and buttons
    @FXML private Label errorLabel;
    @FXML private Button signUpButton;
    @FXML private Button backToLoginButton;



    @FXML
    private void createAccount() {

        if (fullNameField.getText().isEmpty() || emailField.getText().isEmpty()
                || passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            errorLabel.setText("Please fill all fields.");
            errorLabel.setVisible(true);
            return;
        }

        if (!nameIsValid(fullNameField.getText())) {
            errorLabel.setText("Please enter a first and last name.");
            errorLabel.setVisible(true);
            return;
        }

        if (!emailIsValid(fullNameField.getText(), emailField.getText())) {
            errorLabel.setText("Must use personal farmingdale.edu Email.");
            errorLabel.setVisible(true);
            return;
        }

        if (!termsCheckBox.isSelected()) {
            errorLabel.setText("Please accept terms and conditions.");
            errorLabel.setVisible(true);
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            errorLabel.setText("Passwords do not match.");
            errorLabel.setVisible(true);
            return;
        }

        if (!includesSigns(passwordField.getText())) {
            errorLabel.setText("Password must contain a symbol, number, uppercase, and lowercase.");
            errorLabel.setVisible(true);
            return;
        }

        if (Library.getInstance().emailExists(emailField.getText())) {
            errorLabel.setText("Email already exists.");
            errorLabel.setVisible(true);
            return;
        }

        // ✅ Passed all checks → Create student
        String fullName = fullNameField.getText().trim();
        String[] parts = fullName.split("\\s+");
        String firstName = parts[0];
        String lastName = parts[parts.length - 1];

        Library.getInstance().addStudent(
                new Student(passwordField.getText(), emailField.getText(), lastName, firstName)
        );

        errorLabel.setText("✅ Account Created Successfully!");
        errorLabel.setVisible(true);
        passwordField.setText("");
        confirmPasswordField.setText("");
        fullNameField.setText("");
        emailField.setText("");
        termsCheckBox.setSelected(false);

    }


    @FXML
    private void updateCheck(){
        errorLabel.setVisible(false);
    }

    //changes schene root to login
    @FXML
    private void goToLogin() throws IOException {
        Parent newRoot = FXMLLoader.load(getClass().getResource("/edu/farmingdale/library/login-screen.fxml"));
        Scene scene = backToLoginButton.getScene();
        scene.setRoot(newRoot);
    }

    //function to check if the passwords has signs&num&upperlower
    private boolean includesSigns(String str){
        String pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$";
        return (str != null) && str.matches(pattern);
    }

    //function to check if the name is a valid name
    private boolean nameIsValid(String str){
        String pattern = "^[A-Za-z]+\\s+[A-Za-z]+$";
        return (str != null) && str.matches(pattern);
    }

    //function to check if the email is valid school email
    public static boolean emailIsValid(String name, String email) {
        if (name == null || email == null) return false;

        name = name.trim();
        email = email.trim().toLowerCase();

        String namePattern = "^[A-Za-z]+\\s+[A-Za-z]+$";
        if (!name.matches(namePattern)) {
            return false;
        }

        String[] parts = name.split("\\s+");
        String lastName = parts[1];
        char requiredInitial = Character.toLowerCase(lastName.charAt(0));


        return email.startsWith(String.valueOf(requiredInitial))
                && email.endsWith("@farmingdale.edu");
    }



}
