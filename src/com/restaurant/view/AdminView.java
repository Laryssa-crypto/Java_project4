package com.restaurant.view;

import com.restaurant.controller.AdminController;
import com.restaurant.model.Utilisateur;
import com.restaurant.model.enums.Role;
import com.restaurant.utils.DesignSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminView extends JPanel {

    private AdminController controller;

    private JTable tableUtilisateurs;
    private DefaultTableModel tableModel;

    private JTextField txtNomUtil;
    private JPasswordField txtMdp;
    private JLabel lblMdp; // label dynamique selon le mode
    private JComboBox<Role> comboRole;
    private int selectedUserId = -1;

    public AdminView(AdminController controller) {
        this.controller = controller;
        this.controller.setView(this);
        initComposants();
        initLayout();
        controller.chargerUtilisateurs();
    }

    private void initComposants() {
        String[] colonnes = { "ID", "Nom d'utilisateur", "Rôle" };
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableUtilisateurs = new JTable(tableModel);
        tableUtilisateurs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableUtilisateurs.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                chargerLigneSelectionnee();
        });

        txtNomUtil = new JTextField(15);
        txtMdp = new JPasswordField(15);
        comboRole = new JComboBox<>(Role.values());
        lblMdp = new JLabel("Mot de passe (obligatoire) :");
    }

    private void initLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelForm = new JPanel(new GridLayout(3, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Informations Utilisateur"));
        panelForm.add(new JLabel("Nom d'utilisateur :"));
        panelForm.add(txtNomUtil);
        panelForm.add(lblMdp);
        panelForm.add(txtMdp);
        panelForm.add(new JLabel("Rôle :"));
        panelForm.add(comboRole);

        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnAjouter = new JButton("Ajouter");
        DesignSystem.styleButton(btnAjouter, DesignSystem.PRIMARY);

        JButton btnModifier = new JButton("Modifier");
        DesignSystem.styleButton(btnModifier, DesignSystem.SECONDARY);

        JButton btnSupprimer = new JButton("Supprimer");
        DesignSystem.styleButton(btnSupprimer, DesignSystem.DANGER);

        JButton btnVider = new JButton("Vider les champs");

        btnAjouter.addActionListener(e -> {
            if (validerSaisie())
                controller.ajouterUtilisateur(txtNomUtil.getText().trim(), new String(txtMdp.getPassword()),
                        (Role) comboRole.getSelectedItem());
        });

        btnModifier.addActionListener(e -> {
            if (selectedUserId == -1) {
                afficherErreur("Veuillez sélectionner un utilisateur à modifier.");
                return;
            }
            if (validerSaisie())
                controller.modifierUtilisateur(selectedUserId, txtNomUtil.getText().trim(),
                        new String(txtMdp.getPassword()), (Role) comboRole.getSelectedItem());
        });

        btnSupprimer.addActionListener(e -> {
            if (selectedUserId == -1) {
                afficherErreur("Veuillez sélectionner un utilisateur à supprimer.");
                return;
            }
            int rep = JOptionPane.showConfirmDialog(this, "Confirmer la suppression ?", "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (rep == JOptionPane.YES_OPTION)
                controller.supprimerUtilisateur(selectedUserId);
        });

        btnVider.addActionListener(e -> viderFormulaire());

        panelActions.add(btnAjouter);
        panelActions.add(btnModifier);
        panelActions.add(btnSupprimer);
        panelActions.add(btnVider);

        JPanel panelHaut = new JPanel(new BorderLayout());
        panelHaut.add(panelForm, BorderLayout.CENTER);
        panelHaut.add(panelActions, BorderLayout.SOUTH);

        JPanel panelTable = new JPanel(new BorderLayout());
        panelTable.setBorder(BorderFactory.createTitledBorder("Liste des Utilisateurs"));
        panelTable.add(new JScrollPane(tableUtilisateurs), BorderLayout.CENTER);

        add(panelHaut, BorderLayout.NORTH);
        add(panelTable, BorderLayout.CENTER);
    }

    private boolean validerSaisie() {
        if (txtNomUtil.getText().trim().isEmpty()) {
            afficherErreur("Le nom d'utilisateur est obligatoire.");
            return false;
        }
        if (selectedUserId == -1 && new String(txtMdp.getPassword()).trim().isEmpty()) {
            afficherErreur("Le mot de passe est obligatoire pour un nouvel utilisateur.");
            return false;
        }
        return true;
    }

    private void chargerLigneSelectionnee() {
        int row = tableUtilisateurs.getSelectedRow();
        if (row != -1) {
            selectedUserId = (int) tableModel.getValueAt(row, 0);
            txtNomUtil.setText((String) tableModel.getValueAt(row, 1));
            comboRole.setSelectedItem((Role) tableModel.getValueAt(row, 2));
            txtMdp.setText(""); // On ne réaffiche pas le mot de passe
            lblMdp.setText("Mot de passe (laisser vide pour ne pas modifier) :");
        }
    }

    private void viderFormulaire() {
        selectedUserId = -1;
        txtNomUtil.setText("");
        txtMdp.setText("");
        comboRole.setSelectedIndex(0);
        tableUtilisateurs.clearSelection();
        lblMdp.setText("Mot de passe (obligatoire) :");
    }

    public void afficherUtilisateurs(List<Utilisateur> utilisateurs) {
        tableModel.setRowCount(0);
        if (utilisateurs != null) {
            for (Utilisateur u : utilisateurs) {
                tableModel.addRow(new Object[] { u.getIdUtil(), u.getNomUtil(), u.getRole() });
            }
        }
    }

    public void afficherMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
        viderFormulaire();
    }

    public void afficherErreur(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
