package com.progetto.Entity.EntityDto;

import java.time.LocalDate;
import java.util.ArrayList;

abstract public class Sessione {
    private ArrayList<Chef> chefList;
    private ArrayList<UtenteVisitatore> corsoList;
    private LocalDate  data;
    private float orario;
    private String giorno;
    private int durata;
    private int id_Sessione;

    

    public Sessione(String giorno, LocalDate data, float orario, int  durata) {
        this.giorno = giorno;
        this.data = data;
        this.orario = orario;
        this.durata = durata;
        this.chefList = new ArrayList<>();
        this.corsoList = new ArrayList<>();
    }

    public ArrayList<Chef> getChefList() {
        return chefList;
    }

    public void setChefList(ArrayList<Chef> chefList) {
        this.chefList = chefList;
    }

    public ArrayList<UtenteVisitatore> getCorsoList() {
        return corsoList;
    }

    public void setCorsoList(ArrayList<UtenteVisitatore> corsoList) {
        this.corsoList = corsoList;
    }

    public String getGiorno() {
        return giorno;
    }

    public void setGiorno(String giorno) {
        this.giorno = giorno;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public float getOrario() {
        return orario;
    }


    public void setOrario(float orario) {
    int ore = (int) orario;
    int minuti = Math.round((orario - ore) * 100);

    if (ore < 8 || ore > 19) {
      //Aggiungi Errore ora fuori dal range
    }
    if (minuti < 0 || minuti > 59) {
        //Aggiungi Errore minuti fuori dal range
    }
    this.orario = orario;
}   


    public int  getDurata() {
        return durata;
    }

    public void setDurata(int durata) {
        this.durata = durata;
    }
    public int getId_Sessione() {
        return id_Sessione;
    }
    public void setId_Sessione(int id_Sessione) {
        this.id_Sessione = id_Sessione;
    }
}