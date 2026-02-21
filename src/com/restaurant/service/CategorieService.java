package com.restaurant.service;

import com.restaurant.dao.CategorieDAO;
import com.restaurant.model.Categorie;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CategorieService {
    private static final Logger logger = LogManager.getLogger(CategorieService.class);

    private CategorieDAO categorieDAO;

    public CategorieService(CategorieDAO categorieDAO) {
        this.categorieDAO = categorieDAO;
    }

    public boolean ajouterCategorie(String libelle) {
        if (libelle == null || libelle.trim().isEmpty()) {
            logger.warn("Le libellé ne peut pas être vide");
            return false;
        }
        if (libelle.length() > 30) {
            logger.warn("Le libellé ne doit pas dépasser 30 caractères");
            return false;
        }
        if (categorieDAO.existe(libelle.trim())) {
            logger.warn("Cette catégorie existe déjà");
            return false;
        }
        return categorieDAO.ajouter(new Categorie(libelle.trim()));
    }

    public boolean modifierCategorie(int idCat, String libelle) {
        if (libelle == null || libelle.trim().isEmpty()) {
            logger.warn("Le libellé ne peut pas être vide");
            return false;
        }
        if (libelle.length() > 30) {
            logger.warn("Le libellé ne doit pas dépasser 30 caractères");
            return false;
        }
        return categorieDAO.modifier(new Categorie(idCat, libelle.trim()));
    }

    public boolean supprimerCategorie(int idCat) {
        return categorieDAO.supprimer(idCat);
    }

    public List<Categorie> getAllCategories() {
        return categorieDAO.getAll();
    }

    public Categorie getCategorieById(int idCat) {
        return categorieDAO.getById(idCat);
    }
}
