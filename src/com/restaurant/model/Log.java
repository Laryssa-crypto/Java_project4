package com.restaurant.model;

import java.time.LocalDateTime;

public class Log {
    private int id_log;
    private LocalDateTime date_log;
    private String nom_util;
    private String action;

    public Log() {
    }

    // Constructeur pratique pour l'insertion
    public Log(String nom_util, String action) {
        this.nom_util = nom_util;
        this.action = action;
    }

    // Getters et Setters (camelCase for DAO compatibility)
    public int getIdLog() { return id_log; }
    public void setIdLog(int idLog) { this.id_log = idLog; }

    public LocalDateTime getDateLog() { return date_log; }
    public void setDateLog(LocalDateTime dateLog) { this.date_log = dateLog; }

    public String getNomUtil() { return nom_util; }
    public void setNomUtil(String nomUtil) { this.nom_util = nomUtil; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}