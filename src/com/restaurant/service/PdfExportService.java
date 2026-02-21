package com.restaurant.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.restaurant.model.Commande;
import com.restaurant.model.LigneCommande;
import com.restaurant.model.Produit;
import com.restaurant.service.StatistiqueService.StatistiquesGenerales;
import com.restaurant.service.StatistiqueService.ProduitVendu;
import java.io.FileOutputStream;
import java.util.List;

public class PdfExportService {

    // Génère un fichier PDF pour un reçu de commande
    public static void genererRecuPDF(Commande c, List<LigneCommande> lignes, String path) {
        Document document = new Document(PageSize.A6);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            document.add(new Paragraph("RECU COMMANDE #" + c.getIdCmde()));
            document.add(new Paragraph("Date: " + c.getDate()));
            document.add(new Paragraph("Caissier: " + c.getNomUtil()));
            document.add(new Paragraph("----------------------------"));
            for (LigneCommande l : lignes) {
                document.add(new Paragraph(
                        l.getProduit().getNomPro() + " x" + l.getQteLig() + " : " + l.getMontant() + " FCFA"));
            }
            document.add(new Paragraph("----------------------------"));
            document.add(new Paragraph("TOTAL: " + c.getTotal() + " FCFA"));
            document.close();
        } catch (Exception e) {
        }
    }

    // Exporte les statistiques globales dans un fichier PDF
    public static void exporterStatistiquesPDF(String path, StatistiquesGenerales stats, List<ProduitVendu> topQte,
            List<ProduitVendu> topMontant, List<Produit> rupture, List<Produit> sousSeuil) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            document.add(new Paragraph("RAPPORT DE STATISTIQUES", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("CA Jour: " + stats.getCaJour() + " FCFA"));
            document.add(new Paragraph("Commandes Jour: " + stats.getNbCommandesJour()));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("TOP PRODUITS", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            PdfPTable table = new PdfPTable(3);
            table.addCell("Produit");
            table.addCell("Quantité");
            table.addCell("Total");
            for (ProduitVendu pv : topQte) {
                table.addCell(pv.getProduit().getNomPro());
                table.addCell(String.valueOf(pv.getQuantiteTotale()));
                table.addCell(String.valueOf(pv.getMontantTotal()));
            }
            document.add(table);
            document.close();
        } catch (Exception e) {
        }
    }
}
