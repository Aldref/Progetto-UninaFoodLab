package com.progetto.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.IOException;

import com.progetto.utils.SceneSwitcher;

public class LoginController {

    public void handleLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.switchToMainApp(stage, "/fxml/homepageutente.fxml", "UninaFoodLab - Homepage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleRegister(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.switchToRegister(stage, "/fxml/registerpage.fxml", "Registrazione - UninaFoodLab");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}