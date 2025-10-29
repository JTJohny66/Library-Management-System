package edu.farmingdale.library.controllers;

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
    @FXML private TextField userNameField;
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

    @FXML private void login(){

    }

    @FXML
    private void signUp() throws IOException {
        Parent newRoot = FXMLLoader.load(getClass().getResource("/edu/farmingdale/library/sign-up-screen.fxml"));
        Scene scene = loginButton.getScene();
        scene.setRoot(newRoot);
    }

}
