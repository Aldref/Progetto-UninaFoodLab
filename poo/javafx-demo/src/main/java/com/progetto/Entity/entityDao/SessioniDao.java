package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.progetto.Entity.EntityDto.Chef;
import com.progetto.Entity.EntityDto.Sessione;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

abstract public class SessioniDao {

    public boolean ControllosSessioniAttiveLoStessoPeriodo(Sessione sessione, Chef chef) {
        String Quarry = "Select count(*) as NumSessioni from SESSIONE_PRESENZA_CHEF natural join SESSIONE where id_Chef = ? and Giorno = ? and Data = ? and Orario = ? and Durata = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SupportDb dbu = new SupportDb();   

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(Quarry);
            ps.setInt(1, chef.getId_Chef());
            ps.setString(2, sessione.getGiorno());
            ps.setDate(3, java.sql.Date.valueOf(sessione.getData()));
            ps.setTime(4, java.sql.Time.valueOf(sessione.getOrario()));
            ps.setTime(5, java.sql.Time.valueOf(sessione.getDurata()));
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
                dbu.closeAll(conn, ps, rs); 
        }
        return false;
    }

    abstract public void  MemorizzaSessione(Sessione sessione);
}