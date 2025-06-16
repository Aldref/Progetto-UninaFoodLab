package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.FlowPane;
import javafx.event.ActionEvent;
import com.progetto.controller.HomepageChefController;

public class HomepageChefBoundary {

    @FXML
    private FlowPane mainContentArea;
    @FXML
    private Label pageLabel;
    @FXML
    private Label chefNameLabel;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private ComboBox<String> frequencyComboBox;
    @FXML
    private ComboBox<String> lessonTypeComboBox;

    private HomepageChefController controller;

    public HomepageChefBoundary() {}

    @FXML
    public void initialize() {
        controller = new HomepageChefController(
            mainContentArea, 
            pageLabel, 
            categoryComboBox,
            frequencyComboBox,
            lessonTypeComboBox,
            chefNameLabel
        );
        controller.loadChefCourses();
        controller.initializeSearchFilters();
    }

    @FXML
    private void searchCourses(ActionEvent event) {
        controller.searchCourses();
    }

    @FXML
    private void goToCreateCourse(ActionEvent event) {
        controller.goToCreateCourse();
    }

    @FXML
    private void goToMonthlyReport(ActionEvent event) {
        controller.goToMonthlyReport();
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
}