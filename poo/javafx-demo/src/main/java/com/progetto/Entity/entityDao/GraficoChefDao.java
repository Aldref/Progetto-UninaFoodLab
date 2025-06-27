package com.progetto.Entity.entityDao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.progetto.Entity.EntityDto.Chef;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class GraficoChefDao {
public int RicavaMinimo(Chef chef1){
    int ValoreMinimo = 0; 
    String quarry="Select min_ricette_in_sessione From vista_statistiche_mensili_chef where IdChef=?";
    SupportDb dbu= new SupportDb();
    Connection conn=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(quarry);
            ps.setInt(1, chef1.getId_Chef());
            rs = ps.executeQuery(); 

    } catch (SQLException e) {

    }
    return ValoreMinimo;
}


public int RicavaMassimo(Chef chef1){
    int ValoreMassimo = 0; 
    String quarry="Select max_ricette_in_sessione From vista_statistiche_mensili_chef where IdChef=?";
    SupportDb dbu= new SupportDb();
    Connection conn=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(quarry);
            ps.setInt(1, chef1.getId_Chef());
            rs = ps.executeQuery(); 

    } catch (SQLException e) {

    }
    return ValoreMassimo;
}

public float RicavaMedia(Chef chef1){
    float media = 0; 
    String quarry="Select media_ricette_in_sessione From vista_statistiche_mensili_chef where IdChef=?";
    SupportDb dbu= new SupportDb();
    Connection conn=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(quarry);
            ps.setInt(1, chef1.getId_Chef());
            rs = ps.executeQuery(); 

    } catch (SQLException e) {

    }
    return media;
}

public int RicavaNumeroCorsi(Chef chef1){
    int NumeroCorsi = 0; 
    String quarry="Select numero_corsi From vista_statistiche_mensili_chef where IdChef=?";
    SupportDb dbu= new SupportDb();
    Connection conn=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(quarry);
            ps.setInt(1, chef1.getId_Chef());
            rs = ps.executeQuery(); 

    } catch (SQLException e) {

    }
    return NumeroCorsi;
}

public int RicavaNumeroSessioniInPresenza(Chef chef1){
    int numeroSessioniInPresenza = 0; 
    String quarry="Select  numero_sessioni_presenza   From vista_statistiche_mensili_chef where IdChef=?";
    SupportDb dbu= new SupportDb();
    Connection conn=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(quarry);
            ps.setInt(1, chef1.getId_Chef());
            rs = ps.executeQuery(); 

    } catch (SQLException e) {

    }
    return numeroSessioniInPresenza;
}

public int RicavaNumeroSesssioniTelematiche(Chef chef1){
    int numeroSessioneTelematiche = 0; 
    String quarry="Select numero_sessioni_telematiche From vista_statistiche_mensili_chef where IdChef=?";
    SupportDb dbu= new SupportDb();
    Connection conn=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(quarry);
            ps.setInt(1, chef1.getId_Chef());
            rs = ps.executeQuery(); 

    } catch (SQLException e) {

    }
    return numeroSessioneTelematiche;
}

public float ricavaGuadagno (Chef chef1){
  int guadagnoTotale = 0; 
    String quarry="Select guadagno_totale From vista_statistiche_mensili_chef where IdChef=?";
    SupportDb dbu= new SupportDb();
    Connection conn=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(quarry);
            ps.setInt(1, chef1.getId_Chef());
            rs = ps.executeQuery(); 

    } catch (SQLException e) {

    }
    return guadagnoTotale;
}
}

