package com.progetto.Entity.EntityDto;
import java.time.LocalDate;
import java.util.ArrayList;
public class Chef extends Utente {
    // Utente chef loggato attuale (per sessione)
    public static Chef loggedUser = null;
    private com.progetto.Entity.entityDao.ChefDao chefDao = new com.progetto.Entity.entityDao.ChefDao();
    private int anniDiEsperienza;
    private int  id_Chef;
    private String Descrizione;
    private GraficoChef grafico1;

    public com.progetto.Entity.entityDao.ChefDao getChefDao() {
        return chefDao;
    }
    
    public Chef(String nome, String cognome, String email, String password, LocalDate dataDiNascita, int anniDiEsperienza) {
        super(nome, cognome, email, password,  dataDiNascita);
        this.anniDiEsperienza = anniDiEsperienza;
    }  

    public Chef() {
        super();
        this.anniDiEsperienza = 0;
   
    }
    
    
    public String getDescrizione(){
        return Descrizione ;
    }

    public void  setDescrizione(String Descrizione){
        this.Descrizione=Descrizione ;
    } 


    public int getAnniDiEsperienza() {
        return anniDiEsperienza;
    }       
    public void setGrafico(GraficoChef Grafico){
      grafico1=Grafico; 
    }

    public GraficoChef getGraficoChef(){
        return grafico1;
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

    

    








}
  




