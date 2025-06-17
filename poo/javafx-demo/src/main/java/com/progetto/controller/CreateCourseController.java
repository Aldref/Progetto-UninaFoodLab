package com.progetto.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Pattern;

import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.controller.CreateCourseController.Ingredient;
import com.progetto.controller.CreateCourseController.Recipe;
import com.progetto.utils.SceneSwitcher;
import com.progetto.utils.SuccessDialogUtils;

public class CreateCourseController {
    
    private TextField courseNameField;
    private TextArea descriptionArea;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private ComboBox<String> frequencyComboBox;
    private ComboBox<String> lessonTypeComboBox;
    private Spinner<Integer> maxParticipantsSpinner;
    private TextField priceField;
    private ImageView courseImageView;
    private Label chefNameLabel;
    private Button createButton;
    
    // Sezioni condizionali
    private VBox presenceDetailsSection;
    private VBox onlineDetailsSection;
    
    // Campi per lezioni in presenza
    private ListView<String> dayOfWeekListView;
    private TextField timeField;
    private TextField durationField; // Aggiunto campo durata
    private TextField cityField;
    private TextField streetField;
    private TextField capField;
    private VBox recipesContainer;
    
    // Campi per lezioni telematiche
    private ComboBox<String> applicationComboBox;
    private TextField meetingCodeField;
    private ListView<String> onlineDayOfWeekListView;
    private TextField onlineTimeField;
    private TextField onlineDurationField; // Aggiunto campo durata online
    
    // Lista delle ricette
    private ObservableList<Recipe> recipes = FXCollections.observableArrayList();
    
    // Pattern per validazione orario (HH:MM)
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    
    public CreateCourseController(TextField courseNameField, TextArea descriptionArea, 
                                DatePicker startDatePicker, DatePicker endDatePicker,
                                ComboBox<String> frequencyComboBox, ComboBox<String> lessonTypeComboBox,
                                Spinner<Integer> maxParticipantsSpinner, TextField priceField,
                                ImageView courseImageView, Label chefNameLabel, Button createButton,
                                VBox presenceDetailsSection, VBox onlineDetailsSection,
                                ListView<String> dayOfWeekListView, TextField timeField, TextField durationField,
                                TextField cityField, TextField streetField, TextField capField,
                                VBox recipesContainer, ComboBox<String> applicationComboBox, 
                                TextField meetingCodeField, ListView<String> onlineDayOfWeekListView, 
                                TextField onlineTimeField, TextField onlineDurationField) {
        
        this.courseNameField = courseNameField;
        this.descriptionArea = descriptionArea;
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
        this.dayOfWeekListView = dayOfWeekListView;
        this.timeField = timeField;
        this.durationField = durationField; // Aggiunto durata
        this.cityField = cityField;
        this.streetField = streetField;
        this.capField = capField;
        this.recipesContainer = recipesContainer;
        this.applicationComboBox = applicationComboBox;
        this.meetingCodeField = meetingCodeField;
        this.onlineDayOfWeekListView = onlineDayOfWeekListView;
        this.onlineTimeField = onlineTimeField;
        this.onlineDurationField = onlineDurationField; // Aggiunto durata online
    }
    
    public void initialize() {
        // Imposta il nome dello chef
        chefNameLabel.setText("Mario Rossi");
        
        // Configura ComboBox
        frequencyComboBox.getItems().addAll(
            "1 volta a settimana",
            "2 volte a settimana", 
            "3 volte a settimana"
        );
        
        lessonTypeComboBox.getItems().addAll(
            "In presenza",
            "Telematica"
        );
        
        applicationComboBox.getItems().addAll(
            "Zoom",
            "Microsoft Teams",
            "Google Meet"
        );
        
        // Configura ListView giorni
        String[] days = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};
        dayOfWeekListView.getItems().addAll(days);
        onlineDayOfWeekListView.getItems().addAll(days);
        
        dayOfWeekListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        onlineDayOfWeekListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Configura Spinner
        SpinnerValueFactory<Integer> maxParticipantsFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10);
        maxParticipantsSpinner.setValueFactory(maxParticipantsFactory);
        
        // Imposta date minime
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
        
        // Validazione CAP (solo numeri, max 5 cifre)
        capField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                capField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 5) {
                capField.setText(newValue.substring(0, 5));
            }
        });
        
        // Validazione PREZZO (solo numeri e punto decimale)
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                String filtered = newValue.replaceAll("[^\\d.]", "");
                
                int dotIndex = filtered.indexOf('.');
                if (dotIndex != -1) {
                    String beforeDot = filtered.substring(0, dotIndex);
                    String afterDot = filtered.substring(dotIndex + 1);
                    afterDot = afterDot.replaceAll("\\.", "");
                    if (afterDot.length() > 2) {
                        afterDot = afterDot.substring(0, 2);
                    }
                    filtered = beforeDot + "." + afterDot;
                }
                
                priceField.setText(filtered);
            }
        });

        // Validazione ORARIO (formato HH:MM)
        timeField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateAndFormatTime(timeField, newValue);
        });
        
        onlineTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateAndFormatTime(onlineTimeField, newValue);
        });
        
        // Validazione DURATA (solo numeri, max 480 minuti = 8 ore)
        durationField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateDuration(durationField, newValue);
        });
        
        onlineDurationField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateDuration(onlineDurationField, newValue);
        });

        // Inizializza con almeno una ricetta vuota PRIMA di configurare la validazione
        addNewRecipe();

        // Configura validazione in tempo reale DOPO aver aggiunto la ricetta
        setupValidation();
    }
    
    private void validateAndFormatTime(TextField field, String newValue) {
        // Rimuove tutto ciò che non è numero o ":"
        String filtered = newValue.replaceAll("[^\\d:]", "");
        
        // Limita la lunghezza a 5 caratteri (HH:MM)
        if (filtered.length() > 5) {
            filtered = filtered.substring(0, 5);
        }
        
        // Auto-formattazione: aggiunge ":" dopo 2 cifre
        if (filtered.length() == 2 && !filtered.contains(":") && !newValue.endsWith(":")) {
            filtered = filtered + ":";
        }
        
        // Controlla che le ore siano valide (00-23)
        if (filtered.length() >= 2) {
            String hours = filtered.substring(0, 2);
            if (!hours.matches("^([01]?[0-9]|2[0-3])$")) {
                // Se le ore non sono valide, ripristina il valore precedente
                return;
            }
        }
        
        // Controlla che i minuti siano validi (00-59)
        if (filtered.length() >= 5) {
            String minutes = filtered.substring(3, 5);
            if (!minutes.matches("^[0-5][0-9]$")) {
                // Se i minuti non sono validi, ripristina il valore precedente
                return;
            }
        }
        
        field.setText(filtered);
    }
    
    private void validateDuration(TextField field, String newValue) {
        // Solo numeri
        if (!newValue.matches("\\d*")) {
            field.setText(newValue.replaceAll("[^\\d]", ""));
            return;
        }
        
        // Controlla che la durata sia ragionevole (1-480 minuti = max 8 ore)
        if (!newValue.isEmpty()) {
            try {
                int duration = Integer.parseInt(newValue);
                if (duration > 480) {
                    field.setText("480"); // Massimo 8 ore
                } else if (duration < 1 && newValue.length() > 1) {
                    field.setText("1"); // Minimo 1 minuto
                }
            } catch (NumberFormatException e) {
                field.setText("");
            }
        }
    }
    
    private boolean isValidTime(String timeText) {
        if (timeText == null || timeText.trim().isEmpty()) return false;
        return TIME_PATTERN.matcher(timeText.trim()).matches();
    }
    
    private boolean isValidDuration(String durationText) {
        if (durationText == null || durationText.trim().isEmpty()) return false;
        try {
            int duration = Integer.parseInt(durationText.trim());
            return duration >= 1 && duration <= 480; // 1-480 minuti
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private void setupValidation() {
        // Binding per validazione campi base
        BooleanBinding basicFieldsValid = Bindings.createBooleanBinding(() -> 
            courseNameField.getText() != null && !courseNameField.getText().trim().isEmpty() &&
            descriptionArea.getText() != null && !descriptionArea.getText().trim().isEmpty() &&
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
        
        // Binding per validazione dettagli lezioni (semplificato)
        BooleanBinding lessonDetailsValid = Bindings.createBooleanBinding(() -> {
            String lessonType = lessonTypeComboBox.getValue();
            if ("In presenza".equals(lessonType)) {
                return hasSelectedDays(dayOfWeekListView) &&
                    isValidTime(timeField.getText()) &&
                    isValidDuration(durationField.getText()) &&
                    isValidText(cityField.getText()) &&
                    isValidText(streetField.getText()) &&
                    isValidCAP(capField.getText());
            } else if ("Telematica".equals(lessonType)) {
                return applicationComboBox.getValue() != null &&
                    isValidText(meetingCodeField.getText()) &&
                    hasSelectedDays(onlineDayOfWeekListView) &&
                    isValidTime(onlineTimeField.getText()) &&
                    isValidDuration(onlineDurationField.getText());
            }
            return false;
        },
        lessonTypeComboBox.valueProperty(),
        dayOfWeekListView.getSelectionModel().getSelectedItems(),
        timeField.textProperty(),
        durationField.textProperty(),
        cityField.textProperty(),
        streetField.textProperty(),
        capField.textProperty(),
        applicationComboBox.valueProperty(),
        meetingCodeField.textProperty(),
        onlineDayOfWeekListView.getSelectionModel().getSelectedItems(),
        onlineTimeField.textProperty(),
        onlineDurationField.textProperty()
        );
        
        // Disabilita il pulsante se i campi non sono validi
        createButton.disableProperty().bind(
            basicFieldsValid.not().or(lessonDetailsValid.not())
        );
    }
    
    public void onLessonTypeChanged() {
        String selectedType = lessonTypeComboBox.getValue();
        
        if ("In presenza".equals(selectedType)) {
            presenceDetailsSection.setVisible(true);
            presenceDetailsSection.setManaged(true);
            onlineDetailsSection.setVisible(false);
            onlineDetailsSection.setManaged(false);
        } else if ("Telematica".equals(selectedType)) {
            presenceDetailsSection.setVisible(false);
            presenceDetailsSection.setManaged(false);
            onlineDetailsSection.setVisible(true);
            onlineDetailsSection.setManaged(true);
        } else {
            presenceDetailsSection.setVisible(false);
            presenceDetailsSection.setManaged(false);
            onlineDetailsSection.setVisible(false);
            onlineDetailsSection.setManaged(false);
        }
    }
    
    // ...resto dei metodi esistenti per ricette...
    
    public void createCourse() {
        try {
            // TODO: Integrazione con database tramite DAO
            // CourseDAO courseDAO = new CourseDAO();
            // Course newCourse = buildCourseFromForm();
            // courseDAO.save(newCourse);
            
            // Mostra dialog di successo
            showCourseCreationSuccessDialog();
            
        } catch (Exception e) {
            showAlert("Errore", "Si è verificato un errore durante la creazione del corso.");
            e.printStackTrace();
        }
    }
    
    private void showCourseCreationSuccessDialog() {
        try {
            Stage parentStage = (Stage) courseNameField.getScene().getWindow();
            String courseName = courseNameField.getText();
            
            // Usa il dialog di successo generico
            SuccessDialogUtils.showGenericSuccessDialog(
                parentStage, 
                "Corso Creato con Successo!", 
                "Il corso \"" + courseName + "\" è stato creato e pubblicato con successo."
            );
            
            // Dopo aver mostrato il dialog, torna alla homepage
            goToHomepage();
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback al dialog semplice
            showAlert("Successo", "Il corso \"" + courseNameField.getText() + "\" è stato creato con successo!");
            goToHomepage();
        }
    }
    
    // ...resto dei metodi esistenti...
    
    private boolean isValidPrice(String priceText) {
        if (priceText == null || priceText.trim().isEmpty()) return false;
        try {
            double price = Double.parseDouble(priceText.trim());
            return price > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isValidCAP(String capText) {
        if (capText == null || capText.trim().isEmpty()) return false;
        String cap = capText.trim();
        return cap.matches("\\d{5}");
    }
    
    private boolean hasSelectedDays(ListView<String> listView) {
        return listView.getSelectionModel().getSelectedItems() != null && 
            !listView.getSelectionModel().getSelectedItems().isEmpty();
    }

    private boolean isValidText(String text) {
        return text != null && !text.trim().isEmpty();
    }
    
    public void addNewRecipe() {
        Recipe recipe = new Recipe();
        recipes.add(recipe);
        
        VBox recipeBox = createRecipeBox(recipe);
        recipesContainer.getChildren().add(recipeBox);
    }
    
    private VBox createRecipeBox(Recipe recipe) {
        VBox recipeBox = new VBox(15);
        recipeBox.getStyleClass().add("recipe-container");
        recipeBox.setPadding(new Insets(15));
        
        // Header con nome ricetta e pulsante rimuovi
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
        
        // Container per ingredienti
        VBox ingredientsBox = new VBox(10);
        Label ingredientsLabel = new Label("Ingredienti:");
        ingredientsLabel.getStyleClass().add("field-label");
        
        VBox ingredientsList = new VBox(8);
        recipe.setIngredientsContainer(ingredientsList);
        
        Button addIngredientBtn = new Button("+ Aggiungi Ingrediente");
        addIngredientBtn.getStyleClass().add("add-ingredient-button");
        addIngredientBtn.setOnAction(e -> addIngredient(recipe));
        
        ingredientsBox.getChildren().addAll(ingredientsLabel, ingredientsList, addIngredientBtn);
        
        recipeBox.getChildren().addAll(headerBox, ingredientsBox);
        
        // Aggiungi il primo ingrediente
        addIngredient(recipe);
        
        return recipeBox;
    }
    
    private void addIngredient(Recipe recipe) {
        Ingredient ingredient = new Ingredient();
        recipe.addIngredient(ingredient);
        
        HBox ingredientBox = new HBox(10);
        ingredientBox.getStyleClass().add("ingredient-row");
        
        TextField nameField = new TextField();
        nameField.setPromptText("Nome ingrediente");
        nameField.getStyleClass().addAll("form-field", "ingredient-name-field");
        nameField.textProperty().addListener((obs, oldVal, newVal) -> ingredient.setName(newVal));
        
        TextField quantityField = new TextField();
        quantityField.setPromptText("Qta");
        quantityField.getStyleClass().addAll("form-field", "ingredient-quantity-field");
        
        // Validazione quantità ingredienti (solo numeri e decimali)
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Permette solo numeri, punto decimale e virgola
            if (!newValue.matches("\\d*[.,]?\\d*")) {
                String filtered = newValue.replaceAll("[^\\d.,]", "");
                
                // Sostituisce la virgola con il punto per uniformità
                filtered = filtered.replace(",", ".");
                
                // Gestisce multipli punti decimali
                int dotIndex = filtered.indexOf('.');
                if (dotIndex != -1) {
                    String beforeDot = filtered.substring(0, dotIndex);
                    String afterDot = filtered.substring(dotIndex + 1);
                    // Rimuove eventuali punti aggiuntivi dalla parte decimale
                    afterDot = afterDot.replaceAll("\\.", "");
                    // Limita a massimo 3 cifre decimali per le quantità
                    if (afterDot.length() > 3) {
                        afterDot = afterDot.substring(0, 3);
                    }
                    filtered = beforeDot + "." + afterDot;
                }
                
                quantityField.setText(filtered);
            } else {
                // Sostituisce la virgola con il punto per uniformità
                String normalized = newValue.replace(",", ".");
                if (!normalized.equals(newValue)) {
                    quantityField.setText(normalized);
                } else {
                    ingredient.setQuantity(newValue);
                }
            }
        });
        
        ComboBox<String> unitCombo = new ComboBox<>();
        unitCombo.getItems().addAll("g", "kg", "ml", "l", "pz", "cucchiai", "cucchiaini", "tazze", "q.b.");
        unitCombo.setPromptText("Unità");
        unitCombo.getStyleClass().addAll("form-field", "ingredient-unit-combo");
        unitCombo.valueProperty().addListener((obs, oldVal, newVal) -> ingredient.setUnit(newVal));
        
        Button removeBtn = new Button("✕");
        removeBtn.getStyleClass().add("remove-ingredient-button");
        removeBtn.setOnAction(e -> removeIngredient(ingredientBox, recipe, ingredient));
        
        ingredientBox.getChildren().addAll(nameField, quantityField, unitCombo, removeBtn);
        recipe.getIngredientsContainer().getChildren().add(ingredientBox);
    }
    
    private void removeIngredient(HBox ingredientBox, Recipe recipe, Ingredient ingredient) {
        recipe.removeIngredient(ingredient);
        recipe.getIngredientsContainer().getChildren().remove(ingredientBox);
    }
    
    private void removeRecipe(VBox recipeBox, Recipe recipe) {
        if (recipes.size() > 1) {
            recipes.remove(recipe);
            recipesContainer.getChildren().remove(recipeBox);
        } else {
            showAlert("Attenzione", "Deve essere presente almeno una ricetta.");
        }
    }
    
    public void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Immagine del Corso");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg")
        );
        
        Stage stage = (Stage) courseImageView.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                courseImageView.setImage(image);
            } catch (Exception e) {
                showAlert("Errore", "Impossibile caricare l'immagine selezionata.");
            }
        }
    }
    
    public void goToHomepage() {
        try {
            Stage stage = (Stage) courseNameField.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/homepagechef.fxml", "UninaFoodLab - Dashboard Chef");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void goToMonthlyReport() {
        try {
            Stage stage = (Stage) courseNameField.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/monthlyreport.fxml", "UninaFoodLab - Resoconto Mensile");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void goToAccountManagement() {
        try {
            Stage stage = (Stage) courseNameField.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/accountmanagementchef.fxml", "UninaFoodLab - Gestione Account Chef");
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
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Successo") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Classi interne per Recipe e Ingredient
    public static class Recipe {
        private String name;
        private ObservableList<Ingredient> ingredients = FXCollections.observableArrayList();
        private VBox ingredientsContainer;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public ObservableList<Ingredient> getIngredients() { return ingredients; }
        public void addIngredient(Ingredient ingredient) { ingredients.add(ingredient); }
        public void removeIngredient(Ingredient ingredient) { ingredients.remove(ingredient); }
        
        public VBox getIngredientsContainer() { return ingredientsContainer; }
        public void setIngredientsContainer(VBox container) { this.ingredientsContainer = container; }
    }
    
    public static class Ingredient {
        private String name;
        private String quantity;
        private String unit;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getQuantity() { return quantity; }
        public void setQuantity(String quantity) { this.quantity = quantity; }
        
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }
}