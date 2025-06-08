package com.progetto.controller;

import com.progetto.boundary.UserCardsBoundary;
import com.progetto.utils.CardValidator;
import com.progetto.utils.SceneSwitcher;
import com.progetto.utils.SuccessDialogUtils;

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
            // Simula il salvataggio della carta
            showCardSaveSuccessDialog();
            
            boundary.clearFieldsFromController();
            // Dopo il salvataggio, aggiorna la lista:
            // loadCardsFromDb();
        }
    }
    
    // Nuovo metodo per gestire il dialog di successo
    private void showCardSaveSuccessDialog() {
        try {
            Stage parentStage = null; 
            
            SuccessDialogUtils.showGenericSuccessDialog(parentStage, 
                "Carta Salvata!", 
                "La carta è stata aggiunta con successo al tuo account.");
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback al messaggio semplice
            boundary.showSuccessMessage("Carta salvata con successo!");
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