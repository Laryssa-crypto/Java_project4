/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.view;

import com.restaurant.controller.ProduitController;
import com.restaurant.model.Categorie;
import com.restaurant.model.Produit;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 *
 * @author ASUS
 */


public class ProduitView extends JFrame {
    private ProduitController controller;
    
    // Composants pour la gestion des catégories
    private JTextField txtLibelleCategorie;
    private JButton btnAjouterCategorie;
    private JButton btnModifierCategorie;
    private JButton btnSupprimerCategorie;
    private JTable tableCategoriesSimple;
    private DefaultTableModel modeleCategoriesSimple;
    
    // Composants pour la gestion des produits
    private JTextField txtNomProduit;
    private JComboBox<Categorie> cmbCategorie;
    private JTextField txtPrix;
    private JTextField txtStock;
    private JTextField txtSeuil;
    private JTextField txtRecherche;
    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnRechercher;
    private JButton btnAfficherTous;
    private JButton btnProduitsAlerte;
    private JTable tableProduits;
    private DefaultTableModel modeleProduits;
    
    private Produit produitSelectionne = null;
    private Categorie categorieSelectionnee = null;
    
    public ProduitView() {
        controller = new ProduitController();
        initComponents();
        chargerCategories();
        chargerProduits();
    }
    
    private void initComponents() {
        setTitle("Gestion des Produits et Catégories");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal avec onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Onglet 1 : Gestion des Catégories
        JPanel panelCategories = creerPanelCategories();
        tabbedPane.addTab("📂 Catégories", panelCategories);
        
        // Onglet 2 : Gestion des Produits
        JPanel panelProduits = creerPanelProduits();
        tabbedPane.addTab("🍽️ Produits", panelProduits);
        
        add(tabbedPane);
    }
    
    /**
     * PANEL CATÉGORIES
     */
    private JPanel creerPanelCategories() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel formulaire catégorie
        JPanel panelFormCategorie = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFormCategorie.setBorder(BorderFactory.createTitledBorder("Gestion des Catégories"));
        
        panelFormCategorie.add(new JLabel("Libellé :"));
        txtLibelleCategorie = new JTextField(20);
        panelFormCategorie.add(txtLibelleCategorie);
        
        btnAjouterCategorie = new JButton("➕ Ajouter");
        btnAjouterCategorie.addActionListener(e -> ajouterCategorie());
        panelFormCategorie.add(btnAjouterCategorie);
        
        btnModifierCategorie = new JButton("✏️ Modifier");
        btnModifierCategorie.addActionListener(e -> modifierCategorie());
        panelFormCategorie.add(btnModifierCategorie);
        
        btnSupprimerCategorie = new JButton("🗑️ Supprimer");
        btnSupprimerCategorie.addActionListener(e -> supprimerCategorie());
        panelFormCategorie.add(btnSupprimerCategorie);
        
        panel.add(panelFormCategorie, BorderLayout.NORTH);
        
