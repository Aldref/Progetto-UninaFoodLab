package com.progetto.Entity.EntityDto;
import java.time.LocalDate;

public class SessioniInPresenza extends Sessione {
   private String citta, via, cap, attrezzatura;

    public SessioniInPresenza(String giorno, LocalDate data, float orario, int durata, String citta, String via, String cap, String attrezzatura) {
        super(giorno, data, orario, durata);
        this.citta = citta;
        this.via = via;
        this.cap = cap;
        this.attrezzatura = attrezzatura;
    }



}