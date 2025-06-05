package com.progetto.Entity.EntityDto;
import java.time.LocalDate;

public class SessioneOnline extends Sessione {
    String Applicazione, Codicechiamata, Descrizione;

    public SessioneOnline(String giorno, LocalDate data, float orario, int durata, String applicazione, String codicechiamata, String descrizione) {
        super(giorno, data, orario, durata);
        this.Applicazione = applicazione;
        this.Codicechiamata = codicechiamata;
        this.Descrizione = descrizione;
    }


}