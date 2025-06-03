package com.progetto.entity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public abstract class UtenteDao {
    protected  LocalDate dataDiNascita;
    protected String nome;
    protected String cognome;
    protected  String email;
    protected  String password;
    protected  String numeroDiTelefono;


    public UtenteDao(String nome, String cognome, String email, String password, String numeroDiTelefono, LocalDate dataDiNascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.numeroDiTelefono = numeroDiTelefono;
        this.dataDiNascita = dataDiNascita;
    
    }

    public boolean ControlloEmailUtente(){
        String query="SELECT EXISTS (SELECT 1 FROM Partecipante WHERE Email = ?) OR EXISTS (SELECT 1 FROM Chef WHERE Email = ?) AS Esistenza";
        SupportDb dbu= new SupportDb();
        Connection conn=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try{
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1,email);
            ps.setString(2,email);
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
            
    public abstract void RegistrazioneUtente();
    
}