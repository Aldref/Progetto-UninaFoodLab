package com.progetto.controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import com.progetto.boundary.AccountManagementBoundary;
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;

public class AccountManagementController {

    private AccountManagementBoundary boundary;
    private String originalName;
    private String originalSurname;
    private String originalEmail;
    private LocalDate originalBirthDate;

    public AccountManagementController(AccountManagementBoundary boundary) {
        this.boundary = boundary;
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

        boundary.getNameField().setText(originalName);
        boundary.getSurnameField().setText(originalSurname);
        boundary.getEmailField().setText(originalEmail);
        boundary.getBirthDatePicker().setValue(originalBirthDate);

        boundary.getUserNameLabel().setText(originalName + " " + originalSurname);
    }

    public void changePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Foto Profilo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) boundary.getChangePhotoBtn().getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // TODO: Implementare caricamento foto con le classi dedicate
            boundary.showSuccessMessage("Foto profilo selezionata: " + selectedFile.getName());
        }
    }

    public void saveChanges() {
        if (boundary.getNameField().getText().trim().isEmpty() ||
            boundary.getSurnameField().getText().trim().isEmpty() ||
            boundary.getEmailField().getText().trim().isEmpty() ||
            boundary.getBirthDatePicker().getValue() == null) {

            boundary.showErrorMessage("Compila tutti i campi obbligatori.");
            return;
        }

        try {
            // TODO: Implementare salvataggio con le classi dedicate

            originalName = boundary.getNameField().getText();
            originalSurname = boundary.getSurnameField().getText();
            originalEmail = boundary.getEmailField().getText();
            originalBirthDate = boundary.getBirthDatePicker().getValue();

            boundary.getUserNameLabel().setText(originalName + " " + originalSurname);

            boundary.getCurrentPasswordField().clear();
            boundary.getNewPasswordField().clear();
            boundary.getConfirmPasswordField().clear();

            boundary.showSuccessMessage("Dati aggiornati con successo!");

        } catch (Exception e) {
            boundary.showErrorMessage("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    public void cancelChanges() {
        boundary.getNameField().setText(originalName);
        boundary.getSurnameField().setText(originalSurname);
        boundary.getEmailField().setText(originalEmail);
        boundary.getBirthDatePicker().setValue(originalBirthDate);

        boundary.getCurrentPasswordField().clear();
        boundary.getNewPasswordField().clear();
        boundary.getConfirmPasswordField().clear();

        boundary.showInfoMessage("Modifiche annullate.");
    }

    public void goToUserCards() {
        try {
            Stage stage = (Stage) boundary.getViewUserCardsBtn().getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/usercards.fxml", "UninaFoodLab - Carte Utente");
        } catch (IOException e) {
            e.printStackTrace();
            boundary.showErrorMessage("Impossibile aprire la pagina delle carte.");
        }
    }

    public void goToHomepage() {
        try {
            Stage stage = (Stage) boundary.getNameField().getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/homepageutente.fxml", "UninaFoodLab - Homepage");
        } catch (IOException e) {
            System.err.println("Errore nel cambio pagina: " + e.getMessage());
            boundary.showErrorMessage("Errore durante la navigazione alla homepage.");
        }
    }

    public void goToEnrolledCourses() {
        try {
            Stage stage = (Stage) boundary.getNameField().getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/enrolledcourses.fxml", "UninaFoodLab - Corsi Iscritti");
        } catch (IOException e) {
            System.err.println("Errore nel cambio pagina: " + e.getMessage());
            boundary.showErrorMessage("Errore durante la navigazione ai corsi iscritti.");
        }
    }

    public void LogoutClick() {
        try {
            Stage stage = (Stage) boundary.getNameField().getScene().getWindow();
            LogoutDialogBoundary dialogBoundary = SceneSwitcher.showLogoutDialog(stage);

            if (dialogBoundary.isConfirmed()) {
                SceneSwitcher.switchToLogin(stage, "/fxml/loginpage.fxml", "UninaFoodLab - Login");
            }
        } catch (Exception e) {
            e.printStackTrace();
            boundary.showErrorMessage("Errore durante il logout.");
        }
    }
}