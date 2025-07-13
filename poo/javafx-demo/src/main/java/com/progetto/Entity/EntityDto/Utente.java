package com.progetto.Entity.EntityDto;

import java.time.LocalDate;
import java.util.ArrayList;

abstract public class Utente {
    protected  LocalDate dataDiNascita;
    protected String nome;
    protected String cognome;
    protected  String email;
    protected  String password;
    protected String Url_Propic;
    protected  ArrayList<Corso> corsi=new ArrayList<>();

    public Utente(String nome, String cognome, String email, String password, LocalDate dataDiNascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.dataDiNascita = dataDiNascita;
    
    }
    public Utente() {
    }
    
    public ArrayList<Corso> getCorsi() {
        return corsi;
    }
    public void setcorso(ArrayList<Corso> corsi){
       this.corsi=corsi;
    }

    public String getNome() {
        return nome;
    }
    public String getCognome() {       
        return cognome;
    }
    public String getEmail() {
        return email;   
    }
    public String getPassword() {
        return password;
    }
  
    public LocalDate getDataDiNascita() {
        return dataDiNascita;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }   
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {  
        this.password = password;
    }
    
    public String getUrl_Propic(){
        return Url_Propic;
    }

    public void setUrl_Propic(String Propic){
        this.Url_Propic=Propic;
    }
    
    public void setDataDiNascita(LocalDate dataDiNascita) {
        this.dataDiNascita = dataDiNascita;
    } 
}      
