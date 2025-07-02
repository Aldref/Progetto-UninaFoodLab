package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import com.progetto.Entity.EntityDto.Ricetta;
import com.progetto.Entity.EntityDto.Sessione;
import com.progetto.Entity.EntityDto.SessioniInPresenza;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;
import org.postgresql.util.PGInterval;


public class SessioneInPresenzaDao extends SessioniDao {


  @Override
  public void MemorizzaSessione(Sessione sessione) {
        String query = "INSERT INTO SESSIONE_PRESENZA (giorno, Data, Orario, Durata, citta, via, cap, Descrizione, IDcorso, IdChef) VALUES (?::giorno, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, sessione.getGiorno());
            ps.setDate(2, java.sql.Date.valueOf(sessione.getData()));
            ps.setTime(3, java.sql.Time.valueOf(sessione.getOrario()));
            // Calcola la durata in ore intere (1-8) e passa come interval
            java.time.LocalTime durata = sessione.getDurata();
            int durataOre = Math.max(1, Math.min(8, durata.getHour()));
            // Usa PGInterval per PostgreSQL
            org.postgresql.util.PGInterval interval = new org.postgresql.util.PGInterval(0, 0, 0, durataOre, 0, 0);
            ps.setObject(4, interval);
            ps.setString(5, ((SessioniInPresenza) sessione).getCitta());
            ps.setString(6, ((SessioniInPresenza) sessione).getVia());
            ps.setString(7, ((SessioniInPresenza) sessione).getCap());
            // Descrizione: se null, passa stringa vuota
            String descrizione = ((SessioniInPresenza) sessione).getDescrizione();
            ps.setString(8, descrizione != null ? descrizione : "");
            ps.setInt(9, ((SessioniInPresenza) sessione).getId_Corso());
            // Usa l'id_Chef dalla superclasse Sessione (tramite getChef())
            ps.setInt(10, ((SessioniInPresenza) sessione).getChef() != null ? ((SessioniInPresenza) sessione).getChef().getId_Chef() : 0);
            ps.executeUpdate();
            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                ((SessioniInPresenza) sessione).setId_Sessione(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
            if (generatedKeys != null) {
                try { generatedKeys.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public ArrayList<UtenteVisitatore> recuperaPartecipantiSessione(SessioniInPresenza Sessione) {
        String query = "SELECT p.* FROM partecipante p JOIN adesione_sessionepresenza a ON p.idpartecipante = a.idpartecipante WHERE a.idsessionepresenza = ?";
        ArrayList<UtenteVisitatore> partecipanti = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SupportDb dbu = new SupportDb();

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, Sessione.getId_Sessione());
            rs = ps.executeQuery();

            while (rs.next()) {
                UtenteVisitatore utente = new UtenteVisitatore(
                    rs.getString("nome"),
                    rs.getString("cognome"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getDate("datadinascita").toLocalDate()
                );
                partecipanti.add(utente);
            }
        } catch (SQLException e) {
            e.printStackTrace();

    }
        finally {
            dbu.closeAll(conn, ps, rs);
        }
        return partecipanti;
    }

    public ArrayList<Ricetta> recuperaRicetteSessione(Sessione idSessione) {
        String query = "SELECT R.nome, R.idricetta FROM ricetta R JOIN sessione_presenza_ricetta SPR ON R.idricetta = SPR.idricetta WHERE SPR.idsessionepresenza = ?";
        ArrayList<Ricetta> ricette = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SupportDb dbu = new SupportDb();

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, idSessione.getId_Sessione());
            rs = ps.executeQuery();

            while (rs.next()) {
                Ricetta ricetta = new Ricetta(rs.getString("nome"));
                ricetta.setId_Ricetta(rs.getInt("idricetta"));
                ricetta.setIngredientiRicetta(new ricettaDao().getIngredientiRicetta(ricetta));
                ricette.add(ricetta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return ricette;
    }


  }



