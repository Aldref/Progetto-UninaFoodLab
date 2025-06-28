package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import com.progetto.Entity.EntityDto.Chef;
import com.progetto.Entity.EntityDto.Corso;
import com.progetto.Entity.EntityDto.Utente;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class ChefDao extends UtenteDao  {
   
    @Override
    public void RegistrazioneUtente(Utente chef1) {
        String query = "INSERT INTO Chef (Nome, Cognome, Email, Password,DataDiNascita, AnniDiEsperienza) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
            ps.setDate(6, sqlData);
            ps.setInt(7, ((Chef)chef1).getAnniDiEsperienza());
            ps.execute(); 
            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                ((Chef)chef1).setId_Chef(generatedKeys.getInt(1));
            }
        } catch (SQLException sqe) {
            // aggiungi errore 
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }

    @Override
    public void AssegnaCorso(Corso corso, Utente chef1) {
    String query = "INSERT INTO  Chef_Corso (id_Chef, id_Corso) VALUES (?, ?)";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);

            ps.setInt(1, ((Chef) chef1).getId_Chef());
            ps.setInt(2, corso.getId_Corso());
            ps.execute();

        } catch (SQLException sqe) {
            sqe.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }
     
    @Override
    public void  recuperaDatiUtente(Utente chef) {
        String query = "SELECT * FROM Chef WHERE email = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, ((Chef)chef).getEmail());
            rs = ps.executeQuery();

            if (rs.next()) {
                chef.setNome(rs.getString("Nome"));
                chef.setCognome(rs.getString("Cognome"));
                chef.setEmail(rs.getString("Email"));
                chef.setPassword(rs.getString("Password"));
                chef.setDataDiNascita(rs.getDate("DataDiNascita").toLocalDate());
                ((Chef)chef).setAnniDiEsperienza(rs.getInt("AnniDiEsperienza"));
                ((Chef)chef).setId_Chef(rs.getInt("IdChef"));
            }
        } catch (SQLException sqe) {
            // aggiungi errore
        } finally {
            dbu.closeAll(conn, ps, rs);
        }

    }



   
    public void RecuperaCorsi (Utente utente){
      String query = "SELECT C.* FROM CHEF_CORSO CC NATURAL JOIN CORSO C WHERE CC.IdChef = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Corso> Corsi=new ArrayList<>();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ((UtenteVisitatore) utente).getId_UtenteVisitatore());
            rs = ps.executeQuery(); 

             while (rs.next()){
                LocalDate dataInizio = null;
                LocalDate dataFine = null;
                java.sql.Date sqlDataInizio = rs.getDate("DataInizio");
                java.sql.Date sqlDataFine = rs.getDate("DataFine");

                Corso Corso1 = new Corso(
                    rs.getString("Nome"),
                    rs.getString("Descrizione"),
                    dataInizio,
                    dataFine,
                    rs.getString("FrequenzaDelleSessioni"),
                    rs.getInt("MaxPersone"),
                    (float) rs.getDouble("Prezzo"),
                    rs.getString("Propic"));

                    Corso1.setId_Corso(rs.getInt("IdCorso"));
                    Corso1.setSessioni(new CorsoDao().recuperoSessioniPerCorso(Corso1));               
                Corsi.add(Corso1);
               
              
             
            
            }
        } catch (SQLException sqe) {
            //gestire errore
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        utente.setcorso(Corsi);
    }



    
    public void eliminaCorso(Corso corso,Chef chef) {
        String query = "DELETE FROM Corso WHERE id_Chef = ? and dataInizio = ? and dataFine = ? and nome = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, corso.getId_Corso());
            ps.setDate(2, java.sql.Date.valueOf(corso.getDataInizio()));
            ps.setDate(3, java.sql.Date.valueOf(corso.getDataFine()));
            ps.setString(4, corso.getNome());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeConnection(conn);
            dbu.closeStatement(ps);
        
        }
    }
    
    
    
     @Override
    public void ModificaUtente(Utente chef) {
        String query = "UPDATE CHEF SET Nome = COALESCE(?, Nome), Cognome = COALESCE(?, Cognome), Email = COALESCE(?, Email), Password = COALESCE(?, Password), DataDiNascita = COALESCE(?, DataDiNascita), Propic=COALESCE(?,Propic) , DESCRIZIONE=COALESCE(?,DESCRIZIONE) WHERE id_Chef = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;

        try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(query);
        java.sql.Date sqlDataDiNascita = null;
        if (chef.getDataDiNascita() != null) {
            sqlDataDiNascita = java.sql.Date.valueOf(chef.getDataDiNascita());
        }

        ps.setString(1, chef.getNome());
        ps.setString(2, chef.getCognome());
        ps.setString(3, chef.getEmail());
        ps.setString(4, chef.getPassword());
        ps.setDate(6, sqlDataDiNascita);
        ps.setString(5, ((Chef)chef).getUrl_Propic());
        ps.setString(6, ((Chef)chef).getDescrizione());
        ps.setInt(7, ((Chef)chef).getId_Chef());

        ps.executeUpdate();
    } catch (SQLException e) {
       // Gestire l'errore
    } finally {
        dbu.closeStatement(ps);
        dbu.closeConnection(conn);
    }
        

    }
    }


