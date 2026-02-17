/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.controller;

import com.restaurant.view.StockView;
import com.restaurant.model.MouvementStock;
import com.restaurant.model.enums.TypeMouvement;
import com.restaurant.service.StockService;
import java.time.LocalDate;
import javax.swing.JOptionPane;

/**
 *
 * @author jojo
 */
public class StockController {
    private final StockView view;
    private final StockService service = new StockService();

    public StockController(StockView view) {
        this.view = view;
    }

    public void enregistrerMouvement() {
        try {
            // 1. Validation de la quantité (doit être un entier)
            int qte;
            try {
                qte = Integer.parseInt(view.getQuantiteSaisie());
            } catch (NumberFormatException e) {
                throw new Exception("La quantité doit être un nombre entier valide !");
            }

            // 2. Création de l'objet Mouvement
            MouvementStock mvt = new MouvementStock();
            mvt.setProduit(view.getProduitSelectionne());
            mvt.setQuantite(qte);
            mvt.setType(view.isEntreeSelectionnee() ? TypeMouvement.ENTREE : TypeMouvement.SORTIE);
            mvt.setMotif(view.getMotifSaisi());
            mvt.setDate(LocalDate.now());

            // 3. Traitement métier (Calcul stock + Enregistrement DB)
            service.traiterMouvement(mvt);

            // 4. Feedback et mise à jour de l'UI
            JOptionPane.showMessageDialog(view, "Mouvement enregistré avec succès !");
            view.resetChamps();        
            view.actualiserHistorique(); 
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur : " + e.getMessage(), "Échec", JOptionPane.ERROR_MESSAGE);
        }
    }
}