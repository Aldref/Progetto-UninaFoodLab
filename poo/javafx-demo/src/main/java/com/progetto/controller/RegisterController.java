package com.progetto.controller;

import javafx.stage.Window;
import java.io.IOException;
import java.time.LocalDate;
import com.progetto.utils.SceneSwitcher;

public class RegisterController {

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

    public void registraUtente(
        String nome, String cognome, String email, String password, String genere,
        String descrizione, String anniEsperienza, boolean utenteSelezionato,
        boolean chefSelezionato, LocalDate dataNascita
    ) {
        // Qui puoi salvare l'utente nel database o altro
        System.out.println("=== REGISTRAZIONE VALIDA ===");
        System.out.println("Nome: " + nome);
        System.out.println("Cognome: " + cognome);
        System.out.println("Email: " + email);
        System.out.println("Data Nascita: " + dataNascita);
        System.out.println("Genere: " + genere);
        System.out.println("Tipo: " + (chefSelezionato ? "Chef" : "Utente"));
        if (chefSelezionato) {
            System.out.println("Descrizione: " + descrizione);
            System.out.println("Anni di esperienza: " + anniEsperienza);
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