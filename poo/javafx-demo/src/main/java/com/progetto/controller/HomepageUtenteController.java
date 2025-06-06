package com.progetto.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javafx.util.Duration;

public class HomepageUtenteController {
    @FXML
    private ComboBox<String> cbCategoria;
    @FXML
    private ComboBox<String> cbFrequenza;
    @FXML
    private ComboBox<String> cbTipoLezione;
    @FXML
    private FlowPane mainContentArea;
    @FXML
    private Label pageLabel;

    private List<Node> allCards = new ArrayList<>();
    private int currentPage = 0;
    private final int CARDS_PER_PAGE = 12;

    @FXML
    public void initialize() {
        loadCards();
    }

    private void loadCards() {
        allCards.clear();
        try {
            for (int i = 1; i <= 15; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardcorso.fxml"));
                Node card = loader.load();
                CardCorsoController controller = loader.getController();
                controller.setAcquistato(false); 
                
                allCards.add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateCards();
    }

    public void setCards(List<Node> cards) {
        this.allCards = cards;
        this.currentPage = 0;
        updateCards();
    }

    public void addCard(Node card) {
        allCards.add(card);
        updateCards();
    }

    private void updateCards() {
        mainContentArea.getChildren().clear();
        int start = currentPage * CARDS_PER_PAGE;
        int end = Math.min(start + CARDS_PER_PAGE, allCards.size());
        for (int i = start; i < end; i++) {
            mainContentArea.getChildren().add(allCards.get(i));
        }
        pageLabel.setText("Pagina " + (currentPage + 1));
    }

    // Esempio per cambiare scena
    @FXML
    private void goToEnrolledCourses() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/enrolledcourses.fxml", "UninaFoodLab - Corsi Iscritti");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAccountManagement() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/accountmanagement.fxml", "UninaFoodLab - Gestione Account");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void LogoutClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/logoutdialog.fxml"));
            VBox dialogContent = loader.load();
            LogoutDialogController dialogController = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            dialogStage.setTitle("Conferma Logout");

            javafx.scene.Scene dialogScene = new javafx.scene.Scene(dialogContent);
            dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();

            if (dialogController.isConfirmed()) {
                Stage stage = (Stage) mainContentArea.getScene().getWindow();
                SceneSwitcher.switchToLogin(stage, "/fxml/loginpage.fxml", "UninaFoodLab - Login");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void nextPage() {
        if ((currentPage + 1) * CARDS_PER_PAGE < allCards.size()) {
            currentPage++;
            updateCards();
        }
    }

    @FXML
    private void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            updateCards();
        }
    }

    @FXML
    private void handleSearch() {
        String categoria = cbCategoria.getValue();
        String frequenza = cbFrequenza.getValue();
        String tipoLezione = cbTipoLezione.getValue();

        System.out.println("Categoria: " + categoria);
        System.out.println("Frequenza: " + frequenza);
        System.out.println("Tipo Lezione: " + tipoLezione);

        // Qui inserisci la logica per filtrare i corsi con il db
    }

}