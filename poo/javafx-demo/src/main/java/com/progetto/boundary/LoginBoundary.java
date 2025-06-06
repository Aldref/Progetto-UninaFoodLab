package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import com.progetto.controller.LoginController;

public class LoginBoundary {

    private final LoginController controller = new LoginController();

    @FXML
    private void LoginClick(ActionEvent event) {
        controller.handleLogin(event);
    }

    @FXML
    private void RegisterClick(ActionEvent event) {
        controller.handleRegister(event);
    }
}