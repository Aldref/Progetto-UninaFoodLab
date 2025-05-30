package com.progetto.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML
    private CheckBox checkBoxUtente;

    @FXML
    private CheckBox checkBoxChef;

    @FXML
    private TextField textFieldDescrizione;

    @Override
public void initialize(URL location, ResourceBundle resources) {
    textFieldDescrizione.setVisible(false);

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
}
