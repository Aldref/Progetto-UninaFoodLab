
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
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;
import com.progetto.utils.SuccessDialogUtils;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        // Non aggiungiamo ricette di default - verranno create quando necessario
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
            e.printStackTrace();
        }

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
        // Carica le frequenze dal DAO
        try {
            BarraDiRicercaDao dao = new BarraDiRicercaDao();
            java.util.List<String> frequenze = dao.CeraEnumFrequenza();
            frequencyComboBox.getItems().clear();
            frequencyComboBox.getItems().addAll(frequenze);
        } catch (Exception e) {
            e.printStackTrace();
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
                updateSessioniPresenzaUI();
                
                // Aggiorna anche la UI ibrida se necessario
                if ("Entrambi".equals(lessonTypeComboBox.getValue())) {
                    setupHybridDaysUI();
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
            if ("Entrambi".equals(lessonTypeComboBox.getValue())) {
                setupHybridDaysUI();
            }
        });
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSessioniPresenzaUI();
            // Aggiorna anche la UI ibrida se necessario
            if ("Entrambi".equals(lessonTypeComboBox.getValue())) {
                setupHybridDaysUI();
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
            if (inizio != null && fine != null && !fine.isBefore(inizio)) {
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

    /**
     * Converte stringa giorno ("Lunedì", ecc.) in java.time.DayOfWeek.getValue() (1=Lunedì, 7=Domenica)
     */
    private int giornoStringToDayOfWeek(String giorno) {
        switch (giorno.toLowerCase()) {
            case "lunedì": return 1;
            case "martedì": return 2;
            case "mercoledì": return 3;
            case "giovedì": return 4;
            case "venerdì": return 5;
            case "sabato": return 6;
            case "domenica": return 7;
            default: return -1;
        }
    }

    // === VALIDATION ===
    private void setupValidation() {
        BooleanBinding basicValid = createBasicValidation();
        detailsValidBinding = createDetailsValidation();
        addCheckboxValidationListeners(detailsValidBinding);
        createButton.disableProperty().bind(basicValid.not().or(detailsValidBinding.not()));
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
                return validateHybridSessions();
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
        
        // Validazione ricette: delega completamente alla Boundary
        if (boundary != null) {
            return boundary.areAllPresenceRecipesValid();
        }
        
        return false;
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
        // Nuova validazione ibrida: usa solo la lista sessioni
        int expectedDays = getMaxDaysFromFrequency(frequencyComboBox.getValue());
        long selectedDays = sessioni.size();
        if (selectedDays != expectedDays) return false;
        for (Sessione s : sessioni) {
            if (s instanceof SessioniInPresenza) {
                SessioniInPresenza pres = (SessioniInPresenza) s;
                if (!isValidText(pres.getCitta())) return false;
                if (!isValidText(pres.getVia())) return false;
                if (!isValidCAP(pres.getCap())) return false;
                if (!isValidDurationRange(String.valueOf(pres.getDurata()))) return false;
                java.util.List<Ricetta> ricette = pres.getRicette();
                if (ricette == null || ricette.isEmpty()) return false;
                for (Ricetta r : ricette) {
                    if (r.getNome() == null || r.getNome().trim().isEmpty()) return false;
                    java.util.List<Ingredienti> ings = r.getIngredientiRicetta();
                    if (ings == null || ings.isEmpty()) return false;
                    for (Ingredienti ing : ings) {
                        if (ing.getNome() == null || ing.getNome().trim().isEmpty()) return false;
                        if (ing.getQuantita() <= 0) return false;
                        if (ing.getUnitaMisura() == null || ing.getUnitaMisura().trim().isEmpty()) return false;
                    }
                }
            } else if (s instanceof SessioneOnline) {
                SessioneOnline online = (SessioneOnline) s;
                if (!isValidText(online.getApplicazione())) return false;
                if (!isValidText(online.getCodicechiamata())) return false;
                if (!isValidDurationRange(String.valueOf(online.getDurata()))) return false;
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
                setupHybridDaysUI();
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

    private void setupHybridDaysUI() {
        // Delega alla Boundary la creazione della UI
        if (boundary != null) {
            boundary.setupHybridUI(
                frequencyComboBox.getValue(), 
                startDatePicker.getValue(), 
                endDatePicker.getValue()
            );
        }
    }
    
    // === SETTER PER LA BOUNDARY ===
    public void setBoundary(com.progetto.boundary.CreateCourseBoundary boundary) {
        this.boundary = boundary;
    }
    
    // === CALLBACK DALLA BOUNDARY ===
    
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
                setupHybridDaysUI();
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
    
    private String getItalianDayName(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY: return "Lunedì";
            case TUESDAY: return "Martedì";
            case WEDNESDAY: return "Mercoledì";
            case THURSDAY: return "Giovedì";
            case FRIDAY: return "Venerdì";
            case SATURDAY: return "Sabato";
            case SUNDAY: return "Domenica";
            default: return "Lunedì";
        }
    }
    
    private TextField findFieldInContainer(VBox container, String labelText) {
        for (javafx.scene.Node node : container.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                for (javafx.scene.Node child : hbox.getChildren()) {
                    if (child instanceof Label && ((Label) child).getText().equals(labelText)) {
                        // Trova il TextField nella stessa HBox
                        for (javafx.scene.Node sibling : hbox.getChildren()) {
                            if (sibling instanceof TextField) {
                                return (TextField) sibling;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private ComboBox<String> findComboInContainer(VBox container, String labelText) {
        for (javafx.scene.Node node : container.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                for (javafx.scene.Node child : hbox.getChildren()) {
                    if (child instanceof Label && ((Label) child).getText().equals(labelText)) {
                        // Trova il ComboBox nella stessa HBox
                        for (javafx.scene.Node sibling : hbox.getChildren()) {
                            if (sibling instanceof ComboBox) {
                                return (ComboBox<String>) sibling;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    // Ricette in presenza: completamente gestite dalla Boundary
    public void addNewRecipe() {
        // Questo metodo è mantenuto per compatibilità, ma la logica è ora nella Boundary
        // La validazione viene invalidata automaticamente dalla Boundary tramite notifyControllerOfChange
        // (nessun log)
    }
    /**
     * Validazione per le sessioni ibride: delega alla Boundary
     */
    private boolean validateHybridSessions() {
        if (boundary != null) {
            return boundary.areAllHybridSessionsValid();
        }
        return false;
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

        // --- RACCOLTA DATI ---
        String nome = courseNameField.getText();
        String descrizione = descriptionArea.getText();
        LocalDate dataInizio = startDatePicker.getValue();
        LocalDate dataFine = endDatePicker.getValue();
        String frequenza = frequencyComboBox.getValue();
        int maxPersone = maxParticipantsSpinner.getValue();
        double prezzo = 0.0;
        try { prezzo = Double.parseDouble(priceField.getText().replace(",", ".")); } catch (Exception e) { prezzo = 0.0; }
        String urlPropic = null;

        // --- GESTIONE IMMAGINE CORSO ---
        if (courseImageView.getImage() != null && courseImageView.getImage().getUrl() != null) {
            String ext = ".png";
            String chefName = chefNameLabel.getText().replaceAll("[^a-zA-Z0-9]", "_");
            String fileName = nome.replaceAll("[^a-zA-Z0-9]", "_") + "_" + chefName + ext;
            String relativePath = "immagini/PropicCorso/" + fileName;
            String resourcesDir = "src/main/resources/immagini/PropicCorso/";
            new File(resourcesDir).mkdirs();
            String absolutePath = resourcesDir + fileName;
            try {
                javafx.scene.image.Image img = courseImageView.getImage();
                java.io.InputStream is = new java.net.URL(img.getUrl()).openStream();
                java.nio.file.Files.copy(is, new File(absolutePath).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                urlPropic = relativePath;
            } catch (Exception e) {
                urlPropic = null;
            }
        }

        // --- CREA OGGETTO CORSO ---
        Corso corso = new Corso(
            nome, descrizione, dataInizio, dataFine, frequenza, maxPersone, (float) prezzo, urlPropic
        );

        // --- SALVA NEL DB ---
        CorsoDao corsoDao = new CorsoDao();
        corsoDao.memorizzaCorsoERicavaId(corso);

        // --- ASSOCIA TIPO CUCINA (se serve, qui solo esempio commentato) ---
        // TODO: implementa salvataggio tipi cucina se necessario

        // --- SALVA RICETTE E INGREDIENTI ---
        ricettaDao ricettaDao = new ricettaDao();
        IngredientiDao ingredientiDao = new IngredientiDao();
        salvaRicettePresenza(ricettaDao, ingredientiDao);
        salvaSessioniIbride(ricettaDao, ingredientiDao);

        // --- FEEDBACK E NAVIGAZIONE ---
        Stage stage = (Stage) courseNameField.getScene().getWindow();
        SuccessDialogUtils.showGenericSuccessDialog(stage, "Corso creato con successo!", "Il corso è stato creato correttamente.");
        goToHomepage();
    }

    // --- METODI DI SUPPORTO PER SALVATAGGIO ---
    private void salvaRicettePresenza(ricettaDao ricettaDao, IngredientiDao ingredientiDao) {
        if (boundary == null) return;
        
        // Ottieni le ricette dalla Boundary
        Map<LocalDate, ObservableList<Ricetta>> sessioneRicette = boundary.getSessionePresenzaRicette();
        ObservableList<Ricetta> genericRecipes = boundary.getGenericRecipes();
        
        // Salva ricette per sessioni specifiche
        for (Map.Entry<LocalDate, ObservableList<Ricetta>> entry : sessioneRicette.entrySet()) {
            for (Ricetta ricetta : entry.getValue()) {
                if (!isValidText(ricetta.getNome())) continue;
                ricettaDao.memorizzaRicetta(ricetta);
                for (Ingredienti ingrediente : ricetta.getIngredientiRicetta()) {
                    if (!isValidText(ingrediente.getNome())) continue;
                    if (ingrediente.getQuantita() <= 0) continue;
                    if (!isValidText(ingrediente.getUnitaMisura())) continue;
                    ingredientiDao.memorizzaIngredienti(ingrediente);
                    ricettaDao.associaIngredientiARicetta(ricetta, ingrediente);
                }
            }
        }
        
        // Salva ricette generiche
        for (Ricetta ricetta : genericRecipes) {
            if (!isValidText(ricetta.getNome())) continue;
            ricettaDao.memorizzaRicetta(ricetta);
            for (Ingredienti ingrediente : ricetta.getIngredientiRicetta()) {
                if (!isValidText(ingrediente.getNome())) continue;
                if (ingrediente.getQuantita() <= 0) continue;
                if (!isValidText(ingrediente.getUnitaMisura())) continue;
                ingredientiDao.memorizzaIngredienti(ingrediente);
                ricettaDao.associaIngredientiARicetta(ricetta, ingrediente);
            }
        }
    }

    private void salvaSessioniIbride(ricettaDao ricettaDao, IngredientiDao ingredientiDao) {
        for (Sessione s : sessioni) {
            if (s instanceof SessioniInPresenza) {
                SessioniInPresenza pres = (SessioniInPresenza) s;
                List<Ricetta> ricette = pres.getRicette();
                if (ricette == null) continue;
                for (Ricetta ricetta : ricette) {
                    if (!isValidText(ricetta.getNome())) continue;
                    ricettaDao.memorizzaRicetta(ricetta);
                    for (Ingredienti ingrediente : ricetta.getIngredientiRicetta()) {
                        if (!isValidText(ingrediente.getNome())) continue;
                        if (ingrediente.getQuantita() <= 0) continue;
                        if (!isValidText(ingrediente.getUnitaMisura())) continue;
                        ingredientiDao.memorizzaIngredienti(ingrediente);
                        ricettaDao.associaIngredientiARicetta(ricetta, ingrediente);
                    }
                }
            } else if (s instanceof SessioneOnline) {
                SessioneOnline online = (SessioneOnline) s;
                // Salva info telematica se necessario (applicazione, codicechiamata, durata, ecc.)
                // Esempio: sessioneOnlineDao.memorizzaSessioneOnline(online);
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
