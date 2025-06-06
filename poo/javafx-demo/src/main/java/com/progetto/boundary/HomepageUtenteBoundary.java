package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import com.progetto.controller.HomepageUtenteController;

import java.util.List;

public class HomepageUtenteBoundary {

    @FXML
    private ComboBox<String> cbCategoria;
    @FXML
    private ComboBox<String> cbFrequenza;
    @FXML
    private ComboBox<String> cbTipoLezione;
    @FXML
    private FlowPane mainContentArea;
    @FXML
    private Label pageLabel;

    private HomepageUtenteController controller;

    public HomepageUtenteBoundary() {}

    @FXML
    public void initialize() {
        controller = new HomepageUtenteController(mainContentArea, pageLabel, cbCategoria, cbFrequenza, cbTipoLezione);
        controller.loadCards();
    }

    @FXML
    private void goToEnrolledCourses(ActionEvent event) {
        controller.goToEnrolledCourses();
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
    private void handleSearch(ActionEvent event) {
        controller.handleSearch();
    }
}