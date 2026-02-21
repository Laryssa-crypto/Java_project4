package com.restaurant.model.enums;

// États possibles pour le cycle de vie d'une commande
public enum EtatCommande {
    EN_COURS("En cours"),
    VALIDEE("Validée"),
    ANNULEE("Annulée");

    private String libelle;

    EtatCommande(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}
