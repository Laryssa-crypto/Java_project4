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
                int x = 50;
                g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
                g2d.drawString("--- REÇU CLIENT ---", x, y);
                y += 20;
                g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
                g2d.drawString("Commande #" + commande.getIdCmde() + " - " + commande.getDate(), x, y);
                y += 20;
                g2d.drawString("-------------------------------------", x, y);
                y += 20;
                for (LigneCommande l : lignes) {
                    String ligneTxt = l.getProduit().getNomPro() + " x" + l.getQteLig() + " = "
                            + String.format("%.2f", l.getMontant());
                    g2d.drawString(ligneTxt, x, y);
                    y += 15;
                }
                y += 10;
                g2d.drawString("-------------------------------------", x, y);
                y += 20;
                g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
                g2d.drawString("TOTAL: " + String.format("%.2f FCFA", commande.getTotal()), x, y);

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
