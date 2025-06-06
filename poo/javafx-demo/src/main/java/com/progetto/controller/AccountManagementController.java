package com.progetto.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class AccountManagementController {

    // Sidebar elements
    @FXML private Label userNameLabel;
    @FXML private ImageView userProfileImage;

    // Profile elements
    @FXML private ImageView profileImageLarge;
    @FXML private Button changePhotoBtn;

    // Form fields
    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField emailField;
    @FXML private DatePicker birthDatePicker;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    // Action buttons
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    // Original values (per reset)
    private String originalName;
    private String originalSurname;
    private String originalEmail;
    private LocalDate originalBirthDate;

    @FXML
    public void initialize() {
        loadUserData();
    }

    
    private void loadUserData() {
        // Dati di esempio - sostituire con database
        originalName = "Mario";
        originalSurname = "Rossi";
        originalEmail = "mario.rossi@example.com";
        originalBirthDate = LocalDate.of(1990, 5, 15);

        // Popola i campi
        nameField.setText(originalName);
        surnameField.setText(originalSurname);
        emailField.setText(originalEmail);
        birthDatePicker.setValue(originalBirthDate);
        
        // Aggiorna label sidebar
        userNameLabel.setText(originalName + " " + originalSurname);
    }

    /**
     * Cambia foto profilo
     */
    @FXML
    private void changePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Foto Profilo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) changePhotoBtn.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // TODO: Implementare caricamento foto con le classi dedicate
            showSuccessMessage("Foto profilo selezionata: " + selectedFile.getName());
        }
    }


    @FXML
    private void saveChanges() {
        // Semplice validazione base
        if (nameField.getText().trim().isEmpty() || 
            surnameField.getText().trim().isEmpty() || 
            emailField.getText().trim().isEmpty() || 
            birthDatePicker.getValue() == null) {
            
            showErrorMessage("Compila tutti i campi obbligatori.");
            return;
        }

        try {
            // TODO: Implementare salvataggio con le classi dedicate
            
            // Aggiorna i valori originali
            originalName = nameField.getText();
            originalSurname = surnameField.getText();
            originalEmail = emailField.getText();
            originalBirthDate = birthDatePicker.getValue();
            
            // Aggiorna label sidebar
            userNameLabel.setText(originalName + " " + originalSurname);
            
            // Reset password fields
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            
            showSuccessMessage("Dati aggiornati con successo!");
            
        } catch (Exception e) {
            showErrorMessage("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    @FXML
    private void cancelChanges() {
        // Reset ai valori originali
        nameField.setText(originalName);
        surnameField.setText(originalSurname);
        emailField.setText(originalEmail);
        birthDatePicker.setValue(originalBirthDate);
        
        // Clear password fields
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        
        showInfoMessage("Modifiche annullate.");
    }

    @FXML
    private void goToHomepage() {
        try {
            Stage stage = (Stage) nameField.getScene().getWindow(); 
            SceneSwitcher.switchScene(stage, "/fxml/homepageutente.fxml", "UninaFoodLab - Homepage");
        } catch (IOException e) {
            System.err.println("Errore nel cambio pagina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToEnrolledCourses() {
        try {
            Stage stage = (Stage) nameField.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/enrolledcourses.fxml", "UninaFoodLab - Corsi Iscritti");
        } catch (IOException e) {
            System.err.println("Errore nel cambio pagina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void LogoutClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/logoutdialog.fxml"));
            VBox dialogContent = loader.load();
            LogoutDialogController dialogController = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            dialogStage.setTitle("Conferma Logout");

            javafx.scene.Scene dialogScene = new javafx.scene.Scene(dialogContent);
            dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();

            if (dialogController.isConfirmed()) {
                Stage stage = (Stage) nameField.getScene().getWindow(); // Cambiato da profileImageView
                SceneSwitcher.switchToLogin(stage, "/fxml/loginpage.fxml", "UninaFoodLab - Login");
            }
        } catch (Exception e) {
            System.err.println("Errore durante il logout: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}