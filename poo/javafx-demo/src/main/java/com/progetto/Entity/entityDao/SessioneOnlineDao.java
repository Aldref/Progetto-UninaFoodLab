package com.progetto.Entity.entityDao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;
import org.postgresql.util.PGInterval;

import com.progetto.Entity.EntityDto.Sessione;
import com.progetto.Entity.EntityDto.SessioneOnline;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class SessioneOnlineDao extends SessioniDao {

  
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
