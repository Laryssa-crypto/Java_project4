# Gestion Restaurant - Application de Gestion de Restaurant

## 📋 Description

Application de bureau Java pour la gestion d'un restaurant, développée avec une architecture en **4 couches logiques** (Vue, Contrôleur, Service, DAO) et une interface Swing moderne.

## 🏗️ Architecture

L'application suit une architecture MVC (Modèle-Vue-Contrôleur) :

```
com.restaurant/
├── model          → Entités (classes métier)
│   ├── Categorie.java
│   ├── Produit.java
│   ├── MouvementStock.java
│   ├── Commande.java
│   ├── LigneCommande.java
│   ├── Utilisateur.java
│   └── enums
│        ├── TypeMouvement.java
│        ├── EtatCommande.java
│        └── Role.java
│
├── dao            → Accès à la base de données (JDBC)
│   ├── ConnectionDB.java
│   ├── CategorieDAO.java
│   ├── ProduitDAO.java          ← inclut ProduitLieACommandeException
│   ├── MouvementStockDAO.java
│   ├── CommandeDAO.java
│   ├── LigneCommandeDAO.java
│   └── UtilisateurDAO.java
│
├── service        → Logique métier (validation + règles de gestion)
│   ├── AuthService.java
│   ├── CategorieService.java
│   ├── ProduitService.java
│   ├── StockService.java
│   ├── CommandeService.java
│   ├── StatistiqueService.java
│   ├── PrintService.java
│   ├── PdfExportService.java
│   ├── CsvService.java
│   └── DatabaseBackupService.java
│
├── controller     → Contrôleurs (liaison View ↔ Service)
│   ├── LoginController.java
│   ├── ProduitController.java
│   ├── StockController.java
│   ├── CommandeController.java
│   ├── StatistiqueController.java
│   └── DatabaseController.java
│
├── view           → Interfaces graphiques (Swing)
│   ├── SplashScreen.java
│   ├── LoginView.java
│   ├── MainView.java
│   ├── ProduitView.java
│   ├── StockView.java
│   ├── CommandeView.java
│   ├── StatistiqueView.java
│   └── DatabaseView.java
│
└── utils          → Classes utilitaires et support
    ├── DesignSystem.java        ← couleurs, polices, composants centralisés
    ├── ValidationUtils.java
    ├── ValidationUtils.java
    ├── PasswordUtils.java
    └── DateUtils.java
```

## 🗄️ Base de données

### Schéma SQL

```sql
CREATE DATABASE gestion_restaurant;

CREATE TABLE CATEGORIE (
    id_cat INT NOT NULL AUTO_INCREMENT,
    libelle_cat VARCHAR(30) NOT NULL UNIQUE,
    PRIMARY KEY(id_cat)
);

CREATE TABLE PRODUIT (
    id_pro INT NOT NULL AUTO_INCREMENT,
    nom_pro VARCHAR(50) NOT NULL,
    id_cat INT NOT NULL,
    prix_vente DECIMAL(10,2) NOT NULL CHECK(prix_vente > 0),
    stock_actu INT NOT NULL CHECK(stock_actu >= 0),
    seuil_alerte INT NOT NULL,
    PRIMARY KEY(id_pro),
    FOREIGN KEY(id_cat) REFERENCES CATEGORIE(id_cat)
);

CREATE TABLE MOUVEMENT_STOCK (
    id_mvt INT NOT NULL AUTO_INCREMENT,
    type VARCHAR(6) NOT NULL CHECK(type IN('ENTREE', 'SORTIE')),
    id_pro INT NOT NULL,
    quantite INT NOT NULL CHECK(quantite > 0),
    date DATE NOT NULL,
    motif VARCHAR(50),
    PRIMARY KEY(id_mvt),
    FOREIGN KEY(id_pro) REFERENCES PRODUIT(id_pro)
);

CREATE TABLE COMMANDE (
    id_cmde INT NOT NULL AUTO_INCREMENT,
    date DATE NOT NULL,
    etat VARCHAR(8) NOT NULL DEFAULT 'EN_COURS'
        CHECK(etat IN('EN_COURS', 'VALIDEE', 'ANNULEE')),
    total DECIMAL(10,2) NOT NULL,
    PRIMARY KEY(id_cmde)
);

CREATE TABLE LIGNE_COMMANDE (
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

CREATE TABLE UTILISATEUR (
    id_uti INT NOT NULL AUTO_INCREMENT,
    nom_uti VARCHAR(50) NOT NULL UNIQUE,
    mdp VARCHAR(256) NOT NULL,
    role VARCHAR(8) NOT NULL DEFAULT 'ADMIN'
        CHECK(role IN('ADMIN', 'CAISSIER')),
    PRIMARY KEY(id_uti)
);
```

