package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.util.StringConverter;
import com.progetto.controller.CreateCourseController;
import com.progetto.Entity.EntityDto.Ricetta;
import com.progetto.Entity.EntityDto.Ingredienti;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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
    
    // === DATI PER LA MODALITÀ IBRIDA ===
    private final List<HybridSessionData> hybridSessions = new ArrayList<>();
    
    // === DATI PER LE SESSIONI IN PRESENZA ===
    private final Map<LocalDate, ObservableList<Ricetta>> sessionePresenzaRicette = new HashMap<>();
    private final ObservableList<Ricetta> genericRecipes = FXCollections.observableArrayList();
    
    // Inner class per memorizzare i dati di una sessione ibrida
    public static class HybridSessionData {
        public ComboBox<String> typeCombo;
        public DatePicker datePicker;
        public Spinner<Integer> hourSpinner;
        public Spinner<Integer> minuteSpinner;
        public TextField durationField;
        public VBox detailsContainer;
        
        // Campi specifici per sessioni in presenza
        public TextField cityField;
        public TextField streetField;
        public TextField capField;
        public List<Ricetta> ricette = new ArrayList<>();
        
        // Campi specifici per sessioni online
        public ComboBox<String> applicationCombo;
        public TextField meetingCodeField;
        
        public HybridSessionData() {}
        
        public boolean isValid() {
            return typeCombo.getValue() != null && 
                   datePicker.getValue() != null &&
                   durationField.getText() != null && !durationField.getText().trim().isEmpty() &&
                   durationField.getText().matches("\\d+");
        }
        
        public boolean isPresenceSessionValid() {
            if (!"In presenza".equals(typeCombo.getValue())) return true;
            return cityField != null && cityField.getText() != null && !cityField.getText().trim().isEmpty() &&
                   streetField != null && streetField.getText() != null && !streetField.getText().trim().isEmpty() &&
                   capField != null && capField.getText() != null && capField.getText().matches("\\d{5}") &&
                   !ricette.isEmpty() && ricette.stream().anyMatch(this::isRicettaValid);
        }
        
        public boolean isOnlineSessionValid() {
            if (!"Telematica".equals(typeCombo.getValue())) return true;
            return applicationCombo != null && applicationCombo.getValue() != null &&
                   meetingCodeField != null && meetingCodeField.getText() != null && !meetingCodeField.getText().trim().isEmpty();
        }
        
        private boolean isRicettaValid(Ricetta ricetta) {
            if (ricetta.getNome() == null || ricetta.getNome().trim().isEmpty()) return false;
            List<Ingredienti> ings = ricetta.getIngredientiRicetta();
            return ings != null && !ings.isEmpty() && 
                   ings.stream().allMatch(ing -> 
                       ing.getNome() != null && !ing.getNome().trim().isEmpty() && 
                       ing.getQuantita() > 0 && 
                       ing.getUnitaMisura() != null && !ing.getUnitaMisura().trim().isEmpty()
                   );
        }
    }

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
        
        // Imposta il riferimento alla boundary nel controller
        controller.setBoundary(this);
        
        controller.initialize();
    }

    // === METODI PER CREARE UI DINAMICA ===
    
    /**
     * Configura la UI per la sezione ibrida in base alla situazione
     */
    public void setupHybridUI(String frequenza, LocalDate inizio, LocalDate fine) {
        hybridDaysContainer.getChildren().clear();
        hybridSessions.clear();

        if (frequenza == null || inizio == null || fine == null) {
            showHybridErrorMessage("Compila prima tutte le informazioni di base.");
            return;
        }

        // Verifica che la frequenza sia supportata per la modalità ibrida
        String freq = frequenza.toLowerCase();
        boolean isHybridSupported = freq.contains("2 volte") || freq.contains("due volte") || freq.contains("bisettimanale") || freq.contains("bi-settimanale")
            || freq.contains("3 volte") || freq.contains("tre volte")
            || freq.contains("giornaliera") || freq.contains("tutti i giorni");
        if (!isHybridSupported) {
            showHybridErrorMessage("La modalità 'Entrambi' non è supportata per questa frequenza");
            return;
        }

        int numSessioni = getMaxDaysFromFrequency(frequenza);
        // Crea una riga per ogni giorno richiesto dalla frequenza
        Label titleLabel = new Label("Configura per ogni giorno della settimana:");
        titleLabel.getStyleClass().add("section-title");
        hybridDaysContainer.getChildren().add(titleLabel);

        for (int i = 0; i < numSessioni; i++) {
            final int sessionIndex = i;
            HybridSessionData sessionData = new HybridSessionData();
            VBox sessionBox = new VBox(10);
            sessionBox.getStyleClass().add("hybrid-session-container");

            HBox dayBox = new HBox(10);
            Label dayLabel = new Label("Giorno " + (sessionIndex + 1) + ":");
            ComboBox<String> typeCombo = new ComboBox<>();
            typeCombo.getItems().addAll("In presenza", "Telematica");
            typeCombo.setPromptText("Tipo lezione");
            sessionData.typeCombo = typeCombo;
            dayBox.getChildren().addAll(dayLabel, typeCombo);

            VBox detailsContainer = new VBox(10);
            sessionData.detailsContainer = detailsContainer;

            // Listener per mostrare i dettagli in base al tipo
            typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                detailsContainer.getChildren().clear();
                if ("In presenza".equals(newVal)) {
                    detailsContainer.getChildren().add(createPresenceDetails(sessionData));
                    detailsContainer.getChildren().add(createRecipesSection(sessionIndex, sessionData));
                } else if ("Telematica".equals(newVal)) {
                    detailsContainer.getChildren().add(createOnlineDetails(sessionData));
                }
                notifyControllerOfChange();
            });

            sessionBox.getChildren().addAll(dayBox, detailsContainer);
            hybridDaysContainer.getChildren().add(sessionBox);
            hybridSessions.add(sessionData);
        }

        if (controller != null) {
            controller.onHybridUIUpdated();
        }
    }
    
    /**
     * Calcola il numero massimo di giorni dalla frequenza
     */
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
    
    /**
     * Crea la UI per la sezione ibrida con le sessioni configurabili
     */
    public void createHybridUI(int numSessioni) {
        hybridDaysContainer.getChildren().clear();
        hybridSessions.clear();
        
        Label titleLabel = new Label("Configura le " + numSessioni + " sessioni del corso:");
        titleLabel.getStyleClass().add("section-title");
        hybridDaysContainer.getChildren().add(titleLabel);
        
        // Crea un'interfaccia per ogni sessione
        for (int i = 0; i < numSessioni; i++) {
            HybridSessionData sessionData = new HybridSessionData();
            VBox sessionBox = createHybridSessionBox(i, sessionData);
            hybridSessions.add(sessionData);
            hybridDaysContainer.getChildren().add(sessionBox);
        }
    }
    
    /**
     * Mostra un messaggio informativo nella sezione ibrida
     */
    public void showHybridInfoMessage(String message) {
        hybridDaysContainer.getChildren().clear();
        Label infoLabel = new Label(message);
        infoLabel.getStyleClass().add("info-label");
        hybridDaysContainer.getChildren().add(infoLabel);
    }
    
    /**
     * Mostra un messaggio di errore nella sezione ibrida
     */
    public void showHybridErrorMessage(String message) {
        hybridDaysContainer.getChildren().clear();
        Label errorLabel = new Label(message);
        errorLabel.getStyleClass().add("error-label");
        hybridDaysContainer.getChildren().add(errorLabel);
    }
    
    /**
     * Crea il box per configurare una singola sessione ibrida
     */
    private VBox createHybridSessionBox(int sessionIndex, HybridSessionData sessionData) {
        VBox sessionBox = new VBox(15);
        sessionBox.getStyleClass().add("hybrid-session-container");
        
        // Titolo della sessione
        Label sessionTitle = new Label("Sessione " + (sessionIndex + 1));
        sessionTitle.getStyleClass().add("session-title");
        
        // Tipo di sessione (In presenza / Telematica)
        HBox typeBox = new HBox(10);
        Label typeLabel = new Label("Tipo sessione:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("In presenza", "Telematica");
        typeCombo.setPromptText("Seleziona tipo");
        sessionData.typeCombo = typeCombo;
        typeBox.getChildren().addAll(typeLabel, typeCombo);
        
        // Data della sessione
        HBox dateBox = new HBox(10);
        Label dateLabel = new Label("Data:");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Seleziona data");
        sessionData.datePicker = datePicker;
        // Limita le date al range del corso
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate start = startDatePicker.getValue();
                LocalDate end = endDatePicker.getValue();
                setDisable(empty || (start != null && date.isBefore(start)) || 
                          (end != null && date.isAfter(end)));
            }
        });
        dateBox.getChildren().addAll(dateLabel, datePicker);
        
        // Orario
        HBox timeBox = new HBox(10);
        Label timeLabel = new Label("Orario:");
        Spinner<Integer> hourSpinner = new Spinner<>(6, 23, 18);
        setupTimeSpinnerFormatter(hourSpinner, false);
        Label colonLabel = new Label(":");
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0, 15);
        setupTimeSpinnerFormatter(minuteSpinner, true);
        sessionData.hourSpinner = hourSpinner;
        sessionData.minuteSpinner = minuteSpinner;
        timeBox.getChildren().addAll(timeLabel, hourSpinner, colonLabel, minuteSpinner);
        
        // Durata
        HBox durationBox = new HBox(10);
        Label durationLabel = new Label("Durata (minuti):");
        TextField durationField = new TextField();
        durationField.setPromptText("es. 120");
        addTextValidator(durationField, "[^\\d]", 3);
        sessionData.durationField = durationField;
        durationBox.getChildren().addAll(durationLabel, durationField);
        
        // Container per dettagli specifici (presenza o telematica)
        VBox detailsContainer = new VBox(10);
        sessionData.detailsContainer = detailsContainer;
        
        // Listener per mostrare campi specifici in base al tipo
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            detailsContainer.getChildren().clear();
            if ("In presenza".equals(newVal)) {
                VBox presenceDetails = createPresenceDetails(sessionData);
                VBox recipesSection = createRecipesSection(sessionIndex, sessionData);
                detailsContainer.getChildren().addAll(presenceDetails, recipesSection);
            } else if ("Telematica".equals(newVal)) {
                VBox onlineDetails = createOnlineDetails(sessionData);
                detailsContainer.getChildren().add(onlineDetails);
            }
            notifyControllerOfChange();
        });
        
        // Listener per aggiornare i dati della sessione e notificare il controller
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> notifyControllerOfChange());
        hourSpinner.valueProperty().addListener((obs, oldVal, newVal) -> notifyControllerOfChange());
        minuteSpinner.valueProperty().addListener((obs, oldVal, newVal) -> notifyControllerOfChange());
        sessionBox.getChildren().addAll(sessionTitle, typeBox, dateBox, timeBox, durationBox, detailsContainer);
        return sessionBox;
    }
    
    /**
     * Crea i dettagli specifici per sessioni in presenza
     */
    private VBox createPresenceDetails(HybridSessionData sessionData) {
        VBox presenceBox = new VBox(10);
        presenceBox.getStyleClass().add("presence-details");
        
        // Città
        HBox cityBox = new HBox(10);
        Label cityLabel = new Label("Città:");
        TextField cityField = new TextField();
        cityField.setPromptText("es. Napoli");
        cityField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal.replaceAll("[^a-zA-ZàèéìòùÀÈÉÌÒÙ' ]", "");
            if (!filtered.equals(newVal)) cityField.setText(filtered);
            notifyControllerOfChange();
        });
        sessionData.cityField = cityField;
        cityBox.getChildren().addAll(cityLabel, cityField);
        
        // Via
        HBox streetBox = new HBox(10);
        Label streetLabel = new Label("Via:");
        TextField streetField = new TextField();
        streetField.setPromptText("es. Via Roma 123");
        streetField.textProperty().addListener((obs, oldVal, newVal) -> notifyControllerOfChange());
        sessionData.streetField = streetField;
        streetBox.getChildren().addAll(streetLabel, streetField);
        
        // CAP
        HBox capBox = new HBox(10);
        Label capLabel = new Label("CAP:");
        TextField capField = new TextField();
        capField.setPromptText("es. 80100");
        addTextValidator(capField, "[^\\d]", 5);
        capField.textProperty().addListener((obs, oldVal, newVal) -> notifyControllerOfChange());
        sessionData.capField = capField;
        capBox.getChildren().addAll(capLabel, capField);
        
        presenceBox.getChildren().addAll(cityBox, streetBox, capBox);
        return presenceBox;
    }
    
    /**
     * Crea i dettagli specifici per sessioni online
     */
    private VBox createOnlineDetails(HybridSessionData sessionData) {
        VBox onlineBox = new VBox(10);
        onlineBox.getStyleClass().add("online-details");
        
        // Applicazione
        HBox appBox = new HBox(10);
        Label appLabel = new Label("Applicazione:");
        ComboBox<String> appCombo = new ComboBox<>();
        appCombo.getItems().addAll("Zoom", "Microsoft Teams", "Google Meet");
        appCombo.setPromptText("Seleziona applicazione");
        appCombo.valueProperty().addListener((obs, oldVal, newVal) -> notifyControllerOfChange());
        sessionData.applicationCombo = appCombo;
        appBox.getChildren().addAll(appLabel, appCombo);
        
        // Codice riunione
        HBox codeBox = new HBox(10);
        Label codeLabel = new Label("Codice riunione:");
        TextField codeField = new TextField();
        codeField.setPromptText("es. 123-456-789");
        codeField.textProperty().addListener((obs, oldVal, newVal) -> notifyControllerOfChange());
        sessionData.meetingCodeField = codeField;
        codeBox.getChildren().addAll(codeLabel, codeField);
        
        onlineBox.getChildren().addAll(appBox, codeBox);
        return onlineBox;
    }
    
    /**
     * Crea la sezione ricette per una sessione ibrida
     */
    private VBox createRecipesSection(int sessionIndex, HybridSessionData sessionData) {
        VBox recipesBox = new VBox(10);
        recipesBox.getStyleClass().add("recipes-section");
        
        Label recipesLabel = new Label("Ricette per questa sessione:");
        recipesLabel.getStyleClass().add("subsection-title");
        
        VBox ricetteContainer = new VBox(8);
        
        // Aggiungi una ricetta iniziale vuota
        if (sessionData.ricette.isEmpty()) {
            sessionData.ricette.add(new Ricetta(""));
        }
        
        // Crea la UI per ogni ricetta esistente
        for (Ricetta ricetta : sessionData.ricette) {
            VBox recipeBox = createRecipeBoxForHybrid(ricetta, sessionData, ricetteContainer);
            ricetteContainer.getChildren().add(recipeBox);
        }
        
        Button addRecipeBtn = new Button("+ Aggiungi ricetta");
        addRecipeBtn.getStyleClass().add("add-recipe-button");
        addRecipeBtn.setOnAction(e -> {
            Ricetta nuova = new Ricetta("");
            sessionData.ricette.add(nuova);
            VBox recipeBox = createRecipeBoxForHybrid(nuova, sessionData, ricetteContainer);
            ricetteContainer.getChildren().add(recipeBox);
            notifyControllerOfChange();
        });
        
        recipesBox.getChildren().addAll(recipesLabel, ricetteContainer, addRecipeBtn);
        return recipesBox;
    }
    
    /**
     * Crea il box per una singola ricetta nella modalità ibrida
     */
    private VBox createRecipeBoxForHybrid(Ricetta ricetta, HybridSessionData sessionData, VBox container) {
        VBox recipeBox = new VBox(10);
        recipeBox.getStyleClass().add("recipe-box");
        
        // Nome ricetta
        HBox nameBox = new HBox(10);
        Label nameLabel = new Label("Nome ricetta:");
        TextField nameField = new TextField(ricetta.getNome());
        nameField.setPromptText("es. Pasta alla carbonara");
        nameField.getStyleClass().add("ingredient-name-field");
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            ricetta.setNome(newVal);
            notifyControllerOfChange();
        });
        Button removeBtn = new Button("Rimuovi");
        removeBtn.getStyleClass().add("remove-button");
        removeBtn.setOnAction(e -> {
            sessionData.ricette.remove(ricetta);
            container.getChildren().remove(recipeBox);
            notifyControllerOfChange();
        });
        nameBox.getChildren().addAll(nameLabel, nameField, removeBtn);
        
        // Container per ingredienti
        VBox ingredientsContainer = new VBox(5);
        Label ingredientsLabel = new Label("Ingredienti:");
        ingredientsLabel.getStyleClass().add("ingredients-title");
        
        // Se la ricetta non ha ingredienti, aggiungine uno vuoto
        if (ricetta.getIngredientiRicetta() == null || ricetta.getIngredientiRicetta().isEmpty()) {
            ricetta.setIngredientiRicetta(new ArrayList<>());
            ricetta.getIngredientiRicetta().add(new Ingredienti("", 0, ""));
        }
        
        // Crea la UI per ogni ingrediente
        for (Ingredienti ingrediente : ricetta.getIngredientiRicetta()) {
            HBox ingredientBox = createIngredientBoxForHybrid(ingrediente, ricetta, ingredientsContainer);
            ingredientsContainer.getChildren().add(ingredientBox);
        }
        
        Button addIngredientBtn = new Button("+ Ingrediente");
        addIngredientBtn.getStyleClass().add("add-ingredient-button");
        addIngredientBtn.setOnAction(e -> {
            Ingredienti nuovo = new Ingredienti("", 0, "");
            ricetta.getIngredientiRicetta().add(nuovo);
            HBox ingredientBox = createIngredientBoxForHybrid(nuovo, ricetta, ingredientsContainer);
            ingredientsContainer.getChildren().add(ingredientBox);
            notifyControllerOfChange();
        });
        
        recipeBox.getChildren().addAll(nameBox, ingredientsLabel, ingredientsContainer, addIngredientBtn);
        return recipeBox;
    }
    
    /**
     * Crea il box per un singolo ingrediente nella modalità ibrida
     */
    private HBox createIngredientBoxForHybrid(Ingredienti ingrediente, Ricetta ricetta, VBox container) {
        HBox ingredientBox = new HBox(15);
        ingredientBox.getStyleClass().addAll("ingredient-box", "ingredient-row");

        // Nome ingrediente con stile migliorato
        TextField nomeField = new TextField(ingrediente.getNome());
        nomeField.setPromptText("Nome ingrediente");
        nomeField.getStyleClass().add("ingredient-name-field");
        nomeField.textProperty().addListener((obs, oldVal, newVal) -> {
            ingrediente.setNome(newVal);
            notifyControllerOfChange();
        });

        // Quantità come TextField che accetta solo numeri
        TextField quantitaField = new TextField(ingrediente.getQuantita() == 0 ? "" : String.valueOf(ingrediente.getQuantita()));
        quantitaField.setPromptText("Quantità");
        quantitaField.getStyleClass().add("ingredient-quantity-field");
        // Validatore per accettare solo numeri (inclusi decimali)
        quantitaField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                quantitaField.setText(oldVal);
            } else {
                try {
                    if (!newVal.isEmpty() && !newVal.equals(".")) {
                        float value = Float.parseFloat(newVal);
                        if (value >= 0) {
                            ingrediente.setQuantita(value);
                            notifyControllerOfChange();
                        }
                    } else {
                        ingrediente.setQuantita(0);
                    }
                } catch (NumberFormatException e) {
                    // Mantieni il valore precedente se non è valido
                }
            }
        });

        // Unità di misura come ComboBox
        ComboBox<String> unitaCombo = new ComboBox<>();
        unitaCombo.getItems().addAll("g", "kg", "ml", "l", "cucchiaino", "cucchiaio", "tazza", "pz", "spicchio", "foglia", "rametto");
        unitaCombo.setValue(ingrediente.getUnitaMisura() != null && !ingrediente.getUnitaMisura().isEmpty() ? ingrediente.getUnitaMisura() : null);
        unitaCombo.setEditable(true);
        unitaCombo.setPromptText("Unità");
        unitaCombo.getStyleClass().add("combo-box");
        unitaCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ingrediente.setUnitaMisura(newVal);
                notifyControllerOfChange();
            }
        });

        Button removeBtn = new Button("✕");
        removeBtn.getStyleClass().add("remove-ingredient-button");
        removeBtn.setOnAction(e -> {
            ricetta.getIngredientiRicetta().remove(ingrediente);
            container.getChildren().remove(ingredientBox);
            notifyControllerOfChange();
        });

        ingredientBox.getChildren().addAll(nomeField, quantitaField, unitaCombo, removeBtn);
        return ingredientBox;
    }
    
    // === METODI DI UTILITÀ ===
    
    /**
     * Configura il formatter per gli spinner dell'orario
     */
    private void setupTimeSpinnerFormatter(Spinner<Integer> spinner, boolean isMinute) {
        StringConverter<Integer> converter = new StringConverter<Integer>() {
            @Override
            public String toString(Integer value) {
                return value == null ? "00" : String.format("%02d", value);
            }
            @Override
            public Integer fromString(String text) {
                try {
                    int val = Integer.parseInt(text);
                    if (isMinute) {
                        return Math.max(0, Math.min(59, val));
                    } else {
                        return Math.max(6, Math.min(23, val));
                    }
                } catch (NumberFormatException e) {
                    return spinner.getValue();
                }
            }
        };
        spinner.getValueFactory().setConverter(converter);
        spinner.setEditable(true);
    }
    
    /**
     * Aggiunge un validatore di testo a un campo
     */
    private void addTextValidator(TextField field, String regexToRemove, int maxLength) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal.replaceAll(regexToRemove, "");
            if (maxLength > 0 && filtered.length() > maxLength) {
                filtered = filtered.substring(0, maxLength);
            }
            if (!filtered.equals(newVal)) {
                field.setText(filtered);
            }
        });
    }
    
    /**
     * Notifica il controller dei cambiamenti nella UI ibrida
     */
    private void notifyControllerOfChange() {
        if (controller != null) {
            controller.onHybridUIUpdated();
        }
    }
    
    // === GETTER PER IL CONTROLLER ===
    
    /**
     * Restituisce i dati delle sessioni ibride per la validazione
     */
    public List<HybridSessionData> getHybridSessions() {
        return new ArrayList<>(hybridSessions);
    }
    
    /**
     * Verifica se tutte le sessioni ibride sono valide
     */
    public boolean areAllHybridSessionsValid() {
        return hybridSessions.stream().allMatch(session -> 
            session.isValid() && session.isPresenceSessionValid() && session.isOnlineSessionValid()
        );
    }
    
    /**
     * Validazione completa della form - chiamata dal Controller
     */
    public boolean isFormValid() {
        String lessonType = lessonTypeComboBox.getValue();
        
        if ("In presenza".equals(lessonType)) {
            return areAllPresenceRecipesValid();
        } else if ("Telematica".equals(lessonType)) {
            // Per le lezioni telematiche non servono ricette
            return true;
        } else if ("Entrambi".equals(lessonType)) {
            return areAllHybridSessionsValid();
        }
        
        return false;
    }
    
    /**
     * Restituisce le ricette delle sessioni in presenza per il salvataggio
     */
    public Map<LocalDate, ObservableList<Ricetta>> getSessionePresenzaRicette() {
        return new HashMap<>(sessionePresenzaRicette);
    }
    
    /**
     * Restituisce le ricette generiche per il salvataggio  
     */
    public ObservableList<Ricetta> getGenericRecipes() {
        return FXCollections.observableArrayList(genericRecipes);
    }
    
    /**
     * Verifica se tutte le ricette in presenza sono valide
     */
    public boolean areAllPresenceRecipesValid() {
        // Verifica ricette per sessioni specifiche
        for (ObservableList<Ricetta> ricette : sessionePresenzaRicette.values()) {
            if (!areRecipesValid(ricette)) {
                return false;
            }
        }
        
        // Verifica ricette generiche
        if (!genericRecipes.isEmpty() && !areRecipesValid(genericRecipes)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Verifica se una lista di ricette è valida
     */
    private boolean areRecipesValid(ObservableList<Ricetta> ricette) {
        if (ricette == null || ricette.isEmpty()) {
            return false;
        }
        
        for (Ricetta ricetta : ricette) {
            if (ricetta.getNome() == null || ricetta.getNome().trim().isEmpty()) {
                return false;
            }
            
            List<Ingredienti> ingredienti = ricetta.getIngredientiRicetta();
            if (ingredienti == null || ingredienti.isEmpty()) {
                return false;
            }
            
            for (Ingredienti ing : ingredienti) {
                if (ing.getNome() == null || ing.getNome().trim().isEmpty()) {
                    return false;
                }
                if (ing.getQuantita() <= 0) {
                    return false;
                }
                if (ing.getUnitaMisura() == null || ing.getUnitaMisura().trim().isEmpty()) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    // === EVENT HANDLERS ===
    
    // === EVENT HANDLERS ===
    
    @FXML
    private void onLessonTypeChanged(ActionEvent event) {
        if (controller != null) {
            controller.onLessonTypeChanged();
        }
    }

    @FXML
    private void onFrequencyChanged(ActionEvent event) {
        // Aggiorna la UI della sezione "Entrambi" quando cambia la frequenza
        if (controller != null) {
            controller.onFrequencyChanged();
        }
    }

    @FXML
    private void selectImage(ActionEvent event) {
        if (controller != null) {
            controller.selectImage();
        }
    }

    @FXML
    private void createCourse(ActionEvent event) {
        if (controller != null) {
            controller.createCourse();
        }
    }

    @FXML
    private void cancelCreation(ActionEvent event) {
        if (controller != null) {
            controller.goToHomepage();
        }
    }

    @FXML
    private void goToHomepage(ActionEvent event) {
        if (controller != null) {
            controller.goToHomepage();
        }
    }

    @FXML
    private void goToCreateCourse(ActionEvent event) {
        // Già nella pagina di creazione corso
    }

    @FXML
    private void goToMonthlyReport(ActionEvent event) {
        if (controller != null) {
            controller.goToMonthlyReport();
        }
    }

    @FXML
    private void goToAccountManagement(ActionEvent event) {
        if (controller != null) {
            controller.goToAccountManagement();
        }
    }

    @FXML
    private void LogoutClick(ActionEvent event) {
        if (controller != null) {
            controller.LogoutClick();
        }
    }

    @FXML
    private void addRecipe(ActionEvent event) {
        // Gestione intelligente dell'aggiunta ricette
        if (sessionePresenzaRicette.isEmpty()) {
            // Modalità ricette generiche
            if (genericRecipes.isEmpty()) {
                setupGenericRecipes();
            } else {
                // Aggiungi una ricetta alla lista esistente
                addGenericRecipeToExisting();
            }
        } else {
            // Se ci sono sessioni specifiche, non permettere ricette generiche
            System.out.println("Boundary: Ricette per sessioni specifiche già configurate");
        }
        
        notifyControllerOfChange();
    }
    
    /**
     * Aggiunge una ricetta generica alla UI esistente
     */
    private void addGenericRecipeToExisting() {
        Ricetta nuova = new Ricetta("");
        nuova.setIngredientiRicetta(new ArrayList<>());
        nuova.getIngredientiRicetta().add(new Ingredienti("", 0, ""));
        genericRecipes.add(nuova);
        
        // Trova il contenitore delle ricette e aggiungi la nuova ricetta
        for (javafx.scene.Node node : recipesContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox recipesBox = (VBox) node;
                // Trova l'ultimo pulsante e inserisci prima
                for (int i = recipesBox.getChildren().size() - 1; i >= 0; i--) {
                    if (recipesBox.getChildren().get(i) instanceof Button) {
                        VBox recipeBox = createRecipeBox(nuova, genericRecipes, recipesBox);
                        recipesBox.getChildren().add(i, recipeBox);
                        break;
                    }
                }
                break;
            }
        }
    }
    
    // === METODI PER LA GESTIONE DELLE RICETTE IN PRESENZA ===
    
    /**
     * Pulisce la UI delle ricette
     */
    public void clearRecipesUI() {
        recipesContainer.getChildren().clear();
        sessionePresenzaRicette.clear();
        genericRecipes.clear();
    }
    
    /**
     * Configura la UI per le sessioni in presenza con date specifiche
     */
    public void updatePresenceSessionsUI(List<LocalDate> dateSessioni) {
        recipesContainer.getChildren().clear();
        sessionePresenzaRicette.clear();
        genericRecipes.clear();
        
        Label titleLabel = new Label("Ricette per le sessioni in presenza:");
        titleLabel.getStyleClass().add("section-title");
        // Style moved to CSS
        recipesContainer.getChildren().add(titleLabel);
        
        for (LocalDate data : dateSessioni) {
            VBox sessionBox = createSessionRecipeBox(data);
            recipesContainer.getChildren().add(sessionBox);
        }
    }
    
    /**
     * Configura la UI per ricette generiche (sessione singola)
     */
    public void setupGenericRecipes() {
        recipesContainer.getChildren().clear();
        sessionePresenzaRicette.clear();
        genericRecipes.clear();
        
        Label titleLabel = new Label("Ricette del corso:");
        titleLabel.getStyleClass().add("section-title");
        // Style moved to CSS
        
        VBox recipesBox = new VBox(10);
        
        // Aggiungi una ricetta iniziale
        Ricetta ricettaIniziale = new Ricetta("");
        ricettaIniziale.setIngredientiRicetta(new ArrayList<>());
        ricettaIniziale.getIngredientiRicetta().add(new Ingredienti("", 0, ""));
        genericRecipes.add(ricettaIniziale);
        
        VBox recipeBox = createRecipeBox(ricettaIniziale, genericRecipes, recipesBox);
        recipesBox.getChildren().add(recipeBox);
        
        Button addRecipeBtn = new Button("+ Aggiungi ricetta");
        addRecipeBtn.getStyleClass().add("add-recipe-button");
        addRecipeBtn.setOnAction(e -> addGenericRecipeToExisting());
        recipesBox.getChildren().add(addRecipeBtn);
        
        recipesContainer.getChildren().addAll(titleLabel, recipesBox);
    }
    
    /**
     * Crea il box per una sessione specifica con le sue ricette
     */
    private VBox createSessionRecipeBox(LocalDate data) {
        VBox sessionBox = new VBox(10);
        sessionBox.getStyleClass().add("session-recipe-box");
        // Style moved to CSS
        
        String dateString = data.getDayOfWeek().toString() + " " + data.toString();
        Label sessionLabel = new Label("Sessione del " + dateString);
        sessionLabel.getStyleClass().add("session-title");
        // Style moved to CSS
        
        // Crea la lista delle ricette per questa sessione
        ObservableList<Ricetta> sessionRecipes = FXCollections.observableArrayList();
        sessionePresenzaRicette.put(data, sessionRecipes);
        
        // Aggiungi una ricetta iniziale
        Ricetta ricettaIniziale = new Ricetta("");
        ricettaIniziale.setIngredientiRicetta(new ArrayList<>());
        ricettaIniziale.getIngredientiRicetta().add(new Ingredienti("", 0, ""));
        sessionRecipes.add(ricettaIniziale);
        
        VBox recipesBox = new VBox(8);
        VBox recipeBox = createRecipeBox(ricettaIniziale, sessionRecipes, recipesBox);
        recipesBox.getChildren().add(recipeBox);
        
        Button addRecipeBtn = new Button("+ Aggiungi ricetta per questa sessione");
        addRecipeBtn.getStyleClass().add("add-recipe-button");
        addRecipeBtn.setOnAction(e -> {
            Ricetta nuova = new Ricetta("");
            nuova.setIngredientiRicetta(new ArrayList<>());
            nuova.getIngredientiRicetta().add(new Ingredienti("", 0, ""));
            sessionRecipes.add(nuova);
            
            VBox newRecipeBox = createRecipeBox(nuova, sessionRecipes, recipesBox);
            recipesBox.getChildren().add(recipesBox.getChildren().size() - 1, newRecipeBox);
            notifyControllerOfChange();
        });
        recipesBox.getChildren().add(addRecipeBtn);
        
        sessionBox.getChildren().addAll(sessionLabel, recipesBox);
        return sessionBox;
    }
    
    /**
     * Crea il box per una singola ricetta
     */
    private VBox createRecipeBox(Ricetta ricetta, ObservableList<Ricetta> recipesList, VBox container) {
        VBox recipeBox = new VBox(10);
        recipeBox.getStyleClass().add("recipe-box");
        // Style moved to CSS
        
        // Nome ricetta
        HBox nameBox = new HBox(10);
        Label nameLabel = new Label("Nome ricetta:");
        TextField nameField = new TextField(ricetta.getNome());
        nameField.setPromptText("es. Pasta alla carbonara");
        nameField.getStyleClass().add("ingredient-name-field");
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            ricetta.setNome(newVal);
            notifyControllerOfChange();
        });

        Button removeBtn = new Button("Rimuovi");
        removeBtn.getStyleClass().add("remove-button");
        removeBtn.setOnAction(e -> {
            recipesList.remove(ricetta);
            container.getChildren().remove(recipeBox);
            notifyControllerOfChange();
        });
        nameBox.getChildren().addAll(nameLabel, nameField, removeBtn);
        
        // Container per ingredienti
        VBox ingredientsContainer = new VBox(5);
        Label ingredientsLabel = new Label("Ingredienti:");
        ingredientsLabel.getStyleClass().add("ingredients-title");
        
        // Se la ricetta non ha ingredienti, aggiungine uno vuoto
        if (ricetta.getIngredientiRicetta() == null || ricetta.getIngredientiRicetta().isEmpty()) {
            ricetta.setIngredientiRicetta(new ArrayList<>());
            ricetta.getIngredientiRicetta().add(new Ingredienti("", 0, ""));
        }
        
        // Crea la UI per ogni ingrediente
        for (Ingredienti ingrediente : ricetta.getIngredientiRicetta()) {
            HBox ingredientBox = createIngredientBox(ingrediente, ricetta, ingredientsContainer);
            ingredientsContainer.getChildren().add(ingredientBox);
        }
        
        Button addIngredientBtn = new Button("+ Ingrediente");
        addIngredientBtn.getStyleClass().add("add-ingredient-button");
        addIngredientBtn.setOnAction(e -> {
            Ingredienti nuovo = new Ingredienti("", 0, "");
            ricetta.getIngredientiRicetta().add(nuovo);
            HBox ingredientBox = createIngredientBox(nuovo, ricetta, ingredientsContainer);
            ingredientsContainer.getChildren().add(ingredientsContainer.getChildren().size() - 1, ingredientBox);
            notifyControllerOfChange();
        });
        ingredientsContainer.getChildren().add(addIngredientBtn);
        
        recipeBox.getChildren().addAll(nameBox, ingredientsLabel, ingredientsContainer);
        return recipeBox;
    }
    
    /**
     * Crea il box per un singolo ingrediente
     */
    private HBox createIngredientBox(Ingredienti ingrediente, Ricetta ricetta, VBox container) {
        HBox ingredientBox = new HBox(15);
        ingredientBox.getStyleClass().addAll("ingredient-box", "ingredient-row");

        // Nome ingrediente con stile migliorato
        TextField nomeField = new TextField(ingrediente.getNome());
        nomeField.setPromptText("Nome ingrediente");
        nomeField.getStyleClass().add("ingredient-name-field");
        nomeField.textProperty().addListener((obs, oldVal, newVal) -> {
            ingrediente.setNome(newVal);
            notifyControllerOfChange();
        });

        // Quantità come TextField che accetta solo numeri
        TextField quantitaField = new TextField(ingrediente.getQuantita() == 0 ? "" : String.valueOf(ingrediente.getQuantita()));
        quantitaField.setPromptText("Quantità");
        quantitaField.getStyleClass().add("ingredient-quantity-field");
        // Validatore per accettare solo numeri (inclusi decimali)
        quantitaField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                quantitaField.setText(oldVal);
            } else {
                try {
                    if (!newVal.isEmpty() && !newVal.equals(".")) {
                        float value = Float.parseFloat(newVal);
                        if (value >= 0) {
                            ingrediente.setQuantita(value);
                            notifyControllerOfChange();
                        }
                    } else {
                        ingrediente.setQuantita(0);
                    }
                } catch (NumberFormatException e) {
                    // Mantieni il valore precedente se non è valido
                }
            }
        });

        // Unità di misura come ComboBox
        ComboBox<String> unitaCombo = new ComboBox<>();
        unitaCombo.getItems().addAll("g", "kg", "ml", "l", "cucchiaino", "cucchiaio", "tazza", "pz", "spicchio", "foglia", "rametto");
        unitaCombo.setValue(ingrediente.getUnitaMisura() != null && !ingrediente.getUnitaMisura().isEmpty() ? ingrediente.getUnitaMisura() : null);
        unitaCombo.setEditable(true);
        unitaCombo.setPromptText("Unità");
        unitaCombo.getStyleClass().add("combo-box");
        unitaCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ingrediente.setUnitaMisura(newVal);
                notifyControllerOfChange();
            }
        });

        Button removeBtn = new Button("✕");
        removeBtn.getStyleClass().add("remove-ingredient-button");
        removeBtn.setOnAction(e -> {
            ricetta.getIngredientiRicetta().remove(ingrediente);
            container.getChildren().remove(ingredientBox);
            notifyControllerOfChange();
        });

        ingredientBox.getChildren().addAll(nomeField, quantitaField, unitaCombo, removeBtn);
        return ingredientBox;
    }
}