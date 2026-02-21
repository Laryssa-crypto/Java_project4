package com.restaurant.controller;

import com.restaurant.dao.CommandeDAO;
import com.restaurant.dao.LigneCommandeDAO;
import com.restaurant.dao.MouvementStockDAO;
import com.restaurant.dao.ProduitDAO;
import com.restaurant.model.Commande;
import com.restaurant.model.LigneCommande;
import com.restaurant.model.Produit;
import com.restaurant.model.enums.EtatCommande;
import com.restaurant.service.CommandeService;
import com.restaurant.service.ProduitService;
import com.restaurant.service.StockService;
import com.restaurant.view.CommandeView;
import java.sql.SQLException;
import java.util.List;

public class CommandeController {

    private CommandeView commandeView;
    private final CommandeService commandeService;
    private final ProduitService produitService;
    private Commande commandeEnCours;
    private com.restaurant.model.Utilisateur utilisateurConnecte;

    public CommandeController() {
        CommandeDAO cDao = new CommandeDAO();
        LigneCommandeDAO lDao = new LigneCommandeDAO();
        ProduitDAO pDao = new ProduitDAO();
        MouvementStockDAO mDao = new MouvementStockDAO();
        StockService svc = new StockService(pDao, mDao);
        this.commandeService = new CommandeService(cDao, lDao, pDao, svc);
        this.produitService = new ProduitService(pDao);
    }

    public void setView(CommandeView view) {
        this.commandeView = view;
    }

    public void setUtilisateurConnecte(com.restaurant.model.Utilisateur utilisateurConnecte) {
        this.utilisateurConnecte = utilisateurConnecte;
    }

    public boolean creerNouvelleCommande() {
        try {
            commandeEnCours = commandeService
                    .creerCommande(utilisateurConnecte != null ? utilisateurConnecte.getNomUtil() : "Inconnu");
            if (commandeView != null) {
                commandeView.afficherCommande(commandeEnCours);
                commandeView.viderLignes();
                commandeView.afficherMessage("Nouvelle commande créée avec succès");
            }
            return true;
        } catch (SQLException e) {
            afficherErreur("Erreur création commande : " + e.getMessage());
            return false;
        }
    }

