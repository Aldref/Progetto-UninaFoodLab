package com.progetto.Entity.entityDao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.progetto.Entity.EntityDto.Chef;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class GraficoChefDao {
public int RicavaMinimo(Chef chef1, int mese, int anno){
    int ValoreMinimo = 0; 
    String quarry="SELECT min_ricette_in_sessione FROM vista_statistiche_mensili_chef WHERE IdChef=? AND mese=? AND anno=?";
    SupportDb dbu= new SupportDb();
    Connection conn=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(quarry);
        ps.setInt(1, chef1.getId_Chef());
        ps.setInt(2, mese);
        ps.setInt(3, anno);
        rs = ps.executeQuery(); 
        if (rs.next()) {
            ValoreMinimo = rs.getInt(1);
        }
        System.out.println("[DAO] RicavaMinimo: chef=" + chef1.getId_Chef() + ", mese=" + mese + ", anno=" + anno + " -> " + ValoreMinimo);
    } catch (SQLException e) {
        // log error
    } finally {
        dbu.closeAll(conn, ps, rs);
    }
    return ValoreMinimo;
}


public int RicavaMassimo(Chef chef1, int mese, int anno){
    int ValoreMassimo = 0; 
    String quarry="SELECT max_ricette_in_sessione FROM vista_statistiche_mensili_chef WHERE IdChef=? AND mese=? AND anno=?";
    SupportDb dbu= new SupportDb();
    Connection conn=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(quarry);
        ps.setInt(1, chef1.getId_Chef());
        ps.setInt(2, mese);
        ps.setInt(3, anno);
        rs = ps.executeQuery(); 
        if (rs.next()) {
            ValoreMassimo = rs.getInt(1);
        }
        System.out.println("[DAO] RicavaMassimo: chef=" + chef1.getId_Chef() + ", mese=" + mese + ", anno=" + anno + " -> " + ValoreMassimo);
    } catch (SQLException e) {
        // log error
    } finally {
        dbu.closeAll(conn, ps, rs);
    }
    return ValoreMassimo;
}

public float RicavaMedia(Chef chef1, int mese, int anno){
    float media = 0; 
    String quarry="SELECT media_ricette_in_sessione FROM vista_statistiche_mensili_chef WHERE IdChef=? AND mese=? AND anno=?";
    SupportDb dbu= new SupportDb();
    Connection conn=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(quarry);
        ps.setInt(1, chef1.getId_Chef());
        ps.setInt(2, mese);
        ps.setInt(3, anno);
        rs = ps.executeQuery(); 
        if (rs.next()) {
            media = rs.getFloat(1);
        }
        System.out.println("[DAO] RicavaMedia: chef=" + chef1.getId_Chef() + ", mese=" + mese + ", anno=" + anno + " -> " + media);
    } catch (SQLException e) {
        // log error
    } finally {
        dbu.closeAll(conn, ps, rs);
    }
    return media;
}

public int RicavaNumeroCorsi(Chef chef1, int mese, int anno){
    int NumeroCorsi = 0; 
    String quarry="SELECT numero_corsi FROM vista_statistiche_mensili_chef WHERE IdChef=? AND mese=? AND anno=?";
    SupportDb dbu= new SupportDb();
    Connection conn=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(quarry);
        ps.setInt(1, chef1.getId_Chef());
        ps.setInt(2, mese);
        ps.setInt(3, anno);
        rs = ps.executeQuery(); 
        if (rs.next()) {
            NumeroCorsi = rs.getInt(1);
        }
        System.out.println("[DAO] RicavaNumeroCorsi: chef=" + chef1.getId_Chef() + ", mese=" + mese + ", anno=" + anno + " -> " + NumeroCorsi);
    } catch (SQLException e) {
        // log error
    } finally {
        dbu.closeAll(conn, ps, rs);
    }
    return NumeroCorsi;
}

