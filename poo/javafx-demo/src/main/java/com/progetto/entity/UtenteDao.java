package com.progetto.entity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.DbUtils;

public class UtenteDao {
    public boolean ControlloEmailUtente(String Email){
          String query="SELECT EXISTS (SELECT 1 FROM Partecipante WHERE Email = ?) OR EXISTS (SELECT 1 FROM Chef WHERE Email = ?) AS Esistenza";
          DbUtils dbu= new DbUtils();
          Connection conn=null;
          PreparedStatement ps=null;
          ResultSet rs=null;
          try{
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1,Email);
            ps.setString(2,Email);
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
    
    public void RegistrazioneUtente(String nome,String Cognome,String email , String password , String NumeroDiTelefono, LocalDate DataDiNascita){
        String query="Insert INTO Partecipante  (Nome,Cognome,Email,Password,NumeroDiTelefono,DataDiNAscita,) Values (?,?,?,?,?,?)";
          DbUtils dbu= new DbUtils();
          Connection conn=null;
          PreparedStatement ps=null;
          try{
            conn = ConnectionJavaDb.getConnection();
            ps=conn.prepareStatement(query);
            java.sql.Date sqlData = java.sql.Date.valueOf(DataDiNascita);
            ps.setString(1, nome);
            ps.setString(2, Cognome);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, NumeroDiTelefono);
            ps.setDate(6, sqlData);
            } catch (SQLException sqe){
            //inserire errore 
            }finally {
                dbu.closeStatement(ps);
                dbu.closeConnection(conn);
        }
    

    }
    
}   
