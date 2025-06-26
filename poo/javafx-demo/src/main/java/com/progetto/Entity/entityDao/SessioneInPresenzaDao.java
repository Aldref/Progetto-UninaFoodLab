package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.progetto.Entity.EntityDto.Corso;
import com.progetto.Entity.EntityDto.Ricetta;
import com.progetto.Entity.EntityDto.Sessione;
import com.progetto.Entity.EntityDto.SessioniInPresenza;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;


public class SessioneInPresenzaDao extends SessioniDao {
    
    public ArrayList<SessioniInPresenza> recuperoSessionCorso(Corso corso){
      ArrayList<SessioniInPresenza> sessioni = new ArrayList<>();
      String query = "SELECT * FROM SESSIONE_PRESENZA WHERE id_Corso = ?";
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
                    rs.getFloat("orario"),
                    rs.getInt("durata"),
                    rs.getString("citta"),
                    rs.getString("via"),
                    rs.getString("cap"),
                    rs.getString("attrezzatura"),
                    rs.getInt("id_Sessione")
                );
               
                sessione.setRicette(this.recuperaRicetteSessione(sessione));
                sessione.setCorsoList(this.recuperaPartecipantiSessione(sessione));
                sessioni.add(sessione);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return sessioni;
    }
    

  @Override
  public void MemorizzaSessione(Sessione sessione) {
        String query = "INSERT INTO SESSIONE_TELEMATICA ( giorno, Data, Orario, Durata, citta, via, cap, Attrezzatura, IDcorso, IdChef) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        SupportDb dbu = new SupportDb();
    
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1, sessione.getGiorno());
            ps.setDate(2, java.sql.Date.valueOf(sessione.getData()));
            ps.setFloat(3, sessione.getOrario());
            ps.setInt(4, sessione.getDurata());
            ps.setString(5, ((SessioniInPresenza) sessione).getCitta());
            ps.setString(6, ((SessioniInPresenza) sessione).getVia());
            ps.setString(7, ((SessioniInPresenza) sessione).getCap());
            ps.setString(8, ((SessioniInPresenza) sessione).getAttrezzatura());
            ps.setInt(9, ((SessioniInPresenza) sessione).getId_Corso());
            ps.setInt(10, ((SessioniInPresenza) sessione).getChef().getId_Chef());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }

    public ArrayList<UtenteVisitatore> recuperaPartecipantiSessione(SessioniInPresenza Sessione) {
        String query = "SELECT * FROM UTENTE_ WHERE id_Sessione = ?";
        ArrayList<UtenteVisitatore> partecipanti = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SupportDb dbu = new SupportDb();

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, Sessione.getId_Sessione());
            rs = ps.executeQuery();

            while (rs.next()) {
                UtenteVisitatore utente = new UtenteVisitatore(rs.getString("nome"), rs.getString("cognome"), rs.getString("email"), rs.getString("password"), rs.getString("numeroDiTelefono"), rs.getDate("dataDiNascita").toLocalDate());
                partecipanti.add(utente);
            }
        } catch (SQLException e) {
            e.printStackTrace();

    }
        finally {
            dbu.closeAll(conn, ps, rs);
        }
        return partecipanti;
    }

    public ArrayList<Ricetta> recuperaRicetteSessione(Sessione idSessione) {
        String query = "Select R.Nome,R.IdRicetta as Id from Ricetta R Natural Join SESSIONE_PRESENZA_RICETTA SP where So.IdSessione=?";
        ArrayList<Ricetta> ricette = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SupportDb dbu = new SupportDb();

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, idSessione.getId_Sessione());
            rs = ps.executeQuery();

            while (rs.next()) {
                Ricetta ricetta = new Ricetta(rs.getString("nome"));
                ricetta.setId_Ricetta(rs.getInt("id_Ricetta"));
                ricetta.setIngredientiRicetta(new ricettaDao().getIngredientiRicetta(ricetta.getId_Ricetta()));
                ricette.add(ricetta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return ricette;
    }


  }



