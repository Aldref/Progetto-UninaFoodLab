package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.util.ResourceBundle;
import com.progetto.controller.EnrolledCoursesController;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.utils.ImageClipUtils;


public class EnrolledCoursesBoundary implements Initializable {

    @FXML private Label userNameLabel;
    @FXML private javafx.scene.image.ImageView userProfileImage;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> frequencyComboBox;
    // Rimossa lessonTypeComboBox
    @FXML private Button searchBtn;
    @FXML private FlowPane enrolledCoursesArea;
    @FXML private ScrollPane enrolledCoursesScrollPane;
    @FXML private Label enrolledCountLabel;
    @FXML private Button prevPageBtn;
    @FXML private Button nextPageBtn;
    @FXML private Label pageLabel;
    @FXML private javafx.scene.control.ProgressIndicator loadingIndicator;

    private EnrolledCoursesController controller;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Imposta nome e immagine utente nella navbar
        UtenteVisitatore utente = UtenteVisitatore.loggedUser;
        if (utente != null) {
            userNameLabel.setText(utente.getNome() + " " + utente.getCognome());
            String propic = utente.getUrl_Propic();
            if (propic != null && !propic.isEmpty()) {
                java.io.File imgFile = new java.io.File("src/main/resources/" + propic);
                if (imgFile.exists()) {
                    javafx.scene.image.Image img = new javafx.scene.image.Image(imgFile.toURI().toString(), 80, 80, true, true);
                    userProfileImage.setImage(img);
                    ImageClipUtils.setCircularClip(userProfileImage, 40);
                }
            }
        }
        controller = new EnrolledCoursesController(
            userNameLabel, categoryComboBox, frequencyComboBox,
            searchBtn, enrolledCoursesArea, enrolledCoursesScrollPane,
            pageLabel, prevPageBtn, nextPageBtn, enrolledCountLabel, this
        );
        controller.initialize();
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