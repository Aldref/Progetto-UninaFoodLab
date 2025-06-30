package com.progetto.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;
import com.progetto.utils.SuccessDialogUtils;

public class CreateCourseController {
    // --- Hybrid validation binding for manual invalidation ---
    private BooleanBinding detailsValidBinding;
    
    // === CONSTANTS ===
    private static final Pattern DURATION_PATTERN = Pattern.compile("\\d+");
    private static final Pattern CAP_PATTERN = Pattern.compile("\\d{5}");
    private static final Pattern PRICE_PATTERN = Pattern.compile("\\d*(\\.\\d{0,2})?");
    
    // === UI COMPONENTS ===
    // Basic fields
    private final TextField courseNameField, priceField;
    private final TextArea descriptionArea; // Cambiato da TextField a TextArea
    private final DatePicker startDatePicker, endDatePicker;
    private final ComboBox<String> frequencyComboBox, lessonTypeComboBox;
    private final Spinner<Integer> maxParticipantsSpinner;
    private final ImageView courseImageView;
    private final Label chefNameLabel;
    private final Button createButton;
    
    // Sections
    private final VBox presenceDetailsSection, onlineDetailsSection, recipesContainer;
    
    // Presence fields
    private final FlowPane dayOfWeekContainer;
    private final Spinner<Integer> presenceHourSpinner, presenceMinuteSpinner;
    private final TextField durationField, cityField, streetField, capField;
    
    // Online fields  
    private final ComboBox<String> applicationComboBox;
    private final TextField meetingCodeField;
    private final FlowPane onlineDayOfWeekContainer;
    private final Spinner<Integer> onlineHourSpinner, onlineMinuteSpinner;
    private final TextField onlineDurationField;
    
