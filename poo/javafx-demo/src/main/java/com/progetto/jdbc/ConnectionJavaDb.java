package com.progetto.jdbc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class ConnectionJavaDb{
    private static String driver="org.postgresql.Driver";
    private static String URL="jdbc:postgresql://localhost:5432/Uninafoodlab";
    private static String USER = "Mario"; 
    private static String PASSWORD = "PasswordMario1";
   
    
    public static Connection getConnection() throws SQLException {
        try{
            Class.forName(driver);
        } catch (ClassNotFoundException e){
            throw new SQLException("Driver PostgreSQL non trovato!", e);
        }

        return DriverManager.getConnection(URL,USER,PASSWORD);

    }

}