package com.progetto.controller;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

import com.progetto.utils.SceneSwitcher;

public class RegisterController {

    private TextField textFieldNome;
    private TextField textFieldCognome;
    private DatePicker datePickerDataNascita;
    private TextField textFieldEmail;
    private PasswordField textFieldPassword;
    private PasswordField textFieldConfermaPassword;
    private ComboBox<String> comboBoxGenere;
    private RadioButton radioUtente;
    private RadioButton radioChef;
    private VBox descrizioneSection;
    private TextArea textFieldDescrizione;
    private TextField textFieldAnniEsperienza;
    private Label labelErrore;

    public RegisterController(
        TextField textFieldNome, TextField textFieldCognome, DatePicker datePickerDataNascita,
        TextField textFieldEmail, PasswordField textFieldPassword, PasswordField textFieldConfermaPassword,
        ComboBox<String> comboBoxGenere, RadioButton radioUtente, RadioButton radioChef,
        VBox descrizioneSection, TextArea textFieldDescrizione, TextField textFieldAnniEsperienza,
        Label labelErrore
    ) {
        this.textFieldNome = textFieldNome;
        this.textFieldCognome = textFieldCognome;
        this.datePickerDataNascita = datePickerDataNascita;
        this.textFieldEmail = textFieldEmail;
        this.textFieldPassword = textFieldPassword;
        this.textFieldConfermaPassword = textFieldConfermaPassword;
        this.comboBoxGenere = comboBoxGenere;
        this.radioUtente = radioUtente;
        this.radioChef = radioChef;
        this.descrizioneSection = descrizioneSection;
        this.textFieldDescrizione = textFieldDescrizione;
        this.textFieldAnniEsperienza = textFieldAnniEsperienza;
        this.labelErrore = labelErrore;
    }

    public void initializeUI() {
        labelErrore.setVisible(false);
        descrizioneSection.setVisible(false);
        descrizioneSection.setManaged(false);

        ToggleGroup accountTypeGroup = new ToggleGroup();
        radioUtente.setToggleGroup(accountTypeGroup);
        radioChef.setToggleGroup(accountTypeGroup);

        radioUtente.setSelected(true);

        // Listener per rendere il campo anni esperienza solo numerico
        textFieldAnniEsperienza.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textFieldAnniEsperienza.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        accountTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == radioChef) {
                descrizioneSection.setVisible(true);
                descrizioneSection.setManaged(true);
            } else {
                descrizioneSection.setVisible(false);
                descrizioneSection.setManaged(false);
            }
        });

        comboBoxGenere.getItems().addAll("Maschio", "Femmina", "Altro", "Preferisco non specificare");
    }

    public void onRegistratiClick(ActionEvent event) {
        String nome = textFieldNome.getText().trim();
        String cognome = textFieldCognome.getText().trim();
        String email = textFieldEmail.getText().trim();
        String password = textFieldPassword.getText();
        String confermaPassword = textFieldConfermaPassword.getText();
        String genere = comboBoxGenere.getValue();
        String descrizione = textFieldDescrizione.getText().trim();
        String anniEsperienza = textFieldAnniEsperienza.getText().trim();

        boolean utenteSelezionato = radioUtente.isSelected();
        boolean chefSelezionato = radioChef.isSelected();

        boolean valid = true;
        StringBuilder messaggioErrore = new StringBuilder();

        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() ||
            password.isEmpty() || confermaPassword.isEmpty() ||
            datePickerDataNascita.getValue() == null || genere == null) {
            messaggioErrore.append("• Compila tutti i campi obbligatori (*)\n");
            valid = false;
        }

        if (!password.equals(confermaPassword)) {
            messaggioErrore.append("• Le password non coincidono\n");
            valid = false;
        }

        if (password.length() < 6) {
            messaggioErrore.append("• La password deve avere almeno 6 caratteri\n");
            valid = false;
        }

        if (!email.contains("@") || !email.contains(".")) {
            messaggioErrore.append("• Inserisci un'email valida\n");
            valid = false;
        }

        if (!utenteSelezionato && !chefSelezionato) {
            messaggioErrore.append("• Seleziona il tipo di account\n");
            valid = false;
        }

        if (chefSelezionato && descrizione.isEmpty()) {
            messaggioErrore.append("• La descrizione è obbligatoria per i Chef\n");
            valid = false;
        }

        if (chefSelezionato && anniEsperienza.isEmpty()) {
            messaggioErrore.append("• Gli anni di esperienza sono obbligatori per i Chef\n");
            valid = false;
        }

        if (chefSelezionato && !anniEsperienza.isEmpty()) {
            try {
                int anni = Integer.parseInt(anniEsperienza);
                if (anni < 0) {
                    messaggioErrore.append("• Gli anni di esperienza devono essere un numero positivo\n");
                    valid = false;
                }
                if (anni > 50) {
                    messaggioErrore.append("• Gli anni di esperienza non possono superare 50\n");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                messaggioErrore.append("• Gli anni di esperienza devono essere un numero valido\n");
                valid = false;
            }
        }

        if (datePickerDataNascita.getValue() != null &&
            datePickerDataNascita.getValue().isAfter(java.time.LocalDate.now().minusYears(13))) {
            messaggioErrore.append("• Devi avere almeno 13 anni per registrarti\n");
            valid = false;
        }

        if (!valid) {
            labelErrore.setText(messaggioErrore.toString());
            labelErrore.setVisible(true);
        } else {
            labelErrore.setVisible(false);

            System.out.println("=== REGISTRAZIONE VALIDA ===");
            System.out.println("Nome: " + nome);
            System.out.println("Cognome: " + cognome);
            System.out.println("Email: " + email);
            System.out.println("Data Nascita: " + datePickerDataNascita.getValue());
            System.out.println("Genere: " + genere);
            System.out.println("Tipo: " + (chefSelezionato ? "Chef" : "Utente"));
            if (chefSelezionato) {
                System.out.println("Descrizione: " + descrizione);
                System.out.println("Anni di esperienza: " + anniEsperienza);
            }

            showSuccessMessage("Registrazione completata con successo!");
            onIndietroClick(event);
        }
    }

    
    public void onIndietroClick(ActionEvent event) {
        try {
            Stage stage = (Stage) textFieldNome.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/loginpage.fxml", "UninaFoodLab - Login");
        } catch (IOException e) {
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
}