package com.progetto.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.IOException;

import com.progetto.utils.SceneSwitcher;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.Entity.entityDao.UtenteVisitatoreDao;
import com.progetto.Entity.EntityDto.Chef;
import com.progetto.Entity.entityDao.ChefDao;

public class LoginController {
    public String handleLogin(ActionEvent event, String email, String password) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            UtenteVisitatoreDao utenteDao = new UtenteVisitatoreDao();
            String tipo = utenteDao.TipoDiAccount(email, password);
            if (tipo == null) {
                return "Tipo di account non riconosciuto.";
            }

            if ("c".equals(tipo)) {
                ChefDao chefDao = new ChefDao();
                Chef chef = new Chef();
                chef.setEmail(email);
                chef.setPassword(password);
                chefDao.recuperaDatiUtente(chef);
                Chef.loggedUser = chef;
                SceneSwitcher.switchToMainApp(stage, "/fxml/homepagechef.fxml", "UninaFoodLab - Homepage");
                return null;
            } else if ("v".equals(tipo)) {
                UtenteVisitatore utente = new UtenteVisitatore();
                utente.setEmail(email);
                utente.setPassword(password);
                utenteDao.recuperaDatiUtente(utente);
                UtenteVisitatore.loggedUser = utente;
                SceneSwitcher.switchToMainApp(stage, "/fxml/homepageutente.fxml", "UninaFoodLab - Homepage");
                return null;
            } else {
                return "Email o password errati.";
            }
        } catch (IOException e) {
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