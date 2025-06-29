
package com.progetto.Entity.entityDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import com.progetto.Entity.EntityDto.CartaDiCredito;
import com.progetto.jdbc.ConnectionJavaDb;
import com.progetto.jdbc.SupportDb;

public class CartaDiCreditoDao {
    // Recupera tutte le carte di credito associate a un utente
    public List<CartaDiCredito> getCarteByUtenteId(int idUtente) {
        List<CartaDiCredito> carte = new ArrayList<>();
        // JOIN tra POSSIEDE e CARTA per recuperare solo le carte effettivamente possedute dall'utente
        String query = "SELECT c.* FROM POSSIEDE p JOIN Carta c ON p.IdCarta = c.IdCarta WHERE p.IdPartecipante = ?";
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, idUtente);
            rs = ps.executeQuery();
            while (rs.next()) {
                CartaDiCredito carta = new CartaDiCredito();
                carta.setIdCarta(rs.getString("IdCarta"));
                carta.setIntestatario(rs.getString("Intestatario"));
                // Conversione robusta della data
                java.sql.Date sqlDate = rs.getDate("DataScadenza");
                if (sqlDate != null) {
                    carta.setDataScadenza(sqlDate.toLocalDate());
                } else {
                    carta.setDataScadenza(null);
                }
                carta.setUltimeQuattroCifre(rs.getString("UltimeQuattroCifre"));
                carta.setCircuito(rs.getString("Circuito"));
                carte.add(carta);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // LOG per debug
        } finally {
            dbu.closeAll(conn, ps, rs);
        }
        return carte;
    }
    public void memorizzaCarta(CartaDiCredito carta, int idUtente) {
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement psSelect = null;
        PreparedStatement psInsert = null;
        ResultSet rsSelect = null;
        ResultSet generatedKeys = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            // 1. Cerca se esiste già una carta identica
            String selectId = "SELECT IdCarta FROM Carta WHERE Intestatario = ? AND DataScadenza = ? AND UltimeQuattroCifre = ? AND Circuito = ? ORDER BY IdCarta DESC LIMIT 1";
            psSelect = conn.prepareStatement(selectId);
            psSelect.setString(1, carta.getIntestatario());
            psSelect.setDate(2, java.sql.Date.valueOf(carta.getDataScadenza()));
            psSelect.setString(3, carta.getUltimeQuattroCifre());
            psSelect.setString(4, carta.getCircuito());
            rsSelect = psSelect.executeQuery();
            if (rsSelect.next()) {
                // Esiste già: usa quell'id
                carta.setIdCarta(String.valueOf(rsSelect.getInt("IdCarta")));
            } else {
                // Non esiste: inserisci la carta
                String query = "INSERT INTO Carta (Intestatario, DataScadenza, UltimeQuattroCifre, Circuito) VALUES (?, ?, ?, ?)";
                psInsert = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                psInsert.setString(1, carta.getIntestatario());
                psInsert.setDate(2, java.sql.Date.valueOf(carta.getDataScadenza()));
                psInsert.setString(3, carta.getUltimeQuattroCifre());
                psInsert.setString(4, carta.getCircuito());
                psInsert.executeUpdate();
                generatedKeys = psInsert.getGeneratedKeys();
                if (generatedKeys != null && generatedKeys.next()) {
                    carta.setIdCarta(String.valueOf(generatedKeys.getInt(1)));
                }
            }

            // 2. Inserisci la relazione in POSSIEDE se non esiste già
            if (carta.getIdCarta() != null) {
                String checkPossiede = "SELECT 1 FROM POSSIEDE WHERE IdPartecipante = ? AND IdCarta = ?";
                try (PreparedStatement psCheckPossiede = conn.prepareStatement(checkPossiede)) {
                    psCheckPossiede.setInt(1, idUtente);
                    psCheckPossiede.setInt(2, Integer.parseInt(carta.getIdCarta()));
                    try (ResultSet rsPossiede = psCheckPossiede.executeQuery()) {
                        if (!rsPossiede.next()) {
                            String insertPossiede = "INSERT INTO POSSIEDE (IdPartecipante, IdCarta) VALUES (?, ?)";
                            try (PreparedStatement psInsertPossiede = conn.prepareStatement(insertPossiede)) {
                                psInsertPossiede.setInt(1, idUtente);
                                psInsertPossiede.setInt(2, Integer.parseInt(carta.getIdCarta()));
                                psInsertPossiede.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (SQLException sqe) {
            sqe.printStackTrace(); // LOGGA L'ECCEZIONE
        } finally {
            dbu.closeAll(null, psSelect, rsSelect);
            dbu.closeAll(conn, psInsert, generatedKeys);
        }
    }

    /**
     * Cancella la relazione POSSIEDE per la carta e l'utente, poi elimina la carta solo se non più associata ad altri utenti.
     * @param carta la carta da eliminare
     * @param idUtente l'id dell'utente che vuole eliminare la carta
     */
    public void cancellaCarta(CartaDiCredito carta, int idUtente) {
        SupportDb dbu = new SupportDb();
        Connection conn = null;
        PreparedStatement psPossiede = null;
        PreparedStatement psCheck = null;
        PreparedStatement psDeleteCarta = null;
        ResultSet rsCheck = null;
        try {
            conn = ConnectionJavaDb.getConnection();
            // 1. Elimina la relazione POSSIEDE
            String deletePossiede = "DELETE FROM POSSIEDE WHERE IdCarta = ? AND IdPartecipante = ?";
            psPossiede = conn.prepareStatement(deletePossiede);
            psPossiede.setInt(1, Integer.parseInt(carta.getIdCarta()));
            psPossiede.setInt(2, idUtente);
            psPossiede.executeUpdate();

            // 2. Controlla se la carta è ancora associata ad altri utenti
            String checkQuery = "SELECT COUNT(*) AS cnt FROM POSSIEDE WHERE IdCarta = ?";
            psCheck = conn.prepareStatement(checkQuery);
            psCheck.setInt(1, Integer.parseInt(carta.getIdCarta()));
            rsCheck = psCheck.executeQuery();
            boolean deleteCarta = false;
            if (rsCheck.next()) {
                int count = rsCheck.getInt("cnt");
                if (count == 0) {
                    deleteCarta = true;
                }
            }

            // 3. Se non è più associata, elimina la carta
            if (deleteCarta) {
                String deleteCartaQuery = "DELETE FROM Carta WHERE IdCarta = ?";
                psDeleteCarta = conn.prepareStatement(deleteCartaQuery);
                psDeleteCarta.setInt(1, Integer.parseInt(carta.getIdCarta()));
                psDeleteCarta.executeUpdate();
            }
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        } finally {
            dbu.closeAll(null, psPossiede, null);
            dbu.closeAll(null, psCheck, rsCheck);
            dbu.closeAll(conn, psDeleteCarta, null);
        }
    }

}

