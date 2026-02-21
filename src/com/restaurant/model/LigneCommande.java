package com.restaurant.model;

// Représente un article spécifique au sein d'une commande
public class LigneCommande {
    private int idLig;
    private int idCmde;
    private int idPro;
    private int qteLig;
    private double prixUnit;
    private double montant;

    private Produit produit;

    public LigneCommande() {
    }

    public LigneCommande(int idLig, int idCmde, int idPro, int qteLig, double prixUnit) {
        this.idLig = idLig;
        this.idCmde = idCmde;
        this.idPro = idPro;
        this.qteLig = qteLig;
        this.prixUnit = prixUnit;
        this.montant = qteLig * prixUnit;
    }

    public LigneCommande(int idCmde, int idPro, int qteLig, double prixUnit) {
        this.idCmde = idCmde;
        this.idPro = idPro;
        this.qteLig = qteLig;
        this.prixUnit = prixUnit;
        this.montant = qteLig * prixUnit;
    }

    public LigneCommande(Produit produit, int quantite) {
        this.produit = produit;
        this.idPro = produit.getIdPro();
        this.qteLig = quantite;
        this.prixUnit = produit.getPrixVente();
        this.montant = quantite * prixUnit;
    }

    public int getIdLig() {
        return idLig;
    }

    public void setIdLig(int idLig) {
        this.idLig = idLig;
    }

    public int getIdCmde() {
        return idCmde;
    }

    public void setIdCmde(int idCmde) {
        this.idCmde = idCmde;
    }

    public int getIdPro() {
        return idPro;
    }

    public void setIdPro(int idPro) {
        this.idPro = idPro;
    }

    public int getQteLig() {
        return qteLig;
    }

    public void setQteLig(int qteLig) {
        this.qteLig = qteLig;
        calculerMontant();
    }

    public double getPrixUnit() {
        return prixUnit;
    }

    public void setPrixUnit(double prixUnit) {
        this.prixUnit = prixUnit;
        calculerMontant();
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
        if (produit != null) {
            this.idPro = produit.getIdPro();
            this.prixUnit = produit.getPrixVente();
            calculerMontant();
        }
    }

    private void calculerMontant() {
        this.montant = this.qteLig * this.prixUnit;
    }

    @Override
    public String toString() {
        if (produit != null) {
            return produit.getNomPro() + " - " + qteLig + " x " + prixUnit + "€ = " + montant + "€";
        }
        return "Ligne #" + idLig + " - " + qteLig + " x " + prixUnit + "€ = " + montant + "€";
    }
}
