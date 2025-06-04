package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.progetto.Entity.EntityDto.Utente;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public abstract class UtenteDao {
    

    public boolean ControlloEmailUtente(Utente utente) {
        String query="SELECT EXISTS (SELECT 1 FROM Partecipante WHERE Email = ?) OR EXISTS (SELECT 1 FROM Chef WHERE Email = ?) AS Esistenza";
        SupportDb dbu= new SupportDb();
        Connection conn=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try{
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);

            ps.setString(1,utente.getEmail());
            ps.setString(2,utente.getEmail());
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getBoolean("Esistenza");
            }
        } catch (SQLException sqe){
            //inserire errore 
        }finally {
            dbu.closeAll(conn, ps, rs);
        }
        return  true;
    }
     
    


    
}