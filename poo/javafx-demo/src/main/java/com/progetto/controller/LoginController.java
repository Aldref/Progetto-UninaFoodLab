package com.progetto.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.IOException;

import com.progetto.utils.SceneSwitcher;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.Entity.entityDao.UtenteVisitatoreDao;

public class LoginController {

    /**
     * Gestisce il login. Restituisce null se login ok, oppure un messaggio di errore se fallito.
     */
    public String handleLogin(ActionEvent event, String email, String password) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Crea un oggetto UtenteVisitatore
            UtenteVisitatore utente = new UtenteVisitatore();
            utente.setEmail(email);
            utente.setPassword(password);

            // Usa UtenteDao per verificare esistenza e tipo account
            UtenteVisitatoreDao utenteDao = new UtenteVisitatoreDao();
            boolean esiste = utenteDao.LoginUtente(utente);
            if (!esiste) {
                return "Email o password errati.";
            }

            String tipo = utenteDao.TipoDiAccount(utente);
            if ("c".equals(tipo)) {
                // Chef
                SceneSwitcher.switchToMainApp(stage, "/fxml/homepagechef.fxml", "UninaFoodLab - Homepage");
                return null;
            } else if ("v".equals(tipo)) {
                // Utente Visitatore: carica tutti i dati e imposta loggedUser
                utenteDao.recuperaDatiUtente(utente);
                UtenteVisitatore.loggedUser = utente;
                SceneSwitcher.switchToMainApp(stage, "/fxml/homepageutente.fxml", "UninaFoodLab - Homepage");
                return null;
            } else {
                return "Tipo di account non riconosciuto.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Errore interno. Riprova.";
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