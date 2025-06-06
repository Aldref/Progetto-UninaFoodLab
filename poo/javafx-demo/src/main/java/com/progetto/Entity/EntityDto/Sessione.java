package com.progetto.Entity.EntityDto;

import java.time.LocalDate;

abstract public class Sessione {
    private Chef chefList;
    private LocalDate  data;
    private float orario;
    private String giorno;
    private int durata;
    private int id_Sessione;
    private int id_Corso;
    private Chef chef;

    

    public Sessione(String giorno, LocalDate data, float orario, int  durata) {
        this.giorno = giorno;
        this.data = data;
        this.orario = orario;
        this.durata = durata;
        this.chef = new Chef();

    }

    public Chef getChef() {
        return chef;
    }

    public void setChefList(Chef chefList) {
        this.chefList = chefList;
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

    public int getId_Corso() {
        return id_Corso;
    }
    public void setId_Corso(int id_Corso) {
        this.id_Corso = id_Corso;
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