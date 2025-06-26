
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

    public ArrayList<Ingredienti> getIngredientiRicetta(int  id_Ricetta) {
        String query = "Select I.Nome, I.UnitaDiMisura, P.QuanititaTotale, P.QuantitaUnitaria from INGREDIENTI I Natural join PREPARAZIONEINGREDIENTE P where P.IdRicetta=?";
        ArrayList<Ingredienti> ingredientiRic = new ArrayList<>();
        SupportDb dbu= new SupportDb();
        Connection conn=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, id_Ricetta);
            rs = ps.executeQuery();
            while (rs.next()) {
                Ingredienti ingrediente = new Ingredienti(rs.getString("Nome"), rs.getFloat("QuantitaUnitaria"), rs.getString("UnitaDiMisura"));
                ingrediente.setQuantitaTotale(rs.getFloat("QuantitaTotale"));
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
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, ricetta.getNome());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbu.closeStatement(ps);
            dbu.closeConnection(conn);
            }
        }
    

    public void associaIngredientiARicetta(Ricetta ricetta, Ingredienti ingredienti) {
        String query = "Insert into PREPARAZIONEINGREDIENTE (IdRicetta,IdIngrediente,QuantitaUnitaria) values (?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        SupportDb dbu = new SupportDb();
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ricetta.getId_Ricetta());
            ps.setInt(2, ingredienti.getId_Ingrediente());
            ps.setFloat(3, ingredienti.getQuantita());
            ps.executeBatch();
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
