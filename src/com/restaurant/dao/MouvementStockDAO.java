package com.restaurant.dao;

import com.restaurant.model.MouvementStock;
import com.restaurant.model.Produit;
import com.restaurant.model.enums.TypeMouvement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MouvementStockDAO {
    private static final Logger logger = LogManager.getLogger(MouvementStockDAO.class);

    // Enregistre un nouveau mouvement de stock
    public boolean ajouter(MouvementStock mvt) {
        String sql = "INSERT INTO MVT_STOCK (id_pro, TYPE, qte_stock, DATE, motif) VALUES (?, ?, ?, ?, ?)";
        Connection conn = ConnectionDB.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, mvt.getProduit().getIdPro());
            pstmt.setString(2, mvt.getType().toString());
            pstmt.setInt(3, mvt.getQuantite());
            pstmt.setDate(4, Date.valueOf(mvt.getDate()));
            pstmt.setString(5, mvt.getMotif());

            if (pstmt.executeUpdate() > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next())
                    mvt.setId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            logger.warn("Erreur ajout mouvement stock : " + e.getMessage());
        }
        return false;
    }

    // Récupère l'historique chronologique des mouvements
    public List<MouvementStock> listerHistorique() {
        List<MouvementStock> liste = new ArrayList<>();
        String sql = "SELECT m.*, p.nom_pro FROM MVT_STOCK m " +
                "JOIN PRODUIT p ON m.id_pro = p.id_pro " +
                "ORDER BY m.DATE DESC";

        Connection conn = ConnectionDB.getConnection();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MouvementStock mvt = new MouvementStock();
                mvt.setId(rs.getInt("id_stock"));
                mvt.setQuantite(rs.getInt("qte_stock"));
                mvt.setType(TypeMouvement.valueOf(rs.getString("TYPE")));
                mvt.setDate(rs.getDate("DATE").toLocalDate());
                mvt.setMotif(rs.getString("motif"));

                Produit p = new Produit();
                p.setIdPro(rs.getInt("id_pro"));
                p.setNomPro(rs.getString("nom_pro"));
                mvt.setProduit(p);

                liste.add(mvt);
            }
        } catch (SQLException e) {
            logger.warn("Erreur lecture historique stock : " + e.getMessage());
        }
        return liste;
    }
}