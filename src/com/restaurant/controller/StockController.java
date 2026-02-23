package com.restaurant.controller;

import com.restaurant.dao.LogDAO;
import com.restaurant.dao.MouvementStockDAO;
import com.restaurant.dao.ProduitDAO;
import com.restaurant.model.Log;
import com.restaurant.model.MouvementStock;
import com.restaurant.model.Produit;
import com.restaurant.model.enums.TypeMouvement;
import com.restaurant.service.StockService;
import com.restaurant.view.StockView;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.JOptionPane;

public class StockController {

    private final StockView view;
    private final StockService service;
    private final LogDAO logDAO = new LogDAO();

    public StockController(StockView view) {
        this.view = view;
        ProduitDAO pDao = new ProduitDAO();
        MouvementStockDAO mDao = new MouvementStockDAO();
        this.service = new StockService(pDao, mDao);
    }

    // Enregistre une entrée ou sortie de stock depuis la vue
    public void enregistrerMouvement() {
        int qte;
        try {
            qte = Integer.parseInt(view.getQuantiteSaisie());
            if (qte <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "La quantité doit être un entier > 0",
                    "Saisie invalide", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            MouvementStock mvt = new MouvementStock();
            mvt.setProduit(view.getProduitSelectionne());
            mvt.setQuantite(qte);
            mvt.setType(view.isEntreeSelectionnee() ? TypeMouvement.ENTREE : TypeMouvement.SORTIE);
            mvt.setMotif(view.getMotifSaisi());
            mvt.setDate(LocalDate.now());

            service.traiterMouvement(mvt);

            Produit p = mvt.getProduit();
            if (p.getStockActu() == 0) {
                JOptionPane.showMessageDialog(view, "RUPTURE : " + p.getNomPro() + " est en rupture de stock !",
                        "Rupture de stock", JOptionPane.ERROR_MESSAGE);
            } else if (p.isSousSeuilAlerte()) {
                JOptionPane.showMessageDialog(view, "Stock faible : " + p.getNomPro() + " (" + p.getStockActu() + ")",
                        "Seuil d'alerte", JOptionPane.WARNING_MESSAGE);
            }

            // Log en BD : mouvement de stock
            String typeLabel = mvt.getType() == TypeMouvement.ENTREE ? "Entrée" : "Sortie";
            String actionDescription = typeLabel + " stock : " + qte + " x " + p.getNomPro();
            logDAO.ajouter(new Log("stock", actionDescription)); 

            JOptionPane.showMessageDialog(view, "Mouvement enregistré avec succès !");
            view.resetChamps();
            view.actualiserHistorique();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}