    // === HYBRID MODE SUPPORT ===
    private final VBox hybridDaysContainer;
    private final VBox hybridDetailsSection;
    private final Label hybridErrorLabel;
    private final List<HybridDay> hybridDays = new ArrayList<>();
    private static final String[] WEEK_DAYS = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};

    // === DATA ===
    private final ObservableList<Recipe> recipes = FXCollections.observableArrayList();
    private final Map<String, CheckBox> presenceDayCheckBoxes = new HashMap<>();
    private final Map<String, CheckBox> onlineDayCheckBoxes = new HashMap<>();
    private Label presenceDayInfoLabel, onlineDayInfoLabel;
    
    // === CUISINE TYPE FIELDS ===
    private final ComboBox<String> cuisineTypeComboBox1;
    private final ComboBox<String> cuisineTypeComboBox2;
    private final Label cuisineTypeErrorLabel;
    
    // === CONSTRUCTOR ===
    public CreateCourseController(TextField courseNameField, TextArea descriptionArea, 
                                DatePicker startDatePicker, DatePicker endDatePicker,
                                ComboBox<String> frequencyComboBox, ComboBox<String> lessonTypeComboBox,
                                Spinner<Integer> maxParticipantsSpinner, TextField priceField,
                                ImageView courseImageView, Label chefNameLabel, Button createButton,
                                VBox presenceDetailsSection, VBox onlineDetailsSection,
                                FlowPane dayOfWeekContainer, Spinner<Integer> presenceHourSpinner, 
                                Spinner<Integer> presenceMinuteSpinner, TextField durationField,
                                TextField cityField, TextField streetField, TextField capField,
                                VBox recipesContainer, ComboBox<String> applicationComboBox, 
                                TextField meetingCodeField, FlowPane onlineDayOfWeekContainer, 
                                Spinner<Integer> onlineHourSpinner, Spinner<Integer> onlineMinuteSpinner, 
                                TextField onlineDurationField, VBox hybridDetailsSection, VBox hybridDaysContainer, Label hybridErrorLabel,
                                ComboBox<String> cuisineTypeComboBox1, ComboBox<String> cuisineTypeComboBox2, Label cuisineTypeErrorLabel
    ) {
        
        // Initialize all fields
        this.courseNameField = courseNameField;
        this.descriptionArea = descriptionArea; // Rimosso il cast errato
        this.startDatePicker = startDatePicker;
        this.endDatePicker = endDatePicker;
        this.frequencyComboBox = frequencyComboBox;
        this.lessonTypeComboBox = lessonTypeComboBox;
        this.maxParticipantsSpinner = maxParticipantsSpinner;
        this.priceField = priceField;
        this.courseImageView = courseImageView;
        this.chefNameLabel = chefNameLabel;
        this.createButton = createButton;
        this.presenceDetailsSection = presenceDetailsSection;
        this.onlineDetailsSection = onlineDetailsSection;
        this.dayOfWeekContainer = dayOfWeekContainer;
        this.presenceHourSpinner = presenceHourSpinner;
        this.presenceMinuteSpinner = presenceMinuteSpinner;
        this.durationField = durationField;
        this.cityField = cityField;
        this.streetField = streetField;
        this.capField = capField;
        this.recipesContainer = recipesContainer;
        this.applicationComboBox = applicationComboBox;
        this.meetingCodeField = meetingCodeField;
        this.onlineDayOfWeekContainer = onlineDayOfWeekContainer;
        this.onlineHourSpinner = onlineHourSpinner;
        this.onlineMinuteSpinner = onlineMinuteSpinner;
        this.onlineDurationField = onlineDurationField;
        this.hybridDetailsSection = hybridDetailsSection;
        this.hybridDaysContainer = hybridDaysContainer;
        this.hybridErrorLabel = hybridErrorLabel;
        this.cuisineTypeComboBox1 = cuisineTypeComboBox1;
        this.cuisineTypeComboBox2 = cuisineTypeComboBox2;
        this.cuisineTypeErrorLabel = cuisineTypeErrorLabel;
    }
    
    // === INITIALIZATION ===
    public void initialize() {
        initializeData();
        setupUI();
        setupValidation();
        addNewRecipe(); // Aggiungi una ricetta vuota di default
    }
    
    private void initializeData() {
        // TODO: Sostituire con dati dal database
        chefNameLabel.setText("Mario Rossi");
        
        // Popola dropdown da database
        loadFrequencyOptions();
        loadLessonTypes();
        loadApplications();
    }
    
    private void setupUI() {
        setupSpinners();
        setupDaySelection();
        setupDatePickers();
        setupFieldValidators();
        setupEventListeners();
        // Limite caratteri descrizione (min 1, max 60)
        descriptionArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 60) {
                descriptionArea.setText(newVal.substring(0, 60));
            }
        });

        // Popola tipi di cucina
        ObservableList<String> cuisineTypes = FXCollections.observableArrayList(
            "Italiana", "Cinese", "Giapponese", "Messicana", "Indiana", "Francese", "Greca", "Spagnola", "Tailandese", "Americana", "Vegana", "Vegetariana", "Altro"
        );
        cuisineTypeComboBox1.setItems(cuisineTypes);
        cuisineTypeComboBox2.setItems(cuisineTypes);
        cuisineTypeComboBox1.setValue(null);
        cuisineTypeComboBox2.setValue(null);
        cuisineTypeErrorLabel.setVisible(false);

        // Limita durata presenza a 480
        durationField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal.replaceAll("[^\\d]", "");
            if (!filtered.equals(newVal)) durationField.setText(filtered);
            if (!filtered.isEmpty()) {
                try {
                    int val = Integer.parseInt(filtered);
                    if (val > 480) durationField.setText("480");
                } catch (NumberFormatException e) { durationField.setText(""); }
            }
        });
        // Limita durata telematica a 480
        onlineDurationField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal.replaceAll("[^\\d]", "");
            if (!filtered.equals(newVal)) onlineDurationField.setText(filtered);
            if (!filtered.isEmpty()) {
                try {
                    int val = Integer.parseInt(filtered);
                    if (val > 480) onlineDurationField.setText("480");
                } catch (NumberFormatException e) { onlineDurationField.setText(""); }
            }
        });
    }
    
    // === DATABASE INTEGRATION METHODS ===
    private void loadFrequencyOptions() {
        // TODO: Sostituire con chiamata al DAO
        frequencyComboBox.getItems().addAll(
            "1 volta a settimana", "2 volte a settimana", "3 volte a settimana"
        );
        // Esempio: frequencyComboBox.setItems(FrequencyDAO.getAllFrequencies());
    }
    
    private void loadLessonTypes() {
        // TODO: Sostituire con chiamata al DAO
        lessonTypeComboBox.getItems().addAll("In presenza", "Telematica", "Entrambi");
        // Esempio: lessonTypeComboBox.setItems(LessonTypeDAO.getAllTypes());
    }
    
    private void loadApplications() {
        // TODO: Sostituire con chiamata al DAO
        applicationComboBox.getItems().addAll("Zoom", "Microsoft Teams", "Google Meet");
        // Esempio: applicationComboBox.setItems(ApplicationDAO.getAllApplications());
    }
    
    // === UI SETUP METHODS ===
    private void setupSpinners() {
        maxParticipantsSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));
        
        Arrays.asList(presenceHourSpinner, onlineHourSpinner).forEach(spinner -> {
            spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 23, 18));
            setupTimeSpinnerFormatter(spinner, false);
        });
        
        Arrays.asList(presenceMinuteSpinner, onlineMinuteSpinner).forEach(spinner -> {
            spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 15));
            setupTimeSpinnerFormatter(spinner, true);
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
    
    private void setupDaySelection() {
        String[] days = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};
        
        setupDayContainer(dayOfWeekContainer, presenceDayCheckBoxes, days, true);
        setupDayContainer(onlineDayOfWeekContainer, onlineDayCheckBoxes, days, false);
        
        setDayCheckBoxesEnabled(false);
    }
    
    private void setupDayContainer(FlowPane container, Map<String, CheckBox> checkBoxMap, 
                                  String[] days, boolean isPresence) {
        container.getChildren().clear();
        checkBoxMap.clear();
        
        for (String day : days) {
            CheckBox checkBox = new CheckBox(day);
            checkBox.getStyleClass().add("day-checkbox");
            checkBox.setOnAction(e -> validateDaySelection(checkBoxMap, 
                isPresence ? presenceDayInfoLabel : onlineDayInfoLabel));
            checkBoxMap.put(day, checkBox);
            container.getChildren().add(checkBox);
        }
        
        Label infoLabel = new Label("Seleziona la frequenza prima di scegliere i giorni");
        infoLabel.getStyleClass().add("info-label");
        container.getChildren().add(infoLabel);
        
        if (isPresence) presenceDayInfoLabel = infoLabel;
        else onlineDayInfoLabel = infoLabel;
    }
    
    private void setupDatePickers() {
        startDatePicker.setValue(LocalDate.now());
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
        addTextValidator(priceField, PRICE_PATTERN);
        addTextValidator(durationField, "[^\\d]", 3); // max 3 cifre
        addTextValidator(onlineDurationField, "[^\\d]", 3);
        // Solo lettere per città (presenza)
        cityField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal.replaceAll("[^a-zA-ZàèéìòùÀÈÉÌÒÙ' ]", "");
            if (!filtered.equals(newVal)) cityField.setText(filtered);
        });
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
    
    private void addTextValidator(TextField field, Pattern pattern) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!pattern.matcher(newVal).matches()) {
                String filtered = newVal.replaceAll("[^\\d.]", "");
                int dotIndex = filtered.indexOf('.');
                if (dotIndex != -1) {
                    String beforeDot = filtered.substring(0, dotIndex);
                    String afterDot = filtered.substring(dotIndex + 1).replaceAll("\\.", "");
                    if (afterDot.length() > 2) afterDot = afterDot.substring(0, 2);
                    filtered = beforeDot + "." + afterDot;
                }
                field.setText(filtered);
            }
        });
    }
    
    private void setupEventListeners() {
        frequencyComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                clearDaySelections();
                setDayCheckBoxesEnabled(true);
                updateDayInfoLabels(newVal);
            } else {
                setDayCheckBoxesEnabled(false);
                resetDayInfoLabels();
            }
        });
    }
    
    // === VALIDATION ===
    private void setupValidation() {
        BooleanBinding basicValid = createBasicValidation();
        detailsValidBinding = createDetailsValidation();
        addCheckboxValidationListeners(detailsValidBinding);
        createButton.disableProperty().bind(basicValid.not().or(detailsValidBinding.not()));

        // --- Hybrid dynamic reactivity fix ---
        // Listen to changes in hybridDays and their fields
        Runnable hybridListener = this::invalidateHybridValidation;
        for (HybridDay hd : hybridDays) {
            if (hd.modeProperty() != null) hd.modeProperty().addListener((obs, ov, nv) -> hybridListener.run());
            if (hd.hourProperty() != null) hd.hourProperty().addListener((obs, ov, nv) -> hybridListener.run());
            if (hd.minuteProperty() != null) hd.minuteProperty().addListener((obs, ov, nv) -> hybridListener.run());
            if (hd.durationProperty() != null) hd.durationProperty().addListener((obs, ov, nv) -> hybridListener.run());
            if (hd.cityProperty() != null) hd.cityProperty().addListener((obs, ov, nv) -> hybridListener.run());
            if (hd.streetProperty() != null) hd.streetProperty().addListener((obs, ov, nv) -> hybridListener.run());
            if (hd.capProperty() != null) hd.capProperty().addListener((obs, ov, nv) -> hybridListener.run());
            if (hd.enabledProperty() != null) hd.enabledProperty().addListener((obs, ov, nv) -> hybridListener.run());
            if (hd.recipesProperty() != null) hd.recipesProperty().addListener((obs, ov, nv) -> hybridListener.run());
            if (hd.appComboProperty() != null) hd.appComboProperty().addListener((obs, ov, nv) -> hybridListener.run());
            if (hd.linkFieldProperty() != null) hd.linkFieldProperty().addListener((obs, ov, nv) -> hybridListener.run());
        }
    }
    // Call this after any hybrid UI change to revalidate
    private void invalidateHybridValidation() {
        if (detailsValidBinding != null) detailsValidBinding.invalidate();
    }
    
    private BooleanBinding createBasicValidation() {
        return Bindings.createBooleanBinding(() -> 
            isValidText(courseNameField.getText()) &&
            isValidDescription(descriptionArea.getText()) &&
            startDatePicker.getValue() != null &&
            endDatePicker.getValue() != null &&
            frequencyComboBox.getValue() != null &&
            lessonTypeComboBox.getValue() != null &&
            isValidPrice(priceField.getText()) &&
            isValidParticipants(maxParticipantsSpinner.getValue()),
            courseNameField.textProperty(),
            descriptionArea.textProperty(),
            startDatePicker.valueProperty(),
            endDatePicker.valueProperty(),
            frequencyComboBox.valueProperty(),
            lessonTypeComboBox.valueProperty(),
            priceField.textProperty(),
            maxParticipantsSpinner.valueProperty()
        );
    }
    
    private BooleanBinding createDetailsValidation() {
        // Corretto: converti Object[] in Observable[]
        Observable[] properties = Arrays.stream(getAllRelevantProperties())
            .filter(obj -> obj instanceof Observable)
            .toArray(Observable[]::new);

        return Bindings.createBooleanBinding(() -> {
            String lessonType = lessonTypeComboBox.getValue();
            if ("In presenza".equals(lessonType)) {
                return validatePresenceDetails();
            } else if ("Telematica".equals(lessonType)) {
                return validateOnlineDetails();
            } else if ("Entrambi".equals(lessonType)) {
                return validateHybridDetailsFixed();
            }
            return false;
        }, properties);
    }
    
    private boolean validatePresenceDetails() {
        if (!hasCorrectNumberOfDays(presenceDayCheckBoxes)) return false;
        if (!isValidDurationRange(durationField.getText())) return false;
        if (!isValidText(cityField.getText())) return false;
        if (!isValidText(streetField.getText())) return false;
        if (!isValidCAP(capField.getText())) return false;
        // Almeno una ricetta con almeno un ingrediente completo
        boolean hasValidRecipe = false;
        for (javafx.scene.Node node : recipesContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox recipeBox = (VBox) node;
                TextField nameField = null;
                VBox ingredientsBox = null;
                for (javafx.scene.Node child : recipeBox.getChildren()) {
                    if (child instanceof HBox && nameField == null) {
                        // Cerca il TextField del nome ricetta nell'header
                        for (javafx.scene.Node h : ((HBox) child).getChildren()) {
                            if (h instanceof TextField) {
                                nameField = (TextField) h;
                                break;
                            }
                        }
                    }
                    if (child instanceof VBox && ingredientsBox == null) ingredientsBox = (VBox) child;
                }
                if (nameField != null && isValidText(nameField.getText()) && ingredientsBox != null) {
                    for (javafx.scene.Node ingrRow : ingredientsBox.getChildren()) {
                        if (ingrRow instanceof HBox) {
                            HBox row = (HBox) ingrRow;
                            TextField ingrName = null, ingrQty = null;
                            ComboBox<?> ingrUnit = null;
                            for (javafx.scene.Node ingrField : row.getChildren()) {
                                if (ingrField instanceof TextField && ingrName == null) ingrName = (TextField) ingrField;
                                else if (ingrField instanceof TextField && ingrQty == null && ingrName != null) ingrQty = (TextField) ingrField;
                                else if (ingrField instanceof ComboBox && ingrUnit == null) ingrUnit = (ComboBox<?>) ingrField;
                            }
                            if (ingrName != null && isValidText(ingrName.getText()) && ingrQty != null && isValidText(ingrQty.getText()) && ingrUnit != null && ingrUnit.getValue() != null) {
                                hasValidRecipe = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (!hasValidRecipe) return false;
        return true;
    }
    
    private boolean validateOnlineDetails() {
        return applicationComboBox.getValue() != null &&
               isValidText(meetingCodeField.getText()) &&
               hasCorrectNumberOfDays(onlineDayCheckBoxes) &&
               isValidDurationRange(onlineDurationField.getText());
    }
    
    private boolean validateHybridDetails() {
        // Obsoleta, sostituita da validateHybridDetailsFixed()
        return false;
    }

    // Nuova validazione per "Entrambi" (ibrido):
    private boolean validateHybridDetailsFixed() {
        // Controlla che il numero di giorni selezionati sia corretto
        int expectedDays = getMaxDaysFromFrequency(frequencyComboBox.getValue());
        long selectedDays = hybridDays.stream().filter(HybridDay::isEnabled).count();
        if (selectedDays != expectedDays) return false;

        for (HybridDay hd : hybridDays) {
            if (!hd.isEnabled()) continue;
            String mode = hd.getMode();
            if ("Presenza".equals(mode)) {
                // Tutti i campi presenza obbligatori
                if (!isValidText(hd.getCity())) return false;
                if (!isValidText(hd.getStreet())) return false;
                if (!isValidCAP(hd.getCap())) return false;
                if (hd.getHour() == null) return false;
                if (hd.getMinute() == null) return false;
                if (!isValidDurationRange(hd.getDuration())) return false;
                // Almeno una ricetta con almeno un ingrediente completo
                java.util.List<Recipe> recipes = hd.getRecipes();
                boolean hasValidRecipe = false;
                if (recipes != null) {
                    for (Recipe r : recipes) {
                        if (r.getName() != null && !r.getName().trim().isEmpty()) {
                            javafx.collections.ObservableList<Ingredient> ings = r.getIngredients();
                            if (ings != null && !ings.isEmpty()) {
                                boolean allValid = true;
                                for (Ingredient ing : ings) {
                                    if (ing.getName() == null || ing.getName().trim().isEmpty()) allValid = false;
                                    if (ing.getQuantity() == null || ing.getQuantity().trim().isEmpty()) allValid = false;
                                    if (ing.getUnit() == null || ing.getUnit().trim().isEmpty()) allValid = false;
                                }
                                if (allValid) {
                                    hasValidRecipe = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (!hasValidRecipe) return false;
            } else if ("Telematica".equals(mode)) {
                ComboBox<String> appCombo = hd.getAppCombo();
                TextField linkField = hd.getLinkField();
                if (appCombo == null || appCombo.getValue() == null || appCombo.getValue().trim().isEmpty()) return false;
                if (linkField == null || linkField.getText() == null || linkField.getText().trim().isEmpty()) return false;
                if (hd.getHour() == null) return false;
                if (hd.getMinute() == null) return false;
                if (!isValidDurationRange(hd.getDuration())) return false;
            } else {
                return false;
            }
        }
        return true;
    }
    
    private Object[] getAllRelevantProperties() {
        java.util.List<Object> props = new java.util.ArrayList<>();
        props.add(lessonTypeComboBox.valueProperty());
        props.add(frequencyComboBox.valueProperty());
        props.add(durationField.textProperty());
        props.add(cityField.textProperty());
        props.add(streetField.textProperty());
        props.add(capField.textProperty());
        props.add(applicationComboBox.valueProperty());
        props.add(meetingCodeField.textProperty());
        props.add(onlineDurationField.textProperty());

        // Aggiorna: aggiungi tutte le property osservabili dei giorni ibridi
        if (hybridDays != null) {
            for (HybridDay hd : hybridDays) {
                props.add(hd.enabledProperty());
                props.add(hd.modeProperty());
                props.add(hd.cityProperty());
                props.add(hd.streetProperty());
                props.add(hd.capProperty());
                props.add(hd.durationProperty());
                props.add(hd.hourProperty());
                props.add(hd.minuteProperty());
                props.add(hd.recipesProperty());
                if (hd.getAppCombo() != null) props.add(hd.getAppCombo().valueProperty());
                if (hd.getLinkField() != null) props.add(hd.getLinkField().textProperty());
            }
        }
        return props.toArray();
    }
    
    private void addCheckboxValidationListeners(BooleanBinding binding) {
        presenceDayCheckBoxes.values().forEach(cb -> 
            cb.selectedProperty().addListener((obs, oldVal, newVal) -> binding.invalidate())
        );
        onlineDayCheckBoxes.values().forEach(cb -> 
            cb.selectedProperty().addListener((obs, oldVal, newVal) -> binding.invalidate())
        );
    }
    
    // === UTILITY METHODS ===
    private void validateDaySelection(Map<String, CheckBox> checkBoxes, Label infoLabel) {
        if (frequencyComboBox.getValue() == null) return;
        
        int maxDays = getMaxDaysFromFrequency(frequencyComboBox.getValue());
        long selectedCount = checkBoxes.values().stream().mapToLong(cb -> cb.isSelected() ? 1 : 0).sum();
        
        if (selectedCount > maxDays) {
            deselectLastCheckbox(checkBoxes);
            showTemporaryError(infoLabel, maxDays);
        }
    }
    
    private void deselectLastCheckbox(Map<String, CheckBox> checkBoxes) {
        CheckBox lastSelected = null;
        for (CheckBox cb : checkBoxes.values()) {
            if (cb.isSelected()) lastSelected = cb;
        }
        if (lastSelected != null) lastSelected.setSelected(false);
    }
    
    private void showTemporaryError(Label label, int maxDays) {
        String originalText = label.getText();
        label.setText("⚠️ Massimo " + maxDays + " giorni selezionabili!");
        label.getStyleClass().add("error-label");
        
        new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(2), e -> {
                label.setText(originalText);
                label.getStyleClass().remove("error-label");
            })
        ).play();
    }
    
    private int getMaxDaysFromFrequency(String frequency) {
        if (frequency.contains("1 volta")) return 1;
        if (frequency.contains("2 volte")) return 2;
        if (frequency.contains("3 volte")) return 3;
        return 1;
    }
    
    private void clearDaySelections() {
        presenceDayCheckBoxes.values().forEach(cb -> cb.setSelected(false));
        onlineDayCheckBoxes.values().forEach(cb -> cb.setSelected(false));
    }
    
    private void setDayCheckBoxesEnabled(boolean enabled) {
        presenceDayCheckBoxes.values().forEach(cb -> cb.setDisable(!enabled));
        onlineDayCheckBoxes.values().forEach(cb -> cb.setDisable(!enabled));
    }
    
    private void updateDayInfoLabels(String frequency) {
        int maxDays = getMaxDaysFromFrequency(frequency);
        String message = maxDays == 1 ? "Seleziona 1 giorno della settimana" 
                                     : "Seleziona " + maxDays + " giorni della settimana";
        presenceDayInfoLabel.setText(message);
        onlineDayInfoLabel.setText(message);
    }
    
    private void resetDayInfoLabels() {
        String message = "Seleziona la frequenza prima di scegliere i giorni";
        presenceDayInfoLabel.setText(message);
        onlineDayInfoLabel.setText(message);
    }
    
    private String getFormattedTime(Spinner<Integer> hourSpinner, Spinner<Integer> minuteSpinner) {
        return String.format("%02d:%02d", hourSpinner.getValue(), minuteSpinner.getValue());
    }
    
    private List<String> getSelectedDays(Map<String, CheckBox> checkBoxes) {
        return checkBoxes.entrySet().stream()
            .filter(entry -> entry.getValue().isSelected())
            .map(Map.Entry::getKey)
            .collect(ArrayList::new, (list, item) -> list.add(item), ArrayList::addAll);
    }
    
    // === VALIDATION HELPERS ===
    private boolean isValidText(String text) {
        return text != null && !text.trim().isEmpty();
    }
    
    private boolean isValidPrice(String priceText) {
        if (!isValidText(priceText)) return false;
        try {
            return Double.parseDouble(priceText.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidParticipants(Integer value) {
        return value != null && value >= 10;
    }
    
    private boolean isValidCAP(String capText) {
        return isValidText(capText) && CAP_PATTERN.matcher(capText.trim()).matches();
    }
    
    private boolean isValidDurationRange(String durationText) {
        if (!isValidText(durationText)) return false;
        try {
            int duration = Integer.parseInt(durationText.trim());
            return duration >= 1 && duration <= 480;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidDescription(String text) {
        return text != null && text.trim().length() >= 1 && text.length() <= 60;
    }
    
    private boolean hasCorrectNumberOfDays(Map<String, CheckBox> checkBoxes) {
        if (frequencyComboBox.getValue() == null) return false;
        
        int expectedDays = getMaxDaysFromFrequency(frequencyComboBox.getValue());
        long selectedDays = checkBoxes.values().stream().mapToLong(cb -> cb.isSelected() ? 1 : 0).sum();
        
        return selectedDays == expectedDays;
    }
    
    // === PUBLIC ACTIONS ===
    public void onLessonTypeChanged() {
        String selectedType = lessonTypeComboBox.getValue();
        boolean isPresence = "In presenza".equals(selectedType);
        boolean isOnline = "Telematica".equals(selectedType);
        boolean isHybrid = "Entrambi".equals(selectedType);
        presenceDetailsSection.setVisible(isPresence);
        presenceDetailsSection.setManaged(isPresence);
        onlineDetailsSection.setVisible(isOnline);
        onlineDetailsSection.setManaged(isOnline);
        hybridDetailsSection.setVisible(isHybrid);
        hybridDetailsSection.setManaged(isHybrid);
        // Fix macOS: assicurati che la sezione sia visibile/managed PRIMA di popolare i giorni
        if (isHybrid) {
            hybridDetailsSection.setVisible(true);
            hybridDetailsSection.setManaged(true);
            setupHybridDaysUI();
        }
    }

    private void setupHybridDaysUI() {
        hybridDaysContainer.getChildren().clear();
        hybridDays.clear();
        String freq = frequencyComboBox.getValue();
        int maxDays = getMaxDaysFromFrequency(freq != null ? freq : "1 volta a settimana");
        if (maxDays < 2) {
            hybridErrorLabel.setText("La modalità 'Entrambi' richiede almeno 2 giorni a settimana.");
            hybridErrorLabel.setVisible(true);
            hybridErrorLabel.setManaged(true);
            return;
        } else {
            hybridErrorLabel.setVisible(false);
            hybridErrorLabel.setManaged(false);
        }
        FlowPane daysPane = new FlowPane();
        daysPane.setHgap(12);
        daysPane.setVgap(8);
        daysPane.setPrefWrapLength(600);
        List<HybridDay> tempHybridDays = new ArrayList<>();
        for (String day : WEEK_DAYS) {
            VBox dayBox = new VBox(6);
            dayBox.setPadding(new Insets(4, 8, 4, 8));
            dayBox.setStyle("-fx-background-color: #f4f7fb; -fx-border-color: #e1e5e9; -fx-border-radius: 8; -fx-background-radius: 8;");
            CheckBox dayCheck = new CheckBox(day);
            ComboBox<String> modeCombo = new ComboBox<>();
            modeCombo.getItems().addAll("Presenza", "Telematica");
            modeCombo.setDisable(true);
            VBox detailsBox = new VBox(8);
            detailsBox.setVisible(false);
            detailsBox.setManaged(false);
            dayBox.getChildren().addAll(dayCheck, modeCombo, detailsBox);
            daysPane.getChildren().add(dayBox);
            HybridDay hd = new HybridDay(day, null);
            tempHybridDays.add(hd);

            dayCheck.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                long count = tempHybridDays.stream().filter(HybridDay::isEnabled).count();
                if (isSelected) {
                    if (count >= maxDays) {
                        dayCheck.setSelected(false);
                        hybridErrorLabel.setText("Puoi selezionare solo " + maxDays + " giorni.");
                        hybridErrorLabel.setVisible(true);
                        hybridErrorLabel.setManaged(true);
                        return;
                    }
                    modeCombo.setDisable(false);
                    modeCombo.setValue("Presenza");
                    detailsBox.setVisible(true);
                    detailsBox.setManaged(true);
                    hd.setEnabled(true);
                    hd.setMode("Presenza");
                } else {
                    modeCombo.setDisable(true);
                    modeCombo.setValue(null);
                    detailsBox.setVisible(false);
                    detailsBox.setManaged(false);
                    detailsBox.getChildren().clear();
                    hd.setEnabled(false);
                    hd.setMode(null);
                }
                hybridErrorLabel.setVisible(false);
                hybridErrorLabel.setManaged(false);
            });

            modeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (!dayCheck.isSelected()) return;
                // Reset campi non rilevanti quando si cambia modalità
                if ("Presenza".equals(newVal)) {
                    // Svuota campi telematica
                    if (hd.getAppCombo() != null) hd.getAppCombo().setValue(null);
                    if (hd.getLinkField() != null) hd.getLinkField().setText("");
                } else if ("Telematica".equals(newVal)) {
                    // Svuota campi presenza e ricette
                    hd.setCity("");
                    hd.setStreet("");
                    hd.setCap("");
                    hd.setRecipes(new java.util.ArrayList<>());
                }
                // Svuota sempre orario e durata (obbligatori in entrambi)
                hd.setHour(null);
                hd.setMinute(null);
                hd.setDuration("");
                invalidateHybridValidation();
                hd.setMode(newVal);
                detailsBox.getChildren().clear();
                if ("Presenza".equals(newVal)) {
                    // Orario, durata, città, via, cap
                    HBox orarioBox = new HBox(8);
                    Label orarioLabel = new Label("Orario Inizio:");
                    Spinner<Integer> hourSpinner = new Spinner<>(6, 23, 18);
                    Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0, 15);
                    hourSpinner.setEditable(true);
                    minuteSpinner.setEditable(true);
                    orarioBox.getChildren().addAll(orarioLabel, hourSpinner, new Label(":"), minuteSpinner);
                    // Durata
                    HBox durataBox = new HBox(8);
                    Label durataLabel = new Label("Durata (minuti):");
                    TextField durataField = new TextField();
                    durataField.setPromptText("es. 120");
                    // Limita input a numeri e max 480
                    durataField.textProperty().addListener((o, ov, nv) -> {
                        String filtered = nv.replaceAll("[^\\d]", "");
                        if (!filtered.equals(nv)) durataField.setText(filtered);
                        if (!filtered.isEmpty()) {
                            try {
                                int val = Integer.parseInt(filtered);
                                if (val > 480) durataField.setText("480");
                            } catch (NumberFormatException e) { durataField.setText(""); }
                        }
                        hd.setDuration(durataField.getText());
                    });
                    durataBox.getChildren().addAll(durataLabel, durataField);
                    // Indirizzo
                    HBox indirizzoBox = new HBox(8);
                    TextField cityField = new TextField(); cityField.setPromptText("Città");
                    TextField streetField = new TextField(); streetField.setPromptText("Via");
                    TextField capField = new TextField(); capField.setPromptText("CAP");
                    // Limita input CAP a numeri e max 5 cifre
                    capField.textProperty().addListener((o, ov, nv) -> {
                        String filtered = nv.replaceAll("[^\\d]", "");
                        if (filtered.length() > 5) filtered = filtered.substring(0, 5);
                        if (!filtered.equals(nv)) capField.setText(filtered);
                        hd.setCap(filtered);
                    });
                    // Solo lettere per città (ibrido)
                    cityField.textProperty().addListener((o, ov, nv) -> {
                        String filtered = nv.replaceAll("[^a-zA-ZàèéìòùÀÈÉÌÒÙ' ]", "");
                        if (!filtered.equals(nv)) cityField.setText(filtered);
                        hd.setCity(filtered);
                    });
                    streetField.textProperty().addListener((o, ov, nv) -> hd.setStreet(nv));
                    indirizzoBox.getChildren().addAll(new Label("Città:"), cityField, new Label("Via:"), streetField, new Label("CAP:"), capField);
                    // Ricette
                    VBox ricetteBox = new VBox(5);
                    Button addRecipeBtn = new Button("+ Aggiungi Ricetta");
                    addRecipeBtn.getStyleClass().add("add-recipe-button");
                    List<Recipe> dayRecipes = new ArrayList<>();
                    addRecipeBtn.setOnAction(e -> {
                        Recipe r = new Recipe();
                        dayRecipes.add(r);
                        VBox recipeBox = createRecipeBox(r, dayRecipes, ricetteBox);
                        ricetteBox.getChildren().add(recipeBox);
                    });
                    detailsBox.getChildren().addAll(orarioBox, durataBox, indirizzoBox, new Label("Ricette per questo giorno:"), ricetteBox, addRecipeBtn);
                    hd.setRecipes(dayRecipes);
                    // Bind fields to HybridDay
                    hourSpinner.valueProperty().addListener((o, ov, nv) -> hd.setHour(nv));
                    minuteSpinner.valueProperty().addListener((o, ov, nv) -> hd.setMinute(nv));
                    // Forza il layout per garantire la visualizzazione su macOS
                    javafx.application.Platform.runLater(() -> hybridDaysContainer.requestLayout());
                } else if ("Telematica".equals(newVal)) {
                    // Orario, durata
                    HBox orarioBox = new HBox(8);
                    Label orarioLabel = new Label("Orario Inizio:");
                    Spinner<Integer> hourSpinner = new Spinner<>(6, 23, 18);
                    Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0, 15);
                    hourSpinner.setEditable(true);
                    minuteSpinner.setEditable(true);
                    orarioBox.getChildren().addAll(orarioLabel, hourSpinner, new Label(":"), minuteSpinner);
                    // Durata
                    HBox durataBox = new HBox(8);
                    Label durataLabel = new Label("Durata (minuti):");
                    TextField durataField = new TextField();
                    durataField.setPromptText("es. 120");
                    // Limita input a numeri e max 480
                    durataField.textProperty().addListener((o, ov, nv) -> {
                        String filtered = nv.replaceAll("[^\\d]", "");
                        if (!filtered.equals(nv)) durataField.setText(filtered);
                        if (!filtered.isEmpty()) {
                            try {
                                int val = Integer.parseInt(filtered);
                                if (val > 480) durataField.setText("480");
                            } catch (NumberFormatException e) { durataField.setText(""); }
                        }
                        hd.setDuration(durataField.getText());
                    });
                    durataBox.getChildren().addAll(durataLabel, durataField);
                    // App/link
                    Label appLabel = new Label("Applicazione:");
                    ComboBox<String> appCombo = new ComboBox<>();
                    appCombo.getItems().addAll(applicationComboBox.getItems());
                    appCombo.getStyleClass().add("combo-box");
                    Label linkLabel = new Label("Codice/Link:");
                    TextField linkField = new TextField();
                    linkField.getStyleClass().add("form-field");
                    detailsBox.getChildren().addAll(orarioBox, durataBox, appLabel, appCombo, linkLabel, linkField);
                    hd.setAppCombo(appCombo);
                    hd.setLinkField(linkField);
                    // Bind fields to HybridDay
                    hourSpinner.valueProperty().addListener((o, ov, nv) -> hd.setHour(nv));
                    minuteSpinner.valueProperty().addListener((o, ov, nv) -> hd.setMinute(nv));
                }
            });
        }
        hybridDays.clear();
        hybridDays.addAll(tempHybridDays);
        hybridDaysContainer.getChildren().clear();
        hybridDaysContainer.getChildren().add(daysPane);
        // Forza il layout su tutte le piattaforme (fix macOS)
        javafx.application.Platform.runLater(() -> hybridDaysContainer.requestLayout());
    }

    // Ricette in presenza: ripristina la UI completa
    public void addNewRecipe() {
        Recipe recipe = new Recipe();
        recipes.add(recipe);
        VBox recipeBox = createRecipeBox(recipe, recipes, recipesContainer);
        recipesContainer.getChildren().add(recipeBox);
        // Invalida la validazione quando aggiungi una ricetta
        if (detailsValidBinding != null) detailsValidBinding.invalidate();
    }

    // Crea la UI per una ricetta (nome + ingredienti)
    private VBox createRecipeBox(Recipe recipe, List<Recipe> recipeList, VBox parentContainer) {
        VBox box = new VBox(8);
        box.getStyleClass().add("recipe-container");
        TextField nameField = new TextField();
        nameField.setPromptText("Nome ricetta");
        nameField.getStyleClass().add("form-field");
        // Solo lettere e spazi per nome ricetta
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal.replaceAll("[^a-zA-ZàèéìòùÀÈÉÌÒÙ' ]", "");
            if (!filtered.equals(newVal)) nameField.setText(filtered);
            recipe.setName(filtered);
            // Invalida la validazione per abilitare il tasto crea
            if (detailsValidBinding != null) detailsValidBinding.invalidate();
        });
        Button removeBtn = new Button("Rimuovi");
        removeBtn.getStyleClass().add("remove-button");
        removeBtn.setOnAction(e -> {
            recipeList.remove(recipe);
            parentContainer.getChildren().remove(box);
            // Invalida la validazione quando rimuovi una ricetta
            if (detailsValidBinding != null) detailsValidBinding.invalidate();
        });
        HBox header = new HBox(10, new Label("Ricetta:"), nameField, removeBtn);
        VBox ingredientsBox = createIngredientsSection(recipe);
        box.getChildren().addAll(header, ingredientsBox);
        return box;
    }

    // UI per ingredienti di una ricetta
    private VBox createIngredientsSection(Recipe recipe) {
        VBox ingredientsBox = new VBox(5);
        for (Ingredient ing : recipe.getIngredients()) {
            HBox row = createIngredientBox(ing, recipe, ingredientsBox);
            ingredientsBox.getChildren().add(row);
        }
        Button addIngBtn = new Button("+ Ingrediente");
        addIngBtn.getStyleClass().add("add-ingredient-button");
        addIngBtn.setOnAction(e -> addIngredient(recipe, ingredientsBox));
        ingredientsBox.getChildren().add(addIngBtn);
        return ingredientsBox;
    }

    private void addIngredient(Recipe recipe, VBox ingredientsBox) {
        Ingredient ing = new Ingredient();
        recipe.addIngredient(ing);
        HBox row = createIngredientBox(ing, recipe, ingredientsBox);
        ingredientsBox.getChildren().add(ingredientsBox.getChildren().size() - 1, row);
        // Invalida la validazione quando aggiungi un ingrediente
        if (detailsValidBinding != null) detailsValidBinding.invalidate();
    }

    private HBox createIngredientBox(Ingredient ingredient, Recipe recipe, VBox ingredientsBox) {
        TextField nameField = new TextField();
        nameField.setPromptText("Ingrediente");
        nameField.getStyleClass().add("ingredient-name-field");
        nameField.getStyleClass().add("form-field"); // Uniforma stile
        // Solo lettere e spazi per nome ingrediente
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal.replaceAll("[^a-zA-ZàèéìòùÀÈÉÌÒÙ' ]", "");
            if (!filtered.equals(newVal)) nameField.setText(filtered);
            ingredient.setName(filtered);
            if (detailsValidBinding != null) detailsValidBinding.invalidate();
        });

        TextField qtyField = new TextField();
        qtyField.setPromptText("Quantità");
        qtyField.getStyleClass().add("ingredient-quantity-field");
        qtyField.getStyleClass().add("form-field"); // Uniforma stile
        // Permetti solo numeri
        qtyField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal.replaceAll("[^\\d]", "");
            if (!filtered.equals(newVal)) qtyField.setText(filtered);
            ingredient.setQuantity(filtered);
            if (detailsValidBinding != null) detailsValidBinding.invalidate();
        });

        ComboBox<String> unitCombo = new ComboBox<>();
        unitCombo.getItems().addAll("g", "kg", "ml", "l", "pz");
        unitCombo.setPromptText("Unità");
        unitCombo.getStyleClass().add("ingredient-unit-combo");
        unitCombo.getStyleClass().add("combo-box"); // Uniforma stile
        unitCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            ingredient.setUnit(newVal);
            if (detailsValidBinding != null) detailsValidBinding.invalidate();
        });

        Button removeBtn = new Button("-");
        removeBtn.getStyleClass().add("remove-ingredient-button");
        removeBtn.setOnAction(e -> {
            recipe.removeIngredient(ingredient);
            ingredientsBox.getChildren().removeIf(node -> node == removeBtn.getParent());
            // Invalida la validazione quando rimuovi un ingrediente
            if (detailsValidBinding != null) detailsValidBinding.invalidate();
        });
        HBox row = new HBox(8, nameField, qtyField, unitCombo, removeBtn);
        row.getStyleClass().add("ingredient-row");
        return row;
    }
    
    // === DATA TRANSFER OBJECTS ===
    public static class CourseData {
        public String name, description, lessonType, frequency, street, cap, city, application, meetingCode, timeSlot;
        public int maxParticipants, duration;
        public double price;
        public LocalDate startDate, endDate;
        public List<String> days;
        public List<Recipe> recipes;
        public List<HybridDayData> hybridDays;
        public static class HybridDayData {
            public String day;
            public String mode;
            public List<Recipe> recipes;
            public String application;
            public String meetingCode;
        }
    }
    public static class Recipe {
        public String name;
        public final ObservableList<Ingredient> ingredients = FXCollections.observableArrayList();
        public VBox ingredientsContainer;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public ObservableList<Ingredient> getIngredients() { return ingredients; }
        public void addIngredient(Ingredient ingredient) { ingredients.add(ingredient); }
        public void removeIngredient(Ingredient ingredient) { ingredients.remove(ingredient); }
        public VBox getIngredientsContainer() { return ingredientsContainer; }
        public void setIngredientsContainer(VBox container) { this.ingredientsContainer = container; }
    }
    public static class Ingredient {
        public String name, quantity, unit;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getQuantity() { return quantity; }
        public void setQuantity(String quantity) { this.quantity = quantity; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }
    // Classe di supporto per la modalità ibrida
    private static class HybridDay {
        private final String day;
        private final javafx.beans.property.BooleanProperty enabled = new javafx.beans.property.SimpleBooleanProperty(false);
        private final javafx.beans.property.StringProperty mode = new javafx.beans.property.SimpleStringProperty();
        private final javafx.beans.property.ObjectProperty<java.util.List<Recipe>> recipes = new javafx.beans.property.SimpleObjectProperty<>();
        private final javafx.beans.property.IntegerProperty hour = new javafx.beans.property.SimpleIntegerProperty();
        private final javafx.beans.property.IntegerProperty minute = new javafx.beans.property.SimpleIntegerProperty();
        private final javafx.beans.property.StringProperty duration = new javafx.beans.property.SimpleStringProperty();
        private final javafx.beans.property.StringProperty city = new javafx.beans.property.SimpleStringProperty();
        private final javafx.beans.property.StringProperty street = new javafx.beans.property.SimpleStringProperty();
        private final javafx.beans.property.StringProperty cap = new javafx.beans.property.SimpleStringProperty();
        private final javafx.beans.property.ObjectProperty<ComboBox<String>> appCombo = new javafx.beans.property.SimpleObjectProperty<>();
        private final javafx.beans.property.ObjectProperty<TextField> linkField = new javafx.beans.property.SimpleObjectProperty<>();

        public HybridDay(String day, String mode) {
            this.day = day;
            setMode(mode);
        }
        public String getDay() { return day; }
        public boolean isEnabled() { return enabled.get(); }
        public void setEnabled(boolean enabled) { this.enabled.set(enabled); }
        public javafx.beans.property.BooleanProperty enabledProperty() { return enabled; }
        public String getMode() { return mode.get(); }
        public void setMode(String mode) { this.mode.set(mode); }
        public javafx.beans.property.StringProperty modeProperty() { return mode; }
        public java.util.List<Recipe> getRecipes() { return recipes.get(); }
        public void setRecipes(java.util.List<Recipe> recipes) { this.recipes.set(recipes); }
        public javafx.beans.property.ObjectProperty<java.util.List<Recipe>> recipesProperty() { return recipes; }
        public Integer getHour() { return hour.get(); }
        public void setHour(Integer hour) { this.hour.set(hour != null ? hour : 0); }
        public javafx.beans.property.IntegerProperty hourProperty() { return hour; }
        public Integer getMinute() { return minute.get(); }
        public void setMinute(Integer minute) { this.minute.set(minute != null ? minute : 0); }
        public javafx.beans.property.IntegerProperty minuteProperty() { return minute; }
        public String getDuration() { return duration.get(); }
        public void setDuration(String duration) { this.duration.set(duration); }
        public javafx.beans.property.StringProperty durationProperty() { return duration; }
        public String getCity() { return city.get(); }
        public void setCity(String city) { this.city.set(city); }
        public javafx.beans.property.StringProperty cityProperty() { return city; }
        public String getStreet() { return street.get(); }
        public void setStreet(String street) { this.street.set(street); }
        public javafx.beans.property.StringProperty streetProperty() { return street; }
        public String getCap() { return cap.get(); }
        public void setCap(String cap) { this.cap.set(cap); }
        public javafx.beans.property.StringProperty capProperty() { return cap; }
        public ComboBox<String> getAppCombo() { return appCombo.get(); }
        public void setAppCombo(ComboBox<String> appCombo) { this.appCombo.set(appCombo); }
        public javafx.beans.property.ObjectProperty<ComboBox<String>> appComboProperty() { return appCombo; }
        public TextField getLinkField() { return linkField.get(); }
        public void setLinkField(TextField linkField) { this.linkField.set(linkField); }
        public javafx.beans.property.ObjectProperty<TextField> linkFieldProperty() { return linkField; }
    }
    // === HYBRID VALIDATION ===
    private boolean validateHybridDays() {
        for (HybridDay hd : hybridDays) {
            if (!hd.isEnabled()) continue;
            String mode = hd.getMode();
            if ("Presenza".equals(mode)) {
                List<Recipe> recipes = hd.getRecipes();
                if (recipes == null || recipes.isEmpty()) return false;
                for (Recipe r : recipes) {
                    if (r.getName() == null || r.getName().trim().isEmpty()) return false;
                    ObservableList<Ingredient> ings = r.getIngredients();
                    if (ings == null || ings.isEmpty()) return false;
                    for (Ingredient ing : ings) {
                        if (ing.getName() == null || ing.getName().trim().isEmpty()) return false;
                        if (ing.getQuantity() == null || ing.getQuantity().trim().isEmpty()) return false;
                        if (ing.getUnit() == null || ing.getUnit().trim().isEmpty()) return false;
                    }
                }
            } else if ("Telematica".equals(mode)) {
                ComboBox<String> appCombo = hd.getAppCombo();
                TextField linkField = hd.getLinkField();
                if (appCombo == null || appCombo.getValue() == null || appCombo.getValue().trim().isEmpty()) return false;
                if (linkField == null || linkField.getText() == null || linkField.getText().trim().isEmpty()) return false;
            } else {
                return false;
            }
        }
        return true;
    }
    // === PUBLIC API FOR BOUNDARY ===
    public void selectImage() {
        // TODO: implementa selezione immagine
    }
    public void createCourse() {
        // Validazione tipi di cucina: almeno uno selezionato
        String cucina1 = cuisineTypeComboBox1.getValue();
        String cucina2 = cuisineTypeComboBox2.getValue();
        if ((cucina1 == null || cucina1.trim().isEmpty()) && (cucina2 == null || cucina2.trim().isEmpty())) {
            cuisineTypeErrorLabel.setText("Seleziona almeno un tipo di cucina.");
            cuisineTypeErrorLabel.setVisible(true);
            return;
        } else {
            cuisineTypeErrorLabel.setVisible(false);
        }
        // TODO: qui va la logica di salvataggio del corso
        Stage stage = (Stage) courseNameField.getScene().getWindow();
        SuccessDialogUtils.showGenericSuccessDialog(stage, "Corso creato con successo!", "Il corso è stato creato correttamente.");
        goToHomepage();
    }
    public void goToHomepage() {
        try {
            Stage stage = (Stage) courseNameField.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/homepagechef.fxml", "UninaFoodLab - Home Chef");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void goToMonthlyReport() {
        try {
            Stage stage = (Stage) courseNameField.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/monthlyreport.fxml", "UninaFoodLab - Resoconto Mensile");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void goToAccountManagement() {
        try {
            Stage stage = (Stage) courseNameField.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/accountmanagementchef.fxml", "UninaFoodLab - Gestione Account");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void LogoutClick() {
        try {
            Stage stage = (Stage) courseNameField.getScene().getWindow();
            LogoutDialogBoundary dialogBoundary = SceneSwitcher.showLogoutDialog(stage);
            if (dialogBoundary != null && dialogBoundary.isConfirmed()) {
                SceneSwitcher.switchToLogin(stage, "/fxml/loginpage.fxml", "UninaFoodLab - Login");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Conta le parole nella descrizione
    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        return text.trim().split("\\s+").length;
    }
}

