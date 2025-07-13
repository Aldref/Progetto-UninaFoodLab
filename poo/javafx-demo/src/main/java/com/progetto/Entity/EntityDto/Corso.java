package com.progetto.Entity.EntityDto;
import java.time.LocalDate;
import java.util.ArrayList;

public class Corso {
    private String nome;
    private String descrizione;
    private LocalDate dataInizio;
    private  LocalDate dataFine;
    private  String FrequenzaDelleSessioni;
    private  int MaxPersone;
    private float  Prezzo;
    private String Url_Propic;
    private int  id_Corso;
    private ArrayList<Sessione> sessioni = new ArrayList<>();
    private ArrayList<String> TipiDiCucina=new ArrayList<>();

    private String chefNome = "";
    private String chefCognome = "";
    private int chefEsperienza = 0;
   
    public Corso(String nome, String descrizione, LocalDate dataInizio, LocalDate dataFine, String frequenzaDelleSessioni, int maxPersone, float prezzo, String url_Propic) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.FrequenzaDelleSessioni = frequenzaDelleSessioni;
        this.MaxPersone = maxPersone;
        this.Prezzo = prezzo;
        this.Url_Propic = url_Propic;
    }

    public Corso() {
        this.nome = "";
        this.descrizione = "";
        this.dataInizio = LocalDate.now();
        this.dataFine = LocalDate.now();
        this.FrequenzaDelleSessioni = "";
        this.MaxPersone = 0;
        this.Prezzo = 0.0f;
        this.Url_Propic = "";
        this.id_Corso = 0;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public String getFrequenzaDelleSessioni() {
        return FrequenzaDelleSessioni;
    }

    public void setFrequenzaDelleSessioni(String frequenzaDelleSessioni) {
        this.FrequenzaDelleSessioni = frequenzaDelleSessioni;
    }

    public int getMaxPersone() {
        return MaxPersone;
    }

    public void setMaxPersone(int maxPersone) {
        this.MaxPersone = maxPersone;
    }

    public float getPrezzo() {
        return Prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.Prezzo = prezzo;
    }

    public String getUrl_Propic() {
        return Url_Propic;
    }

    public void setUrl_Propic(String url_Propic) {
        this.Url_Propic = url_Propic;
    }

    public int getId_Corso() {
        return id_Corso;
    }

    public void setId_Corso(int id_Corso) {
        this.id_Corso = id_Corso;
    }

    public void setSessioni(ArrayList<Sessione> sessioni) {
        this.sessioni = sessioni;
    }

    public void addTipoDiCucina(String tipo) {

            TipiDiCucina.add(tipo);
        
    }

    public ArrayList<String> getTipiDiCucina() {
        return new ArrayList<>(TipiDiCucina);
    }

    public void setTipiDiCucina(ArrayList<String> tipiDiCucina) {
        if (tipiDiCucina != null) {
            this.TipiDiCucina = new ArrayList<>(tipiDiCucina);
        } else {
            this.TipiDiCucina = new ArrayList<>();
        }
    }

    public String getChefNome() {
        return chefNome;
    }
    public void setChefNome(String chefNome) {
        this.chefNome = chefNome != null ? chefNome : "";
    }
    public String getChefCognome() {
        return chefCognome;
    }
    public void setChefCognome(String chefCognome) {
        this.chefCognome = chefCognome != null ? chefCognome : "";
    }
    public int getChefEsperienza() {
        return chefEsperienza;
    }
    public void setChefEsperienza(int chefEsperienza) {
        this.chefEsperienza = chefEsperienza;
    }



    public ArrayList<String> getGiorniDellaSettimana() {
        return new ArrayList<>();
    }
}
