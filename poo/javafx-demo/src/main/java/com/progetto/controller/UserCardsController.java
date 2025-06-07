package com.progetto.controller;

import com.progetto.boundary.UserCardsBoundary;
import com.progetto.utils.CardValidator;
import com.progetto.utils.SceneSwitcher;

import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.util.List;

public class UserCardsController {
    
    private UserCardsBoundary boundary;
    
    public UserCardsController(UserCardsBoundary boundary) {
        this.boundary = boundary;
    }
    
    public void loadCardsFromDb() {
        // In futuro: carica le carte dal DB e aggiorna la boundary
        // boundary.setCards(listaCarte);
    }

    public void saveNewCard() {
        boundary.clearAllErrors();
        if (validateAllFields()) {
            // In futuro: salva la carta nel DB
            boundary.showSuccessMessage("Carta salvata con successo!");
            boundary.clearFieldsFromController();
            // Dopo il salvataggio, aggiorna la lista:
            // loadCardsFromDb();
        }
    }
    
    private boolean validateAllFields() {
        // ...come già presente...
        return true;
    }
    
    public void deleteCard(String cardId) {
        // In futuro: elimina la carta dal DB usando l'id
        boundary.showSuccessMessage("Carta eliminata con successo.");
        // Dopo l'eliminazione, aggiorna la lista:
        // loadCardsFromDb();
    }

    public void goBack(Stage stage) {
        SceneSwitcher.switchToScene(stage, "/fxml/accountmanagement.fxml");
    }
}