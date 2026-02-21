package com.restaurant.controller;

import com.restaurant.model.Categorie;
import com.restaurant.model.Produit;
import com.restaurant.service.CategorieService;
import com.restaurant.service.ProduitService;
import java.util.List;

public class ProduitController {

    private ProduitService produitService;
    private CategorieService categorieService;

    public ProduitController() {
        com.restaurant.dao.ProduitDAO pDao = new com.restaurant.dao.ProduitDAO();
        com.restaurant.dao.CategorieDAO cDao = new com.restaurant.dao.CategorieDAO();
        this.produitService = new ProduitService(pDao);
        this.categorieService = new CategorieService(cDao);
    }

    public boolean ajouterProduit(String nom, int idCat, double prix, int stock, int seuil) {
        return produitService.ajouterProduit(nom, idCat, prix, stock, seuil);
    }

    public boolean modifierProduit(int idPro, String nom, int idCat, double prix, int stock, int seuil) {
        return produitService.modifierProduit(idPro, nom, idCat, prix, stock, seuil);
    }

    public boolean supprimerProduit(int idPro) throws com.restaurant.dao.ProduitDAO.ProduitLieACommandeException {
        return produitService.supprimerProduit(idPro);
    }

    public List<Produit> getAllProduits() {
        return produitService.getAllProduits();
    }

    public List<Produit> rechercherProduits(String nom) {
        return produitService.rechercherProduits(nom);
    }

    public Produit getProduitById(int idPro) {
        return produitService.getProduitById(idPro);
    }

    public List<Produit> getProduitsParCategorie(int idCat) {
        return produitService.getProduitsParCategorie(idCat);
    }

    public List<Produit> getProduitsSousSeuilAlerte() {
        return produitService.getProduitsSousSeuilAlerte();
    }

    public boolean ajouterCategorie(String libelle) {
        return categorieService.ajouterCategorie(libelle);
    }

    public boolean modifierCategorie(int idCat, String libelle) {
        return categorieService.modifierCategorie(idCat, libelle);
    }

    public boolean supprimerCategorie(int idCat) {
        return categorieService.supprimerCategorie(idCat);
    }

    public List<Categorie> getAllCategories() {
        return categorieService.getAllCategories();
    }

    public Categorie getCategorieById(int idCat) {
        return categorieService.getCategorieById(idCat);
    }

    // Déclenche l'export CSV et affiche une confirmation
    public void exporterCSV(String path) {
        List<Produit> produits = getAllProduits();
        if (com.restaurant.service.CsvService.exporterProduits(path, produits)) {
            javax.swing.JOptionPane.showMessageDialog(null, "Export CSV réussi !", "Succès",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } else {
            javax.swing.JOptionPane.showMessageDialog(null, "Erreur lors de l'export CSV.", "Erreur",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    // Déclenche l'import CSV et affiche un résumé
    public void importerCSV(String path) {
        try {
            List<Produit> importes = com.restaurant.service.CsvService.importerProduits(path);
            int ajoutes = 0;
            for (Produit p : importes) {
                if (ajouterProduit(p.getNomPro(), p.getIdCat(), p.getPrixVente(), p.getStockActu(),
                        p.getSeuilAlerte())) {
                    ajoutes++;
                }
            }
            javax.swing.JOptionPane.showMessageDialog(null, "Import terminé : " + ajoutes + " produits ajoutés.",
                    "Succès", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Erreur lors de l'import : " + e.getMessage(), "Erreur",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}
