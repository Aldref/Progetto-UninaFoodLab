package com.progetto.boundary.utente;


import com.progetto.utils.CardValidator;
import com.progetto.utils.SceneSwitcher;
import com.progetto.Entity.EntityDto.Corso;
import com.progetto.controller.utente.PaymentPageController;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import com.progetto.Entity.EntityDto.CartaDiCredito;
import java.util.ResourceBundle;
import java.time.LocalDate;

public class PaymentPageBoundary implements Initializable {
    private Corso selectedCorso;
    
    @FXML private TextField nomeField, numeroCartaField, scadenzaField, cvcField;
    @FXML private CheckBox salvaCartaCheckBox;
    @FXML private Button confermaBtn, annullaBtn, backBtn, addNewCardBtn, useSavedCardBtn, backToSavedCardsBtn;
    @FXML private Label nomeErrorLabel, numeroCartaErrorLabel, scadenzaErrorLabel, cvcErrorLabel;
    @FXML private Label courseTitle, courseDetails, coursePrice, totalPrice;
    @FXML private VBox savedCardsSection, savedCardsContainer, newCardSection;
    
    private PaymentPageController controller;
    private Map<String, Object> selectedCard; 
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.controller = new PaymentPageController(this);
        setupFieldListeners();
        setCourseDetails("Corso di Cucina Italiana", "Livello Avanzato • 12 lezioni", "€99.99");
        controller.loadSavedCardsForUser();
    }
    
    private void setupFieldListeners() {
        nomeField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("[a-zA-ZÀ-ÿ\\s]*")) {
                nomeField.setText(oldText);
            }
        });

        numeroCartaField.textProperty().addListener((obs, oldText, newText) -> {
            String digits = newText.replaceAll("[^0-9]", "");
            if (digits.length() > 16) digits = digits.substring(0, 16);
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i > 0 && i % 4 == 0) {
                    formatted.append(" ");
                }
                formatted.append(digits.charAt(i));
            }
            if (!numeroCartaField.getText().equals(formatted.toString())) {
                numeroCartaField.setText(formatted.toString());
                numeroCartaField.positionCaret(formatted.length());
            }
        });

        scadenzaField.textProperty().addListener((obs, oldText, newText) -> {
            String filtered = newText.replaceAll("[^0-9/]", "");
            if (filtered.length() == 2 && !oldText.endsWith("/")) {
                filtered += "/";
            }
            if (filtered.length() > 5) filtered = filtered.substring(0, 5);
            scadenzaField.setText(filtered);
        });

        cvcField.textProperty().addListener((obs, oldText, newText) -> {
            String filtered = newText.replaceAll("[^0-9]", "");
            if (filtered.length() > 4) filtered = filtered.substring(0, 4);
            cvcField.setText(filtered);
        });
    }
    
    private void populateSavedCards(List<Map<String, Object>> cards) {
        savedCardsContainer.getChildren().clear();
        
        for (Map<String, Object> card : cards) {
            HBox cardItem = createSavedCardItem(card);
            savedCardsContainer.getChildren().add(cardItem);
        }
    }
    
    private HBox createSavedCardItem(Map<String, Object> card) {
        HBox cardItem = new HBox(15);
        cardItem.getStyleClass().add("saved-card-item");
        cardItem.setPadding(new Insets(15));
        
        Label cardIcon = new Label(getCardTypeIcon((String) card.get("cardType")));
        cardIcon.getStyleClass().add("card-type-icon");
        
        // Informazioni carta
        VBox cardInfo = new VBox(5);
        cardInfo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cardInfo, javafx.scene.layout.Priority.ALWAYS);
        
        Label cardNumber = new Label((String) card.get("maskedNumber"));
        cardNumber.getStyleClass().add("card-number");
        
        Label cardHolder = new Label((String) card.get("holderName"));
        cardHolder.getStyleClass().add("card-holder");
        
        Label cardExpiry = new Label("Scade: " + card.get("expiryDate"));
        cardExpiry.getStyleClass().add("card-expiry");
        
        cardInfo.getChildren().addAll(cardNumber, cardHolder, cardExpiry);
        
        VBox rightSection = new VBox(5);
        if (Boolean.TRUE.equals(card.get("isDefault"))) {
            Label defaultBadge = new Label("PREDEFINITA");
            defaultBadge.getStyleClass().add("default-badge");
            rightSection.getChildren().add(defaultBadge);
        }
        
        cardItem.getChildren().addAll(cardIcon, cardInfo, rightSection);
        
        cardItem.setOnMouseClicked(e -> selectCard(cardItem, card));
        
        return cardItem;
    }
    
    private String getCardTypeIcon(String cardType) {
        if (cardType == null) return "💳";
        switch (cardType.toLowerCase()) {
            case "visa": return "💳";
            case "mastercard": return "💰";
            default: return "💳";
        }
    }
    
    private void selectCard(HBox cardItem, Map<String, Object> card) {
        savedCardsContainer.getChildren().forEach(node -> 
            node.getStyleClass().remove("selected"));
        
        cardItem.getStyleClass().add("selected");
        selectedCard = card;
        useSavedCardBtn.setDisable(false);
    }
    
    @FXML
    private void showSavedCards() {
        savedCardsSection.setVisible(true);
        savedCardsSection.setManaged(true);
        newCardSection.setVisible(false);
        newCardSection.setManaged(false);
        backToSavedCardsBtn.setVisible(false);
        backToSavedCardsBtn.setManaged(false);
    }
    
    @FXML
    private void showNewCardForm() {
        savedCardsSection.setVisible(false);
        savedCardsSection.setManaged(false);
        newCardSection.setVisible(true);
        newCardSection.setManaged(true);
        
        if (savedCardsContainer.getChildren().size() > 0) {
            backToSavedCardsBtn.setVisible(true);
            backToSavedCardsBtn.setManaged(true);
        }
        
        selectedCard = null;
        useSavedCardBtn.setDisable(true);
    }
    
    @FXML
    private void useSavedCard() {
        if (selectedCard != null) {
            String cardId = (String) selectedCard.get("id");
            controller.processPaymentWithSavedCard(cardId);
        }
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
    
    public String getNome() { return nomeField.getText().trim(); }
    public String getNumeroCarta() { return numeroCartaField.getText().replaceAll("\\s+", ""); }
    public String getScadenza() { return scadenzaField.getText().trim(); }
    public String getCvc() { return cvcField.getText().trim(); }
    public boolean isSalvaCarta() { return salvaCartaCheckBox.isSelected(); }
    public Map<String, Object> getSelectedCard() { return selectedCard; }
    
    public void showError(String field, String message) {
        switch (field.toLowerCase()) {
            case "nome": setError(nomeErrorLabel, nomeField, message); break;
            case "numerocarta": setError(numeroCartaErrorLabel, numeroCartaField, message); break;
            case "scadenza": setError(scadenzaErrorLabel, scadenzaField, message); break;
            case "cvc": setError(cvcErrorLabel, cvcField, message); break;
        }
    }

    private void setError(Label label, TextField field, String message) {
        label.setText(message);
        label.setVisible(true);
        field.setStyle(field.getStyle() + "; -fx-border-color: #dc3545;");
    }
    
    private void clearError(Label errorLabel) {
        errorLabel.setVisible(false);
    }
    
    public void clearAllErrors() {
        clearError(nomeErrorLabel); clearError(numeroCartaErrorLabel);
        clearError(scadenzaErrorLabel); clearError(cvcErrorLabel);
        nomeField.setStyle(""); numeroCartaField.setStyle("");
        scadenzaField.setStyle(""); cvcField.setStyle("");
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
        try {
            Stage stage = (Stage) confermaBtn.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/homepageutente.fxml", "UninaFoodLab - Homepage Utente");
        } catch (Exception e) {
            e.printStackTrace();
            Stage stage = (Stage) confermaBtn.getScene().getWindow();
            stage.close();
        }
    }
    
    public void refreshSavedCards() {
        controller.loadSavedCardsForUser();
    }

    public void setSavedCards(List<CartaDiCredito> carte) {
        savedCardsContainer.getChildren().clear();
        if (carte != null && !carte.isEmpty()) {
            for (CartaDiCredito carta : carte) {
                HashMap<String, Object> cardMap = new HashMap<>();
                cardMap.put("id", carta.getIdCarta());
                cardMap.put("maskedNumber", "**** **** **** " + carta.getUltimeQuattroCifre());
                cardMap.put("holderName", carta.getIntestatario());
                cardMap.put("expiryDate", carta.getDataScadenza() != null ? String.format("%02d/%02d", carta.getDataScadenza().getMonthValue(), carta.getDataScadenza().getYear() % 100) : "");
                cardMap.put("cardType", carta.getCircuito());
                cardMap.put("isDefault", false); 
                HBox cardItem = createSavedCardItem(cardMap);
                savedCardsContainer.getChildren().add(cardItem);
            }
            showSavedCards();
        } else {
            showNewCardForm();
        }
    }

    public void setSelectedCorso(Corso corso) {
        this.selectedCorso = corso;
        if (corso != null) {
            setCourseDetails(corso.getNome(), corso.getDescrizione(), String.format("€%.2f", corso.getPrezzo()));
        }
    }

    public Corso getSelectedCorso() {
        return selectedCorso;
    }

    public CartaDiCredito getCartaInserita() {
        CartaDiCredito carta = new CartaDiCredito();
        carta.setIntestatario(getNome());
        String scad = getScadenza();
        try {
            if (scad != null && scad.matches("\\d{2}/\\d{2}")) {
                int month = Integer.parseInt(scad.substring(0, 2));
                int year = 2000 + Integer.parseInt(scad.substring(3, 5));
                LocalDate data = LocalDate.of(year, month, 1);
                carta.setDataScadenza(data);
            }
        } catch (Exception e) {
            carta.setDataScadenza(null);
        }
        String numero = getNumeroCarta();
        if (numero.length() >= 4) {
            carta.setUltimeQuattroCifre(numero.substring(numero.length() - 4));
        } else {
            carta.setUltimeQuattroCifre(numero);
        }
        carta.setCircuito(CardValidator.getCardType(numero));
        return carta;
    }
}