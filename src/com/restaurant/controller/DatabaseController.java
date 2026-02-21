package com.restaurant.controller;

import com.restaurant.service.DatabaseBackupService;
import com.restaurant.view.DatabaseView;
import javax.swing.SwingWorker;

public class DatabaseController {

    private DatabaseView databaseView;
    private DatabaseBackupService databaseBackupService;

    public DatabaseController() {
        this.databaseBackupService = new DatabaseBackupService();
    }

    public void setView(DatabaseView view) {
        this.databaseView = view;
    }

    public void sauvegarderBaseDeDonnees(String outputFilePath) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                databaseBackupService.sauvegarderBaseDeDonnees(outputFilePath);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (databaseView != null) {
                        databaseView.afficherMessage("Sauvegarde de l'historique réussie vers:\n" + outputFilePath);
                    }
                } catch (Exception e) {
                    if (databaseView != null) {
                        databaseView.afficherErreur("Échec de la sauvegarde. Détails : " + e.getMessage());
                    }
                }
            }
        };
        worker.execute();
    }

    public void restaurerBaseDeDonnees(String sqlFilePath) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                databaseBackupService.restaurerBaseDeDonnees(sqlFilePath);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (databaseView != null) {
                        databaseView.afficherMessage("Restauration du système réussie à partir de:\n" + sqlFilePath
                                + "\nVeuillez relancer l'application.");
                    }
                } catch (Exception e) {
                    if (databaseView != null) {
                        databaseView.afficherErreur("Échec de la restauration. Détails : " + e.getMessage());
                    }
                }
            }
        };
        worker.execute();
    }
}
