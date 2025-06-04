package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.progetto.Entity.EntityDto.Corso;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;


public class CorsoDao{
    


    public void memorizzaCorsoERicavaId(Corso corso) {
    String query = "INSERT INTO Corso (Nome, Descrizione, DataInizio, DataFine, FrequenzaDelleSessioni, MaxPersone, Prezzo, Url_Propic) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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
        ps.executeUpdate();

        generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            corso.setId_Corso(generatedKeys.getInt(1));
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        dbu.closeResultSet(generatedKeys);
        dbu.closeStatement(ps);
        dbu.closeConnection(conn);
    }
    }
    
}
