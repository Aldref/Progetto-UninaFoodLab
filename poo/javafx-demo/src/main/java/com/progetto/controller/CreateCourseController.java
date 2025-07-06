package com.progetto.controller;

import com.progetto.Entity.entityDao.BarraDiRicercaDao;
import com.progetto.Entity.entityDao.CorsoDao;
import com.progetto.Entity.entityDao.ricettaDao;
import com.progetto.Entity.entityDao.IngredientiDao;
import com.progetto.Entity.EntityDto.Ricetta;
import com.progetto.Entity.EntityDto.Ingredienti;
import com.progetto.Entity.EntityDto.Corso;
import com.progetto.Entity.EntityDto.Sessione;
import com.progetto.Entity.EntityDto.SessioneOnline;
import com.progetto.Entity.EntityDto.SessioniInPresenza;
import com.progetto.Entity.entityDao.SessioneOnlineDao;
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;
import com.progetto.utils.SuccessDialogUtils;
import com.progetto.Entity.EntityDto.Chef;
import com.progetto.Entity.entityDao.ChefDao;
import com.progetto.Entity.entityDao.SessioneInPresenzaDao;
import com.progetto.boundary.CreateCourseBoundary;
import com.progetto.jdbc.ConnectionJavaDb;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.progetto.boundary.CreateCourseBoundary;
import com.progetto.jdbc.ConnectionJavaDb;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

public class CreateCourseController {
    // Lista delle sessioni (sia in presenza che online/ibride)
    private final List<Sessione> sessioni = new ArrayList<>();

    // --- Hybrid validation binding for manual invalidation ---
    private BooleanBinding detailsValidBinding;
    
