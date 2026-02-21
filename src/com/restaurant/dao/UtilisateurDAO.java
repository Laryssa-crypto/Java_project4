package com.restaurant.dao;

import com.restaurant.model.Utilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.restaurant.model.enums.Role;

public class UtilisateurDAO {

    private Utilisateur fromResultSet(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setIdUtil(rs.getInt("id_util"));
        u.setNomUtil(rs.getString("nom_util"));
        u.setMdp(rs.getString("mdp"));
        String roleStr = rs.getString("role");
        if (roleStr != null) {
            try {
                u.setRole(Role.valueOf(roleStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                u.setRole(Role.CAISSIER);
            }
        } else {
            u.setRole(Role.CAISSIER);
        }
        return u;
    }

    // Crée un nouvel utilisateur en base
    public int create(Utilisateur utilisateur) throws SQLException {
        String sql = "INSERT INTO UTILISATEUR (nom_util, mdp, role) VALUES (?, ?, ?)";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        stmt.setString(1, utilisateur.getNomUtil());
        stmt.setString(2, utilisateur.getMdp());
        stmt.setString(3, utilisateur.getRole() != null ? utilisateur.getRole().name() : Role.CAISSIER.name());

        if (stmt.executeUpdate() > 0) {
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                utilisateur.setIdUtil(id);
                rs.close();
                stmt.close();
                return id;
            }
            rs.close();
        }
        stmt.close();
        return -1;
    }

    public Utilisateur findById(int id) throws SQLException {
        String sql = "SELECT * FROM UTILISATEUR WHERE id_util = ?";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        Utilisateur u = rs.next() ? fromResultSet(rs) : null;
        rs.close();
        stmt.close();
        return u;
    }

    public Utilisateur findByNom(String nomUtil) throws SQLException {
        String sql = "SELECT * FROM UTILISATEUR WHERE nom_util = ?";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, nomUtil);
        ResultSet rs = stmt.executeQuery();
        Utilisateur u = rs.next() ? fromResultSet(rs) : null;
        rs.close();
        stmt.close();
        return u;
    }

    // Vérifie les identifiants pour l'authentification
    public Utilisateur authenticate(String nomUtil, String mdp) throws SQLException {
        String sql = "SELECT * FROM UTILISATEUR WHERE nom_util = ? AND mdp = ?";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, nomUtil);
        stmt.setString(2, mdp);
        ResultSet rs = stmt.executeQuery();
        Utilisateur u = rs.next() ? fromResultSet(rs) : null;
        rs.close();
        stmt.close();
        return u;
    }

    public List<Utilisateur> findAll() throws SQLException {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT * FROM UTILISATEUR ORDER BY nom_util";
        Connection conn = ConnectionDB.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next())
            liste.add(fromResultSet(rs));
        rs.close();
        stmt.close();
        return liste;
    }

    public boolean update(Utilisateur utilisateur) throws SQLException {
        String sql = "UPDATE UTILISATEUR SET nom_util=?, mdp=?, role=? WHERE id_util=?";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, utilisateur.getNomUtil());
        stmt.setString(2, utilisateur.getMdp());
        stmt.setString(3, utilisateur.getRole() != null ? utilisateur.getRole().name() : Role.CAISSIER.name());
        stmt.setInt(4, utilisateur.getIdUtil());
        boolean ok = stmt.executeUpdate() > 0;
        stmt.close();
        return ok;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM UTILISATEUR WHERE id_util = ?";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        boolean ok = stmt.executeUpdate() > 0;
        stmt.close();
        return ok;
    }

    public boolean existsByNom(String nomUtil) throws SQLException {
        String sql = "SELECT COUNT(*) FROM UTILISATEUR WHERE nom_util = ?";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, nomUtil);
        ResultSet rs = stmt.executeQuery();
        boolean exists = rs.next() && rs.getInt(1) > 0;
        rs.close();
        stmt.close();
        return exists;
    }
}
