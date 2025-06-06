package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import com.progetto.controller.RegisterController;

import java.net.URL;
import java.util.ResourceBundle;

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
    @FXML private Label labelErrore;

    private RegisterController controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = new RegisterController(
            textFieldNome, textFieldCognome, datePickerDataNascita, textFieldEmail,
            textFieldPassword, textFieldConfermaPassword, comboBoxGenere,
            radioUtente, radioChef, descrizioneSection, textFieldDescrizione, labelErrore
        );
        controller.initializeUI();
    }

    @FXML
    private void onRegistratiClick(ActionEvent event) {
        controller.onRegistratiClick(event);
    }

    @FXML
    private void onIndietroClick(ActionEvent event) {
        controller.onIndietroClick(event);
    }
}