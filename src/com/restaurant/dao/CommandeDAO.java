package com.restaurant.dao;

import com.restaurant.model.Commande;
import com.restaurant.model.enums.EtatCommande;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {

    private Commande fromResultSet(ResultSet rs) throws SQLException {
        Commande c = new Commande();
        c.setIdCmde(rs.getInt("id_cmde"));
        Timestamp ts = rs.getTimestamp("date");
        if (ts != null) {
            c.setDate(ts.toLocalDateTime());
        } else {
            // If the date is null in the database, it means the column might not have a
            // default.
            // However, the instruction implies adding a default to the DB.
            // For now, we'll keep the existing fallback to current time if DB returns null.
            // The ALTER TABLE statement should be in a database migration script, not here.
            c.setDate(java.time.LocalDateTime.now());
        }
        c.setEtat(EtatCommande.valueOf(rs.getString("etat")));
        c.setTotal(rs.getDouble("total"));
        String nom = rs.getString("nom_util");
        c.setNomUtil(nom != null && !nom.trim().isEmpty() ? nom : "Inconnu");
        return c;
    }

    public int create(Commande commande) throws SQLException {
        String sql = "INSERT INTO COMMANDE (date, etat, total, nom_util) VALUES (NOW(), ?, ?, ?)";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        stmt.setString(1, commande.getEtat().name());
        stmt.setDouble(2, commande.getTotal());
        stmt.setString(3, commande.getNomUtil());

        if (stmt.executeUpdate() > 0) {
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                commande.setIdCmde(rs.getInt(1));
                return rs.getInt(1);
            }
        }
        stmt.close();
        return -1;
    }

    public Commande findById(int id) throws SQLException {
        String sql = "SELECT * FROM COMMANDE WHERE id_cmde = ?";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        Commande c = rs.next() ? fromResultSet(rs) : null;
        rs.close();
        stmt.close();
        return c;
    }

    public List<Commande> findAll() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM COMMANDE ORDER BY date DESC";
        Connection conn = ConnectionDB.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next())
            commandes.add(fromResultSet(rs));
        rs.close();
        stmt.close();
        return commandes;
    }

    public boolean update(Commande commande) throws SQLException {
        String sql = "UPDATE COMMANDE SET etat=?, total=?, nom_util=? WHERE id_cmde=?";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, commande.getEtat().name());
        stmt.setDouble(2, commande.getTotal());
        stmt.setString(3, commande.getNomUtil());
        stmt.setInt(4, commande.getIdCmde());
        boolean ok = stmt.executeUpdate() > 0;
        stmt.close();
        return ok;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM COMMANDE WHERE id_cmde = ?";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        boolean ok = stmt.executeUpdate() > 0;
        stmt.close();
        return ok;
    }

    public int deleteEmptyOrders() throws SQLException {
        String sql = "DELETE FROM COMMANDE WHERE etat = 'EN_COURS' AND total = 0";
        Connection conn = ConnectionDB.getConnection();
        Statement stmt = conn.createStatement();
        int count = stmt.executeUpdate(sql);
        stmt.close();
        return count;
    }

    public List<Commande> findByEtat(EtatCommande etat) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM COMMANDE WHERE etat = ? ORDER BY date DESC";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, etat.name());
        ResultSet rs = stmt.executeQuery();
        while (rs.next())
            commandes.add(fromResultSet(rs));
        rs.close();
        stmt.close();
        return commandes;
    }

    public List<Commande> findByPeriode(LocalDate debut, LocalDate fin) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        // Utiliser DATE() pour extraire uniquement la partie date afin que la
        // comparaison soit correcte avec LocalDate
        String sql = "SELECT * FROM COMMANDE WHERE DATE(date) BETWEEN ? AND ? ORDER BY date DESC";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDate(1, Date.valueOf(debut));
        stmt.setDate(2, Date.valueOf(fin));
        ResultSet rs = stmt.executeQuery();
        while (rs.next())
            commandes.add(fromResultSet(rs));
        rs.close();
        stmt.close();
        return commandes;
    }
}
