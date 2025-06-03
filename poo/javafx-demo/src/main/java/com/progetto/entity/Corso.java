package com.progetto.entity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;


public class Corso{
    private String nome;
    private String descrizione;
    private LocalDate dataInizio;
    private  LocalDate dataFine;
    private  String FrequenzaDelleSessioni;
    private  int MaxPersone;
    private float  Prezzo;
    private String Url_Propic;
    private int  id_Corso;

    public Corso(String nome, String descrizione, LocalDate dataInizio, LocalDate dataFine, String frequenzaDelleSessioni, int maxPersone, float prezzo, String url_Propic) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.FrequenzaDelleSessioni = frequenzaDelleSessioni;
        this.MaxPersone = maxPersone;
        this.Prezzo = prezzo;
        this.Url_Propic = url_Propic;
    }
    


    public void memorizzaCorsoERicavaId() {
    String query = "INSERT INTO Corso (Nome, Descrizione, DataInizio, DataFine, FrequenzaDelleSessioni, MaxPersone, Prezzo, Url_Propic) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    SupportDb dbu = new SupportDb();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet generatedKeys = null;

    try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

        ps.setString(1, nome);
        ps.setString(2, descrizione);
        ps.setDate(3, java.sql.Date.valueOf(dataInizio));
        ps.setDate(4, java.sql.Date.valueOf(dataFine));
        ps.setString(5, FrequenzaDelleSessioni);
        ps.setInt(6, MaxPersone);
        ps.setFloat(7, Prezzo);
        ps.setString(8, Url_Propic);
        ps.executeUpdate();

        generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            id_Corso = generatedKeys.getInt(1);
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        dbu.closeResultSet(generatedKeys);
        dbu.closeStatement(ps);
        dbu.closeConnection(conn);
    }
}
    public int getId_Corso() {
        return id_Corso;
    }
}
