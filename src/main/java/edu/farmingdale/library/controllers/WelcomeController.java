package edu.farmingdale.library.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class WelcomeController {

    @FXML
    private void handleSignIn(ActionEvent event) throws Exception {
        switchScene(event, "/edu/farmingdale/library/login-screen.fxml");
    }

    @FXML
    private void handleSignUp(ActionEvent event) throws Exception {
        switchScene(event, "/edu/farmingdale/library/sign-up-screen.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlPath) throws Exception {
        Parent newRoot = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(newRoot);
    }
}
