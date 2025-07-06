package com.progetto.Entity.EntityDto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class SessioniInPresenza extends Sessione {

    // No-arg constructor for DAO usage
    public SessioniInPresenza() {
        super(null, null, null, null, 0);
        this.citta = null;
        this.via = null;
        this.cap = null;
        this.attrezzatura = null;
        this.descrizione = null;
        this.corsoListPartecipanti = new ArrayList<>();
        this.ricette = new ArrayList<>();
    }
    private String citta, via, cap, attrezzatura;
    private String descrizione;
    private ArrayList<UtenteVisitatore> corsoListPartecipanti;
    private ArrayList<Ricetta> ricette;

  
    public SessioniInPresenza(String giorno, LocalDate data, LocalTime orario, java.time.LocalTime durata, String citta, String via, String cap, String attrezzatura, int id_Sessione) {
        super(giorno, data, orario, durata, id_Sessione);
        this.citta = citta;
        this.via = via;
        this.cap = cap;
        this.attrezzatura = attrezzatura;
        this.descrizione = null;
        this.corsoListPartecipanti = new ArrayList<>();
        this.ricette = new ArrayList<>();
    }

    public SessioniInPresenza(String giorno, LocalDate data, LocalTime orario, java.time.LocalTime durata, String citta, String via, String cap, String attrezzatura, String descrizione, int id_Sessione) {
        super(giorno, data, orario, durata, id_Sessione);
        this.citta = citta;
        this.via = via;
        this.cap = cap;
        this.attrezzatura = attrezzatura;
        this.descrizione = descrizione;
        this.corsoListPartecipanti = new ArrayList<>();
        this.ricette = new ArrayList<>();
    }
    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getAttrezzatura() {
        return attrezzatura;
    }

    public void setAttrezzatura(String attrezzatura) {
        this.attrezzatura = attrezzatura;
    }

     public ArrayList<UtenteVisitatore> getCorsoList() {
        return corsoListPartecipanti;
    }
    

    public void setCorsoList(ArrayList<UtenteVisitatore> corsoList) {
        this.corsoListPartecipanti  = corsoList;
    }
    public ArrayList<Ricetta> getRicette() {
        return ricette;
    }
    public void setRicette(ArrayList<Ricetta> ricette) {
        this.ricette = ricette;
    }
  

}