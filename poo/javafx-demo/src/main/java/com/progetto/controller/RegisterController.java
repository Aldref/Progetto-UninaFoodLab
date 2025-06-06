package com.progetto.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    // Form fields
    @FXML private TextField textFieldNome;
    @FXML private TextField textFieldCognome;
    @FXML private DatePicker datePickerDataNascita;
    @FXML private TextField textFieldEmail;
    @FXML private PasswordField textFieldPassword;
    @FXML private PasswordField textFieldConfermaPassword;
    @FXML private ComboBox<String> comboBoxGenere;
    
    // Account type - cambiati da CheckBox a RadioButton
    @FXML private RadioButton radioUtente;
    @FXML private RadioButton radioChef;
    @FXML private VBox descrizioneSection;
    @FXML private TextArea textFieldDescrizione;
    
    @FXML private Label labelErrore;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Nascondi errore e sezione descrizione inizialmente
        labelErrore.setVisible(false);
        descrizioneSection.setVisible(false);
        descrizioneSection.setManaged(false);

        // Crea ToggleGroup per i RadioButton
        ToggleGroup accountTypeGroup = new ToggleGroup();
        radioUtente.setToggleGroup(accountTypeGroup);
        radioChef.setToggleGroup(accountTypeGroup);
        
        // Seleziona "Utente" di default
        radioUtente.setSelected(true);
        
        // Listener per mostrare/nascondere descrizione
        accountTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == radioChef) {
                descrizioneSection.setVisible(true);
                descrizioneSection.setManaged(true);
            } else {
                descrizioneSection.setVisible(false);
                descrizioneSection.setManaged(false);
            }
        });
        
        // Popola ComboBox genere
        comboBoxGenere.getItems().addAll("Maschio", "Femmina", "Altro", "Preferisco non specificare");
    }

    @FXML
    private void onRegistratiClick(ActionEvent event) {
        String nome = textFieldNome.getText().trim();
        String cognome = textFieldCognome.getText().trim();
        String email = textFieldEmail.getText().trim();
        String password = textFieldPassword.getText();
        String confermaPassword = textFieldConfermaPassword.getText();
        String genere = comboBoxGenere.getValue();
        String descrizione = textFieldDescrizione.getText().trim();
        
        boolean utenteSelezionato = radioUtente.isSelected();
        boolean chefSelezionato = radioChef.isSelected();

        boolean valid = true;
        StringBuilder messaggioErrore = new StringBuilder();

        // Validazione campi obbligatori
        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || 
            password.isEmpty() || confermaPassword.isEmpty() || 
            datePickerDataNascita.getValue() == null || genere == null) {
            messaggioErrore.append("• Compila tutti i campi obbligatori (*)\n");
            valid = false;
        }
        
        // Validazione password
        if (!password.equals(confermaPassword)) {
            messaggioErrore.append("• Le password non coincidono\n");
            valid = false;
        }
        
        if (password.length() < 6) {
            messaggioErrore.append("• La password deve avere almeno 6 caratteri\n");
            valid = false;
        }
        
        // Validazione email (semplice)
        if (!email.contains("@") || !email.contains(".")) {
            messaggioErrore.append("• Inserisci un'email valida\n");
            valid = false;
        }
        
        // Validazione tipo account
        if (!utenteSelezionato && !chefSelezionato) {
            messaggioErrore.append("• Seleziona il tipo di account\n");
            valid = false;
        }
        
        // Validazione descrizione per Chef
        if (chefSelezionato && descrizione.isEmpty()) {
            messaggioErrore.append("• La descrizione è obbligatoria per i Chef\n");
            valid = false;
        }
        
        // Validazione età (almeno 13 anni)
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
            
            // Registrazione valida
            System.out.println("=== REGISTRAZIONE VALIDA ===");
            System.out.println("Nome: " + nome);
            System.out.println("Cognome: " + cognome);
            System.out.println("Email: " + email);
            System.out.println("Data Nascita: " + datePickerDataNascita.getValue());
            System.out.println("Genere: " + genere);
            System.out.println("Tipo: " + (chefSelezionato ? "Chef" : "Utente"));
            if (chefSelezionato) {
                System.out.println("Descrizione: " + descrizione);
            }
            
            // TODO: Salvare i dati nel database e reindirizzare alla homepage
            
            // Per ora mostra messaggio di successo
            showSuccessMessage("Registrazione completata con successo!");
            
            // Torna al login
            onIndietroClick(event);
        }
    }

    @FXML
    private void onIndietroClick(ActionEvent event) { // Aggiungi il parametro ActionEvent
        try {
            Stage stage = (Stage) textFieldNome.getScene().getWindow(); // Usa un elemento FXML esistente
            SceneSwitcher.switchScene(stage, "/fxml/loginpage.fxml", "UninaFoodLab - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Utility method per messaggi di successo
    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}