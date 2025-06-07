package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SuccessDialogBoundary implements Initializable {
    
    @FXML private ImageView successImage;
    @FXML private Label courseAddedMessage;
    @FXML private Label courseNameLabel;
    @FXML private Button okButton;
    
    private Stage dialogStage;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Niente da fare qui, solo l'immagine
    }
    
    @FXML
    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
    
    public void setCourseName(String courseName) {
        if (courseNameLabel != null && courseName != null) {
            courseNameLabel.setText(courseName);
        }
    }
    
    public void setCustomMessage(String message) {
        if (courseAddedMessage != null && message != null) {
            courseAddedMessage.setText(message);
        }
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}