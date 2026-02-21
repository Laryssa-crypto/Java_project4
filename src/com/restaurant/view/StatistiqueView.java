package com.restaurant.view;

import com.restaurant.utils.DesignSystem;
import com.restaurant.model.Produit;
import com.restaurant.service.StatistiqueService.ProduitVendu;
import com.restaurant.service.StatistiqueService.StatistiquesGenerales;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.io.File;
import java.awt.BasicStroke;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

public class StatistiqueView extends JPanel {

    private JTabbedPane tabbedPane;

    private JPanel panelGeneral;
    private JLabel lblNbProduits, lblNbProduitsRupture, lblNbProduitsSousSeuil;
    private JLabel lblNbCommandesJour, lblCaJour, lblNbCommandesEnCours;

    private JPanel panelCA;
    private JDateChooser dateChooserCAJour, dateChooserCADebut, dateChooserCAFin;
    private JButton btnCalculerCAJour, btnCalculerCAPeriode;
    private JLabel lblResultatCAJour, lblResultatCAPeriode;
    private JPanel panelChartContainer;

    private JPanel panelTopProduits;
    private JDateChooser dateChooserTopDebut, dateChooserTopFin;
    private JSpinner spinLimiteTop;
    private JButton btnCalculerTopQuantite, btnCalculerTopMontant;
    private JTable tableTopProduits;
    private DefaultTableModel tableModelTop;

    private JPanel panelStocks;
    private JTable tableProduitsRupture, tableProduitsSousSeuil;
    private DefaultTableModel tableModelRupture, tableModelSousSeuil;

    private JPanel panelAnalyses;
    private JPanel panelChartHeures, panelChartCaissiers;
    private JDateChooser dateChooserAnaDebut, dateChooserAnaFin;
    private JButton btnCalculerAnalyses;

    private com.restaurant.controller.StatistiqueController controller;

    public StatistiqueView() {
        this.controller = new com.restaurant.controller.StatistiqueController();
        this.controller.setView(this);
        initComposants();
        initLayout();
        initListeners();
        controller.rafraichirToutesStatistiques();
    }

