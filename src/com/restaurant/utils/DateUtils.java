package com.restaurant.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * Classe utilitaire pour la manipulation des dates
 */
public class DateUtils {
    
    public static final DateTimeFormatter FORMAT_FR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter FORMAT_SQL = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMAT_AFFICHAGE = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    
    /**
     * Convertit une LocalDate en chaîne au format français
     */
    public static String toStringFR(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(FORMAT_FR);
    }
    
    /**
     * Convertit une LocalDate en chaîne au format SQL
     */
    public static String toStringSQL(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(FORMAT_SQL);
    }
    
    /**
     * Convertit une LocalDate en chaîne formatée pour l'affichage
     */
    public static String toStringAffichage(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(FORMAT_AFFICHAGE);
    }
    
    /**
     * Convertit une chaîne au format français en LocalDate
     */
    public static LocalDate fromStringFR(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(dateStr.trim(), FORMAT_FR);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Convertit une chaîne au format SQL en LocalDate
     */
    public static LocalDate fromStringSQL(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(dateStr.trim(), FORMAT_SQL);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Convertit une Date SQL en LocalDate
     */
    public static LocalDate fromSQLDate(java.sql.Date sqlDate) {
        if (sqlDate == null) {
            return null;
        }
        
        return sqlDate.toLocalDate();
    }
    
    /**
     * Convertit une LocalDate en Date SQL
     */
    public static Date toSQLDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        
        return java.sql.Date.valueOf(localDate);
    }
    
    /**
     * Vérifie si une chaîne est une date valide au format français
     */
    public static boolean estDateValideFR(String dateStr) {
        return fromStringFR(dateStr) != null;
    }
    
    /**
     * Vérifie si une chaîne est une date valide au format SQL
     */
    public static boolean estDateValideSQL(String dateStr) {
        return fromStringSQL(dateStr) != null;
    }
    
    /**
     * Obtient la date du jour
     */
    public static LocalDate aujourdHui() {
        return LocalDate.now();
    }
    
    /**
     * Obtient la date du jour au format français
     */
    public static String aujourdHuiStringFR() {
        return toStringFR(aujourdHui());
    }
    
    /**
     * Obtient la date du jour au format SQL
     */
    public static String aujourdHuiStringSQL() {
        return toStringSQL(aujourdHui());
    }
    
    /**
     * Ajoute des jours à une date
     */
    public static LocalDate ajouterJours(LocalDate date, int jours) {
        if (date == null) {
            return null;
        }
        return date.plusDays(jours);
    }
    
    /**
     * Soustrait des jours à une date
     */
    public static LocalDate soustraireJours(LocalDate date, int jours) {
        if (date == null) {
            return null;
        }
        return date.minusDays(jours);
    }
    
    /**
     * Ajoute des mois à une date
     */
    public static LocalDate ajouterMois(LocalDate date, int mois) {
        if (date == null) {
            return null;
        }
        return date.plusMonths(mois);
    }
    
    /**
     * Soustrait des mois à une date
     */
    public static LocalDate soustraireMois(LocalDate date, int mois) {
        if (date == null) {
            return null;
        }
        return date.minusMonths(mois);
    }
    
    /**
     * Ajoute des années à une date
     */
    public static LocalDate ajouterAnnees(LocalDate date, int annees) {
        if (date == null) {
            return null;
        }
        return date.plusYears(annees);
    }
    
    /**
     * Soustrait des années à une date
     */
    public static LocalDate soustraireAnnees(LocalDate date, int annees) {
        if (date == null) {
            return null;
        }
        return date.minusYears(annees);
    }
    
    /**
     * Calcule le nombre de jours entre deux dates
     */
    public static long differenceJours(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin);
    }
    
    /**
     * Vérifie si une date est comprise entre deux autres dates (inclusif)
     */
    public static boolean estDansPeriode(LocalDate date, LocalDate debut, LocalDate fin) {
        if (date == null || debut == null || fin == null) {
            return false;
        }
        
        if (debut.isAfter(fin)) {
            LocalDate temp = debut;
            debut = fin;
            fin = temp;
        }
        
        return !date.isBefore(debut) && !date.isAfter(fin);
    }
    
    /**
     * Obtient le premier jour du mois pour une date donnée
     */
    public static LocalDate premierDuMois(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.withDayOfMonth(1);
    }
    
    /**
     * Obtient le dernier jour du mois pour une date donnée
     */
    public static LocalDate dernierDuMois(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.withDayOfMonth(date.lengthOfMonth());
    }
    
    /**
     * Obtient le premier jour de l'année pour une date donnée
     */
    public static LocalDate premierDeLAnnee(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.withDayOfYear(1);
    }
    
    /**
     * Obtient le dernier jour de l'année pour une date donnée
     */
    public static LocalDate dernierDeLAnnee(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.withDayOfYear(date.lengthOfYear());
    }
    
    /**
     * Formate une durée en jours en format lisible
     */
    public static String formaterDuree(long jours) {
        if (jours < 0) {
            return "Durée invalide";
        }
        
        if (jours == 0) {
            return "Aujourd'hui";
        }
        
        if (jours == 1) {
            return "1 jour";
        }
        
        if (jours < 7) {
            return jours + " jours";
        }
        
        if (jours < 30) {
            long semaines = jours / 7;
            long joursRestants = jours % 7;
            
            if (joursRestants == 0) {
                return semaines + " semaine" + (semaines > 1 ? "s" : "");
            } else {
                return semaines + " semaine" + (semaines > 1 ? "s" : "") + " et " + joursRestants + " jour" + (joursRestants > 1 ? "s" : "");
            }
        }
        
        if (jours < 365) {
            long mois = jours / 30;
            long joursRestants = jours % 30;
            
            if (joursRestants == 0) {
                return mois + " mois";
            } else {
                return mois + " mois et " + joursRestants + " jour" + (joursRestants > 1 ? "s" : "");
            }
        }
        
        long annees = jours / 365;
        long joursRestants = jours % 365;
        
        if (joursRestants == 0) {
            return annees + " an" + (annees > 1 ? "s" : "");
        } else {
            return annees + " an" + (annees > 1 ? "s" : "") + " et " + joursRestants + " jour" + (joursRestants > 1 ? "s" : "");
        }
    }
    
    /**
     * Vérifie si une date est dans le passé
     */
    public static boolean estDansPasse(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isBefore(aujourdHui());
    }
    
    /**
     * Vérifie si une date est dans le futur
     */
    public static boolean estDansFutur(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isAfter(aujourdHui());
    }
    
    /**
     * Vérifie si une date est aujourd'hui
     */
    public static boolean estAujourdHui(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isEqual(aujourdHui());
    }
}