## 🚀 Installation et Configuration

### Prérequis

- Java 21 (Recommandé) ou supérieur
- MySQL Server 8.0 ou supérieur
- NetBeans IDE (recommandé)
- Bibliothèques dans `/lib` : JFreeChart, iText, Apache POI, Log4j2, MySQL Connector/J

### Configuration

1. **Base de données**
   - Démarrer le serveur MySQL
   - Exécuter le script SQL fourni : `database_setup.sql`

2. **Connexion** — créer `config.properties` à la racine du projet (fichier non versionné) :
   ```properties
   db.url=jdbc:mysql://localhost:3306/gestion_restaurant
   db.user=root
   db.password=votre_mot_de_passe
   ```

3. **Comptes par défaut**

   | Rôle | Login | Mot de passe |
   |---|---|---|
   | Admin | `admin` | `admin` |

> *Note : Le premier compte créé à l'initialisation de l'application prend automatiquement le rôle `ADMIN`.*

### Compilation et Exécution

```bash
# Compilation
javac -d bin -cp "src/:lib/*" $(find src -name "*.java")

# Exécution
java -cp "bin:lib/*" com.restaurant.Main
```

Ou directement depuis **NetBeans** : `Run Project`.

## 📱 Fonctionnalités

### 🔐 Authentification & Sécurité
- Connexion avec identifiant et mot de passe (haché BCrypt)
- Rôles **Admin** et **Caissier** — accès aux modules selon le rôle
- **Initialisation** : Le bouton de création de compte n'est visible que si la base de données est vide (installation initiale).
- Déconnexion automatique après 10 minutes d'inactivité

### 🍽️ Gestion des produits et catégories
- CRUD complet sur les catégories et produits
- Recherche rapide par nom (auto-complétion)
- Coloration automatique : orange = alerte, rouge = rupture
- Import / Export **CSV**

### 📦 Gestion du stock
- Enregistrement des entrées et sorties de stock
- Historique filtrable par type ou produit
- Badge d'alerte dans la sidebar lorsque le stock est critique

### 🛒 Gestion des commandes
- Création, ajout de produits (auto-complétion), modification de quantité
- Validation atomique via transaction SQL (stock déduit, état mis à jour)
- Annulation avec restitution du stock
- Impression après validation : **Reçu Client**, **Format Gestion**, ou les deux

### 📊 Statistiques et rapports
- CA journalier / sur plage personnalisable
- Top produits vendus (par quantité ou par montant)
- Alertes et ruptures en temps réel
- Graphiques JFreeChart intégrés au tableau de bord
- Export **PDF** (iText) et **CSV**

### 👤 Administration (Admin uniquement)
- Création, modification, suppression de comptes employés
- **Sécurité** : L'administrateur courant ne peut pas s'auto-supprimer pour éviter d'être bloqué hors du système.
- Attribution des rôles
- **Sauvegarde et Restauration SQL** : Export automatique (`mysqldump`) de l'intégralité de la base et réimportation depuis l'interface en cas de panne (Continuité métier).

## 🔧 Règles métier

- Le prix de vente doit être strictement positif
- Le stock ne peut pas être négatif
- La quantité d'un mouvement doit être > 0
- Une sortie est refusée si la quantité dépasse le stock disponible
- Une commande doit contenir au moins une ligne pour être validée
- Un produit lié à des commandes ne peut pas être supprimé
- Login unique par utilisateur

## 🎯 Points forts

- ✅ Architecture MVC rigoureuse
- ✅ Transactions SQL atomiques (commit/rollback)
- ✅ Gestion spécifique des exceptions (SQLException, NumberFormatException…)
- ✅ Design System centralisé (couleurs, polices, composants)
- ✅ Performance : Multithreading via `SwingWorker` (UI non-bloquante)
- ✅ Logging via Log4j2
- ✅ Statistiques avancées avec graphiques et exports multi-formats (PDF formaté en tableaux compréhensifs, impressions papier réorganisées)
- ✅ Import/Export CSV

## 🐛 Dépannage

| Problème | Solution |
|---|---|
| Connexion refusée | Vérifier que MySQL est démarré et que `config.properties` est correct |
| Driver introuvable | Vérifier que `mysql-connector-j-*.jar` est dans `/lib` |
| Produit non supprimable | Le produit est lié à des commandes — archivez-le plutôt |
| Impression vide | Vérifier qu'une imprimante système est configurée |
| Export CSV vide | Vérifier qu'il existe des commandes validées pour la période |
| Compilation échoue | Vérifier que tous les JARs de `/lib` sont dans le classpath |

## 👨‍💻 Auteurs

Développé dans le cadre du projet de POO Java à l'**IAI-TOGO** (2025-2026).

## 📄 Licence

Ce projet est développé à des fins pédagogiques.
