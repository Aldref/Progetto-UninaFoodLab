package com.progetto.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import java.io.IOException;

import com.progetto.utils.SceneSwitcher;

public class LoginController {

    public void handleLogin(ActionEvent event, String email, String password) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Crea un oggetto UtenteVisitatore
            com.progetto.Entity.EntityDto.UtenteVisitatore utente = new com.progetto.Entity.EntityDto.UtenteVisitatore();
            utente.setEmail(email);
            utente.setPassword(password);

            // Usa UtenteDao per verificare esistenza e tipo account
            com.progetto.Entity.entityDao.UtenteVisitatoreDao utenteDao = new com.progetto.Entity.entityDao.UtenteVisitatoreDao();
            boolean esiste = utenteDao.LoginUtente(utente);
            if (!esiste) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Login fallito");
                alert.setHeaderText(null);
                alert.setContentText("Email o password errati.");
                alert.showAndWait();
                return;
            }

            String tipo = utenteDao.TipoDiAccount(utente);
            if ("c".equals(tipo)) {
                // Chef
                SceneSwitcher.switchToMainApp(stage, "/fxml/homepagechef.fxml", "UninaFoodLab - Homepage");
            } else if ("v".equals(tipo)) {
                // Utente Visitatore: carica tutti i dati e imposta loggedUser
                utenteDao.recuperaDatiUtente(utente);
                com.progetto.Entity.EntityDto.UtenteVisitatore.loggedUser = utente;
                SceneSwitcher.switchToMainApp(stage, "/fxml/homepageutente.fxml", "UninaFoodLab - Homepage");
            } else {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Login fallito");
                alert.setHeaderText(null);
                alert.setContentText("Tipo di account non riconosciuto.");
                alert.showAndWait();
            }
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