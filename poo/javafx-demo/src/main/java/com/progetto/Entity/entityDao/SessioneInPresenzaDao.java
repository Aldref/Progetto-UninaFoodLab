package com.progetto.Entity.entityDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalTime;
import org.postgresql.util.PGInterval;

import com.progetto.Entity.EntityDto.Ricetta;
import com.progetto.Entity.EntityDto.Sessione;
import com.progetto.Entity.EntityDto.SessioniInPresenza;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class SessioneInPresenzaDao extends SessioniDao {

    public void aggiornaSessione(SessioniInPresenza sessione) {
        String query = "UPDATE sessione_presenza SET via = ?, cap = ?, descrizione = ?, orario = ?, durata = ? WHERE idsessionepresenza = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, sessione.getVia());
            ps.setString(2, sessione.getCap());
            ps.setString(3, sessione.getDescrizione());
            ps.setTime(4, java.sql.Time.valueOf(sessione.getOrario()));
            // Durata come PGInterval (ore e minuti)
            int durataOre = sessione.getDurata().getHour();
            int durataMin = sessione.getDurata().getMinute();
            org.postgresql.util.PGInterval interval = new org.postgresql.util.PGInterval(0, 0, 0, durataOre, durataMin, 0);
            ps.setObject(5, interval);
            ps.setInt(6, sessione.getId_Sessione());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ERRORE] Errore SQL in aggiornaSessione: " + e.getMessage());
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }

    public static ArrayList<SessioniInPresenza> getSessioniByCorso(int idCorso) {
        ArrayList<SessioniInPresenza> sessioni = new ArrayList<>();
        String query = "SELECT * FROM sessione_presenza WHERE idcorso = ? ORDER BY data, orario";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, idCorso);
            rs = ps.executeQuery();
            while (rs.next()) {
                SessioniInPresenza sessione = new SessioniInPresenza();
                sessione.setId_Sessione(rs.getInt("idsessionepresenza"));
                sessione.setId_Corso(rs.getInt("idcorso"));
                sessione.setData(rs.getDate("data").toLocalDate());
                sessione.setOrario(rs.getTime("orario").toLocalTime());
                sessione.setDurata(java.time.LocalTime.of(rs.getObject("durata", org.postgresql.util.PGInterval.class).getHours(), 0));
                sessione.setCitta(rs.getString("citta"));
                sessione.setVia(rs.getString("via"));
                sessione.setCap(rs.getString("cap"));
                sessione.setDescrizione(rs.getString("descrizione"));
                sessione.setGiorno(rs.getString("giorno"));
                sessioni.add(sessione);
            }
        } catch (SQLException e) {
            System.err.println("[ERRORE] Errore SQL in getSessioniByCorso: " + e.getMessage());
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return sessioni;
    }

    @Override
    public void MemorizzaSessione(Sessione sessione) {
        String query = "INSERT INTO SESSIONE_PRESENZA (giorno, Data, Orario, Durata, citta, via, cap, Descrizione, IDcorso, IdChef) VALUES (?::giorno, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            int idCorso = ((SessioniInPresenza) sessione).getId_Corso();
            int idChef = ((SessioniInPresenza) sessione).getChef() != null ? ((SessioniInPresenza) sessione).getChef().getId_Chef() : 0;
            if (idCorso <= 0 || idChef <= 0) {
                System.err.println("[ERRORE] id_Corso o id_Chef non valorizzati: idCorso=" + idCorso + ", idChef=" + idChef);
                return;
            }
            ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, sessione.getGiorno());
            ps.setDate(2, java.sql.Date.valueOf(sessione.getData()));
            ps.setTime(3, java.sql.Time.valueOf(sessione.getOrario()));
            LocalTime durata = sessione.getDurata();
            int durataOre = Math.max(1, Math.min(8, durata.getHour()));
            PGInterval interval = new PGInterval(0, 0, 0, durataOre, 0, 0);
            ps.setObject(4, interval);
            ps.setString(5, ((SessioniInPresenza) sessione).getCitta());
            ps.setString(6, ((SessioniInPresenza) sessione).getVia());
            ps.setString(7, ((SessioniInPresenza) sessione).getCap());
            String descrizione = ((SessioniInPresenza) sessione).getDescrizione();
            ps.setString(8, descrizione != null ? descrizione : "");
            ps.setInt(9, idCorso);
            ps.setInt(10, idChef);
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
                try { generatedKeys.close(); } catch (SQLException e) { /* no debug print */ }
            }
        }
    }

    public void rimuoviTutteAssociazioniRicetta(Ricetta ricetta) {
        String query = "DELETE FROM sessione_presenza_ricetta WHERE idricetta = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ricetta.getId_Ricetta());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }

    public void rimuoviAssociazioneRicettaASessione(SessioniInPresenza sessione, Ricetta ricetta) {
        String query = "DELETE FROM sessione_presenza_ricetta WHERE idsessionepresenza = ? AND idricetta = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, sessione.getId_Sessione());
            ps.setInt(2, ricetta.getId_Ricetta());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
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
        } finally {
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

    public void associaRicettaASessione(SessioniInPresenza sessione, Ricetta ricetta) {
        String query = "INSERT INTO sessione_presenza_ricetta (idsessionepresenza, idricetta) VALUES (?, ?) ON CONFLICT DO NOTHING";
        Connection conn = null;
        PreparedStatement ps = null;
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, sessione.getId_Sessione());
            ps.setInt(2, ricetta.getId_Ricetta());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }
}
