
package com.progetto.Entity.entityDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalTime;
import org.postgresql.util.PGInterval;

import com.progetto.Entity.EntityDto.Sessione;
import com.progetto.Entity.EntityDto.SessioneOnline;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class SessioneOnlineDao extends SessioniDao {
    /**
     * Aggiorna una sessione telematica esistente nel database.
     * Aggiorna applicazione, codicechiamata, orario, durata, giorno, descrizione per la sessione specificata.
     */
    public void aggiornaSessione(SessioneOnline sessione) {
        String query = "UPDATE sessione_telematica SET applicazione = ?, codicechiamata = ?, orario = ?, durata = ?, giorno = ?::giorno, descrizione = ? WHERE idsessionetelematica = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, sessione.getApplicazione());
            ps.setString(2, sessione.getCodicechiamata());
            ps.setTime(3, java.sql.Time.valueOf(sessione.getOrario()));
            LocalTime durata = sessione.getDurata();
            int durataOre = Math.max(1, Math.min(8, durata.getHour()));
            PGInterval interval = new PGInterval(0, 0, 0, durataOre, 0, 0);
            ps.setObject(4, interval);
            ps.setString(5, sessione.getGiorno());
            String descrizione = sessione.getDescrizione();
            ps.setString(6, descrizione != null ? descrizione : "");
            ps.setInt(7, sessione.getId_Sessione());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ERRORE] Errore SQL in aggiornaSessione (SessioneOnlineDao): " + e.getMessage());
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }

    /**
     * Restituisce tutte le sessioni telematiche associate a un corso.
     */
    public static ArrayList<SessioneOnline> getSessioniByCorso(int idCorso) {
        ArrayList<SessioneOnline> sessioni = new ArrayList<>();
        String query = "SELECT * FROM sessione_telematica WHERE idcorso = ? ORDER BY data, orario";
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
                SessioneOnline sessione = new SessioneOnline();
                sessione.setId_Sessione(rs.getInt("idsessionetelematica"));
                sessione.setId_Corso(rs.getInt("idcorso"));
                sessione.setData(rs.getDate("data").toLocalDate());
                sessione.setOrario(rs.getTime("orario").toLocalTime());
                sessione.setDurata(java.time.LocalTime.of(rs.getObject("durata", org.postgresql.util.PGInterval.class).getHours(), 0));
                sessione.setApplicazione(rs.getString("applicazione"));
                sessione.setCodicechiamata(rs.getString("codicechiamata"));
                sessione.setDescrizione(rs.getString("descrizione"));
                sessione.setGiorno(rs.getString("giorno"));
                // Chef non caricato qui (richiedere join se necessario)
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
        String query = "INSERT INTO SESSIONE_TELEMATICA (Applicazione, CodiceChiamata, Data, Orario, Durata, Giorno, Descrizione, IdCorso, IdChef) VALUES (?, ?, ?, ?, ?, ?::giorno, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, ((SessioneOnline) sessione).getApplicazione());
            ps.setString(2, ((SessioneOnline) sessione).getCodicechiamata());
            ps.setDate(3, java.sql.Date.valueOf(sessione.getData()));
            ps.setTime(4, java.sql.Time.valueOf(sessione.getOrario()));
            // Calcola la durata in ore intere (1-8) e passa come interval
            LocalTime durata = sessione.getDurata();
            int durataOre = Math.max(1, Math.min(8, durata.getHour()));
            PGInterval interval = new PGInterval(0, 0, 0, durataOre, 0, 0);
            ps.setObject(5, interval);
            ps.setString(6, sessione.getGiorno());
            String descrizione = ((SessioneOnline) sessione).getDescrizione();
            ps.setString(7, descrizione != null ? descrizione : "");
            ps.setInt(8, ((SessioneOnline) sessione).getId_Corso());
            ps.setInt(9, sessione.getChef().getId_Chef());
            ps.executeUpdate();
        } catch (SQLException e) {
            // Log error if needed (no debug print)
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }
}
