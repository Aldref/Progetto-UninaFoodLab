package com.progetto.controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Period;

import com.progetto.boundary.AccountManagementBoundary;
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;
import com.progetto.utils.SuccessDialogUtils;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.Entity.entityDao.UtenteVisitatoreDao;

public class AccountManagementController {
    private static UtenteVisitatore loggedUser = UtenteVisitatore.loggedUser;
    private static UtenteVisitatoreDao utenteDao = new UtenteVisitatoreDao();

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
        if (loggedUser != null) {
            utenteDao.recuperaDatiUtente(loggedUser);
            originalName = loggedUser.getNome();
            originalSurname = loggedUser.getCognome();
            originalEmail = loggedUser.getEmail();
            originalBirthDate = loggedUser.getDataDiNascita();

            boundary.getNameField().setText(originalName);
            boundary.getSurnameField().setText(originalSurname);
            boundary.getEmailField().setText(originalEmail);
            boundary.getBirthDatePicker().setValue(originalBirthDate);
            boundary.getUserNameLabel().setText(originalName + " " + originalSurname);

            String propic = loggedUser.getUrl_Propic();
            boundary.setProfileImages(propic);
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
            String ext = selectedFile.getName().toLowerCase();
            if (!(ext.endsWith(".png") || ext.endsWith(".jpg") || ext.endsWith(".jpeg") || ext.endsWith(".gif"))) {
                boundary.showErrorMessage("Formato immagine non supportato. Usa PNG, JPG, JPEG o GIF.");
                return;
            }

            if (loggedUser == null) {
                boundary.showErrorMessage("Utente non trovato.");
                return;
            }
            String nome = loggedUser.getNome() != null ? loggedUser.getNome().replaceAll("[^a-zA-Z0-9]", "_") : "utente";
            String cognome = loggedUser.getCognome() != null ? loggedUser.getCognome().replaceAll("[^a-zA-Z0-9]", "_") : "profilo";
            String id = "";
            try {
                id = String.valueOf(loggedUser.getId_UtenteVisitatore());
            } catch (Exception e) {
                id = "id";
            }
            String extension = ext.substring(ext.lastIndexOf('.'));
            String fileName = nome + "_" + cognome + "_" + id + extension;
            String userImgDir = "src/main/resources/immagini/PropicUtente/";
            new File(userImgDir).mkdirs();
            String absolutePath = userImgDir + fileName;
            File destFile = new File(absolutePath);

            try {
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                boundary.showErrorMessage("Errore nel salvataggio della foto profilo: " + e.getMessage());
                return;
            }

            String relativePath = "immagini/PropicUtente/" + fileName;
            loggedUser.setUrl_Propic(relativePath);
            try {
                utenteDao.ModificaUtente(loggedUser);
            } catch (Exception e) {
                boundary.showErrorMessage("Errore nel salvataggio della foto profilo nel database: " + e.getMessage());
                return;
            }
            boundary.setProfileImages(relativePath);
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
        String email = boundary.getEmailField().getText().trim();
        if (!email.contains("@")) {
            boundary.showErrorMessage("Inserisci un'email valida.");
            return;
        }
        LocalDate birthDate = boundary.getBirthDatePicker().getValue();
        LocalDate today = LocalDate.now();
        Period age = Period.between(birthDate, today);
        if (age.getYears() < 18) {
            boundary.showErrorMessage("Devi avere almeno 18 anni.");
            return;
        }

        if (loggedUser == null) {
            boundary.showErrorMessage("Utente non trovato.");
            return;
        }

        String currentPwd = boundary.getCurrentPasswordField().getText();
        String newPwd = boundary.getNewPasswordField().getText();
        String confirmPwd = boundary.getConfirmPasswordField().getText();

        boolean wantChangePwd = !currentPwd.isEmpty() || !newPwd.isEmpty() || !confirmPwd.isEmpty();

        if (wantChangePwd) {
            if (currentPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                boundary.showErrorMessage("Per cambiare password compila tutti i campi password.");
                return;
            }
            if (!loggedUser.getPassword().equals(currentPwd)) {
                boundary.showErrorMessage("La password attuale non è corretta.");
                return;
            }
            if (!newPwd.equals(confirmPwd)) {
                boundary.showErrorMessage("La nuova password e la conferma non coincidono.");
                return;
            }
            loggedUser.setPassword(newPwd);
        }

        // Aggiorna gli altri dati
        loggedUser.setNome(boundary.getNameField().getText());
        loggedUser.setCognome(boundary.getSurnameField().getText());
        loggedUser.setEmail(boundary.getEmailField().getText());
        loggedUser.setDataDiNascita(boundary.getBirthDatePicker().getValue());

        try {
            utenteDao.ModificaUtente(loggedUser);

            originalName = loggedUser.getNome();
            originalSurname = loggedUser.getCognome();
            originalEmail = loggedUser.getEmail();
            originalBirthDate = loggedUser.getDataDiNascita();

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