public int RicavaNumeroSessioniInPresenza(Chef chef1, int mese, int anno){
    int numeroSessioniInPresenza = 0; 
    String quarry="SELECT numero_sessioni_presenza FROM vista_statistiche_mensili_chef WHERE IdChef=? AND mese=? AND anno=?";
    SupportDb dbu= new SupportDb();
    Connection conn=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(quarry);
        ps.setInt(1, chef1.getId_Chef());
        ps.setInt(2, mese);
        ps.setInt(3, anno);
        rs = ps.executeQuery(); 
        if (rs.next()) {
            numeroSessioniInPresenza = rs.getInt(1);
        }
        System.out.println("[DAO] RicavaNumeroSessioniInPresenza: chef=" + chef1.getId_Chef() + ", mese=" + mese + ", anno=" + anno + " -> " + numeroSessioniInPresenza);
    } catch (SQLException e) {
        // log error
    } finally {
        dbu.closeAll(conn, ps, rs);
    }
    return numeroSessioniInPresenza;
}

public int RicavaNumeroSesssioniTelematiche(Chef chef1, int mese, int anno){
    int numeroSessioneTelematiche = 0; 
    String quarry="SELECT numero_sessioni_telematiche FROM vista_statistiche_mensili_chef WHERE IdChef=? AND mese=? AND anno=?";
    SupportDb dbu= new SupportDb();
    Connection conn=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(quarry);
        ps.setInt(1, chef1.getId_Chef());
        ps.setInt(2, mese);
        ps.setInt(3, anno);
        rs = ps.executeQuery(); 
        if (rs.next()) {
            numeroSessioneTelematiche = rs.getInt(1);
        }
        System.out.println("[DAO] RicavaNumeroSesssioniTelematiche: chef=" + chef1.getId_Chef() + ", mese=" + mese + ", anno=" + anno + " -> " + numeroSessioneTelematiche);
    } catch (SQLException e) {
        // log error
    } finally {
        dbu.closeAll(conn, ps, rs);
    }
    return numeroSessioneTelematiche;
}

public float ricavaGuadagno (Chef chef1, int mese, int anno){
    float guadagnoTotale = 0; 
    String quarry="SELECT guadagno_totale FROM vista_statistiche_mensili_chef WHERE IdChef=? AND mese=? AND anno=?";
    SupportDb dbu= new SupportDb();
    Connection conn=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(quarry);
        ps.setInt(1, chef1.getId_Chef());
        ps.setInt(2, mese);
        ps.setInt(3, anno);
        rs = ps.executeQuery(); 
        if (rs.next()) {
            guadagnoTotale = rs.getFloat(1);
        }
        System.out.println("[DAO] ricavaGuadagno: chef=" + chef1.getId_Chef() + ", mese=" + mese + ", anno=" + anno + " -> " + guadagnoTotale);
    } catch (SQLException e) {
        // log error
    } finally {
        dbu.closeAll(conn, ps, rs);
    }
    return guadagnoTotale;
}

         // Metodo di debug: stampa tutte le colonne della view per uno chef, mese e anno
    public void stampaVistaCompleta(Chef chef1, int mese, int anno) {
        String query = "SELECT * FROM vista_statistiche_mensili_chef WHERE IdChef=? AND mese=? AND anno=?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, chef1.getId_Chef());
            ps.setInt(2, mese);
            ps.setInt(3, anno);
            rs = ps.executeQuery();
            boolean trovato = false;
            while (rs.next()) {
                trovato = true;
                System.out.println("[DEBUG-VIEW] IdChef: " + rs.getInt("IdChef")
                        + ", Nome: " + rs.getString("Nome")
                        + ", Cognome: " + rs.getString("Cognome")
                        + ", totale_ricette_mese: " + rs.getInt("totale_ricette_mese")
                        + ", max_ricette_in_sessione: " + rs.getInt("max_ricette_in_sessione")
                        + ", min_ricette_in_sessione: " + rs.getInt("min_ricette_in_sessione")
                        + ", media_ricette_in_sessione: " + rs.getFloat("media_ricette_in_sessione")
                        + ", numero_corsi: " + rs.getInt("numero_corsi")
                        + ", numero_sessioni_presenza: " + rs.getInt("numero_sessioni_presenza")
                        + ", numero_sessioni_telematiche: " + rs.getInt("numero_sessioni_telematiche")
                        + ", guadagno_totale: " + rs.getFloat("guadagno_totale")
                        + ", mese: " + rs.getInt("mese")
                        + ", anno: " + rs.getInt("anno")
                );
            }
            if (!trovato) {
                System.out.println("[DEBUG-VIEW] Nessun risultato trovato per IdChef=" + chef1.getId_Chef() + ", mese=" + mese + ", anno=" + anno);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
    }
}

