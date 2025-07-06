
package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.progetto.Entity.EntityDto.Ingredienti;
import com.progetto.Entity.EntityDto.Ricetta;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class ricettaDao {

    // Rimuove l'associazione tra una ricetta e un ingrediente (tabella PREPARAZIONEINGREDIENTE)
    public void rimuoviAssociazioneIngrediente(Ricetta ricetta, Ingredienti ingrediente) {
        String query = "DELETE FROM PREPARAZIONEINGREDIENTE WHERE IdRicetta = ? AND IdIngrediente = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ricetta.getId_Ricetta());
            ps.setInt(2, ingrediente.getId_Ingrediente());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }

    public ArrayList<Ingredienti> getIngredientiRicetta(Ricetta ricetta) {
        String query = "Select I.Nome, I.UnitaDiMisura, I.IdIngrediente, P.QuanititaUnitaria from INGREDIENTE I Natural join PREPARAZIONEINGREDIENTE P where P.IdRicetta=?";
        ArrayList<Ingredienti> ingredientiRic = new ArrayList<>();
        SupportDb dbu= new SupportDb();
        Connection conn=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ricetta.getId_Ricetta());
            rs = ps.executeQuery();
            while (rs.next()) {
                Ingredienti ingrediente = new Ingredienti(rs.getString("Nome"), rs.getFloat("QuanititaUnitaria"), rs.getString("UnitaDiMisura"));
                ingrediente.setIdIngrediente(rs.getInt("IdIngrediente"));
                new IngredientiDao().recuperaQuantitaTotale(ingrediente,ricetta);
                ingredientiRic.add(ingrediente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return ingredientiRic;
    }

    public void memorizzaRicetta(Ricetta ricetta) {
        String query = "Insert into Ricetta (Nome) values (?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, ricetta.getNome());
            ps.executeUpdate();
            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                ricetta.setId_Ricetta(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
            if (generatedKeys != null) {
                try { generatedKeys.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    

    public void associaIngredientiARicetta(Ricetta ricetta, Ingredienti ingredienti) {
        String query = "Insert into PREPARAZIONEINGREDIENTE (IdRicetta,IdIngrediente,QuanititaUnitaria) values (?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ricetta.getId_Ricetta());
            ps.setInt(2, ingredienti.getId_Ingrediente());
            ps.setFloat(3, ingredienti.getQuantita());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }

    public void cancellaricetta (Ricetta ricetta) {
        String query = "Delete from RICETTA where IdRicetta=?";
        Connection conn = null;
        PreparedStatement ps = null;
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ricetta.getId_Ricetta());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
    }

}
