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

    // Ajoute un nouveau produit après validation
    public boolean ajouterProduit(String nom, int idCat, double prix, int stock, int seuil) {
        if (!valider(nom, prix, stock, seuil))
            return false;
        return produitDAO.ajouter(new Produit(nom.trim(), idCat, prix, stock, seuil));
    }

    // Modifie un produit existant après validation
    public boolean modifierProduit(int idPro, String nom, int idCat, double prix, int stock, int seuil) {
        if (!valider(nom, prix, stock, seuil))
            return false;
        return produitDAO.modifier(new Produit(idPro, nom.trim(), idCat, prix, stock, seuil));
    }

    // Supprime un produit s'il n'est pas lié à une commande
    public boolean supprimerProduit(int idPro) throws ProduitDAO.ProduitLieACommandeException {
        return produitDAO.deleteProduit(idPro);
    }

    // Récupère tous les produits
    public List<Produit> getAllProduits() {
        return produitDAO.getAll();
    }

    // Récupère un produit par son ID
    public Produit getProduitById(int idPro) {
        return produitDAO.getById(idPro);
    }

    // Recherche des produits par nom
    public List<Produit> rechercherProduits(String nom) {
        if (nom == null || nom.trim().isEmpty())
            return getAllProduits();
        return produitDAO.rechercherParNom(nom.trim());
    }

    // Récupère les produits d'une catégorie
    public List<Produit> getProduitsParCategorie(int idCat) {
        return produitDAO.getByCategorie(idCat);
    }

    // Récupère les produits dont le stock est sous le seuil
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