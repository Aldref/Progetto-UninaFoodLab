package com.progetto.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;

public class AccountManagementController {

    private Label userNameLabel;
    private ImageView userProfileImage;
    private ImageView profileImageLarge;
    private Button changePhotoBtn;
    private TextField nameField;
    private TextField surnameField;
    private TextField emailField;
    private DatePicker birthDatePicker;
    private PasswordField currentPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private Button saveBtn;
    private Button cancelBtn;

    private String originalName;
    private String originalSurname;
    private String originalEmail;
    private LocalDate originalBirthDate;

    public AccountManagementController(
        Label userNameLabel, ImageView userProfileImage, ImageView profileImageLarge, Button changePhotoBtn,
        TextField nameField, TextField surnameField, TextField emailField, DatePicker birthDatePicker,
        PasswordField currentPasswordField, PasswordField newPasswordField, PasswordField confirmPasswordField,
        Button saveBtn, Button cancelBtn
    ) {
        this.userNameLabel = userNameLabel;
        this.userProfileImage = userProfileImage;
        this.profileImageLarge = profileImageLarge;
        this.changePhotoBtn = changePhotoBtn;
        this.nameField = nameField;
        this.surnameField = surnameField;
        this.emailField = emailField;
        this.birthDatePicker = birthDatePicker;
        this.currentPasswordField = currentPasswordField;
        this.newPasswordField = newPasswordField;
        this.confirmPasswordField = confirmPasswordField;
        this.saveBtn = saveBtn;
        this.cancelBtn = cancelBtn;
    }

    public void initialize() {
        loadUserData();
    }

    private void loadUserData() {
        // Dati di esempio - sostituire con database
        originalName = "Mario";
        originalSurname = "Rossi";
        originalEmail = "mario.rossi@example.com";
        originalBirthDate = LocalDate.of(1990, 5, 15);

        nameField.setText(originalName);
        surnameField.setText(originalSurname);
        emailField.setText(originalEmail);
        birthDatePicker.setValue(originalBirthDate);

        userNameLabel.setText(originalName + " " + originalSurname);
    }

    public void changePhoto() {
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

    public void saveChanges() {
        if (nameField.getText().trim().isEmpty() ||
            surnameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            birthDatePicker.getValue() == null) {

            showErrorMessage("Compila tutti i campi obbligatori.");
            return;
        }

        try {
            // TODO: Implementare salvataggio con le classi dedicate

            originalName = nameField.getText();
            originalSurname = surnameField.getText();
            originalEmail = emailField.getText();
            originalBirthDate = birthDatePicker.getValue();

            userNameLabel.setText(originalName + " " + originalSurname);

            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

            showSuccessMessage("Dati aggiornati con successo!");

        } catch (Exception e) {
            showErrorMessage("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    public void cancelChanges() {
        nameField.setText(originalName);
        surnameField.setText(originalSurname);
        emailField.setText(originalEmail);
        birthDatePicker.setValue(originalBirthDate);

        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();

        showInfoMessage("Modifiche annullate.");
    }

    public void goToHomepage() {
        try {
            Stage stage = (Stage) nameField.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/homepageutente.fxml", "UninaFoodLab - Homepage");
        } catch (IOException e) {
            System.err.println("Errore nel cambio pagina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void goToEnrolledCourses() {
        try {
            Stage stage = (Stage) nameField.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/enrolledcourses.fxml", "UninaFoodLab - Corsi Iscritti");
        } catch (IOException e) {
            System.err.println("Errore nel cambio pagina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void LogoutClick() {
        try {
            Stage stage = (Stage) nameField.getScene().getWindow();
            LogoutDialogBoundary dialogBoundary = SceneSwitcher.showLogoutDialog(stage);

            if (dialogBoundary.isConfirmed()) {
                SceneSwitcher.switchToLogin(stage, "/fxml/loginpage.fxml", "UninaFoodLab - Login");
            }
        } catch (Exception e) {
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