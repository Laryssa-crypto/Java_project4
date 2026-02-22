# 📖 Guide d'Utilisation — Gestion Restaurant

## 1. Connexion

Lancez l'application. La fenêtre de connexion s'affiche après l'écran de chargement. Si vous installez le système pour la première fois, le bouton pour créer un compte apparaîtra; une fois le premier compte (administrateur par défaut) créé, la création libre se désactivera pour des raisons de sécurité.

- Saisissez vos **identifiant** et **mot de passe**.
- L'accès aux modules dépend de votre rôle :
  - **Admin** : accès complet (Commandes, Produits, Stock, Statistiques, Administration).
  - **Caissier** : accès limité aux **Commandes**.

> 🔒 L'application se déconnecte automatiquement après **10 minutes d'inactivité**.

---

## 2. Commandes (Caissier & Admin)

### Créer une commande
1. Cliquez sur **Nouvelle Commande**.
2. Dans le champ de recherche, tapez le nom du produit — une liste de suggestions apparaît.
3. Sélectionnez le produit, ajustez la quantité, puis cliquez **Ajouter**. 
   *(Un sablier peut apparaître brièvement : l'application charge les données en arrière-plan pour rester fluide).*
4. Répétez pour chaque article.

### Valider une commande
1. Cliquez **Valider Commande**.
2. Confirmez la validation.
3. Un reçu client est généré. Il a été formaté avec précision et alignement pour intégrer l'en-tête, le type de commande et les noms complets des caissiers en bas. Le format d'impression est direct, et le PDF est enregistré dans le dossier `Recus/` avec une belle table `PdfPTable` propre à l'export.

> **Note sur l'identité** : Le nom du caissier connecté est désormais automatiquement attribué à chaque commande. Ces informations sont essentielles pour les rapports de performance par employé disponibles dans l'onglet Statistiques.

### Annuler une commande
- Cliquez **Annuler Commande**. Le stock est restitué si la commande était déjà validée.

---

## 3. Produits & Catégories (Admin)

- **Ajouter / Modifier / Supprimer** via les champs du formulaire.
- Les produits **en alerte** (stock ≤ seuil) s'affichent en **orange**, en **rupture** (stock = 0) en **rouge**.
- Utilisez la barre de recherche pour filtrer par nom.
- **Import / Export CSV** disponible pour les données produits.

---

## 4. Stock (Admin)

1. Sélectionnez un produit.
2. Choisissez **Entrée** ou **Sortie**, saisissez la quantité et un motif.
3. Cliquez **Enregistrer**.
4. L'historique (avec la Réf Facture unique générée à chaque mouvement) est visible en bas de page pour faciliter les audits.

> Un badge d'alerte en rouge apparaît dans la sidebar lorsque des produits sont sous leur seuil.

---

## 5. Statistiques (Admin)

- **CA journalier / par période** : calculé depuis la vue Statistiques.
- **Top produits** : par quantité ou par montant sur une plage personnalisable.
- **Alertes & Ruptures** : tableaux mis à jour en temps réel.
- **Exporter** :
  - 🖨️ Imprimer les statistiques (imprimante système)
  - 📄 Export PDF (iText)
  - 📊 Export CSV

---

## 6. Administration (Admin)

Accessible via le bouton **Gestion Employés** de la sidebar.

- Créer un employé, lui attribuer un rôle (**Admin** ou **Caissier**), définir son mot de passe.
- Modifier les informations d'un employé existant.
- Supprimer un compte (déconseillé si actif). **Note:** Un administrateur connecté ne peut pas supprimer son propre compte (blocage de sécurité).

---

## 7. Sauvegarde & Restauration (Admin)

Pensé pour garantir la continuité des finances en cas de panne, ce module est accessible via le bouton **Sauvegarde** de la sidebar.

- **Exporter la Sauvegarde** : Génère un fichier `.sql` contenant l'intégralité de l'historique et de la comptabilité du système via `mysqldump`.
- **Restaurer le Système** : Permet de réinjecter un fichier `.sql` précédent pour retrouver l'état du système à la date choisie. **Attention**, cette action efface les données en cours.

> *Prérequis technique* : Assurez-vous que les commandes `mysql` et `mysqldump` sont reconnues par votre système d'exploitation.

---

## 8. Dépannage

| Problème | Solution |
|---|---|
| Impossible de se connecter | Vérifiez que MySQL est démarré et que `config.properties` est correctement renseigné. |
| Produit impossible à supprimer | Le produit est lié à des commandes existantes — archivez-le plutôt. |
| Impression sans résultat | Vérifiez qu'une imprimante système est configurée et disponible. |
| Export CSV vide | Assurez-vous qu'il existe des données de commande validées pour la période. |
