package com.progetto.controller;

import com.progetto.boundary.PaymentPageBoundary;
import com.progetto.boundary.SuccessDialogBoundary;
import com.progetto.utils.CardValidator;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.regex.Pattern;

public class PaymentPageController {
    
    private PaymentPageBoundary boundary;
    
    // Pattern per validazione
    private static final Pattern NOME_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ]+(?:\\s+[a-zA-ZÀ-ÿ]+)*$");
    private static final Pattern CARTA_PATTERN = Pattern.compile("^[0-9]{13,19}$");
    private static final Pattern CVC_PATTERN = Pattern.compile("^[0-9]{3,4}$");
    private static final Pattern SCADENZA_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])/([0-9]{2})$");
    
    public PaymentPageController(PaymentPageBoundary boundary) {
        this.boundary = boundary;
    }
    
    public void processPayment() {
        boundary.clearAllErrors();
        
        if (validateAllFields()) {
            showSuccessDialog();
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
            boundary.showError("numerocarta", "Numero carta non valido (13-19 cifre)");
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
    
    private void showSuccessDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/successdialog.fxml"));
            Parent root = loader.load();
            
            SuccessDialogBoundary dialogBoundary = loader.getController();
            
            // dialogBoundary.setCourseName("Nome del corso acquistato"); // TODO: Passa il nome reale del corso
            
            Stage dialogStage = new Stage();
            dialogBoundary.setDialogStage(dialogStage);
            
            // Configura il dialog
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UNDECORATED); 
            dialogStage.setTitle("Pagamento Completato");
            dialogStage.setResizable(false);
            
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(scene);
            
            // Centra il dialog
            Stage parentStage = boundary.getStage();
            if (parentStage != null) {
                dialogStage.initOwner(parentStage);
                dialogStage.setOnShown(e -> {
                    double centerX = parentStage.getX() + (parentStage.getWidth() - dialogStage.getWidth()) / 2;
                    double centerY = parentStage.getY() + (parentStage.getHeight() - dialogStage.getHeight()) / 2;
                    dialogStage.setX(centerX);
                    dialogStage.setY(centerY);
                });
            } else {
                dialogStage.centerOnScreen();
            }
            
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            showSuccessAlert();
        }
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pagamento Completato");
        alert.setHeaderText("Successo!");
        alert.setContentText("Il pagamento è stato elaborato con successo.\nIl corso è stato aggiunto ai tuoi corsi iscritti.");
        alert.showAndWait();
    }
}