package com.progetto.controller.chef;

import java.time.LocalTime;
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
import javafx.fxml.FXML;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.List;

import com.progetto.utils.SuccessDialogUtils;
import com.progetto.utils.SceneSwitcher;

import com.progetto.Entity.entityDao.CorsoDao;
import com.progetto.Entity.entityDao.SessioneInPresenzaDao;
import com.progetto.Entity.entityDao.BarraDiRicercaDao;
import com.progetto.Entity.entityDao.IngredientiDao;
import com.progetto.Entity.entityDao.SessioneOnlineDao;
import com.progetto.Entity.entityDao.ricettaDao;
import com.progetto.boundary.chef.EditCourseBoundary;
import com.progetto.Entity.EntityDto.Corso;
import com.progetto.Entity.EntityDto.SessioniInPresenza;
import com.progetto.Entity.EntityDto.SessioneOnline;
import com.progetto.Entity.EntityDto.Ricetta;
import com.progetto.Entity.EntityDto.Ingredienti;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.stream.Collectors;

public class EditCourseController {
    public void setHybridPresenzaSpinners(Spinner<Integer> hour, Spinner<Integer> min, Spinner<Double> durata) {
        this.startHourSpinnerPresenza = hour;
        this.startMinuteSpinnerPresenza = min;
        this.durationSpinnerPresenza = durata;
    }
    private final EditCourseBoundary boundary;
    private boolean hasPresenzaSessions = false;
    private boolean hasTelematicheSessions = false;
    private static final Pattern CAP_PATTERN = Pattern.compile("\\d{5}");
    private final TextField courseNameField;
    private final TextArea descriptionArea;
    private final DatePicker startDatePicker, endDatePicker;
    private final ComboBox<String> courseTypeCombo;
    private final ComboBox<String> frequencyCombo;
    private final Spinner<Integer> maxPersonsSpinner;
    private final Button saveButton;
    private final VBox locationSection, recipesSection, recipesContainer, onlineSection;
    private final Spinner<Integer> startHourSpinner, startMinuteSpinner;
    private final Spinner<Double> durationSpinner;
    private final TextField streetField, capField;
    private final Label descriptionErrorLabel, maxPersonsErrorLabel;
    private final Label startDateErrorLabel, endDateErrorLabel, startTimeErrorLabel;
    private final Label durationErrorLabel, daysErrorLabel, streetErrorLabel, capErrorLabel;
    private final Label frequencyErrorLabel;
    @FXML private HBox singleTimeSection;
    @FXML private VBox doubleTimeSection;
    private Spinner<Integer> startHourSpinnerPresenza;
    private Spinner<Integer> startMinuteSpinnerPresenza;
    private Spinner<Double> durationSpinnerPresenza;
    @FXML private Spinner<Integer> startHourSpinnerTelematica;
    @FXML private Spinner<Integer> startMinuteSpinnerTelematica;
    @FXML private Spinner<Double> durationSpinnerTelematica;
    private void setupHybridSpinners() {
        if (startHourSpinnerPresenza != null) {
            startHourSpinnerPresenza.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 23, 18));
            startHourSpinnerPresenza.setPrefWidth(100);
        }
        if (startMinuteSpinnerPresenza != null) {
            startMinuteSpinnerPresenza.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 15));
            startMinuteSpinnerPresenza.setPrefWidth(100);
        }
        if (durationSpinnerPresenza != null) {
            durationSpinnerPresenza.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 8.0, 2.0, 0.5));
            durationSpinnerPresenza.getValueFactory().setConverter(new StringConverter<Double>() {
                @Override public String toString(Double object) { return object == null ? "" : String.format("%.1f", object); }
                @Override public Double fromString(String string) { try { return Double.parseDouble(string.replace(",", ".")); } catch (NumberFormatException e) { return 2.0; } }
            });
        }
        if (startHourSpinnerTelematica != null) {
            startHourSpinnerTelematica.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 23, 18));
            startHourSpinnerTelematica.setPrefWidth(100);
        }
        if (startMinuteSpinnerTelematica != null) {
            startMinuteSpinnerTelematica.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 15));
            startMinuteSpinnerTelematica.setPrefWidth(100);
        }
        if (durationSpinnerTelematica != null) {
            durationSpinnerTelematica.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 8.0, 2.0, 0.5));
            durationSpinnerTelematica.getValueFactory().setConverter(new StringConverter<Double>() {
                @Override public String toString(Double object) { return object == null ? "" : String.format("%.1f", object); }
                @Override public Double fromString(String string) { try { return Double.parseDouble(string.replace(",", ".")); } catch (NumberFormatException e) { return 2.0; } }
            });
        }
    }
    private com.progetto.Entity.EntityDto.Corso currentCourse;
    
    // === CONSTRUCTOR ===
    public EditCourseController(EditCourseBoundary boundary,
                               TextField courseNameField, TextArea descriptionArea,
                               DatePicker startDatePicker, DatePicker endDatePicker,
                               ComboBox<String> courseTypeCombo, ComboBox<String> frequencyCombo,
                               Spinner<Integer> maxPersonsSpinner,
                               VBox locationSection, VBox recipesSection, VBox recipesContainer, VBox onlineSection,
                               Spinner<Integer> startHourSpinner, Spinner<Integer> startMinuteSpinner,
                               Spinner<Double> durationSpinner, TextField streetField, TextField capField, 
                               Label descriptionErrorLabel, Label maxPersonsErrorLabel,
                               Label startDateErrorLabel, Label endDateErrorLabel, Label startTimeErrorLabel,
                               Label durationErrorLabel, Label daysErrorLabel, Label streetErrorLabel,
                               Label capErrorLabel, Label frequencyErrorLabel, Button saveButton) {
        this.boundary = boundary;
        this.courseNameField = courseNameField;
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
        this.onlineSection = onlineSection;
        this.startHourSpinnerPresenza = boundary.getStartHourSpinnerPresenza();
        this.startMinuteSpinnerPresenza = boundary.getStartMinuteSpinnerPresenza();
        this.durationSpinnerPresenza = boundary.getDurationSpinnerPresenza();
    }
    
    public void initialize() {
        setupUI();
        loadCourseData();
        setupModeUI();
        setupChangeListenersForSave();
        startDatePicker.setDisable(true);
        endDatePicker.setDisable(true);
        if (saveButton != null) {
            saveButton.disableProperty().bind(Bindings.createBooleanBinding(() -> !hasAnyFieldChanged(),
                courseNameField.textProperty(),
                descriptionArea.textProperty(),
                courseTypeCombo.valueProperty(),
                frequencyCombo.valueProperty(),
                maxPersonsSpinner.valueProperty(),
                streetField.textProperty(),
                capField.textProperty()
            ));
        }
    }

    private void setupModeUI() {
        courseTypeCombo.setDisable(true);
        frequencyCombo.setDisable(true);

        String selectedType = courseTypeCombo.getValue();
        boolean isPresenza = "In presenza".equals(selectedType);
        boolean isEntrambi = "Entrambi".equals(selectedType);
        locationSection.setVisible(isPresenza || isEntrambi);
        locationSection.setManaged(isPresenza || isEntrambi);
        recipesSection.setVisible(isPresenza || isEntrambi);
        recipesSection.setManaged(isPresenza || isEntrambi);

        if (!(isPresenza || isEntrambi)) {
            streetField.clear();
            capField.clear();
            recipesContainer.getChildren().clear();
            streetField.setDisable(true);
            capField.setDisable(true);
        } else {
            if (recipesContainer.getChildren().isEmpty()) {
                loadAllSessionsAndRecipes();
            }
            LocalDate today = LocalDate.now();
            if (currentCourse != null && currentCourse.getDataInizio() != null && currentCourse.getDataInizio().isAfter(today)) {
                streetField.setDisable(false);
                capField.setDisable(false);
            } else {
                streetField.setDisable(true);
                capField.setDisable(true);
            }
        }

        startDatePicker.setDisable(true);
        endDatePicker.setDisable(true);
    }

    private void setupChangeListenersForSave() {
        List<Observable> observables = new ArrayList<>(Arrays.asList(
            courseNameField.textProperty(),
            descriptionArea.textProperty(),
            frequencyCombo.valueProperty(),
            maxPersonsSpinner.valueProperty(),
            startHourSpinner.valueProperty(),
            startMinuteSpinner.valueProperty(),
            durationSpinner.valueProperty(),
            streetField.textProperty(),
            capField.textProperty()
        ));
        addRecipeIngredientListeners(observables);

        BooleanBinding changed = Bindings.createBooleanBinding(() -> hasAnyFieldChanged(),
            observables.toArray(new Observable[0])
        );
        saveButton.disableProperty().unbind();
        saveButton.disableProperty().bind(changed.not());
    }

    private void addRecipeIngredientListeners(List<Observable> observables) {
        for (Node node : recipesContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox recipeBox = (VBox) node;
                if (recipeBox.getChildren().isEmpty()) continue;
                HBox header = (HBox) recipeBox.getChildren().get(0);
                for (Node hNode : header.getChildren()) {
                    if (hNode instanceof TextField) {
                        observables.add(((TextField) hNode).textProperty());
                    }
                }
                if (recipeBox.getChildren().size() > 1) {
                    VBox ingredientsBox = (VBox) recipeBox.getChildren().get(1);
                    if (ingredientsBox.getChildren().size() > 1) {
                        VBox ingredientsList = (VBox) ingredientsBox.getChildren().get(1);
                        for (Node ingrNode : ingredientsList.getChildren()) {
                            if (ingrNode instanceof HBox) {
                                HBox ingrRow = (HBox) ingrNode;
                                if (ingrRow.getChildren().size() >= 3) {
                                    TextField nameField = (TextField) ingrRow.getChildren().get(0);
                                    TextField quantityField = (TextField) ingrRow.getChildren().get(1);
                                    ComboBox<?> unitCombo = (ComboBox<?>) ingrRow.getChildren().get(2);
                                    observables.add(nameField.textProperty());
                                    observables.add(quantityField.textProperty());
                                    observables.add(unitCombo.valueProperty());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean hasAnyFieldChanged() {
        if (currentCourse == null) return false;
        if (!Objects.equals(courseNameField.getText().trim(), currentCourse.getNome())) return true;
        if (!Objects.equals(descriptionArea.getText().trim(), currentCourse.getDescrizione())) return true;
        if (!Objects.equals(frequencyCombo.getValue(), currentCourse.getFrequenzaDelleSessioni())) return true;
        if (!Objects.equals(maxPersonsSpinner.getValue(), currentCourse.getMaxPersone())) return true;

        String type = courseTypeCombo.getValue();
        if ("In presenza".equals(type) || "Telematica".equals(type)) {
            LocalTime globalOrario = LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue());
            double durataVal = durationSpinner.getValue();
            LocalTime globalDurata = LocalTime.of((int) durataVal, (int) ((durataVal - (int) durataVal) * 60));
            if ("In presenza".equals(type)) {
            SessioneInPresenzaDao presenzaDao = new SessioneInPresenzaDao();
            ArrayList<SessioniInPresenza> presenze = presenzaDao.getSessioniByCorso(courseId);
            for (SessioniInPresenza sessione : presenze) {
                    if (sessione.getData().isAfter(java.time.LocalDate.now())) {
                        if (!Objects.equals(sessione.getOrario(), globalOrario)) return true;
                        if (!Objects.equals(sessione.getDurata(), globalDurata)) return true;
                    }
                }
            } else if ("Telematica".equals(type)) {
                SessioneOnlineDao onlineDao = new SessioneOnlineDao();
                List<SessioneOnline> telematiche = SessioneOnlineDao.getSessioniByCorso(courseId);
                for (SessioneOnline sessione : telematiche) {
                    if (sessione.getData().isAfter(java.time.LocalDate.now())) {
                        if (!Objects.equals(sessione.getOrario(), globalOrario)) return true;
                        if (!Objects.equals(sessione.getDurata(), globalDurata)) return true;
                        // App/codice
                        for (Node node : onlineSection.getChildren()) {
                            if (node instanceof VBox) {
                                VBox sessionBox = (VBox) node;
                                HBox header = (HBox) sessionBox.getChildren().get(0);
                                ComboBox<?> appCombo = (ComboBox<?>) header.getChildren().get(1);
                                TextField codeField = (TextField) header.getChildren().get(3);
                                if (!Objects.equals(sessione.getApplicazione(), appCombo.getValue())) return true;
                                if (!Objects.equals(sessione.getCodicechiamata(), codeField.getText().trim())) return true;
                            }
                        }
                    }
                }
            }
        } else if ("Entrambi".equals(type)) {
            if (startHourSpinnerPresenza != null && startMinuteSpinnerPresenza != null && durationSpinnerPresenza != null) {
            LocalTime orarioPresenza = LocalTime.of(startHourSpinnerPresenza.getValue(), startMinuteSpinnerPresenza.getValue());
            double durataValPres = durationSpinnerPresenza.getValue();
            LocalTime durataPres = LocalTime.of((int) durataValPres, (int) ((durataValPres - (int) durataValPres) * 60));
                SessioneInPresenzaDao presenzaDao = new SessioneInPresenzaDao();
                ArrayList<SessioniInPresenza> presenze = presenzaDao.getSessioniByCorso(courseId);
                for (SessioniInPresenza sessione : presenze) {
                    if (sessione.getData().isAfter(java.time.LocalDate.now())) {
                        if (!Objects.equals(sessione.getOrario(), orarioPresenza)) return true;
                        if (!Objects.equals(sessione.getDurata(), durataPres)) return true;
                    }
                }
            }
            if (startHourSpinnerTelematica != null && startMinuteSpinnerTelematica != null && durationSpinnerTelematica != null) {
            LocalTime orarioTele = LocalTime.of(startHourSpinnerTelematica.getValue(), startMinuteSpinnerTelematica.getValue());
            double durataValTele = durationSpinnerTelematica.getValue();
            LocalTime durataTele = LocalTime.of((int) durataValTele, (int) ((durataValTele - (int) durataValTele) * 60));
            List<SessioneOnline> telematiche = SessioneOnlineDao.getSessioniByCorso(courseId);
            for (SessioneOnline sessione : telematiche) {
                if (sessione.getData().isAfter(LocalDate.now())) {
                    if (!Objects.equals(sessione.getOrario(), orarioTele)) return true;
                    if (!Objects.equals(sessione.getDurata(), durataTele)) return true;
                    for (Node node : onlineSection.getChildren()) {
                        if (node instanceof VBox) {
                            VBox sessionBox = (VBox) node;
                            HBox header = (HBox) sessionBox.getChildren().get(0);
                            ComboBox<?> appCombo = (ComboBox<?>) header.getChildren().get(1);
                            TextField codeField = (TextField) header.getChildren().get(3);
                            if (!Objects.equals(sessione.getApplicazione(), appCombo.getValue())) return true;
                            if (!Objects.equals(sessione.getCodicechiamata(), codeField.getText().trim())) return true;
                        }
                    }
                }
            }
            }
        }
        try {
            SessioneInPresenzaDao presenzaDao = new SessioneInPresenzaDao();
            ArrayList<SessioniInPresenza> presenze = presenzaDao.getSessioniByCorso(courseId);
            List<String> ricetteDB = new ArrayList<>();
            for (SessioniInPresenza sessione : presenze) {
                for (Ricetta r : sessione.getRicette()) {
                    ricetteDB.add(r.getNome());
                }
            }
            List<String> ricetteUI = new ArrayList<>();
            for (Node node : recipesContainer.getChildren()) {
                if (node instanceof VBox) {
                    VBox recipeBox = (VBox) node;
                    HBox header = (HBox) recipeBox.getChildren().get(0);
                    TextField recipeNameField = (TextField) header.getChildren().get(1);
                    ricetteUI.add(recipeNameField.getText().trim());
                }
            }
            if (!ricetteDB.equals(ricetteUI)) return true;
            for (Node node : recipesContainer.getChildren()) {
                if (node instanceof VBox) {
                    VBox recipeBox = (VBox) node;
                    VBox ingredientsBox = (VBox) recipeBox.getChildren().get(1);
                    VBox ingredientsList = (VBox) ingredientsBox.getChildren().get(1);
                    List<String> ingrUI = new ArrayList<>();
                    for (Node ingrNode : ingredientsList.getChildren()) {
                        if (ingrNode instanceof HBox) {
                            HBox ingrRow = (HBox) ingrNode;
                            TextField nameField = (TextField) ingrRow.getChildren().get(0);
                            ingrUI.add(nameField.getText().trim());
                        }
                    }
                    String nomeRicetta = ((TextField)((HBox)recipeBox.getChildren().get(0)).getChildren().get(1)).getText().trim();
                    boolean found = false;
                    for (SessioniInPresenza sessione : presenze) {
                        for (Ricetta r : sessione.getRicette()) {
                            if (r.getNome().equalsIgnoreCase(nomeRicetta)) {
                                List<String> ingrDB = new ArrayList<>();
                                for (Ingredienti ingr : r.getIngredientiRicetta()) {
                                    ingrDB.add(ingr.getNome());
                                }
                                if (!ingrDB.equals(ingrUI)) return true;
                                found = true;
                                break;
                            }
                        }
                        if (found) break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateTimeAndDurationVisibility() {
        String type = courseTypeCombo.getValue();
        boolean isPresenza = "In presenza".equals(type);
        boolean isTelematica = "Telematica".equals(type);
        boolean isEntrambi = "Entrambi".equals(type);
        if (singleTimeSection != null && doubleTimeSection != null) {
            if (isEntrambi) {
                singleTimeSection.setVisible(false);
                singleTimeSection.setManaged(false);
                doubleTimeSection.setVisible(true);
                doubleTimeSection.setManaged(true);
            } else {
                singleTimeSection.setVisible(true);
                singleTimeSection.setManaged(true);
                doubleTimeSection.setVisible(false);
                doubleTimeSection.setManaged(false);
            }
        }
        startHourSpinner.setDisable(isEntrambi);
        startMinuteSpinner.setDisable(isEntrambi);
        durationSpinner.setDisable(isEntrambi);
        if (startHourSpinnerPresenza != null) startHourSpinnerPresenza.setDisable(!isEntrambi);
        if (startMinuteSpinnerPresenza != null) startMinuteSpinnerPresenza.setDisable(!isEntrambi);
        if (durationSpinnerPresenza != null) durationSpinnerPresenza.setDisable(!isEntrambi);
        if (startHourSpinnerTelematica != null) startHourSpinnerTelematica.setDisable(!isEntrambi);
        if (startMinuteSpinnerTelematica != null) startMinuteSpinnerTelematica.setDisable(!isEntrambi);
        if (durationSpinnerTelematica != null) durationSpinnerTelematica.setDisable(!isEntrambi);
    }
    
    private void setupUI() {
        maxPersonsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));
        startHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 23, 18));
        startHourSpinner.setPrefWidth(100);
        startMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 15));
        startMinuteSpinner.setPrefWidth(100);
        durationSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 8.0, 2.0, 0.5));
        durationSpinner.getValueFactory().setConverter(new StringConverter<Double>() {
            @Override public String toString(Double object) { return object == null ? "" : String.format("%.1f", object); }
            @Override public Double fromString(String string) { try { return Double.parseDouble(string); } catch (NumberFormatException e) { return 0.0; } }
        });
        ObservableList<String> courseTypes = FXCollections.observableArrayList("In presenza", "Telematica");
        courseTypeCombo.setItems(courseTypes);
        courseTypeCombo.setValue("In presenza");
        ObservableList<String> frequencies = FXCollections.observableArrayList(
            "1 volta a settimana", "2 volte a settimana", "3 volte a settimana", "Tutti i giorni (Lun-Ven)", "Tutti i giorni (Lun-Dom)");
        frequencyCombo.setItems(frequencies);
        frequencyCombo.setValue("2 volte a settimana");
        // Date
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate startDate = startDatePicker.getValue();
                setDisable(empty || (startDate != null && date.isBefore(startDate.plusDays(1))));
            }
        });
        capField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            String filtered = newVal.replaceAll("[^\\d]", "");
            if (filtered.length() > 5) filtered = filtered.substring(0, 5);
            capField.setText(filtered);
        });
        updateTimeAndDurationVisibility();
    }
    
    private int courseId = -1;

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

