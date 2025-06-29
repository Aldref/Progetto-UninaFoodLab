package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import java.io.File;
import com.progetto.controller.HomepageUtenteController;
import com.progetto.Entity.EntityDto.UtenteVisitatore;


public class HomepageUtenteBoundary {

    @FXML private FlowPane mainContentArea;
    @FXML private Label pageLabel;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> frequencyComboBox;
    @FXML private Label userNameLabel;
    @FXML private javafx.scene.image.ImageView userProfileImage;
    @FXML private javafx.scene.control.ProgressIndicator loadingIndicator;

    private HomepageUtenteController controller;

    @FXML
    public void initialize() {
        // Imposta nome e immagine utente nella navbar
        UtenteVisitatore utente = UtenteVisitatore.loggedUser;
        if (utente != null) {
            userNameLabel.setText(utente.getNome() + " " + utente.getCognome());
            String propic = utente.getUrl_Propic();
            if (propic != null && !propic.isEmpty()) {
                File imgFile = new File("src/main/resources/" + propic);
                if (imgFile.exists()) {
                    javafx.scene.image.Image img = new javafx.scene.image.Image(imgFile.toURI().toString(), 80, 80, true, true);
                    userProfileImage.setImage(img);
                    // Clip circolare come in AccountManagement
                    com.progetto.controller.AccountManagementController.setCircularClip(userProfileImage, 40);
                }
            }
        }
        controller = new HomepageUtenteController(mainContentArea, pageLabel, categoryComboBox, frequencyComboBox, userNameLabel, this);
        initializeSearchFiltersFromDb();
        controller.loadCourses();
    }
    // Mostra lo spinner di caricamento
    public void showLoadingIndicator() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        }
    }

    // Nasconde lo spinner di caricamento
    public void hideLoadingIndicator() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(false);
        }
    }

    private void initializeSearchFiltersFromDb() {
        // Popola i ComboBox usando BarraDiRicercaDao
        com.progetto.Entity.entityDao.BarraDiRicercaDao dao = new com.progetto.Entity.entityDao.BarraDiRicercaDao();
        categoryComboBox.getItems().clear();
        frequencyComboBox.getItems().clear();
        // Categoria
        categoryComboBox.getItems().add("Tutte le categorie");
        for (String cat : dao.Categorie()) {
            categoryComboBox.getItems().add(cat);
        }
        categoryComboBox.setValue("Tutte le categorie");
        // Frequenza
        frequencyComboBox.getItems().add("Tutte le frequenze");
        for (String freq : dao.CeraEnumFrequenza()) {
            frequencyComboBox.getItems().add(freq);
        }
        frequencyComboBox.setValue("Tutte le frequenze");
    }

    @FXML
    private void goToEnrolledCourses(ActionEvent event) {
        controller.goToEnrolledCourses();
    }

    @FXML
    private void goToUserCards(ActionEvent event) {
        controller.goToUserCards();
    }

    @FXML
    private void goToAccountManagement(ActionEvent event) {
        controller.goToAccountManagement();
    }

    @FXML
    private void LogoutClick(ActionEvent event) {
        controller.LogoutClick();
    }

    @FXML
    private void nextPage(ActionEvent event) {
        controller.nextPage();
    }

    @FXML
    private void prevPage(ActionEvent event) {
        controller.prevPage();
    }

    @FXML
    private void searchCourses(ActionEvent event) {
        controller.resetPageAndSearch();
    }

    
    public FlowPane getMainContentArea() { return mainContentArea; }
    public Label getPageLabel() { return pageLabel; }
    public ComboBox<String> getCategoryComboBox() { return categoryComboBox; }
    public ComboBox<String> getFrequencyComboBox() { return frequencyComboBox; }
    public Label getUserNameLabel() { return userNameLabel; }
    public void setLoadingIndicatorVisible(boolean visible) {
        if (loadingIndicator != null) loadingIndicator.setVisible(visible);
    }
}