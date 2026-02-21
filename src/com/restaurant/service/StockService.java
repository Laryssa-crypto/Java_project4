package com.restaurant.service;

import com.restaurant.dao.MouvementStockDAO;
import com.restaurant.dao.ProduitDAO;
import com.restaurant.model.MouvementStock;
import com.restaurant.model.Produit;
import com.restaurant.model.enums.TypeMouvement;
import java.sql.SQLException;
import java.time.LocalDate;

public class StockService {

    private final ProduitDAO produitDAO;
    private final MouvementStockDAO mouvementDAO;

    public StockService(ProduitDAO produitDAO, MouvementStockDAO mouvementDAO) {
        this.produitDAO = produitDAO;
        this.mouvementDAO = mouvementDAO;
    }

    // Récupère l'historique complet des mouvements de stock
    public java.util.List<MouvementStock> getAllMouvements() {
        return mouvementDAO.listerHistorique();
    }

    // Traite un mouvement (mise à jour stock + ajout historique)
    public void traiterMouvement(MouvementStock mvt) throws SQLException {
        if (mvt.getQuantite() <= 0)
            throw new SQLException("La quantité doit être supérieure à 0");

        Produit p = mvt.getProduit();
        int ancienStock = p.getStockActu();
        int nouveauStock;

        if (mvt.getType() == TypeMouvement.ENTREE) {
            nouveauStock = ancienStock + mvt.getQuantite();
        } else {
            if (ancienStock < mvt.getQuantite())
                throw new SQLException("Stock insuffisant pour cette sortie");
            nouveauStock = ancienStock - mvt.getQuantite();
        }

        if (produitDAO.mettreAJourStock(p.getIdPro(), nouveauStock)) {
            mouvementDAO.ajouter(mvt);
            p.setStockActu(nouveauStock);
        } else {
            throw new SQLException("Échec mise à jour stock en base");
        }
    }

    public void enregistrerEntree(int idProduit, int quantite, String motif) throws SQLException {
        Produit produit = produitDAO.getById(idProduit);
        if (produit == null)
            throw new SQLException("Produit non trouvé");
        if (motif != null && motif.length() > 50)
            throw new SQLException("Le motif ne doit pas dépasser 50 caractères");

        MouvementStock mvt = new MouvementStock();
        mvt.setProduit(produit);
        mvt.setType(TypeMouvement.ENTREE);
        mvt.setQuantite(quantite);
        mvt.setMotif(motif);
        mvt.setDate(LocalDate.now());
        traiterMouvement(mvt);
    }

    public void enregistrerSortie(int idProduit, int quantite, String motif) throws SQLException {
        Produit produit = produitDAO.getById(idProduit);
        if (produit == null)
            throw new SQLException("Produit non trouvé");
        if (motif != null && motif.length() > 50)
            throw new SQLException("Le motif ne doit pas dépasser 50 caractères");

        MouvementStock mvt = new MouvementStock();
        mvt.setProduit(produit);
        mvt.setType(TypeMouvement.SORTIE);
        mvt.setQuantite(quantite);
        mvt.setMotif(motif);
        mvt.setDate(LocalDate.now());
        traiterMouvement(mvt);
    }
}
