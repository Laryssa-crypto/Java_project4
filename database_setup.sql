-- Script d'initialisation de la base de données Gestion Restaurant

-- 1. CRÉATION ET SÉLECTION DE LA BASE
CREATE DATABASE IF NOT EXISTS `gestion_restaurant`;
USE `gestion_restaurant`;

-- 2. TABLE : CATEGORIE
CREATE TABLE `CATEGORIE` (
    `id_cat` INT NOT NULL AUTO_INCREMENT,
    `libelle_cat` VARCHAR(30) NOT NULL UNIQUE,
    PRIMARY KEY (`id_cat`)
);

-- 3. TABLE : PRODUIT
CREATE TABLE `PRODUIT` (
    `id_pro` INT NOT NULL AUTO_INCREMENT,
    `nom_pro` VARCHAR(50) NOT NULL,
    `id_cat` INT NOT NULL,
    `prix_vente` DECIMAL(10,2) NOT NULL CHECK (`prix_vente` > 0),
    `stock_actu` INT NOT NULL CHECK (`stock_actu` >= 0),
    `seuil_alerte` INT NOT NULL,
    `actif` TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (`id_pro`),
    FOREIGN KEY (`id_cat`) REFERENCES `CATEGORIE` (`id_cat`)
);

-- 4. TABLE : MOUVEMENT_STOCK (MVT_STOCK)
CREATE TABLE `MOUVEMENT_STOCK` (
    `id_mvt` INT NOT NULL AUTO_INCREMENT,
    `type` VARCHAR(6) NOT NULL CHECK (`type` IN ('ENTREE', 'SORTIE')),
    `id_pro` INT NOT NULL,
    `quantite` INT NOT NULL CHECK (`quantite` > 0),
    `date` DATE NOT NULL,
    `motif` VARCHAR(50),
    `reference` VARCHAR(50),
    PRIMARY KEY (`id_mvt`),
    FOREIGN KEY (`id_pro`) REFERENCES `PRODUIT` (`id_pro`)
);

-- 5. TABLE : COMMANDE
CREATE TABLE `COMMANDE` (
    `id_cmde` INT NOT NULL AUTO_INCREMENT,
    `date` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `etat` VARCHAR(8) NOT NULL DEFAULT 'EN_COURS' CHECK (`etat` IN ('EN_COURS', 'VALIDEE', 'ANNULEE')),
    `total` DECIMAL(10,2) NOT NULL,
    `nom_util` VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (`id_cmde`)
);

-- 6. TABLE : LIGNE_COMMANDE (LIG_COMMANDE)
CREATE TABLE `LIGNE_COMMANDE` (
    `id_lig` INT NOT NULL AUTO_INCREMENT,
    `id_cmde` INT NOT NULL,
    `id_pro` INT NOT NULL,
    `qte_lig` INT NOT NULL CHECK (`qte_lig` > 0),
    `prix_unit` DECIMAL(10,2) NOT NULL,
    `montant` DECIMAL(10,2) AS (`qte_lig` * `prix_unit`) STORED,
    PRIMARY KEY (`id_lig`),
    FOREIGN KEY (`id_pro`) REFERENCES `PRODUIT` (`id_pro`),
    FOREIGN KEY (`id_cmde`) REFERENCES `COMMANDE` (`id_cmde`)
);

-- 7. TABLE : LOGS
CREATE TABLE `LOGS` (
    `id_log` INT NOT NULL AUTO_INCREMENT,
    `date_log` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `nom_util` VARCHAR(100) DEFAULT NULL,
    `action` VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id_log`)
);

-- 8. TABLE : UTILISATEUR
CREATE TABLE `UTILISATEUR` (
    `id_uti` INT NOT NULL AUTO_INCREMENT,
    `nom_uti` VARCHAR(50) NOT NULL UNIQUE,
    `mdp` VARCHAR(256) NOT NULL,
    `role` VARCHAR(8) NOT NULL DEFAULT 'ADMIN' CHECK (`role` IN ('ADMIN', 'CAISSIER')),
    PRIMARY KEY (`id_uti`)
);
