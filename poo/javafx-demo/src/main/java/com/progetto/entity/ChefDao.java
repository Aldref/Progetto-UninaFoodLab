package com.progetto.entity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.DbUtils;

public class ChefDao extends UtenteDao  {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String numeroDiTelefono;
    private LocalDate dataDiNascita;
    private int anniDiEsperienza;
    private String id_Utete;// aggiustare tipo di dato in seguito 


    
    public ChefDao(String nome, String cognome, String email, String password, String numeroDiTelefono, LocalDate dataDiNascita, int anniDiEsperienza) {
        super(nome, cognome, email, password, numeroDiTelefono, dataDiNascita);
        this.anniDiEsperienza = anniDiEsperienza;

    }





    public void setid_Utete() {
        String query = "Select Id_Utente as ID from Chef where Email = ? ";
        DbUtils dbu = new DbUtils();
        Connection  conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
            id_Utete = rs.getString("ID");
            }
        } catch (SQLException sqe) {
            sqe.printStackTrace(); // Per debug
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
    }







    @Override
    public void RegistrazioneUtente() {
        String query = "INSERT INTO Chef (Nome, Cognome, Email, Password, NumeroDiTelefono, DataDiNascita, AnniDiEsperienza) VALUES (?, ?, ?, ?, ?, ?, ?)";
        DbUtils dbu = new DbUtils();
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);

            java.sql.Date sqlData = java.sql.Date.valueOf(dataDiNascita);

            ps.setString(1, nome);
            ps.setString(2, cognome);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, numeroDiTelefono);
            ps.setDate(6, sqlData);
            ps.setInt(7, anniDiEsperienza);
            ps.execute(); 
        } catch (SQLException sqe) {
            // Per debug
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }



     // passaggio classe corso invece di tutti sti parametri
    public void CreaCorso(String nome,String descrizione, LocalDate dataInizio, LocalDate dataFine,String FrequenzaDelleSessioni,int MaxPersone, float  Prezzo, String Url_Propic ){
        String query = "INSERT INTO Corso (Nome, Descrizione, DataInizio, DataFine, FrequenzaDelleSessioni, MaxPersone, Prezzo, Url_Propic) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String querryN_N = "INSERT INTO Chef_Corso (IdChef, IdCorso) VALUES (?, ?)";
        DbUtils dbu = new DbUtils();
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement AddN_N= null;// continua da qui 
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            java.sql.Date sqlDataInizio= java.sql.Date.valueOf(dataInizio);
            java.sql.Date sqlDataFine = java.sql.Date.valueOf(dataFine);
            ps.setString(1, nome);
            ps.setString(2, descrizione);
            ps.setDate(3, sqlDataInizio);
            ps.setDate(4, sqlDataFine);
            ps.setString(5, FrequenzaDelleSessioni);
            ps.setInt(6, MaxPersone);
            ps.setFloat(7, Prezzo);
            ps.setString(8, Url_Propic);
            ps.execute();

        } catch (SQLException sqe) {
            sqe.printStackTrace(); // Per debug
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }



    
}