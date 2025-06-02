    package com.progetto.entity;
    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.SQLException;
    import java.time.LocalDate;

    import com.progetto.jdbc.ConnectionJavaDb;
    import com.progetto.jdbc.DbUtils;
    public class UtenteVisitatoreDao extends UtenteDao {
        
        @Override
        public void RegistrazioneUtente(String nome, String cognome, String email, String password, String numeroDiTelefono, LocalDate dataDiNascita) {
            String query = "INSERT INTO Partecipante (Nome, Cognome, Email, Password, NumeroDiTelefono, DataDiNAscita) VALUES (?, ?, ?, ?, ?, ?)";
            DbUtils dbu = new DbUtils();
            Connection conn = null;
            PreparedStatement ps = null;

            try {
                conn = ConnectionJavaDb.getConnection();
                ps = conn.prepareStatement(query);

                java.sql.Date sqlData = java.sql.Date.valueOf(dataDiNascita);

                ps.setString(1, nome);
                ps.setString(2, cognome);
                ps.setString(3, email);
                ps.setString(4, password);
                ps.setString(5, numeroDiTelefono);
                ps.setDate(6, sqlData);
                ps.execute(); 
            } catch (SQLException sqe) {
                //gestire errore
            } finally {
                dbu.closeStatement(ps);
                dbu.closeConnection(conn);
            }
        }

        @Override
        public void RegistrazioneUtente(String nome, String cognome, String email, String password,
                String numeroDiTelefono, LocalDate dataDiNascita, String AnniDiEsperienza) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'RegistrazioneUtente'");
        }
        
    }

