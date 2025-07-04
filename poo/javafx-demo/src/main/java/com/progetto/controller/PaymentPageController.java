package com.progetto.controller;

import com.progetto.boundary.PaymentPageBoundary;
import com.progetto.boundary.SuccessDialogBoundary;
import com.progetto.utils.CardValidator;
import com.progetto.utils.SuccessDialogUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.regex.Pattern;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.Entity.EntityDto.Corso;
import com.progetto.Entity.EntityDto.CartaDiCredito;
import com.progetto.Entity.entityDao.CartaDiCreditoDao;
import com.progetto.Entity.entityDao.UtenteVisitatoreDao;

public class PaymentPageController {
    // Carica le carte salvate dell'utente loggato e le passa alla boundary
    public void loadSavedCardsForUser() {
        UtenteVisitatore utente = UtenteVisitatore.loggedUser;
        if (utente != null) {
            CartaDiCreditoDao dao = new CartaDiCreditoDao();
            List<CartaDiCredito> carte = dao.getCarteByUtenteId(utente.getId_UtenteVisitatore());
            boundary.setSavedCards(carte);
        }
    }
    // Carica le carte salvate dell'utente loggato e le passa alla boundary
    public void loadSavedCardsForUser(int idUtente) {
        CartaDiCreditoDao dao = new CartaDiCreditoDao();
        List<CartaDiCredito> carte = dao.getCarteByUtenteId(idUtente);
        boundary.setSavedCards(carte);
    }
    
    private PaymentPageBoundary boundary;
    
