package com.restaurant.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseBackupService {

    private static final Logger logger = LogManager.getLogger(DatabaseBackupService.class);

    private String url = "jdbc:mysql://localhost:3306/gestion_restaurant";
    private String user = "root";
    private String password = "";

    public DatabaseBackupService() {
        chargerConfiguration();
    }

    private void chargerConfiguration() {
        try (InputStream input = new FileInputStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            url = prop.getProperty("db.url", url);
            user = prop.getProperty("db.user", user);
            password = prop.getProperty("db.password", password);
        } catch (Exception ex) {
            logger.warn("Impossible de charger config.properties pour backup. Utilisation des valeurs par défaut.");
        }
    }

    private String extraireNomBaseDeDonnees(String dbUrl) {
        int indexSlash = dbUrl.lastIndexOf("/");
        if (indexSlash != -1) {
            String db = dbUrl.substring(indexSlash + 1);
            int indexParam = db.indexOf("?");
            if (indexParam != -1) {
                return db.substring(0, indexParam);
            }
            return db;
        }
        return "gestion_restaurant";
    }

    // Exporte la base de données vers un fichier SQL (utilise mysqldump)
    public boolean sauvegarderBaseDeDonnees(String cheminDestination) throws Exception {
        String dbName = extraireNomBaseDeDonnees(url);
        List<String> command = new ArrayList<>();

        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        if (isWindows) {
            command.add("cmd.exe");
            command.add("/c");
        } else {
            command.add("bash");
            command.add("-c");
        }

        String dumpCmd = String.format("mysqldump -u %s %s %s -r \"%s\"",
                user,
                (password != null && !password.isEmpty()) ? "-p" + password : "",
                dbName,
                cheminDestination);

        if (!isWindows) {
            command.add(dumpCmd);
        } else {
            command.add(dumpCmd);
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder erreur = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                erreur.append(line).append("\n");
            }
            throw new Exception("Erreur de sauvegarde (Code: " + exitCode + ") : " + erreur.toString());
        }

        return true;
    }

    // Restaure la base de données depuis un fichier SQL (utilise mysql)
    public boolean restaurerBaseDeDonnees(String cheminSourceSql) throws Exception {
        String dbName = extraireNomBaseDeDonnees(url);
        List<String> command = new ArrayList<>();

        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        if (isWindows) {
            command.add("cmd.exe");
            command.add("/c");
        } else {
            command.add("bash");
            command.add("-c");
        }

        String importCmd = String.format("mysql -u %s %s %s < \"%s\"",
                user,
                (password != null && !password.isEmpty()) ? "-p" + password : "",
                dbName,
                cheminSourceSql);

        if (!isWindows) {
            command.add(importCmd);
        } else {
            command.add(importCmd);
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder erreur = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                erreur.append(line).append("\n");
            }
            throw new Exception("Erreur de restauration (Code: " + exitCode + ") : " + erreur.toString());
        }

        return true;
    }
}
