/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.controller;

import com.restaurant.model.Categorie;
import com.restaurant.model.Produit;
import com.restaurant.service.CategorieService;
import com.restaurant.service.ProduitService;
import java.util.List;

/**
 *
 * @author ASUS
 */


public class ProduitController {
    private final ProduitService produitService;
    private final CategorieService categorieService;
    
    public ProduitController() {
        this.produitService = new ProduitService();
        this.categorieService = new CategorieService();
    }
    
    /**
     * Ajoute un produit
     */
    public boolean ajouterProduit(String nom, int idCat, double prix, int stock, int seuil) {
        return produitService.ajouterProduit(nom, idCat, prix, stock, seuil);
    }
    
    /**
     * Modifie un produit
     */
    public boolean modifierProduit(int idPro, String nom, int idCat, double prix, int stock, int seuil) {
        return produitService.modifierProduit(idPro, nom, idCat, prix, stock, seuil);
    }
    
    /**
     * Supprime un produit
     */
    public boolean supprimerProduit(int idPro) {
        return produitService.supprimerProduit(idPro);
    }
    
    /**
     * Récupère tous les produits
     */
    public List<Produit> getAllProduits() {
        return produitService.getAllProduits();
    }
    
    /**
     * Recherche des produits
     */
    public List<Produit> rechercherProduits(String nom) {
        return produitService.rechercherProduits(nom);
    }
    
    /**
     * Récupère les produits par catégorie
     */
    public List<Produit> getProduitsParCategorie(int idCat) {
        return produitService.getProduitsParCategorie(idCat);
    }
    
    /**
     * Récupère les produits sous le seuil d'alerte
     */
    public List<Produit> getProduitsSousSeuilAlerte() {
        return produitService.getProduitsSousSeuilAlerte();
    }
    
    /**
     * Ajoute une catégorie
     */
    public boolean ajouterCategorie(String libelle) {
        return categorieService.ajouterCategorie(libelle);
    }
    
    /**
     * Modifie une catégorie
     */
    public boolean modifierCategorie(int idCat, String libelle) {
        return categorieService.modifierCategorie(idCat, libelle);
    }
    
    /**
     * Supprime une catégorie
     */
    public boolean supprimerCategorie(int idCat) {
        return categorieService.supprimerCategorie(idCat);
    }
    
    /**
     * Récupère toutes les catégories
     */
    public List<Categorie> getAllCategories() {
        return categorieService.getAllCategories();
    }
    
    /**
     * Récupère une catégorie par ID
     */
    public Categorie getCategorieById(int idCat) {
        return categorieService.getCategorieById(idCat);
    }
}
