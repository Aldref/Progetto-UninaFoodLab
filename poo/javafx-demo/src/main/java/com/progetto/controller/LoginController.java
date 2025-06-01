package com.progetto.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;
import javafx.scene.Node;


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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/homepageutente.fxml"));
            Parent homepageRoot = loader.load();

            HomepageUtenteController controller = loader.getController();

            
            for (int i = 0; i < 3; i++) {
                FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("/fxml/cardcorso2.fxml"));
                Parent card = cardLoader.load();
                
                
                card.getStylesheets().add(getClass().getResource("/css/cardcorso.css").toExternalForm());
                
                controller.addCard(card);
            }

            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(homepageRoot);
            
            
            scene.getStylesheets().add(getClass().getResource("/css/cardcorso.css").toExternalForm());
            
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
