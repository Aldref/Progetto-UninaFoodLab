package com.progetto.controller;

import com.progetto.utils.SceneSwitcher;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CardCorsoController {

    private final Button buyButton;
    private final HBox buttonsBox;
    private final VBox priceSection;
    private final Label acquistatoBadge;
    private final Label priceLabel;
    private final Button calendarButton;

    private boolean isEnrolledPage = false;

    public CardCorsoController(Button buyButton, HBox buttonsBox, VBox priceSection, Label acquistatoBadge, Label priceLabel, Button calendarButton) {
        this.buyButton = buyButton;
        this.buttonsBox = buttonsBox;
        this.priceSection = priceSection;
        this.acquistatoBadge = acquistatoBadge;
        this.priceLabel = priceLabel;
        this.calendarButton = calendarButton;
    }

    public void initialize() {
        updateCardState();
    }

    public void setAcquistato(boolean acquistato) {
        acquistatoBadge.setVisible(acquistato);
    }

    public void setEnrolledPage(boolean enrolledPage) {
        this.isEnrolledPage = enrolledPage;
        updateCardState();
    }

    private void updateCardState() {
        if (isEnrolledPage) {
            if (buyButton != null) {
                buyButton.setVisible(false);
                buyButton.setManaged(false);
            }
            if (acquistatoBadge != null) acquistatoBadge.setVisible(true);
            if (buttonsBox != null) {
                buttonsBox.getStyleClass().removeAll("centered-calendar");
                buttonsBox.getStyleClass().add("centered-calendar");
                calendarButton.setVisible(true);
            }
        } else {
            if (buyButton != null) {
                buyButton.setVisible(true);
                buyButton.setManaged(true);
            }
            if (acquistatoBadge != null) acquistatoBadge.setVisible(false);
            if (buttonsBox != null) {
                buttonsBox.getStyleClass().removeAll("centered-calendar");
                calendarButton.setVisible(true);
            }
        }
    }

    public void handlePurchase() {
        // Animazione del pulsante
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), buyButton);
        scaleDown.setToX(0.95);
        scaleDown.setToY(0.95);

        scaleDown.setOnFinished(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), buyButton);
            scaleUp.setToX(1.0);
            scaleUp.setToY(1.0);
            
            scaleUp.setOnFinished(event -> {
                // Usa Platform.runLater e delega tutto a SceneSwitcher
                Platform.runLater(this::showPaymentDialog);
            });
            
            scaleUp.play();
        });

        scaleDown.play();
    }

    private void showPaymentDialog() {
        try {
            Stage stage = (Stage) buyButton.getScene().getWindow(); 
            SceneSwitcher.switchToScene(stage, "/fxml/paymentpage.fxml"); 
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert();
        }
    }

    private void handleSuccessfulPurchase() {
        setAcquistato(true);
        
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Acquisto Completato");
        successAlert.setHeaderText("Corso acquistato con successo!");
        successAlert.setContentText("Il corso è stato aggiunto ai tuoi corsi iscritti. Ora puoi visualizzare il calendario delle lezioni.");
        successAlert.showAndWait();
    }

    private void showErrorAlert() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Errore");
        errorAlert.setHeaderText("Errore nell'apertura del pagamento");
        errorAlert.setContentText("Si è verificato un errore. Riprova più tardi.");
        errorAlert.showAndWait();
    }

    public void handleShowCalendar() {
        try {
            Stage mainStage = (Stage) calendarButton.getScene().getWindow();
            SceneSwitcher.showCalendarDialog(mainStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}