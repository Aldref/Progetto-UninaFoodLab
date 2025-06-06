package com.progetto.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Duration;


public class EnrolledCoursesController {

    @FXML private Label userNameLabel;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> frequencyComboBox;
    @FXML private ComboBox<String> lessonTypeComboBox;
    @FXML private Button searchBtn;
    @FXML private FlowPane enrolledCoursesArea;
    @FXML private ScrollPane enrolledCoursesScrollPane;
    @FXML private Label enrolledCountLabel;
    @FXML private Button prevPageBtn;
    @FXML private Button nextPageBtn;
    @FXML private Label pageLabel;

    private List<Node> allCards = new ArrayList<>();
    private int currentPage = 0;
    private final int CARDS_PER_PAGE = 12;

    @FXML
    public void initialize() {
        categoryComboBox.getItems().clear();
        frequencyComboBox.getItems().clear();
        lessonTypeComboBox.getItems().clear();

        searchBtn.setOnAction(e -> handleSearch());
        
        loadEnrolledCourses();

    }

    private void loadEnrolledCourses() {
        allCards.clear();
        try {
            for (int i = 1; i <= 5; i++) { 
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardcorso.fxml"));
                Node card = loader.load();
                CardCorsoController controller = loader.getController();
                controller.setAcquistato(true);        
                controller.setEnrolledPage(true);      
                allCards.add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateCards();
    }

    private void updateCards() {
        enrolledCoursesArea.getChildren().clear();
        int start = currentPage * CARDS_PER_PAGE;
        int end = Math.min(start + CARDS_PER_PAGE, allCards.size());
        for (int i = start; i < end; i++) {
            enrolledCoursesArea.getChildren().add(allCards.get(i));
        }
        pageLabel.setText("Pagina " + (currentPage + 1) + " di " + Math.max(1, (int)Math.ceil((double)allCards.size()/CARDS_PER_PAGE)));
        enrolledCountLabel.setText("Sei iscritto a " + allCards.size() + " corsi");
        prevPageBtn.setDisable(currentPage == 0);
        nextPageBtn.setDisable((currentPage + 1) * CARDS_PER_PAGE >= allCards.size());
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
        String categoria = categoryComboBox.getValue();
        String frequenza = frequencyComboBox.getValue();
        String tipoLezione = lessonTypeComboBox.getValue();

        System.out.println("Categoria: " + categoria);
        System.out.println("Frequenza: " + frequenza);
        System.out.println("Tipo Lezione: " + tipoLezione);

        // TODO: Qui inserisci la logica per filtrare i corsi iscritti dal database
        // Esempio: aggiorna allCards con i risultati filtrati e poi chiama updateCards()
        currentPage = 0;
        updateCards();
    }

    @FXML
    private void viewCoursesClick() {
        try {
            Stage stage = (Stage) enrolledCoursesArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/homepageutente.fxml", "UninaFoodLab - Homepage");
        } catch (IOException e) {
            System.err.println("Errore nel cambio pagina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAccountManagement() {
        try {
            Stage stage = (Stage) enrolledCoursesArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/accountmanagement.fxml", "UninaFoodLab - Gestione Account");
        } catch (IOException e) {
            System.err.println("Errore nel cambio pagina: " + e.getMessage());
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
                Stage stage = (Stage) enrolledCoursesArea.getScene().getWindow();
                SceneSwitcher.switchToLogin(stage, "/fxml/loginpage.fxml", "UninaFoodLab - Login");
            }
        } catch (Exception e) {
            System.err.println("Errore durante il logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
}