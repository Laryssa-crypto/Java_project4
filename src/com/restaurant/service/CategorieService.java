/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.service;

import com.restaurant.dao.CategorieDAO;
import com.restaurant.model.Categorie;
import java.util.List;

/**
 *
 * @author ASUS
 */


public class CategorieService {
    private CategorieDAO categorieDAO;
    
    public CategorieService() {
        this.categorieDAO = new CategorieDAO();
    }
    
    /**
     * Valide et ajoute une catégorie
     */
    public boolean ajouterCategorie(String libelle) {
        // Validation
        if (libelle == null || libelle.trim().isEmpty()) {
            System.err.println("❌ Le libellé ne peut pas être vide");
            return false;
        }
        
        if (libelle.length() > 30) {
            System.err.println("❌ Le libellé ne doit pas dépasser 30 caractères");
            return false;
        }
        
        // Vérifier si la catégorie existe déjà
        if (categorieDAO.existe(libelle.trim())) {
            System.err.println("❌ Cette catégorie existe déjà");
            return false;
        }
        
        // Ajouter
        Categorie categorie = new Categorie(libelle.trim());
        return categorieDAO.ajouter(categorie);
    }
    
    /**
     * Valide et modifie une catégorie
     */
    public boolean modifierCategorie(int idCat, String libelle) {
        // Validation
        if (libelle == null || libelle.trim().isEmpty()) {
            System.err.println("❌ Le libellé ne peut pas être vide");
            return false;
        }
        
        if (libelle.length() > 30) {
            System.err.println("❌ Le libellé ne doit pas dépasser 30 caractères");
            return false;
        }
        
        // Modifier
        Categorie categorie = new Categorie(idCat, libelle.trim());
        return categorieDAO.modifier(categorie);
    }
    
    /**
     * Supprime une catégorie
     */
    public boolean supprimerCategorie(int idCat) {
        return categorieDAO.supprimer(idCat);
    }
    
    /**
     * Récupère toutes les catégories
     */
    public List<Categorie> getAllCategories() {
        return categorieDAO.getAll();
    }
    
    /**
     * Récupère une catégorie par ID
     */
    public Categorie getCategorieById(int idCat) {
        return categorieDAO.getById(idCat);
    }
}
