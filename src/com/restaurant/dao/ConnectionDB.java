package com.restaurant.dao;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectionDB {

    private static final Logger logger = LogManager.getLogger(ConnectionDB.class);

    private static String URL = "jdbc:mysql://localhost:3306/gestion_restaurant";
    private static String USER = "root";
    private static String PASSWORD = "";

    private static Connection connection = null;

    static {
        try (InputStream input = new FileInputStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            URL = prop.getProperty("db.url", URL);
            USER = prop.getProperty("db.user", USER);
            PASSWORD = prop.getProperty("db.password", PASSWORD);
        } catch (Exception ex) {
            logger.warn("Le fichier config.properties n'a pas pu être chargé, utilisation des paramètres par défaut.");
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            logger.error("Driver MySQL introuvable: " + e.getMessage());
        } catch (SQLException e) {
            logger.error("Erreur de connexion a la base de donnees: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }
}