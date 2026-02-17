package com.restaurant.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;




public class ConnectionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_restaurant";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Vide par défaut avec XAMPP
    
    private static Connection connection = null;
    
    /**
     * Établit une connexion à la base de données
     * @return 
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion à la base de données réussie !");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données : " + e.getMessage());
        }
        return connection;
    }
    
    /**
     * Ferme la connexion à la base de données
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la fermeture : " + e.getMessage());
        }
    }
    
    /**
     * Test de connexion
     * @param args
     */
    public static void main(String[] args) {
        Connection conn = ConnectionDB.getConnection();
        if (conn != null) {
            System.out.println("🎉 Test de connexion réussi !");
            closeConnection();
        } else {
            System.out.println("❌ Échec du test de connexion");
        }
    }
}