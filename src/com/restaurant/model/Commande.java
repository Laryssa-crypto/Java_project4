package com.restaurant.model;

import com.restaurant.model.enums.EtatCommande;
import java.time.LocalDateTime;

/**
 * Classe représentant une commande client
 */
public class Commande {
    private int idCmde;
    private LocalDateTime date;
    private EtatCommande etat;
    private double total;
    private String nomUtil; // Pour le dashboard des ventes par employé

    public Commande() {
        this.etat = EtatCommande.EN_COURS;
        this.total = 0.0;
        this.date = LocalDateTime.now();
        this.nomUtil = "Inconnu";
    }

    public Commande(int idCmde, LocalDateTime date, EtatCommande etat, double total) {
        this.idCmde = idCmde;
        this.date = date;
        this.etat = etat;
        this.total = total;
        this.nomUtil = "Inconnu";
    }

    public Commande(LocalDateTime date, EtatCommande etat, double total) {
        this.date = date;
        this.etat = etat;
        this.total = total;
        this.nomUtil = "Inconnu";
    }

    public int getIdCmde() {
        return idCmde;
    }

    public void setIdCmde(int idCmde) {
        this.idCmde = idCmde;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public EtatCommande getEtat() {
        return etat;
    }

    public void setEtat(EtatCommande etat) {
        this.etat = etat;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getNomUtil() {
        return nomUtil;
    }

    public void setNomUtil(String nomUtil) {
        this.nomUtil = nomUtil;
    }

    @Override
    public String toString() {
        return "Commande #" + idCmde + " - " + date + " - " + etat + " - " + total + "€";
    }
}
