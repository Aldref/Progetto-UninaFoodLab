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
    
    // === DATA ===
    private final ObservableList<Recipe> recipes = FXCollections.observableArrayList();
    private final Map<String, CheckBox> presenceDayCheckBoxes = new HashMap<>();
    private final Map<String, CheckBox> onlineDayCheckBoxes = new HashMap<>();
    private Label presenceDayInfoLabel, onlineDayInfoLabel;
    
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
                                TextField onlineDurationField) {
        
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
    }
    
    // === INITIALIZATION ===
    public void initialize() {
        initializeData();
        setupUI();
        setupValidation();
        addNewRecipe();
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
        lessonTypeComboBox.getItems().addAll("In presenza", "Telematica");
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
        addTextValidator(durationField, "[^\\d]", null);
        addTextValidator(onlineDurationField, "[^\\d]", null);
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
        BooleanBinding detailsValid = createDetailsValidation();
        
        addCheckboxValidationListeners(detailsValid);
        createButton.disableProperty().bind(basicValid.not().or(detailsValid.not()));
    }
    
    private BooleanBinding createBasicValidation() {
        return Bindings.createBooleanBinding(() -> 
            isValidText(courseNameField.getText()) &&
            isValidText(descriptionArea.getText()) &&
            startDatePicker.getValue() != null &&
            endDatePicker.getValue() != null &&
            frequencyComboBox.getValue() != null &&
            lessonTypeComboBox.getValue() != null &&
            isValidPrice(priceField.getText()),
            
            courseNameField.textProperty(),
            descriptionArea.textProperty(),
            startDatePicker.valueProperty(),
            endDatePicker.valueProperty(),
            frequencyComboBox.valueProperty(),
            lessonTypeComboBox.valueProperty(),
            priceField.textProperty()
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
            }
            return false;
        }, properties);
    }
    
    private boolean validatePresenceDetails() {
        return hasCorrectNumberOfDays(presenceDayCheckBoxes) &&
               isValidDurationRange(durationField.getText()) &&
               isValidText(cityField.getText()) &&
               isValidText(streetField.getText()) &&
               isValidCAP(capField.getText());
    }
    
    private boolean validateOnlineDetails() {
        return applicationComboBox.getValue() != null &&
               isValidText(meetingCodeField.getText()) &&
               hasCorrectNumberOfDays(onlineDayCheckBoxes) &&
               isValidDurationRange(onlineDurationField.getText());
    }
    
    private Object[] getAllRelevantProperties() {
        return new Object[]{
            lessonTypeComboBox.valueProperty(), frequencyComboBox.valueProperty(),
            durationField.textProperty(), cityField.textProperty(), 
            streetField.textProperty(), capField.textProperty(),
            applicationComboBox.valueProperty(), meetingCodeField.textProperty(),
            onlineDurationField.textProperty()
        };
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
        
        presenceDetailsSection.setVisible(isPresence);
        presenceDetailsSection.setManaged(isPresence);
        onlineDetailsSection.setVisible(isOnline);
        onlineDetailsSection.setManaged(isOnline);
    }
    
    public void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Immagine del Corso");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));
        
        Stage stage = (Stage) courseImageView.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            try {
                courseImageView.setImage(new Image(selectedFile.toURI().toString()));
            } catch (Exception e) {
                showAlert("Errore", "Impossibile caricare l'immagine selezionata.");
            }
        }
    }
    
    public void createCourse() {
        try {
            CourseData courseData = collectCourseData();
            
            // TODO: Sostituire con chiamata al DAO
            // boolean success = CourseDAO.createCourse(courseData);
            // if (success) {
                showCourseCreationSuccessDialog();
            // } else {
            //     showAlert("Errore", "Errore durante il salvataggio del corso.");
            // }
            
        } catch (Exception e) {
            showAlert("Errore", "Si è verificato un errore durante la creazione del corso.");
            e.printStackTrace();
        }
    }
    
    private CourseData collectCourseData() {
        String lessonType = lessonTypeComboBox.getValue();
        
        CourseData data = new CourseData();
        data.name = courseNameField.getText().trim();
        data.description = descriptionArea.getText().trim();
        data.startDate = startDatePicker.getValue();
        data.endDate = endDatePicker.getValue();
        data.frequency = frequencyComboBox.getValue();
        data.lessonType = lessonType;
        data.maxParticipants = maxParticipantsSpinner.getValue();
        data.price = Double.parseDouble(priceField.getText().trim());
        data.recipes = new ArrayList<>(recipes);
        
        if ("In presenza".equals(lessonType)) {
            data.timeSlot = getFormattedTime(presenceHourSpinner, presenceMinuteSpinner);
            data.duration = Integer.parseInt(durationField.getText().trim());
            data.location = String.format("%s, %s, %s", 
                streetField.getText().trim(), cityField.getText().trim(), capField.getText().trim());
            data.selectedDays = getSelectedDays(presenceDayCheckBoxes);
        } else if ("Telematica".equals(lessonType)) {
            data.timeSlot = getFormattedTime(onlineHourSpinner, onlineMinuteSpinner);
            data.duration = Integer.parseInt(onlineDurationField.getText().trim());
            data.platform = applicationComboBox.getValue();
            data.meetingCode = meetingCodeField.getText().trim();
            data.selectedDays = getSelectedDays(onlineDayCheckBoxes);
        }
        
        return data;
    }
    
    // === RECIPE MANAGEMENT ===
    public void addNewRecipe() {
        Recipe recipe = new Recipe();
        recipes.add(recipe);
        recipesContainer.getChildren().add(createRecipeBox(recipe));
    }
    
    private VBox createRecipeBox(Recipe recipe) {
        VBox recipeBox = new VBox(15);
        recipeBox.getStyleClass().add("recipe-container");
        recipeBox.setPadding(new Insets(15));
        
        HBox headerBox = createRecipeHeader(recipe, recipeBox);
        VBox ingredientsBox = createIngredientsSection(recipe);
        
        recipeBox.getChildren().addAll(headerBox, ingredientsBox);
        addIngredient(recipe);
        return recipeBox;
    }
    
    private HBox createRecipeHeader(Recipe recipe, VBox recipeBox) {
        HBox headerBox = new HBox(10);
        headerBox.setStyle("-fx-alignment: center-left;");
        
        Label recipeLabel = new Label("Nome Ricetta:");
        recipeLabel.getStyleClass().add("field-label");
        
        TextField recipeNameField = new TextField();
        recipeNameField.setPromptText("Inserisci il nome della ricetta");
        recipeNameField.getStyleClass().add("form-field");
        recipeNameField.setPrefWidth(250);
        recipeNameField.textProperty().addListener((obs, oldVal, newVal) -> recipe.setName(newVal));
        
        Button removeRecipeBtn = new Button("✕");
        removeRecipeBtn.getStyleClass().add("remove-button");
        removeRecipeBtn.setOnAction(e -> removeRecipe(recipeBox, recipe));
        
        headerBox.getChildren().addAll(recipeLabel, recipeNameField, removeRecipeBtn);
        return headerBox;
    }
    
    private VBox createIngredientsSection(Recipe recipe) {
        VBox ingredientsBox = new VBox(10);
        Label ingredientsLabel = new Label("Ingredienti:");
        ingredientsLabel.getStyleClass().add("field-label");
        
        VBox ingredientsList = new VBox(8);
        recipe.setIngredientsContainer(ingredientsList);
        
        Button addIngredientBtn = new Button("+ Aggiungi Ingrediente");
        addIngredientBtn.getStyleClass().add("add-ingredient-button");
        addIngredientBtn.setOnAction(e -> addIngredient(recipe));
        
        ingredientsBox.getChildren().addAll(ingredientsLabel, ingredientsList, addIngredientBtn);
        return ingredientsBox;
    }
    
    private void addIngredient(Recipe recipe) {
        Ingredient ingredient = new Ingredient();
        recipe.addIngredient(ingredient);
        recipe.getIngredientsContainer().getChildren().add(createIngredientBox(ingredient, recipe));
    }
    
    private HBox createIngredientBox(Ingredient ingredient, Recipe recipe) {
        HBox ingredientBox = new HBox(10);
        ingredientBox.getStyleClass().add("ingredient-row");
        
        TextField nameField = createIngredientNameField(ingredient);
        TextField quantityField = createIngredientQuantityField(ingredient);
        ComboBox<String> unitCombo = createIngredientUnitCombo(ingredient);
        Button removeBtn = createRemoveIngredientButton(ingredientBox, recipe, ingredient);
        
        ingredientBox.getChildren().addAll(nameField, quantityField, unitCombo, removeBtn);
        return ingredientBox;
    }
    
    private TextField createIngredientNameField(Ingredient ingredient) {
        TextField nameField = new TextField();
        nameField.setPromptText("Nome ingrediente");
        nameField.getStyleClass().addAll("form-field", "ingredient-name-field");
        nameField.textProperty().addListener((obs, oldVal, newVal) -> ingredient.setName(newVal));
        return nameField;
    }
    
    private TextField createIngredientQuantityField(Ingredient ingredient) {
        TextField quantityField = new TextField();
        quantityField.setPromptText("Qta");
        quantityField.getStyleClass().addAll("form-field", "ingredient-quantity-field");
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
            String normalized = newVal.replace(",", ".");
            if (!normalized.matches("\\d*\\.?\\d{0,3}")) {
                String filtered = normalized.replaceAll("[^\\d.]", "");
                int dotIndex = filtered.indexOf('.');
                if (dotIndex != -1) {
                    String beforeDot = filtered.substring(0, dotIndex);
                    String afterDot = filtered.substring(dotIndex + 1).replaceAll("\\.", "");
                    if (afterDot.length() > 3) afterDot = afterDot.substring(0, 3);
                    filtered = beforeDot + "." + afterDot;
                }
                quantityField.setText(filtered);
            } else {
                ingredient.setQuantity(normalized);
            }
        });
        return quantityField;
    }
    
    private ComboBox<String> createIngredientUnitCombo(Ingredient ingredient) {
        ComboBox<String> unitCombo = new ComboBox<>();
        // TODO: Caricare da database
        unitCombo.getItems().addAll("g", "kg", "ml", "l", "pz", "cucchiai", "cucchiaini", "tazze", "q.b.");
        unitCombo.setPromptText("Unità");
        unitCombo.getStyleClass().addAll("form-field", "ingredient-unit-combo");
        unitCombo.valueProperty().addListener((obs, oldVal, newVal) -> ingredient.setUnit(newVal));
        return unitCombo;
    }
    
    private Button createRemoveIngredientButton(HBox ingredientBox, Recipe recipe, Ingredient ingredient) {
        Button removeBtn = new Button("✕");
        removeBtn.getStyleClass().add("remove-ingredient-button");
        removeBtn.setOnAction(e -> {
            recipe.removeIngredient(ingredient);
            recipe.getIngredientsContainer().getChildren().remove(ingredientBox);
        });
        return removeBtn;
    }
    
    private void removeRecipe(VBox recipeBox, Recipe recipe) {
        if (recipes.size() > 1) {
            recipes.remove(recipe);
            recipesContainer.getChildren().remove(recipeBox);
        } else {
            showAlert("Attenzione", "Deve essere presente almeno una ricetta.");
        }
    }
    
    // === NAVIGATION ===
    public void goToHomepage() {
        navigateTo("/fxml/homepagechef.fxml", "UninaFoodLab - Homepage");
    }
    
    public void goToMonthlyReport() {
        navigateTo("/fxml/monthlyreport.fxml", "UninaFoodLab - Resoconto Mensile");
    }
    
    public void goToAccountManagement() {
        navigateTo("/fxml/accountmanagementchef.fxml", "UninaFoodLab - Gestione Account");
    }
    
    private void navigateTo(String fxml, String title) {
        try {
            Stage stage = (Stage) courseNameField.getScene().getWindow();
            SceneSwitcher.switchScene(stage, fxml, title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void LogoutClick() {
        try {
            Stage stage = (Stage) courseNameField.getScene().getWindow();
            LogoutDialogBoundary dialogBoundary = SceneSwitcher.showLogoutDialog(stage);
            if (dialogBoundary.isConfirmed()) {
                SceneSwitcher.switchToLogin(stage, "/fxml/loginpage.fxml", "UninaFoodLab - Login");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // === UTILITY ===
    private void showCourseCreationSuccessDialog() {
        try {
            Stage parentStage = (Stage) courseNameField.getScene().getWindow();
            String courseName = courseNameField.getText();
            SuccessDialogUtils.showGenericSuccessDialog(
                parentStage, 
                "Corso Creato con Successo!", 
                "Il corso \"" + courseName + "\" è stato creato e pubblicato con successo."
            );
            goToHomepage();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Successo", "Il corso \"" + courseNameField.getText() + "\" è stato creato con successo!");
            goToHomepage();
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
        public String name, description, frequency, lessonType, timeSlot, location, platform, meetingCode;
        public LocalDate startDate, endDate;
        public int maxParticipants, duration;
        public double price;
        public List<String> selectedDays;
        public List<Recipe> recipes;
    }
    
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