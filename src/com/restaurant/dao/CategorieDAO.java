package com.restaurant.dao;

import com.restaurant.model.Categorie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CategorieDAO {
    private static final Logger logger = LogManager.getLogger(CategorieDAO.class);

    // Ajoute une catégorie et récupère son ID généré
    public boolean ajouter(Categorie categorie) {
        String sql = "INSERT INTO CATEGORIE (libelle_cat) VALUES (?)";
        Connection conn = ConnectionDB.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, categorie.getLibelleCat());
            if (pstmt.executeUpdate() > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next())
                    categorie.setIdCat(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            logger.warn("Erreur ajout catégorie : " + e.getMessage());
        }
        return false;
    }

    public boolean modifier(Categorie categorie) {
        String sql = "UPDATE CATEGORIE SET libelle_cat=? WHERE id_cat=?";
        Connection conn = ConnectionDB.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categorie.getLibelleCat());
            pstmt.setInt(2, categorie.getIdCat());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.warn("Erreur modification catégorie : " + e.getMessage());
        }
        return false;
    }

    public boolean supprimer(int idCat) {
        String sql = "DELETE FROM CATEGORIE WHERE id_cat = ?";
        Connection conn = ConnectionDB.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCat);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.warn("Erreur suppression catégorie : " + e.getMessage());
        }
        return false;
    }

    public Categorie getById(int idCat) {
        String sql = "SELECT * FROM CATEGORIE WHERE id_cat = ?";
        Connection conn = ConnectionDB.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCat);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                return new Categorie(rs.getInt("id_cat"), rs.getString("libelle_cat"));
        } catch (SQLException e) {
            logger.warn("Erreur récupération catégorie : " + e.getMessage());
        }
        return null;
    }

    // Récupère toutes les catégories (triées par libellé)
    public List<Categorie> getAll() {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORIE ORDER BY libelle_cat";
        Connection conn = ConnectionDB.getConnection();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next())
                categories.add(new Categorie(rs.getInt("id_cat"), rs.getString("libelle_cat")));
        } catch (SQLException e) {
            logger.warn("Erreur récupération catégories : " + e.getMessage());
        }
        return categories;
    }

    public boolean existe(String libelle) {
        String sql = "SELECT COUNT(*) FROM CATEGORIE WHERE libelle_cat = ?";
        Connection conn = ConnectionDB.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, libelle);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            logger.warn("Erreur vérification catégorie : " + e.getMessage());
        }
        return false;
    }
}