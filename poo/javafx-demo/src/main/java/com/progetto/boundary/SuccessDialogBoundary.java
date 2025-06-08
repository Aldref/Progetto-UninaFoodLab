package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class SuccessDialogBoundary {
    
    @FXML private Label successTitle;
    @FXML private Label successMessage;
    @FXML private Label courseAddedMessage;
    @FXML private Label courseNameLabel;
    @FXML private VBox courseInfoBox;
    @FXML private Button okButton;
    
    private Stage dialogStage;
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setTitle(String title) {
        if (successTitle != null) {
            successTitle.setText(title);
            successTitle.setWrapText(true);
        }
    }
    
    public void setMainMessage(String message) {
        if (successMessage != null) {
            successMessage.setText(message);
            successMessage.setWrapText(true);
        }
    }
    
    public void setSubMessage(String message) {
        if (courseAddedMessage != null) {
            courseAddedMessage.setText(message);
            courseAddedMessage.setWrapText(true);
        }
    }
    
    public void setCourseName(String courseName) {
        if (courseNameLabel != null) {
            courseNameLabel.setText(courseName);
            courseNameLabel.setWrapText(true);
            courseNameLabel.setTextAlignment(TextAlignment.CENTER);
            
            if (courseInfoBox != null) {
                courseInfoBox.setAlignment(Pos.CENTER);
            }
        }
    }
    
    public void hideCourseInfo() {
        if (courseInfoBox != null) {
            courseInfoBox.setVisible(false);
            courseInfoBox.setManaged(false);
        }
    }
    
    @FXML
    private void initialize() {
        if (successTitle != null) {
            successTitle.setWrapText(true);
            successTitle.setTextAlignment(TextAlignment.CENTER);
        }
        if (successMessage != null) {
            successMessage.setWrapText(true);
            successMessage.setTextAlignment(TextAlignment.CENTER);
        }
        if (courseAddedMessage != null) {
            courseAddedMessage.setWrapText(true);
            courseAddedMessage.setTextAlignment(TextAlignment.CENTER);
        }
        if (courseNameLabel != null) {
            courseNameLabel.setWrapText(true);
            courseNameLabel.setTextAlignment(TextAlignment.CENTER);
        }
        if (courseInfoBox != null) {
            courseInfoBox.setAlignment(Pos.CENTER);
        }
    }
    
    @FXML
    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}