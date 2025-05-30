package com.progetto.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML private TextField textFieldNome;
    @FXML private TextField textFieldCognome;
    @FXML private DatePicker datePickerDataNascita;
    @FXML private TextField textFieldEmail;
    @FXML private PasswordField textFieldPassword;
    @FXML private PasswordField textFieldConfermaPassword;
    @FXML private CheckBox checkBoxUtente;
    @FXML private CheckBox checkBoxChef;
    @FXML private TextField textFieldDescrizione;
    @FXML private Label labelErrore;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldDescrizione.setVisible(false);
        labelErrore.setVisible(false);

        checkBoxChef.setOnAction(e -> {
            if (checkBoxChef.isSelected()) {
                checkBoxUtente.setSelected(false);
                textFieldDescrizione.setVisible(true);
            } else {
                textFieldDescrizione.setVisible(false);
            }
        });

        checkBoxUtente.setOnAction(e -> {
            if (checkBoxUtente.isSelected()) {
                checkBoxChef.setSelected(false);
                textFieldDescrizione.setVisible(false);
            }
        });
    }

    @FXML
    private void onRegistratiClick(ActionEvent event) {
        String nome = textFieldNome.getText().trim();
        String cognome = textFieldCognome.getText().trim();
        String email = textFieldEmail.getText().trim();
        String password = textFieldPassword.getText();
        String confermaPassword = textFieldConfermaPassword.getText();
        String descrizione = textFieldDescrizione.getText().trim();
        boolean utenteSelezionato = checkBoxUtente.isSelected();
        boolean chefSelezionato = checkBoxChef.isSelected();

        boolean valid = true;
        StringBuilder messaggioErrore = new StringBuilder();

        
        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty()
                || confermaPassword.isEmpty() || datePickerDataNascita.getValue() == null) {
            messaggioErrore.append("Compila tutti i campi obbligatori con *\n");
            valid = false;
        }
        
        if (!password.equals(confermaPassword)) {
            messaggioErrore.append("Le password non coincidono\n");
            valid = false;
        }
   
        if (!valid) {
            labelErrore.setText(messaggioErrore.toString());
            labelErrore.setVisible(true);
        } else {
            labelErrore.setVisible(false);
            System.out.println("Registrazione valida. Procedo...");
            // TODO: salva dati, cambia scena, ecc.
        }
    }
}
