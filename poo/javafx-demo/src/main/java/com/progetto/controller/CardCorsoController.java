package com.progetto.controller;

import com.progetto.utils.SceneSwitcher;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
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
                buyButton.setManaged(false); // Importante: rimuove completamente dallo spazio
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

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acquisto completato");
        alert.setHeaderText("Corso acquistato con successo!");
        alert.setContentText("Il corso è stato aggiunto ai tuoi corsi iscritti.");
        alert.showAndWait();
    }

    public void handleShowCalendar() {
        showCalendarDialog();
    }

    public void showCalendarDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/calendardialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Calendario lezioni");
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            Stage mainStage = (Stage) calendarButton.getScene().getWindow();
            dialogStage.initOwner(mainStage);

            
            SceneSwitcher.showDialogCentered(mainStage, dialogStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}