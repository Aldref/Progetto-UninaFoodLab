package com.progetto.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

import com.progetto.Entity.EntityDto.Chef;
import com.progetto.Entity.entityDao.ChefDao;
import com.progetto.boundary.AccountManagementChefBoundary;
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;
import com.progetto.utils.SuccessDialogUtils;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AccountManagementChefController {

    // Chef loggato e DAO dichiarati come attributi di classe
    private static Chef loggedChef = Chef.loggedUser;
    private static ChefDao chefDao = new ChefDao();

    private AccountManagementChefBoundary boundary;
    private String originalName;
    private String originalSurname;
    private String originalEmail;
    private LocalDate originalBirthDate;
    private String originalDescription;
    private String originalExperienceYears;
    private String originalProfilePic;

    private File tempSelectedPhoto = null;
    private String tempAbsolutePhotoPath = null;

    public AccountManagementChefController(AccountManagementChefBoundary boundary) {
        this.boundary = boundary;
    }

    public void initialize() {
        setupExperienceYearsValidation();
        loadChefData();
    }

    private void setupExperienceYearsValidation() {
        // Listener per rendere il campo anni esperienza solo numerico
        boundary.getExperienceYearsField().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                boundary.getExperienceYearsField().setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void loadChefData() {
        // Carica i dati reali dello chef loggato dal DB
        if (loggedChef != null) {
            chefDao.recuperaDatiUtente(loggedChef);
            originalName = loggedChef.getNome();
            originalSurname = loggedChef.getCognome();
            originalEmail = loggedChef.getEmail();
            originalBirthDate = loggedChef.getDataDiNascita();
            originalDescription = loggedChef.getDescrizione();
            originalExperienceYears = Integer.toString(loggedChef.getAnniDiEsperienza());
            originalProfilePic = loggedChef.getUrl_Propic();

            boundary.getNameField().setText(originalName);
            boundary.getSurnameField().setText(originalSurname);
            boundary.getEmailField().setText(originalEmail);
            boundary.getBirthDatePicker().setValue(originalBirthDate);
            boundary.getDescriptionField().setText(originalDescription);
            boundary.getExperienceYearsField().setText(originalExperienceYears);
            boundary.getUserNameLabel().setText(originalName + " " + originalSurname);

            // Carica la foto profilo se presente
            String propic = loggedChef.getUrl_Propic();
            if (propic != null && !propic.isEmpty()) {
                // Prova a caricare come risorsa dal classpath (funziona anche in JAR)
                String resourcePath = "/" + propic.replace("\\", "/");
                java.net.URL imgUrl = getClass().getResource(resourcePath);
                if (imgUrl != null) {
                    boundary.setProfileImages(imgUrl.toExternalForm());
                } else {
                    // fallback: prova a caricare da file system in sviluppo
                    File imgFile = new File("src/main/resources/" + propic.replace("/", java.io.File.separator));
                    if (imgFile.exists()) {
                        boundary.setProfileImages(imgFile.getAbsolutePath());
                    } else {
                        boundary.setProfileImages(null); // o un'immagine di default
                    }
                }
            } else {
                boundary.setProfileImages(null); // o un'immagine di default
            }
        }
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
            String ext = selectedFile.getName().toLowerCase();
            if (!(ext.endsWith(".png") || ext.endsWith(".jpg") || ext.endsWith(".jpeg") || ext.endsWith(".gif"))) {
                boundary.showErrorMessage("Formato immagine non supportato. Usa PNG, JPG, JPEG o GIF.");
                return;
            }

            if (loggedChef == null) {
                return;
            }
            String nome = loggedChef.getNome() != null ? loggedChef.getNome().replaceAll("[^a-zA-Z0-9]", "_") : "chef";
            String cognome = loggedChef.getCognome() != null ? loggedChef.getCognome().replaceAll("[^a-zA-Z0-9]", "_") : "profilo";
            String idChef = "";
            try {
                idChef = String.valueOf(loggedChef.getId_Chef());
            } catch (Exception e) {
                idChef = "id";
            }
            String extension = ext.substring(ext.lastIndexOf('.'));
            String fileName = nome + "_" + cognome + "_" + idChef + extension;
            // Path relativo da salvare nel DB e per resources
            String relativePath = "immagini/PropicChef/" + fileName;
            // Path assoluto per copia in resources (dev)
            String resourcesDir = "src/main/resources/immagini/PropicChef/";
            new File(resourcesDir).mkdirs();
            String absolutePath = resourcesDir + fileName;
            tempSelectedPhoto = null;
            tempAbsolutePhotoPath = null;

            // Copia subito la foto nella destinazione finale
            try {
                Files.copy(selectedFile.toPath(), new File(absolutePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                boundary.showErrorMessage("Errore nel salvataggio della foto profilo: " + e.getMessage());
                return;
            }

            // Mostra subito l'anteprima come sarà salvata (usando il path relativo)
            boundary.setProfileImages(relativePath);

            // Imposta il path relativo su loggedChef (così viene salvato nel DB al salvataggio)
            loggedChef.setUrl_Propic(relativePath);
            try {
                chefDao.ModificaUtente(loggedChef);
            } catch (Exception e) {
                boundary.showErrorMessage("Errore nel salvataggio della foto profilo nel database: " + e.getMessage());
                return;
            }
        }
    }

    public void saveChanges() {
        if (loggedChef == null) {
            boundary.showErrorMessage("Chef non trovato.");
            return;
        }

        // Prendi i valori attuali
        String name = boundary.getNameField().getText();
        String surname = boundary.getSurnameField().getText();
        String email = boundary.getEmailField().getText();
        LocalDate birthDateValue = boundary.getBirthDatePicker().getValue();
        String description = boundary.getDescriptionField().getText();
        String experienceYears = boundary.getExperienceYearsField().getText();

        // Validazione solo sui campi modificati e solo se non sono vuoti
        boolean changed = false;
        if (name != null && !name.equals(originalName) && !name.trim().isEmpty()) {
            loggedChef.setNome(name);
            changed = true;
        }
        if (surname != null && !surname.equals(originalSurname) && !surname.trim().isEmpty()) {
            loggedChef.setCognome(surname);
            changed = true;
        }
        if (email != null && !email.equals(originalEmail) && !email.trim().isEmpty()) {
            if (!email.contains("@")) {
                boundary.showErrorMessage("Inserisci un'email valida.");
                return;
            }
            loggedChef.setEmail(email);
            changed = true;
        }
        if (birthDateValue != null && !birthDateValue.equals(originalBirthDate)) {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.Period age = java.time.Period.between(birthDateValue, today);
            if (age.getYears() < 18) {
                boundary.showErrorMessage("Devi avere almeno 18 anni.");
                return;
            }
            loggedChef.setDataDiNascita(birthDateValue);
            changed = true;
        }
        if (description != null && !description.equals(originalDescription) && !description.trim().isEmpty()) {
            loggedChef.setDescrizione(description);
            changed = true;
        }
        if (experienceYears != null && !experienceYears.equals(originalExperienceYears) && !experienceYears.trim().isEmpty()) {
            String expTrim = experienceYears.trim();
            try {
                int years = Integer.parseInt(expTrim);
                if (years < 0) {
                    boundary.showErrorMessage("Gli anni di esperienza devono essere un numero positivo.");
                    return;
                }
                if (years > 50) {
                    boundary.showErrorMessage("Gli anni di esperienza non possono superare 50.");
                    return;
                }
                loggedChef.setAnniDiEsperienza(years);
                changed = true;
            } catch (NumberFormatException e) {
                boundary.showErrorMessage("Gli anni di esperienza devono essere un numero valido.");
                return;
            }
        }
        // Se la foto profilo è cambiata rispetto all'originale, considera come modifica
        String currentProfilePic = loggedChef.getUrl_Propic();
        if ((originalProfilePic == null && currentProfilePic != null) || (originalProfilePic != null && !originalProfilePic.equals(currentProfilePic))) {
            changed = true;
        }

        // Gestione password
        String currentPwd = boundary.getCurrentPasswordField().getText();
        String newPwd = boundary.getNewPasswordField().getText();
        String confirmPwd = boundary.getConfirmPasswordField().getText();
        boolean wantChangePwd = !currentPwd.isEmpty() || !newPwd.isEmpty() || !confirmPwd.isEmpty();
        if (wantChangePwd) {
            if (currentPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                boundary.showErrorMessage("Per cambiare password compila tutti i campi password.");
                return;
            }
            if (!loggedChef.getPassword().equals(currentPwd)) {
                boundary.showErrorMessage("La password attuale non è corretta.");
                return;
            }
            if (!newPwd.equals(confirmPwd)) {
                boundary.showErrorMessage("La nuova password e la conferma non coincidono.");
                return;
            }
            loggedChef.setPassword(newPwd);
            changed = true;
        }

        // Se è stata selezionata una nuova foto, copia il file in resources/immagini/PropicChef/ e salva il path relativo
        if (tempSelectedPhoto != null && tempAbsolutePhotoPath != null) {
            File destFile = new File(tempAbsolutePhotoPath);
            try {
                Files.copy(tempSelectedPhoto.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                // Salva solo il path relativo per il DB
                String fileName = destFile.getName();
                String relativePath = "immagini/PropicChef/" + fileName;
                loggedChef.setUrl_Propic(relativePath);
                changed = true;
            } catch (Exception e) {
                boundary.showErrorMessage("Errore nel salvataggio della foto profilo: " + e.getMessage());
                return;
            }
        }

        if (!changed) {
            boundary.showInfoMessage("Nessuna modifica da salvare.");
            return;
        }

        try {
            chefDao.ModificaUtente(loggedChef);

            originalName = loggedChef.getNome();
            originalSurname = loggedChef.getCognome();
            originalEmail = loggedChef.getEmail();
            originalBirthDate = loggedChef.getDataDiNascita();
            originalDescription = loggedChef.getDescrizione();
            originalExperienceYears = Integer.toString(loggedChef.getAnniDiEsperienza());

            // Reset variabili temporanee dopo il salvataggio
            tempSelectedPhoto = null;
            tempAbsolutePhotoPath = null;

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
        boundary.getDescriptionField().setText(originalDescription);
        boundary.getExperienceYearsField().setText(originalExperienceYears);

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