package com.progetto.controller.utente;

import com.progetto.utils.CardValidator;
import com.progetto.utils.SceneSwitcher;
import com.progetto.utils.SuccessDialogUtils;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.Entity.EntityDto.CartaDiCredito;
import com.progetto.Entity.entityDao.CartaDiCreditoDao;
import com.progetto.Entity.entityDao.UtenteVisitatoreDao;
import com.progetto.boundary.utente.UserCardsBoundary;

import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.util.List;
import java.util.regex.Pattern;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.time.LocalDate;

public class UserCardsController {
    private static final Pattern NOME_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ]+(?:\\s+[a-zA-ZÀ-ÿ]+)*$");
    private static final Pattern CARTA_PATTERN = Pattern.compile("^[0-9]{16}$");
    private static final Pattern CVC_PATTERN = Pattern.compile("^[0-9]{3,4}$");
    private static final Pattern SCADENZA_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])/([0-9]{2})$");
    private UserCardsBoundary boundary;
    
    public UserCardsController(UserCardsBoundary boundary) {
        this.boundary = boundary;
    }
    
    public void loadCardsFromDb() {
        UtenteVisitatore utente = UtenteVisitatore.loggedUser;
        if (utente != null) {
            CartaDiCreditoDao dao = new CartaDiCreditoDao();
            List<CartaDiCredito> carte = dao.getCarteByUtenteId(utente.getId_UtenteVisitatore());
            utente.setCarte(carte);
            boundary.setCards(carte);
        }
    }

    public void saveNewCard() {
        boundary.clearAllErrors();
        if (validateAllFields()) {
            String holder = boundary.getCardHolderName();
            String number = boundary.getCardNumber().replaceAll("\\s", "");
            String expiry = boundary.getExpiry();
            String circuito = CardValidator.getCardType(number);
            String ultimeQuattro = number.substring(number.length() - 4);

            if (!"Visa".equals(circuito) && !"Mastercard".equals(circuito)) {
                boundary.showFieldError("cardNumber", "Tipo di carta non supportato. Accettiamo solo Visa e Mastercard");
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth ym = YearMonth.parse(expiry, formatter);
            LocalDate dataScadenza = ym.atEndOfMonth();

            CartaDiCredito carta = new CartaDiCredito();
            carta.setIntestatario(holder);
            carta.setUltimeQuattroCifre(ultimeQuattro);
            carta.setCircuito(circuito); 
            carta.setDataScadenza(dataScadenza);

            UtenteVisitatore utente = UtenteVisitatore.loggedUser;
            if (utente != null) {
                CartaDiCreditoDao dao = new CartaDiCreditoDao();
                dao.memorizzaCarta(carta, utente.getId_UtenteVisitatore());
                if (carta.getIdCarta() != null) {
                    UtenteVisitatoreDao utenteDao = new UtenteVisitatoreDao();
                    utenteDao.aggiungiCartaAPossiede(utente, carta);
                }
                showCardSaveSuccessDialog();
                boundary.clearFieldsFromController();
                loadCardsFromDb();
            }
        }
    }
    
    private void showCardSaveSuccessDialog() {
        try {
            Stage parentStage = null; 
            SuccessDialogUtils.showGenericSuccessDialog(parentStage, 
                "Carta Salvata!", 
                "La carta è stata aggiunta con successo al tuo account.");
        } catch (Exception e) {
            e.printStackTrace();
            boundary.showSuccessMessage("Carta salvata con successo!");
        }
    }
    
    private boolean validateAllFields() {
        boolean isValid = true;

        String holder = boundary.getCardHolderName();
        String number = boundary.getCardNumber().replaceAll("\\s", "");
        String expiry = boundary.getExpiry();
        String cvv = boundary.getCvv();

        if (holder.isEmpty()) {
            boundary.showFieldError("cardHolder", "Il nome è obbligatorio");
            isValid = false;
        } else if (holder.length() < 2) {
            boundary.showFieldError("cardHolder", "Il nome deve contenere almeno 2 caratteri");
            isValid = false;
        } else if (holder.length() > 50) {
            boundary.showFieldError("cardHolder", "Il nome non può superare 50 caratteri");
            isValid = false;
        } else if (!NOME_PATTERN.matcher(holder.trim()).matches()) {
            boundary.showFieldError("cardHolder", "Il nome può contenere solo lettere e spazi");
            isValid = false;
        } else if (holder.trim().split("\\s+").length < 2) {
            boundary.showFieldError("cardHolder", "Inserisci nome e cognome");
            isValid = false;
        }

        if (number.isEmpty()) {
            boundary.showFieldError("cardNumber", "Il numero della carta è obbligatorio");
            isValid = false;
        } else if (!CARTA_PATTERN.matcher(number).matches()) {
            boundary.showFieldError("cardNumber", "Il numero della carta deve essere di 16 cifre");
            isValid = false;
        } else if (!CardValidator.isValidCardType(number)) {
            boundary.showFieldError("cardNumber", "Tipo di carta non supportato. Accettiamo solo Visa e Mastercard");
            isValid = false;
        }

        if (expiry.isEmpty()) {
            boundary.showFieldError("expiry", "La scadenza è obbligatoria");
            isValid = false;
        } else if (!SCADENZA_PATTERN.matcher(expiry).matches()) {
            boundary.showFieldError("expiry", "Formato scadenza non valido (MM/AA)");
            isValid = false;
        } else if (!CardValidator.isValidExpiryDate(expiry)) {
            boundary.showFieldError("expiry", "La carta è scaduta o la data non è valida");
            isValid = false;
        }

        if (cvv.isEmpty()) {
            boundary.showFieldError("cvv", "Il CVC è obbligatorio");
            isValid = false;
        } else if (!CVC_PATTERN.matcher(cvv).matches()) {
            boundary.showFieldError("cvv", "CVC non valido (3-4 cifre)");
            isValid = false;
        }

        return isValid;
    }
    
    public void deleteCard(CartaDiCredito carta) {
        UtenteVisitatore utente = UtenteVisitatore.loggedUser;
        if (utente != null) {
            CartaDiCreditoDao dao = new CartaDiCreditoDao();
            dao.cancellaCarta(carta, utente.getId_UtenteVisitatore());
            boundary.showSuccessMessage("Carta eliminata con successo.");
            loadCardsFromDb();
        }
    }

    public void goBack(Stage stage) {
        SceneSwitcher.switchToScene(stage, "/fxml/accountmanagement.fxml");
    }
}