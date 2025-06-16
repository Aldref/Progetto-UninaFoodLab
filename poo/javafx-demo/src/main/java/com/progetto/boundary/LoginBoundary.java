package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import com.progetto.controller.LoginController;

public class LoginBoundary {

    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;

    private final LoginController controller = new LoginController();

    @FXML
    private void LoginClick(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        controller.handleLogin(event, email, password);
    }

    @FXML
    private void RegisterClick(ActionEvent event) {
        controller.handleRegister(event);
    }
}