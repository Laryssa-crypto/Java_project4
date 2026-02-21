package com.restaurant.controller;

import com.restaurant.model.Produit;
import com.restaurant.service.StatistiqueService;
import com.restaurant.service.StatistiqueService.ProduitVendu;
import com.restaurant.service.StatistiqueService.StatistiquesGenerales;
import com.restaurant.view.StatistiqueView;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StatistiqueController {

    private StatistiqueView statistiqueView;
    private StatistiqueService statistiqueService;

    public StatistiqueController() {
        com.restaurant.dao.CommandeDAO cDao = new com.restaurant.dao.CommandeDAO();
        com.restaurant.dao.LigneCommandeDAO lDao = new com.restaurant.dao.LigneCommandeDAO();
        com.restaurant.dao.ProduitDAO pDao = new com.restaurant.dao.ProduitDAO();
        this.statistiqueService = new StatistiqueService(cDao, lDao, pDao);
    }

    public void setView(StatistiqueView view) {
        this.statistiqueView = view;
    }

    // Rafraîchit toutes les statistiques du tableau de bord
    public void rafraichirToutesStatistiques() {
        javax.swing.SwingWorker<Void, Void> worker = new javax.swing.SwingWorker<Void, Void>() {
            StatistiquesGenerales stats;
            List<Produit> rupture;
            List<Produit> sousSeuil;
            List<ProduitVendu> topQte;
            List<ProduitVendu> topMontant;

            @Override
            protected Void doInBackground() throws Exception {
                LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
                LocalDate finMois = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
                stats = statistiqueService.getStatistiquesGenerales();
                rupture = statistiqueService.getProduitsEnRupture();
                sousSeuil = statistiqueService.getProduitsSousSeuilAlerte();
                topQte = statistiqueService.getTopProduitsQuantite(debutMois, finMois, 10);
                topMontant = statistiqueService.getTopProduitsMontant(debutMois, finMois, 10);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (statistiqueView == null)
                        return;
                    statistiqueView.afficherStatistiquesGenerales(stats);
                    statistiqueView.afficherProduitsRupture(rupture);
                    statistiqueView.afficherProduitsSousSeuil(sousSeuil);
                    statistiqueView.afficherTopProduitsQuantite(topQte);
                    statistiqueView.afficherTopProduitsMontant(topMontant);
                } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
                    afficherErreur("Erreur chargement statistiques : " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void afficherChiffreAffairesJour(LocalDate date) {
        try {
            if (statistiqueView != null)
                statistiqueView.afficherChiffreAffairesJour(date, statistiqueService.getChiffreAffairesParJour(date));
        } catch (SQLException e) {
            afficherErreur("Erreur lors du calcul du CA journalier : " + e.getMessage());
        }
    }

    public void afficherChiffreAffairesPeriode(LocalDate debut, LocalDate fin) {
        try {
            if (statistiqueView != null)
                statistiqueView.afficherChiffreAffairesPeriode(debut, fin,
                        statistiqueService.getChiffreAffairesPeriode(debut, fin),
                        statistiqueService.getChiffreAffairesPeriodeJournalier(debut, fin));
        } catch (SQLException e) {
            afficherErreur("Erreur lors du calcul du CA sur la période : " + e.getMessage());
        }
    }

    public void afficherTopProduitsQuantitePeriode(LocalDate debut, LocalDate fin, int limite) {
        try {
            if (statistiqueView != null)
                statistiqueView.afficherTopProduitsQuantitePeriode(debut, fin,
                        statistiqueService.getTopProduitsQuantite(debut, fin, limite));
        } catch (SQLException e) {
            afficherErreur("Erreur lors du chargement des top produits : " + e.getMessage());
        }
    }

    public void afficherTopProduitsMontantPeriode(LocalDate debut, LocalDate fin, int limite) {
        try {
            if (statistiqueView != null)
                statistiqueView.afficherTopProduitsMontantPeriode(debut, fin,
                        statistiqueService.getTopProduitsMontant(debut, fin, limite));
        } catch (SQLException e) {
            afficherErreur("Erreur chargement top produits montant : " + e.getMessage());
        }
    }

    public void rafraichirAlertesEtRuptures() {
        try {
            List<Produit> rupture = statistiqueService.getProduitsEnRupture();
            List<Produit> sousSeuil = statistiqueService.getProduitsSousSeuilAlerte();
            if (statistiqueView != null) {
                statistiqueView.afficherProduitsRupture(rupture);
                statistiqueView.afficherProduitsSousSeuil(sousSeuil);
            }
        } catch (SQLException e) {
            afficherErreur("Erreur lors du rafraîchissement des stocks : " + e.getMessage());
        }
    }

    public void afficherProduitsRupture() {
        rafraichirAlertesEtRuptures();
    }

    public void afficherProduitsSousSeuil() {
        rafraichirAlertesEtRuptures();
    }

    public void afficherStatistiquesPersonnalisees(LocalDate debut, LocalDate fin) {
        try {
            double ca = statistiqueService.getChiffreAffairesPeriode(debut, fin);
            List<ProduitVendu> top = statistiqueService.getTopProduitsQuantite(debut, fin, 5);
            if (statistiqueView != null)
                statistiqueView.afficherStatistiquesPersonnalisees(debut, fin, ca, top);
        } catch (SQLException e) {
            afficherErreur("Erreur lors du chargement des statistiques personnalisées : " + e.getMessage());
        }
    }

    public void afficherHeuresDePointe(LocalDate debut, LocalDate fin) {
        try {
            if (statistiqueView != null) {
                statistiqueView.afficherHeuresDePointe(debut, fin, statistiqueService.getHeuresDePointe(debut, fin));
            }
        } catch (SQLException e) {
            afficherErreur("Erreur chargement heures de pointe : " + e.getMessage());
        }
    }

    public void afficherVentesParCaissier(LocalDate debut, LocalDate fin) {
        try {
            if (statistiqueView != null) {
                statistiqueView.afficherVentesParCaissier(debut, fin,
                        statistiqueService.getVentesParCaissier(debut, fin));
            }
        } catch (SQLException e) {
            afficherErreur("Erreur chargement ventes par caissier : " + e.getMessage());
        }
    }

    private void afficherErreur(String msg) {
        if (statistiqueView != null)
            statistiqueView.afficherErreur(msg);
    }

    // Imprime le résumé des statistiques globales
    public void imprimerStatistiques() {
        try {
            StatistiquesGenerales stats = statistiqueService.getStatistiquesGenerales();
            com.restaurant.service.PrintService.imprimerStatistiques(stats);
        } catch (SQLException e) {
            afficherErreur("Erreur lors de l'impression : " + e.getMessage());
        }
    }

    // Exporte les statistiques actuelles au format CSV
    public void exporterCSVStatistiques(String destPath) {
        try {
            LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
            LocalDate finMois = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            StatistiquesGenerales stats = statistiqueService.getStatistiquesGenerales();
            List<Produit> rupture = statistiqueService.getProduitsEnRupture();
            List<Produit> sousSeuil = statistiqueService.getProduitsSousSeuilAlerte();
            List<ProduitVendu> topQte = statistiqueService.getTopProduitsQuantite(debutMois, finMois, 10);
            List<ProduitVendu> topMontant = statistiqueService.getTopProduitsMontant(debutMois, finMois, 10);

            boolean ok = com.restaurant.service.CsvService.exporterStatistiques(
                    destPath, stats, topQte, topMontant, rupture, sousSeuil);
            if (statistiqueView != null)
                statistiqueView.afficherMessage(ok
                        ? "Statistiques exportées en CSV avec succès !"
                        : "Erreur lors de l'export CSV.");

        } catch (Exception e) {
            afficherErreur("Erreur lors de l'exportation CSV : " + e.getMessage());
        }
    }

    public void exporterPDFStatistiques(String destPath) {
        try {
            LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
            LocalDate finMois = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            StatistiquesGenerales stats = statistiqueService.getStatistiquesGenerales();
            List<Produit> rupture = statistiqueService.getProduitsEnRupture();
            List<Produit> sousSeuil = statistiqueService.getProduitsSousSeuilAlerte();
            List<ProduitVendu> topQte = statistiqueService.getTopProduitsQuantite(debutMois, finMois, 10);
            List<ProduitVendu> topMontant = statistiqueService.getTopProduitsMontant(debutMois, finMois, 10);
            com.restaurant.service.PdfExportService.exporterStatistiquesPDF(
                    destPath, stats, topQte, topMontant, rupture, sousSeuil);
            if (statistiqueView != null)
                statistiqueView.afficherMessage("Rapport PDF exporté avec succès !");
        } catch (Exception e) {
            afficherErreur("Erreur export PDF : " + e.getMessage());
        }
    }

    public void imprimerAncienneCommande(int id) {
        try {
            com.restaurant.dao.CommandeDAO cDao = new com.restaurant.dao.CommandeDAO();
            com.restaurant.dao.LigneCommandeDAO lDao = new com.restaurant.dao.LigneCommandeDAO();
            com.restaurant.dao.ProduitDAO pDao = new com.restaurant.dao.ProduitDAO();
            com.restaurant.dao.MouvementStockDAO mDao = new com.restaurant.dao.MouvementStockDAO();
            com.restaurant.service.StockService sService = new com.restaurant.service.StockService(pDao, mDao);
            com.restaurant.service.CommandeService cmdService = new com.restaurant.service.CommandeService(cDao, lDao,
                    pDao, sService);

            com.restaurant.model.Commande cmd = cmdService.getCommande(id);
            if (cmd == null) {
                afficherErreur("Commande introuvable.");
                return;
            }
            java.util.List<com.restaurant.model.LigneCommande> lignes = cmdService.getLignesCommande(id);
            com.restaurant.service.PrintService.imprimerRecuClient(cmd, lignes);
        } catch (SQLException e) {
            afficherErreur("Erreur lors de l'impression : " + e.getMessage());
        }
    }
}
