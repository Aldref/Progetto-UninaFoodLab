package com.progetto.Entity.EntityDto;
public class Ingredienti{
    private String nome;
    private float quantita;
    private String unitaMisura;
    private int id_Ingrediente;
    private float quantitaTotale;

    public Ingredienti(String nome, float quantita, String unitaMisura) {
        this.nome = nome;
        this.quantita= quantita;
        this.unitaMisura = unitaMisura;
    }

    public String getNome() {
        return nome;
    }

    public float getQuantita() {
        return quantita;
    }

    public String getUnitaMisura() {
        return unitaMisura;
    }

    public float getQuantitaTotale() {
        return quantitaTotale;
    }

    public void setQuantitaTotale(float quantitaTotale) {
        this.quantitaTotale = quantitaTotale;
    }

    public int getId_Ingrediente() {
        return id_Ingrediente;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setQuantita(float quantita) {
        this.quantita = quantita;
    }

    public void setUnitaMisura(String unitaMisura) {
        this.unitaMisura = unitaMisura;
    }
    
    public void setIdIngrediente(int id_Ingrediente){
        this.id_Ingrediente=id_Ingrediente;
    }
}