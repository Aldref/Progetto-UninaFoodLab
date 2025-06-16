package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.event.ActionEvent;
import com.progetto.controller.HomepageUtenteController;

public class HomepageUtenteBoundary {

    @FXML private FlowPane mainContentArea;
    @FXML private Label pageLabel;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> frequencyComboBox;
    @FXML private ComboBox<String> lessonTypeComboBox;
    @FXML private Label userNameLabel;

    private HomepageUtenteController controller;

    @FXML
    public void initialize() {
        // Aggiungi userNameLabel come parametro mancante
        controller = new HomepageUtenteController(mainContentArea, pageLabel, categoryComboBox, 
                                                frequencyComboBox, lessonTypeComboBox, userNameLabel);
        controller.initializeSearchFilters();
        controller.loadCourses(); // Cambiato da loadCards() a loadCourses()
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
        controller.searchCourses(); 
    }

    
    public FlowPane getMainContentArea() { return mainContentArea; }
    public Label getPageLabel() { return pageLabel; }
    public ComboBox<String> getCategoryComboBox() { return categoryComboBox; }
    public ComboBox<String> getFrequencyComboBox() { return frequencyComboBox; }
    public ComboBox<String> getLessonTypeComboBox() { return lessonTypeComboBox; }
    public Label getUserNameLabel() { return userNameLabel; }
}