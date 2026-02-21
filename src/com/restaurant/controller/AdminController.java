package com.restaurant.controller;

import com.restaurant.dao.UtilisateurDAO;
import com.restaurant.model.Utilisateur;
import com.restaurant.model.enums.Role;
import com.restaurant.utils.PasswordUtils;
import com.restaurant.view.AdminView;

import java.sql.SQLException;
import java.util.List;

public class AdminController {

    private AdminView adminView;
    private UtilisateurDAO utilisateurDAO;

    public AdminController() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public void setView(AdminView view) {
        this.adminView = view;
    }

    // Charge tous les utilisateurs dans la vue
    public void chargerUtilisateurs() {
        try {
            List<Utilisateur> utilisateurs = utilisateurDAO.findAll();
            if (adminView != null) {
                adminView.afficherUtilisateurs(utilisateurs);
            }
        } catch (SQLException e) {
            afficherErreur("Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }
    }

    // Ajoute un nouvel utilisateur avec mot de passe haché
    public void ajouterUtilisateur(String nom, String rawMdp, Role role) {
        try {
            if (utilisateurDAO.findByNom(nom) != null) {
                afficherErreur("Un utilisateur avec ce nom existe déjà.");
                return;
            }
            String hashedMdp = PasswordUtils.hashPassword(rawMdp);
            Utilisateur newUser = new Utilisateur(nom, hashedMdp, role);
            if (utilisateurDAO.create(newUser) > 0) {
                if (adminView != null) {
                    adminView.afficherMessage("Utilisateur ajouté avec succès.");
                    chargerUtilisateurs();
                }
            }
        } catch (SQLException e) {
            afficherErreur("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }
    }

    // Modifie un utilisateur (met à jour le mot de passe seulement si fourni)
    public void modifierUtilisateur(int idUtil, String nom, String rawMdp, Role role) {
        try {
            Utilisateur u = new Utilisateur();
            u.setIdUtil(idUtil);
            u.setNomUtil(nom);
            u.setRole(role);

            if (rawMdp == null || rawMdp.trim().isEmpty()) {
                List<Utilisateur> all = utilisateurDAO.findAll();
                for (Utilisateur existing : all) {
                    if (existing.getIdUtil() == idUtil) {
                        u.setMdp(existing.getMdp()); // Keep old hash
                        break;
                    }
                }
            } else {
                u.setMdp(PasswordUtils.hashPassword(rawMdp));
            }

            if (utilisateurDAO.update(u)) {
                if (adminView != null) {
                    adminView.afficherMessage("Utilisateur modifié avec succès.");
                    chargerUtilisateurs();
                }
            } else {
                afficherErreur("La modification a échoué.");
            }
        } catch (SQLException e) {
            afficherErreur("Erreur lors de la modification : " + e.getMessage());
        }
    }

    // Supprime un utilisateur par son ID
    public void supprimerUtilisateur(int idUtil) {
        try {
            if (utilisateurDAO.delete(idUtil)) {
                if (adminView != null) {
                    adminView.afficherMessage("Utilisateur supprimé.");
                    chargerUtilisateurs();
                }
            } else {
                afficherErreur("Impossible de supprimer cet utilisateur.");
            }
        } catch (SQLException e) {
            afficherErreur("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    private void afficherErreur(String msg) {
        if (adminView != null) {
            adminView.afficherErreur(msg);
        }
    }
}
