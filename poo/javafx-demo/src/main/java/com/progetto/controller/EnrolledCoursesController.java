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
        // Il nome utente viene impostato dalla boundary
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
            com.progetto.Entity.EntityDto.UtenteVisitatore utente = com.progetto.Entity.EntityDto.UtenteVisitatore.loggedUser;
            if (utente == null) {
                // Nessun utente loggato, non mostrare nulla
                updateCourseCards();
                updateTotalCoursesLabel();
                return;
            }
            // Recupera i corsi dal database
            utente.getUtenteVisitatoreDao().RecuperaCorsi(utente);
            List<com.progetto.Entity.EntityDto.Corso> corsi = utente.getCorsi();
            for (com.progetto.Entity.EntityDto.Corso corso : corsi) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardcorso.fxml"));
                Node card = loader.load();
                CardCorsoBoundary boundary = loader.getController();
                // FIX: Associa sempre il corso alla card
                boundary.setCorso(corso);
                boundary.setEnrolledMode(true);
                String title = corso.getNome();
                String description = corso.getDescrizione();
                String startDate = corso.getDataInizio() != null ? corso.getDataInizio().toString() : "";
                String endDate = corso.getDataFine() != null ? corso.getDataFine().toString() : "";
                String frequency = corso.getFrequenzaDelleSessioni();
                String price = "€" + String.format("%.2f", corso.getPrezzo());
                // Nome e cognome chef
                String chefName = corso.getChefNome();
                if (corso.getChefCognome() != null && !corso.getChefCognome().isEmpty()) {
                    chefName += " " + corso.getChefCognome();
                }
                String experience = corso.getChefEsperienza() > 0 ? String.valueOf(corso.getChefEsperienza()) : "";
                String maxPeople = String.valueOf(corso.getMaxPersone());
                boundary.setCourseData(title, description, startDate, endDate, frequency, price, chefName, experience, maxPeople);
                // Targhette tipo cucina
                List<String> tipiCucina = corso.getTipiDiCucina();
                String cucina1 = tipiCucina.size() > 0 ? tipiCucina.get(0) : "";
                String cucina2 = tipiCucina.size() > 1 ? tipiCucina.get(1) : "";
                boundary.setCuisineTypes(cucina1, cucina2);
                String imagePath = corso.getUrl_Propic() != null && !corso.getUrl_Propic().isEmpty() ? corso.getUrl_Propic() : "/immagini/corsi/esempio.png";
                boundary.setCourseImage(imagePath);
                allCourseCards.add(card);
            }
        } catch (Exception e) {
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