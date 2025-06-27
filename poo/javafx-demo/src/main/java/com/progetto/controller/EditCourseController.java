package com.progetto.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

import com.progetto.utils.SuccessDialogUtils;
import com.progetto.utils.SceneSwitcher;
public class EditCourseController {
    
    // === CONSTANTS ===
    private static final Pattern CAP_PATTERN = Pattern.compile("\\d{5}");
    
    // === UI COMPONENTS ===
    // Basic fields
    private final Label courseNameLabel;
    private final TextArea descriptionArea;
    private final DatePicker startDatePicker, endDatePicker;
    private final ComboBox<String> courseTypeCombo;
    private final ComboBox<String> frequencyCombo;
    private final Spinner<Integer> maxPersonsSpinner;
    private final Button saveButton;
    
    // Sections
    private final VBox locationSection, recipesSection, recipesContainer;
    
    // Time fields
    private final Spinner<Integer> startHourSpinner, startMinuteSpinner;
    private final Spinner<Double> durationSpinner;
    
    // Location fields
    private final TextField streetField, capField;
    
    // Day checkboxes
    private final CheckBox mondayCheckBox, tuesdayCheckBox, wednesdayCheckBox;
    private final CheckBox thursdayCheckBox, fridayCheckBox, saturdayCheckBox, sundayCheckBox;
    
    // Error labels
    private final Label descriptionErrorLabel, maxPersonsErrorLabel;
    private final Label startDateErrorLabel, endDateErrorLabel, startTimeErrorLabel;
    private final Label durationErrorLabel, daysErrorLabel, streetErrorLabel, capErrorLabel;
    private final Label frequencyErrorLabel;
    
    // === DATA ===
    private final ObservableList<Recipe> recipes = FXCollections.observableArrayList();
    private final Map<String, CheckBox> dayCheckBoxes = new HashMap<>();
    private CourseData currentCourse;
    
    // === CONSTRUCTOR ===
    public EditCourseController(Label courseNameLabel, TextArea descriptionArea,
                              DatePicker startDatePicker, DatePicker endDatePicker,
                              ComboBox<String> courseTypeCombo, ComboBox<String> frequencyCombo,
                              Spinner<Integer> maxPersonsSpinner,
                              VBox locationSection, VBox recipesSection, VBox recipesContainer,
                              Spinner<Integer> startHourSpinner, Spinner<Integer> startMinuteSpinner,
                              Spinner<Double> durationSpinner, TextField streetField, TextField capField,
                              CheckBox mondayCheckBox, CheckBox tuesdayCheckBox, CheckBox wednesdayCheckBox,
                              CheckBox thursdayCheckBox, CheckBox fridayCheckBox, CheckBox saturdayCheckBox,
                              CheckBox sundayCheckBox, Label descriptionErrorLabel, Label maxPersonsErrorLabel,
                              Label startDateErrorLabel, Label endDateErrorLabel, Label startTimeErrorLabel,
                              Label durationErrorLabel, Label daysErrorLabel, Label streetErrorLabel,
                              Label capErrorLabel, Label frequencyErrorLabel, Button saveButton) {
        
        // Initialize all fields
        this.courseNameLabel = courseNameLabel;
        this.descriptionArea = descriptionArea;
        this.startDatePicker = startDatePicker;
        this.endDatePicker = endDatePicker;
        this.courseTypeCombo = courseTypeCombo;
        this.frequencyCombo = frequencyCombo;
        this.maxPersonsSpinner = maxPersonsSpinner;
        this.locationSection = locationSection;
        this.recipesSection = recipesSection;
        this.recipesContainer = recipesContainer;
        this.startHourSpinner = startHourSpinner;
        this.startMinuteSpinner = startMinuteSpinner;
        this.durationSpinner = durationSpinner;
        this.streetField = streetField;
        this.capField = capField;
        this.mondayCheckBox = mondayCheckBox;
        this.tuesdayCheckBox = tuesdayCheckBox;
        this.wednesdayCheckBox = wednesdayCheckBox;
        this.thursdayCheckBox = thursdayCheckBox;
        this.fridayCheckBox = fridayCheckBox;
        this.saturdayCheckBox = saturdayCheckBox;
        this.sundayCheckBox = sundayCheckBox;
        this.descriptionErrorLabel = descriptionErrorLabel;
        this.maxPersonsErrorLabel = maxPersonsErrorLabel;
        this.startDateErrorLabel = startDateErrorLabel;
        this.endDateErrorLabel = endDateErrorLabel;
        this.startTimeErrorLabel = startTimeErrorLabel;
        this.durationErrorLabel = durationErrorLabel;
        this.daysErrorLabel = daysErrorLabel;
        this.streetErrorLabel = streetErrorLabel;
        this.capErrorLabel = capErrorLabel;
        this.frequencyErrorLabel = frequencyErrorLabel;
        this.saveButton = saveButton;
    }
    