    public void ajouterProduit(int idProduit, int quantite) {
        if (commandeEnCours == null) {
            commandeView.afficherErreur("Veuillez d'abord créer une commande");
            return;
        }
        try {
            boolean success = commandeService.ajouterProduit(commandeEnCours.getIdCmde(), idProduit, quantite);
            if (success) {
                rafraichirAffichageCommande();
                rafraichirProduits();
                if (commandeView != null)
                    commandeView.afficherMessage("Produit ajouté à la commande");
            } else {
                commandeView.afficherErreur("Échec de l'ajout du produit. Vérifiez le stock ou l'état de la commande.");
            }
        } catch (Exception e) {
            commandeView.afficherErreur("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    public boolean modifierQuantiteLigne(int idLigne, int nouvelleQuantite) {
        if (commandeEnCours == null)
            return false;
        try {
            boolean ok = commandeService.modifierQuantiteLigne(idLigne, nouvelleQuantite);
            if (ok) {
                rafraichirAffichageCommande();
                if (commandeView != null)
                    commandeView.afficherMessage("Quantité modifiée");
            }
            return ok;
        } catch (SQLException e) {
            afficherErreur(e.getMessage());
            return false;
        }
    }

    public boolean supprimerLigne(int idLigne) {
        if (commandeEnCours == null)
            return false;
        try {
            boolean ok = commandeService.supprimerLigne(idLigne);
            if (ok) {
                rafraichirAffichageCommande();
                if (commandeView != null)
                    commandeView.afficherMessage("Ligne supprimée");
            }
            return ok;
        } catch (SQLException e) {
            afficherErreur(e.getMessage());
            return false;
        }
    }

    public boolean validerCommande() {
        if (commandeEnCours == null) {
            afficherErreur("Aucune commande en cours");
            return false;
        }
        try {
            boolean ok = commandeService.validerCommande(commandeEnCours.getIdCmde());
            if (ok && commandeView != null) {
                commandeView.afficherMessage("Commande validée avec succès !");

                List<LigneCommande> lignes = commandeService.getLignesCommande(commandeEnCours.getIdCmde());
                for (LigneCommande l : lignes) {
                    Produit p = produitService.getProduitById(l.getIdPro());
                    if (p != null) {
                        if (p.getStockActu() == 0) {
                            commandeView.afficherErreur("RUPTURE : " + p.getNomPro() + " est en rupture de stock !");
                        } else if (p.getStockActu() <= p.getSeuilAlerte()) {
                            commandeView
                                    .afficherMessage("Stock faible : " + p.getNomPro() + " (" + p.getStockActu() + ")");
                        }
                    }
                }

                try {
                    java.io.File dir = new java.io.File("Recus");
                    if (!dir.exists())
                        dir.mkdirs();
                    String path = "Recus/Recu_Commande_" + commandeEnCours.getIdCmde() + ".pdf";
                    com.restaurant.service.PdfExportService.genererRecuPDF(commandeEnCours, lignes, path);
                    commandeView.afficherMessage("Reçu généré automatiquement : " + path);
                } catch (Exception ex) {
                    commandeView.afficherErreur("Erreur génération reçu PDF : " + ex.getMessage());
                }

                commandeEnCours = null;
                commandeView.afficherCommande(null);
                commandeView.viderLignes();
                commandeView.desactiverModification();
                commandeView.reinitialiserVues();
                rafraichirProduits();
            }
            return ok;
        } catch (SQLException e) {
            afficherErreur(e.getMessage());
            return false;
        }
    }

    public boolean annulerCommande() {
        if (commandeEnCours == null) {
            afficherErreur("Aucune commande en cours");
            return false;
        }
        try {
            boolean ok = commandeService.annulerCommande(commandeEnCours.getIdCmde());
            if (ok && commandeView != null) {
                commandeView.afficherMessage("Commande annulée");
                commandeEnCours = null;
                commandeView.afficherCommande(null);
                commandeView.viderLignes();
                commandeView.desactiverModification();
                commandeView.reinitialiserVues();
                rafraichirProduits();
            }
            return ok;
        } catch (SQLException e) {
            afficherErreur(e.getMessage());
            return false;
        }
    }

    public boolean chargerCommande(int idCommande) {
        try {
            Commande commande = commandeService.getCommande(idCommande);
            if (commande == null) {
                afficherErreur("Commande non trouvée");
                return false;
            }
            commandeEnCours = commande;
            if (commandeView != null) {
                commandeView.afficherCommande(commande);
                commandeView.afficherLignes(commandeService.getLignesCommande(idCommande));
                if (commande.getEtat() != EtatCommande.EN_COURS)
                    commandeView.desactiverModification();
                else
                    commandeView.activerModification();
            }
            return true;
        } catch (SQLException e) {
            afficherErreur("Erreur chargement commande : " + e.getMessage());
            return false;
        }
    }

    public List<Produit> getProduits() {
        return produitService.getAllProduits();
    }

    public List<Commande> getCommandes() {
        try {
            return commandeService.getAllCommandes();
        } catch (SQLException e) {
            afficherErreur("Erreur chargement commandes : " + e.getMessage());
            return null;
        }
    }

    public void imprimerAncienneCommande(int id) {
        try {
            Commande cmd = commandeService.getCommande(id);
            if (cmd == null) {
                afficherErreur("Commande introuvable.");
                return;
            }
            List<LigneCommande> lignes = commandeService.getLignesCommande(id);
            com.restaurant.service.PrintService.imprimerRecuClient(cmd, lignes);
        } catch (SQLException e) {
            afficherErreur("Erreur lors de l'impression : " + e.getMessage());
        }
    }

    public void exporterToutesLesCommandesCSV(String path) {
        try {
            java.time.LocalDate today = java.time.LocalDate.now();
            List<Commande> commandes = commandeService.getAllCommandes().stream()
                    .filter(c -> c.getDate().toLocalDate().equals(today))
                    .collect(java.util.stream.Collectors.toList());
            List<LigneCommande> toutesLignes = new java.util.ArrayList<>();
            for (Commande c : commandes) {
                toutesLignes.addAll(commandeService.getLignesCommande(c.getIdCmde()));
            }
            boolean ok = com.restaurant.service.CsvService.exporterCommandes(path, commandes, toutesLignes);
            if (ok && commandeView != null) {
                commandeView.afficherMessage("Export CSV réussi !");
            } else if (!ok) {
                afficherErreur("Erreur lors de l'export CSV.");
            }
        } catch (SQLException e) {
            afficherErreur("Erreur BD lors de l'export : " + e.getMessage());
        }
    }

    public void rafraichirProduits() {
        if (commandeView == null)
            return;
        new javax.swing.SwingWorker<List<Produit>, Void>() {
            @Override
            protected List<Produit> doInBackground() {
                return produitService.getAllProduits();
            }

            @Override
            protected void done() {
                try {
                    commandeView.mettreAJourProduits(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public Commande getCommandeEnCours() {
        return commandeEnCours;
    }

    private void rafraichirAffichageCommande() {
        if (commandeEnCours == null || commandeView == null)
            return;
        try {
            commandeEnCours = commandeService.getCommande(commandeEnCours.getIdCmde());
            commandeView.afficherCommande(commandeEnCours);
            commandeView.afficherLignes(commandeService.getLignesCommande(commandeEnCours.getIdCmde()));
        } catch (SQLException e) {
            commandeView.afficherErreur("Erreur rafraîchissement : " + e.getMessage());
        }
    }

    private void afficherErreur(String msg) {
        if (commandeView != null)
            commandeView.afficherErreur(msg);
    }
}
