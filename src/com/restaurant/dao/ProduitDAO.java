package com.restaurant.dao;

import com.restaurant.model.Produit;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProduitDAO {
    private static final Logger logger = LogManager.getLogger(ProduitDAO.class);

    private static final String SELECT_BASE = "SELECT p.*, c.libelle_cat FROM PRODUIT p " +
            "JOIN CATEGORIE c ON p.id_cat = c.id_cat ";

    private Produit fromResultSet(ResultSet rs) throws SQLException {
        Produit p = new Produit(
                rs.getInt("id_pro"),
                rs.getString("nom_pro"),
                rs.getInt("id_cat"),
                rs.getDouble("prix_vente"),
                rs.getInt("stock_actu"),
                rs.getInt("seuil_alerte"));
        p.setNomCategorie(rs.getString("libelle_cat"));
        return p;
    }

    public boolean ajouter(Produit produit) {
        String sql = "INSERT INTO PRODUIT (nom_pro, id_cat, prix_vente, stock_actu, seuil_alerte) VALUES (?, ?, ?, ?, ?)";
        Connection conn = ConnectionDB.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, produit.getNomPro());
            pstmt.setInt(2, produit.getIdCat());
            pstmt.setDouble(3, produit.getPrixVente());
            pstmt.setInt(4, produit.getStockActu());
            pstmt.setInt(5, produit.getSeuilAlerte());

            if (pstmt.executeUpdate() > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next())
                    produit.setIdPro(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            logger.warn("Erreur ajout produit : " + e.getMessage());
        }
        return false;
    }

    public boolean modifier(Produit produit) {
        String sql = "UPDATE PRODUIT SET nom_pro=?, id_cat=?, prix_vente=?, stock_actu=?, seuil_alerte=? WHERE id_pro=?";
        Connection conn = ConnectionDB.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, produit.getNomPro());
            pstmt.setInt(2, produit.getIdCat());
            pstmt.setDouble(3, produit.getPrixVente());
            pstmt.setInt(4, produit.getStockActu());
            pstmt.setInt(5, produit.getSeuilAlerte());
            pstmt.setInt(6, produit.getIdPro());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.warn("Erreur modification produit : " + e.getMessage());
        }
        return false;
    }

    /**
     * Supprime un produit par son ID.
     * 
     * @throws ProduitLieACommandeException si le produit est lié à des commandes
     *                                      (MySQL error 1451)
     */
    public boolean deleteProduit(int idPro) throws ProduitLieACommandeException {
        String sql = "DELETE FROM PRODUIT WHERE id_pro = ?";
        Connection conn = ConnectionDB.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPro);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new ProduitLieACommandeException(idPro);
            }
            logger.warn("Erreur suppression produit : " + e.getMessage());
        }
        return false;
    }

    public Produit getById(int idPro) {
        String sql = SELECT_BASE + "WHERE p.id_pro = ?";
        Connection conn = ConnectionDB.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPro);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                return fromResultSet(rs);
        } catch (SQLException e) {
            logger.warn("Erreur récupération produit : " + e.getMessage());
        }
        return null;
    }

    public List<Produit> getAll() {
        List<Produit> produits = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY p.nom_pro";
        Connection conn = ConnectionDB.getConnection();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next())
                produits.add(fromResultSet(rs));
        } catch (SQLException e) {
            logger.warn("Erreur récupération produits : " + e.getMessage());
        }
        return produits;
    }

    public List<Produit> rechercherParNom(String nom) {
        List<Produit> produits = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE p.nom_pro LIKE ? ORDER BY p.nom_pro";
        Connection conn = ConnectionDB.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nom + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                produits.add(fromResultSet(rs));
        } catch (SQLException e) {
            logger.warn("Erreur recherche produit : " + e.getMessage());
        }
        return produits;
    }

    public List<Produit> getByCategorie(int idCat) {
        List<Produit> produits = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE p.id_cat = ? ORDER BY p.nom_pro";
        Connection conn = ConnectionDB.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCat);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                produits.add(fromResultSet(rs));
        } catch (SQLException e) {
            logger.warn("Erreur récupération par catégorie : " + e.getMessage());
        }
        return produits;
    }

    public List<Produit> getProduitsSousSeuilAlerte() {
        List<Produit> produits = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE p.stock_actu < p.seuil_alerte ORDER BY p.stock_actu";
        Connection conn = ConnectionDB.getConnection();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next())
                produits.add(fromResultSet(rs));
        } catch (SQLException e) {
            logger.warn("Erreur produits sous seuil : " + e.getMessage());
        }
        return produits;
    }

    public boolean mettreAJourStock(int idPro, int nouvelleQuantite) {
        String sql = "UPDATE PRODUIT SET stock_actu = ? WHERE id_pro = ?";
        Connection conn = ConnectionDB.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nouvelleQuantite);
            pstmt.setInt(2, idPro);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.warn("Erreur mise à jour stock : " + e.getMessage());
        }
        return false;
    }

    public static class ProduitLieACommandeException extends Exception {
        private final int idProduit;

        public ProduitLieACommandeException(int idProduit) {
            super("Le produit #" + idProduit + " est lié à des commandes existantes.");
            this.idProduit = idProduit;
        }

        public int getIdProduit() {
            return idProduit;
        }
    }
}