    // === INITIALIZATION ===
    public void initialize() {
        initializeDayCheckBoxesMap();
        setupUI();
        loadCourseData();
        setupValidation();
        setupModeUI();
        setupChangeListenersForSave(); // <--- aggiunto
        setupFrequencyRestrictionForHybrid(); // <--- aggiunto
    }

    /**
     * Imposta la UI in modalità modifica: disabilita il cambio tipo corso e aggiorna sezioni visibili.
     */
    private void setupModeUI() {
        // Disabilita la ComboBox del tipo corso (non modificabile in edit mode)
        courseTypeCombo.setDisable(true);

        // Mostra/nascondi le sezioni location/ricette in base al tipo corso attuale
        String selectedType = courseTypeCombo.getValue();
        boolean isPresenza = "In presenza".equals(selectedType);
        locationSection.setVisible(isPresenza);
        locationSection.setManaged(isPresenza);
        recipesSection.setVisible(isPresenza);
        recipesSection.setManaged(isPresenza);

        // Se non "In presenza", svuota i campi location e ricette
        if (!isPresenza) {
            streetField.clear();
            capField.clear();
            recipesContainer.getChildren().clear();
        } else {
            // Se torna a presenza e non ci sono ricette, carica quelle di default
            if (recipesContainer.getChildren().isEmpty()) {
                loadExistingRecipes();
            }
        }
        // (Se c'è un controllo per l'immagine, qui si può disabilitare)
    }

    /**
     * Abilita il salvataggio solo se c'è almeno una modifica rispetto ai dati originali.
     */
    private void setupChangeListenersForSave() {
        List<Observable> observables = new ArrayList<>(Arrays.asList(
            descriptionArea.textProperty(),
            startDatePicker.valueProperty(),
            endDatePicker.valueProperty(),
            frequencyCombo.valueProperty(),
            maxPersonsSpinner.valueProperty(),
            startHourSpinner.valueProperty(),
            startMinuteSpinner.valueProperty(),
            durationSpinner.valueProperty(),
            streetField.textProperty(),
            capField.textProperty()
        ));
        dayCheckBoxes.values().forEach(cb -> observables.add(cb.selectedProperty()));
        // Puoi aggiungere qui anche listeners per ricette/ingredienti se vuoi

        BooleanBinding changed = Bindings.createBooleanBinding(() -> hasAnyFieldChanged(),
            observables.toArray(new Observable[0])
        );
        saveButton.disableProperty().unbind();
        saveButton.disableProperty().bind(changed.not());
    }

    /**
     * Ritorna true se almeno un campo è stato modificato rispetto ai dati originali.
     */
    private boolean hasAnyFieldChanged() {
        if (currentCourse == null) return false;
        if (!Objects.equals(descriptionArea.getText().trim(), currentCourse.getDescription())) return true;
        if (!Objects.equals(startDatePicker.getValue(), currentCourse.getStartDate())) return true;
        if (!Objects.equals(endDatePicker.getValue(), currentCourse.getEndDate())) return true;
        if (!Objects.equals(frequencyCombo.getValue(), currentCourse.getFrequency())) return true;
        if (!Objects.equals(maxPersonsSpinner.getValue(), currentCourse.getMaxPersons())) return true;
        if (!Objects.equals(startHourSpinner.getValue(), currentCourse.getStartHour())) return true;
        if (!Objects.equals(startMinuteSpinner.getValue(), currentCourse.getStartMinute())) return true;
        if (!Objects.equals(durationSpinner.getValue(), currentCourse.getDuration())) return true;
        if (!Objects.equals(streetField.getText().trim(), currentCourse.getStreet())) return true;
        if (!Objects.equals(capField.getText().trim(), currentCourse.getCap())) return true;
        // Giorni
        List<String> selectedDays = getSelectedDays();
        if (!Objects.equals(selectedDays, currentCourse.getSelectedDays())) return true;
        // Puoi aggiungere qui anche il confronto per ricette/ingredienti
        return false;
    }