    // Pattern per validazione
    private static final Pattern NOME_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ]+(?:\\s+[a-zA-ZÀ-ÿ]+)*$");
    private static final Pattern CARTA_PATTERN = Pattern.compile("^[0-9]{16}$");
    private static final Pattern CVC_PATTERN = Pattern.compile("^[0-9]{3,4}$");
    private static final Pattern SCADENZA_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])/([0-9]{2})$");
    
    public PaymentPageController(PaymentPageBoundary boundary) {
        this.boundary = boundary;
    }
    
    // Metodo per pagamento con carta salvata (pronto per integrazione DB)
    public void processPaymentWithSavedCard(String cardId) {
        if (cardId == null || cardId.isEmpty()) {
            showErrorAlert("Errore", "Carta non valida");
            return;
        }
        try {
            // Iscrivi l'utente al corso (simulazione, sostituisci con logica reale)
            iscriviUtenteAlCorso();
            showPaymentSuccessDialog();
        } catch (Exception e) {
            showErrorAlert("Errore Pagamento", "Si è verificato un errore durante l'elaborazione del pagamento.");
        }
    }
    
    public void processPayment() {
        // Pulisci errori precedenti
        boundary.clearAllErrors();
        
        
        if (boundary.getSelectedCard() != null) {
            String cardId = (String) boundary.getSelectedCard().get("id");
            processPaymentWithSavedCard(cardId);
            return;
        }
        
        // Altrimenti valida e processa nuova carta
        if (!validateAllFields()) {
            return;
        }
        
        try {
            // Se l'utente ha scelto di salvare la carta, salvala nel DB
            if (boundary.isSalvaCarta()) {
                salvaCartaUtente();
            }
            // Iscrivi l'utente al corso (simulazione, sostituisci con logica reale)
            iscriviUtenteAlCorso();
            showPaymentSuccessDialog();
        } catch (Exception e) {
            showErrorAlert("Errore Pagamento", "Si è verificato un errore durante l'elaborazione del pagamento.");
        }
    }

    // Metodo per iscrivere l'utente al corso (da personalizzare con la logica reale)
    private void iscriviUtenteAlCorso() {
        // Logica reale di iscrizione al corso
        UtenteVisitatore utente = UtenteVisitatore.loggedUser;
        Corso corso = boundary.getSelectedCorso();
        if (utente != null && corso != null) {
            new UtenteVisitatoreDao().AssegnaCorso(corso, utente);
        }
    }

    // Metodo per salvare la carta nel DB se richiesto
    private void salvaCartaUtente() {
        UtenteVisitatore utente = UtenteVisitatore.loggedUser;
        if (utente != null) {
            CartaDiCredito carta = boundary.getCartaInserita();
            if (carta != null) {
                new CartaDiCreditoDao().memorizzaCarta(carta, utente.getId_UtenteVisitatore());
            }
        }
    // NON chiudere qui la classe! Tutti i metodi successivi devono restare dentro PaymentPageController
    }
    
    // Nuovo metodo privato per gestire il dialog di successo
    private void showPaymentSuccessDialog() {
        try {
            // Usa null come parentStage - il dialog si centrerà sullo schermo
            Stage parentStage = null;
            String courseName = "";
            Corso corso = boundary.getSelectedCorso();
            if (corso != null && corso.getNome() != null) {
                courseName = corso.getNome();
            } else {
                courseName = "Corso acquistato";
            }
            SuccessDialogUtils.showPaymentSuccessDialog(parentStage, courseName);
            // Dopo aver mostrato il dialog di successo, torna alla homepage
            boundary.navigateToSuccess();
        } catch (Exception e) {
            // TODO: handle exception appropriately (logging or user feedback)
            showSuccessAlert(); // Fallback al dialog semplice
            // Anche nel caso di fallback, torna alla homepage
            boundary.navigateToSuccess();
        }
    }

    
    private boolean validateAllFields() {
        boolean isValid = true;
        
        // Validazione nome
        String nome = boundary.getNome();
        if (nome.isEmpty()) {
            boundary.showError("nome", "Il nome è obbligatorio");
            isValid = false;
        } else if (nome.length() < 2) {
            boundary.showError("nome", "Il nome deve contenere almeno 2 caratteri");
            isValid = false;
        } else if (nome.length() > 50) {
            boundary.showError("nome", "Il nome non può superare 50 caratteri");
            isValid = false;
        } else if (!NOME_PATTERN.matcher(nome.trim()).matches()) {
            boundary.showError("nome", "Il nome può contenere solo lettere e spazi");
            isValid = false;
        } else if (nome.trim().split("\\s+").length < 2) {
            boundary.showError("nome", "Inserisci nome e cognome");
            isValid = false;
        }
        
        // Validazione numero carta
        String numeroCarta = boundary.getNumeroCarta();
        if (numeroCarta.isEmpty()) {
            boundary.showError("numerocarta", "Il numero della carta è obbligatorio");
            isValid = false;
        } else if (!CARTA_PATTERN.matcher(numeroCarta).matches()) {
            boundary.showError("numerocarta", "Il numero della carta deve essere di 16 cifre");
            isValid = false;
        } else if (!CardValidator.isValidCardType(numeroCarta)) {
            boundary.showError("numerocarta", "Tipo di carta non supportato. Accettiamo solo Visa e Mastercard");
            isValid = false;
        }
        
        // Validazione scadenza
        String scadenza = boundary.getScadenza();
        if (scadenza.isEmpty()) {
            boundary.showError("scadenza", "La scadenza è obbligatoria");
            isValid = false;
        } else if (!SCADENZA_PATTERN.matcher(scadenza).matches()) {
            boundary.showError("scadenza", "Formato scadenza non valido (MM/AA)");
            isValid = false;
        } else if (!CardValidator.isValidExpiryDate(scadenza)) {
            boundary.showError("scadenza", "La carta è scaduta o la data non è valida");
            isValid = false;
        }
        
        // Validazione CVC
        String cvc = boundary.getCvc();
        if (cvc.isEmpty()) {
            boundary.showError("cvc", "Il CVC è obbligatorio");
            isValid = false;
        } else if (!CVC_PATTERN.matcher(cvc).matches()) {
            boundary.showError("cvc", "CVC non valido (3-4 cifre)");
            isValid = false;
        }
        
        return isValid;
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pagamento Completato");
        alert.setHeaderText("Successo!");
        alert.setContentText("Il pagamento è stato elaborato con successo.\nIl corso è stato aggiunto ai tuoi corsi iscritti.");
        alert.showAndWait();
        
        boundary.navigateToSuccess();
    }
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Errore");
        alert.setContentText(message);
        alert.showAndWait();
    }
}