package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.progetto.Entity.EntityDto.Corso;
import com.progetto.Entity.EntityDto.SessioniInPresenza;
import com.progetto.Entity.EntityDto.Utente;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class UtenteVisitatoreDao extends UtenteDao {
   
    @Override
    public void RegistrazioneUtente(Utente utenteVisitatore) {
        String query = "INSERT INTO Partecipante (Nome, Cognome, Email, Password, NumeroDiTelefono, DataDiNascita) VALUES (?, ?, ?, ?, ?, ?)";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys=null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);

            java.sql.Date sqlData = java.sql.Date.valueOf(utenteVisitatore.getDataDiNascita());

            ps.setString(1, ((UtenteVisitatore) utenteVisitatore).getNome());
            ps.setString(2, ((UtenteVisitatore) utenteVisitatore).getCognome());
            ps.setString(3, ((UtenteVisitatore) utenteVisitatore).getEmail());
            ps.setString(4, ((UtenteVisitatore) utenteVisitatore).getPassword());
            ps.setString(5, ((UtenteVisitatore) utenteVisitatore).getNumeroDiTelefono());
            ps.setDate(6, sqlData);
            ps.execute(); 
            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                ((UtenteVisitatore)utenteVisitatore).setId_UtenteVisitatore(generatedKeys.getInt(1));
            }
        } catch (SQLException sqe) {
            //gestire errore
        } finally {
            dbu.closeAll(conn, ps, generatedKeys);
        }
    }
    @Override
    public void recuperaDatiUtente   (Utente utenteVisitatore) {
        String query = "SELECT * FROM Partecipante WHERE id_UtenteVisitatore = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ((UtenteVisitatore) utenteVisitatore).getId_UtenteVisitatore());
            rs = ps.executeQuery();

            if (rs.next()) {
                utenteVisitatore.setNome(rs.getString("Nome"));
                utenteVisitatore.setCognome(rs.getString("Cognome"));
                utenteVisitatore.setEmail(rs.getString("Email"));
                utenteVisitatore.setPassword(rs.getString("Password"));
                utenteVisitatore.setNumeroDiTelefono(rs.getString("NumeroDiTelefono"));
                utenteVisitatore.setDataDiNascita(rs.getDate("DataDiNascita").toLocalDate());
            }
        } catch (SQLException sqe) {
            //gestire errore
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
    }


    @Override
    public void AssegnaCorso(Corso corso, Utente utente1) {
    String query = "INSERT INTO  RICHIESTAPAGAMENTO (idPartecipante, idCorso, DataRichiesta,ImportoPagato,StatoPagamento) VALUES (?, ?,?, ?,'Pagato')";
        LocalDate DataRichiesta= LocalDate.now();
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            java.sql.Date sqlData = java.sql.Date.valueOf(DataRichiesta);
            ps.setInt(1, ((UtenteVisitatore)utente1).getId_UtenteVisitatore());
            ps.setInt(2, corso.getId_Corso());
            ps.setDate(3, sqlData);
            ps.setDouble(4, corso.getPrezzo());
            ps.execute();

        } catch (SQLException sqe) {
            sqe.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }
     
     public void partecipaAllaSessioneDalVivo(SessioniInPresenza sessione,UtenteVisitatore UtenteVisitatore) {
        String query = "INSERT INTO ADESIONE_SESSIONEPRESENZA (id_Sessione, id_Utente,Conferma) VALUES (?, ?,TRUE)";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, sessione.getId_Sessione());
            ps.setInt(2, UtenteVisitatore.getId_UtenteVisitatore());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeConnection(conn);
            dbu.closeStatement(ps);
        }
    }

    @Override
    public void ModificaUtente(Utente utenteVisitatore) {
        String query = "UPDATE Partecipante SET Nome = COALESCE(?, Nome), Cognome = COALESCE(?, Cognome), Email = COALESCE(?, Email), Password = COALESCE(?, Password), NumeroDiTelefono = COALESCE(?, NumeroDiTelefono), DataDiNascita = COALESCE(?, DataDiNascita) WHERE id_UtenteVisitatore = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;

        try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(query);
        java.sql.Date sqlDataDiNascita = null;
        if (utenteVisitatore.getDataDiNascita() != null) {
            sqlDataDiNascita = java.sql.Date.valueOf(utenteVisitatore.getDataDiNascita());
        }

        ps.setString(1, utenteVisitatore.getNome());
        ps.setString(2, utenteVisitatore.getCognome());
        ps.setString(3, utenteVisitatore.getEmail());
        ps.setString(4, utenteVisitatore.getPassword());
        ps.setString(5, utenteVisitatore.getNumeroDiTelefono());
        ps.setDate(6, sqlDataDiNascita);
        ps.setInt(7, ((UtenteVisitatore)utenteVisitatore).getId_UtenteVisitatore());

        ps.executeUpdate();
    } catch (SQLException e) {
       // Gestire l'errore
    } finally {
        dbu.closeStatement(ps);
        dbu.closeConnection(conn);
    }
}

    }
