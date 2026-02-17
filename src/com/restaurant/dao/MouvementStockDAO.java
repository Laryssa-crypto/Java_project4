/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.dao;

import com.restaurant.model.MouvementStock;
import com.restaurant.model.Produit;
import com.restaurant.model.enums.TypeMouvement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des mouvements de stock
 * @author jojo
 */
public class MouvementStockDAO {

    /**
     * Enregistre un nouveau mouvement dans la table MVT_STOCK
     */
    public boolean ajouter(MouvementStock mvt) {
        // Utilisation des noms de colonnes : id_pro, TYPE, qte_stock, DATE, motif
        String sql = "INSERT INTO MVT_STOCK (id_pro, TYPE, qte_stock, DATE, motif) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, mvt.getProduit().getIdPro()); 
            pstmt.setString(2, mvt.getType().toString());
            pstmt.setInt(3, mvt.getQuantite());
            pstmt.setDate(4, java.sql.Date.valueOf(mvt.getDate()));
            pstmt.setString(5, mvt.getMotif());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    // Récupération de l'ID généré pour la colonne id_stock
                    mvt.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout (MVT_STOCK) : " + e.getMessage());
        }
        return false;
    }

    /**
     * Récupère l'historique complet depuis MVT_STOCK
     */
    public List<MouvementStock> listerHistorique() {
        List<MouvementStock> liste = new ArrayList<>();
        // Jointure avec la table PRODUIT de Laryssa pour le nom du produit
        String sql = "SELECT m.*, p.nom_pro FROM MVT_STOCK m " +
                     "JOIN PRODUIT p ON m.id_pro = p.id_pro " +
                     "ORDER BY m.DATE DESC";
        
        try (Connection conn = ConnectionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                MouvementStock mvt = new MouvementStock();
                
                // Mappage avec tes noms de colonnes exacts
                mvt.setId(rs.getInt("id_stock"));
                mvt.setQuantite(rs.getInt("qte_stock"));
                mvt.setType(TypeMouvement.valueOf(rs.getString("TYPE")));
                mvt.setDate(rs.getDate("DATE").toLocalDate());
                mvt.setMotif(rs.getString("motif"));
                
                // Création du produit associé
                Produit p = new Produit();
                p.setIdPro(rs.getInt("id_pro"));
                p.setNomPro(rs.getString("nom_pro")); 
                mvt.setProduit(p);
                
                liste.add(mvt);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la lecture de l'historique : " + e.getMessage());
        }
        return liste;
    }
}