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

    @FXML
    private javafx.scene.control.Label errorLabel;

    private final LoginController controller = new LoginController();

    @FXML
    private void LoginClick(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        String errorMsg = controller.handleLogin(event, email, password);
        if (errorMsg != null) {
            errorLabel.setText(errorMsg);
            errorLabel.setVisible(true);
        } else {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }
    }

    @FXML
    private void RegisterClick(ActionEvent event) {
        controller.handleRegister(event);
    }
}