    /**
     * Impedisce di selezionare '1 volta a settimana' se il corso è "Entrambi" o "In presenza".
     */
    private void setupFrequencyRestrictionForHybrid() {
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
        // Se già selezionato, correggi
        frequencyCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            String type = courseTypeCombo.getValue();
            if (("Entrambi".equals(type) || "In presenza".equals(type)) && "1 volta a settimana".equals(newVal)) {
                // Trova la prima frequenza valida
                for (String freq : frequencyCombo.getItems()) {
                    if (!"1 volta a settimana".equals(freq)) {
                        frequencyCombo.setValue(freq);
                        break;
                    }
                }
            }
        });
    }
    
    private void initializeDayCheckBoxesMap() {
        dayCheckBoxes.put("Lunedì", mondayCheckBox);
        dayCheckBoxes.put("Martedì", tuesdayCheckBox);
        dayCheckBoxes.put("Mercoledì", wednesdayCheckBox);
        dayCheckBoxes.put("Giovedì", thursdayCheckBox);
        dayCheckBoxes.put("Venerdì", fridayCheckBox);
        dayCheckBoxes.put("Sabato", saturdayCheckBox);
        dayCheckBoxes.put("Domenica", sundayCheckBox);
    }
    
    private void setupUI() {
        setupSpinners();
        setupComboBoxes();
        setupDatePickers();
        setupFieldValidators();
        setupFrequencyListener();
    }
    
    // === UI SETUP METHODS ===
    private void setupSpinners() {
        // Max persone (1-50)
        maxPersonsSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));
        
        // Ore (6-23) - Più largo
        startHourSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 23, 18));
        startHourSpinner.setPrefWidth(100);
        setupTimeSpinnerFormatter(startHourSpinner, false);
        
        // Minuti (0-59, step 15) - Più largo
        startMinuteSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 15));
        startMinuteSpinner.setPrefWidth(100);
        setupTimeSpinnerFormatter(startMinuteSpinner, true);
        
        // Durata (0.5-8 ore, step 0.5)
        SpinnerValueFactory<Double> durationFactory = 
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 8.0, 2.0, 0.5);
        durationSpinner.setValueFactory(durationFactory);
        
        // Formatter per durata
        durationSpinner.getValueFactory().setConverter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                if (object == null) return "";
                return String.format("%.1f", object);
            }
            
            @Override
            public Double fromString(String string) {
                try {
                    return Double.parseDouble(string);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        });
    }
    
    private void setupTimeSpinnerFormatter(Spinner<Integer> spinner, boolean isMinute) {
        spinner.setEditable(true);
        
        spinner.getValueFactory().setConverter(new StringConverter<Integer>() {
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
                } catch (NumberFormatException e) {
                    return spinner.getValue();
                }
            }
        });
        
        TextField editor = spinner.getEditor();
        setupSpinnerEditor(editor, spinner, isMinute);
    }
    
    private void setupSpinnerEditor(TextField editor, Spinner<Integer> spinner, boolean isMinute) {
        editor.textProperty().addListener((obs, oldValue, newValue) -> {
            String filtered = newValue.replaceAll("[^\\d]", "");
            if (filtered.length() > 2) filtered = filtered.substring(0, 2);
            
            if (!filtered.equals(newValue)) {
                editor.setText(filtered);
            }
            
            if (!filtered.isEmpty()) {
                try {
                    int value = Integer.parseInt(filtered);
                    boolean isValid = isMinute ? (value <= 59) : (value >= 6 && value <= 23);
                    editor.getStyleClass().removeAll("spinner-error");
                    if (!isValid) editor.getStyleClass().add("spinner-error");
                } catch (NumberFormatException e) {
                    editor.getStyleClass().add("spinner-error");
                }
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
    
    private void setupComboBoxes() {
        // Tipo corso
        ObservableList<String> courseTypes = FXCollections.observableArrayList(
            "In presenza", "Telematica"
        );
        courseTypeCombo.setItems(courseTypes);
        courseTypeCombo.setValue("In presenza");
        
        // Frequenza
        ObservableList<String> frequencies = FXCollections.observableArrayList(
            "1 volta a settimana",
            "2 volte a settimana", 
            "3 volte a settimana",
            "Tutti i giorni (Lun-Ven)",
            "Tutti i giorni (Lun-Dom)"
        );
        frequencyCombo.setItems(frequencies);
        frequencyCombo.setValue("2 volte a settimana");
    }
    
    private void setupDatePickers() {
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate startDate = startDatePicker.getValue();
                setDisable(empty || (startDate != null && date.isBefore(startDate.plusDays(1))));
            }
        });
    }
    
    private void setupFieldValidators() {
        addTextValidator(capField, "[^\\d]", 5);
    }
    
    private void addTextValidator(TextField field, String regex, Integer maxLength) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal.replaceAll(regex, "");
            if (maxLength != null && filtered.length() > maxLength) {
                filtered = filtered.substring(0, maxLength);
            }
            field.setText(filtered);
        });
    }
    
    private void setupFrequencyListener() {
        frequencyCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateDayCheckboxesBasedOnFrequency(newVal);
        });
    }
    
    private void updateDayCheckboxesBasedOnFrequency(String frequency) {
        // Reset tutti i checkbox
        dayCheckBoxes.values().forEach(cb -> {
            cb.setSelected(false);
            cb.setDisable(false);
            cb.getStyleClass().remove("day-checkbox-disabled");
        });
        
        if (frequency == null) return;
        
        switch (frequency) {
            case "1 volta a settimana":
                limitDaySelection(1);
                break;
            case "2 volte a settimana":
                limitDaySelection(2);
                break;
            case "3 volte a settimana":
                limitDaySelection(3);
                break;
            case "Tutti i giorni (Lun-Ven)":
                selectWeekdays();
                break;
            case "Tutti i giorni (Lun-Dom)":
                selectAllDays();
                break;
        }
    }
    
    private void limitDaySelection(int maxSelection) {
        dayCheckBoxes.values().forEach(cb -> {
            cb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    long selectedCount = dayCheckBoxes.values().stream()
                        .mapToLong(checkbox -> checkbox.isSelected() ? 1 : 0)
                        .sum();
                    
                    if (selectedCount > maxSelection) {
                        cb.setSelected(false);
                        showFrequencyLimitMessage(maxSelection);
                    }
                }
            });
        });
    }
    
    private void selectWeekdays() {
        mondayCheckBox.setSelected(true);
        tuesdayCheckBox.setSelected(true);
        wednesdayCheckBox.setSelected(true);
        thursdayCheckBox.setSelected(true);
        fridayCheckBox.setSelected(true);
        
        saturdayCheckBox.setDisable(true);
        sundayCheckBox.setDisable(true);
        saturdayCheckBox.getStyleClass().add("day-checkbox-disabled");
        sundayCheckBox.getStyleClass().add("day-checkbox-disabled");
    }
    
    private void selectAllDays() {
        dayCheckBoxes.values().forEach(cb -> cb.setSelected(true));
        dayCheckBoxes.values().forEach(cb -> cb.setDisable(true));
        dayCheckBoxes.values().forEach(cb -> cb.getStyleClass().add("day-checkbox-disabled"));
    }
    
    private void showFrequencyLimitMessage(int maxSelection) {
        String message = "Puoi selezionare massimo " + maxSelection + " giorni per questa frequenza.";
        showError(frequencyErrorLabel, message);
        
        // Nascondi il messaggio dopo 3 secondi
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3), 
                e -> frequencyErrorLabel.setVisible(false))
        );
        timeline.play();
    }
    
    // === DATA LOADING ===
    private void loadCourseData() {
        // TODO: Sostituire con caricamento dal database
        // CourseData courseData = CourseDAO.getCourseById(courseId);
        
        // Simula il caricamento dei dati del corso dal database
        currentCourse = new CourseData();
        currentCourse.setName("Corso di Cucina Italiana Avanzata");
        currentCourse.setDescription("Un corso completo per imparare le tecniche avanzate della cucina italiana tradizionale.");
        currentCourse.setType("In presenza");
        currentCourse.setFrequency("2 volte a settimana");
        currentCourse.setMaxPersons(15);
        currentCourse.setStartDate(LocalDate.now().plusDays(10));
        currentCourse.setEndDate(LocalDate.now().plusDays(24));
        currentCourse.setStartHour(10);
        currentCourse.setStartMinute(0);
        currentCourse.setDuration(3.0);
        currentCourse.setStreet("Via Roma 123");
        currentCourse.setCap("80100");
        
        // Carica i dati nell'interfaccia
        populateUIFromCourseData();
        
        // Carica ricette esistenti
        loadExistingRecipes();
    }
    
    private void populateUIFromCourseData() {
        courseNameLabel.setText(currentCourse.getName());
        descriptionArea.setText(currentCourse.getDescription());
        courseTypeCombo.setValue(currentCourse.getType());
        frequencyCombo.setValue(currentCourse.getFrequency());
        maxPersonsSpinner.getValueFactory().setValue(currentCourse.getMaxPersons());
        startDatePicker.setValue(currentCourse.getStartDate());
        endDatePicker.setValue(currentCourse.getEndDate());
        startHourSpinner.getValueFactory().setValue(currentCourse.getStartHour());
        startMinuteSpinner.getValueFactory().setValue(currentCourse.getStartMinute());
        durationSpinner.getValueFactory().setValue(currentCourse.getDuration());
        streetField.setText(currentCourse.getStreet());
        capField.setText(currentCourse.getCap());
        
        // Imposta giorni della settimana (esempio)
        mondayCheckBox.setSelected(true);
        wednesdayCheckBox.setSelected(true);
        
        // Mostra/nascondi sezione location
        onCourseTypeChanged();
        
        // Aggiorna i checkbox in base alla frequenza
        updateDayCheckboxesBasedOnFrequency(currentCourse.getFrequency());
    }
    
    private void loadExistingRecipes() {
        // TODO: Sostituire con caricamento dal database
        // List<Recipe> existingRecipes = RecipeDAO.getRecipesByCourseId(courseId);
        
        // Simula il caricamento delle ricette esistenti
        addRecipeToContainer("Pasta all'Amatriciana", 
            new String[]{"Pasta", "Guanciale", "Pomodori", "Pecorino"},
            new String[]{"500", "150", "400", "100"},
            new String[]{"g", "g", "g", "g"});
            
        addRecipeToContainer("Risotto ai Porcini", 
            new String[]{"Riso Carnaroli", "Porcini", "Brodo", "Parmigiano"},
            new String[]{"320", "200", "1", "80"},
            new String[]{"g", "g", "L", "g"});
    }
    
    // === VALIDATION ===
    private void setupValidation() {
        // Rimuovi il binding qui: la logica di abilitazione è gestita da setupChangeListenersForSave()
        // BooleanBinding formValid = createFormValidation();
        // saveButton.disableProperty().bind(formValid.not());
    }
    
    private BooleanBinding createFormValidation() {
        Observable[] properties = getAllRelevantProperties();
        
        return Bindings.createBooleanBinding(() -> {
            return isValidText(descriptionArea.getText()) &&
                   startDatePicker.getValue() != null &&
                   endDatePicker.getValue() != null &&
                   courseTypeCombo.getValue() != null &&
                   frequencyCombo.getValue() != null &&
                   isValidDaySelection() &&
                   validateTypeSpecificFields();
        }, properties);
    }
    
    private boolean validateTypeSpecificFields() {
        String courseType = courseTypeCombo.getValue();
        if ("In presenza".equals(courseType)) {
            return isValidText(streetField.getText()) && isValidCAP(capField.getText());
        }
        return true; // Per corsi telematici non ci sono campi specifici da validare
    }
    
    private Observable[] getAllRelevantProperties() {
        // Crea una lista mutabile invece di usare Arrays.asList()
        List<Observable> observables = new ArrayList<>(Arrays.asList(
            descriptionArea.textProperty(),
            startDatePicker.valueProperty(),
            endDatePicker.valueProperty(),
            courseTypeCombo.valueProperty(),
            frequencyCombo.valueProperty(),
            streetField.textProperty(),
            capField.textProperty()
        ));
        
        // Aggiungi listeners per le checkbox
        dayCheckBoxes.values().forEach(cb -> 
            observables.add(cb.selectedProperty())
        );
        
        return observables.toArray(new Observable[0]);
    }
    
    private boolean isValidDaySelection() {
        String frequency = frequencyCombo.getValue();
        if (frequency == null) return false;
        
        long selectedCount = dayCheckBoxes.values().stream()
            .mapToLong(cb -> cb.isSelected() ? 1 : 0)
            .sum();
        
        switch (frequency) {
            case "1 volta a settimana":
                return selectedCount == 1;
            case "2 volte a settimana":
                return selectedCount == 2;
            case "3 volte a settimana":
                return selectedCount == 3;
            case "Tutti i giorni (Lun-Ven)":
                return selectedCount == 5;
            case "Tutti i giorni (Lun-Dom)":
                return selectedCount == 7;
            default:
                return selectedCount > 0;
        }
    }
    
    // === PUBLIC ACTIONS ===
    public void onCourseTypeChanged() {
        String selectedType = courseTypeCombo.getValue();
        boolean isPresenza = "In presenza".equals(selectedType);
        
        // Gestisci visibilità sezione location
        locationSection.setVisible(isPresenza);
        locationSection.setManaged(isPresenza);
        
        // Gestisci visibilità sezione ricette (solo per corsi in presenza)
        recipesSection.setVisible(isPresenza);
        recipesSection.setManaged(isPresenza);
        
        
        if (!isPresenza) {
            streetField.clear();
            capField.clear();
            recipesContainer.getChildren().clear(); // Rimuovi tutte le ricette
        } else {
            // Se torna a presenza e non ci sono ricette, carica quelle di default
            if (recipesContainer.getChildren().isEmpty()) {
                loadExistingRecipes();
            }
        }
    }
    
    public void onFrequencyChanged() {
        updateDayCheckboxesBasedOnFrequency(frequencyCombo.getValue());
    }
    
    public void addNewRecipe() {
        addRecipeToContainer("", new String[0], new String[0], new String[0]);
    }
    
    public void saveCourse() {
        // Salva solo se almeno un campo è stato modificato e il form è valido
        if (hasAnyFieldChanged() && validateFormData()) {
            try {
                CourseData updatedCourse = collectUpdatedCourseData();
                // TODO: Sostituire con chiamata al DAO
                // boolean success = CourseDAO.updateCourse(updatedCourse);
                // if (success) {
                showCourseUpdateSuccessDialog();
                // } else {
                //     showAlert("Errore", "Errore durante l'aggiornamento del corso.");
                // }
            } catch (Exception e) {
                showAlert("Errore", "Si è verificato un errore durante l'aggiornamento del corso.");
                e.printStackTrace();
            }
        }
    }
    
    public void goBack() {
        try {
            // Ottieni lo stage dalla scena corrente
            Stage stage = (Stage) courseNameLabel.getScene().getWindow();
            
            // Chiudi la finestra corrente
            //stage.close();
            
            // Se vuoi tornare alla homepage chef invece di chiudere, decommentare la riga sotto:
            SceneSwitcher.switchScene(stage, "/fxml/homepagechef.fxml", "UninaFoodLab - Homepage");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore durante la chiusura della finestra: " + e.getMessage());
            
            // Fallback: prova a chiudere comunque
            try {
                Stage fallbackStage = (Stage) courseNameLabel.getScene().getWindow();
                fallbackStage.close();
            } catch (Exception ex) {
                System.err.println("Impossibile chiudere la finestra anche nel fallback: " + ex.getMessage());
            }
        }
    }
    
    // === RECIPE MANAGEMENT ===
    private void addRecipeToContainer(String recipeName, String[] ingredients, 
                                     String[] quantities, String[] units) {
        VBox recipeBox = new VBox(10);
        recipeBox.getStyleClass().add("recipe-container");
        
        // Header ricetta
        HBox recipeHeader = new HBox(15);
        recipeHeader.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label recipeLabel = new Label("Nome Ricetta:");
        recipeLabel.getStyleClass().add("field-label");
        
        TextField recipeNameField = new TextField(recipeName);
        recipeNameField.setPromptText("Inserisci il nome della ricetta...");
        recipeNameField.getStyleClass().add("form-field");
        recipeNameField.setPrefWidth(300);
        
        Button removeRecipeBtn = new Button("Rimuovi Ricetta");
        removeRecipeBtn.getStyleClass().add("remove-button");
        removeRecipeBtn.setOnAction(e -> removeRecipe(recipeBox));
        
        recipeHeader.getChildren().addAll(recipeLabel, recipeNameField, removeRecipeBtn);
        
        // Container ingredienti
        VBox ingredientsBox = new VBox(5);
        Label ingredientsLabel = new Label("Ingredienti:");
        ingredientsLabel.getStyleClass().add("field-label");
        
        VBox ingredientsList = new VBox(5);
        
        // Aggiungi ingredienti esistenti
        for (int i = 0; i < ingredients.length; i++) {
            addIngredientRow(ingredientsList, ingredients[i], quantities[i], units[i]);
        }
        
        Button addIngredientBtn = new Button("+ Aggiungi Ingrediente");
        addIngredientBtn.getStyleClass().add("add-ingredient-button");
        addIngredientBtn.setOnAction(e -> addIngredientRow(ingredientsList, "", "", "g"));
        
        ingredientsBox.getChildren().addAll(ingredientsLabel, ingredientsList, addIngredientBtn);
        
        recipeBox.getChildren().addAll(recipeHeader, ingredientsBox);
        recipesContainer.getChildren().add(recipeBox);
    }
    
    private void addIngredientRow(VBox parent, String name, String quantity, String unit) {
        HBox ingredientRow = new HBox(10);
        ingredientRow.getStyleClass().add("ingredient-row");
        
        TextField nameField = new TextField(name);
        nameField.setPromptText("Nome ingrediente");
        nameField.getStyleClass().addAll("form-field", "ingredient-name-field");
        
        TextField quantityField = new TextField(quantity);
        quantityField.setPromptText("Quantità");
        quantityField.getStyleClass().addAll("form-field", "ingredient-quantity-field");
        
        ComboBox<String> unitCombo = new ComboBox<>();
        unitCombo.getItems().addAll("g", "kg", "ml", "L", "n°", "cucchiai", "cucchiaini", "tazze", "q.b.");
        unitCombo.setValue(unit.isEmpty() ? "g" : unit);
        unitCombo.getStyleClass().addAll("combo-box", "ingredient-unit-combo");
        
        Button removeBtn = new Button("×");
        removeBtn.getStyleClass().add("remove-ingredient-button");
        removeBtn.setOnAction(e -> parent.getChildren().remove(ingredientRow));
        
        ingredientRow.getChildren().addAll(nameField, quantityField, unitCombo, removeBtn);
        parent.getChildren().add(ingredientRow);
    }
    
    private void removeRecipe(VBox recipeBox) {
        if (recipesContainer.getChildren().size() > 1) {
            recipesContainer.getChildren().remove(recipeBox);
        } else {
            showAlert("Attenzione", "Deve essere presente almeno una ricetta.");
        }
    }
    
    // === DATA COLLECTION ===
    private CourseData collectUpdatedCourseData() {
        CourseData updatedData = new CourseData();
        
        updatedData.setName(currentCourse.getName()); // Nome non modificabile
        updatedData.setDescription(descriptionArea.getText().trim());
        updatedData.setType(courseTypeCombo.getValue());
        updatedData.setFrequency(frequencyCombo.getValue());
        updatedData.setMaxPersons(maxPersonsSpinner.getValue());
        updatedData.setStartDate(startDatePicker.getValue());
        updatedData.setEndDate(endDatePicker.getValue());
        updatedData.setStartHour(startHourSpinner.getValue());
        updatedData.setStartMinute(startMinuteSpinner.getValue());
        updatedData.setDuration(durationSpinner.getValue());
        
        if ("In presenza".equals(courseTypeCombo.getValue())) {
            updatedData.setStreet(streetField.getText().trim());
            updatedData.setCap(capField.getText().trim());
        }
        
        updatedData.setSelectedDays(getSelectedDays());
        
        return updatedData;
    }
    
    private List<String> getSelectedDays() {
        return dayCheckBoxes.entrySet().stream()
            .filter(entry -> entry.getValue().isSelected())
            .map(Map.Entry::getKey)
            .collect(ArrayList::new, (list, item) -> list.add(item), ArrayList::addAll);
    }
    
    // === VALIDATION HELPERS ===
    private boolean validateFormData() {
        boolean isValid = true;
        
        // Reset error labels
        hideAllErrors();
        
        // Validazione descrizione
        if (!isValidText(descriptionArea.getText())) {
            showError(descriptionErrorLabel, "La descrizione è obbligatoria");
            isValid = false;
        }
        
        // Validazione date
        if (startDatePicker.getValue() == null) {
            showError(startDateErrorLabel, "La data di inizio è obbligatoria");
            isValid = false;
        }
        
        if (endDatePicker.getValue() == null) {
            showError(endDateErrorLabel, "La data di fine è obbligatoria");
            isValid = false;
        }
        
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null &&
            !endDatePicker.getValue().isAfter(startDatePicker.getValue())) {
            showError(endDateErrorLabel, "La data di fine deve essere successiva a quella di inizio");
            isValid = false;
        }
        
        // Validazione frequenza e giorni
        if (!isValidDaySelection()) {
            showError(daysErrorLabel, "Seleziona i giorni corretti per la frequenza scelta");
            isValid = false;
        }
        
        // Validazione location (se in presenza)
        if ("In presenza".equals(courseTypeCombo.getValue())) {
            if (!isValidText(streetField.getText())) {
                showError(streetErrorLabel, "La via è obbligatoria per corsi in presenza");
                isValid = false;
            }
            
            if (!isValidCAP(capField.getText())) {
                showError(capErrorLabel, "Il CAP è obbligatorio per corsi in presenza");
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    private boolean isValidText(String text) {
        return text != null && !text.trim().isEmpty();
    }
    
    private boolean isValidCAP(String capText) {
        return isValidText(capText) && CAP_PATTERN.matcher(capText.trim()).matches();
    }
    
    private void hideAllErrors() {
        descriptionErrorLabel.setVisible(false);
        maxPersonsErrorLabel.setVisible(false);
        startDateErrorLabel.setVisible(false);
        endDateErrorLabel.setVisible(false);
        startTimeErrorLabel.setVisible(false);
        durationErrorLabel.setVisible(false);
        daysErrorLabel.setVisible(false);
        streetErrorLabel.setVisible(false);
        capErrorLabel.setVisible(false);
        frequencyErrorLabel.setVisible(false);
    }
    
    private void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    // === UTILITY ===
    private void showCourseUpdateSuccessDialog() {
        try {
            Stage parentStage = (Stage) courseNameLabel.getScene().getWindow();
            String courseName = currentCourse.getName();
            SuccessDialogUtils.showGenericSuccessDialog(
                parentStage, 
                "Corso Aggiornato con Successo!", 
                "Il corso \"" + courseName + "\" è stato aggiornato con successo."
            );
            goBack();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Successo", "Il corso \"" + currentCourse.getName() + "\" è stato aggiornato con successo!");
            goBack();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Successo") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // === DATA TRANSFER OBJECTS ===
    public static class CourseData {
        private String name, description, type, frequency, street, cap;
        private int maxPersons, startHour, startMinute;
        private double duration;
        private LocalDate startDate, endDate;
        private List<String> selectedDays;
        
        // Getters e setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }
        
        public int getMaxPersons() { return maxPersons; }
        public void setMaxPersons(int maxPersons) { this.maxPersons = maxPersons; }
        
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        
        public int getStartHour() { return startHour; }
        public void setStartHour(int startHour) { this.startHour = startHour; }
        
        public int getStartMinute() { return startMinute; }
        public void setStartMinute(int startMinute) { this.startMinute = startMinute; }
        
        public double getDuration() { return duration; }
        public void setDuration(double duration) { this.duration = duration; }
        
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        
        public String getCap() { return cap; }
        public void setCap(String cap) { this.cap = cap; }
        
        public List<String> getSelectedDays() { return selectedDays; }
        public void setSelectedDays(List<String> selectedDays) { this.selectedDays = selectedDays; }
    }
    
    // Recipe class reused from CreateCourseController
    public static class Recipe {
        private String name;
        private final ObservableList<Ingredient> ingredients = FXCollections.observableArrayList();
        private VBox ingredientsContainer;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public ObservableList<Ingredient> getIngredients() { return ingredients; }
        public void addIngredient(Ingredient ingredient) { ingredients.add(ingredient); }
        public void removeIngredient(Ingredient ingredient) { ingredients.remove(ingredient); }
        public VBox getIngredientsContainer() { return ingredientsContainer; }
        public void setIngredientsContainer(VBox container) { this.ingredientsContainer = container; }
    }
    
    public static class Ingredient {
        private String name, quantity, unit;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getQuantity() { return quantity; }
        public void setQuantity(String quantity) { this.quantity = quantity; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }
}