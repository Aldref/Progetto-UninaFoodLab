package com.progetto.boundary;

import com.progetto.controller.PaymentPageController;
import com.progetto.utils.CardValidator;
import com.progetto.utils.SceneSwitcher;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentPageBoundary implements Initializable {
    
    @FXML private TextField nomeField;
    @FXML private TextField numeroCartaField;
    @FXML private TextField scadenzaField;
    @FXML private TextField cvcField;
    @FXML private CheckBox salvaCartaCheckBox;
    @FXML private Button confermaBtn;
    @FXML private Button annullaBtn;
    @FXML private Button backBtn;
    
    @FXML private Label nomeErrorLabel;
    @FXML private Label numeroCartaErrorLabel;
    @FXML private Label scadenzaErrorLabel;
    @FXML private Label cvcErrorLabel;
    
    @FXML private Label courseTitle;
    @FXML private Label courseDetails;
    @FXML private Label coursePrice;
    @FXML private Label totalPrice;
    
    private PaymentPageController controller;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.controller = new PaymentPageController(this);
        setupFieldListeners();
        
        // Imposta i dati del corso di default
        setCourseDetails("Corso di Cucina Italiana", "Livello Avanzato • 12 lezioni", "€99.99");
    }
    
    private void setupFieldListeners() {
        // Filtro per il nome
        nomeField.textProperty().addListener((obs, oldText, newText) -> {
            // Qui puoi mettere la logica di filtro direttamente, se vuoi
            // Ad esempio: solo lettere e spazi
            if (!newText.matches("[a-zA-ZÀ-ÿ\\s]*")) {
                nomeField.setText(oldText);
            }
        });

        // Formattazione automatica numero carta
        numeroCartaField.textProperty().addListener((obs, oldText, newText) -> {
        String digits = newText.replaceAll("[^0-9]", "");
        if (digits.length() > 19) digits = digits.substring(0, 16);
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(digits.charAt(i));
        }
        // Evita loop infinito di setText
        if (!numeroCartaField.getText().equals(formatted.toString())) {
            numeroCartaField.setText(formatted.toString());
            numeroCartaField.positionCaret(formatted.length());
        }
    });

        // Formattazione automatica scadenza
        scadenzaField.textProperty().addListener((obs, oldText, newText) -> {
            String filtered = newText.replaceAll("[^0-9/]", "");
            if (filtered.length() == 2 && !oldText.endsWith("/")) {
                filtered += "/";
            }
            if (filtered.length() > 5) filtered = filtered.substring(0, 5);
            scadenzaField.setText(filtered);
        });

        // Solo numeri per CVC
        cvcField.textProperty().addListener((obs, oldText, newText) -> {
            String filtered = newText.replaceAll("[^0-9]", "");
            if (filtered.length() > 4) filtered = filtered.substring(0, 4);
            cvcField.setText(filtered);
        });
    }
    
    @FXML
    private void processPayment() {
        controller.processPayment();
    }
    
    @FXML
    private void cancelPayment() {
        goBack();
    }
    
    @FXML
    private void goBack() {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        SceneSwitcher.switchToScene(stage, "/fxml/homepageutente.fxml"); 
    }
    
    public Stage getStage() {
        return (Stage) confermaBtn.getScene().getWindow();
    }
    

    // Getter per i campi
    public String getNome() { return nomeField.getText().trim(); }
    public String getNumeroCarta() { return numeroCartaField.getText().replaceAll("\\s+", ""); }
    public String getScadenza() { return scadenzaField.getText().trim(); }
    public String getCvc() { return cvcField.getText().trim(); }
    public boolean isSalvaCarta() { return salvaCartaCheckBox.isSelected(); }
    
    // Metodi per mostrare errori
    public void showError(String field, String message) {
        switch (field.toLowerCase()) {
            case "nome":
                nomeErrorLabel.setText(message);
                nomeErrorLabel.setVisible(true);
                nomeField.setStyle(nomeField.getStyle() + "; -fx-border-color: #dc3545;");
                break;
            case "numerocarta":
                numeroCartaErrorLabel.setText(message);
                numeroCartaErrorLabel.setVisible(true);
                numeroCartaField.setStyle(numeroCartaField.getStyle() + "; -fx-border-color: #dc3545;");
                break;
            case "scadenza":
                scadenzaErrorLabel.setText(message);
                scadenzaErrorLabel.setVisible(true);
                scadenzaField.setStyle(scadenzaField.getStyle() + "; -fx-border-color: #dc3545;");
                break;
            case "cvc":
                cvcErrorLabel.setText(message);
                cvcErrorLabel.setVisible(true);
                cvcField.setStyle(cvcField.getStyle() + "; -fx-border-color: #dc3545;");
                break;
        }
    }
    
    private void clearError(Label errorLabel) {
        errorLabel.setVisible(false);
    }
    
    public void clearAllErrors() {
        clearError(nomeErrorLabel);
        clearError(numeroCartaErrorLabel);
        clearError(scadenzaErrorLabel);
        clearError(cvcErrorLabel);
        
        nomeField.setStyle("");
        numeroCartaField.setStyle("");
        scadenzaField.setStyle("");
        cvcField.setStyle("");
    }
    
    public void setCourseDetails(String title, String details, String price) {
        Platform.runLater(() -> {
            if (courseTitle != null) courseTitle.setText(title);
            if (courseDetails != null) courseDetails.setText(details);
            if (coursePrice != null) coursePrice.setText(price);
            if (totalPrice != null) totalPrice.setText(price);
        });
    }
    
    public void navigateToSuccess() {
    Stage stage = (Stage) confermaBtn.getScene().getWindow();
    SceneSwitcher.switchToScene(stage, "/fxml/homepageutente.fxml"); 
}
}