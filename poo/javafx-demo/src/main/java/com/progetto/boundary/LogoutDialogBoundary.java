package com.progetto.boundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class LogoutDialogBoundary {

    private boolean confirmed = false;

    @FXML
    private void handleCancel(ActionEvent event) {
        confirmed = false;
        closeDialog(event);
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
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