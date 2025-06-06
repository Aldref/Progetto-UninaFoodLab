package com.progetto.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Eventuali inizializzazioni future per loginpage.fxml
    }

    @FXML
    private void LoginClick(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Va a schermo intero
            SceneSwitcher.switchToMainApp(stage, "/fxml/homepageutente.fxml", "UninaFoodLab - Homepage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void RegisterClick(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            SceneSwitcher.switchToRegister(stage, "/fxml/registerpage.fxml", "Registrazione - UninaFoodLab");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}