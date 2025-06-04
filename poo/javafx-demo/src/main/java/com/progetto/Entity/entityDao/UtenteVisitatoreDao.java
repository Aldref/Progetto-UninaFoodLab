package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class UtenteVisitatoreDao extends UtenteDao {
   
   
    public void RegistrazioneUtente(UtenteVisitatore utenteVisitatore) {
        String query = "INSERT INTO Partecipante (Nome, Cognome, Email, Password, NumeroDiTelefono, DataDiNascita) VALUES (?, ?, ?, ?, ?, ?)";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys=null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);

            java.sql.Date sqlData = java.sql.Date.valueOf(utenteVisitatore.getDataDiNascita());

            ps.setString(1, utenteVisitatore.getNome());
            ps.setString(2, utenteVisitatore.getCognome());
            ps.setString(3, utenteVisitatore.getEmail());
            ps.setString(4, utenteVisitatore.getPassword());
            ps.setString(5, utenteVisitatore.getNumeroDiTelefono());
            ps.setDate(6, sqlData);
            ps.execute(); 
            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                utenteVisitatore.setId_UtenteVisitatore(generatedKeys.getInt(1));
            }
        } catch (SQLException sqe) {
            //gestire errore
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }

 
}