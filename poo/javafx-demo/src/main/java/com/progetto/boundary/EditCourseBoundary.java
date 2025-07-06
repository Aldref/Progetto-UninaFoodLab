// ...existing code...
package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import com.progetto.controller.EditCourseController;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;

public class EditCourseBoundary implements Initializable {
    
    // Header
    @FXML private Button backButton;
    @FXML private TextField courseNameField;
    
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
    // Online section (per corsi telematici)
    @FXML private VBox onlineSection;
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
    private int courseId = -1;

    /**
     * Metodo da chiamare PRIMA di mostrare la boundary, per passare l'ID del corso da modificare.
     */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
        System.out.println("[DEBUG] EditCourseBoundary.setCourseId: courseId = " + courseId);
        if (controller != null) {
            controller.setCourseId(courseId);
            controller.initialize(); // Inizializza SOLO dopo aver settato l'id
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inizializza il controller passando tutti i componenti UI nel giusto ordine
        controller = new EditCourseController(
            // Basic fields
            courseNameField, descriptionArea, startDatePicker, endDatePicker,
            courseTypeCombo, frequencyCombo, maxPersonsSpinner,
            // Sections (tutte VBox)
            locationSection, recipesSection, recipesContainer, onlineSection,
            // Time fields
            startHourSpinner, startMinuteSpinner, durationSpinner,
            // Location fields
            streetField, capField,
            // Error labels
            descriptionErrorLabel, maxPersonsErrorLabel, startDateErrorLabel,
            endDateErrorLabel, startTimeErrorLabel, durationErrorLabel,
            daysErrorLabel, streetErrorLabel, capErrorLabel, frequencyErrorLabel,
            // Buttons
            saveButton
        );
        // Disabilita il campo nome corso (grigio, non modificabile)
        courseNameField.setDisable(true);
        courseNameField.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #888;");
        // NON chiamare più qui controller.initialize();
        // L'inizializzazione avverrà solo dopo che l'id sarà stato passato con setCourseId
    }
        // --- UI section and field helpers for controller ---
    // RIMOSSO: la gestione dei dayCheckBoxes va fatta solo nel controller

    public void setupUI() {
        setupSpinners();
        setupComboBoxes();
        setupDatePickers();
        setupFieldValidators();
        setupFrequencyListener();
    }

