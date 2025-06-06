package com.progetto.controller;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

import com.progetto.boundary.CardCorsoBoundary;
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;

import java.io.IOException;

public class HomepageUtenteController {
    private ComboBox<String> cbCategoria;
    private ComboBox<String> cbFrequenza;
    private ComboBox<String> cbTipoLezione;
    private FlowPane mainContentArea;
    private Label pageLabel;

    private List<Node> allCards = new ArrayList<>();
    private int currentPage = 0;
    private final int CARDS_PER_PAGE = 12;

    public HomepageUtenteController(FlowPane mainContentArea, Label pageLabel, ComboBox<String> cbCategoria, ComboBox<String> cbFrequenza, ComboBox<String> cbTipoLezione) {
        this.mainContentArea = mainContentArea;
        this.pageLabel = pageLabel;
        this.cbCategoria = cbCategoria;
        this.cbFrequenza = cbFrequenza;
        this.cbTipoLezione = cbTipoLezione;
    }
    //dati temporanei per le card dei corsi
    public void loadCards() {
        allCards.clear();
        try {
            for (int i = 1; i <= 15; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardcorso.fxml"));
                Node card = loader.load();
                CardCorsoBoundary boundary = loader.getController();

                String img = "/immagini/corsi/esempio.png";
                String titolo = "Corso Avanzato di Cucina Tradizionale " + i;
                String descrizione = "L’applicazione utilizza attualmente dati di esempio in attesa dell’integrazione con il database. È stata avviata una separazione tra boundary e controller, utile per gestire la UI e le azioni dell’utente. Anche se il controller contiene ancora riferimenti a componenti JavaFX, la struttura è pensata per evolvere verso un’architettura più pulita e modulare.";
                String inizio = "15/07/2025";
                String fine = "30/09/2025";
                String frequenza = "2 volte a settimana";
                String tipoLezione = (i % 2 == 0) ? "Online" : "Presenza";
                String prezzo = (i % 2 == 0) ? "250€" : "380€";

                boundary.setCourseImage(img);
                boundary.setCourseTitle(titolo);
                boundary.setCourseDescription(descrizione);
                boundary.setStartDate(inizio);
                boundary.setEndDate(fine);
                boundary.setFrequency(frequenza);
                boundary.setLessonType(tipoLezione);
                boundary.setPrice(prezzo);
                boundary.setAcquistato(false);

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

    public void goToEnrolledCourses() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/enrolledcourses.fxml", "UninaFoodLab - Corsi Iscritti");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToAccountManagement() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/accountmanagement.fxml", "UninaFoodLab - Gestione Account");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void LogoutClick() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            LogoutDialogBoundary dialogBoundary = SceneSwitcher.showLogoutDialog(stage);

            if (dialogBoundary.isConfirmed()) {
                SceneSwitcher.switchToLogin(stage, "/fxml/loginpage.fxml", "UninaFoodLab - Login");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nextPage() {
        if ((currentPage + 1) * CARDS_PER_PAGE < allCards.size()) {
            currentPage++;
            updateCards();
        }
    }

    public void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            updateCards();
        }
    }

    public void handleSearch() {
        String categoria = cbCategoria.getValue();
        String frequenza = cbFrequenza.getValue();
        String tipoLezione = cbTipoLezione.getValue();

        System.out.println("Categoria: " + categoria);
        System.out.println("Frequenza: " + frequenza);
        System.out.println("Tipo Lezione: " + tipoLezione);

        // Qui inserisci la logica per filtrare i corsi con il db
    }
}