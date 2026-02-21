package com.restaurant.model;

public class Produit {
    private int idPro;
    private String nomPro;
    private int idCat;
    private String nomCategorie; // Pour l'affichage
    private double prixVente;
    private int stockActu;
    private int seuilAlerte;
    
    public Produit() {}
    
    public Produit(int idPro, String nomPro, int idCat, double prixVente, 
                   int stockActu, int seuilAlerte) {
        this.idPro = idPro;
        this.nomPro = nomPro;
        this.idCat = idCat;
        this.prixVente = prixVente;
        this.stockActu = stockActu;
        this.seuilAlerte = seuilAlerte;
    }
    
    public Produit(String nomPro, int idCat, double prixVente, 
                   int stockActu, int seuilAlerte) {
        this.nomPro = nomPro;
        this.idCat = idCat;
        this.prixVente = prixVente;
        this.stockActu = stockActu;
        this.seuilAlerte = seuilAlerte;
    }
    
    public int getIdPro() {
        return idPro;
    }
    
    public void setIdPro(int idPro) {
        this.idPro = idPro;
    }
    
    public String getNomPro() {
        return nomPro;
    }
    
    public void setNomPro(String nomPro) {
        this.nomPro = nomPro;
    }
    
    public int getIdCat() {
        return idCat;
    }
    
    public void setIdCat(int idCat) {
        this.idCat = idCat;
    }
    
    public String getNomCategorie() {
        return nomCategorie;
    }
    
    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }
    
    public double getPrixVente() {
        return prixVente;
    }
    
    public void setPrixVente(double prixVente) {
        this.prixVente = prixVente;
    }
    
    public int getStockActu() {
        return stockActu;
    }
    
    public void setStockActu(int stockActu) {
        this.stockActu = stockActu;
    }
    
    public int getSeuilAlerte() {
        return seuilAlerte;
    }
    
    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }
    
    /**
     * Vérifie si le stock est sous le seuil d'alerte
     */
    public boolean isSousSeuilAlerte() {
        return stockActu < seuilAlerte;
    }
    
    @Override
    public String toString() {
        return nomPro + " (" + prixVente + " FCFA)";
    }
}