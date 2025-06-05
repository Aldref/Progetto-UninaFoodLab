package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.progetto.Entity.EntityDto.Corso;
import com.progetto.Entity.EntityDto.SessioniInPresenza;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;


public class SessioneInPresenzaDao extends SessioniDao {
  public ArrayList<SessioniInPresenza> recuperoSessioniInPresenzaPerCorso(Corso corso){
      ArrayList<SessioniInPresenza> sessioni = new ArrayList<>();
      String query = "SELECT * FROM SESSIONE_PRESENZA WHERE id_Corso = ?";
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

                SessioniInPresenza sessione = new SessioniInPresenza(rs.getString("giorno"), rs.getDate("data").toLocalDate(), rs.getFloat("orario"), rs.getInt("durata"), rs.getString("citta"), rs.getString("via"), rs.getString("cap"), rs.getString("attrezzatura"));
                sessioni.add(sessione);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return sessioni;
    }
  }



