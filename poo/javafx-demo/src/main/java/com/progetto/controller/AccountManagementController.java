package com.progetto.controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import com.progetto.boundary.AccountManagementBoundary;
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;
import com.progetto.utils.SuccessDialogUtils;


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
        // Carica i dati reali dell'utente loggato dal DB
        com.progetto.Entity.EntityDto.UtenteVisitatore utente = com.progetto.Entity.EntityDto.UtenteVisitatore.loggedUser;
        if (utente != null) {
            utente.getUtenteVisitatoreDao().recuperaDatiUtente(utente);
            originalName = utente.getNome();
            originalSurname = utente.getCognome();
            originalEmail = utente.getEmail();
            originalBirthDate = utente.getDataDiNascita();

            boundary.getNameField().setText(originalName);
            boundary.getSurnameField().setText(originalSurname);
            boundary.getEmailField().setText(originalEmail);
            boundary.getBirthDatePicker().setValue(originalBirthDate);
            boundary.getUserNameLabel().setText(originalName + " " + originalSurname);

            // Carica la foto profilo se presente
            String propic = utente.getUrl_Propic();
            if (propic != null && !propic.isEmpty()) {
                File imgFile = new File("src/main/resources/" + propic);
                if (imgFile.exists()) {
                    javafx.scene.image.Image img = new javafx.scene.image.Image(imgFile.toURI().toString(), 256, 256, true, true);
                    if (boundary.getUserProfileImage() != null) boundary.getUserProfileImage().setImage(img);
                    if (boundary.getProfileImageLarge() != null) boundary.getProfileImageLarge().setImage(img);
                }
            }
        }
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
            try {
                com.progetto.Entity.EntityDto.UtenteVisitatore utente = com.progetto.Entity.EntityDto.UtenteVisitatore.loggedUser;
                if (utente != null) {
                    File destDir = new File("src/main/resources/immagini/PropicUtente");
                    if (!destDir.exists() || !destDir.isDirectory()) {
                        boundary.showErrorMessage("La cartella delle immagini non esiste o non è accessibile: " + destDir.getAbsolutePath());
                        return;
                    }
                    String nome = utente.getNome().replaceAll("\\s+", "_");
                    String cognome = utente.getCognome().replaceAll("\\s+", "_");
                    String ext = selectedFile.getName().substring(selectedFile.getName().lastIndexOf('.'));
                    String newFileName = nome + "_" + cognome + ext;
                    File destFile = new File(destDir, newFileName);
                    if (destFile.exists()) {
                        destFile.delete();
                    }
                    try {
                        java.nio.file.Files.copy(selectedFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception copyEx) {
                        boundary.showErrorMessage("Errore nel salvataggio fisico dell'immagine: " + copyEx.getMessage() + "\nPercorso: " + destFile.getAbsolutePath());
                        return;
                    }
                    if (!destFile.exists()) {
                        boundary.showErrorMessage("Copia fallita: il file non è stato creato in " + destFile.getAbsolutePath());
                        return;
                    }
                    String relativePath = "immagini/PropicUtente/" + newFileName;
                    utente.setUrl_Propic(relativePath);
                    try {
                        utente.getUtenteVisitatoreDao().ModificaUtente(utente);
                    } catch (Exception dbEx) {
                        boundary.showErrorMessage("Errore nel salvataggio del percorso nel database: " + dbEx.getMessage());
                        return;
                    }
                    if (!destFile.exists()) {
                        boundary.showErrorMessage("Errore: il file non esiste dopo la copia.");
                        return;
                    }
                    javafx.scene.image.Image img = new javafx.scene.image.Image(destFile.toURI().toString());
                    if (boundary.getUserProfileImage() != null) boundary.getUserProfileImage().setImage(img);
                    if (boundary.getProfileImageLarge() != null) boundary.getProfileImageLarge().setImage(img);
                    boundary.showSuccessMessage("Foto profilo aggiornata!");
                }
            } catch (Exception ex) {
                boundary.showErrorMessage("Errore durante il caricamento della foto: " + ex.getMessage());
            }
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
        // Validazione email
        String email = boundary.getEmailField().getText().trim();
        if (!email.contains("@")) {
            boundary.showErrorMessage("Inserisci un'email valida.");
            return;
        }
        // Validazione età >= 18
        java.time.LocalDate birthDate = boundary.getBirthDatePicker().getValue();
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.Period age = java.time.Period.between(birthDate, today);
        if (age.getYears() < 18) {
            boundary.showErrorMessage("Devi avere almeno 18 anni.");
            return;
        }

        com.progetto.Entity.EntityDto.UtenteVisitatore utente = com.progetto.Entity.EntityDto.UtenteVisitatore.loggedUser;
        if (utente == null) {
            boundary.showErrorMessage("Utente non trovato.");
            return;
        }

        String currentPwd = boundary.getCurrentPasswordField().getText();
        String newPwd = boundary.getNewPasswordField().getText();
        String confirmPwd = boundary.getConfirmPasswordField().getText();

        boolean wantChangePwd = !currentPwd.isEmpty() || !newPwd.isEmpty() || !confirmPwd.isEmpty();

        if (wantChangePwd) {
            // Tutti i campi devono essere compilati
            if (currentPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                boundary.showErrorMessage("Per cambiare password compila tutti i campi password.");
                return;
            }
            // Controllo che la password attuale sia corretta
            if (!utente.getPassword().equals(currentPwd)) {
                boundary.showErrorMessage("La password attuale non è corretta.");
                return;
            }
            // Controllo che la nuova password sia confermata
            if (!newPwd.equals(confirmPwd)) {
                boundary.showErrorMessage("La nuova password e la conferma non coincidono.");
                return;
            }
            // Aggiorna la password
            utente.setPassword(newPwd);
        }

        // Aggiorna gli altri dati
        utente.setNome(boundary.getNameField().getText());
        utente.setCognome(boundary.getSurnameField().getText());
        utente.setEmail(boundary.getEmailField().getText());
        utente.setDataDiNascita(boundary.getBirthDatePicker().getValue());

        try {
            utente.getUtenteVisitatoreDao().ModificaUtente(utente);

            originalName = utente.getNome();
            originalSurname = utente.getCognome();
            originalEmail = utente.getEmail();
            originalBirthDate = utente.getDataDiNascita();

            boundary.getUserNameLabel().setText(originalName + " " + originalSurname);

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