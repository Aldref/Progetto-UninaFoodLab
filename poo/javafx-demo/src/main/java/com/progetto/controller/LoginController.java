package com.progetto.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
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
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Passa dimensioni minime e massime per la homepage
            HomepageUtenteController controller = SceneSwitcher.switchScene(
                stage,
                "/fxml/homepageutente.fxml",
                "Homepage Utente - UninaFoodLab",
                true, // Resizable
                800, 600,
                1920, 1080  
            );
            
            stage.setMaximized(true);
        
            // Carica le card nella homepage DA TOGLIERE ASSOLUTAMENTE
            for (int i = 0; i < 11; i++) {
                FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("/fxml/cardcorso2.fxml"));
                Parent card = cardLoader.load();
                controller.addCard(card);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void RegisterClick(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            SceneSwitcher.switchScene(
                stage,
                "/fxml/registerpage.fxml",
                "Registrazione - UninaFoodLab",
                false, 
                500, 400,
                -1, -1   
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}