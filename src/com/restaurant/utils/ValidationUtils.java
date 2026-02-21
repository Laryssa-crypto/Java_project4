package com.restaurant.utils;

/**
 * Classe utilitaire pour la validation des données
 */
public class ValidationUtils {
    
    public static boolean validerNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return false;
        }
        
        String nomTrim = nom.trim();
        
        if (nomTrim.length() < 2 || nomTrim.length() > 50) {
            return false;
        }
        
        return nomTrim.matches("^[a-zA-Z0-9\\s\\-']+$");
    }
    
    public static boolean validerLibelleCategorie(String libelle) {
        if (libelle == null || libelle.trim().isEmpty()) {
            return false;
        }
        
        String libelleTrim = libelle.trim();
        
        if (libelleTrim.length() < 2 || libelleTrim.length() > 30) {
            return false;
        }
        
        return validerNom(libelleTrim);
    }
    
    public static boolean validerNomProduit(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return false;
        }
        
        String nomTrim = nom.trim();
        
        if (nomTrim.length() < 2 || nomTrim.length() > 50) {
            return false;
        }
        
        return validerNom(nomTrim);
    }
    
    public static boolean validerPrix(double prix) {
        return prix > 0 && prix <= 999999.99; // Limite raisonnable
    }
    
    public static boolean validerPrix(String prixStr) {
        try {
            double prix = Double.parseDouble(prixStr.replace(',', '.'));
            return validerPrix(prix);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean validerQuantite(int quantite) {
        return quantite > 0 && quantite <= 9999; // Limite raisonnable
    }
    
    public static boolean validerQuantite(String quantiteStr) {
        try {
            int quantite = Integer.parseInt(quantiteStr);
            return validerQuantite(quantite);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean validerStock(int stock) {
        return stock >= 0 && stock <= 99999; // Limite raisonnable
    }
    
    public static boolean validerStock(String stockStr) {
        try {
            int stock = Integer.parseInt(stockStr);
            return validerStock(stock);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean validerSeuilAlerte(int seuil) {
        return seuil >= 0 && seuil <= 9999; // Limite raisonnable
    }
    
    public static boolean validerSeuilAlerte(String seuilStr) {
        try {
            int seuil = Integer.parseInt(seuilStr);
            return validerSeuilAlerte(seuil);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean validerNomUtilisateur(String nomUtil) {
        if (nomUtil == null || nomUtil.trim().isEmpty()) {
            return false;
        }
        
        String nomTrim = nomUtil.trim();
        
        if (nomTrim.length() < 3 || nomTrim.length() > 50) {
            return false;
        }
        
        return nomTrim.matches("^[a-zA-Z0-9\\-_]+$");
    }
    
    public static boolean validerMotDePasse(String motDePasse) {
        if (motDePasse == null || motDePasse.trim().isEmpty()) {
            return false;
        }
        
        String mdpTrim = motDePasse.trim();
        
        if (mdpTrim.length() < 4 || mdpTrim.length() > 256) {
            return false;
        }
        
        return !mdpTrim.contains(" ");
    }
    
    public static boolean validerMotif(String motif) {
        if (motif == null || motif.trim().isEmpty()) {
            return true; // Le motif peut être optionnel
        }
        
        String motifTrim = motif.trim();
        
        if (motifTrim.length() > 50) {
            return false;
        }
        
        return motifTrim.matches("^[a-zA-Z0-9\\s\\-']+$");
    }
    
    public static boolean validerDescription(String description, int longueurMax) {
        if (description == null) {
            return true; // La description peut être nulle
        }
        
        String descTrim = description.trim();
        
        if (descTrim.length() > longueurMax) {
            return false;
        }
        
        return true;
    }
    
    public static boolean validerChampNonVide(String champ) {
        return champ != null && !champ.trim().isEmpty();
    }
    
    public static boolean validerEntierDansPlage(int valeur, int min, int max) {
        return valeur >= min && valeur <= max;
    }
    
    public static boolean validerEntierDansPlage(String valeurStr, int min, int max) {
        try {
            int valeur = Integer.parseInt(valeurStr);
            return validerEntierDansPlage(valeur, min, max);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static String normaliserChaine(String chaine) {
        if (chaine == null) {
            return "";
        }
        
        return chaine.trim().replaceAll("\\s+", " ");
    }
    
    public static String getMessageErreur(String champ, String type) {
        switch (type.toLowerCase()) {
            case "nom":
                return "Le " + champ + " doit contenir entre 2 et 50 caractères alphanumériques";
            case "prix":
                return "Le " + champ + " doit être un nombre positif";
            case "quantite":
                return "La " + champ + " doit être un nombre entier positif";
            case "stock":
                return "Le " + champ + " doit être un nombre entier positif ou nul";
            case "utilisateur":
                return "Le " + champ + " doit contenir entre 3 et 50 caractères (lettres, chiffres, _, -)";
            case "motdepasse":
                return "Le " + champ + " doit contenir au moins 4 caractères";
            case "nonvide":
                return "Le " + champ + " ne peut pas être vide";
            default:
                return "Le " + champ + " n'est pas valide";
        }
    }
}
