package com.progetto.entity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class ChefDao extends UtenteDao  {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String numeroDiTelefono;
    private LocalDate dataDiNascita;
    private int anniDiEsperienza;
    private String id_Chef;// aggiustare tipo di dato in seguito 


    
    public ChefDao(String nome, String cognome, String email, String password, String numeroDiTelefono, LocalDate dataDiNascita, int anniDiEsperienza) {
        super(nome, cognome, email, password, numeroDiTelefono, dataDiNascita);
        this.anniDiEsperienza = anniDiEsperienza;

    }
    
    @Override
    public void RegistrazioneUtente() {
        String query = "INSERT INTO Chef (Nome, Cognome, Email, Password, NumeroDiTelefono, DataDiNascita, AnniDiEsperienza) VALUES (?, ?, ?, ?, ?, ?, ?)";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);

            java.sql.Date sqlData = java.sql.Date.valueOf(dataDiNascita);

            ps.setString(1, nome);
            ps.setString(2, cognome);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, numeroDiTelefono);
            ps.setDate(6, sqlData);
            ps.setInt(7, anniDiEsperienza);
            ps.execute(); 
            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                id_Chef = generatedKeys.getString(1);
            }
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }


    public void AssegnaCorso(Corso corso){
    String query = "INSERT INTO  Chef_Corso (id_Chef, id_Corso) VALUES (?, ?)";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);

            ps.setString(1, id_Chef);
            ps.setInt(2, corso.getId_Corso());
            ps.execute();

        } catch (SQLException sqe) {
            sqe.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }
}