package com.progetto.Entity.EntityDto;
import java.time.LocalDate;
import java.time.LocalTime;

public class SessioneOnline extends Sessione {
    String Applicazione, Codicechiamata, Descrizione;

    public SessioneOnline(String giorno, LocalDate data, LocalTime orario, java.time.LocalTime durata, String applicazione, String codicechiamata, String descrizione, int id_Sessione) {
        super(giorno, data, orario, durata, id_Sessione);
        this.Applicazione = applicazione;
        this.Codicechiamata = codicechiamata;
        this.Descrizione = descrizione;
        
    }
    public String getApplicazione() {
        return Applicazione;
    }           
    
    public String getCodicechiamata() {
        return Codicechiamata;
    }

    public String getDescrizione() {
        return Descrizione;
    }

    public void setApplicazione(String applicazione) {
        this.Applicazione = applicazione;
    }

    public void setCodicechiamata(String codicechiamata) {
        this.Codicechiamata = codicechiamata;
    }

    public void setDescrizione(String descrizione) {
        this.Descrizione = descrizione;
    }

   

}