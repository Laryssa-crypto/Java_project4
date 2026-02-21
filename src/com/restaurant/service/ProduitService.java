package com.restaurant.service;

import com.restaurant.dao.ProduitDAO;
import com.restaurant.model.Produit;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProduitService {
    private static final Logger logger = LogManager.getLogger(ProduitService.class);

    private ProduitDAO produitDAO;

    public ProduitService(ProduitDAO produitDAO) {
        this.produitDAO = produitDAO;
    }

    public boolean ajouterProduit(String nom, int idCat, double prix, int stock, int seuil) {
        if (!valider(nom, prix, stock, seuil))
            return false;
        return produitDAO.ajouter(new Produit(nom.trim(), idCat, prix, stock, seuil));
    }

    public boolean modifierProduit(int idPro, String nom, int idCat, double prix, int stock, int seuil) {
        if (!valider(nom, prix, stock, seuil))
            return false;
        return produitDAO.modifier(new Produit(idPro, nom.trim(), idCat, prix, stock, seuil));
    }

    public boolean supprimerProduit(int idPro) throws ProduitDAO.ProduitLieACommandeException {
        return produitDAO.deleteProduit(idPro);
    }

    public List<Produit> getAllProduits() {
        return produitDAO.getAll();
    }

    public Produit getProduitById(int idPro) {
        return produitDAO.getById(idPro);
    }

    public List<Produit> rechercherProduits(String nom) {
        if (nom == null || nom.trim().isEmpty())
            return getAllProduits();
        return produitDAO.rechercherParNom(nom.trim());
    }

    public List<Produit> getProduitsParCategorie(int idCat) {
        return produitDAO.getByCategorie(idCat);
    }

    public List<Produit> getProduitsSousSeuilAlerte() {
        return produitDAO.getProduitsSousSeuilAlerte();
    }

    public boolean estSousSeuilAlerte(int idPro) {
        Produit p = produitDAO.getById(idPro);
        return p != null && p.isSousSeuilAlerte();
    }

    private boolean valider(String nom, double prix, int stock, int seuil) {
        if (nom == null || nom.trim().isEmpty()) {
            logger.warn("Le nom du produit ne peut pas être vide");
            return false;
        }
        if (nom.length() > 50) {
            logger.warn("Le nom ne doit pas dépasser 50 caractères");
            return false;
        }
        if (prix <= 0) {
            logger.warn("Le prix doit être supérieur à 0");
            return false;
        }
        if (stock < 0 || seuil < 0) {
            logger.warn("Le stock et le seuil ne peuvent pas être négatifs");
            return false;
        }
        return true;
    }
}