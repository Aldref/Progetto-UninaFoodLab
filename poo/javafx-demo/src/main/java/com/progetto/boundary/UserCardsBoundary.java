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
import com.progetto.Entity.EntityDto.CartaDiCredito;

public class UserCardsBoundary {

    @FXML
    private Button backBtn;
    @FXML
    private Button addCardBtn;

    @FXML
    private ListView<CartaDiCredito> cardsListView;
    @FXML
    private VBox noCardsMessage;

    private UserCardsController controller;
    private ObservableList<CartaDiCredito> cards = FXCollections.observableArrayList();

    @FXML private TextField cardHolderField;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private TextField cvvField;

    @FXML private Label cardHolderErrorLabel;
    @FXML private Label cardNumberErrorLabel;
    @FXML private Label expiryErrorLabel;
    @FXML private Label cvvErrorLabel;

    // Flags per evitare ricorsione nei listener
    private boolean updatingCardNumber = false;
    private boolean updatingExpiry = false;
    private boolean updatingCvv = false;

    @FXML
    private void initialize() {
        controller = new UserCardsController(this);
        controller.loadCardsFromDb(); // Carica sempre le carte dal DB all'apertura della pagina
        cardsListView.setItems(cards);
        updateCardsView();
        setupFieldListeners();
    }


    public void setCards(java.util.List<CartaDiCredito> cardList) {
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
            if (updatingCardNumber) return;
            updatingCardNumber = true;
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
                showFieldError("cardNumber", "Tipo di carta non supportato. Accettiamo solo Visa e Mastercard");
            } else {
                clearAllErrors();
            }
            updatingCardNumber = false;
        });

        cvvField.textProperty().addListener((obs, oldText, newText) -> {
            if (updatingCvv) return;
            updatingCvv = true;
            String filtered = newText.replaceAll("[^0-9]", "");
            if (filtered.length() > 4) filtered = filtered.substring(0, 4);
            if (!cvvField.getText().equals(filtered)) {
                cvvField.setText(filtered);
                cvvField.positionCaret(filtered.length());
            }
            updatingCvv = false;
        });

        expiryField.textProperty().addListener((obs, oldText, newText) -> {
            if (updatingExpiry) return;
            updatingExpiry = true;
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
            updatingExpiry = false;
        });

        // Imposta la cell factory per visualizzare le info della carta
        cardsListView.setCellFactory(listView -> new ListCell<CartaDiCredito>() {
            @Override
            protected void updateItem(CartaDiCredito card, boolean empty) {
                super.updateItem(card, empty);
                if (empty || card == null) {
                    setText(null);
                } else {
                    // Mostra solo MM/YY anche se nel DB è salvato come LocalDate
                    String scadenza = "";
                    if (card.getDataScadenza() != null) {
                        int mese = card.getDataScadenza().getMonthValue();
                        int anno = card.getDataScadenza().getYear() % 100;
                        scadenza = String.format("%02d/%02d", mese, anno);
                    }
                    setText(card.getIntestatario() + " •••• " + card.getUltimeQuattroCifre() + " (" + card.getCircuito() + ")  Scad.: " + scadenza);
                }
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
        CartaDiCredito selected = cardsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            controller.deleteCard(selected); // Passa direttamente l'oggetto carta
        }
    }

    @FXML
    private void saveCard() {
        controller.saveNewCard();
    }

    @FXML
    private void clearFields() {
        cardHolderField.setText("");
        cardNumberField.setText("");
        expiryField.setText("");
        cvvField.setText("");
        clearAllErrors();
    }

    // Utility methods
    public void clearAllErrors() {
        cardHolderErrorLabel.setVisible(false);
        cardNumberErrorLabel.setVisible(false);
        expiryErrorLabel.setVisible(false);
        cvvErrorLabel.setVisible(false);
    }
    public void showSuccessMessage(String msg) {
        // Implementa se vuoi mostrare un messaggio di successo
    }
    public void clearFieldsFromController() {
        clearFields();
    }
    public void showFieldError(String field, String msg) {
        switch (field) {
            case "cardHolder":
                cardHolderErrorLabel.setText(msg);
                cardHolderErrorLabel.setVisible(true);
                break;
            case "cardNumber":
                cardNumberErrorLabel.setText(msg);
                cardNumberErrorLabel.setVisible(true);
                break;
            case "expiry":
                expiryErrorLabel.setText(msg);
                expiryErrorLabel.setVisible(true);
                break;
            case "cvv":
                cvvErrorLabel.setText(msg);
                cvvErrorLabel.setVisible(true);
                break;
        }
    }

    public String getCardHolderName() {
        return cardHolderField.getText().trim();
    }
    public String getCardNumber() {
        return cardNumberField.getText().replaceAll("\\s", "");
    }
    public String getExpiry() {
        return expiryField.getText().trim();
    }
    public String getCvv() {
        return cvvField.getText().trim();
    }
}