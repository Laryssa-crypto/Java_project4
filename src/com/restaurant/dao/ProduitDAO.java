/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.dao;

import com.restaurant.model.Produit;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ASUS
 */



public class ProduitDAO {
    
    /**
     * Ajouter un produit
     */
    public boolean ajouter(Produit produit) {
        String sql = "INSERT INTO PRODUIT (nom_pro, id_cat, prix_vente, stock_actu, seuil_alerte) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, produit.getNomPro());
            pstmt.setInt(2, produit.getIdCat());
            pstmt.setDouble(3, produit.getPrixVente());
            pstmt.setInt(4, produit.getStockActu());
            pstmt.setInt(5, produit.getSeuilAlerte());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    produit.setIdPro(rs.getInt(1));
                }
                System.out.println("✅ Produit ajouté avec succès !");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Modifier un produit
     */
    public boolean modifier(Produit produit) {
        String sql = "UPDATE PRODUIT SET nom_pro = ?, id_cat = ?, prix_vente = ?, " +
                     "stock_actu = ?, seuil_alerte = ? WHERE id_pro = ?";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, produit.getNomPro());
            pstmt.setInt(2, produit.getIdCat());
            pstmt.setDouble(3, produit.getPrixVente());
            pstmt.setInt(4, produit.getStockActu());
            pstmt.setInt(5, produit.getSeuilAlerte());
            pstmt.setInt(6, produit.getIdPro());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Produit modifié avec succès !");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Supprimer un produit
     */
    public boolean supprimer(int idPro) {
        String sql = "DELETE FROM PRODUIT WHERE id_pro = ?";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPro);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Produit supprimé avec succès !");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Récupérer un produit par son ID
     */
    public Produit getById(int idPro) {
        String sql = "SELECT p.*, c.libelle_cat FROM PRODUIT p " +
                     "JOIN CATEGORIE c ON p.id_cat = c.id_cat " +
                     "WHERE p.id_pro = ?";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPro);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Produit produit = new Produit(
                    rs.getInt("id_pro"),
                    rs.getString("nom_pro"),
                    rs.getInt("id_cat"),
                    rs.getDouble("prix_vente"),
                    rs.getInt("stock_actu"),
                    rs.getInt("seuil_alerte")
                );
                produit.setNomCategorie(rs.getString("libelle_cat"));
                return produit;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Récupérer tous les produits
     */
    public List<Produit> getAll() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle_cat FROM PRODUIT p " +
                     "JOIN CATEGORIE c ON p.id_cat = c.id_cat " +
                     "ORDER BY p.nom_pro";
        
        try (Connection conn = ConnectionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Produit produit = new Produit(
                    rs.getInt("id_pro"),
                    rs.getString("nom_pro"),
                    rs.getInt("id_cat"),
                    rs.getDouble("prix_vente"),
                    rs.getInt("stock_actu"),
                    rs.getInt("seuil_alerte")
                );
                produit.setNomCategorie(rs.getString("libelle_cat"));
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }
        return produits;
    }
    
    /**
     * Rechercher des produits par nom
     */
    public List<Produit> rechercherParNom(String nom) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle_cat FROM PRODUIT p " +
                     "JOIN CATEGORIE c ON p.id_cat = c.id_cat " +
                     "WHERE p.nom_pro LIKE ? ORDER BY p.nom_pro";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nom + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Produit produit = new Produit(
                    rs.getInt("id_pro"),
                    rs.getString("nom_pro"),
                    rs.getInt("id_cat"),
                    rs.getDouble("prix_vente"),
                    rs.getInt("stock_actu"),
                    rs.getInt("seuil_alerte")
                );
                produit.setNomCategorie(rs.getString("libelle_cat"));
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche : " + e.getMessage());
        }
        return produits;
    }
    
    /**
     * Récupérer les produits par catégorie
     */
    public List<Produit> getByCategorie(int idCat) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle_cat FROM PRODUIT p " +
                     "JOIN CATEGORIE c ON p.id_cat = c.id_cat " +
                     "WHERE p.id_cat = ? ORDER BY p.nom_pro";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCat);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Produit produit = new Produit(
                    rs.getInt("id_pro"),
                    rs.getString("nom_pro"),
                    rs.getInt("id_cat"),
                    rs.getDouble("prix_vente"),
                    rs.getInt("stock_actu"),
                    rs.getInt("seuil_alerte")
                );
                produit.setNomCategorie(rs.getString("libelle_cat"));
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }
        return produits;
    }
    
    /**
     * Récupérer les produits sous le seuil d'alerte
     */
    public List<Produit> getProduitsSousSeuilAlerte() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle_cat FROM PRODUIT p " +
                     "JOIN CATEGORIE c ON p.id_cat = c.id_cat " +
                     "WHERE p.stock_actu < p.seuil_alerte " +
                     "ORDER BY p.stock_actu";
        
        try (Connection conn = ConnectionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Produit produit = new Produit(
                    rs.getInt("id_pro"),
                    rs.getString("nom_pro"),
                    rs.getInt("id_cat"),
                    rs.getDouble("prix_vente"),
                    rs.getInt("stock_actu"),
                    rs.getInt("seuil_alerte")
                );
                produit.setNomCategorie(rs.getString("libelle_cat"));
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }
        return produits;
    }
    
    /**
     * Mettre à jour le stock d'un produit
     */
    public boolean mettreAJourStock(int idPro, int nouvelleQuantite) {
        String sql = "UPDATE PRODUIT SET stock_actu = ? WHERE id_pro = ?";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nouvelleQuantite);
            pstmt.setInt(2, idPro);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Stock mis à jour avec succès !");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du stock : " + e.getMessage());
        }
        return false;
    }
}