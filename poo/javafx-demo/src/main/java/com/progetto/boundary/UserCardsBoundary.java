package com.progetto.boundary;

import com.progetto.controller.UserCardsController;
import com.progetto.utils.CardValidator;
import com.progetto.utils.SceneSwitcher;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserCardsBoundary {

    @FXML
    private Button backBtn;
    @FXML
    private Button addCardBtn;
    @FXML
    private ListView<String> cardsListView; // Ogni stringa rappresenta una carta formattata
    @FXML
    private VBox noCardsMessage;

    private UserCardsController controller;
    private ObservableList<String> cards = FXCollections.observableArrayList();

    @FXML private TextField cardHolderField;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private TextField cvvField;

    @FXML
    private void initialize() {
        controller = new UserCardsController(this);
        cardsListView.setItems(cards);
        updateCardsView();
        setupFieldListeners();
    }

    public void setCards(java.util.List<String> cardList) {
        cards.setAll(cardList);
        updateCardsView();
    }

    private void updateCardsView() {
        boolean hasCards = !cards.isEmpty();
        cardsListView.setVisible(hasCards);
        noCardsMessage.setVisible(!hasCards);
    }

    private void setupFieldListeners() {
        cardHolderField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("[a-zA-ZÀ-ÿ\\s]*")) {
                cardHolderField.setText(oldText);
            }
        });

        cardNumberField.textProperty().addListener((obs, oldText, newText) -> {
            String digits = newText.replaceAll("[^0-9]", "");
            if (digits.length() > 16) digits = digits.substring(0, 16);
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i > 0 && i % 4 == 0) formatted.append(" ");
                formatted.append(digits.charAt(i));
            }
            if (!cardNumberField.getText().equals(formatted.toString())) {
                cardNumberField.setText(formatted.toString());
                cardNumberField.positionCaret(formatted.length());
            }
            // Validazione tipo carta
            if (digits.length() >= 4 && !CardValidator.isValidCardType(digits)) {
                showFieldError("cardNumber", "Tipo carta non supportato");
            } else {
                clearAllErrors();
            }
        });

        cvvField.textProperty().addListener((obs, oldText, newText) -> {
            String filtered = newText.replaceAll("[^0-9]", "");
            if (filtered.length() > 4) filtered = filtered.substring(0, 4);
            cvvField.setText(filtered);
        });

        expiryField.textProperty().addListener((obs, oldText, newText) -> {
            String filtered = newText.replaceAll("[^0-9]", "");
            if (filtered.length() > 4) filtered = filtered.substring(0, 4);
            StringBuilder formatted = new StringBuilder(filtered);
            if (filtered.length() > 2) {
                formatted.insert(2, "/");
            }
            if (!expiryField.getText().equals(formatted.toString())) {
                expiryField.setText(formatted.toString());
                expiryField.positionCaret(formatted.length());
            }
            
            if (formatted.length() == 5 && !CardValidator.isValidExpiryDate(formatted.toString())) {
                showFieldError("expiry", "Data di scadenza non valida");
            } else {
                clearAllErrors();
            }
        });
    }

    @FXML
    public void goBack() {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        controller.goBack(stage);
    }

    @FXML
    private void showAddCardDialog() {
        // ...come già presente...
    }

    @FXML
    private void deleteSelectedCard() {
        String selected = cardsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Qui chiamerai il controller per eliminare la carta dal DB
            controller.deleteCard(selected); // Passa l'id o info della carta
        }
    }

    @FXML
    private void saveCard() {
        // Chiama il controller per salvare la carta
        controller.saveNewCard();
    }

    @FXML
    private void clearFields() {
        // Svuota i campi della form
        cardHolderField.setText("");
        cardNumberField.setText("");
        expiryField.setText("");
        cvvField.setText("");
        clearAllErrors();
    }

    // Utility methods (come già presenti)
    public void clearAllErrors() {}
    public void showSuccessMessage(String msg) {}
    public void clearFieldsFromController() {}
    public void showFieldError(String field, String msg) {}

    // Getter per i dati della carta (da implementare secondo la tua UI)
    public String getCardHolderName() { return ""; }
    public String getCardNumber() { return ""; }
    public String getExpiry() { return ""; }
    public String getCvv() { return ""; }
}