private void loadCourseData() {
    if (courseId <= 0) {
        boundary.showAlert("Errore", "ID corso non valido. Impossibile caricare i dati.");
        return;
    }
    try {
        CorsoDao corsoDao = new CorsoDao();
        Corso corsoDto = corsoDao.getCorsoById(courseId);
        if (corsoDto == null) {
            boundary.showAlert("Errore", "Corso non trovato nel database.");
            return;
        }
        currentCourse = corsoDto;
        SessioneInPresenzaDao presenzaDao = new SessioneInPresenzaDao();
        SessioneOnlineDao onlineDao = new SessioneOnlineDao();
        List<SessioniInPresenza> presenze = presenzaDao.getSessioniByCorso(courseId);
        List<SessioneOnline> telematiche = onlineDao.getSessioniByCorso(courseId);

        boolean hasPresenza = presenze != null && !presenze.isEmpty();
        boolean hasTelematica = telematiche != null && !telematiche.isEmpty();

        String tipoCorso;
        if (hasPresenza && hasTelematica) {
            tipoCorso = "Entrambi";
        } else if (hasPresenza) {
            tipoCorso = "In presenza";
        } else if (hasTelematica) {
            tipoCorso = "Telematica";
        } else {
            tipoCorso = "In presenza";
        }
        populateUIFromCourseData(tipoCorso, presenze);
        updateDynamicSections(hasPresenza, hasTelematica);
    } catch (Exception e) {
        boundary.showAlert("Errore", "Errore durante il caricamento dei dati del corso: " + e.getMessage());
    }
}

    private void updateDynamicSections(boolean hasPresenza, boolean hasTelematica) {
        String tipoCorso = courseTypeCombo.getValue();
        boolean isEntrambi = "Entrambi".equals(tipoCorso);
        boolean showLocation = hasPresenza || isEntrambi;
        boolean showRecipes = hasPresenza || isEntrambi;
        boolean showOnline = hasTelematica || isEntrambi;

        locationSection.setVisible(showLocation);
        locationSection.setManaged(showLocation);
        recipesSection.setVisible(showRecipes);
        recipesSection.setManaged(showRecipes);
        onlineSection.setVisible(showOnline);
        onlineSection.setManaged(showOnline);

        streetField.setDisable(!showLocation);
        capField.setDisable(!showLocation);

        if (!showLocation) {
            streetField.clear();
            capField.clear();
        }
        if (!showOnline) {
            onlineSection.getChildren().clear();
        }
    }
    
    private void populateUIFromCourseData(String tipoCorso, List<SessioniInPresenza> presenze) {
        courseNameField.setText(currentCourse.getNome());
        descriptionArea.setText(currentCourse.getDescrizione());
        courseTypeCombo.setValue(tipoCorso);
        frequencyCombo.setValue(currentCourse.getFrequenzaDelleSessioni());
        maxPersonsSpinner.getValueFactory().setValue(currentCourse.getMaxPersone());
        startDatePicker.setValue(currentCourse.getDataInizio());
        endDatePicker.setValue(currentCourse.getDataFine());
        List<String> giorniCorso = new ArrayList<>();
        try {
            java.lang.reflect.Method giorniMethod = currentCourse.getClass().getMethod("getGiorniDellaSettimana");
            Object result = giorniMethod.invoke(currentCourse);
            if (result instanceof List) {
                giorniCorso = (List<String>) result;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        if ("In presenza".equals(tipoCorso) || "Entrambi".equals(tipoCorso)) {
            List<SessioniInPresenza> presenzeDB = new ArrayList<>();
            try {
                SessioneInPresenzaDao presenzaDao = new SessioneInPresenzaDao();
                presenzeDB = presenzaDao.getSessioniByCorso(courseId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            locationSection.setVisible(true);
            locationSection.setManaged(true);
            recipesSection.setVisible(true);
            recipesSection.setManaged(true);
            streetField.setVisible(true);
            capField.setVisible(true);
            if (!presenzeDB.isEmpty()) {
                SessioniInPresenza firstPresenza = null;
                LocalDate today = LocalDate.now();
                for (SessioniInPresenza s : presenzeDB) {
                    if (s.getData() != null && s.getData().isAfter(today)) { firstPresenza = s; break; }
                }
                if (firstPresenza == null) firstPresenza = presenzeDB.get(0);
                streetField.setText(firstPresenza.getVia() != null ? firstPresenza.getVia() : "");
                capField.setText(firstPresenza.getCap() != null ? firstPresenza.getCap() : "");
                boolean editable = currentCourse != null && currentCourse.getDataInizio() != null && currentCourse.getDataInizio().isAfter(today);
                streetField.setDisable(!editable);
                capField.setDisable(!editable);
                if ("Entrambi".equals(tipoCorso)) {
                    if (startHourSpinnerPresenza != null && firstPresenza.getOrario() != null) {
                        startHourSpinnerPresenza.getValueFactory().setValue(firstPresenza.getOrario().getHour());
                        startHourSpinnerPresenza.setDisable(false);
                    }
                    if (startMinuteSpinnerPresenza != null && firstPresenza.getOrario() != null) {
                        startMinuteSpinnerPresenza.getValueFactory().setValue(firstPresenza.getOrario().getMinute());
                        startMinuteSpinnerPresenza.setDisable(false);
                    }
                    if (durationSpinnerPresenza != null && firstPresenza.getDurata() != null) {
                        double durataPres = firstPresenza.getDurata().getHour() + firstPresenza.getDurata().getMinute() / 60.0;
                        durationSpinnerPresenza.getValueFactory().setValue(durataPres);
                        durationSpinnerPresenza.setDisable(false);
                    }
                    if (startHourSpinner != null) startHourSpinner.setDisable(true);
                    if (startMinuteSpinner != null) startMinuteSpinner.setDisable(true);
                    if (durationSpinner != null) durationSpinner.setDisable(true);
                } else if ("In presenza".equals(tipoCorso)) {
                    if (startHourSpinner != null && firstPresenza.getOrario() != null) {
                        startHourSpinner.getValueFactory().setValue(firstPresenza.getOrario().getHour());
                        startHourSpinner.setDisable(false);
                    }
                    if (startMinuteSpinner != null && firstPresenza.getOrario() != null) {
                        startMinuteSpinner.getValueFactory().setValue(firstPresenza.getOrario().getMinute());
                        startMinuteSpinner.setDisable(false);
                    }
                    if (durationSpinner != null && firstPresenza.getDurata() != null) {
                        double durataPres = firstPresenza.getDurata().getHour() + firstPresenza.getDurata().getMinute() / 60.0;
                        durationSpinner.getValueFactory().setValue(durataPres);
                        durationSpinner.setDisable(false);
                    }
                }
            } else {
                streetField.setText("");
                capField.setText("");
                streetField.setDisable(true);
                capField.setDisable(true);
            }
            recipesContainer.getChildren().clear();
            Label presenzaTitle = new Label("Sessioni in presenza");
            presenzaTitle.getStyleClass().add("section-title");
            recipesContainer.getChildren().add(presenzaTitle);
            ricettaDao ricettaDao = new ricettaDao();
            boolean almenoUnaRicetta = false;
            SessioneInPresenzaDao presenzaDao = new SessioneInPresenzaDao();
            for (SessioniInPresenza sessione : presenzeDB) {
                ArrayList<Ricetta> ricette = presenzaDao.recuperaRicetteSessione(sessione);
                for (Ricetta ricetta : ricette) {
                    almenoUnaRicetta = true;
                    ArrayList<Ingredienti> ingredienti = ricettaDao.getIngredientiRicetta(ricetta);
                    String[] ingredientNames = ingredienti.stream().map(Ingredienti::getNome).toArray(String[]::new);
                    String[] quantities = ingredienti.stream().map(i -> String.valueOf(i.getQuantita())).toArray(String[]::new);
                    String[] units = ingredienti.stream().map(Ingredienti::getUnitaMisura).toArray(String[]::new);
                    boundary.addRecipeToContainer(ricetta.getNome(), ingredientNames, quantities, units, sessione.getData());
                }
            }
            if (!almenoUnaRicetta) {
                Label noRicette = new Label("(Nessuna ricetta associata alle sessioni in presenza)");
                noRicette.setStyle("-fx-font-style: italic; -fx-text-fill: #888;");
                recipesContainer.getChildren().add(noRicette);
            }
        }
        if ("Telematica".equals(tipoCorso) || "Entrambi".equals(tipoCorso)) {
            SessioneOnlineDao onlineDao = new SessioneOnlineDao();
            List<SessioneOnline> telematiche = onlineDao.getSessioniByCorso(courseId);
            if (telematiche != null && !telematiche.isEmpty()) {
                Label telematicaTitle = new Label("Sessioni telematiche");
                telematicaTitle.getStyleClass().add("section-title");
                onlineSection.getChildren().clear();
                onlineSection.getChildren().add(telematicaTitle);
                SessioneOnline ref = null;
                LocalDate today = LocalDate.now();
                for (SessioneOnline s : telematiche) {
                    if (s.getData() != null && s.getData().isAfter(today)) { ref = s; break; }
                }
                if (ref == null) ref = telematiche.get(0);
                String orario = ref.getOrario() != null ? ref.getOrario().toString() : "";
                double durata = 0.0;
                if (ref.getDurata() != null) {
                    durata = ref.getDurata().getHour() + ref.getDurata().getMinute() / 60.0;
                }
                if ("Entrambi".equals(tipoCorso)) {
                    if (startHourSpinnerTelematica != null && ref.getOrario() != null) {
                        startHourSpinnerTelematica.getValueFactory().setValue(ref.getOrario().getHour());
                        startHourSpinnerTelematica.setDisable(false);
                    }
                    if (startMinuteSpinnerTelematica != null && ref.getOrario() != null) {
                        startMinuteSpinnerTelematica.getValueFactory().setValue(ref.getOrario().getMinute());
                        startMinuteSpinnerTelematica.setDisable(false);
                    }
                    if (durationSpinnerTelematica != null && ref.getDurata() != null) {
                        double durataTele = ref.getDurata().getHour() + ref.getDurata().getMinute() / 60.0;
                        durationSpinnerTelematica.getValueFactory().setValue(durataTele);
                        durationSpinnerTelematica.setDisable(false);
                    }
                } else if ("Telematica".equals(tipoCorso)) {
                    if (startHourSpinner != null && ref.getOrario() != null) {
                        startHourSpinner.getValueFactory().setValue(ref.getOrario().getHour());
                        startHourSpinner.setDisable(false);
                    }
                    if (startMinuteSpinner != null && ref.getOrario() != null) {
                        startMinuteSpinner.getValueFactory().setValue(ref.getOrario().getMinute());
                        startMinuteSpinner.setDisable(false);
                    }
                    if (durationSpinner != null && ref.getDurata() != null) {
                        double durataTele = ref.getDurata().getHour() + ref.getDurata().getMinute() / 60.0;
                        durationSpinner.getValueFactory().setValue(durataTele);
                        durationSpinner.setDisable(false);
                    }
                }
                addOnlineSessionToOnlineSection(ref.getApplicazione(), ref.getCodicechiamata(), orario, durata, ref.getData());
            }
        }
        if ("Telematica".equals(tipoCorso)) {
            locationSection.setVisible(false);
            locationSection.setManaged(false);
            recipesSection.setVisible(false);
            recipesSection.setManaged(false);
            streetField.setText("");
            capField.setText("");
        }
        setupChangeListenersForSave();
    }
    private void loadAllSessionsAndRecipes() {
        recipesContainer.getChildren().clear();
        onlineSection.getChildren().clear();
        hasPresenzaSessions = false;
        hasTelematicheSessions = false;
        try {
            SessioneInPresenzaDao presenzaDao = new SessioneInPresenzaDao();
            List<SessioniInPresenza> presenze = presenzaDao.getSessioniByCorso(courseId);
            if (presenze != null && !presenze.isEmpty()) {
                hasPresenzaSessions = true;
                Label presenzaTitle = new Label("Sessioni in presenza");
                presenzaTitle.getStyleClass().add("section-title");
                recipesContainer.getChildren().add(presenzaTitle);
                ricettaDao ricettaDao = new ricettaDao();
                for (SessioniInPresenza sessione : presenze) {
                    ArrayList<Ricetta> ricette = presenzaDao.recuperaRicetteSessione(sessione);
                    if (ricette == null || ricette.isEmpty()) {
                        Label noRicette = new Label("(Nessuna ricetta associata a questa sessione)");
                        noRicette.getStyleClass().add("info-label");
                        recipesContainer.getChildren().add(noRicette);
                        continue;
                    }
                    for (Ricetta ricetta : ricette) {
                        ArrayList<Ingredienti> ingredienti = ricettaDao.getIngredientiRicetta(ricetta);
                        String[] ingredientNames = ingredienti.stream().map(Ingredienti::getNome).toArray(String[]::new);
                        String[] quantities = ingredienti.stream().map(i -> String.valueOf(i.getQuantita())).toArray(String[]::new);
                        String[] units = ingredienti.stream().map(Ingredienti::getUnitaMisura).toArray(String[]::new);
                        boundary.addRecipeToContainer(ricetta.getNome(), ingredientNames, quantities, units, sessione.getData());
                    }
                }
            }
            SessioneOnlineDao onlineDao = new SessioneOnlineDao();
            List<SessioneOnline> telematiche = onlineDao.getSessioniByCorso(courseId);
            if (telematiche != null && !telematiche.isEmpty()) {
                hasTelematicheSessions = true;
                Label telematicaTitle = new Label("Sessioni telematiche");
                telematicaTitle.getStyleClass().add("section-title");
                onlineSection.getChildren().add(telematicaTitle);
                for (SessioneOnline sessione : telematiche) {
                    String orario = sessione.getOrario() != null ? sessione.getOrario().toString() : "";
                    double durata = 0.0;
                    if (sessione.getDurata() != null) {
                        durata = sessione.getDurata().getHour() + sessione.getDurata().getMinute() / 60.0;
                    }
                    addOnlineSessionToOnlineSection(sessione.getApplicazione(), sessione.getCodicechiamata(), orario, durata, sessione.getData());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            boundary.showAlert("Errore", "Errore durante il caricamento delle sessioni/ricette: " + e.getMessage());
        }
        updateCourseTypeUIFromSessions();
    }

    private void addOnlineSessionToOnlineSection(String app, String meetingCode, String orario, double durata, LocalDate sessionDate) {
        VBox sessionBox = new VBox(10);
        sessionBox.getStyleClass().add("online-session-container");

        boolean editable = sessionDate.isAfter(LocalDate.now());
        if (!editable) {
            sessionBox.getStyleClass().add("session-past");
        }

        HBox header = new HBox(15);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label appLabel = new Label("App:");
        ComboBox<String> appCombo = new ComboBox<>();
        appCombo.getItems().addAll("Zoom", "Microsoft Teams", "Google Meet");
        appCombo.setValue(app != null && !app.isEmpty() ? app : "Zoom");
        appCombo.setPrefWidth(140);
        appCombo.setDisable(!editable);

        Label codeLabel = new Label("Codice Meeting:");
        TextField codeField = new TextField(meetingCode);
        codeField.setPromptText("Codice riunione...");
        codeField.setPrefWidth(120);
        codeField.setDisable(!editable);

        String tipoCorso = courseTypeCombo.getValue();
        if ("Entrambi".equals(tipoCorso)) {
            Label orarioLabel = new Label("Orario:");
            Spinner<Integer> hourSpinner = new Spinner<>(6, 23, 18, 1);
            hourSpinner.setPrefWidth(90);
            hourSpinner.setDisable(!editable);
            Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0, 15);
            minuteSpinner.setPrefWidth(60);
            minuteSpinner.setDisable(!editable);
            if (orario != null && !orario.isEmpty() && orario.contains(":")) {
                try {
                    String[] parts = orario.split(":");
                    int h = Integer.parseInt(parts[0]);
                    int m = Integer.parseInt(parts[1]);
                    hourSpinner.getValueFactory().setValue(h);
                    minuteSpinner.getValueFactory().setValue(m);
                } catch (Exception ignored) {}
            }
            Label durataLabel = new Label("Durata (h):");
            Spinner<Double> durataSpinner = new Spinner<>(1.0, 8.0, Math.max(1.0, Math.round(durata)), 1.0);
            durataSpinner.setPrefWidth(80);
            durataSpinner.setDisable(!editable);
            header.getChildren().addAll(appLabel, appCombo, codeLabel, codeField, orarioLabel, hourSpinner, minuteSpinner, durataLabel, durataSpinner);
            if (editable) {
                hourSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTelematicaSessionsFromHybridUI(hourSpinner, minuteSpinner, durataSpinner, appCombo, codeField));
                minuteSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTelematicaSessionsFromHybridUI(hourSpinner, minuteSpinner, durataSpinner, appCombo, codeField));
                durataSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTelematicaSessionsFromHybridUI(hourSpinner, minuteSpinner, durataSpinner, appCombo, codeField));
                appCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateTelematicaSessionsFromHybridUI(hourSpinner, minuteSpinner, durataSpinner, appCombo, codeField));
                codeField.textProperty().addListener((obs, oldVal, newVal) -> updateTelematicaSessionsFromHybridUI(hourSpinner, minuteSpinner, durataSpinner, appCombo, codeField));
            }
        } else {
            header.getChildren().addAll(appLabel, appCombo, codeLabel, codeField);
        }
        sessionBox.getChildren().add(header);
        onlineSection.getChildren().add(sessionBox);
    }

    private void updateTelematicaSessionsFromHybridUI(Spinner<Integer> hourSpinner, Spinner<Integer> minuteSpinner, Spinner<Double> durataSpinner, ComboBox<String> appCombo, TextField codeField) {
        try {
            SessioneOnlineDao onlineDao = new SessioneOnlineDao();
            List<SessioneOnline> telematiche = SessioneOnlineDao.getSessioniByCorso(courseId);
            int hourTele = hourSpinner.getValue();
            int minTele = minuteSpinner.getValue();
            double durataTele = durataSpinner.getValue();
            LocalTime orarioTele = LocalTime.of(hourTele, minTele);
            LocalTime durataTeleTime = LocalTime.of((int)durataTele, (int)((durataTele - (int)durataTele)*60));
            String appValue = appCombo.getValue();
            String codeValue = codeField.getText();
            for (SessioneOnline sessione : telematiche) {
                if (!sessione.getData().isAfter(LocalDate.now())) continue;
                sessione.setOrario(orarioTele);
                sessione.setDurata(durataTeleTime);
                if (appValue != null) sessione.setApplicazione(appValue);
                if (codeValue != null) sessione.setCodicechiamata(codeValue);
                onlineDao.aggiornaSessione(sessione);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updatePresenzaSessionsFromHybridUI(Spinner<Integer> hourSpinner, Spinner<Integer> minuteSpinner, Spinner<Double> durataSpinner) {
        try {
            SessioneInPresenzaDao presenzaDao = new SessioneInPresenzaDao();
            ArrayList<SessioniInPresenza> presenze = SessioneInPresenzaDao.getSessioniByCorso(courseId);
            int hourPres = hourSpinner.getValue();
            int minPres = minuteSpinner.getValue();
            double durataPres = durataSpinner.getValue();
            LocalTime orarioPres = LocalTime.of(hourPres, minPres);
            LocalTime durataPresTime = LocalTime.of((int)durataPres, (int)((durataPres - (int)durataPres)*60));
            for (SessioniInPresenza sessione : presenze) {
                if (!sessione.getData().isAfter(java.time.LocalDate.now())) continue;
                sessione.setOrario(orarioPres);
                sessione.setDurata(durataPresTime);
                presenzaDao.aggiornaSessione(sessione);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCourseTypeUIFromSessions() {
        String type;
        if (hasPresenzaSessions && hasTelematicheSessions) {
            type = "Entrambi";
        } else if (hasPresenzaSessions) {
            type = "In presenza";
        } else if (hasTelematicheSessions) {
            type = "Telematica";
        } else {
            type = "In presenza"; 
        }
        courseTypeCombo.setValue(type);
        courseTypeCombo.setDisable(true);
        locationSection.setVisible(hasPresenzaSessions);
        locationSection.setManaged(hasPresenzaSessions);
        recipesSection.setVisible(hasPresenzaSessions);
        recipesSection.setManaged(hasPresenzaSessions);
    }
    private void addOnlineSessionToContainer(String app, String meetingCode, String orario, double durata, LocalDate sessionDate) {
        VBox sessionBox = new VBox(10);
        sessionBox.getStyleClass().add("online-session-container");

        boolean editable = sessionDate.isAfter(LocalDate.now());
        if (!editable) {
            sessionBox.getStyleClass().add("session-past");
        }

        HBox header = new HBox(15);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label appLabel = new Label("App:");
        TextField appField = new TextField(app);
        appField.setPromptText("App (es. Zoom, Teams)...");
        appField.setPrefWidth(120);
        appField.setDisable(!editable);

        Label codeLabel = new Label("Codice Meeting:");
        TextField codeField = new TextField(meetingCode);
        codeField.setPromptText("Codice riunione...");
        codeField.setPrefWidth(120);
        codeField.setDisable(!editable);

        Label orarioLabel = new Label("Orario:");
        TextField orarioField = new TextField(orario);
        orarioField.setPromptText("HH:MM");
        orarioField.setPrefWidth(80);
        orarioField.setDisable(!editable);

        Label durataLabel = new Label("Durata (h):");
        Spinner<Double> durataSpinner = new Spinner<>(0.5, 8.0, durata, 0.5);
        durataSpinner.setPrefWidth(80);
        durataSpinner.setDisable(!editable);

        Label sessionDateLabel = new Label("Sessione: " + sessionDate.toString());
        sessionDateLabel.getStyleClass().add("session-date-label");
        if (!editable) {
            sessionDateLabel.setText(sessionDateLabel.getText() + " (non modificabile)");
            sessionDateLabel.setStyle("-fx-text-fill: #888;");
            Tooltip.install(sessionBox, new Tooltip("Questa sessione è già avvenuta o è oggi e non è più modificabile."));
        } else {
            Tooltip.install(sessionBox, new Tooltip("Modificabile: la sessione è futura"));
        }

        header.getChildren().addAll(appLabel, appField, codeLabel, codeField, orarioLabel, orarioField, durataLabel, durataSpinner, sessionDateLabel);
        sessionBox.getChildren().add(header);
    }
    
    public void onCourseTypeChanged() {
        String selectedType = courseTypeCombo.getValue();
        boolean isPresenza = "In presenza".equals(selectedType);
        boolean isTelematica = "Telematica".equals(selectedType);
        boolean isEntrambi = "Entrambi".equals(selectedType);
        locationSection.setVisible(isPresenza || isEntrambi);
        locationSection.setManaged(isPresenza || isEntrambi);
        recipesSection.setVisible(isPresenza || isEntrambi);
        recipesSection.setManaged(isPresenza || isEntrambi);
        onlineSection.setVisible(isTelematica || isEntrambi);
        onlineSection.setManaged(isTelematica || isEntrambi);
        updateTimeAndDurationVisibility();
        setupHybridSpinners();
        if ("Entrambi".equals(selectedType)) {
            if (doubleTimeSection != null) {
                doubleTimeSection.setVisible(true);
                doubleTimeSection.setManaged(true);
            }
            if (singleTimeSection != null) {
                singleTimeSection.setVisible(false);
                singleTimeSection.setManaged(false);
            }
            if (startHourSpinnerPresenza != null) startHourSpinnerPresenza.setDisable(false);
            if (startMinuteSpinnerPresenza != null) startMinuteSpinnerPresenza.setDisable(false);
            if (durationSpinnerPresenza != null) durationSpinnerPresenza.setDisable(false);
            if (startHourSpinnerTelematica != null) startHourSpinnerTelematica.setDisable(false);
            if (startMinuteSpinnerTelematica != null) startMinuteSpinnerTelematica.setDisable(false);
            if (durationSpinnerTelematica != null) durationSpinnerTelematica.setDisable(false);
        }

        if (isTelematica) {
            streetField.clear();
            capField.clear();
            recipesContainer.getChildren().clear();
        } else if (isPresenza || isEntrambi) {
            if ("Entrambi".equals(selectedType)) {
                if (doubleTimeSection != null) {
                    doubleTimeSection.setVisible(true);
                    doubleTimeSection.setManaged(true);
                }
                if (singleTimeSection != null) {
                    singleTimeSection.setVisible(false);
                    singleTimeSection.setManaged(false);
                }
            }
            if (recipesContainer.getChildren().isEmpty()) {
                loadCourseData();
            }
        }
    }
    
    public void onFrequencyChanged() {
    }
    
    public void addNewRecipe() {
        boundary.addRecipeToContainer("", new String[0], new String[0], new String[0], LocalDate.now().plusDays(1));
    }
    
    public void saveCourse() {
        if (hasAnyFieldChanged() && boundary.validateFormData()) {
            try {
                CorsoDao corsoDao = new CorsoDao();
                Corso corsoDto = corsoDao.getCorsoById(courseId);
                if (corsoDto == null) {
                    boundary.showAlert("Errore", "Corso non trovato nel database.");
                    return;
                }
                corsoDto.setNome(courseNameField.getText().trim());
                corsoDto.setDescrizione(descriptionArea.getText().trim());
                corsoDto.setDataInizio(startDatePicker.getValue());
                corsoDto.setDataFine(endDatePicker.getValue());
                corsoDto.setMaxPersone(maxPersonsSpinner.getValue());
                corsoDto.setFrequenzaDelleSessioni(frequencyCombo.getValue());
                corsoDao.aggiornaCorso(corsoDto);

                String type = courseTypeCombo.getValue();

                if ("Telematica".equals(type) || "Entrambi".equals(type)) {
                    SessioneOnlineDao onlineDao = new SessioneOnlineDao();
                    List<SessioneOnline> telematiche = SessioneOnlineDao.getSessioniByCorso(courseId);
                    int hourTele = 18, minTele = 0;
                    double durataTele = 2.0;
                    String appValue = null;
                    String codeValue = null;
                    if ("Entrambi".equals(type)) {
                        for (Node node : onlineSection.getChildren()) {
                            if (node instanceof VBox) {
                                VBox sessionBox = (VBox) node;
                                HBox header = (HBox) sessionBox.getChildren().get(0);
                                for (Node n : header.getChildren()) {
                                    if (n instanceof ComboBox) {
                                        @SuppressWarnings("unchecked")
                                        ComboBox<String> appCombo = (ComboBox<String>) n;
                                        appValue = appCombo.getValue();
                                    } else if (n instanceof TextField) {
                                        TextField tf = (TextField) n;
                                        if (tf.getPromptText() != null && tf.getPromptText().toLowerCase().contains("codice")) {
                                            codeValue = tf.getText();
                                        }
                                    } else if (n instanceof Spinner) {
                                        Spinner<?> spinner = (Spinner<?>) n;
                                        double width = spinner.getPrefWidth();
                                        if (width == 90) { // ora
                                            hourTele = (Integer) spinner.getValue();
                                        } else if (width == 60) { // minuti
                                            minTele = (Integer) spinner.getValue();
                                        } else if (width == 80) { // durata
                                            durataTele = (Double) spinner.getValue();
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    } else {
                        hourTele = startHourSpinner.getValue();
                        minTele = startMinuteSpinner.getValue();
                        durataTele = durationSpinner.getValue();
                        for (Node node : onlineSection.getChildren()) {
                            if (node instanceof VBox) {
                                VBox sessionBox = (VBox) node;
                                HBox header = (HBox) sessionBox.getChildren().get(0);
                                for (Node n : header.getChildren()) {
                                    if (n instanceof ComboBox) {
                                        @SuppressWarnings("unchecked")
                                        ComboBox<String> appCombo = (ComboBox<String>) n;
                                        appValue = appCombo.getValue();
                                    }
                                    if (n instanceof TextField) {
                                        TextField tf = (TextField) n;
                                        if (tf.getPromptText() != null && tf.getPromptText().toLowerCase().contains("codice")) {
                                            codeValue = tf.getText();
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    int durataOre = (int) Math.round(Math.max(1, Math.min(8, durataTele)));
                    int durataMin = (int) Math.round((durataTele - durataOre) * 60);
                    LocalTime orarioTele = LocalTime.of(hourTele, minTele);
                    LocalTime durataTeleTime = LocalTime.of(durataOre, durataMin);
                    for (SessioneOnline sessione : telematiche) {
                        if (!sessione.getData().isAfter(LocalDate.now())) continue;
                        sessione.setOrario(orarioTele);
                        sessione.setDurata(durataTeleTime);
                        if (appValue != null) sessione.setApplicazione(appValue);
                        if (codeValue != null) sessione.setCodicechiamata(codeValue);
                        onlineDao.aggiornaSessione(sessione);
                    }
                }
                if ("In presenza".equals(type) || "Entrambi".equals(type)) {
                    SessioneInPresenzaDao presenzaDao = new SessioneInPresenzaDao();
                    ArrayList<SessioniInPresenza> presenze = presenzaDao.getSessioniByCorso(courseId);
                    int hourPres, minPres;
                    double durataPres;
                    if ("Entrambi".equals(type) && startHourSpinnerPresenza != null && startMinuteSpinnerPresenza != null && durationSpinnerPresenza != null) {
                        hourPres = startHourSpinnerPresenza.getValue();
                        minPres = startMinuteSpinnerPresenza.getValue();
                        durataPres = durationSpinnerPresenza.getValue();
                    } else {
                        hourPres = startHourSpinner.getValue();
                        minPres = startMinuteSpinner.getValue();
                        durataPres = durationSpinner.getValue();
                    }
                    int durataOre = (int) Math.max(1, Math.min(8, Math.round(durataPres)));
                    LocalTime orarioPres = LocalTime.of(hourPres, minPres);
                    LocalTime durataPresTime = LocalTime.of(durataOre, 0);
                    Map<LocalDate, SessioniInPresenza> presenzaByDate = new HashMap<>();
                    for (SessioniInPresenza sessione : presenze) {
                        presenzaByDate.put(sessione.getData(), sessione);
                        if (!sessione.getData().isAfter(LocalDate.now())) continue;
                        sessione.setVia(streetField.getText().trim());
                        sessione.setCap(capField.getText().trim());
                        sessione.setDescrizione(descriptionArea.getText().trim());
                        sessione.setOrario(orarioPres);
                        sessione.setDurata(durataPresTime);
                        presenzaDao.aggiornaSessione(sessione);
                    }
                    Set<LocalDate> sessioniConRicettaUI = new HashSet<>();
                    for (Node node : recipesContainer.getChildren()) {
                        if (node instanceof VBox) {
                            VBox recipeBox = (VBox) node;
                            HBox header = (HBox) recipeBox.getChildren().get(0);
                            Label sessionDateLabel = (Label) header.getChildren().get(3);
                            String labelText = sessionDateLabel.getText();
                            String dateStr = labelText.replace("Sessione: ", "").split(" ")[0];
                            LocalDate sessionDate = LocalDate.parse(dateStr);
                            if (sessionDate.isAfter(LocalDate.now())) {
                                sessioniConRicettaUI.add(sessionDate);
                            }
                        }
                    }

                    
                    ricettaDao ricettaDao = new ricettaDao();
                    for (LocalDate sessionDate : sessioniConRicettaUI) {
                        SessioniInPresenza sessione = presenzaByDate.get(sessionDate);
                        if (sessione == null) continue;
                        ArrayList<Ricetta> ricetteEsistenti = presenzaDao.recuperaRicetteSessione(sessione);
                        if (ricetteEsistenti != null) {
                            for (Ricetta ricettaOld : ricetteEsistenti) {
                                presenzaDao.rimuoviTutteAssociazioniRicetta(ricettaOld);
                                ricettaDao.cancellaricetta(ricettaOld);
                            }
                        }
                    }

                    for (Node node : recipesContainer.getChildren()) {
                        if (node instanceof VBox) {
                            VBox recipeBox = (VBox) node;
                            HBox header = (HBox) recipeBox.getChildren().get(0);
                            TextField recipeNameField = (TextField) header.getChildren().get(1);
                            Label sessionDateLabel = (Label) header.getChildren().get(3);
                            String labelText = sessionDateLabel.getText();
                            String dateStr = labelText.replace("Sessione: ", "").split(" ")[0];
                            LocalDate sessionDate = LocalDate.parse(dateStr);
                            if (!sessionDate.isAfter(LocalDate.now())) continue; 
                            String nomeRicetta = recipeNameField.getText().trim();
                            if (nomeRicetta.isEmpty()) continue;
                            SessioniInPresenza sessione = presenzaByDate.get(sessionDate);
                            if (sessione == null) continue;
                            ArrayList<Ricetta> ricetteEsistenti = presenzaDao.recuperaRicetteSessione(sessione);
                            Ricetta ricetta = null;
                            if (ricetteEsistenti != null) {
                                for (Ricetta r : ricetteEsistenti) {
                                    if (r.getNome().equalsIgnoreCase(nomeRicetta)) {
                                        ricetta = r;
                                        break;
                                    }
                                }
                            }
                            if (ricetta != null) {
                                ricetta.setNome(nomeRicetta);
                                ricettaDao.memorizzaRicetta(ricetta); 
                                presenzaDao.associaRicettaASessione(sessione, ricetta); 
                            } else {
                                ricetta = new Ricetta(nomeRicetta);
                                ricettaDao.memorizzaRicetta(ricetta);
                                presenzaDao.associaRicettaASessione(sessione, ricetta);
                            }
                            VBox ingredientsBox = (VBox) recipeBox.getChildren().get(1);
                            VBox ingredientsList = (VBox) ingredientsBox.getChildren().get(1);
                            List<Ingredienti> ingredientiEsistenti = ricettaDao.getIngredientiRicetta(ricetta);
                            Set<String> ingredientiUI = new HashSet<>();
                            for (Node ingrNode : ingredientsList.getChildren()) {
                                if (ingrNode instanceof HBox) {
                                    HBox ingrRow = (HBox) ingrNode;
                                    TextField nameField = (TextField) ingrRow.getChildren().get(0);
                                    TextField quantityField = (TextField) ingrRow.getChildren().get(1);
                                    ComboBox<?> unitCombo = (ComboBox<?>) ingrRow.getChildren().get(2);
                                    String ingrName = nameField.getText().trim();
                                    String ingrQuantity = quantityField.getText().trim();
                                    String ingrUnit = unitCombo.getValue() != null ? unitCombo.getValue().toString() : "g";
                                    if (ingrName.isEmpty() || ingrQuantity.isEmpty()) continue;
                                    float quant = 0;
                                    try { quant = Float.parseFloat(ingrQuantity); } catch (Exception ex) {}
                                    ingredientiUI.add(ingrName.toLowerCase());
                                    Ingredienti ingrEsistente = null;
                                    if (ingredientiEsistenti != null) {
                                        for (Ingredienti ingrDb : ingredientiEsistenti) {
                                            if (ingrDb.getNome().equalsIgnoreCase(ingrName)) {
                                                ingrEsistente = ingrDb;
                                                break;
                                            }
                                        }
                                    }
                                    IngredientiDao ingrDao = new IngredientiDao();
                                    if (ingrEsistente != null) {
                                        ingrEsistente.setQuantita(quant);
                                        ingrEsistente.setUnitaMisura(ingrUnit);
                                        ingrDao.modificaIngredienti(ingrEsistente);
                                    } else {
                                        Ingredienti nuovo = new Ingredienti(ingrName, quant, ingrUnit);
                                        ingrDao.memorizzaIngredienti(nuovo);
                                        ricettaDao.associaIngredientiARicetta(ricetta, nuovo);
                                    }
                                }
                            }
                            if (ingredientiEsistenti != null) {
                                for (Ingredienti ingrDb : ingredientiEsistenti) {
                                    if (!ingredientiUI.contains(ingrDb.getNome().toLowerCase())) {
                                        ricettaDao.rimuoviAssociazioneIngrediente(ricetta, ingrDb);
                                        IngredientiDao ingrDao = new IngredientiDao();
                                        boolean usatoAltrove = ingrDao.isIngredienteUsatoAltrove(ingrDb);
                                        if (!usatoAltrove) {
                                            ingrDao.eliminaIngrediente(ingrDb);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                boundary.showCourseUpdateSuccessDialog();
            } catch (Exception e) {
                boundary.showAlert("Errore", "Si è verificato un errore durante l'aggiornamento del corso.");
                e.printStackTrace();
            }
        }
    }
    
    public void goBack() {
        try {
            Stage stage = (Stage) courseNameField.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/homepagechef.fxml", "UninaFoodLab - Homepage");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}