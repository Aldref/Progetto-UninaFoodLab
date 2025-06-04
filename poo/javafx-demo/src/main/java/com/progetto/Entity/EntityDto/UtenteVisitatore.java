package com.progetto.Entity.EntityDto;

import java.time.LocalDate;

import com.progetto.Entity.entityDao.UtenteVisitatoreDao;

public class UtenteVisitatore extends Utente {
    private int id_UtenteVisitatore;
    private UtenteVisitatoreDao utenteVisitatoreDao;

    public UtenteVisitatore(String nome, String cognome, String email, String password, String numeroDiTelefono, LocalDate dataDiNascita) {
        super(nome, cognome, email, password, numeroDiTelefono, dataDiNascita);
        this.utenteVisitatoreDao = new UtenteVisitatoreDao();
    }

    public UtenteVisitatore() {
        super("", "", "", "", "", null);
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