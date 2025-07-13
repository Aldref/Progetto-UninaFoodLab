package com.progetto.Entity.EntityDto;
import java.util.ArrayList;

public class Ricetta  {
    private String nome;
    private ArrayList<Ingredienti> ingredientiRicetta;
    private int id_Ricetta;
    public Ricetta(String nome) {
        this.nome = nome;
        this.ingredientiRicetta = new ArrayList<>();
    }
    public Ricetta(String nome,ArrayList<Ingredienti> ingredientiRicetta) {
        this.nome = nome;
        this.ingredientiRicetta = ingredientiRicetta;
    }

    public String getNome() {
        return nome;
    }

    public ArrayList<Ingredienti> getIngredientiRicetta() {
        return ingredientiRicetta;
    }

    public void aggiungiIngrediente(Ingredienti ingrediente) {
        ingredientiRicetta.add(ingrediente);
    }

    public void rimuoviIngrediente(Ingredienti ingrediente) {
        ingredientiRicetta.remove(ingrediente);
    }

    public int getId_Ricetta() {
        return id_Ricetta;
    }
    public void setId_Ricetta(int id_Ricetta) {
        this.id_Ricetta = id_Ricetta;
    }

    public void setIngredientiRicetta(ArrayList<Ingredienti> ingredientiRicetta) {
        this.ingredientiRicetta = ingredientiRicetta;
    }
    
    public String setNome(String nome) {
        this.nome = nome;
        return this.nome;
    }
} 