        // Table des catégories
        String[] colonnesCat = {"ID", "Libellé"};
        modeleCategoriesSimple = new DefaultTableModel(colonnesCat, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableCategoriesSimple = new JTable(modeleCategoriesSimple);
        tableCategoriesSimple.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableCategoriesSimple.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectionnerCategorie();
            }
        });
        
        JScrollPane scrollCategories = new JScrollPane(tableCategoriesSimple);
        panel.add(scrollCategories, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * PANEL PRODUITS
     */
    private JPanel creerPanelProduits() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel du haut : Formulaire
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Informations du Produit"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Nom
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Nom :"), gbc);
        gbc.gridx = 1;
        txtNomProduit = new JTextField(20);
        panelForm.add(txtNomProduit, gbc);
        
        // Catégorie
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Catégorie :"), gbc);
        gbc.gridx = 1;
        cmbCategorie = new JComboBox<>();
        panelForm.add(cmbCategorie, gbc);
        
        // Prix
        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Prix (FCFA) :"), gbc);
        gbc.gridx = 1;
        txtPrix = new JTextField(20);
        panelForm.add(txtPrix, gbc);
        
        // Stock
        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Stock actuel :"), gbc);
        gbc.gridx = 1;
        txtStock = new JTextField(20);
        panelForm.add(txtStock, gbc);
        
        // Seuil
        gbc.gridx = 0; gbc.gridy = 4;
        panelForm.add(new JLabel("Seuil alerte :"), gbc);
        gbc.gridx = 1;
        txtSeuil = new JTextField(20);
        panelForm.add(txtSeuil, gbc);
        
        // Boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAjouter = new JButton("➕ Ajouter");
        btnAjouter.addActionListener(e -> ajouterProduit());
        panelBoutons.add(btnAjouter);
        
        btnModifier = new JButton("✏️ Modifier");
        btnModifier.addActionListener(e -> modifierProduit());
        panelBoutons.add(btnModifier);
        
        btnSupprimer = new JButton("🗑️ Supprimer");
        btnSupprimer.addActionListener(e -> supprimerProduit());
        panelBoutons.add(btnSupprimer);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panelForm.add(panelBoutons, gbc);
        
        panel.add(panelForm, BorderLayout.NORTH);
        
        // Panel du milieu : Recherche
        JPanel panelRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelRecherche.add(new JLabel("🔍 Rechercher :"));
        txtRecherche = new JTextField(25);
        panelRecherche.add(txtRecherche);
        
        btnRechercher = new JButton("Rechercher");
        btnRechercher.addActionListener(e -> rechercherProduits());
        panelRecherche.add(btnRechercher);
        
        btnAfficherTous = new JButton("Afficher Tous");
        btnAfficherTous.addActionListener(e -> chargerProduits());
        panelRecherche.add(btnAfficherTous);
        
        btnProduitsAlerte = new JButton("⚠️ Produits en Alerte");
        btnProduitsAlerte.setForeground(Color.RED);
        btnProduitsAlerte.addActionListener(e -> afficherProduitsAlerte());
        panelRecherche.add(btnProduitsAlerte);
        
        panel.add(panelRecherche, BorderLayout.CENTER);
        
        // Panel du bas : Table
        String[] colonnes = {"ID", "Nom", "Catégorie", "Prix (FCFA)", "Stock", "Seuil", "État"};
        modeleProduits = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableProduits = new JTable(modeleProduits);
        tableProduits.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableProduits.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectionnerProduit();
            }
        });
        
        JScrollPane scrollProduits = new JScrollPane(tableProduits);
        panel.add(scrollProduits, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * MÉTHODES CATÉGORIES
     */
    private void chargerCategories() {
        // Charger dans la combo box
        cmbCategorie.removeAllItems();
        List<Categorie> categories = controller.getAllCategories();
        for (Categorie cat : categories) {
            cmbCategorie.addItem(cat);
        }
        
        // Charger dans la table
        modeleCategoriesSimple.setRowCount(0);
        for (Categorie cat : categories) {
            modeleCategoriesSimple.addRow(new Object[]{
                cat.getIdCat(),
                cat.getLibelleCat()
            });
        }
    }
    
    private void ajouterCategorie() {
        String libelle = txtLibelleCategorie.getText().trim();
        
        if (controller.ajouterCategorie(libelle)) {
            JOptionPane.showMessageDialog(this, "✅ Catégorie ajoutée avec succès !");
            txtLibelleCategorie.setText("");
            chargerCategories();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de l'ajout de la catégorie", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void modifierCategorie() {
        if (categorieSelectionnee == null) {
            JOptionPane.showMessageDialog(this, "⚠️ Veuillez sélectionner une catégorie à modifier", 
                    "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String libelle = txtLibelleCategorie.getText().trim();
        
        if (controller.modifierCategorie(categorieSelectionnee.getIdCat(), libelle)) {
            JOptionPane.showMessageDialog(this, "✅ Catégorie modifiée avec succès !");
            txtLibelleCategorie.setText("");
            categorieSelectionnee = null;
            chargerCategories();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de la modification", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerCategorie() {
        if (categorieSelectionnee == null) {
            JOptionPane.showMessageDialog(this, "⚠️ Veuillez sélectionner une catégorie à supprimer", 
                    "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer cette catégorie ?", 
                "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.supprimerCategorie(categorieSelectionnee.getIdCat())) {
                JOptionPane.showMessageDialog(this, "✅ Catégorie supprimée avec succès !");
                txtLibelleCategorie.setText("");
                categorieSelectionnee = null;
                chargerCategories();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "❌ Impossible de supprimer cette catégorie.\nElle est peut-être utilisée par des produits.", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void selectionnerCategorie() {
        int selectedRow = tableCategoriesSimple.getSelectedRow();
        if (selectedRow >= 0) {
            int idCat = (int) modeleCategoriesSimple.getValueAt(selectedRow, 0);
            categorieSelectionnee = controller.getCategorieById(idCat);
            if (categorieSelectionnee != null) {
                txtLibelleCategorie.setText(categorieSelectionnee.getLibelleCat());
            }
        }
    }
    
    /**
     * MÉTHODES PRODUITS
     */
    private void chargerProduits() {
        modeleProduits.setRowCount(0);
        List<Produit> produits = controller.getAllProduits();
        
        for (Produit p : produits) {
            String etat = p.isSousSeuilAlerte() ? "⚠️ ALERTE" : "✅ OK";
            modeleProduits.addRow(new Object[]{
                p.getIdPro(),
                p.getNomPro(),
                p.getNomCategorie(),
                String.format("%.2f", p.getPrixVente()),
                p.getStockActu(),
                p.getSeuilAlerte(),
                etat
            });
        }
    }
    
    private void ajouterProduit() {
        try {
            String nom = txtNomProduit.getText().trim();
            Categorie cat = (Categorie) cmbCategorie.getSelectedItem();
            
            if (cat == null) {
                JOptionPane.showMessageDialog(this, "⚠️ Veuillez sélectionner une catégorie", 
                        "Attention", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double prix = Double.parseDouble(txtPrix.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            int seuil = Integer.parseInt(txtSeuil.getText().trim());
            
            if (controller.ajouterProduit(nom, cat.getIdCat(), prix, stock, seuil)) {
                JOptionPane.showMessageDialog(this, "✅ Produit ajouté avec succès !");
                viderChamps();
                chargerProduits();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Erreur lors de l'ajout du produit", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Veuillez entrer des valeurs numériques valides", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void modifierProduit() {
        if (produitSelectionne == null) {
            JOptionPane.showMessageDialog(this, "⚠️ Veuillez sélectionner un produit à modifier", 
                    "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String nom = txtNomProduit.getText().trim();
            Categorie cat = (Categorie) cmbCategorie.getSelectedItem();
            
            if (cat == null) {
                JOptionPane.showMessageDialog(this, "⚠️ Veuillez sélectionner une catégorie", 
                        "Attention", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double prix = Double.parseDouble(txtPrix.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            int seuil = Integer.parseInt(txtSeuil.getText().trim());
            
            if (controller.modifierProduit(produitSelectionne.getIdPro(), nom, cat.getIdCat(), 
                    prix, stock, seuil)) {
                JOptionPane.showMessageDialog(this, "✅ Produit modifié avec succès !");
                viderChamps();
                produitSelectionne = null;
                chargerProduits();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Erreur lors de la modification", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Veuillez entrer des valeurs numériques valides", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerProduit() {
        if (produitSelectionne == null) {
            JOptionPane.showMessageDialog(this, "⚠️ Veuillez sélectionner un produit à supprimer", 
                    "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer ce produit ?", 
                "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.supprimerProduit(produitSelectionne.getIdPro())) {
                JOptionPane.showMessageDialog(this, "✅ Produit supprimé avec succès !");
                viderChamps();
                produitSelectionne = null;
                chargerProduits();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Erreur lors de la suppression", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void rechercherProduits() {
        String recherche = txtRecherche.getText().trim();
        modeleProduits.setRowCount(0);
        List<Produit> produits = controller.rechercherProduits(recherche);
        
        for (Produit p : produits) {
            String etat = p.isSousSeuilAlerte() ? "⚠️ ALERTE" : "✅ OK";
            modeleProduits.addRow(new Object[]{
                p.getIdPro(),
                p.getNomPro(),
                p.getNomCategorie(),
                String.format("%.2f", p.getPrixVente()),
                p.getStockActu(),
                p.getSeuilAlerte(),
                etat
            });
        }
    }
    
    private void afficherProduitsAlerte() {
        modeleProduits.setRowCount(0);
        List<Produit> produits = controller.getProduitsSousSeuilAlerte();
        
        if (produits.isEmpty()) {
            JOptionPane.showMessageDialog(this, "✅ Aucun produit en alerte !", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        for (Produit p : produits) {
            modeleProduits.addRow(new Object[]{
                p.getIdPro(),
                p.getNomPro(),
                p.getNomCategorie(),
                String.format("%.2f", p.getPrixVente()),
                p.getStockActu(),
                p.getSeuilAlerte(),
                "⚠️ ALERTE"
            });
        }
        
        JOptionPane.showMessageDialog(this, 
                "⚠️ " + produits.size() + " produit(s) sous le seuil d'alerte !", 
                "Alerte Stock", JOptionPane.WARNING_MESSAGE);
    }
    
    private void selectionnerProduit() {
        int selectedRow = tableProduits.getSelectedRow();
        if (selectedRow >= 0) {
            int idPro = (int) modeleProduits.getValueAt(selectedRow, 0);
            produitSelectionne = controller.getAllProduits().stream()
                    .filter(p -> p.getIdPro() == idPro)
                    .findFirst()
                    .orElse(null);
            
            if (produitSelectionne != null) {
                txtNomProduit.setText(produitSelectionne.getNomPro());
                
                // Sélectionner la catégorie dans la combo
                for (int i = 0; i < cmbCategorie.getItemCount(); i++) {
                    Categorie cat = cmbCategorie.getItemAt(i);
                    if (cat.getIdCat() == produitSelectionne.getIdCat()) {
                        cmbCategorie.setSelectedIndex(i);
                        break;
                    }
                }
                
                txtPrix.setText(String.valueOf(produitSelectionne.getPrixVente()));
                txtStock.setText(String.valueOf(produitSelectionne.getStockActu()));
                txtSeuil.setText(String.valueOf(produitSelectionne.getSeuilAlerte()));
            }
        }
    }
    
    private void viderChamps() {
        txtNomProduit.setText("");
        txtPrix.setText("");
        txtStock.setText("");
        txtSeuil.setText("");
        cmbCategorie.setSelectedIndex(0);
    }
    
    /**
     * Point d'entrée pour tester la vue
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProduitView view = new ProduitView();
            view.setVisible(true);
        });
    }
}