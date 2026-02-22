package com.restaurant.view;

import com.restaurant.model.Utilisateur;
import com.restaurant.model.enums.Role;
import com.restaurant.service.StatistiqueService;
import com.restaurant.service.StatistiqueService.StatistiquesGenerales;
import com.restaurant.utils.DesignSystem;
import com.restaurant.controller.DatabaseController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.time.LocalDate;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainView extends JFrame {

    private static final Logger logger = LogManager.getLogger(MainView.class);

    private void defineIcon() {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png"));
            if (icon.getImage() != null)
                setIconImage(icon.getImage());
        } catch (Exception e) {
        }
    }

    private Utilisateur utilisateurConnecte;
    private StatistiqueService statistiqueService;
    private JPanel panelContent;

    private JLabel lblCA, lblCommandes, lblAlerte, lblTotalProduits;
    private JLabel lblStatus;
    private ChartPanel chartPanel;
    private JLabel lblStockBadge;
    private static final int TIMEOUT_MS = 10 * 60 * 1000;
    private javax.swing.Timer inactivityTimer;

    public MainView(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        com.restaurant.dao.CommandeDAO cDao = new com.restaurant.dao.CommandeDAO();
        com.restaurant.dao.LigneCommandeDAO lDao = new com.restaurant.dao.LigneCommandeDAO();
        com.restaurant.dao.ProduitDAO pDao = new com.restaurant.dao.ProduitDAO();
        this.statistiqueService = new StatistiqueService(cDao, lDao, pDao);

        setupFrame();
        defineIcon();
        initLayout();
        initAutoLogout();

        if (Role.ADMIN.equals(utilisateurConnecte.getRole())) {
            showView("Dashboard");
        } else {
            showView("Commandes");
        }
    }

    private void setupFrame() {
        setTitle("Gestion Restaurant - Accueil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private boolean isDialogShowing = false;

    private void initAutoLogout() {
        inactivityTimer = new javax.swing.Timer(TIMEOUT_MS, e -> {
            if (isDialogShowing)
                return;
            isDialogShowing = true;

            int rep = JOptionPane.showConfirmDialog(this,
                    "Vous allez être déconnecté pour cause d'inactivité.\nRester connecté ?",
                    "Déconnexion automatique", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            isDialogShowing = false;
            if (rep == JOptionPane.YES_OPTION) {
                inactivityTimer.restart();
            } else {
                // Déconnexion immédiate sans deuxième dialogue
                dispose();
                new com.restaurant.controller.LoginController().afficherLogin();
            }
        });
        inactivityTimer.setRepeats(false);
        inactivityTimer.start();

        AWTEventListener resetListener = event -> {
            if (inactivityTimer != null && !isDialogShowing) {
                inactivityTimer.restart();
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(resetListener,
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    private void initLayout() {
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);

        panelContent = new JPanel(new CardLayout());

        panelContent.add(createDashboardPanel(), "Dashboard");
        panelContent.add(new CommandeView(utilisateurConnecte), "Commandes");
        panelContent.add(new ProduitView(), "Produits");
        panelContent.add(new StockView(), "Stocks");
        panelContent.add(new StatistiqueView(), "Statistiques");
        if (Role.ADMIN.equals(utilisateurConnecte.getRole())) {
            com.restaurant.controller.AdminController adminCtrl = new com.restaurant.controller.AdminController();
            adminCtrl.setUtilisateurConnecte(utilisateurConnecte);
            panelContent.add(new AdminView(adminCtrl), "Employes");
            panelContent.add(new DatabaseView(new DatabaseController()), "Sauvegarde");
        }

        add(panelContent, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(DesignSystem.PRIMARY);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel lblLogo = new JLabel("GESTION RESTAU");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(DesignSystem.FONT_SUBTITLE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblLogo);
        sidebar.add(Box.createVerticalStrut(30));

        if (Role.ADMIN.equals(utilisateurConnecte.getRole())) {
            addSidebarButton(sidebar, "🏠 Accueil", "Dashboard");
        }

        addSidebarButton(sidebar, "💰 Commandes", "Commandes");

        if (Role.ADMIN.equals(utilisateurConnecte.getRole())) {
            addSidebarButton(sidebar, "📦 Produits", "Produits");
            JPanel stockRow = new JPanel(new BorderLayout());
            stockRow.setOpaque(false);
            stockRow.setMaximumSize(new Dimension(230, 45));
            JButton btnStocks = new JButton("📦 Gestion Stock");
            btnStocks.setMaximumSize(new Dimension(190, 45));
            DesignSystem.styleButton(btnStocks, DesignSystem.PRIMARY);
            btnStocks.addActionListener(e -> showView("Stocks"));
            stockRow.add(btnStocks, BorderLayout.CENTER);
            lblStockBadge = new JLabel("");
            lblStockBadge.setForeground(Color.WHITE);
            lblStockBadge.setFont(DesignSystem.FONT_BADGE);
            lblStockBadge.setOpaque(true);
            lblStockBadge.setBackground(DesignSystem.DANGER);
            lblStockBadge.setHorizontalAlignment(SwingConstants.CENTER);
            lblStockBadge.setVisible(false);
            stockRow.add(lblStockBadge, BorderLayout.EAST);
            sidebar.add(stockRow);
            sidebar.add(Box.createVerticalStrut(10));
            addSidebarButton(sidebar, "📉 Statistiques", "Statistiques");
            addSidebarButton(sidebar, "👥 Utilisateurs", "Employes");
            addSidebarButton(sidebar, "💾 Sauvegarde", "Sauvegarde");
        }

        sidebar.add(Box.createVerticalGlue());

        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("Se déconnecter");
        DesignSystem.styleButton(btnLogout, DesignSystem.DANGER);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> seDeconnecter());
        sidebar.add(btnLogout);

        return sidebar;
    }

    private void addSidebarButton(JPanel sidebar, String text, String viewName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(230, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        DesignSystem.styleButton(btn, DesignSystem.PRIMARY);
        btn.addActionListener(e -> showView(viewName));
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(10));
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(DesignSystem.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Tableau de Bord");
        DesignSystem.styleLabel(title, true);
        header.add(title, BorderLayout.WEST);
        panel.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setOpaque(false);
        grid.add(createWidget("Chiffre d'Affaires (Journalier)", lblCA = new JLabel("0.00 F CFA"),
                DesignSystem.PRIMARY));
        grid.add(createWidget("Commandes effectuées (J)", lblCommandes = new JLabel("0"), DesignSystem.SECONDARY));
        grid.add(createWidget("Alertes Stock", lblAlerte = new JLabel("0"), DesignSystem.DANGER));
        grid.add(createWidget("Produits au Total", lblTotalProduits = new JLabel("0"), DesignSystem.SUCCESS));

        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setOpaque(false);
        chartPanel = new ChartPanel(null);
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        chartContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        panel.add(grid, BorderLayout.NORTH);
        panel.add(chartContainer, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createWidget(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(DesignSystem.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DesignSystem.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        JLabel lblT = new JLabel(title);
        lblT.setFont(DesignSystem.FONT_SUBTITLE);
        lblT.setForeground(DesignSystem.TEXT_MUTED);
        valueLabel.setFont(DesignSystem.FONT_HUGE);
        valueLabel.setForeground(color);
        card.add(lblT, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void rafraichirDashboard() {
        setStatus("Actualisation...", DesignSystem.SECONDARY);
        SwingWorker<StatistiquesGenerales, Void> worker = new SwingWorker<StatistiquesGenerales, Void>() {
            private List<StatistiqueService.ProduitVendu> topProduits;

            @Override
            protected StatistiquesGenerales doInBackground() throws Exception {
                // Nettoyage des commandes vides abandonnées
                new com.restaurant.dao.CommandeDAO().deleteEmptyOrders();

                LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
                LocalDate finMois = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
                topProduits = statistiqueService.getTopProduitsQuantite(debutMois, finMois, 5);
                return statistiqueService.getStatistiquesGenerales();
            }

            @Override
            protected void done() {
                try {
                    StatistiquesGenerales stats = get();
                    lblCA.setText(String.format("%.2f F CFA", stats.getCaJour()));
                    lblCommandes.setText(String.valueOf(stats.getNbCommandesJour()));
                    lblAlerte.setText(String.valueOf(stats.getNbProduitsSousSeuil()));
                    lblTotalProduits.setText(String.valueOf(stats.getNbProduits()));

                    int alertCount = stats.getNbProduitsSousSeuil();
                    if (lblStockBadge != null) {
                        if (alertCount > 0) {
                            lblStockBadge.setText(" " + alertCount + " ");
                            lblStockBadge.setVisible(true);
                        } else {
                            lblStockBadge.setVisible(false);
                        }
                    }

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                    if (topProduits != null) {
                        for (StatistiqueService.ProduitVendu pv : topProduits) {
                            dataset.addValue(pv.getQuantiteTotale(), "Quantité", pv.getProduit().getNomPro());
                        }
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Top 5 Produits (Ce mois)", "Produit", "Quantité",
                            dataset, PlotOrientation.VERTICAL, false, true, false);
                    chart.setBackgroundPaint(Color.WHITE);
                    org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
                    plot.setBackgroundPaint(DesignSystem.CHART_PLOT);
                    plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

                    org.jfree.chart.renderer.category.BarRenderer renderer = (org.jfree.chart.renderer.category.BarRenderer) plot
                            .getRenderer();
                    renderer.setSeriesPaint(0, DesignSystem.PRIMARY);
                    renderer.setMaximumBarWidth(0.10); // Thinner bars
                    renderer.setItemMargin(0.10); // More space between bars

                    chartPanel.setChart(chart);

                    setStatus("Système prêt", DesignSystem.SUCCESS);
                } catch (InterruptedException | java.util.concurrent.ExecutionException ex) {
                    logger.warn("Erreur dashboard : " + ex.getMessage());
                    setStatus("Erreur Base de données", DesignSystem.DANGER);
                }
            }
        };
        worker.execute();
    }

    public void setStatus(String message, Color color) {
        if (lblStatus != null) {
            lblStatus.setText("● " + message);
            lblStatus.setForeground(color);
        }
    }

    private void showView(String nom) {
        CardLayout cl = (CardLayout) panelContent.getLayout();
        cl.show(panelContent, nom);
        setTitle("Gestion Restaurant - " + nom);

        if ("Dashboard".equals(nom)) {
            rafraichirDashboard();
        }
    }

    private void seDeconnecter() {
        int rep = JOptionPane.showConfirmDialog(this, "Confirmer la déconnexion ?", "Déconnexion",
                JOptionPane.YES_NO_OPTION);
        if (rep == JOptionPane.YES_OPTION) {
            dispose();
            new com.restaurant.controller.LoginController().afficherLogin();
        }
    }

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, DesignSystem.BORDER));
        bar.setPreferredSize(new Dimension(getWidth(), 30));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        left.setOpaque(false);
        lblStatus = new JLabel("● Système prêt");
        lblStatus.setForeground(DesignSystem.SUCCESS);
        lblStatus.setFont(DesignSystem.FONT_BODY);
        left.add(lblStatus);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        right.setOpaque(false);
        JLabel lblUser = new JLabel("👤 " + utilisateurConnecte.getNomUtil());
        lblUser.setFont(DesignSystem.FONT_BODY);
        lblUser.setForeground(DesignSystem.TEXT_MUTED);

        JLabel lblDate = new JLabel("📅 " + LocalDate.now());
        lblDate.setFont(DesignSystem.FONT_BODY);
        lblDate.setForeground(DesignSystem.TEXT_MUTED);

        right.add(lblUser);
        right.add(new JLabel("|"));
        right.add(lblDate);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }
}
