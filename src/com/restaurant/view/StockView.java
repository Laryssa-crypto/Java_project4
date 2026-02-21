package com.restaurant.view;

import com.restaurant.controller.StockController;
import com.restaurant.model.Produit;
import com.restaurant.model.MouvementStock;
import com.restaurant.dao.ProduitDAO;
import com.restaurant.dao.MouvementStockDAO;
import com.restaurant.utils.DesignSystem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StockView extends JPanel {

    private JComboBox<Produit> comboProduits;
    private JTextField txtQuantite, txtMotif;
    private JRadioButton rbEntree, rbSortie;
    private JButton btnValider;
    private JTable tableHistorique;
    private DefaultTableModel tableModel;
    private final StockController controller;

    private final ProduitDAO produitDAO;
    private final MouvementStockDAO mouvementDAO;

    public StockView() {
        this.produitDAO = new ProduitDAO();
        this.mouvementDAO = new MouvementStockDAO();
        initAndLayout();
        this.controller = new StockController(this);
        chargerProduits();
        actualiserHistorique();
    }

    private void initAndLayout() {
        setLayout(new BorderLayout(15, 15));

        JPanel panelForm = new JPanel(new GridLayout(5, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Nouveau Mouvement"));

        panelForm.add(new JLabel("Produit :"));
        comboProduits = new JComboBox<>();
        panelForm.add(comboProduits);

        panelForm.add(new JLabel("Type :"));
        JPanel panelRadio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rbEntree = new JRadioButton("ENTRÉE", true);
        rbSortie = new JRadioButton("SORTIE");
        ButtonGroup group = new ButtonGroup();
        group.add(rbEntree);
        group.add(rbSortie);
        panelRadio.add(rbEntree);
        panelRadio.add(rbSortie);
        panelForm.add(panelRadio);

        panelForm.add(new JLabel("Quantité :"));
        txtQuantite = new JTextField();
        DesignSystem.styleTextField(txtQuantite);
        panelForm.add(txtQuantite);

        panelForm.add(new JLabel("Motif :"));
        txtMotif = new JTextField();
        DesignSystem.styleTextField(txtMotif);
        panelForm.add(txtMotif);

        btnValider = new JButton("Enregistrer le mouvement");
        DesignSystem.styleButton(btnValider, DesignSystem.PRIMARY);
        panelForm.add(new JLabel());
        panelForm.add(btnValider);

        String[] columns = { "ID", "Réf Facture", "Produit", "Type", "Quantité", "Date", "Motif" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableHistorique = new JTable(tableModel);

        javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(tableModel);
        tableHistorique.setRowSorter(sorter);

        JPanel panelHistorique = new JPanel(new BorderLayout());
        JPanel panelRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelRecherche.add(new JLabel("Rechercher:"));
        JTextField txtSearch = new JTextField(20);
        DesignSystem.styleTextField(txtSearch);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void handle() {
                String t = txtSearch.getText();
                sorter.setRowFilter(t.trim().isEmpty()
                        ? null
                        : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(t)));
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                handle();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                handle();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                handle();
            }
        });
        panelRecherche.add(txtSearch);

        panelHistorique.add(panelRecherche, BorderLayout.NORTH);
        panelHistorique.add(new JScrollPane(tableHistorique), BorderLayout.CENTER);
        panelHistorique.setBorder(BorderFactory.createTitledBorder("Historique des mouvements"));

        add(panelForm, BorderLayout.NORTH);
        add(panelHistorique, BorderLayout.CENTER);

        btnValider.addActionListener(e -> controller.enregistrerMouvement());
    }

    public void actualiserHistorique() {
        tableModel.setRowCount(0);
        for (MouvementStock m : mouvementDAO.listerHistorique()) {
            tableModel.addRow(new Object[] {
                    m.getId(), m.getReference(), m.getProduit().getNomPro(), m.getType(),
                    m.getQuantite(), m.getDate(), m.getMotif()
            });
        }
    }

    public void resetChamps() {
        txtQuantite.setText("");
        txtMotif.setText("");
        rbEntree.setSelected(true);
        if (comboProduits.getItemCount() > 0)
            comboProduits.setSelectedIndex(0);
        txtQuantite.requestFocus();
    }

    private void chargerProduits() {
        comboProduits.removeAllItems();
        for (Produit p : produitDAO.getAll())
            comboProduits.addItem(p);
    }

    public Produit getProduitSelectionne() {
        return (Produit) comboProduits.getSelectedItem();
    }

    public String getQuantiteSaisie() {
        return txtQuantite.getText();
    }

    public boolean isEntreeSelectionnee() {
        return rbEntree.isSelected();
    }

    public String getMotifSaisi() {
        return txtMotif.getText();
    }
}
