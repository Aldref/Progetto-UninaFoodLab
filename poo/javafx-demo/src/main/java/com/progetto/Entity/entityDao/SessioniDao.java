package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.progetto.Entity.EntityDto.Chef;
import com.progetto.Entity.EntityDto.Corso;
import com.progetto.Entity.EntityDto.Sessione;
import com.progetto.jdbc.ConnectionJavaDb;

abstract public class SessioniDao {

    public boolean ControllosSessioniAttiveLoStessoPeriodo(Sessione sessione, Chef chef) {
        String Quarry = "Select count(*) as NumSessioni from SESSIONE_PRESENZA_CHEF natural join SESSIONE where id_Chef = ? and Giorno = ? and Data = ? and Orario = ? and Durata = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(Quarry);
            ps.setInt(1, chef.getId_Chef());
            ps.setString(2, sessione.getGiorno());
            ps.setDate(3, java.sql.Date.valueOf(sessione.getData()));
            ps.setFloat(4, sessione.getOrario());
            ps.setInt(5, sessione.getDurata());
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
          // gestisci errore 
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public ArrayList<Sessione> recuperoSessioniPerChef(Corso corso) {
        SessioneInPresenzaDao sessioneInPresenzaDao = new SessioneInPresenzaDao();
        SessioneOnlineDao sessioneOnlineDao = new SessioneOnlineDao();
        ArrayList<Sessione> sessioni = new ArrayList<>();
        sessioni.addAll(sessioneInPresenzaDao.recuperoSessioniInPresenzaPerCorso(corso));
        sessioni.addAll(sessioneOnlineDao.recuperoSessioniOnlinePerCorso(corso));
            return sessioni;
        }
    }