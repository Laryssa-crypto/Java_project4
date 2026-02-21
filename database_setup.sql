-- Script d'initialisation de la base de données Gestion Restaurant

-- 1. Création de la base de données
CREATE DATABASE IF NOT EXISTS gestion_restaurant;
USE gestion_restaurant;

-- 2. Création des tables
CREATE TABLE IF NOT EXISTS CATEGORIE (
    id_cat INT NOT NULL AUTO_INCREMENT,
    libelle_cat VARCHAR(30) NOT NULL UNIQUE,
    PRIMARY KEY(id_cat)
);

CREATE TABLE IF NOT EXISTS PRODUIT (
    id_pro INT NOT NULL AUTO_INCREMENT,
    nom_pro VARCHAR(50) NOT NULL,
    id_cat INT NOT NULL,
    prix_vente DECIMAL(10,2) NOT NULL CHECK(prix_vente > 0),
    stock_actu INT NOT NULL CHECK(stock_actu >= 0),
    seuil_alerte INT NOT NULL,
    PRIMARY KEY(id_pro),
    FOREIGN KEY(id_cat) REFERENCES CATEGORIE(id_cat)
);

CREATE TABLE IF NOT EXISTS MVT_STOCK(
    id_stock INT NOT NULL AUTO_INCREMENT,
    type CHAR(6) NOT NULL CHECK(type IN('ENTREE', 'SORTIE')),
    id_pro INT NOT NULL,
    qte_stock INT NOT NULL CHECK(qte_stock > 0),
    date DATE NOT NULL,
    motif VARCHAR(50),
    PRIMARY KEY(id_stock),
    FOREIGN KEY(id_pro) REFERENCES PRODUIT(id_pro)
);

CREATE TABLE IF NOT EXISTS COMMANDE (
    id_cmde INT NOT NULL AUTO_INCREMENT,
    date DATE NOT NULL,
    etat VARCHAR(8) NOT NULL DEFAULT 'EN_COURS' CHECK(etat IN('EN_COURS', 'VALIDEE', 'ANNULEE')),
    total DECIMAL(10,2) NOT NULL,
    PRIMARY KEY(id_cmde)
);

CREATE TABLE IF NOT EXISTS LIG_COMMANDE (
    id_lig INT NOT NULL AUTO_INCREMENT,
    id_cmde INT NOT NULL,
    id_pro INT NOT NULL,
    qte_lig INT NOT NULL CHECK(qte_lig > 0),
    prix_unit DECIMAL(10,2) NOT NULL,
    montant DECIMAL(10,2) AS (qte_lig * prix_unit) STORED,
    PRIMARY KEY(id_lig),
    FOREIGN KEY(id_pro) REFERENCES PRODUIT(id_pro),
    FOREIGN KEY(id_cmde) REFERENCES COMMANDE(id_cmde)
);

CREATE TABLE IF NOT EXISTS UTILISATEUR(
    id_util INT NOT NULL AUTO_INCREMENT,
    nom_util VARCHAR(50) NOT NULL UNIQUE,
    mdp VARCHAR(256) NOT NULL,
    role VARCHAR(15) NOT NULL DEFAULT 'CAISSIER' CHECK(role IN ('ADMIN', 'CAISSIER')),
    PRIMARY KEY(id_util)
);

