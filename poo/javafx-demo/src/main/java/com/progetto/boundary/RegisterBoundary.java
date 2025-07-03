package com.progetto.boundary;

import java.net.URL;
import java.util.ResourceBundle;

import com.progetto.controller.RegisterController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class RegisterBoundary implements Initializable {

    @FXML private TextField textFieldNome;
    @FXML private TextField textFieldCognome;
    @FXML private DatePicker datePickerDataNascita;
    @FXML private TextField textFieldEmail;
    @FXML private PasswordField textFieldPassword;
    @FXML private PasswordField textFieldConfermaPassword;
    @FXML private ComboBox<String> comboBoxGenere;
    @FXML private RadioButton radioUtente;
    @FXML private RadioButton radioChef;
    @FXML private VBox descrizioneSection;
    @FXML private TextArea textFieldDescrizione;
    @FXML private TextField textFieldAnniEsperienza;
    @FXML private Label labelErrore;

    private final RegisterController controller = new RegisterController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        labelErrore.setVisible(false);
        descrizioneSection.setVisible(false);
        descrizioneSection.setManaged(false);

        ToggleGroup accountTypeGroup = new ToggleGroup();
        radioUtente.setToggleGroup(accountTypeGroup);
        radioChef.setToggleGroup(accountTypeGroup);
        radioUtente.setSelected(true);

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

    @FXML
    private void onRegistratiClick(ActionEvent event) {
        // Estrai i dati dalla GUI
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
        var dataNascita = datePickerDataNascita.getValue();

    
        // Reset sempre il messaggio di errore prima di ogni tentativo
        labelErrore.setText("");
        labelErrore.setVisible(false);

        // Passa i dati al controller
        String errore = controller.validaRegistrazione(
            nome, cognome, email, password, confermaPassword, genere,
            descrizione, anniEsperienza, utenteSelezionato, chefSelezionato, dataNascita
        );

        if (errore != null) {
            labelErrore.setText(errore);
            labelErrore.setVisible(true);
            return;
        }

        String esito = controller.registraUtente(
            nome, cognome, email, password, genere, descrizione, anniEsperienza,
            utenteSelezionato, chefSelezionato, dataNascita
        );
        if (esito == null) {
            labelErrore.setText("");
            labelErrore.setVisible(false);
            showSuccessMessage("Registrazione completata con successo!");
            onIndietroClick(event);
        } else {
            labelErrore.setText(esito);
            labelErrore.setVisible(true);
        }
    }

    @FXML
    private void onIndietroClick(ActionEvent event) {
        controller.tornaAlLogin(textFieldNome.getScene().getWindow());
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}