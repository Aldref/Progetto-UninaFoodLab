package com.progetto.Entity.EntityDto;


import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import com.progetto.Entity.entityDao.UtenteVisitatoreDao;

public class UtenteVisitatore extends Utente {
    private List<CartaDiCredito> carte = new ArrayList<>();
    public List<CartaDiCredito> getCarte() {
        return carte;
    }

    public void setCarte(List<CartaDiCredito> carte) {
        this.carte = carte;
    }
    // Utente loggato attuale (per sessione)
    public static UtenteVisitatore loggedUser = null;
    private int id_UtenteVisitatore;
    private UtenteVisitatoreDao utenteVisitatoreDao;

    public UtenteVisitatore(String nome, String cognome, String email, String password, LocalDate dataDiNascita) {
        super(nome, cognome, email, password, dataDiNascita);
        this.utenteVisitatoreDao = new UtenteVisitatoreDao();
    }

    public UtenteVisitatore() {
        super("", "", "", "",  null);
        this.utenteVisitatoreDao = new UtenteVisitatoreDao();
    }
   
    
    

    public int getId_UtenteVisitatore() {
        return id_UtenteVisitatore;
    }

    public void setId_UtenteVisitatore(int id_UtenteVisitatore) {
        this.id_UtenteVisitatore = id_UtenteVisitatore;
    }

    public UtenteVisitatoreDao getUtenteVisitatoreDao() {
        return utenteVisitatoreDao;
    }

    public void setUtenteVisitatoreDao(UtenteVisitatoreDao utenteVisitatoreDao) {
        this.utenteVisitatoreDao = utenteVisitatoreDao;
    }
}