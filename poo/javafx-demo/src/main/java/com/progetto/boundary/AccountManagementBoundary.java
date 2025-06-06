package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import com.progetto.controller.AccountManagementController;

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

    private AccountManagementController controller;

    @FXML
    public void initialize() {
        controller = new AccountManagementController(
            userNameLabel, userProfileImage, profileImageLarge, changePhotoBtn,
            nameField, surnameField, emailField, birthDatePicker,
            currentPasswordField, newPasswordField, confirmPasswordField,
            saveBtn, cancelBtn
        );
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
}