package com.progetto.controller;

import java.io.IOException;
import java.time.LocalDate;

import com.progetto.Entity.entityDao.ChefDao;
import com.progetto.Entity.entityDao.UtenteVisitatoreDao;
import com.progetto.utils.SceneSwitcher;

import javafx.stage.Window;


public class RegisterController {
    private final ChefDao chefDao = new ChefDao();
    private final UtenteVisitatoreDao utenteVisitatoreDao = new UtenteVisitatoreDao();

    public String validaRegistrazione(
        String nome, String cognome, String email, String password, String confermaPassword,
        String genere, String descrizione, String anniEsperienza,
        boolean utenteSelezionato, boolean chefSelezionato, LocalDate dataNascita
    ) {
        StringBuilder messaggioErrore = new StringBuilder();
        boolean valid = true;

        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() ||
            password.isEmpty() || confermaPassword.isEmpty() ||
            dataNascita == null || genere == null) {
            messaggioErrore.append("• Compila tutti i campi obbligatori (*)\n");
            valid = false;
        }

        if (!password.equals(confermaPassword)) {
            messaggioErrore.append("• Le password non coincidono\n");
            valid = false;
        }

        if (password.length() < 6) {
            messaggioErrore.append("• La password deve avere almeno 6 caratteri\n");
            valid = false;
        }

        if (!email.contains("@") || !email.contains(".")) {
            messaggioErrore.append("• Inserisci un'email valida\n");
            valid = false;
        }

        if (!utenteSelezionato && !chefSelezionato) {
            messaggioErrore.append("• Seleziona il tipo di account\n");
            valid = false;
        }

        if (chefSelezionato && (descrizione == null || descrizione.isEmpty())) {
            messaggioErrore.append("• La descrizione è obbligatoria per i Chef\n");
            valid = false;
        }

        if (chefSelezionato && (anniEsperienza == null || anniEsperienza.isEmpty())) {
            messaggioErrore.append("• Gli anni di esperienza sono obbligatori per i Chef\n");
            valid = false;
        }

        if (chefSelezionato && anniEsperienza != null && !anniEsperienza.isEmpty()) {
            try {
                int anni = Integer.parseInt(anniEsperienza);
                if (anni < 0) {
                    messaggioErrore.append("• Gli anni di esperienza devono essere un numero positivo\n");
                    valid = false;
                }
                if (anni > 50) {
                    messaggioErrore.append("• Gli anni di esperienza non possono superare 50\n");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                messaggioErrore.append("• Gli anni di esperienza devono essere un numero valido\n");
                valid = false;
            }
        }

        if (dataNascita != null &&
            dataNascita.isAfter(java.time.LocalDate.now().minusYears(13))) {
            messaggioErrore.append("• Devi avere almeno 13 anni per registrarti\n");
            valid = false;
        }

        return valid ? null : messaggioErrore.toString();
    }

    /**
     * Registra un utente nel database in base al tipo selezionato.
     * @return String messaggio di esito (null se tutto ok, altrimenti errore)
     */
    public String registraUtente(
        String nome, String cognome, String email, String password, String genere,
        String descrizione, String anniEsperienza, boolean utenteSelezionato,
        boolean chefSelezionato, LocalDate dataNascita
    ) {
        if (chefSelezionato && utenteSelezionato) {
            // Caso anomalo: entrambi selezionati
            return "Seleziona solo un tipo di account.";
        }
        if (chefSelezionato) {
            // Registrazione Chef
            try {
                int anniExp = Integer.parseInt(anniEsperienza);
                com.progetto.Entity.EntityDto.Chef chef = new com.progetto.Entity.EntityDto.Chef(
                    nome, cognome, email, password, dataNascita, anniExp
                );
                chef.setDescrizione(descrizione);
                chefDao.RegistrazioneUtente(chef);
                return null; // Successo
            } catch (NumberFormatException e) {
                return "Errore: anni di esperienza non validi.";
            } catch (Exception e) {
                String msg = e.getMessage();
                if (msg != null) {
                    String msgLower = msg.toLowerCase();
                    if (msgLower.contains("duplicate")) {
                        return "Email già registrata come Chef.";
                    }
                    if (msgLower.contains("età stimata di inizio esperienza") || msgLower.contains("iniziare (18)")) {
                        return "Gli anni di esperienza inseriti non sono compatibili con la tua età: puoi iniziare a lavorare come chef solo a partire dai 18 anni.";
                    }
                    if (msgLower.contains("esperienza") || msgLower.contains("trigger") || msgLower.contains("check")) {
                        return "Gli anni di esperienza non possono superare l'età.";
                    }
                }
                return "Errore durante la registrazione dello chef: " + (msg != null ? msg : "Errore sconosciuto");
            }
        } else if (utenteSelezionato) {
            // Registrazione Utente Visitatore
            try {
                com.progetto.Entity.EntityDto.UtenteVisitatore utente = new com.progetto.Entity.EntityDto.UtenteVisitatore(
                    nome, cognome, email, password, dataNascita
                );
                utenteVisitatoreDao.RegistrazioneUtente(utente);
                return null; // Successo
            } catch (Exception e) {
                String msg = e.getMessage();
                if (msg != null && msg.toLowerCase().contains("duplicate")) {
                    return "Email già registrata come Utente.";
                }
                return "Errore durante la registrazione dell'utente: " + (msg != null ? msg : "Errore sconosciuto");
            }
        } else {
            return "Tipo di account non selezionato.";
        }
    }

    public void tornaAlLogin(Window window) {
        try {
            SceneSwitcher.switchScene((javafx.stage.Stage) window, "/fxml/loginpage.fxml", "UninaFoodLab - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}