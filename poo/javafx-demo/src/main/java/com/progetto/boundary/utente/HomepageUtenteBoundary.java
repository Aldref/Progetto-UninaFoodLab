package com.progetto.boundary.utente;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.scene.control.ProgressIndicator;
import java.io.File;

import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.utils.ImageClipUtils;
import com.progetto.Entity.entityDao.BarraDiRicercaDao;
import com.progetto.controller.utente.HomepageUtenteController;

public class HomepageUtenteBoundary {

    @FXML private FlowPane mainContentArea;
    @FXML private Label pageLabel;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> frequencyComboBox;
    @FXML private Label userNameLabel;
    @FXML private ImageView userProfileImage;
    @FXML private ProgressIndicator loadingIndicator;

    private HomepageUtenteController controller;
    private BarraDiRicercaDao dao = new BarraDiRicercaDao();

    @FXML
    public void initialize() {
        setupUserProfile();
        controller = new HomepageUtenteController(mainContentArea, pageLabel, categoryComboBox, frequencyComboBox, userNameLabel, this);
        initializeSearchFiltersFromDb();
        controller.loadCourses();
    }

    public void showLoadingIndicator() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        }
    }

    public void hideLoadingIndicator() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(false);
        }
    }

    private void initializeSearchFiltersFromDb() {
        BarraDiRicercaDao dao = new BarraDiRicercaDao();
        categoryComboBox.getItems().clear();
        frequencyComboBox.getItems().clear();
        categoryComboBox.getItems().add("Tutte le categorie");
        for (String cat : dao.Categorie()) {
            categoryComboBox.getItems().add(cat);
        }
        categoryComboBox.setValue("Tutte le categorie");
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

    public void setupUserProfile() {
        UtenteVisitatore utente = UtenteVisitatore.loggedUser;
        if (utente != null) {
            userNameLabel.setText(utente.getNome() + " " + utente.getCognome());
            String propic = utente.getUrl_Propic();
            if (propic != null && !propic.isEmpty()) {
                File imgFile = new File("src/main/resources/" + propic);
                if (imgFile.exists()) {
                    javafx.scene.image.Image img = new javafx.scene.image.Image(imgFile.toURI().toString(), 80, 80, true, true);
                    userProfileImage.setImage(img);
                    ImageClipUtils.setCircularClip(userProfileImage, 40);
                }
            }
        }
    }
}