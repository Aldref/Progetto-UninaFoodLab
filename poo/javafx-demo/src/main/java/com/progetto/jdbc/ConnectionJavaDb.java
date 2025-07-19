package com.progetto.jdbc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class ConnectionJavaDb{
    private static String driver = "org.postgresql.Driver";
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("db.properties"));
            String profile = props.getProperty("db.profile", "default").trim();
            if (profile.equals("default")) {
                URL = props.getProperty("db.url", "jdbc:postgresql://localhost:5432/Uninafoodlab");
                USER = props.getProperty("db.user", "INSERISCI_USERNAME");
                PASSWORD = props.getProperty("db.password", "INSERISCI_PASSWORD");
            } else {
                URL = props.getProperty(profile + ".db.url", "jdbc:postgresql://localhost:5432/Uninafoodlab");
                USER = props.getProperty(profile + ".db.user", "INSERISCI_USERNAME");
                PASSWORD = props.getProperty(profile + ".db.password", "INSERISCI_PASSWORD");
            }
        } catch (IOException e) {
            URL = "jdbc:postgresql://localhost:5432/Uninafoodlab";
            USER = "INSERISCI_USERNAME";
            PASSWORD = "INSERISCI_PASSWORD";
        }
    }

    public static Connection getConnection() throws SQLException {
        try{
            Class.forName(driver);
        } catch (ClassNotFoundException e){
            throw new SQLException("Driver PostgreSQL non trovato!", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}