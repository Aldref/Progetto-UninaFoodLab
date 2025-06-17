package com.progetto.boundary;

import com.progetto.controller.PaymentPageController;
import com.progetto.utils.CardValidator;
import com.progetto.utils.SceneSwitcher;
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
    
    // Nuovi elementi per le carte salvate
    @FXML private VBox savedCardsSection;
    @FXML private VBox savedCardsContainer;
    @FXML private VBox newCardSection;
    @FXML private Button addNewCardBtn;
    @FXML private Button useSavedCardBtn;
    @FXML private Button backToSavedCardsBtn;
    
    private PaymentPageController controller;
    private Map<String, Object> selectedCard; // Usa Map generica invece di SavedCard
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.controller = new PaymentPageController(this);
        setupFieldListeners();
        
        // Imposta i dati del corso di default
        setCourseDetails("Corso di Cucina Italiana", "Livello Avanzato • 12 lezioni", "€99.99");
        
        // Carica le carte salvate (simulazione - da sostituire con chiamata DB)
        loadSavedCards();
    }
    
    private void setupFieldListeners() {
        // Filtro per il nome
        nomeField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("[a-zA-ZÀ-ÿ\\s]*")) {
                nomeField.setText(oldText);
            }
        });

        // Formattazione automatica numero carta
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
    
    private void loadSavedCards() {
        // TODO: Sostituire con chiamata al DB
        List<Map<String, Object>> cards = getSavedCardsFromDatabase();
        
        if (cards != null && !cards.isEmpty()) {
            populateSavedCards(cards);
            showSavedCards();
        } else {
            showNewCardForm();
        }
    }
    
    // Metodo stub per simulare il recupero dal DB
    private List<Map<String, Object>> getSavedCardsFromDatabase() {
        // TEMPORANEO: Simulazione carte per testare l'UI
        List<Map<String, Object>> cards = new ArrayList<>();
        
        Map<String, Object> card1 = new HashMap<>();
        card1.put("id", "1");
        card1.put("maskedNumber", "**** **** **** 1234");
        card1.put("holderName", "Mario Rossi");
        card1.put("expiryDate", "12/25");
        card1.put("cardType", "visa");
        card1.put("isDefault", true);
        cards.add(card1);
        
        Map<String, Object> card2 = new HashMap<>();
        card2.put("id", "2");
        card2.put("maskedNumber", "**** **** **** 5678");
        card2.put("holderName", "Mario Rossi");
        card2.put("expiryDate", "06/26");
        card2.put("cardType", "mastercard");
        card2.put("isDefault", false);
        cards.add(card2);
        
        return cards;
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
        
        // Icona tipo carta
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
        
        // Badge default se applicabile
        VBox rightSection = new VBox(5);
        if (Boolean.TRUE.equals(card.get("isDefault"))) {
            Label defaultBadge = new Label("PREDEFINITA");
            defaultBadge.getStyleClass().add("default-badge");
            rightSection.getChildren().add(defaultBadge);
        }
        
        cardItem.getChildren().addAll(cardIcon, cardInfo, rightSection);
        
        // Gestione click
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
        // Rimuovi selezione precedente
        savedCardsContainer.getChildren().forEach(node -> 
            node.getStyleClass().remove("selected"));
        
        // Seleziona carta corrente
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
        
        // Mostra il pulsante "torna alle carte salvate" solo se ci sono carte salvate
        if (savedCardsContainer.getChildren().size() > 0) {
            backToSavedCardsBtn.setVisible(true);
            backToSavedCardsBtn.setManaged(true);
        }
        
        // Reset selezione carta
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
    
    // Getter per i campi
    public String getNome() { return nomeField.getText().trim(); }
    public String getNumeroCarta() { return numeroCartaField.getText().replaceAll("\\s+", ""); }
    public String getScadenza() { return scadenzaField.getText().trim(); }
    public String getCvc() { return cvcField.getText().trim(); }
    public boolean isSalvaCarta() { return salvaCartaCheckBox.isSelected(); }
    public Map<String, Object> getSelectedCard() { return selectedCard; }
    
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
        try {
            Stage stage = (Stage) confermaBtn.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/homepageutente.fxml", "UninaFoodLab - Homepage Utente");
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: chiudi semplicemente la finestra corrente
            Stage stage = (Stage) confermaBtn.getScene().getWindow();
            stage.close();
        }
    }
    
    public void refreshSavedCards() {
        loadSavedCards();
    }
}