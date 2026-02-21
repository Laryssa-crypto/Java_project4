package com.restaurant.view;

import com.restaurant.controller.LoginController;
import com.restaurant.utils.DesignSystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginView extends JFrame {

    private LoginController controller;

    private JTextField txtNomUtil;
    private JPasswordField txtMotDePasse;
    private JTextField txtNomUtilNew;
    private JPasswordField txtMotDePasseNew;
    private JPasswordField txtConfirmationMdp;
    private JButton btnConnexion;
    private JButton btnCreerCompte;
    private JButton btnValiderCreation;
    private JButton btnAnnulerCreation;
    private JLabel lblMessage;
    private JCheckBox chkSeSouvenir;
    private JPanel panelConnexion;
    private JPanel panelCreation;

    private void defineIcon() {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png"));
            if (icon.getImage() != null)
                setIconImage(icon.getImage());
        } catch (NullPointerException e) {
        }
    }

    public LoginView(LoginController controller) {
        this.controller = controller;

        setTitle("Gestion Restaurant - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(DesignSystem.BACKGROUND);

        defineIcon();
        initComposants();
        initLayout();
        initListeners();
        afficherPanelConnexion();
    }

    private void initComposants() {
        JLabel lblLogo = new JLabel("GESTION RESTAURANT", SwingConstants.CENTER);
        lblLogo.setFont(DesignSystem.FONT_TITLE);
        lblLogo.setForeground(DesignSystem.PRIMARY);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblLogo, BorderLayout.NORTH);

        panelConnexion = new JPanel(new GridBagLayout());
        panelConnexion.setBackground(Color.WHITE);
        panelConnexion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DesignSystem.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        txtNomUtil = new JTextField(15);
        txtMotDePasse = new JPasswordField(15);
        chkSeSouvenir = new JCheckBox("Se souvenir de moi");
        chkSeSouvenir.setBackground(Color.WHITE);
        chkSeSouvenir.setFont(DesignSystem.FONT_BODY);
        btnConnexion = new JButton("Se connecter");
        btnCreerCompte = new JButton("Créer un compte");

        DesignSystem.styleTextField(txtNomUtil);
        DesignSystem.styleTextField(txtMotDePasse);
        DesignSystem.styleButton(btnConnexion);
        btnCreerCompte.setContentAreaFilled(false);
        btnCreerCompte.setForeground(DesignSystem.PRIMARY);
        btnCreerCompte.setBorder(null);
        btnCreerCompte.setCursor(new Cursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelConnexion.add(new JLabel("Nom d'utilisateur:"), gbc);
        gbc.gridx = 1;
        panelConnexion.add(txtNomUtil, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelConnexion.add(new JLabel("Mot de passe:"), gbc);
        gbc.gridx = 1;
        panelConnexion.add(txtMotDePasse, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panelConnexion.add(chkSeSouvenir, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelConnexion.add(btnConnexion, gbc);
        gbc.gridy = 4;
        panelConnexion.add(btnCreerCompte, gbc);

        panelCreation = new JPanel(new GridBagLayout());
        panelCreation.setBackground(DesignSystem.CARD_BG);
        panelCreation.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DesignSystem.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        txtNomUtilNew = new JTextField(15);
        txtMotDePasseNew = new JPasswordField(15);
        txtConfirmationMdp = new JPasswordField(15);
        btnValiderCreation = new JButton("Créer");
        btnAnnulerCreation = new JButton("Annuler");

        DesignSystem.styleTextField(txtNomUtilNew);
        DesignSystem.styleTextField(txtMotDePasseNew);
        DesignSystem.styleTextField(txtConfirmationMdp);
        DesignSystem.styleButton(btnValiderCreation, DesignSystem.SUCCESS);
        btnAnnulerCreation.setContentAreaFilled(false);
        btnAnnulerCreation.setForeground(DesignSystem.DANGER);
        btnAnnulerCreation.setBorder(null);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCreation.add(new JLabel("Nom d'utilisateur:"), gbc);
        gbc.gridx = 1;
        panelCreation.add(txtNomUtilNew, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelCreation.add(new JLabel("Mot de passe:"), gbc);
        gbc.gridx = 1;
        panelCreation.add(txtMotDePasseNew, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelCreation.add(new JLabel("Confirmation:"), gbc);
        gbc.gridx = 1;
        panelCreation.add(txtConfirmationMdp, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelCreation.add(btnValiderCreation, gbc);
        gbc.gridy = 4;
        panelCreation.add(btnAnnulerCreation, gbc);

        lblMessage = new JLabel(" ");
        lblMessage.setFont(DesignSystem.FONT_BODY);
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void initLayout() {
        setLayout(new BorderLayout());

        JPanel panelCards = new JPanel(new CardLayout());
        panelCards.setOpaque(false);
        panelCards.add(panelConnexion, "CONNEXION");
        panelCards.add(panelCreation, "CREATION");

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(panelCards);

        add(panelCards, BorderLayout.CENTER);
        add(lblMessage, BorderLayout.SOUTH);
    }

    private void initListeners() {
        btnConnexion.addActionListener(e -> seConnecter());
        btnCreerCompte.addActionListener(e -> afficherPanelCreation());
        btnValiderCreation.addActionListener(e -> creerCompte());
        btnAnnulerCreation.addActionListener(e -> afficherPanelConnexion());

        txtMotDePasse.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    seConnecter();
            }
        });
        txtConfirmationMdp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    creerCompte();
            }
        });
    }

    private void seConnecter() {
        String nom = txtNomUtil.getText().trim();
        String mdp = new String(txtMotDePasse.getPassword());
        if (controller.seConnecter(nom, mdp)) {
            viderChamps();
        }
    }

    private void creerCompte() {
        String nom = txtNomUtilNew.getText().trim();
        String mdp = new String(txtMotDePasseNew.getPassword());
        String confirm = new String(txtConfirmationMdp.getPassword());
        if (controller.creerCompte(nom, mdp, confirm)) {
            viderChamps();
            afficherPanelConnexion();
        }
    }

    public void afficherPanelConnexion() {
        if (panelConnexion.getParent() instanceof JPanel) {
            JPanel parent = (JPanel) panelConnexion.getParent();
            if (parent.getLayout() instanceof CardLayout) {
                ((CardLayout) parent.getLayout()).show(parent, "CONNEXION");
            }
        }
        viderChamps();
        lblMessage.setText(" ");
        txtNomUtil.requestFocus();
    }

    public void afficherPanelCreation() {
        if (panelCreation.getParent() instanceof JPanel) {
            JPanel parent = (JPanel) panelCreation.getParent();
            if (parent.getLayout() instanceof CardLayout) {
                ((CardLayout) parent.getLayout()).show(parent, "CREATION");
            }
        }
        viderChamps();
        lblMessage.setText(" ");
        txtNomUtilNew.requestFocus();
    }

    public void viderChamps() {
        txtNomUtil.setText("");
        txtMotDePasse.setText("");
        txtNomUtilNew.setText("");
        txtMotDePasseNew.setText("");
        txtConfirmationMdp.setText("");
    }

    public boolean isSeSouvenir() {
        return chkSeSouvenir.isSelected();
    }

    public void setSeSouvenir(boolean b) {
        chkSeSouvenir.setSelected(b);
    }

    public String getNomUtil() {
        return txtNomUtil.getText().trim();
    }

    public void setNomUtil(String nom) {
        txtNomUtil.setText(nom);
    }

    public void afficherMessage(String message) {
        afficherMessage(message, DesignSystem.SUCCESS);
    }

    public void afficherErreur(String erreur) {
        afficherMessage(erreur, DesignSystem.DANGER);
    }

    private void afficherMessage(String message, Color couleur) {
        lblMessage.setText(message);
        lblMessage.setForeground(couleur);
        if (couleur == DesignSystem.SUCCESS && !message.trim().isEmpty()) {
            Timer timer = new Timer(3000, e -> lblMessage.setText(" "));
            timer.setRepeats(false);
            timer.start();
        }
    }
}
