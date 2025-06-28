package com.progetto.controller;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
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
        // Il nome utente viene impostato dalla boundary
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
            com.progetto.Entity.entityDao.CorsoDao corsoDao = new com.progetto.Entity.entityDao.CorsoDao();
            com.progetto.Entity.entityDao.UtenteVisitatoreDao utenteDao = new com.progetto.Entity.entityDao.UtenteVisitatoreDao();
            com.progetto.Entity.EntityDto.UtenteVisitatore utente = com.progetto.Entity.EntityDto.UtenteVisitatore.loggedUser;
            // Recupera tutti i corsi
            ArrayList<com.progetto.Entity.EntityDto.Corso> corsi = corsoDao.recuperaCorsi();
            // Recupera i corsi a cui l'utente è iscritto
            utenteDao.RecuperaCorsi(utente);
            List<com.progetto.Entity.EntityDto.Corso> corsiIscritti = utente.getCorsi();
            HashSet<Integer> idCorsiIscritti = new HashSet<>();
            if (corsiIscritti != null) {
                for (com.progetto.Entity.EntityDto.Corso c : corsiIscritti) {
                    idCorsiIscritti.add(c.getId_Corso());
                }
            }
            // Filtra i corsi: mostra solo quelli a cui NON è iscritto
            for (com.progetto.Entity.EntityDto.Corso corso : corsi) {
                if (idCorsiIscritti.contains(corso.getId_Corso())) continue;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardcorso.fxml"));
                Node card = loader.load();
                CardCorsoBoundary boundary = loader.getController();
                // FIX: Associa sempre il corso alla card
                boundary.setCorso(corso);

                String title = corso.getNome();
                String description = corso.getDescrizione();
                String startDate = corso.getDataInizio() != null ? corso.getDataInizio().toString() : "";
                String endDate = corso.getDataFine() != null ? corso.getDataFine().toString() : "";
                String frequency = corso.getFrequenzaDelleSessioni();
                String price = "€" + String.format("%.2f", corso.getPrezzo());
                String chefName = corso.getChefNome() + (corso.getChefCognome().isEmpty() ? "" : (" " + corso.getChefCognome()));
                String experience = corso.getChefEsperienza() > 0 ? String.valueOf(corso.getChefEsperienza()) : "";
                String maxPeople = String.valueOf(corso.getMaxPersone());

                boundary.setCourseData(title, description, startDate, endDate, frequency, price, chefName, experience, maxPeople);

                // Tipi di cucina dal corso
                List<String> tipiCucina = corso.getTipiDiCucina();
                if (tipiCucina.size() > 0) {
                    String cucina1 = tipiCucina.get(0);
                    String cucina2 = tipiCucina.size() > 1 ? tipiCucina.get(1) : "";
                    boundary.setCuisineTypes(cucina1, cucina2);
                }

                // Immagine del corso
                String imagePath = corso.getUrl_Propic() != null && !corso.getUrl_Propic().isEmpty() ? corso.getUrl_Propic() : "/immagini/corsi/esempio.png";
                boundary.setCourseImage(imagePath);

                allCourseCards.add(card);
            }
        } catch (Exception e) {
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