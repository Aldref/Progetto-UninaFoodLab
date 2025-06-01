package com.progetto.entity;
import com.progetto.jdbc.ConnectionJavaDb;
import java.sql.*;

public class UtenteDao {
    public boolean ControlloEmailUtente(String Email){
        try{
            Connection conn = ConnectionJavaDb.getConnection();
            String query="SELECT EXISTS (SELECT 1 FROM Partecipante WHERE Email = ?) OR EXISTS (SELECT 1 FROM Chef WHERE Email = ?) AS Esistenza";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1,Email);
            ps.setString(2,Email);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
            return rs.getBoolean("Esistenza");
            }
        } catch (SQLException sqe){
            //inserire errore 
        }
        return  true;
    }
    
}   
