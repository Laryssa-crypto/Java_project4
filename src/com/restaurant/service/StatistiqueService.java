package com.restaurant.service;

import com.restaurant.dao.CommandeDAO;
import com.restaurant.dao.LigneCommandeDAO;
import com.restaurant.dao.ProduitDAO;
import com.restaurant.model.Commande;
import com.restaurant.model.LigneCommande;
import com.restaurant.model.Produit;
import com.restaurant.model.enums.EtatCommande;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatistiqueService {

    private CommandeDAO commandeDAO;
    private LigneCommandeDAO ligneCommandeDAO;
    private ProduitDAO produitDAO;

    public StatistiqueService(CommandeDAO commandeDAO, LigneCommandeDAO ligneCommandeDAO, ProduitDAO produitDAO) {
        this.commandeDAO = commandeDAO;
        this.ligneCommandeDAO = ligneCommandeDAO;
        this.produitDAO = produitDAO;
    }

    public double getChiffreAffairesParJour(LocalDate date) throws SQLException {
        return getChiffreAffairesPeriode(date, date);
    }

    public double getChiffreAffairesPeriode(LocalDate debut, LocalDate fin) throws SQLException {
        double ca = 0.0;
        for (Commande c : commandeDAO.findByPeriode(debut, fin)) {
            if (c.getEtat() == EtatCommande.VALIDEE)
                ca += c.getTotal();
        }
        return ca;
    }

    public Map<LocalDate, Double> getChiffreAffairesPeriodeJournalier(LocalDate debut, LocalDate fin)
            throws SQLException {
        Map<LocalDate, Double> map = new java.util.TreeMap<>();
        LocalDate current = debut;
        while (!current.isAfter(fin)) {
            map.put(current, 0.0);
            current = current.plusDays(1);
        }
        for (Commande c : commandeDAO.findByPeriode(debut, fin)) {
            if (c.getEtat() == EtatCommande.VALIDEE) {
                LocalDate date = c.getDate().toLocalDate();
                map.put(date, map.getOrDefault(date, 0.0) + c.getTotal());
            }
        }
        return map;
    }

    public List<ProduitVendu> getTopProduitsQuantite(LocalDate debut, LocalDate fin, int limite) throws SQLException {
        Map<Integer, ProduitVendu> map = new HashMap<>();

        for (Commande c : commandeDAO.findByPeriode(debut, fin)) {
            if (c.getEtat() != EtatCommande.VALIDEE)
                continue;
            for (LigneCommande ligne : ligneCommandeDAO.findByCommande(c.getIdCmde())) {
                ProduitVendu pv = map.computeIfAbsent(ligne.getIdPro(), id -> {
                    Produit p = produitDAO.getById(id);
                    return p != null ? new ProduitVendu(p) : null;
                });
                if (pv != null) {
                    pv.ajouterQuantite(ligne.getQteLig());
                    pv.ajouterMontant(ligne.getMontant());
                }
            }
        }

        List<ProduitVendu> result = new ArrayList<>(map.values());
        result.sort((a, b) -> Integer.compare(b.getQuantiteTotale(), a.getQuantiteTotale()));
        return limite > 0 && result.size() > limite ? result.subList(0, limite) : result;
    }

    public List<ProduitVendu> getTopProduitsMontant(LocalDate debut, LocalDate fin, int limite) throws SQLException {
        List<ProduitVendu> result = getTopProduitsQuantite(debut, fin, 0);
        result.sort((a, b) -> Double.compare(b.getMontantTotal(), a.getMontantTotal()));
        return limite > 0 && result.size() > limite ? result.subList(0, limite) : result;
    }

    public Map<Integer, Integer> getHeuresDePointe(LocalDate debut, LocalDate fin) throws SQLException {
        Map<Integer, Integer> heures = new HashMap<>();
        for (Commande c : commandeDAO.findByPeriode(debut, fin)) {
            if (c.getEtat() == EtatCommande.VALIDEE) {
                int heure = c.getDate().getHour();
                heures.put(heure, heures.getOrDefault(heure, 0) + 1);
            }
        }
        return heures;
    }

    public Map<String, Double> getVentesParCaissier(LocalDate debut, LocalDate fin) throws SQLException {
        Map<String, Double> ventes = new HashMap<>();
        for (Commande c : commandeDAO.findByPeriode(debut, fin)) {
            if (c.getEtat() == EtatCommande.VALIDEE) {
                String dbNom = c.getNomUtil();
                String nom = (dbNom != null && !dbNom.trim().isEmpty()) ? dbNom : "Inconnu";
                ventes.put(nom, ventes.getOrDefault(nom, 0.0) + c.getTotal());
            }
        }
        return ventes;
    }

    public List<Produit> getProduitsEnRupture() throws SQLException {
        List<Produit> rupture = new ArrayList<>();
        for (Produit p : produitDAO.getAll()) {
            if (p.getStockActu() <= 0)
                rupture.add(p);
        }
        return rupture;
    }

    public List<Produit> getProduitsSousSeuilAlerte() throws SQLException {
        List<Produit> sousSeuil = new ArrayList<>();
        for (Produit p : produitDAO.getAll()) {
            if (p.getStockActu() > 0 && p.getStockActu() <= p.getSeuilAlerte()) {
                sousSeuil.add(p);
            }
        }
        return sousSeuil;
    }

    public StatistiquesGenerales getStatistiquesGenerales() throws SQLException {
        StatistiquesGenerales stats = new StatistiquesGenerales();
        LocalDate today = LocalDate.now();

        stats.setNbProduits(produitDAO.getAll().size());
        stats.setNbProduitsRupture(getProduitsEnRupture().size());
        stats.setNbProduitsSousSeuil(getProduitsSousSeuilAlerte().size());

        // Compter uniquement les commandes VALIDÉES pour le total journalier
        int validToday = 0;
        int encoursToday = 0;
        for (Commande c : commandeDAO.findByPeriode(today, today)) {
            if (c.getEtat() == EtatCommande.VALIDEE) {
                validToday++;
            } else if (c.getEtat() == EtatCommande.EN_COURS && c.getTotal() > 0) {
                // On ne compte que les commandes "En cours" qui ont au moins un article (total
                // > 0)
                encoursToday++;
            }
        }

        stats.setNbCommandesJour(validToday);
        stats.setCaJour(getChiffreAffairesParJour(today));
        stats.setNbCommandesEnCours(encoursToday);

        return stats;
    }

    public static class ProduitVendu {
        private Produit produit;
        private int quantiteTotale;
        private double montantTotal;

        public ProduitVendu(Produit produit) {
            this.produit = produit;
        }

        public void ajouterQuantite(int q) {
            this.quantiteTotale += q;
        }

        public void ajouterMontant(double m) {
            this.montantTotal += m;
        }

        public Produit getProduit() {
            return produit;
        }

        public int getQuantiteTotale() {
            return quantiteTotale;
        }

        public double getMontantTotal() {
            return montantTotal;
        }

        @Override
        public String toString() {
            return produit.getNomPro() + " - " + quantiteTotale + " unités - " + montantTotal + "€";
        }
    }

    public static class StatistiquesGenerales {
        private int nbProduits, nbProduitsRupture, nbProduitsSousSeuil, nbCommandesJour, nbCommandesEnCours;
        private double caJour;

        public int getNbProduits() {
            return nbProduits;
        }

        public void setNbProduits(int v) {
            this.nbProduits = v;
        }

        public int getNbProduitsRupture() {
            return nbProduitsRupture;
        }

        public void setNbProduitsRupture(int v) {
            this.nbProduitsRupture = v;
        }

        public int getNbProduitsSousSeuil() {
            return nbProduitsSousSeuil;
        }

        public void setNbProduitsSousSeuil(int v) {
            this.nbProduitsSousSeuil = v;
        }

        public int getNbCommandesJour() {
            return nbCommandesJour;
        }

        public void setNbCommandesJour(int v) {
            this.nbCommandesJour = v;
        }

        public double getCaJour() {
            return caJour;
        }

        public void setCaJour(double v) {
            this.caJour = v;
        }

        public int getNbCommandesEnCours() {
            return nbCommandesEnCours;
        }

        public void setNbCommandesEnCours(int v) {
            this.nbCommandesEnCours = v;
        }
    }
}
