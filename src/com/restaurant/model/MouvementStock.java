/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.model;

import com.restaurant.model.enums.TypeMouvement;
import java.time.LocalDate;

/**
 *
 * @author jojo
 */
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

    public String getReference() {
        if (type == com.restaurant.model.enums.TypeMouvement.ENTREE) {
            return "FAC-ACH-" + String.format("%04d", id);
        } else {
            return "FAC-VEN-" + String.format("%04d", id);
        }
    }
}
