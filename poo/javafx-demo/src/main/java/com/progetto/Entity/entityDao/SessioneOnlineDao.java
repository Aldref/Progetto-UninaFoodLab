package com.progetto.Entity.entityDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.progetto.Entity.EntityDto.Corso;
import com.progetto.Entity.EntityDto.Sessione;
import com.progetto.Entity.EntityDto.SessioneOnline;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class SessioneOnlineDao extends SessioniDao {

  
  public ArrayList<SessioneOnline> recuperoSessioniCorsoOnline(Corso corso){
      ArrayList<SessioneOnline> sessioni = new ArrayList<>();
      String query = "SELECT * FROM SESSIONE_TELEMATICA WHERE id_Corso = ?";
      Connection conn = null;
      PreparedStatement ps = null;
      ResultSet rs = null;
        SupportDb dbu = new SupportDb();

        try {
            conn = ConnectionJavaDb.getConnection();
                ps = conn.prepareStatement(query);
                ps.setInt(1, corso.getId_Corso());
                rs = ps.executeQuery();

            while (rs.next()) {

                SessioneOnline sessione = new SessioneOnline(rs.getString("giorno"),
                        rs.getDate("data").toLocalDate(),
                        rs.getFloat("orario"),
                        rs.getInt("durata"),
                        rs.getString("Applicazione"),
                        rs.getString("Codicechiamata"),
                        rs.getString("Descrizione"));
                sessioni.add(sessione);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return sessioni;
    }
    @Override
  public void MemorizzaSessione(Sessione sessione) {
        String query = "INSERT INTO SESSIONE_TELEMATICA (Giorno, Data, Orario, Durata, Applicazione, Codicechiamata, Descrizione,IDChef) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        SupportDb dbu = new SupportDb();
    
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ((SessioneOnline) sessione).getId_Corso());
            ps.setString(2, sessione.getGiorno());
            ps.setDate(3, java.sql.Date.valueOf(sessione.getData()));
            ps.setFloat(4, sessione.getOrario());
            ps.setInt(5, sessione.getDurata());
            ps.setString(6, ((SessioneOnline) sessione).getApplicazione());
            ps.setString(7, ((SessioneOnline) sessione).getCodicechiamata());
            ps.setString(8, ((SessioneOnline) sessione).getDescrizione());
            ps.setInt(9, sessione.getChef().getId_Chef());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);        }
    }
    public Collection<? extends Sessione> recuperoSessionCorsoTelematiche(Corso corso) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'recuperoSessionCorsoTelematiche'");
    }



    
}
