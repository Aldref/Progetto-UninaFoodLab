package com.progetto.Entity.EntityDto;

import java.time.LocalDate;

 abstract public class Utente {
   protected  LocalDate dataDiNascita;
    protected String nome;
    protected String cognome;
    protected  String email;
    protected  String password;
    protected  String numeroDiTelefono;
    

    public Utente(String nome, String cognome, String email, String password, String numeroDiTelefono, LocalDate dataDiNascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.numeroDiTelefono = numeroDiTelefono;
        this.dataDiNascita = dataDiNascita;
    
    }
    public Utente() {
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
    public String getNumeroDiTelefono() {
        return numeroDiTelefono;
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
    public void setNumeroDiTelefono(String numeroDiTelefono) {
        this.numeroDiTelefono = numeroDiTelefono;   
    }
    public void setDataDiNascita(LocalDate dataDiNascita) {
        this.dataDiNascita = dataDiNascita;
    }       

    
    
    }



    