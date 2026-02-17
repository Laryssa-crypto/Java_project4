/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.service;

import com.restaurant.dao.ProduitDAO;
import com.restaurant.model.Produit;
import java.util.List;

/**
 *
 * @author ASUS
 */


public class ProduitService {
    private ProduitDAO produitDAO;
    
    public ProduitService() {
        this.produitDAO = new ProduitDAO();
    }
    
    /**
     * Valide et ajoute un produit
     */
    public boolean ajouterProduit(String nom, int idCat, double prix, int stock, int seuil) {
        // Validation du nom
        if (nom == null || nom.trim().isEmpty()) {
            System.err.println("❌ Le nom du produit ne peut pas être vide");
            return false;
        }
        
        if (nom.length() > 50) {
            System.err.println("❌ Le nom ne doit pas dépasser 50 caractères");
            return false;
        }
        
        // Validation du prix
        if (prix <= 0) {
            System.err.println("❌ Le prix doit être supérieur à 0");
            return false;
        }
        
        // Validation du stock
        if (stock < 0) {
            System.err.println("❌ Le stock ne peut pas être négatif");
            return false;
        }
        
        // Validation du seuil
        if (seuil < 0) {
            System.err.println("❌ Le seuil d'alerte ne peut pas être négatif");
            return false;
        }
        
        // Ajouter
        Produit produit = new Produit(nom.trim(), idCat, prix, stock, seuil);
        return produitDAO.ajouter(produit);
    }
    
    /**
     * Valide et modifie un produit
     */
    public boolean modifierProduit(int idPro, String nom, int idCat, double prix, int stock, int seuil) {
        // Validation (même que pour ajouterProduit)
        if (nom == null || nom.trim().isEmpty()) {
            System.err.println("❌ Le nom du produit ne peut pas être vide");
            return false;
        }
        
        if (nom.length() > 50) {
            System.err.println("❌ Le nom ne doit pas dépasser 50 caractères");
            return false;
        }
        
        if (prix <= 0) {
            System.err.println("❌ Le prix doit être supérieur à 0");
            return false;
        }
        
        if (stock < 0) {
            System.err.println("❌ Le stock ne peut pas être négatif");
            return false;
        }
        
        if (seuil < 0) {
            System.err.println("❌ Le seuil d'alerte ne peut pas être négatif");
            return false;
        }
        
        // Modifier
        Produit produit = new Produit(idPro, nom.trim(), idCat, prix, stock, seuil);
        return produitDAO.modifier(produit);
    }
    
    /**
     * Supprime un produit
     */
    public boolean supprimerProduit(int idPro) {
        return produitDAO.supprimer(idPro);
    }
    
    /**
     * Récupère tous les produits
     */
    public List<Produit> getAllProduits() {
        return produitDAO.getAll();
    }
    
    /**
     * Récupère un produit par ID
     */
    public Produit getProduitById(int idPro) {
        return produitDAO.getById(idPro);
    }
    
    /**
     * Recherche des produits par nom
     */
    public List<Produit> rechercherProduits(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return getAllProduits();
        }
        return produitDAO.rechercherParNom(nom.trim());
    }
    
    /**
     * Récupère les produits par catégorie
     */
    public List<Produit> getProduitsParCategorie(int idCat) {
        return produitDAO.getByCategorie(idCat);
    }
    
    /**
     * Récupère les produits sous le seuil d'alerte
     */
    public List<Produit> getProduitsSousSeuilAlerte() {
        return produitDAO.getProduitsSousSeuilAlerte();
    }
    
    /**
     * Vérifie si un produit est sous le seuil d'alerte
     */
    public boolean estSousSeuilAlerte(int idPro) {
        Produit produit = produitDAO.getById(idPro);
        return produit != null && produit.isSousSeuilAlerte();
    }
}