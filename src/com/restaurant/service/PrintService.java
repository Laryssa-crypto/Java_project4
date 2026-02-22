package com.restaurant.service;

import com.restaurant.model.Commande;
import com.restaurant.model.LigneCommande;
import com.restaurant.service.StatistiqueService.StatistiquesGenerales;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;
import javax.swing.JOptionPane;

public class PrintService {

    // Imprime le reçu de vente pour le client (format ticket)
    public static void imprimerRecuClient(Commande commande, List<LigneCommande> lignes) {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0)
                    return NO_SUCH_PAGE;

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                int y = 50;
                int x = 10;
                g2d.setFont(new Font("Monospaced", Font.BOLD, 16));
                g2d.drawString("*** MON RESTAURANT ***", x + 20, y);
                y += 25;
                g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
                g2d.drawString("Reçu Commande #" + commande.getIdCmde(), x, y);
                y += 15;
                g2d.drawString("Date: " + commande.getDate(), x, y);
                y += 15;
                String caissierName = commande.getNomUtil() != null ? commande.getNomUtil() : "Inconnu";
                g2d.drawString("Caissier: " + caissierName, x, y);
                y += 15;
                g2d.drawString("---------------------------------", x, y);
                y += 15;

                g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
                g2d.drawString(String.format("%-15s %3s %9s", "Article", "Qté", "Montant"), x, y);
                y += 15;
                g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
                g2d.drawString("---------------------------------", x, y);
                y += 15;

                for (LigneCommande l : lignes) {
                    String nomProd = l.getProduit().getNomPro();
                    if (nomProd.length() > 14)
                        nomProd = nomProd.substring(0, 11) + "...";
                    String ligneTxt = String.format("%-15s %3d %9.2f", nomProd, l.getQteLig(), l.getMontant());
                    g2d.drawString(ligneTxt, x, y);
                    y += 15;
                }

                y += 5;
                g2d.drawString("---------------------------------", x, y);
                y += 20;
                g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
                g2d.drawString("TOTAL NET: " + String.format("%.2f FCFA", commande.getTotal()), x, y);
                y += 30;
                g2d.setFont(new Font("Monospaced", Font.ITALIC, 10));
                g2d.drawString("Merci de votre visite !", x + 25, y);

                return PAGE_EXISTS;
            }
        });

        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(null, "Erreur d'impression : " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Imprime le reçu détaillé pour la gestion interne
    public static void imprimerRecuGestion(Commande commande, List<LigneCommande> lignes) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0)
                    return NO_SUCH_PAGE;
                Graphics2D g = (Graphics2D) graphics;
                g.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                int y = 40;
                int x = 40;
                g.setFont(new Font("Monospaced", Font.BOLD, 13));
                g.drawString("=== FORMAT GESTION ===", x, y);
                y += 20;
                g.setFont(new Font("Monospaced", Font.PLAIN, 11));
                g.drawString("Commande #" + commande.getIdCmde(), x, y);
                y += 15;
                g.drawString("Date : " + commande.getDate(), x, y);
                y += 15;
                g.drawString("Etat : " + commande.getEtat().getLibelle(), x, y);
                y += 20;
                g.drawString("------------------------------------------", x, y);
                y += 15;
                String header = String.format("%-20s %5s %8s %10s", "Produit", "Qte", "P.Unit", "Total");
                g.setFont(new Font("Monospaced", Font.BOLD, 11));
                g.drawString(header, x, y);
                y += 15;
                g.setFont(new Font("Monospaced", Font.PLAIN, 11));
                g.drawString("------------------------------------------", x, y);
                y += 12;
                for (LigneCommande l : lignes) {
                    String nom = l.getProduit() != null ? l.getProduit().getNomPro() : "Produit#" + l.getIdPro();
                    if (nom.length() > 20)
                        nom = nom.substring(0, 17) + "...";
                    String row = String.format("%-20s %5d %8.2f %10.2f",
                            nom, l.getQteLig(), l.getPrixUnit(), l.getMontant());
                    g.drawString(row, x, y);
                    y += 12;
                }
                g.drawString("------------------------------------------", x, y);
                y += 18;
                g.setFont(new Font("Monospaced", Font.BOLD, 13));
                g.drawString(String.format("TOTAL NET : %.2f FCFA", commande.getTotal()), x, y);
                return PAGE_EXISTS;
            }
        });
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(null, "Erreur impression gestion : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void imprimerStatistiques(StatistiquesGenerales stats) {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0)
                    return NO_SUCH_PAGE;

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                int y = 50;
                int x = 50;
                g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
                g2d.drawString("--- STATISTIQUES GLOBALES ---", x, y);
                y += 30;
                g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));

                g2d.drawString("C.A Journalier : " + String.format("%.2f FCFA", stats.getCaJour()), x, y);
                y += 20;
                g2d.drawString("Nombre Commandes (Aujourd'hui) : " + stats.getNbCommandesJour(), x, y);
                y += 20;
                g2d.drawString("Total Produits : " + stats.getNbProduits(), x, y);
                y += 20;
                g2d.drawString("Produits en Alerte : " + stats.getNbProduitsSousSeuil(), x, y);
                y += 20;
                g2d.drawString("-------------------------------------", x, y);

                return PAGE_EXISTS;
            }
        });

        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(null, "Erreur d'impression : " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
