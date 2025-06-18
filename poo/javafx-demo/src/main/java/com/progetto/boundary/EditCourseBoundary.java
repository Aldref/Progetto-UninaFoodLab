package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import com.progetto.controller.EditCourseController;

import java.net.URL;
import java.util.ResourceBundle;

public class EditCourseBoundary implements Initializable {
    
    // Header
    @FXML private Button backButton;
    @FXML private Label courseNameLabel;
    
    // Informazioni generali
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> courseTypeCombo;
    @FXML private ComboBox<String> frequencyCombo;
    @FXML private Spinner<Integer> maxPersonsSpinner;
    
    // Date e orari
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Spinner<Integer> startHourSpinner;
    @FXML private Spinner<Integer> startMinuteSpinner;
    @FXML private Spinner<Double> durationSpinner;
    
    // Giorni della settimana
    @FXML private CheckBox mondayCheckBox;
    @FXML private CheckBox tuesdayCheckBox;
    @FXML private CheckBox wednesdayCheckBox;
    @FXML private CheckBox thursdayCheckBox;
    @FXML private CheckBox fridayCheckBox;
    @FXML private CheckBox saturdayCheckBox;
    @FXML private CheckBox sundayCheckBox;
    
    // Location (per corsi in presenza)
    @FXML private VBox locationSection;
    @FXML private TextField streetField;
    @FXML private TextField capField;
    
    // Ricette (per corsi in presenza)
    @FXML private VBox recipesSection;
    @FXML private VBox recipesContainer;
    @FXML private Button addRecipeButton;
    
    // Error labels
    @FXML private Label descriptionErrorLabel;
    @FXML private Label maxPersonsErrorLabel;
    @FXML private Label startDateErrorLabel;
    @FXML private Label endDateErrorLabel;
    @FXML private Label startTimeErrorLabel;
    @FXML private Label durationErrorLabel;
    @FXML private Label daysErrorLabel;
    @FXML private Label streetErrorLabel;
    @FXML private Label capErrorLabel;
    @FXML private Label frequencyErrorLabel;
    
    // Buttons
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    
    // Controller
    private EditCourseController controller;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inizializza il controller passando tutti i componenti UI nel giusto ordine
        controller = new EditCourseController(
            // Basic fields
            courseNameLabel, descriptionArea, startDatePicker, endDatePicker,
            courseTypeCombo, frequencyCombo, maxPersonsSpinner,
            
            // Sections (tutte VBox)
            locationSection, recipesSection, recipesContainer,
            
            // Time fields
            startHourSpinner, startMinuteSpinner, durationSpinner,
            
            // Location fields
            streetField, capField,
            
            // Day checkboxes
            mondayCheckBox, tuesdayCheckBox, wednesdayCheckBox, thursdayCheckBox,
            fridayCheckBox, saturdayCheckBox, sundayCheckBox,
            
            // Error labels
            descriptionErrorLabel, maxPersonsErrorLabel, startDateErrorLabel,
            endDateErrorLabel, startTimeErrorLabel, durationErrorLabel,
            daysErrorLabel, streetErrorLabel, capErrorLabel, frequencyErrorLabel,
            
            // Buttons
            saveButton
        );
        
        // Inizializza il controller
        controller.initialize();
    }
    
    // === ACTION METHODS ===
    @FXML
    private void onCourseTypeChanged() {
        controller.onCourseTypeChanged();
    }
    
    @FXML
    private void onFrequencyChanged() {
        controller.onFrequencyChanged();
    }
    
    @FXML
    private void addRecipe() {
        controller.addNewRecipe();
    }
    
    @FXML
    private void saveCourse() {
        controller.saveCourse();
    }
    
    @FXML
    private void goBack() {
        controller.goBack();
    }
}