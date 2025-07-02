package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.progetto.Entity.EntityDto.Ingredienti;
import com.progetto.Entity.EntityDto.Ricetta;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;
public class IngredientiDao {

public void memorizzaIngredienti(Ingredienti ingredienti) {
        String query = "INSERT INTO ingrediente (Nome, UnitaDiMisura) VALUES (?, ?::unitadimisura)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, ingredienti.getNome());
            ps.setString(2, ingredienti.getUnitaMisura());
            ps.executeUpdate();
            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                ingredienti.setIdIngrediente(generatedKeys.getInt(1));
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
public void cancellaingrediente(Ingredienti ingredienti) {
        String query = "DELETE FROM ingrediente WHERE IdIngrediente= ?";
        Connection conn = null;
        PreparedStatement ps = null;
        SupportDb dbu = new SupportDb();
        
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ingredienti.getId_Ingrediente());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        }
}

        public void modificaIngredienti(Ingredienti ingredienti) {
        String query = "UPDATE ingrediente SET Nome = ?, UnitaDiMisura = ? WHERE IdIngrediente = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        SupportDb dbu = new SupportDb();
        
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, ingredienti.getNome());
            ps.setString(2, ingredienti.getUnitaMisura());
            ps.setInt(3, ingredienti.getId_Ingrediente());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
        } 
    }

    public void recuperaQuantitaTotale(Ingredienti ingredienti, Ricetta ricetta) {
        String query = "SELECT QuantitaTotale FROM QuantitaPerSessione WHERE IdIngrediente = ? and IdRicetta = ?  ";
        Connection conn = null;
        PreparedStatement ps = null;
        SupportDb dbu = new SupportDb();
        ResultSet rs = null;

        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ingredienti.getId_Ingrediente());
            ps.setInt(2, ricetta.getId_Ricetta());
            rs = ps.executeQuery();

            if (rs.next()) {
                float quantitaTotale = rs.getFloat("QuantitaTotale");
                ingredienti.setQuantitaTotale(quantitaTotale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
    }
    // la quantita totale di un ingrediente si aggiorna in automatico quando si associa un ingrediente a una ricetta
}