    public void setupSpinners() {
        maxPersonsSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));
        startHourSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 23, 18));
        startHourSpinner.setPrefWidth(100);
        setupTimeSpinnerFormatter(startHourSpinner, false);
        startMinuteSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 15));
        startMinuteSpinner.setPrefWidth(100);
        setupTimeSpinnerFormatter(startMinuteSpinner, true);
        SpinnerValueFactory<Double> durationFactory =
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 8.0, 2.0, 0.5);
        durationSpinner.setValueFactory(durationFactory);
        durationSpinner.getValueFactory().setConverter(new javafx.util.StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                if (object == null) return "";
                return String.format("%.1f", object);
            }
            @Override
            public Double fromString(String string) {
                try { return Double.parseDouble(string); } catch (NumberFormatException e) { return 0.0; }
            }
        });
    }

    private void setupTimeSpinnerFormatter(Spinner<Integer> spinner, boolean isMinute) {
        spinner.setEditable(true);
        spinner.getValueFactory().setConverter(new javafx.util.StringConverter<Integer>() {
            @Override
            public String toString(Integer value) {
                return value == null ? "00" : String.format("%02d", value);
            }
            @Override
            public Integer fromString(String string) {
                try {
                    String cleaned = string.trim().replaceAll("[^\\d]", "");
                    if (cleaned.length() > 2) cleaned = cleaned.substring(0, 2);
                    if (cleaned.isEmpty()) return spinner.getValue();
                    int value = Integer.parseInt(cleaned);
                    return isMinute ? Math.max(0, Math.min(59, value)) : Math.max(6, Math.min(23, value));
                } catch (NumberFormatException e) { return spinner.getValue(); }
            }
        });
        TextField editor = spinner.getEditor();
        setupSpinnerEditor(editor, spinner, isMinute);
    }

    private void setupSpinnerEditor(TextField editor, Spinner<Integer> spinner, boolean isMinute) {
        editor.textProperty().addListener((obs, oldValue, newValue) -> {
            String filtered = newValue.replaceAll("[^\\d]", "");
            if (filtered.length() > 2) filtered = filtered.substring(0, 2);
            if (!filtered.equals(newValue)) editor.setText(filtered);
            if (!filtered.isEmpty()) {
                try {
                    int value = Integer.parseInt(filtered);
                    boolean isValid = isMinute ? (value <= 59) : (value >= 6 && value <= 23);
                    editor.getStyleClass().removeAll("spinner-error");
                    if (!isValid) editor.getStyleClass().add("spinner-error");
                } catch (NumberFormatException e) { editor.getStyleClass().add("spinner-error"); }
            } else {
                editor.getStyleClass().remove("spinner-error");
            }
        });
        editor.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                validateAndUpdateSpinner(editor, spinner, isMinute);
                editor.getStyleClass().remove("spinner-error");
            }
        });
        editor.setOnAction(e -> {
            validateAndUpdateSpinner(editor, spinner, isMinute);
            spinner.getParent().requestFocus();
        });
    }

    private void validateAndUpdateSpinner(TextField editor, Spinner<Integer> spinner, boolean isMinute) {
        String text = editor.getText().trim();
        if (text.isEmpty()) {
            editor.setText(String.format("%02d", spinner.getValue()));
        } else {
            try {
                int value = Integer.parseInt(text);
                value = isMinute ? Math.max(0, Math.min(59, value)) : Math.max(6, Math.min(23, value));
                spinner.getValueFactory().setValue(value);
            } catch (NumberFormatException e) {
                editor.setText(String.format("%02d", spinner.getValue()));
            }
        }
    }

    public void setupComboBoxes() {
        javafx.collections.ObservableList<String> courseTypes = javafx.collections.FXCollections.observableArrayList(
            "In presenza", "Telematica"
        );
        courseTypeCombo.setItems(courseTypes);
        courseTypeCombo.setValue("In presenza");
        javafx.collections.ObservableList<String> frequencies = javafx.collections.FXCollections.observableArrayList(
            "1 volta a settimana",
            "2 volte a settimana",
            "3 volte a settimana",
            "Tutti i giorni (Lun-Ven)",
            "Tutti i giorni (Lun-Dom)"
        );
        frequencyCombo.setItems(frequencies);
        frequencyCombo.setValue("2 volte a settimana");
    }

    public void setupDatePickers() {
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(java.time.LocalDate.now()));
            }
        });
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                java.time.LocalDate startDate = startDatePicker.getValue();
                setDisable(empty || (startDate != null && date.isBefore(startDate.plusDays(1))));
            }
        });
    }

    public void setupFieldValidators() {
        addTextValidator(capField, "[^\\d]", 5);
    }

    private void addTextValidator(TextField field, String regex, Integer maxLength) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            String filtered = newVal.replaceAll(regex, "");
            if (maxLength != null && filtered.length() > maxLength) {
                filtered = filtered.substring(0, maxLength);
            }
            field.setText(filtered);
        });
    }

    public void setupFrequencyListener() {
        // La logica di aggiornamento dei checkbox dei giorni va gestita dal controller
        frequencyCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            // gestito dal controller
        });
    }

    public void setupChangeListenersForSave(java.util.function.Supplier<Boolean> hasAnyFieldChanged) {
        // Implement binding logic if needed, or expose for controller
    }

    public void setupValidation() {
        // Implement validation logic if needed, or expose for controller
    }

    public void setupFrequencyRestrictionForHybrid() {
        frequencyCombo.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (!empty && item != null) {
                    String type = courseTypeCombo.getValue();
                    boolean isHybrid = "Entrambi".equals(type);
                    boolean isPresence = "In presenza".equals(type);
                    setDisable((isHybrid || isPresence) && "1 volta a settimana".equals(item));
                } else {
                    setDisable(false);
                }
            }
        });
        frequencyCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            String type = courseTypeCombo.getValue();
            if (("Entrambi".equals(type) || "In presenza".equals(type)) && "1 volta a settimana".equals(newVal)) {
                for (String freq : frequencyCombo.getItems()) {
                    if (!"1 volta a settimana".equals(freq)) {
                        frequencyCombo.setValue(freq);
                        break;
                    }
                }
            }
        });
    }

    // RIMOSSO: la gestione dei checkbox va fatta dal controller

    public void addEndDateListener(javafx.util.Callback<java.time.LocalDate, Void> listener) {
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            listener.call(newVal);
        });
    }

    public void setStartDateDisabled(boolean disabled) { startDatePicker.setDisable(disabled); }
    public void setEndDateDisabled(boolean disabled) { endDatePicker.setDisable(disabled); }

    public int getStartHour() { return startHourSpinner.getValue(); }
    public int getStartMinute() { return startMinuteSpinner.getValue(); }
    public double getDuration() { return durationSpinner.getValue(); }

    public List<String> getSelectedDays() {
        List<String> days = new ArrayList<>();
        if (mondayCheckBox.isSelected()) days.add("Lunedì");
        if (tuesdayCheckBox.isSelected()) days.add("Martedì");
        if (wednesdayCheckBox.isSelected()) days.add("Mercoledì");
        if (thursdayCheckBox.isSelected()) days.add("Giovedì");
        if (fridayCheckBox.isSelected()) days.add("Venerdì");
        if (saturdayCheckBox.isSelected()) days.add("Sabato");
        if (sundayCheckBox.isSelected()) days.add("Domenica");
        return days;
    }

    public void showError(String field, String message) {
        Label label = null;
        switch (field) {
            case "description": label = descriptionErrorLabel; break;
            case "maxPersons": label = maxPersonsErrorLabel; break;
            case "startDate": label = startDateErrorLabel; break;
            case "endDate": label = endDateErrorLabel; break;
            case "startTime": label = startTimeErrorLabel; break;
            case "duration": label = durationErrorLabel; break;
            case "days": label = daysErrorLabel; break;
            case "street": label = streetErrorLabel; break;
            case "cap": label = capErrorLabel; break;
            case "frequency": label = frequencyErrorLabel; break;
        }
        if (label != null) {
            label.setText(message);
            label.setVisible(true);
        }
    }
    // --- UI section and field helpers for controller ---
    public void setCourseTypeDisabled(boolean disabled) { courseTypeCombo.setDisable(disabled); }
    public void setFrequencyDisabled(boolean disabled) { frequencyCombo.setDisable(disabled); }
    public void setLocationSectionVisible(boolean visible) {
        locationSection.setVisible(visible);
        locationSection.setManaged(visible);
    }
    public void setRecipesSectionVisible(boolean visible) {
        recipesSection.setVisible(visible);
        recipesSection.setManaged(visible);
    }
    public void setStreetDisabled(boolean disabled) { streetField.setDisable(disabled); }
    public void setCapDisabled(boolean disabled) { capField.setDisable(disabled); }
    public void clearRecipesContainer() { recipesContainer.getChildren().clear(); }
    public boolean isRecipesContainerEmpty() { return recipesContainer.getChildren().isEmpty(); }
    // === GETTER/SETTER per il controller ===
    public String getDescription() {
        return descriptionArea.getText();
    }

    public void setDescription(String text) {
        descriptionArea.setText(text);
    }

    public String getCourseType() {
        return courseTypeCombo.getValue();
    }

    public void setCourseType(String type) {
        courseTypeCombo.setValue(type);
    }

    public String getFrequency() {
        return frequencyCombo.getValue();
    }

    public void setFrequency(String freq) {
        frequencyCombo.setValue(freq);
    }

    public int getMaxPersons() {
        return maxPersonsSpinner.getValue();
    }

    public void setMaxPersons(int value) {
        maxPersonsSpinner.getValueFactory().setValue(value);
    }

    public java.time.LocalDate getStartDate() {
        return startDatePicker.getValue();
    }

    public void setStartDate(java.time.LocalDate date) {
        startDatePicker.setValue(date);
    }

    public java.time.LocalDate getEndDate() {
        return endDatePicker.getValue();
    }

    public void setEndDate(java.time.LocalDate date) {
        endDatePicker.setValue(date);
    }

    public void setCourseName(String name) {
        courseNameField.setText(name);
        courseNameField.setDisable(true);
        courseNameField.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #888;");
    }

    public String getStreet() {
        return streetField.getText();
    }

    public void setStreet(String value) {
        streetField.setText(value);
    }

    public String getCap() {
        return capField.getText();
    }

    public void setCap(String value) {
        capField.setText(value);
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