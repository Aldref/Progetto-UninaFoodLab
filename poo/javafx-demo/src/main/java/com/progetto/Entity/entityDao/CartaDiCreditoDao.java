package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.progetto.Entity.EntityDto.CartaDiCredito;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class CartaDiCreditoDao {
public void memorizzaCarte(CartaDiCredito carta){
        String query = "INSERT INTO Carta (Intestatario, DataScadenza, UltimeQuattroCifre, Circuito) VALUES (?, ?, ?, ?, ?)";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys=null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);

            ps.setString(1,carta.getIntestatario() );
            ps.setString(2, carta.getDataScadenza().toString());
            ps.setString(3, carta.getUltimeQuattroCifre());
            ps.setString(4, carta.getCircuito());
            ps.execute();
            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                carta.setIdCarta(String.valueOf(generatedKeys.getInt(1)));
            }
        } catch (SQLException sqe) {
            //gestire errore
        } finally {
            dbu.closeAll(conn, ps, generatedKeys);
        }
    }

public void CancellaCarta(CartaDiCredito carta){
        String query = "Deleat Carta where IdCarta=? ";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys=null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1,carta.getIdCarta() );
        } catch (SQLException sqe) {
            //gestire errore
        } finally {
            dbu.closeAll(conn, ps, generatedKeys);
        }
    }

}

