package com.restaurant.service;

import com.restaurant.dao.CommandeDAO;
import com.restaurant.dao.ConnectionDB;
import com.restaurant.dao.LigneCommandeDAO;
import com.restaurant.dao.ProduitDAO;
import com.restaurant.model.Commande;
import com.restaurant.model.LigneCommande;
import com.restaurant.model.Produit;
import com.restaurant.model.enums.EtatCommande;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CommandeService {

    private final CommandeDAO commandeDAO;
    private final LigneCommandeDAO ligneCommandeDAO;
    private final ProduitDAO produitDAO;
    private final StockService stockService;

    public CommandeService(CommandeDAO commandeDAO, LigneCommandeDAO ligneCommandeDAO,
            ProduitDAO produitDAO, StockService stockService) {
        this.commandeDAO = commandeDAO;
        this.ligneCommandeDAO = ligneCommandeDAO;
        this.produitDAO = produitDAO;
        this.stockService = stockService;
    }

    public Commande creerCommande(String nomUtil) throws SQLException {
        Commande commande = new Commande();
        commande.setNomUtil(nomUtil);
        int id = commandeDAO.create(commande);
        if (id > 0)
            return commande;
        throw new SQLException("Impossible de créer la commande");
    }

    public boolean ajouterProduit(int idCommande, int idProduit, int quantite) throws SQLException {
        Commande commande = commandeDAO.findById(idCommande);
        if (commande == null || commande.getEtat() != EtatCommande.EN_COURS)
            throw new SQLException("Commande non trouvée ou déjà validée");
        if (quantite <= 0)
            throw new SQLException("La quantité doit être supérieure à 0");

        Produit produit = produitDAO.getById(idProduit);
        if (produit == null)
            throw new SQLException("Produit non trouvé");
        if (produit.getStockActu() < quantite)
            throw new SQLException("Stock insuffisant pour : " + produit.getNomPro());

        LigneCommande ligne = new LigneCommande(idCommande, idProduit, quantite, produit.getPrixVente());
        int idLigne = ligneCommandeDAO.create(ligne);
        if (idLigne > 0) {
            commande.setTotal(ligneCommandeDAO.calculerTotalCommande(idCommande));
            commandeDAO.update(commande);
            return true;
        }
        return false;
    }

    public boolean modifierQuantiteLigne(int idLigne, int nouvelleQuantite) throws SQLException {
        LigneCommande ligne = ligneCommandeDAO.findById(idLigne);
        if (ligne == null)
            throw new SQLException("Ligne de commande non trouvée");

        Commande commande = commandeDAO.findById(ligne.getIdCmde());
        if (commande == null || commande.getEtat() != EtatCommande.EN_COURS)
            throw new SQLException("Impossible de modifier une commande validée");
        if (nouvelleQuantite <= 0)
            throw new SQLException("La quantité doit être supérieure à 0");

        Produit produit = produitDAO.getById(ligne.getIdPro());
        int diff = nouvelleQuantite - ligne.getQteLig();
        if (diff > 0 && produit != null && produit.getStockActu() < diff)
            throw new SQLException("Stock insuffisant pour augmenter la quantité");

        ligne.setQteLig(nouvelleQuantite);
        boolean ok = ligneCommandeDAO.update(ligne);
        if (ok) {
            commande.setTotal(ligneCommandeDAO.calculerTotalCommande(ligne.getIdCmde()));
            commandeDAO.update(commande);
        }
        return ok;
    }

    public boolean supprimerLigne(int idLigne) throws SQLException {
        LigneCommande ligne = ligneCommandeDAO.findById(idLigne);
        if (ligne == null)
            throw new SQLException("Ligne de commande non trouvée");

        Commande commande = commandeDAO.findById(ligne.getIdCmde());
        if (commande == null || commande.getEtat() != EtatCommande.EN_COURS)
            throw new SQLException("Impossible de modifier une commande validée");

        boolean ok = ligneCommandeDAO.delete(idLigne);
        if (ok) {
            commande.setTotal(ligneCommandeDAO.calculerTotalCommande(ligne.getIdCmde()));
            commandeDAO.update(commande);
        }
        return ok;
    }

    public boolean validerCommande(int idCommande) throws SQLException {
        Connection conn = ConnectionDB.getConnection();
        boolean prevAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            Commande commande = commandeDAO.findById(idCommande);
            if (commande == null)
                throw new SQLException("Commande non trouvée");
            if (commande.getEtat() != EtatCommande.EN_COURS)
                throw new SQLException("La commande n'est pas en cours");

            List<LigneCommande> lignes = ligneCommandeDAO.findByCommande(idCommande);
            if (lignes.isEmpty())
                throw new SQLException("Impossible de valider une commande sans lignes");

            for (LigneCommande ligne : lignes) {
                Produit produit = produitDAO.getById(ligne.getIdPro());
                if (produit != null) {
                    stockService.enregistrerSortie(ligne.getIdPro(), ligne.getQteLig(),
                            "Vente commande #" + idCommande);
                }
            }

            commande.setEtat(EtatCommande.VALIDEE);
            boolean ok = commandeDAO.update(commande);
            conn.commit();
            return ok;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(prevAutoCommit);
        }
    }

    public boolean annulerCommande(int idCommande) throws SQLException {
        Connection conn = ConnectionDB.getConnection();
        boolean prevAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            Commande commande = commandeDAO.findById(idCommande);
            if (commande == null)
                throw new SQLException("Commande non trouvée");
            if (commande.getEtat() == EtatCommande.ANNULEE)
                return true;

            if (commande.getEtat() == EtatCommande.VALIDEE) {
                for (LigneCommande ligne : ligneCommandeDAO.findByCommande(idCommande)) {
                    Produit produit = produitDAO.getById(ligne.getIdPro());
                    if (produit != null) {
                        produit.setStockActu(produit.getStockActu() + ligne.getQteLig());
                        produitDAO.modifier(produit);
                        stockService.enregistrerEntree(ligne.getIdPro(), ligne.getQteLig(),
                                "Annulation commande #" + idCommande);
                    }
                }
            }

            commande.setEtat(EtatCommande.ANNULEE);
            boolean ok = commandeDAO.update(commande);
            conn.commit();
            return ok;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(prevAutoCommit);
        }
    }

    public Commande getCommande(int id) throws SQLException {
        return commandeDAO.findById(id);
    }

    public List<Commande> getAllCommandes() throws SQLException {
        return commandeDAO.findAll();
    }

    public List<LigneCommande> getLignesCommande(int idCommande) throws SQLException {
        return ligneCommandeDAO.findByCommande(idCommande);
    }

    public List<Commande> getCommandesByEtat(EtatCommande etat) throws SQLException {
        return commandeDAO.findByEtat(etat);
    }

    public void nettoyerCommandesVides() throws SQLException {
        int count = commandeDAO.deleteEmptyOrders();
        if (count > 0) {
            // logger.info("Nettoyage : " + count + " commandes vides supprimées");
        }
    }
}
