package com.restaurant.model;

import com.restaurant.model.enums.TypeMouvement;
import java.time.LocalDate;

// Représente un mouvement de stock (entrée ou sortie)
public class MouvementStock {

    private int id;
    private Produit produit;
    private TypeMouvement type;
    private int quantite;
    private LocalDate date;
    private String motif;

    public MouvementStock() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public TypeMouvement getType() {
        return type;
    }

    public void setType(TypeMouvement type) {
        this.type = type;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    private String reference;

    public String getReference() {
        if (reference != null && !reference.isEmpty()) {
            return reference;
        }
        if (type == com.restaurant.model.enums.TypeMouvement.ENTREE) {
            return "FAC-ACH-" + String.format("%04d", id);
        } else {
            return "FAC-VEN-" + String.format("%04d", id);
        }
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
