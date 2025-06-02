package com.progetto.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

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
    private final int CARDS_PER_PAGE = 10;

    
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

    @FXML
    private void LogoutClick() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Logout");
        alert.setHeaderText(null);
        alert.setContentText("Sei sicuro di voler uscire dal tuo account?");

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            try {
                javafx.stage.Stage stage = (javafx.stage.Stage) mainContentArea.getScene().getWindow();
                SceneSwitcher.switchScene(stage, "/fxml/Loginpage.fxml", "UninaFoodLab Login", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}