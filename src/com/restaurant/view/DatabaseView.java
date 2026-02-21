package com.restaurant.view;

import com.restaurant.controller.DatabaseController;
import com.restaurant.utils.DesignSystem;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;

public class DatabaseView extends JPanel {

    private DatabaseController controller;

    private JButton btnSauvegarder;
    private JButton btnRestaurer;

    public DatabaseView(DatabaseController controller) {
        this.controller = controller;
        this.controller.setView(this);
        initComposants();
        initLayout();
    }

    private void initComposants() {
        btnSauvegarder = new JButton("💾 Exporter la Sauvegarde");
        DesignSystem.styleButton(btnSauvegarder, DesignSystem.PRIMARY);
        btnSauvegarder.setFont(DesignSystem.FONT_BUTTON);

        btnRestaurer = new JButton("🔄 Restaurer le Système");
        DesignSystem.styleButton(btnRestaurer, DesignSystem.DANGER);
        btnRestaurer.setFont(DesignSystem.FONT_BUTTON);

        btnSauvegarder.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Sauvegarder l'historique SQL");
            chooser.setSelectedFile(new File("Sauvegarde_" + LocalDate.now() + ".sql"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".sql")) {
                    path += ".sql";
                }
                btnSauvegarder.setEnabled(false);
                controller.sauvegarderBaseDeDonnees(path);
            }
        });

        btnRestaurer.addActionListener(e -> {
            int rep = JOptionPane.showConfirmDialog(this,
                    "ATTENTION : Restaurer la base remplacera TOUTES les données actuelles par celles du fichier.\n" +
                            "Voulez-vous vraiment continuer avec la restauration ?",
                    "Confirmation de Restauration", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (rep == JOptionPane.YES_OPTION) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Sélectionner le fichier SQL à restaurer");
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    btnRestaurer.setEnabled(false);
                    controller.restaurerBaseDeDonnees(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
    }

    private void initLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JPanel panelCenter = new JPanel(new GridLayout(2, 1, 20, 20));
        panelCenter.setOpaque(false);
        panelCenter.setMaximumSize(new Dimension(400, 200));

        JLabel lblTitle = new JLabel("Gestion de l'Historique (Continuité)");
        lblTitle.setFont(DesignSystem.FONT_TITLE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panelBtns = new JPanel(new GridLayout(1, 2, 20, 20));
        panelBtns.setOpaque(false);
        panelBtns.add(btnSauvegarder);
        panelBtns.add(btnRestaurer);

        panelCenter.add(lblTitle);
        panelCenter.add(panelBtns);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(panelCenter);

        add(wrapper, BorderLayout.CENTER);
    }

    public void afficherMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            btnSauvegarder.setEnabled(true);
            btnRestaurer.setEnabled(true);
            JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void afficherErreur(String msg) {
        SwingUtilities.invokeLater(() -> {
            btnSauvegarder.setEnabled(true);
            btnRestaurer.setEnabled(true);
            JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
        });
    }
}
