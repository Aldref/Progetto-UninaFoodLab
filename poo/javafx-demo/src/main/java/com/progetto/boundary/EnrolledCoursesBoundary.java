package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import com.progetto.controller.EnrolledCoursesController;

import java.net.URL;
import java.util.ResourceBundle;

public class EnrolledCoursesBoundary implements Initializable {

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

    private EnrolledCoursesController controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = new EnrolledCoursesController(
            userNameLabel, categoryComboBox, frequencyComboBox, lessonTypeComboBox,
            searchBtn, enrolledCoursesArea, enrolledCoursesScrollPane,
            pageLabel, prevPageBtn, nextPageBtn, enrolledCountLabel
        );
        controller.initialize();
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
    private void handleSearch(ActionEvent event) {
        controller.handleSearch();
    }

    @FXML
    private void viewCoursesClick(ActionEvent event) {
        controller.viewCoursesClick();
    }

    // AGGIUNGI QUESTO METODO MANCANTE
    @FXML
    private void goToHomepage(ActionEvent event) {
        controller.goToHomepage();
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
}