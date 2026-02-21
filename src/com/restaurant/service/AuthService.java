package com.restaurant.service;

import com.restaurant.dao.UtilisateurDAO;
import com.restaurant.model.Utilisateur;
import com.restaurant.utils.PasswordUtils;
import com.restaurant.model.enums.Role;
import java.sql.SQLException;

public class AuthService {

    private UtilisateurDAO utilisateurDAO;

    public AuthService(UtilisateurDAO utilisateurDAO) {
        this.utilisateurDAO = utilisateurDAO;
    }

    // Authentifie un utilisateur avec son nom et mot de passe
    public Utilisateur authenticate(String nomUtil, String motDePasse) throws SQLException {
        String hashedMdp = PasswordUtils.hashPassword(motDePasse);
        return utilisateurDAO.authenticate(nomUtil.trim(), hashedMdp);
    }

    // Crée un nouvel utilisateur de type CAISSIER
    public boolean creerUtilisateur(String nomUtil, String motDePasse) throws SQLException {
        String trimmedNom = nomUtil.trim();
        if (trimmedNom.isEmpty() || trimmedNom.length() > 50) {
            throw new SQLException("Le nom d'utilisateur doit contenir entre 1 et 50 caractères.");
        }
        if (utilisateurDAO.existsByNom(trimmedNom)) {
            throw new SQLException("Ce nom d'utilisateur existe déjà");
        }
        String hashedMdp = PasswordUtils.hashPassword(motDePasse);
        Utilisateur utilisateur = new Utilisateur(trimmedNom, hashedMdp, Role.CAISSIER);
        int id = utilisateurDAO.create(utilisateur);
        return id > 0;
    }

}
