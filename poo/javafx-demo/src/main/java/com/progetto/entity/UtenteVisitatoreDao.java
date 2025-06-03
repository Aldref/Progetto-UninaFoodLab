package com.progetto.entity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class UtenteVisitatoreDao extends UtenteDao {
    private int id_Utente;
    public UtenteVisitatoreDao(String nome, String cognome, String email, String password, String numeroDiTelefono, LocalDate dataDiNascita) {
        super(nome, cognome, email, password, numeroDiTelefono, dataDiNascita);
    }

    @Override
    public void RegistrazioneUtente() {
        String query = "INSERT INTO Partecipante (Nome, Cognome, Email, Password, NumeroDiTelefono, DataDiNascita) VALUES (?, ?, ?, ?, ?, ?)";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys=null;
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
            ps.execute(); 
            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                id_Utente = generatedKeys.getInt(1);
            }
        } catch (SQLException sqe) {
            //gestire errore
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }

 
}