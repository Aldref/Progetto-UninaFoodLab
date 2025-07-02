package com.progetto.Entity.EntityDto;

import java.time.LocalDate;
import java.time.LocalTime;

abstract public class Sessione {
    private Chef chefList;
    private LocalDate  data;
    private LocalTime orario;
    private String giorno;
    private java.time.LocalTime durata;
    private int id_Sessione;
    private int id_Corso;
    private Chef chef;

    
    public Sessione(String giorno, LocalDate data, LocalTime orario, java.time.LocalTime durata, int id_Sessione) {
        this.giorno = giorno;
        this.data = data;
        this.orario = orario;
        this.durata = durata;
        this.chef = new Chef();
        this.id_Sessione = id_Sessione;

    }


    public Chef getChef() {
        return chef;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
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

    public LocalTime getOrario() {
        return orario;
    }

    public int getId_Corso() {
        return id_Corso;
    }
    public void setId_Corso(int id_Corso) {
        this.id_Corso = id_Corso;
    }


    public void setOrario(LocalTime orario) {
        this.orario = orario;
    }


    public java.time.LocalTime getDurata() {
        return durata;
    }

    public void setDurata(java.time.LocalTime durata) {
        this.durata = durata;
    }
    public int getId_Sessione() {
        return id_Sessione;
    }
    public void setId_Sessione(int id_Sessione) {
        this.id_Sessione = id_Sessione;
    }
    

}