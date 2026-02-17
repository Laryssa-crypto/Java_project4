/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.service;
import com.restaurant.model.MouvementStock;
import com.restaurant.model.Produit;
import com.restaurant.model.enums.TypeMouvement;
import com.restaurant.dao.ProduitDAO;
import com.restaurant.dao.MouvementStockDAO;

/**
 *
 * @author jojo
 */
public class StockService {
    public void traiterMouvement(MouvementStock mvt) throws Exception {
    ProduitDAO produitDAO = new ProduitDAO();
    MouvementStockDAO mouvementDAO = new MouvementStockDAO();

    // 1. Récupérer le produit actuel pour avoir son stock
    Produit p = mvt.getProduit();
    int ancienStock = p.getStockActu();
    int nouveauStock;

    // 2. Calculer le nouveau stock
    if (mvt.getType() == TypeMouvement.ENTREE) {
        nouveauStock = ancienStock + mvt.getQuantite();
    } else {
        if (ancienStock < mvt.getQuantite()) {
            throw new Exception("Stock insuffisant pour cette sortie !");
        }
        nouveauStock = ancienStock - mvt.getQuantite();
    }

    // 3. Mettre à jour en base de données
    boolean stockOk = produitDAO.mettreAJourStock(p.getIdPro(), nouveauStock);
    if (stockOk) {
        mouvementDAO.ajouter(mvt); // Ajoute le mouvement dans l'historique
        p.setStockActu(nouveauStock); // Met à jour l'objet en mémoire
    } else {
        throw new Exception("Erreur lors de la mise à jour du stock en base.");
    }
}
}
