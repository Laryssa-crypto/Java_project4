package com.restaurant.model;

import com.restaurant.model.enums.Role;

// Représente un utilisateur (Admin ou Caissier) du système
public class Utilisateur {
    private int idUtil;
    private String nomUtil;
    private String mdp;
    private Role role;

    public Utilisateur() {
    }

    public Utilisateur(int idUtil, String nomUtil, String mdp, Role role) {
        this.idUtil = idUtil;
        this.nomUtil = nomUtil;
        this.mdp = mdp;
        this.role = role;
    }

    public Utilisateur(String nomUtil, String mdp, Role role) {
        this.nomUtil = nomUtil;
        this.mdp = mdp;
        this.role = role;
    }

    public int getIdUtil() {
        return idUtil;
    }

    public void setIdUtil(int idUtil) {
        this.idUtil = idUtil;
    }

    public String getNomUtil() {
        return nomUtil;
    }

    public void setNomUtil(String nomUtil) {
        this.nomUtil = nomUtil;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return nomUtil;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Utilisateur that = (Utilisateur) obj;
        return nomUtil.equals(that.nomUtil);
    }

    @Override
    public int hashCode() {
        return nomUtil.hashCode();
    }
}
