package com.progetto.controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import com.progetto.boundary.AccountManagementChefBoundary;
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;
import com.progetto.utils.SuccessDialogUtils;

public class AccountManagementChefController {

    private AccountManagementChefBoundary boundary;
    private String originalName;
    private String originalSurname;
    private String originalEmail;
    private LocalDate originalBirthDate;

    public AccountManagementChefController(AccountManagementChefBoundary boundary) {
        this.boundary = boundary;
    }

    public void initialize() {
        loadChefData();
    }

    private void loadChefData() {
        // Dati di esempio per chef - sostituire con database
        originalName = "Mario";
        originalSurname = "Rossi";
        originalEmail = "chef.mario@unifoodlab.com";
        originalBirthDate = LocalDate.of(1985, 3, 20);

        boundary.getNameField().setText(originalName);
        boundary.getSurnameField().setText(originalSurname);
        boundary.getEmailField().setText(originalEmail);
        boundary.getBirthDatePicker().setValue(originalBirthDate);

        boundary.getUserNameLabel().setText(originalName + " " + originalSurname);
    }

    public void changePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Foto Profilo Chef");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) boundary.getChangePhotoBtn().getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // TODO: Implementare caricamento foto con le classi dedicate
            boundary.showSuccessMessage("Foto profilo chef selezionata: " + selectedFile.getName());
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
            // TODO: Implementare salvataggio con le classi dedicate per chef

            originalName = boundary.getNameField().getText();
            originalSurname = boundary.getSurnameField().getText();
            originalEmail = boundary.getEmailField().getText();
            originalBirthDate = boundary.getBirthDatePicker().getValue();

            boundary.getUserNameLabel().setText("Chef " + originalName + " " + originalSurname);

            boundary.getCurrentPasswordField().clear();
            boundary.getNewPasswordField().clear();
            boundary.getConfirmPasswordField().clear();

            
            Stage stage = (Stage) boundary.getNameField().getScene().getWindow();
            SuccessDialogUtils.showSaveSuccessDialog(stage);

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

        Stage stage = (Stage) boundary.getNameField().getScene().getWindow();
        SuccessDialogUtils.showCancelSuccessDialog(stage);
    }

    // Navigazione specifica per chef
    public void goToHomepage() {
        try {
            Stage stage = (Stage) boundary.getNameField().getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/homepagechef.fxml", "UninaFoodLab - Homepage Chef");
        } catch (IOException e) {
            System.err.println("Errore nel cambio pagina: " + e.getMessage());
            boundary.showErrorMessage("Errore durante la navigazione alla homepage chef.");
        }
    }

    public void goToCreateCourse() {
        try {
            Stage stage = (Stage) boundary.getNameField().getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/createcourse.fxml", "UninaFoodLab - Crea Nuovo Corso");
        } catch (IOException e) {
            System.err.println("Errore nel cambio pagina: " + e.getMessage());
            boundary.showErrorMessage("Errore durante la navigazione alla creazione corso.");
        }
    }

    public void goToMonthlyReport() {
        try {
            Stage stage = (Stage) boundary.getNameField().getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/monthlyreport.fxml", "UninaFoodLab - Resoconto Mensile");
        } catch (IOException e) {
            System.err.println("Errore nel cambio pagina: " + e.getMessage());
            boundary.showErrorMessage("Errore durante la navigazione al resoconto mensile.");
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