/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.dao;

import com.restaurant.model.Categorie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ASUS
 */


public class CategorieDAO {
    
    /**
     * Ajouter une catégorie
     */
    public boolean ajouter(Categorie categorie) {
        String sql = "INSERT INTO CATEGORIE (libelle_cat) VALUES (?)";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, categorie.getLibelleCat());
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Récupérer l'ID généré
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    categorie.setIdCat(rs.getInt(1));
                }
                System.out.println("✅ Catégorie ajoutée avec succès !");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Modifier une catégorie
     */
    public boolean modifier(Categorie categorie) {
        String sql = "UPDATE CATEGORIE SET libelle_cat = ? WHERE id_cat = ?";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categorie.getLibelleCat());
            pstmt.setInt(2, categorie.getIdCat());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Catégorie modifiée avec succès !");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Supprimer une catégorie
     */
    public boolean supprimer(int idCat) {
        String sql = "DELETE FROM CATEGORIE WHERE id_cat = ?";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCat);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Catégorie supprimée avec succès !");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
            System.err.println("⚠️ Vérifiez qu'aucun produit n'utilise cette catégorie");
        }
        return false;
    }
    
    /**
     * Récupérer une catégorie par son ID
     */
    public Categorie getById(int idCat) {
        String sql = "SELECT * FROM CATEGORIE WHERE id_cat = ?";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCat);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Categorie(
                    rs.getInt("id_cat"),
                    rs.getString("libelle_cat")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Récupérer toutes les catégories
     */
    public List<Categorie> getAll() {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORIE ORDER BY libelle_cat";
        
        try (Connection conn = ConnectionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Categorie cat = new Categorie(
                    rs.getInt("id_cat"),
                    rs.getString("libelle_cat")
                );
                categories.add(cat);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }
        return categories;
    }
    
    /**
     * Vérifier si une catégorie existe déjà (par libellé)
     */
    public boolean existe(String libelle) {
        String sql = "SELECT COUNT(*) FROM CATEGORIE WHERE libelle_cat = ?";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, libelle);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification : " + e.getMessage());
        }
        return false;
    }
}