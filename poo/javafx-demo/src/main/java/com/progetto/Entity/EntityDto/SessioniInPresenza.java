package com.progetto.Entity.EntityDto;
import java.time.LocalDate;
import java.util.ArrayList;

public class SessioniInPresenza extends Sessione {
   private String citta, via, cap, attrezzatura;
   private ArrayList<UtenteVisitatore> corsoListPartecipanti;

    public SessioniInPresenza(String giorno, LocalDate data, float orario, int durata, String citta, String via, String cap, String attrezzatura) {
        super(giorno, data, orario, durata);
        this.citta = citta;
        this.via = via;
        this.cap = cap;
        this.attrezzatura = attrezzatura;
        this.corsoListPartecipanti = new ArrayList<>();
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




}