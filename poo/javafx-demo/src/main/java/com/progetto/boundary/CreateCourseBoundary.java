package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.event.ActionEvent;
import com.progetto.controller.CreateCourseController;

public class CreateCourseBoundary {

    @FXML
    private TextField courseNameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<String> cuisineTypeComboBox1;
    @FXML
    private ComboBox<String> cuisineTypeComboBox2;
    @FXML
    private Label cuisineTypeErrorLabel;
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
    private VBox hybridDetailsSection;
    
    @FXML
    private FlowPane dayOfWeekContainer;
    @FXML
    private Spinner<Integer> presenceHourSpinner;
    @FXML
    private Spinner<Integer> presenceMinuteSpinner;
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
    private FlowPane onlineDayOfWeekContainer;
    @FXML
    private Spinner<Integer> onlineHourSpinner;
    @FXML
    private Spinner<Integer> onlineMinuteSpinner;
    @FXML
    private TextField onlineDurationField; 
    @FXML
    private VBox hybridDaysContainer;
    @FXML
    private Label hybridErrorLabel;

    private CreateCourseController controller;

    @FXML
    public void initialize() {
        controller = new CreateCourseController(
            courseNameField, descriptionArea, startDatePicker, endDatePicker,
            frequencyComboBox, lessonTypeComboBox, maxParticipantsSpinner,
            priceField, courseImageView, chefNameLabel, createButton,
            presenceDetailsSection, onlineDetailsSection, dayOfWeekContainer, 
            presenceHourSpinner, presenceMinuteSpinner, durationField, 
            cityField, streetField, capField, recipesContainer, 
            applicationComboBox, meetingCodeField, onlineDayOfWeekContainer, 
            onlineHourSpinner, onlineMinuteSpinner, onlineDurationField,
            hybridDetailsSection, hybridDaysContainer, hybridErrorLabel,
            cuisineTypeComboBox1, cuisineTypeComboBox2, cuisineTypeErrorLabel
        );
        controller.initialize();
    }

    // ...resto dei metodi invariato...
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