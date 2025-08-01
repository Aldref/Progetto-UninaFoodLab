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
import com.progetto.utils.ErrorCaricamentoPropic;

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
            sqe.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, generatedKeys);
        }
    }
    
    @Override
    public void recuperaDatiUtente(Utente utenteVisitatore) {
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
                ((UtenteVisitatore) utenteVisitatore).setUrl_Propic(rs.getString("Propic"));
            }
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
    }


    @Override
    public void AssegnaCorso(Corso corso, Utente utente1) {
        String query = "INSERT INTO RICHIESTAPAGAMENTO (idPartecipante, idCorso, DataRichiesta, ImportoPagato, StatoPagamento) VALUES (?, ?, ?, ?, 'Pagato')";
        LocalDate DataRichiesta = LocalDate.now();
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            java.sql.Date sqlData = java.sql.Date.valueOf(DataRichiesta);
            int idUtente = ((UtenteVisitatore) utente1).getId_UtenteVisitatore();
            int idCorso = corso.getId_Corso();
            System.out.println("[DEBUG] AssegnaCorso - idUtente: " + idUtente + ", idCorso: " + idCorso);
            ps.setInt(1, idUtente);
            ps.setInt(2, idCorso);
            ps.setDate(3, sqlData);
            ps.setDouble(4, corso.getPrezzo());
            ps.execute();
            boolean iscritto = isUtenteIscrittoAlCorso(idUtente, idCorso);
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }

    public boolean haGiaConfermatoPresenza(int idSessione, int idUtente) {
        String query = "SELECT 1 FROM ADESIONE_SESSIONEPRESENZA WHERE idsessionepresenza = ? AND idpartecipante = ? AND conferma = TRUE";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, idSessione);
            ps.setInt(2, idUtente);
            rs = ps.executeQuery();
            boolean result = rs.next();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return false;
    }

    public void partecipaAllaSessioneDalVivo(SessioniInPresenza sessione,UtenteVisitatore UtenteVisitatore) {
        String query = "INSERT INTO ADESIONE_SESSIONEPRESENZA (idsessionepresenza, idpartecipante, conferma) VALUES (?, ?, TRUE)";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, sessione.getId_Sessione());
            ps.setInt(2, UtenteVisitatore.getId_UtenteVisitatore());
            int rows = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeConnection(conn);
            dbu.closeStatement(ps);
        }
    }

    public boolean isUtenteIscrittoAlCorso(int idUtente, int idCorso) {
        String query = "SELECT 1 FROM RICHIESTAPAGAMENTO WHERE idpartecipante = ? AND idcorso = ? AND statopagamento = 'Pagato'";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, idUtente);
            ps.setInt(2, idCorso);
            rs = ps.executeQuery();
            boolean result = rs.next();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return false;
    }

    @Override
    public void ModificaUtente(Utente utenteVisitatore) {
        String query = "UPDATE Partecipante SET Nome = COALESCE(?, Nome), Cognome = COALESCE(?, Cognome), Email = COALESCE(?, Email), Password = COALESCE(?, Password), DataDiNascita = COALESCE(?, DataDiNascita), Propic = COALESCE(?, Propic) WHERE idpartecipante = ?";
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
        ps.setString(6, utenteVisitatore.getUrl_Propic());
        ps.setInt(7, ((UtenteVisitatore)utenteVisitatore).getId_UtenteVisitatore());

        ps.executeUpdate();
    } catch (SQLException e) {
       e.printStackTrace();
    } finally {
        dbu.closeStatement(ps);
        dbu.closeConnection(conn);
    }
}

    public void RecuperaCorsiNonIscritto(Utente utente) {
        String query = "SELECT C.*, CH.Nome AS chef_nome, CH.Cognome AS chef_cognome, CH.AnniDiEsperienza AS chef_esperienza " +
                "FROM CORSO C " +
                "LEFT JOIN CHEF CH ON C.IdChef = CH.IdChef " +
                "WHERE C.IdCorso NOT IN (SELECT R.IdCorso FROM RICHIESTAPAGAMENTO R WHERE R.IdPartecipante = ? AND R.StatoPagamento = 'Pagato') " +
                "AND (SELECT COUNT(*) FROM RICHIESTAPAGAMENTO R2 WHERE R2.IdCorso = C.IdCorso AND R2.StatoPagamento = 'Pagato') < C.MaxPersone";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Corso> Corsi = new ArrayList<>();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ((UtenteVisitatore) utente).getId_UtenteVisitatore());
            rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate dataInizio = rs.getDate("DataInizio") != null ? rs.getDate("DataInizio").toLocalDate() : null;
                LocalDate dataFine = rs.getDate("DataFine") != null ? rs.getDate("DataFine").toLocalDate() : null;
                Corso corso = new Corso(
                        rs.getString("Nome"),
                        rs.getString("Descrizione"),
                        dataInizio,
                        dataFine,
                        rs.getString("FrequenzaDelleSessioni"),
                        rs.getInt("MaxPersone"),
                        (float) rs.getDouble("Prezzo"),
                        rs.getString("Propic"));
                corso.setId_Corso(rs.getInt("IdCorso"));
                corso.setChefNome(rs.getString("chef_nome"));
                corso.setChefCognome(rs.getString("chef_cognome"));
                corso.setChefEsperienza(rs.getInt("chef_esperienza"));
                corso.setSessioni(new CorsoDao().recuperoSessioniPerCorso(corso));
                new CorsoDao().recuperaTipoCucinaCorsi(corso);
                Corsi.add(corso);
            }
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        utente.setcorso(Corsi);
    }

    public void RecuperaCorsi (Utente utente){
        String query = "SELECT C.*, CH.Nome AS chef_nome, CH.Cognome AS chef_cognome, CH.AnniDiEsperienza AS chef_esperienza " +
                    "FROM RICHIESTAPAGAMENTO R " +
                    "JOIN CORSO C ON R.IdCorso = C.IdCorso " +
                    "LEFT JOIN CHEF CH ON C.IdChef = CH.IdChef " +
                    "WHERE R.IdPartecipante = ?";
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
                LocalDate dataInizio = rs.getDate("DataInizio") != null ? rs.getDate("DataInizio").toLocalDate() : null;
                LocalDate dataFine = rs.getDate("DataFine") != null ? rs.getDate("DataFine").toLocalDate() : null;
                Corso corso = new Corso(
                    rs.getString("Nome"),
                    rs.getString("Descrizione"),
                    dataInizio,
                    dataFine,
                    rs.getString("FrequenzaDelleSessioni"),
                    rs.getInt("MaxPersone"),
                    (float) rs.getDouble("Prezzo"),
                    rs.getString("Propic"));
                corso.setId_Corso(rs.getInt("IdCorso"));
                corso.setChefNome(rs.getString("chef_nome"));
                corso.setChefCognome(rs.getString("chef_cognome"));
                corso.setChefEsperienza(rs.getInt("chef_esperienza"));
                corso.setSessioni(new CorsoDao().recuperoSessioniPerCorso(corso));
                new CorsoDao().recuperaTipoCucinaCorsi(corso);
                Corsi.add(corso);
            }
        } catch (SQLException sqe) {
            sqe.printStackTrace();
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
            sqe.printStackTrace();
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

    @Override
    public String caricaPropic(Utente utente) throws ErrorCaricamentoPropic {
    
        String query = "SELECT Propic FROM Partecipante WHERE IdPartecipante = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet RS = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ((UtenteVisitatore) utente).getId_UtenteVisitatore());
            RS = ps.executeQuery();

            if (!RS.next()) {
                throw new ErrorCaricamentoPropic();
            } else 
            {
                return(RS.getString("Propic"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, RS);
        }
        return null;
    }

    public String TipoDiAccount(String email, String password) {
        UtenteVisitatore utente = new UtenteVisitatore();
        utente.setEmail(email);
        utente.setPassword(password);
        return TipoDiAccount(utente);
    }
}


