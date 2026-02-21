package com.restaurant.model;

// Représente une catégorie de produits (ex: Boissons, Plats)
public class Categorie {
    private int idCat;
    private String libelleCat;

    public Categorie() {
    }

    public Categorie(int idCat, String libelleCat) {
        this.idCat = idCat;
        this.libelleCat = libelleCat;
    }

    public Categorie(String libelleCat) {
        this.libelleCat = libelleCat;
    }

    public int getIdCat() {
        return idCat;
    }

    public void setIdCat(int idCat) {
        this.idCat = idCat;
    }

    public String getLibelleCat() {
        return libelleCat;
    }

    public void setLibelleCat(String libelleCat) {
        this.libelleCat = libelleCat;
    }

    @Override
    public String toString() {
        return libelleCat; // Pour affichage dans JComboBox
    }
}
