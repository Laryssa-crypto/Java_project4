package com.restaurant.utils;

import com.restaurant.dao.ConnectionDB;
import java.sql.Connection;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseUpdater {
    private static final Logger logger = LogManager.getLogger(DatabaseUpdater.class);

    public static void updateSchema() {
        try (Connection conn = ConnectionDB.getConnection();
                Statement stmt = conn.createStatement()) {

            // Gérer nom_util dans COMMANDE (renommage si nécessaire ou ajout)
            try {
                // Tenter de renommer nomUtil en nom_util si l'ancien nom existe
                stmt.execute("ALTER TABLE COMMANDE CHANGE COLUMN nomUtil nom_util VARCHAR(50) DEFAULT 'Inconnu'");
                logger.info("Colonne nomUtil renommée en nom_util dans COMMANDE");
            } catch (Exception e1) {
                // Si le renommage échoue (ex: nomUtil absent), assurer que nom_util existe
                try {
                    stmt.execute("ALTER TABLE COMMANDE ADD COLUMN nom_util VARCHAR(50) DEFAULT 'Inconnu'");
                    logger.info("Colonne nom_util ajoutée à COMMANDE");
                } catch (Exception e2) {
                    // La colonne existe déjà probablement avec le bon nom
                }
            }

            // Ajouter reference à MVT_STOCK si absent
            try {
                stmt.execute("ALTER TABLE MVT_STOCK ADD COLUMN reference VARCHAR(50)");
                logger.info("Colonne reference ajoutée à MVT_STOCK");
            } catch (Exception e) {
            }

            // Mettre à jour le type de date dans COMMANDE (DATETIME avec valeur par défaut)
            try {
                stmt.execute("ALTER TABLE COMMANDE MODIFY COLUMN date DATETIME DEFAULT CURRENT_TIMESTAMP");
                logger.info("Colonne date de COMMANDE mise à jour avec DATETIME et DEFAULT");
            } catch (Exception e) {
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du schéma: " + e.getMessage());
        }
    }
}
