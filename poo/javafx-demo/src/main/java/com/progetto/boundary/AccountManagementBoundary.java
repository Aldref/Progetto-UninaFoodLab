package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import com.progetto.controller.AccountManagementController;
import com.progetto.utils.ImageClipUtils;
import java.io.File;

public class AccountManagementBoundary {

    @FXML private Label userNameLabel;
    @FXML private ImageView userProfileImage;
    @FXML private ImageView profileImageLarge;
    @FXML private Button changePhotoBtn;
    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField emailField;
    @FXML private DatePicker birthDatePicker;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private Button viewUserCardsBtn;

    private AccountManagementController controller;

    @FXML
    public void initialize() {
        controller = new AccountManagementController(this);
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
    private void goToEnrolledCourses(ActionEvent event) {
        controller.goToEnrolledCourses();
    }

    @FXML
    private void LogoutClick(ActionEvent event) {
        controller.LogoutClick();
    }
    
    @FXML
    private void goToUserCards(ActionEvent event) {
        controller.goToUserCards();
    }

    public Label getUserNameLabel() { return userNameLabel; }
    public ImageView getUserProfileImage() { return userProfileImage; }
    public ImageView getProfileImageLarge() { return profileImageLarge; }
    public Button getChangePhotoBtn() { return changePhotoBtn; }
    public TextField getNameField() { return nameField; }
    public TextField getSurnameField() { return surnameField; }
    public TextField getEmailField() { return emailField; }
    public DatePicker getBirthDatePicker() { return birthDatePicker; }
    public PasswordField getCurrentPasswordField() { return currentPasswordField; }
    public PasswordField getNewPasswordField() { return newPasswordField; }
    public PasswordField getConfirmPasswordField() { return confirmPasswordField; }
    public Button getSaveBtn() { return saveBtn; }
    public Button getCancelBtn() { return cancelBtn; }
    public Button getViewUserCardsBtn() { return viewUserCardsBtn; }

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
            File imgFile = new File("src/main/resources/" + propicPath);
            if (!imgFile.exists()) {
                imgFile = new File(propicPath);
            }
            if (imgFile.exists()) {
                javafx.scene.image.Image img = new javafx.scene.image.Image(imgFile.toURI().toString(), 256, 256, true, true);
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