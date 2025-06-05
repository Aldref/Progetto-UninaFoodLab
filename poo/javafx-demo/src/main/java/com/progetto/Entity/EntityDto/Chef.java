package com.progetto.Entity.EntityDto;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.progetto.Entity.entityDao.ChefDao;
public class Chef extends Utente {
    private int anniDiEsperienza;
    private int  id_Chef;
     private List<Corso> corsi = new ArrayList<Corso>();
    
    public Chef(String nome, String cognome, String email, String password, String numeroDiTelefono, LocalDate dataDiNascita, int anniDiEsperienza) {
        super(nome, cognome, email, password, numeroDiTelefono, dataDiNascita);
        this.anniDiEsperienza = anniDiEsperienza;
    }  

    public Chef() {
        super();
        this.anniDiEsperienza = 0;
   
    }


    public int getAnniDiEsperienza() {
        return anniDiEsperienza;
    }       

    public int getId_Chef() {
        return id_Chef;
    }
    public void setId_Chef(int id_Chef) {
        this.id_Chef = id_Chef;
    }

    public void setAnniDiEsperienza(int anniDiEsperienza) {
        this.anniDiEsperienza = anniDiEsperienza;
    }
     
    public void recuperaCorsiChef(ArrayList<Corso> corsi) {
        this.corsi = corsi; 
    }

    public List<Corso> getCorsi() {
        return corsi;
    }

    








}
  




