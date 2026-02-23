package com.restaurant.dao;

import com.restaurant.model.Log;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogDAO {
    private static final Logger logger = LogManager.getLogger(LogDAO.class);

    public boolean ajouter(Log log) {
        // Requête vers la table LOGS
        String sql = "INSERT INTO LOGS (nom_util, action) VALUES (?, ?)";
        
        // Utilisation du try-with-resources pour fermer la connexion
        try (Connection conn = ConnectionDB.getConnection()) {
            if (conn == null)
                return false;

            // On s'assure que le commit est automatique
            conn.setAutoCommit(true);

            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, log.getNomUtil());
                pstmt.setString(2, log.getAction());

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            log.setIdLog(rs.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur SQL ajouter: " + e.getMessage());
        }
        return false;
    }

    public List<Log> listerTous() {
        List<Log> liste = new ArrayList<>();
        // Tri du plus récent au plus ancien
        String sql = "SELECT * FROM LOGS ORDER BY date_log DESC";

        try (Connection conn = ConnectionDB.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Log log = new Log();
                log.setIdLog(rs.getInt("id_log"));

                // Conversion SQL Timestamp vers LocalDateTime
                Timestamp ts = rs.getTimestamp("date_log");
                if (ts != null) {
                    log.setDateLog(ts.toLocalDateTime());
                }

                log.setNomUtil(rs.getString("nom_util"));
                log.setAction(rs.getString("action"));
                liste.add(log);
            }
        } catch (SQLException e) {
            logger.error("Erreur SQL lister: " + e.getMessage());
        }
        return liste;
    }
}