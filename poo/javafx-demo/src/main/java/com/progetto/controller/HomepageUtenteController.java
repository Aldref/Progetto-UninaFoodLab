package com.progetto.controller;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

import com.progetto.boundary.CardCorsoBoundary;
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;

import java.io.IOException;

public class HomepageUtenteController {
    private FlowPane mainContentArea;
    private Label pageLabel;
    private ComboBox<String> categoryComboBox;
    private ComboBox<String> frequencyComboBox;
    private ComboBox<String> lessonTypeComboBox;
    private Label userNameLabel;

    // Aggiungi queste variabili mancanti
    private List<Node> allCourseCards = new ArrayList<>();
    private int currentPage = 0;
    private final int CARDS_PER_PAGE = 12;

    public HomepageUtenteController(FlowPane mainContentArea, Label pageLabel, 
                                  ComboBox<String> categoryComboBox, ComboBox<String> frequencyComboBox,
                                  ComboBox<String> lessonTypeComboBox, Label userNameLabel) {
        this.mainContentArea = mainContentArea;
        this.pageLabel = pageLabel;
        this.categoryComboBox = categoryComboBox;
        this.frequencyComboBox = frequencyComboBox;
        this.lessonTypeComboBox = lessonTypeComboBox;
        this.userNameLabel = userNameLabel;
    }

    public void initializeSearchFilters() {
        categoryComboBox.getItems().clear();
        frequencyComboBox.getItems().clear();
        lessonTypeComboBox.getItems().clear();
        
        userNameLabel.setText("Mario Rossi");
    }

    public void searchCourses() {
        // Implementa la logica di ricerca
        String categoria = categoryComboBox.getValue();
        String frequenza = frequencyComboBox.getValue();
        String tipoLezione = lessonTypeComboBox.getValue();
        
        // Per ora ricarica semplicemente tutti i corsi
        loadCourses();
    }

    public void loadCourses() {
        allCourseCards.clear();
        try {
            // Dati di esempio per corsi disponibili
            String[] courseTitles = {
                "Corso di Pasta Fresca",
                "Risotti e Cereali",
                "Dolci Tradizionali",
                "Cucina Mediterranea",
                "Pizza Napoletana",
                "Cucina Vegana Italiana",
                "Antipasti Creativi",
                "Secondi di Pesce",
                "Salse e Sughi",
                "Cucina Regionale",
                "Cucina Francese Classica",
                "Sushi e Cucina Giapponese",
                "Pasticceria Moderna",
                "Cucina Thailandese",
                "Barbecue Americano",
                "Cucina Indiana",
                "Panificazione Artigianale",
                "Cucina Messicana",
                "Dessert al Cioccolato",
                "Cucina Libanese"
            };
            
            String[] descriptions = {
                "Impara l'arte della pasta fresca fatta in casa con tecniche tradizionali.",
                "Scopri i segreti dei risotti cremosi e dei cereali della tradizione italiana.",
                "Dolci della nonna: ricette autentiche tramandate di generazione in generazione.",
                "Un viaggio nella cucina mediterranea con ingredienti freschi e genuini.",
                "La vera pizza napoletana: dall'impasto alla cottura nel forno a legna.",
                "Cucina plant-based italiana: sapori autentici senza ingredienti animali.",
                "Antipasti creativi per stupire i tuoi ospiti con presentazioni uniche.",
                "Pesce fresco del Mediterraneo: tecniche di cottura e preparazione.",
                "Salse tradizionali e sughi regionali per valorizzare ogni piatto.",
                "Viaggio nelle cucine regionali italiane: dalla Sicilia al Trentino.",
                "Tecniche classiche della cucina francese con ingredienti di qualità.",
                "L'arte del sushi e i sapori autentici della cucina giapponese.",
                "Pasticceria contemporanea con tecniche innovative e presentazioni moderne.",
                "Spezie esotiche e sapori piccanti della tradizione thailandese.",
                "Grigliata perfetta: dalle marinature alle cotture low and slow.",
                "Curry, spezie e i sapori intensi della cucina indiana tradizionale.",
                "Pane fatto in casa: dalla farina alla tavola con lieviti naturali.",
                "Tacos, enchiladas e i sapori vivaci del Messico autentico.",
                "L'arte del cioccolato: dalla ganache alle decorazioni più elaborate.",
                "Hummus, falafel e i sapori del Medio Oriente nella cucina libanese."
            };
            
            for (int i = 0; i < courseTitles.length; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardcorso.fxml"));
                Node card = loader.load();
                CardCorsoBoundary boundary = loader.getController();
                
                String title = courseTitles[i];
                String description = descriptions[i];
                String startDate = "15/0" + ((i % 9) + 1) + "/2025";
                String endDate = "30/" + String.format("%02d", ((i % 12) + 1)) + "/2025";
                String frequency = (i % 2 == 0) ? "2 volte a settimana" : "1 volta a settimana";
                String price = "€" + (100 + i * 15) + ",00";
                
                boundary.setCourseData(title, description, startDate, endDate, frequency, price);
                
                // Una sola immagine per tutte le card
                String imagePath = "/immagini/corsi/esempio.png";
                boundary.setCourseImage(imagePath);
                
                allCourseCards.add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateCourseCards();
    }

    private void updateCourseCards() {
        mainContentArea.getChildren().clear();
        int start = currentPage * CARDS_PER_PAGE;
        int end = Math.min(start + CARDS_PER_PAGE, allCourseCards.size());
        for (int i = start; i < end; i++) {
            mainContentArea.getChildren().add(allCourseCards.get(i));
        }
        int totalPages = (int) Math.ceil((double) allCourseCards.size() / CARDS_PER_PAGE);
        pageLabel.setText("Pagina " + (currentPage + 1) + " di " + Math.max(1, totalPages));
    }

    public void goToEnrolledCourses() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/enrolledcourses.fxml", "UninaFoodLab - Corsi Iscritti");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToUserCards() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/usercards.fxml", "UninaFoodLab - Carte Utente");
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
        if ((currentPage + 1) * CARDS_PER_PAGE < allCourseCards.size()) {
            currentPage++;
            updateCourseCards();
        }
    }

    public void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            updateCourseCards();
        }
    }
}