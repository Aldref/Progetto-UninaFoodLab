package com.progetto.boundary;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import java.time.LocalTime;

import com.progetto.Entity.EntityDto.Sessione;
import com.progetto.Entity.EntityDto.SessioneOnline;
import com.progetto.Entity.EntityDto.SessioniInPresenza;

import com.progetto.utils.UnifiedRecipeIngredientUI;
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
    // Track controls for each hybrid session for robust validation
    private final List<Map<String, Control>> hybridSessionControlsList = new ArrayList<>();

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
    private ImageView chefProfileImage;
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
    // Ora contiene SessioniInPresenza o SessioneOnline
private final List<Sessione> hybridSessions = new ArrayList<>();

    // === DATI PER LE SESSIONI TELEMATICA PURE ===
    /**
     * Restituisce la lista delle sessioni telematiche pure per "Telematica" (non ibride) come DTO SessioneOnline
     */
    public List<SessioneOnline> getSessioniTelematiche() {
        List<SessioneOnline> result = new ArrayList<>();
        if ("Telematica".equals(lessonTypeComboBox.getValue())) {
            List<String> giorniSelezionati = new ArrayList<>();
            for (Node node : onlineDayOfWeekContainer.getChildren()) {
                if (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
                    giorniSelezionati.add(((CheckBox) node).getText());
                }
            }
            LocalDate inizio = startDatePicker.getValue();
            LocalDate fine = endDatePicker.getValue();
            if (inizio != null && fine != null && !giorniSelezionati.isEmpty()) {
                for (String giorno : giorniSelezionati) {
                    java.time.DayOfWeek target = null;
                    switch (giorno.toLowerCase()) {
                        case "lunedì": target = java.time.DayOfWeek.MONDAY; break;
                        case "martedì": target = java.time.DayOfWeek.TUESDAY; break;
                        case "mercoledì": target = java.time.DayOfWeek.WEDNESDAY; break;
                        case "giovedì": target = java.time.DayOfWeek.THURSDAY; break;
                        case "venerdì": target = java.time.DayOfWeek.FRIDAY; break;
                        case "sabato": target = java.time.DayOfWeek.SATURDAY; break;
                        case "domenica": target = java.time.DayOfWeek.SUNDAY; break;
                    }
                    if (target == null) continue;
                    LocalDate current = inizio;
                    while (current.getDayOfWeek() != target) {
                        current = current.plusDays(1);
                        if (current.isAfter(fine)) break;
                    }
                    while (!current.isAfter(fine)) {
                        // Crea una SessioneOnline per ogni data
                        SessioneOnline sessione = new SessioneOnline(
                            giorno,
                            current,
                            LocalTime.of(onlineHourSpinner.getValue(), onlineMinuteSpinner.getValue()),
                            LocalTime.of(Integer.parseInt(onlineDurationField.getText().isEmpty() ? "1" : onlineDurationField.getText()), 0),
                            applicationComboBox.getValue() != null ? applicationComboBox.getValue() : "",
                            meetingCodeField.getText() != null ? meetingCodeField.getText() : "",
                            "",
                            -1
                        );
                        result.add(sessione);
                        current = current.plusWeeks(1);
                    }
                }
            }
        }
        return result;
    }
    
    // === DATI PER LE SESSIONI IN PRESENZA ===
    private final Map<LocalDate, ObservableList<Ricetta>> sessionePresenzaRicette = new HashMap<>();
    private final ObservableList<Ricetta> genericRecipes = FXCollections.observableArrayList();
    private List<String> giorniSettimanaEnum = new ArrayList<>();
    private List<String> unitaDiMisuraEnum = new ArrayList<>();
    // RIMOSSA HybridSessionData: ora si usano SessioniInPresenza e SessioneOnline

    @FXML
    public void initialize() {
        // Aggiorna subito la label con il nome/cognome dello chef loggato
        com.progetto.Entity.EntityDto.Chef chef = com.progetto.Entity.EntityDto.Chef.loggedUser;
        if (chefNameLabel != null && chef != null) {
            chefNameLabel.setText(chef.getNome() + " " + chef.getCognome());
        }
        // Carica e mostra la propic rotonda come in HomepageChefBoundary
        if (chef != null && chefProfileImage != null && chef.getUrl_Propic() != null && !chef.getUrl_Propic().isEmpty()) {
            System.out.println("URL propic chef: " + chef.getUrl_Propic());
            try {
                java.io.File imgFile = new java.io.File("src/main/resources/" + chef.getUrl_Propic());
                javafx.scene.image.Image img;
                if (imgFile.exists()) {
                    img = new javafx.scene.image.Image(imgFile.toURI().toString(), 80, 80, true, true);
                } else {
                    img = new javafx.scene.image.Image(chef.getUrl_Propic(), 80, 80, true, true);
                }
                chefProfileImage.setImage(img);
                // Clip circolare
                com.progetto.utils.ImageClipUtils.setCircularClip(chefProfileImage, 0);
                System.out.println("Caricata immagine: " + chef.getUrl_Propic());
                if (img.isError()) {
                    System.out.println("Errore caricamento immagine: " + img.getException());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Imposta dimensioni fisse e stile rotondo
        chefProfileImage.setFitWidth(80);
        chefProfileImage.setFitHeight(80);
        chefProfileImage.setPreserveRatio(true);
        controller = new CreateCourseController(
            courseNameField, descriptionArea, startDatePicker, endDatePicker,
            frequencyComboBox, lessonTypeComboBox, maxParticipantsSpinner,
            priceField, courseImageView, chefProfileImage, chefNameLabel, createButton,
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
        // Carica enum dal DB (tramite DAO, come fallback se controller non li espone)
        try {
            com.progetto.Entity.entityDao.BarraDiRicercaDao dao = new com.progetto.Entity.entityDao.BarraDiRicercaDao();
            giorniSettimanaEnum = dao.GiorniSettimanaEnum();
            unitaDiMisuraEnum = dao.GrandezzeDiMisura();
        } catch (Exception e) {
            giorniSettimanaEnum = java.util.Arrays.asList("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica");
            unitaDiMisuraEnum = java.util.Arrays.asList("g", "kg", "ml", "l", "pz");
        }
    }

    // === METODI PER CREARE UI DINAMICA ===
    
    /**
     * Configura la UI per la sezione ibrida in base alla situazione
     */
    public void setupHybridUI(String frequenza, LocalDate inizio, LocalDate fine) {
        hybridDaysContainer.getChildren().clear();
        hybridSessions.clear();
        hybridSessionControlsList.clear();

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
        Label titleLabel = new Label("Configura per ogni giorno della settimana:");
        titleLabel.getStyleClass().add("section-title");
        hybridDaysContainer.getChildren().add(titleLabel);

        List<String> giorniSettimana = giorniSettimanaEnum.isEmpty() ? java.util.Arrays.asList("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica") : giorniSettimanaEnum;
        List<ComboBox<String>> dayCombos = new ArrayList<>();

        for (int i = 0; i < numSessioni; i++) {
            final int sessionIndex = i;
            VBox sessionBox = new VBox(10);
            sessionBox.getStyleClass().add("hybrid-session-container");

            // Map to track controls for this session
            Map<String, Control> sessionControls = new HashMap<>();

            // Selettore giorno della settimana
            HBox dayBox = new HBox(10);
            Label dayLabel = new Label("Giorno " + (sessionIndex + 1) + ":");
            ComboBox<String> dayCombo = new ComboBox<>();
            dayCombo.getItems().addAll(giorniSettimana);
            dayCombo.setPromptText("Seleziona giorno");
            dayBox.getChildren().addAll(dayLabel, dayCombo);
            dayCombos.add(dayCombo);

            sessionControls.put("dayCombo", dayCombo);

            // Imposta la cell factory custom per disabilitare i giorni già scelti
            dayCombo.setCellFactory(lv -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                    if (empty || item == null) {
                        setDisable(false);
                    } else {
                        boolean disable = false;
                        for (ComboBox<String> otherCombo : dayCombos) {
                            if (otherCombo != dayCombo && item.equals(otherCombo.getValue())) {
                                disable = true;
                                break;
                            }
                        }
                        setDisable(disable);
                    }
                }
            });

            // Listener per impedire la selezione di un giorno già scelto
            dayCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    for (ComboBox<String> otherCombo : dayCombos) {
                        if (otherCombo != dayCombo && newVal.equals(otherCombo.getValue())) {
                            dayCombo.setValue(null);
                            showHybridErrorMessage("Ogni giorno può essere scelto solo una volta tra tutte le sessioni.");
                            return;
                        }
                    }
                }
                updateHybridDayCombos(dayCombos);
            });

            HBox typeBox = new HBox(10);
            Label typeLabel = new Label("Tipo sessione:");
            ComboBox<String> typeCombo = new ComboBox<>();
            typeCombo.getItems().addAll("In presenza", "Telematica");
            typeCombo.setPromptText("Tipo lezione");
            typeBox.getChildren().addAll(typeLabel, typeCombo);

            sessionControls.put("typeCombo", typeCombo);

            // Orario
            HBox timeBox = new HBox(10);
            Label timeLabel = new Label("Orario:");
            Spinner<Integer> hourSpinner = new Spinner<>(6, 23, 18);
            setupTimeSpinnerFormatter(hourSpinner, false);
            Label colonLabel = new Label(":");
            Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0, 15);
            setupTimeSpinnerFormatter(minuteSpinner, true);
            timeBox.getChildren().addAll(timeLabel, hourSpinner, colonLabel, minuteSpinner);

            sessionControls.put("hourSpinner", hourSpinner);
            sessionControls.put("minuteSpinner", minuteSpinner);

            // Durata (in ore, 1-8)
            HBox durationBox = new HBox(10);
            Label durationLabel = new Label("Durata (ore):");
            TextField durationField = new TextField();
            durationField.setPromptText("es. 2");
            addTextValidator(durationField, "[^\\d]", 1);
            durationBox.getChildren().addAll(durationLabel, durationField);

            sessionControls.put("durationField", durationField);

            VBox detailsContainer = new VBox(10);

            // --- PATCH: Ricette per ogni sessione ibrida in presenza (tutte le date del giorno selezionato) ---
            VBox recipesSectionBox = new VBox(10);
            recipesSectionBox.getStyleClass().add("recipes-section");
            recipesSectionBox.setVisible(false);

            Runnable updateHybridSessionDetails = () -> {
                detailsContainer.getChildren().clear();
                detailsContainer.getChildren().addAll(timeBox, durationBox);
                recipesSectionBox.getChildren().clear();
                recipesSectionBox.setVisible(false);
                if ("In presenza".equals(typeCombo.getValue()) && dayCombo.getValue() != null) {
                    VBox presenceDetails = createPresenceDetails();
                    detailsContainer.getChildren().add(presenceDetails);
                    // Find and store city, street, cap fields
                    for (Node hbox : presenceDetails.getChildren()) {
                        if (hbox instanceof HBox) {
                            List<Node> fields = ((HBox) hbox).getChildren();
                            if (fields.size() == 2 && fields.get(1) instanceof TextField) {
                                String label = ((Label) fields.get(0)).getText().toLowerCase();
                                if (label.contains("citt")) sessionControls.put("cityField", (TextField) fields.get(1));
                                else if (label.contains("via")) sessionControls.put("streetField", (TextField) fields.get(1));
                                else if (label.contains("cap")) sessionControls.put("capField", (TextField) fields.get(1));
                            }
                        }
                    }
                    // Calcola tutte le date per il giorno selezionato
                    String giorno = dayCombo.getValue();
                    LocalDate inizioCorso = startDatePicker.getValue();
                    LocalDate fineCorso = endDatePicker.getValue();
                    List<LocalDate> possibiliDate = calcolaDateSessioniPresenzaHybrid(giorno, inizioCorso, fineCorso);
                    if (!possibiliDate.isEmpty()) {
                        for (LocalDate data : possibiliDate) {
                            if (!sessionePresenzaRicette.containsKey(data)) {
                                ObservableList<Ricetta> ricette = FXCollections.observableArrayList();
                                Ricetta ricettaIniziale = new Ricetta("");
                                ricettaIniziale.setIngredientiRicetta(new ArrayList<>());
                                ricettaIniziale.getIngredientiRicetta().add(new Ingredienti("", 0, ""));
                                ricette.add(ricettaIniziale);
                                sessionePresenzaRicette.put(data, ricette);
                            }
                            VBox sessionRecipeBox = createSessionRecipeBox(data);
                            sessionRecipeBox.setId("recipes-" + data.toString());
                            recipesSectionBox.getChildren().add(sessionRecipeBox);
                        }
                        recipesSectionBox.setVisible(true);
                    }
                } else if ("Telematica".equals(typeCombo.getValue())) {
                    VBox onlineDetails = createOnlineDetails();
                    detailsContainer.getChildren().add(onlineDetails);
                    // Find and store applicationComboBox and meetingCodeField
                    for (Node hbox : onlineDetails.getChildren()) {
                        if (hbox instanceof HBox) {
                            List<Node> fields = ((HBox) hbox).getChildren();
                            if (fields.size() == 2) {
                                if (fields.get(1) instanceof ComboBox) sessionControls.put("applicationComboBox", (ComboBox<?>) fields.get(1));
                                else if (fields.get(1) instanceof TextField) {
                                    String label = ((Label) fields.get(0)).getText().toLowerCase();
                                    if (label.contains("codice")) sessionControls.put("meetingCodeField", (TextField) fields.get(1));
                                }
                            }
                        }
                    }
                }
            };

            // Listener per mostrare i dettagli in base al tipo e giorno
            typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                updateHybridSessionDetails.run();
                notifyControllerOfChange();
            });

            // Listener per mostrare la sezione ricette quando si seleziona il giorno
            dayCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                updateHybridSessionDetails.run();
            });

            sessionBox.getChildren().addAll(dayBox, typeBox, detailsContainer, recipesSectionBox);
            hybridDaysContainer.getChildren().add(sessionBox);

            // Add this session's controls map to the list
            hybridSessionControlsList.add(sessionControls);
        }


        // Listener per aggiornare le opzioni disponibili in tutti i ComboBox giorno (aggiorna cell factory)
        for (ComboBox<String> combo : dayCombos) {
            combo.valueProperty().addListener((obs, oldVal, newVal) -> {
                for (ComboBox<String> c : dayCombos) {
                    c.setButtonCell(null); // forza refresh
                    c.setCellFactory(lv -> new javafx.scene.control.ListCell<String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(item);
                            if (empty || item == null) {
                                setDisable(false);
                            } else {
                                boolean disable = false;
                                for (ComboBox<String> otherCombo : dayCombos) {
                                    if (otherCombo != c && item.equals(otherCombo.getValue())) {
                                        disable = true;
                                        break;
                                    }
                                }
                                setDisable(disable);
                            }
                        }
                    });
                }
            });
        }

        if (controller != null) {
            controller.onHybridUIUpdated();
        }
    }

    /**
     * Disabilita nei ComboBox dei giorni le opzioni già selezionate nelle altre sessioni
     */
    private void updateHybridDayCombos(List<ComboBox<String>> dayCombos) {
        // Raccogli i giorni già selezionati
        List<String> selectedDays = new ArrayList<>();
        for (ComboBox<String> combo : dayCombos) {
            String val = combo.getValue();
            if (val != null && !val.isEmpty()) {
                selectedDays.add(val);
            }
        }
        for (ComboBox<String> combo : dayCombos) {
            String current = combo.getValue();
            combo.getItems().forEach(day -> {
                boolean disable = selectedDays.contains(day) && !day.equals(current);
                combo.setCellFactory(lv -> new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        setDisable(disable && item != null && item.equals(day));
                    }
                });
            });
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
            VBox sessionBox = createHybridSessionBox(i);
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
    private VBox createHybridSessionBox(int sessionIndex) {
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
        typeBox.getChildren().addAll(typeLabel, typeCombo);

        // Data della sessione
        HBox dateBox = new HBox(10);
        Label dateLabel = new Label("Data:");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Seleziona data");
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

        // Container per dettagli specifici (presenza o telematica)
        VBox detailsContainer = new VBox(10);

        // Container per la sezione ricette di questa sessione (solo per "In presenza")
        VBox recipesSectionBox = new VBox(10);
        recipesSectionBox.getStyleClass().add("recipes-section");

        // Listener per mostrare campi specifici in base al tipo
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            detailsContainer.getChildren().clear();
            recipesSectionBox.getChildren().clear();
            // Orario
            HBox timeBox = new HBox(10);
            Label timeLabel = new Label("Orario:");
            Spinner<Integer> hourSpinner = new Spinner<>(6, 23, 18);
            setupTimeSpinnerFormatter(hourSpinner, false);
            Label colonLabel = new Label(":");
            Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0, 15);
            setupTimeSpinnerFormatter(minuteSpinner, true);
            timeBox.getChildren().addAll(timeLabel, hourSpinner, colonLabel, minuteSpinner);

            // Durata
            HBox durationBox = new HBox(10);
            Label durationLabel = new Label("Durata (minuti):");
            TextField durationField = new TextField();
            durationField.setPromptText("es. 120");
            addTextValidator(durationField, "[^\\d]", 3);
            durationField.textProperty().addListener((obs2, oldVal2, newVal2) -> notifyControllerOfChange());
            durationBox.getChildren().addAll(durationLabel, durationField);

            detailsContainer.getChildren().addAll(timeBox, durationBox);

            if ("In presenza".equals(newVal)) {
                VBox presenceDetails = createPresenceDetails();
                // Aggiungi listener a tutti i campi dinamici della presenza
                for (javafx.scene.Node node : presenceDetails.getChildren()) {
                    if (node instanceof HBox) {
                        HBox hbox = (HBox) node;
                        for (javafx.scene.Node fieldNode : hbox.getChildren()) {
                            if (fieldNode instanceof TextField) {
                                ((TextField) fieldNode).textProperty().addListener((o, ov, nv) -> notifyControllerOfChange());
                            }
                        }
                    }
                }
                detailsContainer.getChildren().addAll(presenceDetails);

                // Listener per la data: mostra la sezione ricette per questa sessione
                datePicker.valueProperty().addListener((o, oldDate, newDate) -> {
                    if (oldDate != null) {
                        // Rimuovi la sezione ricette per la vecchia data
                        recipesContainer.getChildren().removeIf(node2 -> node2.getId() != null && node2.getId().equals("recipes-" + oldDate.toString()));
                        sessionePresenzaRicette.remove(oldDate);
                    }
                    if (newDate != null) {
                        // Se non esiste già una lista di ricette per questa data, creala
                        if (!sessionePresenzaRicette.containsKey(newDate)) {
                            ObservableList<Ricetta> ricette = FXCollections.observableArrayList();
                            Ricetta ricettaIniziale = new Ricetta("");
                            ricettaIniziale.setIngredientiRicetta(new ArrayList<>());
                            ricettaIniziale.getIngredientiRicetta().add(new Ingredienti("", 0, ""));
                            ricette.add(ricettaIniziale);
                            sessionePresenzaRicette.put(newDate, ricette);
                        }
                        // Crea o aggiorna la sezione ricette per questa data
                        VBox sessionRecipeBox = createSessionRecipeBox(newDate);
                        sessionRecipeBox.setId("recipes-" + newDate.toString());
                        // Rimuovi eventuale sezione già presente per questa data
                        recipesContainer.getChildren().removeIf(node2 -> node2.getId() != null && node2.getId().equals("recipes-" + newDate.toString()));
                        recipesContainer.getChildren().add(sessionRecipeBox);
                    }
                });
            } else if ("Telematica".equals(newVal)) {
                VBox onlineDetails = createOnlineDetails();
                // Aggiungi listener a tutti i campi dinamici della telematica
                for (javafx.scene.Node node : onlineDetails.getChildren()) {
                    if (node instanceof HBox) {
                        HBox hbox = (HBox) node;
                        for (javafx.scene.Node fieldNode : hbox.getChildren()) {
                            if (fieldNode instanceof TextField) {
                                ((TextField) fieldNode).textProperty().addListener((o, ov, nv) -> notifyControllerOfChange());
                            } else if (fieldNode instanceof ComboBox) {
                                ((ComboBox<?>) fieldNode).valueProperty().addListener((o, ov, nv) -> notifyControllerOfChange());
                            }
                        }
                    }
                }
                detailsContainer.getChildren().add(onlineDetails);
                // Se si cambia da "In presenza" a "Telematica", rimuovi la sezione ricette se presente
                datePicker.valueProperty().addListener((o, oldDate, newDate) -> {
                    if (oldDate != null) {
                        recipesContainer.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("recipes-" + oldDate.toString()));
                        sessionePresenzaRicette.remove(oldDate);
                    }
                    if (newDate != null) {
                        recipesContainer.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("recipes-" + newDate.toString()));
                        sessionePresenzaRicette.remove(newDate);
                    }
                });
            }
            notifyControllerOfChange();

            detailsContainer.getChildren().addAll(timeBox, durationBox);

            if ("In presenza".equals(newVal)) {
                VBox presenceDetails = createPresenceDetails();
                detailsContainer.getChildren().addAll(presenceDetails);

                // Listener per la data: mostra la sezione ricette per questa sessione
                datePicker.valueProperty().addListener((o, oldDate, newDate) -> {
                    if (oldDate != null) {
                        // Rimuovi la sezione ricette per la vecchia data
                        recipesContainer.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("recipes-" + oldDate.toString()));
                        sessionePresenzaRicette.remove(oldDate);
                    }
                    if (newDate != null) {
                        // Se non esiste già una lista di ricette per questa data, creala
                        if (!sessionePresenzaRicette.containsKey(newDate)) {
                            ObservableList<Ricetta> ricette = FXCollections.observableArrayList();
                            Ricetta ricettaIniziale = new Ricetta("");
                            ricettaIniziale.setIngredientiRicetta(new ArrayList<>());
                            ricettaIniziale.getIngredientiRicetta().add(new Ingredienti("", 0, ""));
                            ricette.add(ricettaIniziale);
                            sessionePresenzaRicette.put(newDate, ricette);
                        }
                        // Crea o aggiorna la sezione ricette per questa data
                        VBox sessionRecipeBox = createSessionRecipeBox(newDate);
                        sessionRecipeBox.setId("recipes-" + newDate.toString());
                        // Rimuovi eventuale sezione già presente per questa data
                        recipesContainer.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("recipes-" + newDate.toString()));
                        recipesContainer.getChildren().add(sessionRecipeBox);
                    }
                });
            } else if ("Telematica".equals(newVal)) {
                VBox onlineDetails = createOnlineDetails();
                detailsContainer.getChildren().add(onlineDetails);
                // Se si cambia da "In presenza" a "Telematica", rimuovi la sezione ricette se presente
                datePicker.valueProperty().addListener((o, oldDate, newDate) -> {
                    if (oldDate != null) {
                        recipesContainer.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("recipes-" + oldDate.toString()));
                        sessionePresenzaRicette.remove(oldDate);
                    }
                    if (newDate != null) {
                        recipesContainer.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("recipes-" + newDate.toString()));
                        sessionePresenzaRicette.remove(newDate);
                    }
                });
            }
            notifyControllerOfChange();
        });

        // Listener per aggiornare i dati della sessione e notificare il controller
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> notifyControllerOfChange());

        sessionBox.getChildren().addAll(sessionTitle, typeBox, dateBox, detailsContainer);
        return sessionBox;
    }
    
    /**
     * Crea i dettagli specifici per sessioni in presenza
     */
    private VBox createPresenceDetails() {
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
        cityBox.getChildren().addAll(cityLabel, cityField);

        // Via
        HBox streetBox = new HBox(10);
        Label streetLabel = new Label("Via:");
        TextField streetField = new TextField();
        streetField.setPromptText("es. Via Roma 123");
        streetField.textProperty().addListener((obs, oldVal, newVal) -> notifyControllerOfChange());
        streetBox.getChildren().addAll(streetLabel, streetField);

        // CAP
        HBox capBox = new HBox(10);
        Label capLabel = new Label("CAP:");
        TextField capField = new TextField();
        capField.setPromptText("es. 80100");
        addTextValidator(capField, "[^\\d]", 5);
        capField.textProperty().addListener((obs, oldVal, newVal) -> notifyControllerOfChange());
        capBox.getChildren().addAll(capLabel, capField);

        presenceBox.getChildren().addAll(cityBox, streetBox, capBox);
        return presenceBox;
    }
    
    /**
     * Crea i dettagli specifici per sessioni online
     */
    private VBox createOnlineDetails() {
        VBox onlineBox = new VBox(10);
        onlineBox.getStyleClass().add("online-details");

        // Applicazione
        HBox appBox = new HBox(10);
        Label appLabel = new Label("Applicazione:");
        ComboBox<String> appCombo = new ComboBox<>();
        appCombo.getItems().addAll("Zoom", "Microsoft Teams", "Google Meet");
        appCombo.setPromptText("Seleziona applicazione");
        appCombo.valueProperty().addListener((obs, oldVal, newVal) -> notifyControllerOfChange());
        appBox.getChildren().addAll(appLabel, appCombo);

        // Codice riunione
        HBox codeBox = new HBox(10);
        Label codeLabel = new Label("Codice riunione:");
        TextField codeField = new TextField();
        codeField.setPromptText("es. 123-456-789");
        codeField.textProperty().addListener((obs, oldVal, newVal) -> notifyControllerOfChange());
        codeBox.getChildren().addAll(codeLabel, codeField);

        onlineBox.getChildren().addAll(appBox, codeBox);
        return onlineBox;
    }
    
    /**
     * Crea la sezione ricette per una sessione ibrida
     */
    private VBox createRecipesSection(int sessionIndex, java.util.List<Ricetta> ricette) {
        // Hybrid session recipes UI logic removed: sessionData is no longer used.
        VBox recipesBox = new VBox(10);
        recipesBox.getStyleClass().add("recipes-section");
        Label recipesLabel = new Label("Ricette per questa sessione:");
        recipesLabel.getStyleClass().add("subsection-title");
        recipesBox.getChildren().add(recipesLabel);
        // Add your DTO-based recipe UI logic here if needed
        return recipesBox;
    }
    
    /**
     * Crea il box per una singola ricetta nella modalità ibrida
     */
    private VBox createRecipeBoxForHybrid(Ricetta ricetta, java.util.List<Ricetta> ricette, VBox container) {
        return UnifiedRecipeIngredientUI.createUnifiedRecipeBox(ricetta, ricette, container, true, this::notifyControllerOfChange, unitaDiMisuraEnum);
    }
    
    /**
     * Crea il box per un singolo ingrediente nella modalità ibrida
     */
    private HBox createIngredientBoxForHybrid(Ingredienti ingrediente, Ricetta ricetta, VBox container) {
        return UnifiedRecipeIngredientUI.createUnifiedIngredientBox(ingrediente, ricetta, container, true, this::notifyControllerOfChange, unitaDiMisuraEnum);
    }
    
    // === METODI DI UTILITÀ ===

    /**
     * Calcola tutte le date comprese tra inizio e fine che corrispondono al giorno della settimana specificato (in italiano)
     */
    private List<LocalDate> calcolaDateSessioniPresenzaHybrid(String giornoSettimana, LocalDate inizio, LocalDate fine) {
        List<LocalDate> date = new ArrayList<>();
        if (giornoSettimana == null || inizio == null || fine == null) return date;
        // Mappa giorni italiani a DayOfWeek
        Map<String, java.time.DayOfWeek> giorni = new HashMap<>();
        giorni.put("Lunedì", java.time.DayOfWeek.MONDAY);
        giorni.put("Martedì", java.time.DayOfWeek.TUESDAY);
        giorni.put("Mercoledì", java.time.DayOfWeek.WEDNESDAY);
        giorni.put("Giovedì", java.time.DayOfWeek.THURSDAY);
        giorni.put("Venerdì", java.time.DayOfWeek.FRIDAY);
        giorni.put("Sabato", java.time.DayOfWeek.SATURDAY);
        giorni.put("Domenica", java.time.DayOfWeek.SUNDAY);
        java.time.DayOfWeek target = giorni.get(giornoSettimana);
        if (target == null) return date;
        LocalDate current = inizio;
        // Trova il primo giorno corrispondente
        while (current.getDayOfWeek() != target) {
            current = current.plusDays(1);
            if (current.isAfter(fine)) return date;
        }
        // Aggiungi tutte le date corrispondenti
        while (!current.isAfter(fine)) {
            date.add(current);
            current = current.plusWeeks(1);
        }
        return date;
    }
    
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
            // Aggiorna la lista hybridSessions con i dati attuali della UI
            updateHybridSessionsFromUI();
            controller.onHybridUIUpdated();
        }
        // RIMOSSO: Non forzare manualmente lo stato del pulsante se è già bound in FXML o altrove
    }

    /**
     * Aggiorna la lista hybridSessions leggendo i dati attuali dalla UI ibrida
     */
    public void updateHybridSessionsFromUI() {
        hybridSessions.clear();
        // Usa hybridSessionControlsList per leggere i dati reali dei controlli UI
        for (Map<String, Control> controls : hybridSessionControlsList) {
            ComboBox<String> typeCombo = (ComboBox<String>) controls.get("typeCombo");
            ComboBox<String> dayCombo = (ComboBox<String>) controls.get("dayCombo");
            Spinner<Integer> hourSpinner = (Spinner<Integer>) controls.get("hourSpinner");
            Spinner<Integer> minuteSpinner = (Spinner<Integer>) controls.get("minuteSpinner");
            TextField durationField = (TextField) controls.get("durationField");
            // Base validation
            if (typeCombo == null || typeCombo.getValue() == null || dayCombo == null || dayCombo.getValue() == null ||
                hourSpinner == null || minuteSpinner == null || durationField == null || durationField.getText().isEmpty()) {
                continue;
            }
            String tipo = typeCombo.getValue();
            String giorno = dayCombo.getValue();
            LocalDate inizio = startDatePicker.getValue();
            LocalDate fine = endDatePicker.getValue();
            List<LocalDate> dateSessioni = calcolaDateSessioniPresenzaHybrid(giorno, inizio, fine);
            for (LocalDate data : dateSessioni) {
                LocalTime orario = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
                LocalTime durata;
                try {
                    int dur = Integer.parseInt(durationField.getText());
                    durata = LocalTime.of(dur, 0);
                } catch (Exception e) { durata = LocalTime.of(1, 0); }
                if ("In presenza".equals(tipo)) {
                    TextField cityField = (TextField) controls.get("cityField");
                    TextField streetField = (TextField) controls.get("streetField");
                    TextField capField = (TextField) controls.get("capField");
                    String citta = cityField != null ? cityField.getText() : "";
                    String via = streetField != null ? streetField.getText() : "";
                    String cap = capField != null ? capField.getText() : "";
                    if (citta.isEmpty() || via.isEmpty() || cap.isEmpty()) continue;
                    com.progetto.Entity.EntityDto.SessioniInPresenza s = new com.progetto.Entity.EntityDto.SessioniInPresenza(
                        giorno, data, orario, durata, citta, via, cap, null, 0
                    );
                    hybridSessions.add(s);
                } else if ("Telematica".equals(tipo)) {
                    ComboBox<String> applicationComboBox = (ComboBox<String>) controls.get("applicationComboBox");
                    TextField meetingCodeField = (TextField) controls.get("meetingCodeField");
                    String applicazione = applicationComboBox != null && applicationComboBox.getValue() != null ? applicationComboBox.getValue() : "";
                    String codice = meetingCodeField != null && meetingCodeField.getText() != null ? meetingCodeField.getText() : "";
                    if (applicazione.isEmpty() || codice.isEmpty()) continue;
                    com.progetto.Entity.EntityDto.SessioneOnline s = new com.progetto.Entity.EntityDto.SessioneOnline(
                        giorno, data, orario, durata, applicazione, codice, "", 0
                    );
                    hybridSessions.add(s);
                }
            }
        }
    }
    
    // === GETTER PER IL CONTROLLER ===
    
    /**
     * Restituisce i dati delle sessioni ibride per la validazione
     */
    // Restituisce le sessioni ibride come lista di Sessione (SessioniInPresenza o SessioneOnline)
    public List<Sessione> getHybridSessions() {
        return new ArrayList<>(hybridSessions);
    }
    
    /**
     * Restituisce la mappa delle ricette per le sessioni ibride di tipo presenza (solo quelle effettivamente configurate)
     */
    public Map<LocalDate, ObservableList<Ricetta>> getHybridSessionRecipes() {
        Map<LocalDate, ObservableList<Ricetta>> result = new HashMap<>();
        // Prendi solo le date delle sessioni ibride di tipo presenza
        for (Sessione session : hybridSessions) {
            if (session instanceof SessioniInPresenza) {
                LocalDate data = ((SessioniInPresenza) session).getData();
                if (data != null && sessionePresenzaRicette.containsKey(data)) {
                    result.put(data, sessionePresenzaRicette.get(data));
                }
            }
        }
        return result;
    }
    
    /**
     * Verifica se tutte le sessioni ibride sono valide
     */
    public boolean areAllHybridSessionsValid() {
        boolean atLeastOneSession = false;
        StringBuilder errorBuilder = new StringBuilder();
        int sessionIdx = 0;
        for (Map<String, Control> controls : hybridSessionControlsList) {
            ComboBox<String> typeCombo = (ComboBox<String>) controls.get("typeCombo");
            ComboBox<String> dayCombo = (ComboBox<String>) controls.get("dayCombo");
            Spinner<Integer> hourSpinner = (Spinner<Integer>) controls.get("hourSpinner");
            Spinner<Integer> minuteSpinner = (Spinner<Integer>) controls.get("minuteSpinner");
            TextField durationField = (TextField) controls.get("durationField");
            DatePicker datePicker = null; // Not used in current UI, but can be added if needed

            boolean anyFieldFilled = false;
            if ((typeCombo != null && typeCombo.getValue() != null) ||
                (dayCombo != null && dayCombo.getValue() != null) ||
                (hourSpinner != null) ||
                (minuteSpinner != null) ||
                (durationField != null && !durationField.getText().isEmpty())) {
                anyFieldFilled = true;
            }
            if (!anyFieldFilled) {
                sessionIdx++;
                continue;
            }
            atLeastOneSession = true;
            boolean valid = true;
            StringBuilder sessionError = new StringBuilder();
            if (typeCombo == null || typeCombo.getValue() == null) {
                valid = false;
                sessionError.append("- Seleziona il tipo di sessione.\n");
            }
            if (dayCombo == null || dayCombo.getValue() == null) {
                valid = false;
                sessionError.append("- Seleziona il giorno della settimana.\n");
            }
            if (hourSpinner == null || minuteSpinner == null) {
                valid = false;
                sessionError.append("- Inserisci l'orario della sessione.\n");
            }
            if (durationField == null || durationField.getText().isEmpty()) {
                valid = false;
                sessionError.append("- Inserisci la durata della sessione.\n");
            }
            String tipo = (typeCombo != null) ? typeCombo.getValue() : null;
            if ("In presenza".equals(tipo)) {
                TextField cityField = (TextField) controls.get("cityField");
                TextField streetField = (TextField) controls.get("streetField");
                TextField capField = (TextField) controls.get("capField");
                if (cityField == null || cityField.getText().isEmpty()) {
                    valid = false;
                    sessionError.append("- Inserisci la città.\n");
                }
                if (streetField == null || streetField.getText().isEmpty()) {
                    valid = false;
                    sessionError.append("- Inserisci la via.\n");
                }
                if (capField == null || capField.getText().isEmpty() || !capField.getText().matches("\\d{5}")) {
                    valid = false;
                    sessionError.append("- Inserisci un CAP valido (5 cifre).\n");
                }
            } else if ("Telematica".equals(tipo)) {
                ComboBox<?> applicationComboBox = (ComboBox<?>) controls.get("applicationComboBox");
                TextField meetingCodeField = (TextField) controls.get("meetingCodeField");
                if (applicationComboBox == null || applicationComboBox.getValue() == null || applicationComboBox.getValue().toString().isEmpty()) {
                    valid = false;
                    sessionError.append("- Seleziona l'applicazione.\n");
                }
                if (meetingCodeField == null || meetingCodeField.getText().isEmpty()) {
                    valid = false;
                    sessionError.append("- Inserisci il codice riunione.\n");
                }
            }
            if (!valid) {
                errorBuilder.append("Sessione ").append(sessionIdx + 1).append(":\n");
                errorBuilder.append(sessionError);
            }
            sessionIdx++;
        }
        if (!atLeastOneSession) {
            if (hybridErrorLabel != null) hybridErrorLabel.setText("Compila almeno una sessione ibrida.");
            return false;
        }
        if (errorBuilder.length() > 0) {
            if (hybridErrorLabel != null) hybridErrorLabel.setText(errorBuilder.toString());
            return false;
        } else {
            if (hybridErrorLabel != null) hybridErrorLabel.setText("");
        }
        return true;
    }

    /**
     * Ritorna tutti i discendenti (ricorsivo) di un nodo JavaFX
     */
    private java.util.List<Node> getAllDescendants(javafx.scene.Parent parent) {
        java.util.List<Node> descendants = new java.util.ArrayList<>();
        for (Node child : parent.getChildrenUnmodifiable()) {
            descendants.add(child);
            if (child instanceof javafx.scene.Parent) {
                descendants.addAll(getAllDescendants((javafx.scene.Parent) child));
            }
        }
        return descendants;
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
                // Validazione quantità: >0 e compatibile con NUMERIC(10,2)
                if (ing.getQuantita() <= 0 || !isValidNumericField(Double.toString(ing.getQuantita()))) {
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

    /**
     * Valida che un valore numerico (es. prezzo, quantità) sia compatibile con NUMERIC(10,2) PostgreSQL
     * (max 99999999.99, max 2 decimali)
     */
    private boolean isValidNumericField(String value) {
        if (value == null || value.isEmpty()) return false;
        try {
            double d = Double.parseDouble(value.replace(",", "."));
            if (Math.abs(d) >= 1e8) return false;
            String[] parts = value.replace(",", ".").split("\\.");
            if (parts.length == 2 && parts[1].length() > 2) return false;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void createCourse(ActionEvent event) {
        // Validazione campo prezzo
        if (!isValidNumericField(priceField.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Il prezzo deve essere un numero valido (max 99999999.99, max 2 decimali)", ButtonType.OK);
            alert.showAndWait();
            return;
        }
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
        return UnifiedRecipeIngredientUI.createUnifiedRecipeBox(ricetta, recipesList, container, false, this::notifyControllerOfChange, unitaDiMisuraEnum);
    }
    
    /**
     * Crea il box per un singolo ingrediente
     */
    private HBox createIngredientBox(Ingredienti ingrediente, Ricetta ricetta, VBox container) {
        return UnifiedRecipeIngredientUI.createUnifiedIngredientBox(ingrediente, ricetta, container, false, this::notifyControllerOfChange, unitaDiMisuraEnum);
    }
}