package com.restaurant.view;

import com.restaurant.utils.DesignSystem;
import javax.swing.*;
import java.awt.*;

// Fenêtre de démarrage (Splash Screen) affichée pendant l'initialisation.
public class SplashScreen extends JWindow {

    private JProgressBar progressBar;
    private JLabel lblStatut;

    public SplashScreen() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(DesignSystem.PRIMARY);
        content.setBorder(BorderFactory.createLineBorder(DesignSystem.SECONDARY, 3));

        JLabel lblTitre = new JLabel("🍽️  Gestion Restaurant", SwingConstants.CENTER);
        lblTitre.setFont(DesignSystem.FONT_HUGE);
        lblTitre.setForeground(Color.WHITE);
        lblTitre.setBorder(BorderFactory.createEmptyBorder(40, 20, 10, 20));
        content.add(lblTitre, BorderLayout.NORTH);

        JLabel lblSous = new JLabel("Chargement en cours...", SwingConstants.CENTER);
        lblSous.setFont(DesignSystem.FONT_BODY);
        lblSous.setForeground(new Color(200, 220, 255));
        lblSous.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        content.add(lblSous, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20));

        lblStatut = new JLabel("Connexion à la base de données...", SwingConstants.LEFT);
        lblStatut.setFont(DesignSystem.FONT_BADGE);
        lblStatut.setForeground(new Color(180, 200, 240));
        bottom.add(lblStatut, BorderLayout.NORTH);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setForeground(DesignSystem.SUCCESS);
        progressBar.setBackground(new Color(30, 50, 100));
        progressBar.setPreferredSize(new Dimension(400, 12));
        bottom.add(progressBar, BorderLayout.SOUTH);

        content.add(bottom, BorderLayout.SOUTH);

        setContentPane(content);
        setSize(480, 260);
        setLocationRelativeTo(null);
    }

    // Affiche le splash et anime la barre de 0 → 100 % en environ {code durationMs}
    // ms
    // Ferme automatiquement à la fin, puis exécute {code onDone}
    public void afficherEtAnimer(int durationMs, Runnable onDone) {
        setVisible(true);
        toFront();

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                int steps = 100;
                int delay = durationMs / steps;
                for (int i = 0; i <= steps; i++) {
                    publish(i);
                    Thread.sleep(delay);
                    if (i == 30)
                        setStatut("Chargement des configurations...");
                    if (i == 60)
                        setStatut("Initialisation de l'interface...");
                    if (i == 90)
                        setStatut("Prêt !");
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int v = chunks.get(chunks.size() - 1);
                progressBar.setValue(v);
            }

            @Override
            protected void done() {
                dispose();
                if (onDone != null)
                    onDone.run();
            }
        };
        worker.execute();
    }

    private void setStatut(String msg) {
        SwingUtilities.invokeLater(() -> lblStatut.setText(msg));
    }
}
