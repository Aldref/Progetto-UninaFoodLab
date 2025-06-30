package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.FlowPane;
import javafx.event.ActionEvent;
import com.progetto.controller.HomepageChefController;
import com.progetto.utils.ImageClipUtils;
import java.io.File;

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
    private javafx.scene.image.ImageView chefProfileImage;

    private HomepageChefController controller;

    public HomepageChefBoundary() {}

    @FXML
    public void initialize() {
        controller = new HomepageChefController(
            mainContentArea, 
            pageLabel, 
            categoryComboBox,
            frequencyComboBox,
            chefNameLabel,
            chefProfileImage
        );
        // Imposta nome e immagine chef nella navbar
        com.progetto.Entity.EntityDto.Chef chef = com.progetto.Entity.EntityDto.Chef.loggedUser;
        if (chef != null) {
            chefNameLabel.setText(chef.getNome() + " " + chef.getCognome());
            String propic = chef.getUrl_Propic();
            if (propic != null && !propic.isEmpty()) {
                File imgFile = new File("src/main/resources/" + propic);
                if (imgFile.exists()) {
                    javafx.scene.image.Image img = new javafx.scene.image.Image(imgFile.toURI().toString(), 256, 256, true, true);
                    chefProfileImage.setImage(img);
                    ImageClipUtils.setCircularClip(chefProfileImage, 0); // raggio automatico
                }
            }
        }
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