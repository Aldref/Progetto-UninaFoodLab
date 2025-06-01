package com.progetto.controller;

import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class CardCorsoController {

    @FXML
    private Button buyButton;

    @FXML
    private void handlePurchase() {
        // Animazione del bottone
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), buyButton);
        scaleDown.setToX(0.95);
        scaleDown.setToY(0.95);
        
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), buyButton);
        scaleUp.setToX(1.0);
        scaleUp.setToY(1.0);
        
        // Cambia testo e stile
        scaleDown.setOnFinished(e -> {
            buyButton.setText("✓ Aggiunto al carrello!");
            buyButton.setStyle("-fx-background-color: linear-gradient(to right, #00b894, #00a085);");
            scaleUp.play();
        });
        
        // Ripristina dopo 1.5 secondi
        Timeline resetTimeline = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
            buyButton.setText("ACQUISTA ORA");
            buyButton.setStyle(""); // Ripristina lo stile CSS originale
        }));
        
        scaleDown.play();
        resetTimeline.play();
        
        // Qui puoi aggiungere la logica di acquisto
        System.out.println("Corso aggiunto al carrello!");
        
        // Esempio: chiamare un servizio o aggiornare un modello
        // CartService.addCourse("Cucina Italiana Tradizionale", 380);
    }
}