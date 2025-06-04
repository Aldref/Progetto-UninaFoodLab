package com.progetto.Entity.EntityDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.progetto.Entity.entityDao.ChefDao;
public class Chef extends Utente {
    private int anniDiEsperienza;
    private int  id_Chef;
    private ChefDao chefDao ;
     private List<Corso> corsi = new ArrayList<Corso>();
    
    public Chef(String nome, String cognome, String email, String password, String numeroDiTelefono, LocalDate dataDiNascita, int anniDiEsperienza) {
        super(nome, cognome, email, password, numeroDiTelefono, dataDiNascita);
        this.anniDiEsperienza = anniDiEsperienza;
        this.chefDao = new ChefDao();
    }  

    public Chef() {
        super("", "", "", "", "", null);
        this.anniDiEsperienza = 0;
        this.chefDao = new ChefDao();
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
     
    public void recuperaCorsiChef() {
        corsi = chefDao.recuperaCorsiChef(this);
    }

    public List<Corso> getCorsi() {
        return corsi;
    }









}
  




