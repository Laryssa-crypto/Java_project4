/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant;
import com.restaurant.dao.ConnectionDB;
import javax.swing.JOptionPane;
import com.restaurant.view.ProduitView;
import com.restaurant.view.StockView;
import javax.swing.SwingUtilities;
import java.sql.Connection;

/**
 *
 * @author ASUS
 */



public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Lancement du test d'intégration global...");
        
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Vérification de la connexion
                Connection conn = ConnectionDB.getConnection();
                
                if (conn != null) {
                    System.out.println("✅ Connexion BDD réussie.");

                    // 2. Ouverture de la vue des Produits 
                    ProduitView pView = new ProduitView();
                    pView.setTitle("Gestion des Produits (Laryssa)");
                    pView.setVisible(true);

                    // 3. Ouverture de la vue des Stocks 
                    StockView sView = new StockView();
                    sView.setTitle("Gestion des Stocks (Jojo)");
                    sView.setLocation(pView.getX() + 50, pView.getY() + 50); // Décale un peu la fenêtre
                    sView.setVisible(true);
                    
                } else {
                    System.err.println("❌ Impossible d'ouvrir les vues : la BDD est inaccessible.");
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de l'initialisation : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}