package com.progetto.jdbc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class ConnectionJavaDb{
    private static String driver="org.postgresql.Driver";
    private static String URL="";//Aggiungere url del db con localhost:5432
    private static String USER = "nomeUtente"; 
    private static String PASSWORD = "tuaPassword";
   
    
    public static Connection getConnection() throws SQLException {
        try{
            Class.forName(driver);
        } catch (ClassNotFoundException e){
            //Aggiungere messaggio di errore, non è stato trovato il driver
        }

        return DriverManager.getConnection(URL,USER,PASSWORD);

    }



}