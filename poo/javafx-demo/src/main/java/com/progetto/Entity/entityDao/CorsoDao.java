package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.progetto.Entity.EntityDto.Corso;
import com.progetto.Entity.EntityDto.Sessione;
import com.progetto.Entity.EntityDto.SessioneOnline;
import com.progetto.Entity.EntityDto.SessioniInPresenza;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;


public class CorsoDao{

    public void  memorizzaCorsoERicavaId(Corso corso) {
    String query = "INSERT INTO Corso (Nome, Descrizione, DataInizio, DataFine, FrequenzaDelleSessioni, MaxPersone, Prezzo, Propic) VALUES (?, ?, ?, ?, ?::fds, ?, ?, ?)";
    SupportDb dbu = new SupportDb();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet generatedKeys = null;

    try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

        ps.setString(1, corso.getNome());
        ps.setString(2, corso.getDescrizione());
        ps.setDate(3, java.sql.Date.valueOf(corso.getDataInizio()));
        ps.setDate(4, java.sql.Date.valueOf(corso.getDataFine()));
        ps.setString(5, corso.getFrequenzaDelleSessioni());
        ps.setInt(6, corso.getMaxPersone());
        ps.setFloat(7, corso.getPrezzo());
        ps.setString(8, corso.getUrl_Propic());
        int rows = ps.executeUpdate();

        generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            int id = generatedKeys.getInt(1);
            corso.setId_Corso(id);
        } else {
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        dbu.closeResultSet(generatedKeys);
        dbu.closeStatement(ps);
        dbu.closeConnection(conn);
    }


}
    public Corso getCorsoById(int idCorso) {
        String query = "SELECT * FROM Corso WHERE idCorso = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Corso corso = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, idCorso);
            rs = ps.executeQuery();

            if (rs.next()) {
                corso = new Corso(rs.getString("Nome"), rs.getString("Descrizione"), rs.getDate("DataInizio").toLocalDate(), rs.getDate("DataFine").toLocalDate(), rs.getString("FrequenzaDelleSessioni"), rs.getInt("MaxPersone"), rs.getFloat("Prezzo"), rs.getString("Propic"));
                corso.setId_Corso(rs.getInt("idCorso")); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return corso;
    }
    
    public ArrayList<Corso> recuperaCorsi(){
        ArrayList<Corso> corsi = new ArrayList<>();
        String query = "SELECT c.*, ch.Nome AS chef_nome, ch.Cognome AS chef_cognome, ch.AnniDiEsperienza AS chef_esperienza " +
                "FROM corso c " +
                "LEFT JOIN chef ch ON c.idchef = ch.idchef";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                Corso corso = new Corso(
                        rs.getString("Nome"),
                        rs.getString("Descrizione"),
                        rs.getDate("DataInizio") != null ? rs.getDate("DataInizio").toLocalDate() : null,
                        rs.getDate("DataFine") != null ? rs.getDate("DataFine").toLocalDate() : null,
                        rs.getString("FrequenzaDelleSessioni"),
                        rs.getInt("MaxPersone"),
                        rs.getFloat("Prezzo"),
                        rs.getString("Propic")
                );
                corso.setId_Corso(rs.getInt("idCorso"));
                corso.setChefNome(rs.getString("chef_nome"));
                corso.setChefCognome(rs.getString("chef_cognome"));
                corso.setChefEsperienza(rs.getInt("chef_esperienza"));
                recuperaTipoCucinaCorsi(corso);
                corsi.add(corso);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return corsi;
    }

    public void aggiornaCorso(Corso corso) {
        String query = "UPDATE Corso SET Nome = ?, Descrizione = ?, DataInizio = ?, DataFine = ?, FrequenzaDelleSessioni = ?::fds, MaxPersone = ?, Prezzo = ?, Propic = ? WHERE idcorso = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, corso.getNome());
            ps.setString(2, corso.getDescrizione());
            ps.setDate(3, java.sql.Date.valueOf(corso.getDataInizio()));
            ps.setDate(4, java.sql.Date.valueOf(corso.getDataFine()));
            ps.setString(5, corso.getFrequenzaDelleSessioni());
            ps.setInt(6, corso.getMaxPersone());
            ps.setFloat(7, corso.getPrezzo());
            ps.setString(8, corso.getUrl_Propic());
            ps.setInt(9, corso.getId_Corso());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, null);
        }

        }

    public void recuperaTipoCucinaCorsi(Corso corso){
        String query = "SELECT T.Nome FROM TIPODICUCINA_CORSO TC NATURAL JOIN TIPODICUCINA T WHERE TC.idcorso = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, corso.getId_Corso());
            rs = ps.executeQuery();
            while (rs.next()) {
                corso.addTipoDiCucina(rs.getString("Nome"));
            }
        } catch(SQLException e){
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
    }

    public ArrayList<Sessione> recuperoSessioniPerCorso(Corso corso) {
        ArrayList<Sessione> sessioni = new ArrayList<>();
        sessioni.addAll(this.recuperoSessionCorso(corso));
        sessioni.addAll(this.recuperoSessioniCorsoOnline(corso));
            return sessioni;
        }    



    public ArrayList<SessioneOnline> recuperoSessioniCorsoOnline(Corso corso){
        ArrayList<SessioneOnline> sessioni = new ArrayList<>();
        String query = "SELECT * FROM SESSIONE_TELEMATICA WHERE idcorso = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SupportDb dbu = new SupportDb();

        try {
            conn = ConnectionJavaDb.getConnection();
                ps = conn.prepareStatement(query);
                ps.setInt(1, corso.getId_Corso());
                rs = ps.executeQuery();

            while (rs.next()) {
                SessioneOnline sessione = new SessioneOnline(
                    rs.getString("giorno"),
                    rs.getDate("data").toLocalDate(),
                    rs.getTime("orario").toLocalTime(),
                    rs.getTime("durata").toLocalTime(),
                    rs.getString("Applicazione"),
                    rs.getString("Codicechiamata"),
                    rs.getString("Descrizione"),
                    rs.getInt("idsessionetelematica") 
                );
                sessioni.add(sessione);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return sessioni;
    }

    public ArrayList<SessioniInPresenza> recuperoSessionCorso(Corso corso){
        ArrayList<SessioniInPresenza> sessioni = new ArrayList<>();
        String query = "SELECT * FROM SESSIONE_PRESENZA WHERE idcorso = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SupportDb dbu = new SupportDb();

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, corso.getId_Corso());
            rs = ps.executeQuery();

            while (rs.next()) {
                SessioniInPresenza sessione = new SessioniInPresenza(
                    rs.getString("giorno"),
                    rs.getDate("data").toLocalDate(),
                    rs.getTime("orario").toLocalTime(),
                    rs.getTime("durata").toLocalTime(),
                    rs.getString("citta"),
                    rs.getString("via"),
                    rs.getString("cap"),
                    rs.getString("descrizione"),
                    rs.getInt("idsessionepresenza") 
                );
                sessione.setId_Corso(corso.getId_Corso());
                sessione.setRicette(new SessioneInPresenzaDao().recuperaRicetteSessione(sessione));
                sessioni.add(sessione);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return sessioni;
    }
    public static int getIdTipoCucinaByNome(String nome) {
        int idTipo = -1;
        String query = "SELECT IDTipoCucina FROM TipoDiCucina WHERE Nome = ?";
        try (Connection conn = ConnectionJavaDb.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idTipo = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idTipo;
    }

    public static void inserisciTipoCucinaCorso(int idTipoCucina, int idCorso) {
        String query = "INSERT INTO TipoDiCucina_Corso (IDTipoCucina, IDCorso) VALUES (?, ?)";
        try (Connection conn = ConnectionJavaDb.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idTipoCucina);
            ps.setInt(2, idCorso);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    }