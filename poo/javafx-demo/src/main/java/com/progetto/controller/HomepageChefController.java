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

public class HomepageChefController {
    private FlowPane mainContentArea;
    private Label pageLabel;
    private ComboBox<String> categoryComboBox;
    private ComboBox<String> frequencyComboBox;
    private ComboBox<String> lessonTypeComboBox;
    private Label chefNameLabel;

    private List<Node> allCourseCards = new ArrayList<>();
    private int currentPage = 0;
    private final int CARDS_PER_PAGE = 12;

    public HomepageChefController(FlowPane mainContentArea, Label pageLabel, 
                                ComboBox<String> categoryComboBox, ComboBox<String> frequencyComboBox,
                                ComboBox<String> lessonTypeComboBox, Label chefNameLabel) {
        this.mainContentArea = mainContentArea;
        this.pageLabel = pageLabel;
        this.categoryComboBox = categoryComboBox;
        this.frequencyComboBox = frequencyComboBox;
        this.lessonTypeComboBox = lessonTypeComboBox;
        this.chefNameLabel = chefNameLabel;
    }

    public void initializeSearchFilters() {
        
        categoryComboBox.getItems().clear();
        frequencyComboBox.getItems().clear();
        lessonTypeComboBox.getItems().clear();
        
        chefNameLabel.setText("Mario Rossi");
    }

    public void searchCourses() {
        // Implementa la logica di ricerca nei corsi dello chef
        String categoria = categoryComboBox.getValue();
        String frequenza = frequencyComboBox.getValue();
        String tipoLezione = lessonTypeComboBox.getValue();
        
        // Per ora ricarica semplicemente tutti i corsi
        loadChefCourses();
    }

    public void loadChefCourses() {
        allCourseCards.clear();
        try {
            // Dati di esempio per corsi del chef
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
                "Cucina Regionale"
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
                "Viaggio nelle cucine regionali italiane: dalla Sicilia al Trentino."
            };

            // Array con nomi chef variabili
            String[] chefNames = {
                "Chef Mario Rossi",
                "Chef Giuseppe Verdi", 
                "Chef Anna Bianchi",
                "Chef Francesco Neri",
                "Chef Laura Gialli",
                "Chef Roberto Blues",
                "Chef Sofia Viola",
                "Chef Alessandro Rosa",
                "Chef Elena Grigi",
                "Chef Davide Azzurri"
            };

            // Array con anni di esperienza variabili
            String[] experienceYears = {
                "15", "12", "8", "20", "10", 
                "18", "6", "25", "14", "22"
            };
            
            for (int i = 0; i < courseTitles.length; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardcorso.fxml"));
                Node card = loader.load();
                CardCorsoBoundary boundary = loader.getController();

                // Configura la card per la modalità chef
                boundary.setChefMode(true);

                // Imposta i dati del corso con variabili
                String title = courseTitles[i];
                String description = descriptions[i];
                String startDate = "15/0" + ((i % 9) + 1) + "/2025";
                String endDate = "30/" + String.format("%02d", ((i % 12) + 1)) + "/2025";
                String frequency = (i % 2 == 0) ? "2 volte a settimana" : "1 volta a settimana";
                String price = null; // Per chef non serve il prezzo
                String chefName = chefNames[i % chefNames.length];
                String experience = experienceYears[i % experienceYears.length];
                String maxPeople = String.valueOf(20 + (i % 5) * 5); // esempio: 20, 25, 30, 35, 40

                boundary.setCourseData(title, description, startDate, endDate, frequency, price, chefName, experience, maxPeople);

                // Dati fittizi per i tipi di cucina: alterna tra uno e due tipi
                String cucina1 = (i % 3 == 0) ? "Italiana" : (i % 3 == 1) ? "Vegetariana" : "Giapponese";
                String cucina2 = (i % 2 == 0) ? "" : "Fusion"; // Solo per alcuni corsi
                boundary.setCuisineTypes(cucina1, cucina2);

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

    public void goToCreateCourse() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/createcourse.fxml", "UninaFoodLab - Crea Nuovo Corso");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToMonthlyReport() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/monthlyreport.fxml", "UninaFoodLab - Resoconto Mensile");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToAccountManagement() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/accountmanagementchef.fxml", "UninaFoodLab - Gestione Account Chef");
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