    // === RIFERIMENTO ALLA BOUNDARY ===
    private com.progetto.boundary.CreateCourseBoundary boundary;
    
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
    private final ImageView chefProfileImageView;
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
    private static final String[] WEEK_DAYS = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};

    // === DATA ===
    private final Map<String, CheckBox> presenceDayCheckBoxes = new HashMap<>();
    private final Map<String, CheckBox> onlineDayCheckBoxes = new HashMap<>();
    private Label presenceDayInfoLabel, onlineDayInfoLabel;
    // Per tenere traccia dell'ordine di selezione dei giorni
    private final List<String> presenceDaySelectionOrder = new ArrayList<>();
    private final List<String> onlineDaySelectionOrder = new ArrayList<>();
    
    // === CUISINE TYPE FIELDS ===
    private final ComboBox<String> cuisineTypeComboBox1;
    private final ComboBox<String> cuisineTypeComboBox2;
    private final Label cuisineTypeErrorLabel;
    
    // === CONSTRUCTOR ===
    public CreateCourseController(
        TextField courseNameField, TextArea descriptionArea, 
        DatePicker startDatePicker, DatePicker endDatePicker,
        ComboBox<String> frequencyComboBox, ComboBox<String> lessonTypeComboBox,
        Spinner<Integer> maxParticipantsSpinner, TextField priceField,
        ImageView courseImageView, ImageView chefProfileImageView, Label chefNameLabel, Button createButton,
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
        this.courseNameField = courseNameField;
        this.descriptionArea = descriptionArea; 
        this.startDatePicker = startDatePicker;
        this.endDatePicker = endDatePicker;
        this.frequencyComboBox = frequencyComboBox;
        this.lessonTypeComboBox = lessonTypeComboBox;
        this.maxParticipantsSpinner = maxParticipantsSpinner;
        this.priceField = priceField;
        this.courseImageView = courseImageView;
        this.chefProfileImageView = chefProfileImageView;
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
        // Non aggiungiamo ricette di default - verranno create quando necessario
    }

    private void initializeData() {
        // Mostra nome e foto dello chef loggato
        com.progetto.Entity.EntityDto.Chef chef = com.progetto.Entity.EntityDto.Chef.loggedUser;
        if (chef != null && chefNameLabel != null) {
            chefNameLabel.setText(chef.getNome() + " " + chef.getCognome());
        }
        if (chef != null && chefProfileImageView != null && chef.getUrl_Propic() != null && !chef.getUrl_Propic().isEmpty()) {
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image(chef.getUrl_Propic(), true);
                chefProfileImageView.setImage(img);
            } catch (Exception e) {
                // In caso di errore, lascia l'immagine di default
            }
        }

        // Popola dropdown da database
        loadFrequencyOptions();
        loadLessonTypes();
        loadApplications();

        // Popola giorni settimana da enum DB
        try {
            BarraDiRicercaDao dao = new BarraDiRicercaDao();
            java.util.List<String> giorni = dao.GiorniSettimanaEnum();
            ObservableList<String> giorniEnum = FXCollections.observableArrayList(giorni);
            // Esempio: se hai ComboBox per i giorni, usali qui
            // dayOfWeekComboBox.setItems(giorniEnum);
            // onlineDayOfWeekComboBox.setItems(giorniEnum);
            // Oppure aggiorna la UI dinamica dove serve
        } catch (Exception e) {
            // debug print rimosso
        }

        // Popola unità di misura da enum DB
        try {
            BarraDiRicercaDao dao = new BarraDiRicercaDao();
            java.util.List<String> unita = dao.GrandezzeDiMisura();
            ObservableList<String> unitaEnum = FXCollections.observableArrayList(unita);
            // Esempio: se hai ComboBox per unità di misura ingredienti, usali qui
            // ingredientUnitComboBox.setItems(unitaEnum);
            // Oppure aggiorna la UI dinamica dove serve
        } catch (Exception e) {
            // debug print rimosso
        }
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

        // Popola tipi di cucina dal DAO
        try {
            BarraDiRicercaDao dao = new BarraDiRicercaDao();
            java.util.List<String> categorie = dao.Categorie();
            ObservableList<String> cuisineTypes = FXCollections.observableArrayList(categorie);
            cuisineTypeComboBox1.setItems(cuisineTypes);
            cuisineTypeComboBox2.setItems(cuisineTypes);
            cuisineTypeComboBox1.setValue(null);
            cuisineTypeComboBox2.setValue(null);
            cuisineTypeErrorLabel.setVisible(false);
        } catch (Exception e) {
            // debug print rimosso
        }

        // Limita durata presenza a 8 ore intere
        durationField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal.replaceAll("[^\\d]", "");
            if (!filtered.equals(newVal)) durationField.setText(filtered);
            if (!filtered.isEmpty()) {
                try {
                    int val = Integer.parseInt(filtered);
                    if (val > 8) durationField.setText("8");
                    else if (val < 1) durationField.setText("1");
                } catch (NumberFormatException e) { durationField.setText(""); }
            }
        });
        // Limita durata telematica a 8 ore intere
        onlineDurationField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal.replaceAll("[^\\d]", "");
            if (!filtered.equals(newVal)) onlineDurationField.setText(filtered);
            if (!filtered.isEmpty()) {
                try {
                    int val = Integer.parseInt(filtered);
                    if (val > 8) onlineDurationField.setText("8");
                    else if (val < 1) onlineDurationField.setText("1");
                } catch (NumberFormatException e) { onlineDurationField.setText(""); }
            }
        });
    }
    // === DATABASE INTEGRATION METHODS ===
    private void loadFrequencyOptions() {
        // Carica le frequenze dal DAO
        try {
            BarraDiRicercaDao dao = new BarraDiRicercaDao();
            java.util.List<String> frequenze = dao.CeraEnumFrequenza();
            frequencyComboBox.getItems().clear();
            frequencyComboBox.getItems().addAll(frequenze);
        } catch (Exception e) {
            // debug print rimosso
        }
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
        
        // Pulisci anche l'ordine di selezione
        if (isPresence) {
            presenceDaySelectionOrder.clear();
        } else {
            onlineDaySelectionOrder.clear();
        }
        
        for (String day : days) {
            CheckBox checkBox = new CheckBox(day);
            checkBox.getStyleClass().add("day-checkbox");
            // Listener migliorato che tiene traccia dell'ordine di selezione
            checkBox.setOnAction(e -> {
                List<String> selectionOrder = isPresence ? presenceDaySelectionOrder : onlineDaySelectionOrder;
                if (checkBox.isSelected()) {
                    // Giorno selezionato: aggiungilo alla lista dell'ordine
                    if (!selectionOrder.contains(day)) {
                        selectionOrder.add(day);
                    }
                } else {
                    // Giorno deselezionato: rimuovilo dalla lista dell'ordine
                    selectionOrder.remove(day);
                }
                validateDaySelection(checkBoxMap, 
                    isPresence ? presenceDayInfoLabel : onlineDayInfoLabel, isPresence);
            });
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
        addTextValidator(capField, "[^\\d]", 5, null);
        addTextValidator(priceField, PRICE_PATTERN, null, null);
        addTextValidator(durationField, "[^\\d]", 3, null); // max 3 cifre
        addTextValidator(onlineDurationField, "[^\\d]", 3, null);
        // Solo lettere per città (presenza)
        addTextValidator(cityField, "[^a-zA-ZàèéìòùÀÈÉÌÒÙ' ]", null, null);
    }
    
    /**
     * Unificato: aggiunge un listener di validazione a un TextField.
     * @param field il campo da validare
     * @param regexOrPattern regex stringa (per replaceAll) oppure Pattern (per match)
     * @param maxLength lunghezza massima (opzionale)
     * @param customFilter filtro custom opzionale (può essere null)
     */
    private void addTextValidator(TextField field, Object regexOrPattern, Integer maxLength, java.util.function.Function<String, String> customFilter) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal;
            if (customFilter != null) {
                filtered = customFilter.apply(filtered);
            } else if (regexOrPattern instanceof String) {
                filtered = filtered.replaceAll((String) regexOrPattern, "");
            } else if (regexOrPattern instanceof Pattern) {
                Pattern pattern = (Pattern) regexOrPattern;
                if (!pattern.matcher(filtered).matches()) {
                    filtered = filtered.replaceAll("[^\\d.]", "");
                    int dotIndex = filtered.indexOf('.');
                    if (dotIndex != -1) {
                        String beforeDot = filtered.substring(0, dotIndex);
                        String afterDot = filtered.substring(dotIndex + 1).replaceAll("\\.", "");
                        if (afterDot.length() > 2) afterDot = afterDot.substring(0, 2);
                        filtered = beforeDot + "." + afterDot;
                    }
                }
            }
            if (maxLength != null && filtered.length() > maxLength) {
                filtered = filtered.substring(0, maxLength);
            }
            if (!filtered.equals(newVal)) {
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
                updateSessioniPresenzaUI();
                // Aggiorna anche la UI ibrida se necessario
                if ("Entrambi".equals(lessonTypeComboBox.getValue()) && boundary != null) {
                    boundary.setupHybridUI(newVal, startDatePicker.getValue(), endDatePicker.getValue());
                }
                // Controlla se la modalità "Entrambi" è ancora supportata con la nuova frequenza
                checkHybridModeSupport(newVal);
            } else {
                setDayCheckBoxesEnabled(false);
                resetDayInfoLabels();
                clearSessioniPresenzaUI();
            }
        });

        // Aggiorna le sessioni anche quando cambiano giorni o date
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSessioniPresenzaUI();
            // Aggiorna anche la UI ibrida se necessario
            if ("Entrambi".equals(lessonTypeComboBox.getValue()) && boundary != null) {
                boundary.setupHybridUI(frequencyComboBox.getValue(), newVal, endDatePicker.getValue());
            }
        });
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSessioniPresenzaUI();
            // Aggiorna anche la UI ibrida se necessario
            if ("Entrambi".equals(lessonTypeComboBox.getValue()) && boundary != null) {
                boundary.setupHybridUI(frequencyComboBox.getValue(), startDatePicker.getValue(), newVal);
            }
        });
        presenceDayCheckBoxes.values().forEach(cb -> cb.selectedProperty().addListener((obs, oldVal, newVal) -> updateSessioniPresenzaUI()));
    }
    
    /**
     * Controlla se la modalità "Entrambi" è supportata per la frequenza data
     * e cambia automaticamente a "In presenza" se non supportata
     */
    private void checkHybridModeSupport(String frequenza) {
        if ("Entrambi".equals(lessonTypeComboBox.getValue())) {
            boolean isHybridSupported = frequenza != null && 
                (frequenza.contains("2 volte") || frequenza.contains("3 volte") || 
                 frequenza.toLowerCase().contains("giornaliera"));
            
            if (!isHybridSupported) {
                // Cambia automaticamente a "In presenza" se "Entrambi" non è supportato
                lessonTypeComboBox.setValue("In presenza");
                hybridErrorLabel.setText("La modalità 'Entrambi' non è disponibile per questa frequenza. Modalità cambiata automaticamente a 'In presenza'.");
                hybridErrorLabel.setVisible(true);
                
                // Nascondi il messaggio dopo 3 secondi
                new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3), e -> {
                        hybridErrorLabel.setVisible(false);
                    })
                ).play();
            }
        }
    }

    /**
     * Calcola le date delle sessioni in presenza in base a giorni selezionati, frequenza e intervallo date
     * e delega alla Boundary l'aggiornamento della UI per le ricette.
     */
    private void updateSessioniPresenzaUI() {
        String tipoLezione = lessonTypeComboBox.getValue();
        List<String> giorniSelezionati = getSelectedDays(presenceDayCheckBoxes);
        LocalDate inizio = startDatePicker.getValue();
        LocalDate fine = endDatePicker.getValue();
        String frequenza = frequencyComboBox.getValue();

        // Mostra la UI delle ricette solo se TUTTI i requisiti sono soddisfatti:
        // - Modalità "In presenza" 
        // - Frequenza selezionata
        // - Data inizio e fine valide
        // - Giorni selezionati correttamente in base alla frequenza
        boolean showSessioniPresenza = "In presenza".equals(tipoLezione)
            && frequenza != null
            && inizio != null && fine != null && !fine.isBefore(inizio)
            && hasCorrectNumberOfDays(presenceDayCheckBoxes)
            && !giorniSelezionati.isEmpty();

        if (!showSessioniPresenza) {
            // Delega alla Boundary la pulizia della UI
            if (boundary != null) {
                boundary.clearRecipesUI();
            }
            return;
        }
        
        // Gestione speciale per frequenza mensile: una ricetta per ogni mese
        if (frequenza != null && frequenza.toLowerCase().contains("mensile")) {
            List<LocalDate> dateSessioni = new ArrayList<>();
            if (inizio != null && fine != null && !fine.isAfter(inizio)) {
                LocalDate data = inizio.withDayOfMonth(1);
                while (!data.isAfter(fine)) {
                    dateSessioni.add(data);
                    data = data.plusMonths(1);
                }
            }
            if (dateSessioni.isEmpty()) {
                if (boundary != null) boundary.clearRecipesUI();
                return;
            }
            if (boundary != null) {
                if (dateSessioni.size() > 1) {
                    boundary.updatePresenceSessionsUI(dateSessioni);
                } else {
                    boundary.setupGenericRecipes();
                }
            }
            if (detailsValidBinding != null) detailsValidBinding.invalidate();
            return;
        }

        // Calcola tutte le date delle sessioni in presenza (default)
        List<LocalDate> dateSessioni = calcolaDateSessioniPresenza(inizio, fine, giorniSelezionati);

        // Se non ci sono date valide, pulisci
        if (dateSessioni.isEmpty()) {
            if (boundary != null) {
                boundary.clearRecipesUI();
            }
            return;
        }

        // Delega alla Boundary l'aggiornamento della UI delle ricette
        if (boundary != null) {
            if (dateSessioni.size() > 1) {
                // Sessioni multiple con date specifiche
                boundary.updatePresenceSessionsUI(dateSessioni);
            } else {
                // Sessione singola o ricette generiche
                boundary.setupGenericRecipes();
            }
        }

        if (detailsValidBinding != null) detailsValidBinding.invalidate();
    }

    private void clearSessioniPresenzaUI() {
        // Delega alla Boundary la pulizia della UI
        if (boundary != null) {
            boundary.clearRecipesUI();
        }
        if (detailsValidBinding != null) detailsValidBinding.invalidate();
    }

    /**
     * Calcola tutte le date delle sessioni in presenza tra inizio e fine, solo per i giorni selezionati.
     */
    private List<LocalDate> calcolaDateSessioniPresenza(LocalDate inizio, LocalDate fine, List<String> giorniSettimana) {
        List<LocalDate> result = new ArrayList<>();
        String frequenza = frequencyComboBox.getValue();
        if (frequenza != null && frequenza.toLowerCase().contains("mensile")) {
            // Frequenza mensile: una data per ogni mese (primo giorno selezionato del mese, o primo giorno del mese se nessun giorno selezionato)
            Set<Integer> giorniTarget = new HashSet<>();
            for (String giorno : giorniSettimana) {
                int dayOfWeek = giornoStringToDayOfWeek(giorno);
                if (dayOfWeek > 0) giorniTarget.add(dayOfWeek);
            }
            LocalDate current = inizio.withDayOfMonth(1);
            while (!current.isAfter(fine)) {
                LocalDate meseFine = current.withDayOfMonth(current.lengthOfMonth());
                LocalDate dataScelta = null;
                // Cerca il primo giorno selezionato del mese
                for (LocalDate d = current; !d.isAfter(meseFine) && !d.isAfter(fine); d = d.plusDays(1)) {
                    if (giorniTarget.isEmpty() || giorniTarget.contains(d.getDayOfWeek().getValue())) {
                        dataScelta = d;
                        break;
                    }
                }
                if (dataScelta != null) {
                    result.add(dataScelta);
                }
                current = current.plusMonths(1).withDayOfMonth(1);
            }
            return result;
        } else {
            // Default: tutte le date in base ai giorni selezionati
            Set<Integer> giorniTarget = new HashSet<>();
            for (String giorno : giorniSettimana) {
                int dayOfWeek = giornoStringToDayOfWeek(giorno);
                if (dayOfWeek > 0) giorniTarget.add(dayOfWeek);
            }
            LocalDate data = inizio;
            while (!data.isAfter(fine)) {
                if (giorniTarget.contains(data.getDayOfWeek().getValue())) {
                    result.add(data);
                }
                data = data.plusDays(1);
            }
            return result;
        }
    }

    // === GIORNI SETTIMANA: conversione bidirezionale con mappe statiche ===
    private static final Map<String, Integer> ITALIAN_DAY_TO_NUM = new HashMap<>();
    private static final Map<Integer, String> NUM_TO_ITALIAN_DAY = new HashMap<>();
    static {
        ITALIAN_DAY_TO_NUM.put("lunedì", 1); NUM_TO_ITALIAN_DAY.put(1, "Lunedì");
        ITALIAN_DAY_TO_NUM.put("martedì", 2); NUM_TO_ITALIAN_DAY.put(2, "Martedì");
        ITALIAN_DAY_TO_NUM.put("mercoledì", 3); NUM_TO_ITALIAN_DAY.put(3, "Mercoledì");
        ITALIAN_DAY_TO_NUM.put("giovedì", 4); NUM_TO_ITALIAN_DAY.put(4, "Giovedì");
        ITALIAN_DAY_TO_NUM.put("venerdì", 5); NUM_TO_ITALIAN_DAY.put(5, "Venerdì");
        ITALIAN_DAY_TO_NUM.put("sabato", 6); NUM_TO_ITALIAN_DAY.put(6, "Sabato");
        ITALIAN_DAY_TO_NUM.put("domenica", 7); NUM_TO_ITALIAN_DAY.put(7, "Domenica");
    }
    private int giornoStringToDayOfWeek(String giorno) {
        if (giorno == null) return -1;
        Integer val = ITALIAN_DAY_TO_NUM.get(giorno.toLowerCase());
        return val != null ? val : -1;
    }
    private String getItalianDayName(LocalDate date) {
        if (date == null || date.getDayOfWeek() == null) return "Lunedì";
        return NUM_TO_ITALIAN_DAY.getOrDefault(date.getDayOfWeek().getValue(), "Lunedì");
    }

    // === VALIDATION ===
    private void setupValidation() {
        BooleanBinding basicValid = createBasicValidation();
        detailsValidBinding = createDetailsValidation();
        addCheckboxValidationListeners(detailsValidBinding);
        // DEBUG: aggiungi listener per capire quale validazione fallisce
        basicValid.addListener((obs, oldVal, newVal) -> {
            // debug print rimosso
        });
        detailsValidBinding.addListener((obs, oldVal, newVal) -> {
            // debug print rimosso
        });
        createButton.disableProperty().bind(basicValid.not().or(detailsValidBinding.not()));
    }
    
    private BooleanBinding createBasicValidation() {
        return Bindings.createBooleanBinding(() -> {
            boolean valid = isValidText(courseNameField.getText()) &&
                isValidDescription(descriptionArea.getText()) &&
                startDatePicker.getValue() != null &&
                endDatePicker.getValue() != null &&
                frequencyComboBox.getValue() != null &&
                lessonTypeComboBox.getValue() != null &&
                isValidPrice(priceField.getText()) &&
                isValidParticipants(maxParticipantsSpinner.getValue());
            if (!valid) {
                // debug print rimosso
            }
            return valid;
        },
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
        Observable[] properties = Arrays.stream(getAllRelevantProperties())
            .filter(obj -> obj instanceof Observable)
            .toArray(Observable[]::new);

        return Bindings.createBooleanBinding(() -> {
            String lessonType = lessonTypeComboBox.getValue();
            boolean result = false;
            if ("In presenza".equals(lessonType)) {
                result = validatePresenceDetails();
            } else if ("Telematica".equals(lessonType)) {
                // Per la telematica NON validare le ricette
                result = validateOnlineDetails();
            } else if ("Entrambi".equals(lessonType)) {
                // Per l'ibrido: almeno una ricetta per ogni sessione in presenza, nessun obbligo per le telematiche
                result = validateHybridSessions();
            }
            return result;
        }, properties);
    }
    
    private boolean validatePresenceDetails() {
        boolean days = hasCorrectNumberOfDays(presenceDayCheckBoxes);
        boolean duration = isValidDurationRange(durationField.getText());
        boolean city = isValidText(cityField.getText());
        boolean street = isValidText(streetField.getText());
        boolean cap = isValidCAP(capField.getText());
        boolean ricette = true;
        // L'obbligo delle ricette solo per "In presenza" e non per telematica
        if (boundary != null) {
            ricette = boundary.areAllPresenceRecipesValid();
        }
        // Se la boundary non è impostata, non bloccare la validazione sulle ricette
        boolean result = days && duration && city && street && cap && ricette;
        return result;
    }
    
    private boolean validateOnlineDetails() {
        return applicationComboBox.getValue() != null &&
                isValidText(meetingCodeField.getText()) &&
                hasCorrectNumberOfDays(onlineDayCheckBoxes) &&
                isValidDurationRange(onlineDurationField.getText());
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
        // HybridDayDTO non ha più property JavaFX, quindi non serve più aggiungere property qui
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
    private void validateDaySelection(Map<String, CheckBox> checkBoxes, Label infoLabel, boolean isPresence) {
        if (frequencyComboBox.getValue() == null) return;
        
        int maxDays = getMaxDaysFromFrequency(frequencyComboBox.getValue());
        long selectedCount = checkBoxes.values().stream().mapToLong(cb -> cb.isSelected() ? 1 : 0).sum();
        
        // Solo se il numero di giorni selezionati supera il massimo consentito
        if (selectedCount > maxDays) {
            // Deseleziona l'ultimo giorno selezionato usando l'ordine di selezione
            deselectLastSelectedDay(checkBoxes, isPresence);
            showTemporaryError(infoLabel, maxDays);
        }
    }
    
    private void deselectLastSelectedDay(Map<String, CheckBox> checkBoxes, boolean isPresence) {
        List<String> selectionOrder = isPresence ? presenceDaySelectionOrder : onlineDaySelectionOrder;
        // Deseleziona l'ultimo giorno nella lista dell'ordine di selezione
        if (!selectionOrder.isEmpty()) {
            String lastSelectedDay = selectionOrder.get(selectionOrder.size() - 1);
            CheckBox lastCheckBox = checkBoxes.get(lastSelectedDay);
            if (lastCheckBox != null) {
                // Rimuovi temporaneamente il listener per evitare ricorsioni infinite
                lastCheckBox.setSelected(false);
                selectionOrder.remove(lastSelectedDay);
            }
        }
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
        if (frequency == null) return 1;
        
        frequency = frequency.toLowerCase();
        
        // Frequenza giornaliera: permette di selezionare tutti i giorni (max 7)
        if (frequency.contains("giornaliera") || frequency.contains("tutti i giorni")) {
            return 7;
        }
        // Tre volte a settimana
        if (frequency.contains("3 volte") || frequency.contains("tre volte")) {
            return 3;
        }
        // Due volte a settimana (bisettimanale)
        if (frequency.contains("2 volte") || frequency.contains("due volte") || 
            frequency.contains("bisettimanale") || frequency.contains("bi-settimanale")) {
            return 2;
        }
        // Una volta a settimana (settimanale) - default
        return 1;
    }
    
    private void clearDaySelections() {
        presenceDayCheckBoxes.values().forEach(cb -> cb.setSelected(false));
        onlineDayCheckBoxes.values().forEach(cb -> cb.setSelected(false));
        presenceDaySelectionOrder.clear();
        onlineDaySelectionOrder.clear();
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
        List<String> selected = new ArrayList<>();
        for (Map.Entry<String, CheckBox> entry : checkBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                selected.add(entry.getKey());
            }
        }
        return selected;
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
            return duration >= 1 && duration <= 8;
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

        if (isHybrid) {
            String frequenza = frequencyComboBox.getValue();
            boolean isHybridSupported = false;
            if (frequenza != null) {
                String freq = frequenza.toLowerCase();
                isHybridSupported = freq.contains("2 volte") || freq.contains("due volte") || freq.contains("bisettimanale") || freq.contains("bi-settimanale")
                    || freq.contains("3 volte") || freq.contains("tre volte")
                    || freq.contains("giornaliera") || freq.contains("tutti i giorni");
            }
            if (isHybridSupported) {
                hybridDetailsSection.setVisible(true);
                hybridDetailsSection.setManaged(true);
                hybridErrorLabel.setVisible(false);
                if (boundary != null) {
                    boundary.setupHybridUI(frequenza, startDatePicker.getValue(), endDatePicker.getValue());
                }
            } else {
                hybridDetailsSection.setVisible(false);
                hybridDetailsSection.setManaged(false);
                hybridErrorLabel.setText("La modalità 'Entrambi' è disponibile solo per frequenza giornaliera, bisettimanale o tre volte a settimana.");
                hybridErrorLabel.setVisible(true);
            }
        } else {
            hybridDetailsSection.setVisible(false);
            hybridDetailsSection.setManaged(false);
            hybridErrorLabel.setVisible(false);
        }

        // Aggiorna la UI delle ricette per la modalità in presenza
        if (isPresence) {
            updateSessioniPresenzaUI();
        } else {
            clearSessioniPresenzaUI();
        }
    }


    private List<LocalDate> calcolaDateSessioniPresenzaHybrid(String giornoSettimana, LocalDate inizio, LocalDate fine) {
        List<LocalDate> result = new ArrayList<>();
        int dayOfWeek = giornoStringToDayOfWeek(giornoSettimana);
        if (dayOfWeek == -1) return result;
        LocalDate data = inizio;
        // Trova il primo giorno corrispondente
        while (data.getDayOfWeek().getValue() != dayOfWeek) {
            data = data.plusDays(1);
            if (data.isAfter(fine)) return result;
        }
        // Aggiungi tutte le date corrispondenti
        while (!data.isAfter(fine)) {
            result.add(data);
            data = data.plusWeeks(1);
        }
        return result;
    }
    // === SETTER PER LA BOUNDARY ===
    public void setBoundary(com.progetto.boundary.CreateCourseBoundary boundary) {
        this.boundary = boundary;
    }
    
    
    /**
     * Chiamato dalla Boundary quando la UI ibrida viene aggiornata
     */
    public void onHybridUIUpdated() {
        if (detailsValidBinding != null) {
            detailsValidBinding.invalidate();
        }
    }
    
    /**
     * Chiamato dalla Boundary quando cambia la frequenza
     */
    public void onFrequencyChanged() {
        // Aggiorna la visibilità della sezione ibrida come in onLessonTypeChanged
        String selectedType = lessonTypeComboBox.getValue();
        boolean isHybrid = "Entrambi".equals(selectedType);
        if (isHybrid) {
            String frequenza = frequencyComboBox.getValue();
            boolean isHybridSupported = false;
            if (frequenza != null) {
                String freq = frequenza.toLowerCase();
                isHybridSupported = freq.contains("2 volte") || freq.contains("due volte") || freq.contains("bisettimanale") || freq.contains("bi-settimanale")
                    || freq.contains("3 volte") || freq.contains("tre volte")
                    || freq.contains("giornaliera") || freq.contains("tutti i giorni");
            }
            if (isHybridSupported) {
                hybridDetailsSection.setVisible(true);
                hybridDetailsSection.setManaged(true);
                hybridErrorLabel.setVisible(false);
                if (boundary != null) {
                    boundary.setupHybridUI(frequenza, startDatePicker.getValue(), endDatePicker.getValue());
                }
            } else {
                hybridDetailsSection.setVisible(false);
                hybridDetailsSection.setManaged(false);
                hybridErrorLabel.setText("La modalità 'Entrambi' è disponibile solo per frequenza giornaliera, bisettimanale o tre volte a settimana.");
                hybridErrorLabel.setVisible(true);
            }
        } else {
            hybridDetailsSection.setVisible(false);
            hybridDetailsSection.setManaged(false);
            hybridErrorLabel.setVisible(false);
        }

        updateSessioniPresenzaUI();
    }
    

    
    
    /**
     * Validazione per le sessioni ibride: delega alla Boundary
     */
    private boolean validateHybridSessions() {
        if (boundary != null) {
            // Ricette obbligatorie: per ogni sessione ibrida in presenza deve esserci almeno una ricetta valida
            List<Sessione> hybridSessions = boundary.getHybridSessions();
            Map<LocalDate, ObservableList<Ricetta>> hybridSessionRecipes = boundary.getHybridSessionRecipes();
            boolean ricetteOk = true;
            if (hybridSessions != null && !hybridSessions.isEmpty()) {
                for (Sessione sessione : hybridSessions) {
                    if (sessione instanceof SessioniInPresenza) {
                        LocalDate data = sessione.getData();
                        ObservableList<Ricetta> ricetteGiorno = hybridSessionRecipes.get(data);
                        if (ricetteGiorno == null || ricetteGiorno.isEmpty()) {
                            ricetteOk = false;
                            break;
                        }
                        for (Ricetta ricetta : ricetteGiorno) {
                            if (ricetta == null || ricetta.getNome() == null || ricetta.getNome().trim().isEmpty()) {
                                ricetteOk = false;
                                break;
                            }
                        }
                        if (!ricetteOk) break;
                    }
                }
            }
            return boundary.areAllHybridSessionsValid() && ricetteOk;
        }
        return false;
    }
    // === PUBLIC API FOR BOUNDARY ===
    // Variabili per gestire l'immagine selezionata
    private File selectedCourseImageFile = null;
    private String selectedCourseImageExtension = null;
    private boolean isDefaultCourseImage = true;

    public void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona immagine corso");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(courseImageView.getScene().getWindow());
        if (file != null) {
            String ext = "";
            int i = file.getName().lastIndexOf('.');
            if (i > 0) ext = file.getName().substring(i);
            selectedCourseImageFile = file;
            selectedCourseImageExtension = ext;
            isDefaultCourseImage = false;
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image(file.toURI().toString(), 180, 180, true, true);
                courseImageView.setImage(img);
            } catch (Exception e) {
                // fallback: non cambiare immagine
            }
        } else {
            selectedCourseImageFile = null;
            selectedCourseImageExtension = null;
            isDefaultCourseImage = true;
        }
    }
    // Variabile di istanza per tenere traccia dell'ID del corso appena creato
    private int lastCreatedCourseId = -1;

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

        // --- RACCOLTA DATI ---
        String nome = courseNameField.getText();
        String descrizione = descriptionArea.getText();
        LocalDate dataInizio = startDatePicker.getValue();
        LocalDate dataFine = endDatePicker.getValue();
        String frequenza = frequencyComboBox.getValue();
        int maxPersone = maxParticipantsSpinner.getValue();
        double prezzo = 0.0;
        try { prezzo = Double.parseDouble(priceField.getText().replace(",", ".")); } catch (Exception e) { prezzo = 0.0; }


        // --- ASSOCIA CHEF LOGGATO ---
        com.progetto.Entity.EntityDto.Chef chef = com.progetto.Entity.EntityDto.Chef.loggedUser;
        if (chef == null) {
            SuccessDialogUtils.showGenericSuccessDialog((Stage) courseNameField.getScene().getWindow(), "Errore", "Chef non loggato!");
            return;
        }

        // --- CREA OGGETTO CORSO (senza propic, la gestiamo dopo l'id) ---
        Corso corso = new Corso(
            nome, descrizione, dataInizio, dataFine, frequenza, maxPersone, (float) prezzo, null
        );
        corso.setChefNome(chef.getNome());
        corso.setChefCognome(chef.getCognome());
        corso.setChefEsperienza(chef.getAnniDiEsperienza());

        // --- SALVA NEL DB (così otteniamo l'id corso) ---
        CorsoDao corsoDao = new CorsoDao();
        corsoDao.memorizzaCorsoERicavaId(corso);
        // Salva l'id del corso appena creato in una variabile di istanza
        this.lastCreatedCourseId = corso.getId_Corso();

        // --- ASSOCIA IL CORSO ALLO CHEF (UPDATE FK) ---
        // Usa ChefDao per aggiornare la FK IdChef nel corso appena creato
        com.progetto.Entity.entityDao.ChefDao chefDao = new com.progetto.Entity.entityDao.ChefDao();
        chefDao.AssegnaCorso(corso, chef);

        // --- AGGIORNA LA NAVBAR/UI CON IL NOME DELLO CHEF LOGGATO ---
        if (chefNameLabel != null) {
            chefNameLabel.setText(chef.getNome() + " " + chef.getCognome());
        }

        // --- GESTIONE IMMAGINE CORSO ---
        String urlPropic;
        int idCorso = corso.getId_Corso();
        String resourcesDir = "src/main/resources/immagini/PropicCorso/";
        new File(resourcesDir).mkdirs();
        if (!isDefaultCourseImage && selectedCourseImageFile != null && selectedCourseImageExtension != null) {
            String fileName = idCorso + selectedCourseImageExtension;
            String absolutePath = resourcesDir + fileName;
            String relativePath = "immagini/PropicCorso/" + fileName;
            try {
                java.nio.file.Files.copy(selectedCourseImageFile.toPath(), new File(absolutePath).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                urlPropic = relativePath;
            } catch (Exception e) {
                urlPropic = "immagini/corso_default.png";
            }
        } else {
            urlPropic = "immagini/corso_default.png";
        }
        corso.setUrl_Propic(urlPropic);
        corsoDao.aggiornaCorso(corso);

        // --- AGGIORNA LA BOUNDARY PRIMA DI SALVARE LE SESSIONI ---
        String lessonType = lessonTypeComboBox.getValue();
        if (boundary != null) {
            if ("In presenza".equals(lessonType)) {
                updateSessioniPresenzaUI(); // Assicura che la boundary sia aggiornata
            } else if ("Telematica".equals(lessonType)) {
                // Se hai un metodo simile per telematica, chiamalo qui (es: updateSessioniTelematica())
            } else if ("Entrambi".equals(lessonType)) {
                boundary.updateHybridSessionsFromUI();
            }
        }
        // --- SALVA SESSIONI IN BASE AL TIPO DI LEZIONE ---
        if ("In presenza".equals(lessonType)) {
            salvaSessioniPresenza();
        } else if ("Telematica".equals(lessonType)) {
            salvaSessioniTelematica();
        } else if ("Entrambi".equals(lessonType)) {
            salvaSessioniHybridSmistate(new ricettaDao(), new IngredientiDao());
        }

        // --- ASSOCIA TIPO CUCINA ---
        ArrayList<String> tipiCucina = new ArrayList<>();
        if (cucina1 != null && !cucina1.trim().isEmpty()) tipiCucina.add(cucina1);
        if (cucina2 != null && !cucina2.trim().isEmpty() && !cucina2.equals(cucina1)) tipiCucina.add(cucina2);
        // Salva tipi cucina nel db (tabella TIPODICICINA_CORSO)
        try {
            Connection conn = com.progetto.jdbc.ConnectionJavaDb.getConnection();
            for (String tipo : tipiCucina) {
                // Recupera id tipo cucina
                PreparedStatement ps = conn.prepareStatement("SELECT IDTipoCucina FROM TipoDiCucina WHERE Nome = ?");
                ps.setString(1, tipo);
                ResultSet rs = ps.executeQuery();
                int idTipo = -1;
                if (rs.next()) idTipo = rs.getInt(1);
                rs.close();
                ps.close();
                if (idTipo != -1) {
                    PreparedStatement ps2 = conn.prepareStatement("INSERT INTO TipoDiCucina_Corso (IDTipoCucina, IDCorso) VALUES (?, ?)");
                    ps2.setInt(1, idTipo);
                    ps2.setInt(2, idCorso);
                    ps2.executeUpdate();
                    ps2.close();
                }
            }
            conn.close();
        } catch (Exception e) {
            // Log error if needed (no debug print)
        }

        // --- SALVA RICETTE E INGREDIENTI ---
        ricettaDao ricettaDao = new ricettaDao();
        IngredientiDao ingredientiDao = new IngredientiDao();
        if ("In presenza".equals(lessonType)) {
            salvaRicettePresenza(ricettaDao, ingredientiDao);
        }
        // For "Entrambi", sessioni ibride are already saved above; no need to call again.

        // --- FEEDBACK E NAVIGAZIONE ---
        Stage stage = (Stage) courseNameField.getScene().getWindow();
        SuccessDialogUtils.showGenericSuccessDialog(stage, "Corso creato con successo!", "Il corso è stato creato correttamente.");
        // Aggiorna la homepage e forza il reload delle immagini dei corsi
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/homepagechef.fxml"));
            javafx.scene.Parent root = loader.load();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            goToHomepage();
        }
    }
    // --- SALVATAGGIO SESSIONI IN PRESENZA ---
    private void salvaSessioniPresenza() {
        if (boundary == null) {
            return;
        }
        Map<LocalDate, ObservableList<Ricetta>> sessioniPresenza = boundary.getSessionePresenzaRicette();
        if (sessioniPresenza == null || sessioniPresenza.isEmpty()) {
            LocalDate today = LocalDate.now();
            ObservableList<Ricetta> ricette = FXCollections.observableArrayList();
            Ricetta ricetta = new Ricetta("Ricetta Test");
            ricetta.setIngredientiRicetta(new ArrayList<>());
            ricetta.getIngredientiRicetta().add(new Ingredienti("Ingrediente Test", 1, "g"));
            ricette.add(ricetta);
            sessioniPresenza = new HashMap<>();
            sessioniPresenza.put(today, ricette);
        }
        SessioneInPresenzaDao presenzaDao = new SessioneInPresenzaDao();
        Chef chef = Chef.loggedUser;
        String lessonType = lessonTypeComboBox.getValue();
        for (Map.Entry<LocalDate, ObservableList<Ricetta>> entry : sessioniPresenza.entrySet()) {
            LocalDate data = entry.getKey();
            ObservableList<Ricetta> ricette = entry.getValue();
            java.time.LocalTime orario = java.time.LocalTime.of(18, 0);
            java.time.LocalTime durata = java.time.LocalTime.of(2, 0); // default 120 min = 2h 0m
            String citta = null, via = null, cap = null;
            if ("In presenza".equals(lessonType)) {
                orario = (presenceHourSpinner != null && presenceMinuteSpinner != null) ? java.time.LocalTime.of(presenceHourSpinner.getValue(), presenceMinuteSpinner.getValue()) : java.time.LocalTime.of(18, 0);
                try {
                    int durataMinuti = (durationField != null && durationField.getText() != null && !durationField.getText().isEmpty()) ? Integer.parseInt(durationField.getText()) : 120;
                    int hours = durataMinuti / 60;
                    int minutes = durataMinuti % 60;
                    durata = java.time.LocalTime.of(hours, minutes);
                } catch (Exception e) {
                    durata = java.time.LocalTime.of(2, 0);
                }
                citta = cityField != null ? cityField.getText() : null;
                via = streetField != null ? streetField.getText() : null;
                cap = capField != null ? capField.getText() : null;
            }
            SessioniInPresenza sessione = new SessioniInPresenza(
                getItalianDayName(data),
                data,
                orario,
                durata,
                citta,
                via,
                cap,
                null, // attrezzatura
                0 // id_Sessione (autoincrement)
            );
            sessione.setId_Corso(this.lastCreatedCourseId);
            sessione.setChef(chef);
            sessione.setRicette(new ArrayList<>(ricette));
            try {
                presenzaDao.MemorizzaSessione(sessione);
                // --- COLLEGA OGNI RICETTA ALLA SESSIONE IN SESSIONE_PRESENZA_RICETTA ---
                for (Ricetta ricetta : ricette) {
                    collegaRicettaASessionePresenza(sessione.getId_Sessione(), ricetta.getId_Ricetta());
                }
            } catch (Exception ex) {
                // Log error if necessario
            }
        }
    }

    // --- COLLEGA UNA RICETTA A UNA SESSIONE IN PRESENZA ---
    private void collegaRicettaASessionePresenza(int idSessione, int idRicetta) {
        String query = "INSERT INTO sessione_presenza_ricetta (IdSessionePresenza, IdRicetta) VALUES (?, ?)";
        try (java.sql.Connection conn = com.progetto.jdbc.ConnectionJavaDb.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idSessione);
            ps.setInt(2, idRicetta);
            ps.executeUpdate();
        } catch (Exception e) {
            // Log error if necessario
        }
    }

    // --- SALVATAGGIO SESSIONI TELEMATICA/IBRIDE ---
    private void salvaSessioniTelematica() {
        if (boundary == null) {
            // debug print rimosso
            return;
        }
        // Se il corso è solo telematico, usa la lista delle sessioni telematiche pure
        List<SessioneOnline> telematicSessions = boundary.getSessioniTelematiche();
        try {
            telematicSessions = boundary.getSessioniTelematiche();
        } catch (Exception e) {
            System.out.println("[DEBUG] salvaSessioniTelematica: errore nel recupero delle sessioni telematiche pure: " + e);
        }
        if (telematicSessions == null || telematicSessions.isEmpty()) {
            System.out.println("[DEBUG] salvaSessioniTelematica: nessuna sessione telematica trovata (solo telematica).");
            return;
        }
        com.progetto.Entity.entityDao.SessioneOnlineDao onlineDao = new com.progetto.Entity.entityDao.SessioneOnlineDao();
        com.progetto.Entity.EntityDto.Chef chef = com.progetto.Entity.EntityDto.Chef.loggedUser;
        for (SessioneOnline session : telematicSessions) {
            System.out.println("[DEBUG] salvaSessioniTelematica: session=" + session);
            // Usa direttamente i dati dal DTO
            SessioneOnline online = new SessioneOnline(
                session.getGiorno(),
                session.getData(),
                session.getOrario(),
                session.getDurata(),
                session.getApplicazione(),
                session.getCodicechiamata(),
                null, // descrizione
                0 // id_Sessione (autoincrement)
            );
            online.setId_Corso(this.lastCreatedCourseId);
            online.setChef(chef);
            try {
                onlineDao.MemorizzaSessione(online);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // --- METODI DI SUPPORTO PER SALVATAGGIO ---
    // === RICETTE/INGREDIENTI: validazione e salvataggio unificati ===
    private boolean isValidRicetta(Ricetta ricetta) {
        if (ricetta == null || !isValidText(ricetta.getNome())) return false;
        List<Ingredienti> ingredienti = ricetta.getIngredientiRicetta();
        if (ingredienti == null || ingredienti.isEmpty()) return false;
        for (Ingredienti ing : ingredienti) {
            if (ing == null || !isValidText(ing.getNome())) return false;
            if (ing.getQuantita() <= 0) return false;
            if (!isValidText(ing.getUnitaMisura())) return false;
        }
        return true;
    }

    private void salvaRicettaCompleta(Ricetta ricetta, ricettaDao ricettaDao, IngredientiDao ingredientiDao) {
        if (!isValidRicetta(ricetta)) return;
        // Reset id_Ricetta to force DB to generate a new one and avoid PK conflicts
        ricetta.setId_Ricetta(0);
        ricettaDao.memorizzaRicetta(ricetta);
        if (ricetta.getId_Ricetta() <= 0) {
            return;
        }
        // Save each ingredient and update its ID before association
        for (Ingredienti ingrediente : ricetta.getIngredientiRicetta()) {
            if (ingrediente.getId_Ingrediente() == 0) {
                ingrediente.setIdIngrediente(0); // Ensure DB generates a new ID
                ingredientiDao.memorizzaIngredienti(ingrediente);
            }
        }
        // Now associate all ingredients (with correct IDs) to the recipe
        for (Ingredienti ingrediente : ricetta.getIngredientiRicetta()) {
            ricettaDao.associaIngredientiARicetta(ricetta, ingrediente);
        }
    }

    private void salvaRicettePresenza(ricettaDao ricettaDao, IngredientiDao ingredientiDao) {
        if (boundary == null) return;
        Map<LocalDate, ObservableList<Ricetta>> sessioneRicette = boundary.getSessionePresenzaRicette();
        ObservableList<Ricetta> genericRecipes = boundary.getGenericRecipes();
        for (Map.Entry<LocalDate, ObservableList<Ricetta>> entry : sessioneRicette.entrySet()) {
            for (Ricetta ricetta : entry.getValue()) {
                salvaRicettaCompleta(ricetta, ricettaDao, ingredientiDao);
            }
        }
        for (Ricetta ricetta : genericRecipes) {
            salvaRicettaCompleta(ricetta, ricettaDao, ingredientiDao);
        }
    }

    // --- NUOVA LOGICA: SALVATAGGIO SESSIONI IBRIDE SMISTATE ---
    private void salvaSessioniHybridSmistate(ricettaDao ricettaDao, IngredientiDao ingredientiDao) {
        if (boundary == null) return;
        boundary.updateHybridSessionsFromUI();
        List<Sessione> hybridSessions = boundary.getHybridSessions();
        if (hybridSessions == null || hybridSessions.isEmpty()) return;
        Chef chef = Chef.loggedUser;
        int idCorso = this.lastCreatedCourseId;
        Map<LocalDate, ObservableList<Ricetta>> hybridSessionRecipes = null;
        try {
            hybridSessionRecipes = boundary.getHybridSessionRecipes();
        } catch (Exception e) {
            hybridSessionRecipes = new HashMap<>();
        }
        SessioneInPresenzaDao presenzaDao = new SessioneInPresenzaDao();
        SessioneOnlineDao onlineDao = new SessioneOnlineDao();
        // Prima salva tutte le sessioni telematiche
        for (Sessione session : hybridSessions) {
            if (session instanceof SessioneOnline) {
                SessioneOnline s = (SessioneOnline) session;
                s.setId_Corso(idCorso);
                s.setChef(chef);
                onlineDao.MemorizzaSessione(s);
            }
        }
        // Poi salva tutte le sessioni in presenza e le relative ricette
        for (Sessione session : hybridSessions) {
            if (session instanceof SessioniInPresenza) {
                SessioniInPresenza s = (SessioniInPresenza) session;
                s.setId_Corso(idCorso);
                s.setChef(chef);
                // Salva la sessione in presenza nel DB
                presenzaDao.MemorizzaSessione(s);
                // Salva e collega le ricette a questa sessione
                List<Ricetta> ricette = new ArrayList<>();
                if (hybridSessionRecipes != null && hybridSessionRecipes.containsKey(s.getData())) {
                    ricette.addAll(hybridSessionRecipes.get(s.getData()));
                }
                for (Ricetta ricetta : ricette) {
                    // Salva la ricetta e i suoi ingredienti se non già salvati
                    salvaRicettaCompleta(ricetta, ricettaDao, ingredientiDao);
                    collegaRicettaASessionePresenza(s.getId_Sessione(), ricetta.getId_Ricetta());
                }
            }
        }
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
