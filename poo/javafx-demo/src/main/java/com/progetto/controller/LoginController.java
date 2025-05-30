package com.progetto.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;


import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Eventuali inizializzazioni future per loginpage.fxml
    }

    @FXML
    private void LoginClick(ActionEvent event) {
        System.out.println("Login button clicked!");
    }

    @FXML
    private void RegisterClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/registerpage.fxml"));
            Parent registerRoot = fxmlLoader.load();

            Scene registerScene = new Scene(registerRoot, 600, 400); 
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            currentStage.setScene(registerScene);
            currentStage.setTitle("Registrazione - UninaFoodLab");

            // Blocca dimensioni anche qui
            currentStage.setResizable(false);
            currentStage.setMinWidth(600);
            currentStage.setMaxWidth(600);
            currentStage.setMinHeight(400);
            currentStage.setMaxHeight(400);

            currentStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
