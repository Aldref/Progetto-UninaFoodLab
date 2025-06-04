package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.progetto.Entity.EntityDto.Chef;
import com.progetto.Entity.EntityDto.Corso;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class ChefDao extends UtenteDao  {
   
    public void RegistrazioneUtente(Chef chef1) {
        String query = "INSERT INTO Chef (Nome, Cognome, Email, Password, NumeroDiTelefono, DataDiNascita, AnniDiEsperienza) VALUES (?, ?, ?, ?, ?, ?, ?)";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);

            java.sql.Date sqlData = java.sql.Date.valueOf(chef1.getDataDiNascita());

            ps.setString(1, chef1.getNome());
            ps.setString(2, chef1.getCognome());
            ps.setString(3, chef1.getEmail());
            ps.setString(4, chef1.getPassword());
            ps.setString(5, chef1.getNumeroDiTelefono());
            ps.setDate(6, sqlData);
            ps.setInt(7, chef1.getAnniDiEsperienza());
            ps.execute(); 
            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                chef1.setId_Chef(generatedKeys.getInt(1));
            }
        } catch (SQLException sqe) {
            // aggiungi errore 
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }


    public void AssegnaCorso(Corso corso, Chef chef1) {
    String query = "INSERT INTO  Chef_Corso (id_Chef, id_Corso) VALUES (?, ?)";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);

            ps.setInt(1, chef1.getId_Chef());
            ps.setInt(2, corso.getId_Corso());
            ps.execute();

        } catch (SQLException sqe) {
            sqe.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }
     
    public void  recuperaDatiChef(Chef chef) {
        String query = "SELECT * FROM Chef WHERE id_Chef = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, chef.getId_Chef());
            rs = ps.executeQuery();

            if (rs.next()) {
                chef.setNome(rs.getString("Nome"));
                chef.setCognome(rs.getString("Cognome"));
                chef.setEmail(rs.getString("Email"));
                chef.setPassword(rs.getString("Password"));
                chef.setNumeroDiTelefono(rs.getString("NumeroDiTelefono"));
                chef.setDataDiNascita(rs.getDate("DataDiNascita").toLocalDate());
                chef.setAnniDiEsperienza(rs.getInt("AnniDiEsperienza"));
            }
        } catch (SQLException sqe) {
            // aggiungi errore
        } finally {
            dbu.closeAll(conn, ps, rs);
        }

    }



    public List<Corso> recuperaCorsiChef(Chef chef) {
        String query = "SELECT c.* FROM Corso c JOIN Chef_Corso cc ON c.id_Corso = cc.id_Corso WHERE cc.id_Chef = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Corso> corsi = new ArrayList<>();

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, chef.getId_Chef());
            rs = ps.executeQuery();

            while (rs.next()) {
                Corso corso = new Corso();
                corso.setId_Corso(rs.getInt("id_Corso"));
                corso.setNome(rs.getString("Nome"));
                corso.setDescrizione(rs.getString("Descrizione"));
                corso.setDataInizio(rs.getDate("DataInizio").toLocalDate());
                corso.setDataFine(rs.getDate("DataFine").toLocalDate());
                corso.setFrequenzaDelleSessioni(rs.getString("FrequenzaDelleSessioni"));
                corso.setMaxPersone(rs.getInt("MaxPersone"));
                corso.setPrezzo(rs.getFloat("Prezzo"));
                corso.setUrl_Propic(rs.getString("Url_Propic"));
                corsi.add(corso);
            }
        
        } catch (SQLException sqe) {
       // aggiungi errore
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return corsi;
    }

}
