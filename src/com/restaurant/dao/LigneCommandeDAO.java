package com.restaurant.dao;

import com.restaurant.model.LigneCommande;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LigneCommandeDAO {

    private ProduitDAO produitDAO = new ProduitDAO();

    private LigneCommande fromResultSet(ResultSet rs) throws SQLException {
        LigneCommande ligne = new LigneCommande();
        ligne.setIdLig(rs.getInt("id_lig"));
        ligne.setIdCmde(rs.getInt("id_cmde"));
        ligne.setIdPro(rs.getInt("id_pro"));
        ligne.setQteLig(rs.getInt("qte_lig"));
        ligne.setPrixUnit(rs.getDouble("prix_unit"));
        ligne.setMontant(rs.getDouble("montant"));
        ligne.setProduit(produitDAO.getById(ligne.getIdPro()));
        return ligne;
    }

    public int create(LigneCommande ligne) throws SQLException {
        String sql = "INSERT INTO LIG_COMMANDE (id_cmde, id_pro, qte_lig, prix_unit) VALUES (?, ?, ?, ?)";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        stmt.setInt(1, ligne.getIdCmde());
        stmt.setInt(2, ligne.getIdPro());
        stmt.setInt(3, ligne.getQteLig());
        stmt.setDouble(4, ligne.getPrixUnit());

        if (stmt.executeUpdate() > 0) {
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                ligne.setIdLig(rs.getInt(1));
                int id = rs.getInt(1);
                rs.close();
                stmt.close();
                return id;
            }
            rs.close();
        }
        stmt.close();
        return -1;
    }

    public LigneCommande findById(int id) throws SQLException {
        String sql = "SELECT * FROM LIG_COMMANDE WHERE id_lig = ?";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        LigneCommande l = rs.next() ? fromResultSet(rs) : null;
        rs.close();
        stmt.close();
        return l;
    }

    public List<LigneCommande> findByCommande(int idCommande) throws SQLException {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT * FROM LIG_COMMANDE WHERE id_cmde = ? ORDER BY id_lig";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idCommande);
        ResultSet rs = stmt.executeQuery();
        while (rs.next())
            lignes.add(fromResultSet(rs));
        rs.close();
        stmt.close();
        return lignes;
    }

    public boolean update(LigneCommande ligne) throws SQLException {
        String sql = "UPDATE LIG_COMMANDE SET id_pro=?, qte_lig=?, prix_unit=? WHERE id_lig=?";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, ligne.getIdPro());
        stmt.setInt(2, ligne.getQteLig());
        stmt.setDouble(3, ligne.getPrixUnit());
        stmt.setInt(4, ligne.getIdLig());
        boolean ok = stmt.executeUpdate() > 0;
        stmt.close();
        return ok;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM LIG_COMMANDE WHERE id_lig = ?";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        boolean ok = stmt.executeUpdate() > 0;
        stmt.close();
        return ok;
    }

    public boolean deleteByCommande(int idCommande) throws SQLException {
        String sql = "DELETE FROM LIG_COMMANDE WHERE id_cmde = ?";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idCommande);
        boolean ok = stmt.executeUpdate() >= 0;
        stmt.close();
        return ok;
    }

    public double calculerTotalCommande(int idCommande) throws SQLException {
        String sql = "SELECT SUM(montant) FROM LIG_COMMANDE WHERE id_cmde = ?";
        Connection conn = ConnectionDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idCommande);
        ResultSet rs = stmt.executeQuery();
        double total = rs.next() ? rs.getDouble(1) : 0.0;
        rs.close();
        stmt.close();
        return total;
    }
}
