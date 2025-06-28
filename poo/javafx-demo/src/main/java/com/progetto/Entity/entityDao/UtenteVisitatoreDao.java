package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import com.progetto.Entity.EntityDto.CartaDiCredito;
import com.progetto.Entity.EntityDto.Corso;
import com.progetto.Entity.EntityDto.SessioniInPresenza;
import com.progetto.Entity.EntityDto.Utente;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class UtenteVisitatoreDao extends UtenteDao {
   
    @Override
    public void RegistrazioneUtente(Utente utenteVisitatore) {
        String query = "INSERT INTO Partecipante (Nome, Cognome, Email, Password,  DataDiNascita) VALUES (?, ?, ?, ?, ?)";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys=null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);

            java.sql.Date sqlData = java.sql.Date.valueOf(utenteVisitatore.getDataDiNascita());

            ps.setString(1, ((UtenteVisitatore) utenteVisitatore).getNome());
            ps.setString(2, ((UtenteVisitatore) utenteVisitatore).getCognome());
            ps.setString(3, ((UtenteVisitatore) utenteVisitatore).getEmail());
            ps.setString(4, ((UtenteVisitatore) utenteVisitatore).getPassword());
            ps.setDate(5, sqlData);
            ps.execute();
            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                ((UtenteVisitatore)utenteVisitatore).setId_UtenteVisitatore(generatedKeys.getInt(1));
            }
        } catch (SQLException sqe) {
            //gestire errore
        } finally {
            dbu.closeAll(conn, ps, generatedKeys);
        }
    }
    
    @Override
    public void recuperaDatiUtente   (Utente utenteVisitatore) {
        String query = "SELECT * FROM Partecipante WHERE email = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, ((UtenteVisitatore) utenteVisitatore).getEmail());
            rs = ps.executeQuery();

            if (rs.next()) {
                utenteVisitatore.setNome(rs.getString("Nome"));
                utenteVisitatore.setCognome(rs.getString("Cognome"));
                utenteVisitatore.setEmail(rs.getString("Email"));
                utenteVisitatore.setPassword(rs.getString("Password"));
                utenteVisitatore.setDataDiNascita(rs.getDate("DataDiNascita").toLocalDate());
                ((UtenteVisitatore) utenteVisitatore).setId_UtenteVisitatore(rs.getInt("IdPartecipante"));
            }
        } catch (SQLException sqe) {
            //gestire errore
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
    }


    @Override
    public void AssegnaCorso(Corso corso, Utente utente1) {
    String query = "INSERT INTO  RICHIESTAPAGAMENTO (idPartecipante, idCorso, DataRichiesta,ImportoPagato,StatoPagamento) VALUES (?, ?,?, ?,'Pagato')";
        LocalDate DataRichiesta= LocalDate.now();
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            java.sql.Date sqlData = java.sql.Date.valueOf(DataRichiesta);
            ps.setInt(1, ((UtenteVisitatore)utente1).getId_UtenteVisitatore());
            ps.setInt(2, corso.getId_Corso());
            ps.setDate(3, sqlData);
            ps.setDouble(4, corso.getPrezzo());
            ps.execute();

        } catch (SQLException sqe) {
            sqe.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }
     
     public void partecipaAllaSessioneDalVivo(SessioniInPresenza sessione,UtenteVisitatore UtenteVisitatore) {
        String query = "INSERT INTO ADESIONE_SESSIONEPRESENZA (id_Sessione, id_Utente,Conferma) VALUES (?, ?,TRUE)";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, sessione.getId_Sessione());
            ps.setInt(2, UtenteVisitatore.getId_UtenteVisitatore());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeConnection(conn);
            dbu.closeStatement(ps);
        }
    }

    @Override
    public void ModificaUtente(Utente utenteVisitatore) {
        String query = "UPDATE Partecipante SET Nome = COALESCE(?, Nome), Cognome = COALESCE(?, Cognome), Email = COALESCE(?, Email), Password = COALESCE(?, Password), DataDiNascita = COALESCE(?, DataDiNascita) WHERE id_UtenteVisitatore = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;

        try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(query);
        java.sql.Date sqlDataDiNascita = null;
        if (utenteVisitatore.getDataDiNascita() != null) {
            sqlDataDiNascita = java.sql.Date.valueOf(utenteVisitatore.getDataDiNascita());
        }

        ps.setString(1, utenteVisitatore.getNome());
        ps.setString(2, utenteVisitatore.getCognome());
        ps.setString(3, utenteVisitatore.getEmail());
        ps.setString(4, utenteVisitatore.getPassword());
        ps.setDate(5, sqlDataDiNascita);
        ps.setInt(6, ((UtenteVisitatore)utenteVisitatore).getId_UtenteVisitatore());

        ps.executeUpdate();
    } catch (SQLException e) {
       // Gestire l'errore
    } finally {
        dbu.closeStatement(ps);
        dbu.closeConnection(conn);
    }
}
     public void RecuperaCorsi (Utente utente){
      String query = "SELECT C.* FROM RICHIESTAPAGAMENTO R NATURAL JOIN CORSO C WHERE R.IdPartecipante = ?";
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

    public void EliminaCarta( CartaDiCredito carta) {
        String query = "DELETE FROM Carta WHERE IdCarta = ? ";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(carta.getIdCarta()));
            ps.executeUpdate();
        } catch (SQLException sqe) {
            // gestire errore
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }
       
    public void aggiungiCartaAPossiede(UtenteVisitatore utente, CartaDiCredito carta) {
        String query = "INSERT INTO POSSIEDE (IdUtente, IdCarta) VALUES (?, ?)";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, utente.getId_UtenteVisitatore());
            ps.setInt(2, Integer.parseInt(carta.getIdCarta()));
            ps.executeUpdate();
        } catch (SQLException e) {
            // gestire errore
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }


    }
