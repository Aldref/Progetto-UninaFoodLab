package com.progetto.Entity.EntityDto;



public class GraficoChef {
    private int numeroMassimo;
    private int NumeroMinimo;
    private float Media;
    private int NumeriCorsi;
    private int numeroSessioniInPresenza;
    private int numerosessionitelematiche;

    public GraficoChef(int numeroMassimo,float Media,int NumeroMinimo,int NumeriCorsi,int numeroSessioniInPresenza,int numerosessionitelematiche){
        this.numeroMassimo=numeroMassimo;
        this.Media=(float) ((int) (Media * 10)) / 10;;
        this.NumeroMinimo=NumeroMinimo;
        this.NumeriCorsi=NumeriCorsi;
        this.numeroSessioniInPresenza=numeroSessioniInPresenza;
        this.numerosessionitelematiche=numerosessionitelematiche;
        
    }

    public GraficoChef(){
        numeroMassimo=0;
        Media=0;
        NumeroMinimo=0;
    }

    public int getNumeroMassimo() {
        return numeroMassimo;
    }

    public void setNumeroMassimo(int numeroMassimo) {
        this.numeroMassimo = numeroMassimo;
    }

    public int getNumeroMinimo() {
        return NumeroMinimo;
    }

    public void setNumeroMinimo(int numeroMinimo) {
        this.NumeroMinimo = numeroMinimo;
    }

    public float getMedia() {
        return Media;
    }

    public void setMedia(float media) {
        this.Media = (float) ((int) (media * 10)) / 10;
    }

    public int getNumeriCorsi() {
        return NumeriCorsi;
    }
    public void setNumeriCorsi(int numeriCorsi) {
        this.NumeriCorsi = numeriCorsi;
    }
    public int getNumeroSessioniInPresenza() {
        return numeroSessioniInPresenza;
    }
    public void setNumeroSessioniInPresenza(int numeroSessioniInPresenza) {
        this.numeroSessioniInPresenza = numeroSessioniInPresenza;
    }
    public int getNumerosessionitelematiche() {
        return numerosessionitelematiche;
    }
    public void setNumerosessionitelematiche(int numerosessionitelematiche) {
        this.numerosessionitelematiche = numerosessionitelematiche;
    }
}