    private void initComposants() {
        panelGeneral = new JPanel(new GridLayout(3, 2, 20, 20));
        panelGeneral.setBackground(DesignSystem.BACKGROUND);
        panelGeneral.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        lblNbProduits = createStatLabel("Total Produits");
        lblNbProduitsRupture = createStatLabel("En Rupture");
        lblNbProduitsSousSeuil = createStatLabel("Sous Seuil Alerte");
        lblNbCommandesJour = createStatLabel("Commandes du jour");
        lblCaJour = createStatLabel("CA du jour");
        lblNbCommandesEnCours = createStatLabel("En cours");

        panelGeneral.add(creerCarteStat(lblNbProduits, DesignSystem.PRIMARY));
        panelGeneral.add(creerCarteStat(lblNbProduitsRupture, DesignSystem.DANGER));
        panelGeneral.add(creerCarteStat(lblNbProduitsSousSeuil, DesignSystem.WARNING));
        panelGeneral.add(creerCarteStat(lblNbCommandesJour, DesignSystem.SUCCESS));
        panelGeneral.add(creerCarteStat(lblCaJour, DesignSystem.PRIMARY));
        panelGeneral.add(creerCarteStat(lblNbCommandesEnCours, DesignSystem.SECONDARY));

        panelCA = new JPanel(new BorderLayout());
        JPanel panelCAForm = new JPanel(new GridLayout(2, 3, 10, 10));
        panelCAForm.setBorder(BorderFactory.createTitledBorder("Calcul du chiffre d'affaires"));
        dateChooserCAJour = new JDateChooser();
        dateChooserCADebut = new JDateChooser();
        dateChooserCAFin = new JDateChooser();
        panelCAForm.add(new JLabel("CA par jour:"));
        panelCAForm.add(dateChooserCAJour);
        panelCAForm.add(btnCalculerCAJour = new JButton("Calculer"));
        panelCAForm.add(new JLabel("CA par période:"));
        panelCAForm.add(dateChooserCADebut);
        panelCAForm.add(dateChooserCAFin);

        JPanel panelCAResultats = new JPanel(new GridLayout(2, 2, 10, 10));
        panelCAResultats.setBorder(BorderFactory.createTitledBorder("Résultats bruts"));
        lblResultatCAJour = new JLabel("CA du jour: 0.00 F CFA");
        lblResultatCAPeriode = new JLabel("CA de la période: 0.00 F CFA");
        lblResultatCAJour.setFont(DesignSystem.FONT_SUBTITLE);
        lblResultatCAPeriode.setFont(DesignSystem.FONT_SUBTITLE);
        panelCAResultats.add(lblResultatCAJour);
        panelCAResultats.add(lblResultatCAPeriode);
        btnCalculerCAPeriode = new JButton("Calculer période");
        panelCAResultats.add(new JLabel()); // Spacer
        panelCAResultats.add(btnCalculerCAPeriode);

        panelChartContainer = new JPanel(new BorderLayout());
        panelChartContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel panelCAContent = new JPanel(new BorderLayout());
        panelCAContent.add(panelCAResultats, BorderLayout.NORTH);
        panelCAContent.add(panelChartContainer, BorderLayout.CENTER);

        panelCA.add(panelCAForm, BorderLayout.NORTH);
        panelCA.add(panelCAContent, BorderLayout.CENTER);

        panelTopProduits = new JPanel(new BorderLayout());
        JPanel panelTopForm = new JPanel(new FlowLayout());
        panelTopForm.setBorder(BorderFactory.createTitledBorder("Période d'analyse"));
        dateChooserTopDebut = new JDateChooser();
        dateChooserTopFin = new JDateChooser();
        spinLimiteTop = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        btnCalculerTopQuantite = new JButton("Top par quantité");
        btnCalculerTopMontant = new JButton("Top par montant");
        panelTopForm.add(new JLabel("Du:"));
        panelTopForm.add(dateChooserTopDebut);
        panelTopForm.add(new JLabel("Au:"));
        panelTopForm.add(dateChooserTopFin);
        panelTopForm.add(new JLabel("Limite:"));
        panelTopForm.add(spinLimiteTop);
        panelTopForm.add(btnCalculerTopQuantite);
        panelTopForm.add(btnCalculerTopMontant);
        String[] colonnesTop = { "Produit", "Quantité totale", "Montant total" };
        tableModelTop = new DefaultTableModel(colonnesTop, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableTopProduits = new JTable(tableModelTop);
        panelTopProduits.add(panelTopForm, BorderLayout.NORTH);
        panelTopProduits.add(new JScrollPane(tableTopProduits), BorderLayout.CENTER);

        panelStocks = new JPanel(new BorderLayout());
        JPanel panelStocksTables = new JPanel(new GridLayout(1, 2, 10, 10)); // 2 colonnes

        String[] colonnesRupture = { "ID", "Produit", "Stock actuel" };
        tableModelRupture = new DefaultTableModel(colonnesRupture, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableProduitsRupture = new JTable(tableModelRupture);
        String[] colonnesSousSeuil = { "ID", "Produit", "Stock", "Seuil" };
        tableModelSousSeuil = new DefaultTableModel(colonnesSousSeuil, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableProduitsSousSeuil = new JTable(tableModelSousSeuil);

        JScrollPane scrollRupture = new JScrollPane(tableProduitsRupture);
        scrollRupture.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Produits en Rupture"));
        JScrollPane scrollAlerte = new JScrollPane(tableProduitsSousSeuil);
        scrollAlerte.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Produits en Alerte"));

        panelStocksTables.add(scrollRupture);
        panelStocksTables.add(scrollAlerte);

        panelStocks.add(panelStocksTables, BorderLayout.CENTER);

        JPanel panelOngletGeneral = new JPanel(new BorderLayout());
        panelOngletGeneral.add(panelGeneral, BorderLayout.CENTER);
        JButton btnImprimerStats = new JButton("🖨️ Imprimer les Statistiques");
        btnImprimerStats.setFont(DesignSystem.FONT_BUTTON);
        btnImprimerStats.setBackground(DesignSystem.PRIMARY);
        btnImprimerStats.setForeground(Color.WHITE);
        btnImprimerStats.addActionListener(e -> controller.imprimerStatistiques());

        JButton btnExportCSV = new JButton("📊 Exporter CSV");
        btnExportCSV.setFont(DesignSystem.FONT_BUTTON);
        btnExportCSV.setBackground(DesignSystem.SUCCESS);
        btnExportCSV.setForeground(Color.WHITE);
        btnExportCSV.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Sauvegarder les Statistiques en CSV");
            chooser.setSelectedFile(new File("Statistiques_" + LocalDate.now() + ".csv"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".csv"))
                    path += ".csv";
                controller.exporterCSVStatistiques(path);
            }
        });

        JButton btnExportPDF = new JButton("📄 Exporter PDF");
        btnExportPDF.setFont(DesignSystem.FONT_BUTTON);
        btnExportPDF.setBackground(DesignSystem.SECONDARY);
        btnExportPDF.setForeground(Color.WHITE);
        btnExportPDF.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Sauvegarder les Statistiques en PDF");
            chooser.setSelectedFile(new File("Statistiques_" + LocalDate.now() + ".pdf"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
                controller.exporterPDFStatistiques(chooser.getSelectedFile().getAbsolutePath());
        });

        JPanel panelBtn = new JPanel();
        panelBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnImprimerAncien = new JButton("🖨️ Imprimer Ancien Reçu");
        btnImprimerAncien.setFont(DesignSystem.FONT_BUTTON);
        btnImprimerAncien.setBackground(DesignSystem.WARNING);
        btnImprimerAncien.setForeground(Color.BLACK);
        btnImprimerAncien.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog(this, "Entrez l'ID de la commande à imprimer :");
            if (idStr != null && !idStr.trim().isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr.trim());
                    controller.imprimerAncienneCommande(id);
                } catch (NumberFormatException ex) {
                    afficherErreur("Veuillez saisir un ID numérique valide.");
                }
            }
        });

        panelBtn.add(btnImprimerAncien);
        panelBtn.add(btnImprimerStats);
        panelBtn.add(btnExportCSV);
        panelBtn.add(btnExportPDF);
        panelOngletGeneral.add(panelBtn, BorderLayout.SOUTH);

        panelAnalyses = new JPanel(new BorderLayout());
        JPanel panelAnaForm = new JPanel(new FlowLayout());
        panelAnaForm.setBorder(BorderFactory.createTitledBorder("Période d'analyse avancée"));
        dateChooserAnaDebut = new JDateChooser();
        dateChooserAnaFin = new JDateChooser();
        btnCalculerAnalyses = new JButton("Calculer");
        panelAnaForm.add(new JLabel("Du:"));
        panelAnaForm.add(dateChooserAnaDebut);
        panelAnaForm.add(new JLabel("Au:"));
        panelAnaForm.add(dateChooserAnaFin);
        panelAnaForm.add(btnCalculerAnalyses);

        JPanel panelAnaCharts = new JPanel(new GridLayout(1, 2, 10, 10));
        panelChartHeures = new JPanel(new BorderLayout());
        panelChartCaissiers = new JPanel(new BorderLayout());
        panelChartHeures.setBorder(BorderFactory.createTitledBorder("Heures de pointe"));
        panelChartCaissiers.setBorder(BorderFactory.createTitledBorder("Ventes par Caissier"));
        panelAnaCharts.add(panelChartHeures);
        panelAnaCharts.add(panelChartCaissiers);

        panelAnalyses.add(panelAnaForm, BorderLayout.NORTH);
        panelAnalyses.add(panelAnaCharts, BorderLayout.CENTER);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Statistiques générales", panelOngletGeneral);
        tabbedPane.addTab("Chiffre d'affaires", panelCA);
        tabbedPane.addTab("Top produits", panelTopProduits);
        tabbedPane.addTab("Alertes & Ruptures", panelStocks);
        tabbedPane.addTab("Analyses Avancées", panelAnalyses);
    }

    private void initLayout() {
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void initListeners() {
        btnCalculerCAJour.addActionListener(e -> {
            LocalDate d = dateChooserCAJour.getDate();
            if (d == null) {
                afficherErreur("Veuillez sélectionner une date.");
                return;
            }
            controller.afficherChiffreAffairesJour(d);
        });

        btnCalculerCAPeriode.addActionListener(e -> {
            LocalDate d1 = dateChooserCADebut.getDate();
            LocalDate d2 = dateChooserCAFin.getDate();
            if (d1 == null || d2 == null) {
                afficherErreur("Veuillez sélectionner les deux dates.");
                return;
            }
            if (d1.isAfter(d2)) {
                afficherErreur("La date de début doit précéder la date de fin.");
                return;
            }
            controller.afficherChiffreAffairesPeriode(d1, d2);
        });

        btnCalculerTopQuantite.addActionListener(e -> {
            LocalDate d1 = dateChooserTopDebut.getDate();
            LocalDate d2 = dateChooserTopFin.getDate();
            if (d1 == null || d2 == null)
                return;
            controller.afficherTopProduitsQuantitePeriode(d1, d2, (Integer) spinLimiteTop.getValue());
        });

        btnCalculerTopMontant.addActionListener(e -> {
            LocalDate d1 = dateChooserTopDebut.getDate();
            LocalDate d2 = dateChooserTopFin.getDate();
            if (d1 == null || d2 == null)
                return;
            controller.afficherTopProduitsMontantPeriode(d1, d2, (Integer) spinLimiteTop.getValue());
        });
        // btnRafraichirStocks removed

        btnCalculerAnalyses.addActionListener(e -> {
            LocalDate d = dateChooserAnaDebut.getDate();
            LocalDate f = dateChooserAnaFin.getDate();
            if (d != null && f != null) {
                controller.afficherHeuresDePointe(d, f);
                controller.afficherVentesParCaissier(d, f);
            }
        });

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 3) {
                controller.rafraichirAlertesEtRuptures();
            }
        });
    }

    private JLabel createStatLabel(String title) {
        JLabel label = new JLabel("0");
        label.setFont(DesignSystem.FONT_TITLE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JPanel creerCarteStat(JLabel label, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DesignSystem.BORDER, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        String title = "";
        if (label == lblNbProduits)
            title = "Total Produits";
        else if (label == lblNbProduitsRupture)
            title = "En Rupture";
        else if (label == lblNbProduitsSousSeuil)
            title = "Alerte Stock";
        else if (label == lblNbCommandesJour)
            title = "Cmdes Jour";
        else if (label == lblCaJour)
            title = "CA Jour";
        else if (label == lblNbCommandesEnCours)
            title = "En cours";

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(DesignSystem.FONT_SUBTITLE);
        lblTitle.setForeground(DesignSystem.TEXT_MUTED);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        label.setForeground(color);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(label, BorderLayout.CENTER);
        return card;
    }

    public void afficherStatistiquesGenerales(StatistiquesGenerales stats) {
        if (stats == null)
            return;
        lblNbProduits.setText(String.valueOf(stats.getNbProduits()));
        lblNbProduitsRupture.setText(String.valueOf(stats.getNbProduitsRupture()));
        lblNbProduitsSousSeuil.setText(String.valueOf(stats.getNbProduitsSousSeuil()));
        lblNbCommandesJour.setText(String.valueOf(stats.getNbCommandesJour()));
        lblCaJour.setText(String.format("%.2f F CFA", stats.getCaJour()));
        lblNbCommandesEnCours.setText(String.valueOf(stats.getNbCommandesEnCours()));
    }

    public void afficherChiffreAffairesJour(LocalDate date, double ca) {
        lblResultatCAJour.setText(String.format("CA du %s: %.2f F CFA", date, ca));
        java.util.Map<LocalDate, Double> map = new java.util.TreeMap<>();
        map.put(date, ca);
        mettreAJourGraphique("CA Journalier", date.toString(), map);
    }

    public void afficherChiffreAffairesPeriode(LocalDate debut, LocalDate fin, double ca,
            java.util.Map<LocalDate, Double> journalier) {
        lblResultatCAPeriode.setText(String.format("CA du %s au %s: %.2f F CFA", debut, fin, ca));
        mettreAJourGraphique("CA sur Période", debut + " au " + fin, journalier);
    }

    private void mettreAJourGraphique(String titre, String categorie, java.util.Map<LocalDate, Double> valeurs) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (java.util.Map.Entry<LocalDate, Double> entry : valeurs.entrySet()) {
            dataset.addValue(entry.getValue(), "Chiffre d'Affaires", entry.getKey().toString());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                titre,
                "Période",
                "Chiffre d'Affaires (FCFA)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Fix Y-axis to start at 0
        org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        rangeAxis.setLowerBound(0.0);

        org.jfree.chart.renderer.category.LineAndShapeRenderer renderer = new org.jfree.chart.renderer.category.LineAndShapeRenderer();
        renderer.setSeriesPaint(0, DesignSystem.PRIMARY);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        plot.setRenderer(renderer);

        panelChartContainer.removeAll();
        panelChartContainer.add(new ChartPanel(chart), BorderLayout.CENTER);
        panelChartContainer.revalidate();
        panelChartContainer.repaint();
    }

    public void afficherHeuresDePointe(LocalDate debut, LocalDate fin, java.util.Map<Integer, Integer> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i <= 23; i++) {
            dataset.addValue(data.getOrDefault(i, 0), "Commandes", i + "h");
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Heures de Pointe",
                "Heure de la journée",
                "Nombre de commandes",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Fix Y-axis to start at 0
        org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        rangeAxis.setLowerBound(0.0);

        org.jfree.chart.renderer.category.LineAndShapeRenderer renderer = new org.jfree.chart.renderer.category.LineAndShapeRenderer();
        renderer.setSeriesPaint(0, DesignSystem.SUCCESS);
        plot.setRenderer(renderer);

        panelChartHeures.removeAll();
        panelChartHeures.add(new ChartPanel(chart), BorderLayout.CENTER);
        panelChartHeures.revalidate();
        panelChartHeures.repaint();
    }

    public void afficherVentesParCaissier(LocalDate debut, LocalDate fin, java.util.Map<String, Double> data) {
        org.jfree.data.general.DefaultPieDataset<String> dataset = new org.jfree.data.general.DefaultPieDataset<>();
        for (java.util.Map.Entry<String, Double> entry : data.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Répartition des Ventes par Caissier",
                dataset,
                true, true, false);

        @SuppressWarnings("unchecked")
        org.jfree.chart.plot.PiePlot<String> plot = (org.jfree.chart.plot.PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setSectionPaint("Inconnu", Color.GRAY);

        panelChartCaissiers.removeAll();
        panelChartCaissiers.add(new ChartPanel(chart), BorderLayout.CENTER);
        panelChartCaissiers.revalidate();
        panelChartCaissiers.repaint();
    }

    private void afficherTopProduits(List<ProduitVendu> top) {
        tableModelTop.setRowCount(0);
        if (top != null)
            for (ProduitVendu pv : top)
                tableModelTop.addRow(new Object[] { pv.getProduit().getNomPro(), pv.getQuantiteTotale(),
                        String.format("%.2f F CFA", pv.getMontantTotal()) });
    }

    public void afficherTopProduitsQuantite(List<ProduitVendu> top) {
        afficherTopProduits(top);
    }

    public void afficherTopProduitsMontant(List<ProduitVendu> top) {
        afficherTopProduits(top);
    }

    public void afficherTopProduitsQuantitePeriode(LocalDate d, LocalDate f, List<ProduitVendu> top) {
        afficherTopProduits(top);
    }

    public void afficherTopProduitsMontantPeriode(LocalDate d, LocalDate f, List<ProduitVendu> top) {
        afficherTopProduits(top);
    }

    public void afficherStatistiquesPersonnalisees(LocalDate d, LocalDate f, double ca, List<ProduitVendu> top) {
        afficherTopProduits(top);
    }

    public void afficherProduitsRupture(List<Produit> produits) {
        tableModelRupture.setRowCount(0);
        if (produits != null)
            for (Produit p : produits)
                tableModelRupture.addRow(new Object[] { p.getIdPro(), p.getNomPro(), p.getStockActu() });
    }

    public void afficherProduitsSousSeuil(List<Produit> produits) {
        tableModelSousSeuil.setRowCount(0);
        if (produits != null)
            for (Produit p : produits)
                tableModelSousSeuil
                        .addRow(new Object[] { p.getIdPro(), p.getNomPro(), p.getStockActu(), p.getSeuilAlerte() });
    }

    public void afficherMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void afficherErreur(String err) {
        JOptionPane.showMessageDialog(this, err, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}

class JDateChooser extends JPanel {

    private JTextField dateField;

    public JDateChooser() {
        setLayout(new BorderLayout());
        dateField = new JTextField(LocalDate.now().toString());
        JButton btn = new JButton("...");
        btn.setPreferredSize(new Dimension(30, 20));
        add(dateField, BorderLayout.CENTER);
        add(btn, BorderLayout.EAST);

        btn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Saisir la date (AAAA-MM-JJ):", dateField.getText());
            if (input != null && !input.trim().isEmpty()) {
                try {
                    LocalDate.parse(input.trim());
                    dateField.setText(input.trim());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Format invalide ! Utilisez AAAA-MM-JJ", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void setDate(LocalDate date) {
        dateField.setText(date.toString());
    }

    public LocalDate getDate() {
        try {
            return LocalDate.parse(dateField.getText());
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}
