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
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph title = new Paragraph("FACTURE RESTAURANT", boldFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Commande #" + c.getIdCmde()));
            document.add(new Paragraph("Date: " + c.getDate()));
            String caissierName = c.getNomUtil() != null ? c.getNomUtil() : "Inconnu";
            document.add(new Paragraph("Caissier: " + caissierName));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            try {
                table.setWidths(new float[] { 5f, 2f, 3f });
            } catch (Exception ignored) {
            }

            table.addCell(new Phrase("Article", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            table.addCell(new Phrase("Qté", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            table.addCell(new Phrase("Montant", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));

            for (LigneCommande l : lignes) {
                table.addCell(l.getProduit().getNomPro());
                table.addCell(String.valueOf(l.getQteLig()));
                table.addCell(l.getMontant() + " F");
            }
            document.add(table);

            document.add(new Paragraph(" "));
            Paragraph total = new Paragraph("TOTAL: " + c.getTotal() + " FCFA",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Merci de votre visite !",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

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
