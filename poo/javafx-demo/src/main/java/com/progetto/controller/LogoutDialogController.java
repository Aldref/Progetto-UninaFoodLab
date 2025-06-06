package com.progetto.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class LogoutDialogController {

    private boolean confirmed = false;

    public void handleCancel(ActionEvent event) {
        confirmed = false;
        closeDialog(event);
    }

    public void handleConfirm(ActionEvent event) {
        confirmed = true;
        closeDialog(event);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private void closeDialog(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}