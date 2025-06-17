// filepath: /mnt/c/Users/mario/Desktop/prova/Progetto-UninaFoodLab/poo/javafx-demo/src/main/java/com/progetto/boundary/CreateCourseBoundary.java
package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import com.progetto.controller.CreateCourseController;

public class CreateCourseBoundary {

    @FXML
    private TextField courseNameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> frequencyComboBox;
    @FXML
    private ComboBox<String> lessonTypeComboBox;
    @FXML
    private Spinner<Integer> maxParticipantsSpinner;
    @FXML
    private TextField priceField;
    @FXML
    private ImageView courseImageView;
    @FXML
    private Label chefNameLabel;
    @FXML
    private Button createButton;

    
    @FXML
    private VBox presenceDetailsSection;
    @FXML
    private VBox onlineDetailsSection;
    
    
    @FXML
    private ListView<String> dayOfWeekListView;
    @FXML
    private TextField timeField;
    @FXML
    private TextField durationField; 
    @FXML
    private TextField cityField;
    @FXML
    private TextField streetField;
    @FXML
    private TextField capField;
    @FXML
    private VBox recipesContainer;
    
    
    @FXML
    private ComboBox<String> applicationComboBox;
    @FXML
    private TextField meetingCodeField;
    @FXML
    private ListView<String> onlineDayOfWeekListView;
    @FXML
    private TextField onlineTimeField;
    @FXML
    private TextField onlineDurationField; 

    private CreateCourseController controller;

    @FXML
    public void initialize() {
        controller = new CreateCourseController(
            courseNameField, descriptionArea, startDatePicker, endDatePicker,
            frequencyComboBox, lessonTypeComboBox, maxParticipantsSpinner,
            priceField, courseImageView, chefNameLabel, createButton,
            presenceDetailsSection, onlineDetailsSection, dayOfWeekListView, 
            timeField, durationField, cityField, streetField, capField,
            recipesContainer, applicationComboBox, meetingCodeField, 
            onlineDayOfWeekListView, onlineTimeField, onlineDurationField 
        );
        controller.initialize();
    }

    @FXML
    private void onLessonTypeChanged(ActionEvent event) {
        controller.onLessonTypeChanged();
    }

    @FXML
    private void selectImage(ActionEvent event) {
        controller.selectImage();
    }

    @FXML
    private void createCourse(ActionEvent event) {
        controller.createCourse();
    }

    @FXML
    private void cancelCreation(ActionEvent event) {
        controller.goToHomepage();
    }

    @FXML
    private void goToHomepage(ActionEvent event) {
        controller.goToHomepage();
    }

    @FXML
    private void goToCreateCourse(ActionEvent event) {
        // Già nella pagina di creazione corso
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
    private void addRecipe(ActionEvent event) {
        controller.addNewRecipe();
    }
}