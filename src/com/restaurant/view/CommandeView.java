package com.restaurant.view;

import com.restaurant.model.Commande;
import com.restaurant.model.LigneCommande;
import com.restaurant.model.Produit;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CommandeView extends JPanel {

    private JLabel lblInfoCommande;
    private JLabel lblTotal;
    private JComboBox<Produit> comboProduits;
    private JSpinner spinQuantite;
    private JButton btnAjouterProduit;
    private JButton btnNouvelleCommande;
    private JButton btnValiderCommande;
    private JButton btnAnnulerCommande;
    private JButton btnSupprimerLigne;
    private JButton btnModifierQuantite;
    private JButton btnExporterCSV;
    private JTable tableLignes;
    private DefaultTableModel tableModel;

    private JPanel panelInfo;
    private JPanel panelAjout;
    private JPanel panelActions;
    private JPanel panelTable;

    private com.restaurant.controller.CommandeController controller;

    public CommandeView(com.restaurant.model.Utilisateur utilisateurConnecte) {
        this.controller = new com.restaurant.controller.CommandeController();
        this.controller.setUtilisateurConnecte(utilisateurConnecte);
        this.controller.setView(this);

        initComposants();
        initLayout();
        initListeners();

        controller.rafraichirProduits();
        desactiverModification();
    }

    private void initComposants() {
        panelInfo = new JPanel(new GridLayout(2, 2, 5, 5));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Informations commande"));
        lblInfoCommande = new JLabel("Aucune commande en cours");
        lblTotal = new JLabel("Total: 0.00 F CFA");
        panelInfo.add(new JLabel("Commande:"));
        panelInfo.add(lblInfoCommande);
        panelInfo.add(new JLabel("Total:"));
        panelInfo.add(lblTotal);

        panelAjout = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAjout.setBorder(BorderFactory.createTitledBorder("Ajouter un produit"));

        comboProduits = new JComboBox<>();
        comboProduits.setPreferredSize(new Dimension(300, 30));
        comboProduits.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Produit) {
                    Produit p = (Produit) value;
                    String status = "🟢"; // Dispo
                    if (p.getStockActu() <= 0) {
                        status = "🔴"; // Rupture
                    } else if (p.getStockActu() <= p.getSeuilAlerte()) {
                        status = "🟠"; // Alerte
                    }
                    setText(status + " " + p.getNomPro() + " - Stock: " + p.getStockActu());
                }
                return c;
            }
        });

        spinQuantite = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spinQuantite.setPreferredSize(new Dimension(60, 30));
        btnAjouterProduit = new JButton("Ajouter");
        panelAjout.add(new JLabel("Produit:"));
        panelAjout.add(comboProduits);
        panelAjout.add(new JLabel("Quantité:"));
        panelAjout.add(spinQuantite);
        panelAjout.add(btnAjouterProduit);

        panelActions = new JPanel(new FlowLayout());
        panelActions.setBorder(BorderFactory.createTitledBorder("Actions"));
        btnNouvelleCommande = new JButton("Nouvelle commande");
        btnValiderCommande = new JButton("Valider commande");
        btnAnnulerCommande = new JButton("Annuler commande");
        btnSupprimerLigne = new JButton("Supprimer ligne");
        btnModifierQuantite = new JButton("Modifier quantité");
        btnExporterCSV = new JButton("📊 Exporter (CSV)");
        panelActions.add(btnNouvelleCommande);
        panelActions.add(btnValiderCommande);
        panelActions.add(btnAnnulerCommande);
        panelActions.add(btnSupprimerLigne);
        panelActions.add(btnModifierQuantite);
        panelActions.add(btnExporterCSV);

        panelTable = new JPanel(new BorderLayout());
        panelTable.setBorder(BorderFactory.createTitledBorder("Détail de la commande"));
        String[] colonnes = { "ID", "Produit", "Quantité", "Prix unitaire", "Montant" };
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableLignes = new JTable(tableModel);
        tableLignes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableLignes.getTableHeader().setReorderingAllowed(false);
        tableLignes.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableLignes.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableLignes.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableLignes.getColumnModel().getColumn(3).setPreferredWidth(100);
        tableLignes.getColumnModel().getColumn(4).setPreferredWidth(100);
        panelTable.add(new JScrollPane(tableLignes), BorderLayout.CENTER);
    }

    private void initLayout() {
        setLayout(new BorderLayout());
        JPanel panelHaut = new JPanel(new BorderLayout());
        panelHaut.add(panelInfo, BorderLayout.NORTH);
        panelHaut.add(panelAjout, BorderLayout.CENTER);
        add(panelHaut, BorderLayout.NORTH);
        add(panelTable, BorderLayout.CENTER);
        add(panelActions, BorderLayout.SOUTH);
    }

    private void initListeners() {
        btnAjouterProduit.addActionListener(e -> ajouterProduit());
        btnNouvelleCommande.addActionListener(e -> creerNouvelleCommande());
        btnValiderCommande.addActionListener(e -> validerCommande());
        btnAnnulerCommande.addActionListener(e -> annulerCommande());
        btnSupprimerLigne.addActionListener(e -> supprimerLigne());
        btnModifierQuantite.addActionListener(e -> modifierQuantite());
        btnExporterCSV.addActionListener(e -> exporterCommandesCSV());
    }

    private void ajouterProduit() {
        if (controller.getCommandeEnCours() == null) {
            int rep = JOptionPane.showConfirmDialog(this,
                    "Aucune commande n'est en cours. Voulez-vous en créer une nouvelle ?",
                    "Nouvelle Commande", JOptionPane.YES_NO_OPTION);
            if (rep == JOptionPane.YES_OPTION) {
                creerNouvelleCommande();
                if (controller.getCommandeEnCours() == null)
                    return;
            } else {
                return;
            }
        }

        Produit produit = (Produit) comboProduits.getSelectedItem();
        if (produit == null) {
            afficherErreur("Veuillez sélectionner un produit");
            return;
        }
        if (produit.getStockActu() <= 0) {
            afficherErreur("Stock épuisé pour ce produit ! Impossible de l'ajouter.");
            return;
        }
        int qteSaisie = (Integer) spinQuantite.getValue();
        if (qteSaisie > produit.getStockActu()) {
            afficherErreur("Quantité insuffisante ! Stock disponible : " + produit.getStockActu());
            return;
        }
        controller.ajouterProduit(produit.getIdPro(), qteSaisie);
    }

    private void creerNouvelleCommande() {
        if (controller.creerNouvelleCommande())
            activerModification();
    }

    private void validerCommande() {
        int rep = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment valider cette commande ?",
                "Validation commande", JOptionPane.YES_NO_OPTION);
        if (rep == JOptionPane.YES_OPTION)
            controller.validerCommande();
    }

    private void annulerCommande() {
        int rep = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment annuler cette commande ?",
                "Annulation commande", JOptionPane.YES_NO_OPTION);
        if (rep == JOptionPane.YES_OPTION)
            controller.annulerCommande();
    }

    private void supprimerLigne() {
        int row = tableLignes.getSelectedRow();
        if (row == -1) {
            afficherErreur("Veuillez sélectionner une ligne à supprimer");
            return;
        }
        int idLigne = (int) tableModel.getValueAt(row, 0);
        int rep = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cette ligne ?",
                "Suppression ligne", JOptionPane.YES_NO_OPTION);
        if (rep == JOptionPane.YES_OPTION)
            controller.supprimerLigne(idLigne);
    }

    private void modifierQuantite() {
        int row = tableLignes.getSelectedRow();
        if (row == -1) {
            afficherErreur("Veuillez sélectionner une ligne à modifier");
            return;
        }
        String input = JOptionPane.showInputDialog(this, "Nouvelle quantité:", "Modification quantité",
                JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                int qte = Integer.parseInt(input.trim());
                if (qte <= 0) {
                    afficherErreur("La quantité doit être supérieure à 0");
                    return;
                }
                controller.modifierQuantiteLigne((int) tableModel.getValueAt(row, 0), qte);
            } catch (NumberFormatException e) {
                afficherErreur("Veuillez saisir un nombre valide");
            }
        }
    }

    private void exporterCommandesCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exporter les commandes en CSV");
        chooser.setSelectedFile(new java.io.File("Commandes_" + java.time.LocalDate.now() + ".csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".csv"))
                path += ".csv";
            controller.exporterToutesLesCommandesCSV(path);
        }
    }

    public void afficherCommande(Commande commande) {
        if (commande != null) {
            lblInfoCommande.setText("Commande #" + commande.getIdCmde() + " - " + commande.getDate() + " - "
                    + commande.getEtat().getLibelle());
            lblTotal.setText(String.format("Total: %.2f F CFA", commande.getTotal()));
        } else {
            lblInfoCommande.setText("Aucune commande en cours");
            lblTotal.setText("Total: 0.00 F CFA");
        }
    }

    public void afficherLignes(List<LigneCommande> lignes) {
        tableModel.setRowCount(0);
        if (lignes != null) {
            for (LigneCommande l : lignes) {
                tableModel.addRow(new Object[] {
                        l.getIdLig(),
                        l.getProduit() != null ? l.getProduit().getNomPro() : "Produit #" + l.getIdPro(),
                        l.getQteLig(),
                        String.format("%.2f F CFA", l.getPrixUnit()),
                        String.format("%.2f F CFA", l.getMontant())
                });
            }
        }
    }

    public void viderLignes() {
        tableModel.setRowCount(0);
    }

    public void mettreAJourProduits(List<Produit> produits) {
        comboProduits.removeAllItems();
        if (produits != null) {
            for (Produit p : produits) {
                comboProduits.addItem(p);
            }
        }
    }

    public void activerModification() {
        comboProduits.setEnabled(true);
        spinQuantite.setEnabled(true);
        btnAjouterProduit.setEnabled(true);
        btnValiderCommande.setEnabled(true);
        btnAnnulerCommande.setEnabled(true);
        btnSupprimerLigne.setEnabled(true);
        btnModifierQuantite.setEnabled(true);
    }

    public void desactiverModification() {
        comboProduits.setEnabled(false);
        spinQuantite.setEnabled(false);
        btnAjouterProduit.setEnabled(true); // Keep enabled for the prompt
        btnValiderCommande.setEnabled(false);
        btnAnnulerCommande.setEnabled(false);
        btnSupprimerLigne.setEnabled(false);
        btnModifierQuantite.setEnabled(false);
    }

    public void afficherMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void reinitialiserVues() {
        if (comboProduits.getItemCount() > 0) {
            comboProduits.setSelectedIndex(0);
        }
        spinQuantite.setValue(1);
    }

    public Produit getProduitSelectionne() {
        return (Produit) comboProduits.getSelectedItem();
    }

    public int getQuantiteSaisie() {
        return (Integer) spinQuantite.getValue();
    }

    public void afficherErreur(String err) {
        JOptionPane.showMessageDialog(this, err, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
