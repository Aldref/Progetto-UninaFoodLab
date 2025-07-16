
package com.progetto.Entity.entityDao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class BarraDiRicercaDao {
public ArrayList<String> CeraEnumFrequenza() {
    ArrayList<String> Enum = new ArrayList<>();
    SupportDb dbu = new SupportDb();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String query = "SELECT unnest(enum_range(NULL::FDS )) AS valore";
    try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(query);
        rs = ps.executeQuery();
        while (rs.next()) {
            Enum.add(rs.getString("valore"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
          dbu.closeAll(conn, ps, rs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    return Enum;
}



public ArrayList<String> Categorie() {
    ArrayList<String> Categorie = new ArrayList<>();
    SupportDb dbu = new SupportDb();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String query = "SELECT DISTINCT Nome as valore FROM TIPODICUCINA ORDER BY Nome";
    try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(query);
        rs = ps.executeQuery();
        while (rs.next()) {
            Categorie.add(rs.getString("valore"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
          dbu.closeAll(conn, ps, rs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    return Categorie;
}
public ArrayList<String> GrandezzeDiMisura() {
    ArrayList<String> UnitaDiMisura = new ArrayList<>();
    SupportDb dbu = new SupportDb();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String query = "SELECT unnest(enum_range(NULL::UnitaDiMisura )) AS valore";
    try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(query);
        rs = ps.executeQuery();
        while (rs.next()) {
            UnitaDiMisura.add(rs.getString("valore"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
          dbu.closeAll(conn, ps, rs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    return UnitaDiMisura;
}

public ArrayList<String> GiorniSettimanaEnum() {
    ArrayList<String> giorni = new ArrayList<>();
    SupportDb dbu = new SupportDb();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String query = "SELECT unnest(enum_range(NULL::Giorno )) AS valore";
    try {
        conn = ConnectionJavaDb.getConnection();
        ps = conn.prepareStatement(query);
        rs = ps.executeQuery();
        while (rs.next()) {
            giorni.add(rs.getString("valore"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
          dbu.closeAll(conn, ps, rs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    return giorni;
}
}
