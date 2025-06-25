package com.progetto.controller;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

import com.progetto.boundary.CardCorsoBoundary;
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;

import java.io.IOException;

public class EnrolledCoursesController {
    private Label userNameLabel;
    private ComboBox<String> categoryComboBox;
    private ComboBox<String> frequencyComboBox;
    private ComboBox<String> lessonTypeComboBox;
    private Button searchButton;
    private FlowPane mainContentArea;
    private ScrollPane scrollPane;
    private Label pageLabel;
    private Button prevButton;
    private Button nextButton;
    private Label totalCoursesLabel;

    // Variabili per la paginazione
    private List<Node> allCourseCards = new ArrayList<>();
    private int currentPage = 0;
    private final int CARDS_PER_PAGE = 12;

    public EnrolledCoursesController(Label userNameLabel, ComboBox<String> categoryComboBox,
                                   ComboBox<String> frequencyComboBox, ComboBox<String> lessonTypeComboBox,
                                   Button searchButton, FlowPane mainContentArea, ScrollPane scrollPane,
                                   Label pageLabel, Button prevButton, Button nextButton, Label totalCoursesLabel) {
        this.userNameLabel = userNameLabel;
        this.categoryComboBox = categoryComboBox;
        this.frequencyComboBox = frequencyComboBox;
        this.lessonTypeComboBox = lessonTypeComboBox;
        this.searchButton = searchButton;
        this.mainContentArea = mainContentArea;
        this.scrollPane = scrollPane;
        this.pageLabel = pageLabel;
        this.prevButton = prevButton;
        this.nextButton = nextButton;
        this.totalCoursesLabel = totalCoursesLabel;
    }

    public void initialize() {
        initializeSearchFilters();
        loadEnrolledCourses();
    }

    public void initializeSearchFilters() {
        // Inizializza i filtri di ricerca
        categoryComboBox.getItems().clear();
        frequencyComboBox.getItems().clear();
        lessonTypeComboBox.getItems().clear();
        
        // Aggiungi opzioni ai ComboBox
        categoryComboBox.getItems().addAll("Tutte le categorie", "Cucina Italiana", "Cucina Internazionale", "Pasticceria", "Panificazione");
        frequencyComboBox.getItems().addAll("Tutte le frequenze", "1 volta a settimana", "2 volte a settimana", "3 volte a settimana");
        lessonTypeComboBox.getItems().addAll("Tutti i tipi", "Presenza", "Online", "Ibrido");
        
        // Imposta i valori di default
        categoryComboBox.setValue("Tutte le categorie");
        frequencyComboBox.setValue("Tutte le frequenze");
        lessonTypeComboBox.setValue("Tutti i tipi");
        
        userNameLabel.setText("Mario Rossi");
    }

    public void handleSearch() {
        // Implementa la logica di ricerca filtrata
        String categoria = categoryComboBox.getValue();
        String frequenza = frequencyComboBox.getValue();
        String tipoLezione = lessonTypeComboBox.getValue();
        
        // Per ora ricarica semplicemente tutti i corsi
        loadEnrolledCourses();
    }

    public void viewCoursesClick() {
        // Metodo per visualizzare i corsi (se necessario)
        loadEnrolledCourses();
    }

    public void loadEnrolledCourses() {
        allCourseCards.clear();
        try {
            // Dati di esempio per corsi iscritti
            String[] courseTitles = {
                "Corso di Pasta Fresca",
                "Risotti e Cereali", 
                "Dolci Tradizionali",
                "Cucina Mediterranea",
                "Pizza Napoletana",
                "Cucina Vegana Italiana",
                "Antipasti Creativi",
                "Secondi di Pesce"
            };
            
            String[] descriptions = {
                "Impara l'arte della pasta fresca fatta in casa con tecniche tradizionali.",
                "Scopri i segreti dei risotti cremosi e dei cereali della tradizione italiana.",
                "Dolci della nonna: ricette autentiche tramandate di generazione in generazione.",
                "Un viaggio nella cucina mediterranea con ingredienti freschi e genuini.",
                "La vera pizza napoletana: dall'impasto alla cottura nel forno a legna.",
                "Cucina plant-based italiana: sapori autentici senza ingredienti animali.",
                "Antipasti creativi per stupire i tuoi ospiti con presentazioni uniche.",
                "Pesce fresco del Mediterraneo: tecniche di cottura e preparazione."
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
                "Chef Alessandro Rosa"
            };

            // Array con anni di esperienza variabili
            String[] experienceYears = {
                "15", "12", "8", "20", "10", "18", "6", "25"
            };
            
            for (int i = 0; i < courseTitles.length; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardcorso.fxml"));
                Node card = loader.load();
                CardCorsoBoundary boundary = loader.getController();
                
                // Configura la card per la modalità iscritto
                boundary.setEnrolledMode(true);
                
                String title = courseTitles[i];
                String description = descriptions[i];
                String startDate = "15/0" + ((i % 9) + 1) + "/2025";
                String endDate = "30/" + String.format("%02d", ((i % 12) + 1)) + "/2025";
                String frequency = (i % 2 == 0) ? "2 volte a settimana" : "1 volta a settimana";
                String price = "€" + (100 + i * 15) + ",00";
                String chefName = chefNames[i % chefNames.length];
                String experience = experienceYears[i % experienceYears.length];
                
                
                boundary.setCourseData(title, description, startDate, endDate, frequency, price, chefName, experience);
                
                String imagePath = "/immagini/corsi/esempio.png";
                boundary.setCourseImage(imagePath);
                
                allCourseCards.add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateCourseCards();
        updateTotalCoursesLabel();
    }

    private void updateCourseCards() {
        mainContentArea.getChildren().clear();
        int start = currentPage * CARDS_PER_PAGE;
        int end = Math.min(start + CARDS_PER_PAGE, allCourseCards.size());
        for (int i = start; i < end; i++) {
            mainContentArea.getChildren().add(allCourseCards.get(i));
        }
        int totalPages = (int) Math.ceil((double) allCourseCards.size() / CARDS_PER_PAGE);
        
        // RIMOSSO IL LABEL DELLA PAGINA COME RICHIESTO
        // pageLabel.setText("Pagina " + (currentPage + 1) + " di " + Math.max(1, totalPages));
        
        // Aggiorna i pulsanti di navigazione
        if (prevButton != null) {
            prevButton.setDisable(currentPage == 0);
        }
        if (nextButton != null) {
            nextButton.setDisable((currentPage + 1) * CARDS_PER_PAGE >= allCourseCards.size());
        }
    }

    private void updateTotalCoursesLabel() {
        if (totalCoursesLabel != null) {
            totalCoursesLabel.setText("Totale corsi iscritti: " + allCourseCards.size());
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

    public void goToHomepage() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/homepageutente.fxml", "UninaFoodLab - Homepage");
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
}