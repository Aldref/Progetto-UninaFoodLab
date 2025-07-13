package com.progetto.boundary.chef;

import java.io.File;

import com.progetto.controller.chef.AccountManagementChefController;
import com.progetto.utils.ImageClipUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class AccountManagementChefBoundary {

    @FXML private Label userNameLabel;
    @FXML private ImageView userProfileImage;
    @FXML private ImageView profileImageLarge;
    @FXML private Button changePhotoBtn;
    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField emailField;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextArea descriptionField;
    @FXML private TextField experienceYearsField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private AccountManagementChefController controller;

    @FXML
    public void initialize() {
        controller = new AccountManagementChefController(this);
        controller.initialize();
    }

    
    @FXML
    private void changePhoto(ActionEvent event) {
        controller.changePhoto();
    }

    @FXML
    private void saveChanges(ActionEvent event) {
        controller.saveChanges();
    }

    @FXML
    private void cancelChanges(ActionEvent event) {
        controller.cancelChanges();
    }

    
    @FXML
    private void goToHomepage(ActionEvent event) {
        controller.goToHomepage(); 
    }

    @FXML
    private void goToCreateCourse(ActionEvent event) {
        controller.goToCreateCourse();
    }

    @FXML
    private void goToMonthlyReport(ActionEvent event) {
        controller.goToMonthlyReport();
    }

    @FXML
    private void LogoutClick(ActionEvent event) {
        controller.LogoutClick();
    }

    public Label getUserNameLabel() { return userNameLabel; }
    public ImageView getUserProfileImage() { return userProfileImage; }
    public ImageView getProfileImageLarge() { return profileImageLarge; }
    public Button getChangePhotoBtn() { return changePhotoBtn; }
    public TextField getNameField() { return nameField; }
    public TextField getSurnameField() { return surnameField; }
    public TextField getEmailField() { return emailField; }
    public DatePicker getBirthDatePicker() { return birthDatePicker; }
    public TextArea getDescriptionField() { return descriptionField; }
    public TextField getExperienceYearsField() { return experienceYearsField; }
    public PasswordField getCurrentPasswordField() { return currentPasswordField; }
    public PasswordField getNewPasswordField() { return newPasswordField; }
    public PasswordField getConfirmPasswordField() { return confirmPasswordField; }
    public Button getSaveBtn() { return saveBtn; }
    public Button getCancelBtn() { return cancelBtn; }

    
    public void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showInfoMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void setProfileImages(String propicPath) {
        if (propicPath != null && !propicPath.isEmpty()) {
            Image img = null;
            try {
                if (propicPath.startsWith("file:")) {
                    img = new javafx.scene.image.Image(propicPath, 256, 256, true, true);
                } else if (propicPath.startsWith("http") || propicPath.startsWith("jar:")) {
                    img = new javafx.scene.image.Image(propicPath, 256, 256, true, true);
                } else {
                    File absFile = new File(propicPath);
                    if (absFile.exists()) {
                        img = new javafx.scene.image.Image(absFile.toURI().toString(), 256, 256, true, true);
                    } else {
                        File imgFile = new File("src/main/resources/" + propicPath);
                        if (imgFile.exists()) {
                            img = new javafx.scene.image.Image(imgFile.toURI().toString(), 256, 256, true, true);
                        }
                    }
                }
            } catch (Exception e) {
                img = null;
            }
            if (img != null) {
                if (userProfileImage != null) {
                    userProfileImage.setImage(img);
                    ImageClipUtils.setCircularClip(userProfileImage, 40);
                }
                if (profileImageLarge != null) {
                    profileImageLarge.setImage(img);
                    ImageClipUtils.setCircularClip(profileImageLarge, 60);
                }
            }
        }
    }
}