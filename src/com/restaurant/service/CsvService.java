package com.restaurant.service;

import com.restaurant.model.Produit;
import com.restaurant.model.Commande;
import com.restaurant.model.LigneCommande;
import com.restaurant.service.StatistiqueService.StatistiquesGenerales;
import com.restaurant.service.StatistiqueService.ProduitVendu;
import java.io.*;
import java.util.List;

public class CsvService {

    // Exporte la liste des produits en CSV
    public static boolean exporterProduits(String path, List<Produit> produits) {
        try (PrintWriter writer = new PrintWriter(new File(path))) {
            writer.println("ID;Nom;Categorie;Prix;Stock;Seuil");
            for (Produit p : produits) {
                writer.println(String.format("%d;%s;%s;%.2f;%d;%d",
                        p.getIdPro(), p.getNomPro(), p.getNomCategorie(),
                        p.getPrixVente(), p.getStockActu(), p.getSeuilAlerte()));
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Importe des produits depuis un fichier CSV
    public static List<Produit> importerProduits(String path) {
        java.util.List<Produit> liste = new java.util.ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length >= 6) {
                    Produit p = new Produit();
                    p.setNomPro(data[1]);
                    p.setNomCategorie(data[2]);
                    p.setPrixVente(Double.parseDouble(data[3].replace(",", ".")));
                    p.setStockActu(Integer.parseInt(data[4]));
                    p.setSeuilAlerte(Integer.parseInt(data[5]));
                    liste.add(p);
                }
            }
        } catch (Exception e) {
        }
        return liste;
    }

    // Exporte le récapitulatif des commandes et leurs détails
    public static boolean exporterCommandes(String path, List<Commande> commandes, List<LigneCommande> toutesLignes) {
        try (PrintWriter writer = new PrintWriter(new File(path))) {
            writer.println("RECAPITULATIF DES COMMANDES");
            writer.println("ID;Date;Etat;Total;Caissier");
            for (Commande c : commandes) {
                writer.println(String.format("%d;%s;%s;%.2f;%s",
                        c.getIdCmde(), c.getDate(), c.getEtat(), c.getTotal(), c.getNomUtil()));
            }
            writer.println("\nDETAILS DES LIGNES");
            writer.println("ID_Cmde;Produit;Qte;Prix;Total");
            for (LigneCommande l : toutesLignes) {
                writer.println(String.format("%d;%s;%d;%.2f;%.2f",
                        l.getIdCmde(), l.getProduit().getNomPro(), l.getQteLig(), l.getPrixUnit(), l.getMontant()));
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Exporte les statistiques générales et tops produits
    public static boolean exporterStatistiques(String path, StatistiquesGenerales stats, List<ProduitVendu> topQte,
            List<ProduitVendu> topMontant, List<Produit> rupture, List<Produit> sousSeuil) {
        try (PrintWriter writer = new PrintWriter(new File(path))) {
            writer.println("STATISTIQUES GENERALES");
            writer.println("Indicateur;Valeur");
            writer.println("Chiffre d'Affaires;" + stats.getCaJour());
            writer.println("Commandes du jour;" + stats.getNbCommandesJour());
            writer.println("Produits en Reupture;" + stats.getNbProduitsRupture());

            writer.println("\nTOP PRODUITS (QUANTITE)");
            for (ProduitVendu pv : topQte) {
                writer.println(pv.getProduit().getNomPro() + ";" + pv.getQuantiteTotale());
            }

            writer.println("\nTOP PRODUITS (MONTANT)");
            for (ProduitVendu pv : topMontant) {
                writer.println(pv.getProduit().getNomPro() + ";" + pv.getMontantTotal());
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
