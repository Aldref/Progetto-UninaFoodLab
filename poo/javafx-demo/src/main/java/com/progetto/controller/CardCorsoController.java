package com.progetto.controller;

import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CardCorsoController {

    @FXML
    private Button buyButton;
    @FXML
    private HBox buttonsBox; 
    @FXML
    private VBox priceSection;
    @FXML
    private Label acquistatoBadge;
    @FXML
    private Label priceLabel;
    @FXML
    private Button calendarButton;

    private boolean isAcquistato = false;
    private boolean isEnrolledPage = false;

    public void setAcquistato(boolean acquistato) {
        this.isAcquistato = acquistato;
        updateCardState();
    }

    public void setEnrolledPage(boolean enrolledPage) {
        this.isEnrolledPage = enrolledPage;
        updateCardState();
    }

    private void updateCardState() {
        if (isEnrolledPage) {
            // In enrolledcourses: rimuovi completamente il bottone acquista dall'HBox
            if (priceSection != null) priceSection.setVisible(false);
            if (acquistatoBadge != null) acquistatoBadge.setVisible(true);
            if (calendarButton != null) calendarButton.setVisible(true);
            
            // Rimuovi il bottone acquista dall'HBox invece di renderlo invisibile
            if (buttonsBox != null && buyButton != null) {
                if (buttonsBox.getChildren().contains(buyButton)) {
                    buttonsBox.getChildren().remove(buyButton);
                }
                buttonsBox.setAlignment(javafx.geometry.Pos.CENTER);
            }
        } else {
            if (priceSection != null) priceSection.setVisible(true);
            if (acquistatoBadge != null) acquistatoBadge.setVisible(isAcquistato);
            if (calendarButton != null) calendarButton.setVisible(true);
            
            if (buttonsBox != null && buyButton != null) {
                if (!buttonsBox.getChildren().contains(buyButton)) {
                    buttonsBox.getChildren().add(0, buyButton); 
                }
                buyButton.setVisible(true);
                buttonsBox.setAlignment(javafx.geometry.Pos.CENTER);
            }
        }
    }

    @FXML
    private void initialize() {
        updateCardState();
    }

    @FXML
    private void handlePurchase() {
        // Animazione del bottone
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), buyButton);
        scaleDown.setToX(0.95);
        scaleDown.setToY(0.95);
        
        scaleDown.setOnFinished(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), buyButton);
            scaleUp.setToX(1.0);
            scaleUp.setToY(1.0);
            scaleUp.play();
        });
        
        scaleDown.play();
        
        // Qui aggiungerai la logica di acquisto
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acquisto completato");
        alert.setHeaderText("Corso acquistato con successo!");
        alert.setContentText("Il corso è stato aggiunto ai tuoi corsi iscritti.");
        alert.showAndWait();
    }

    @FXML
    private void handleShowCalendar() {
        try {
            Stage dialogStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/calendardialog.fxml"));
            VBox dialogContent = loader.load();
            
            CalendarDialogController controller = loader.getController();
            // Qui puoi passare i dati specifici del corso
            
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            dialogStage.setTitle("Calendario Lezioni");
            
            javafx.scene.Scene dialogScene = new javafx.scene.Scene(dialogContent);
            dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            
            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}