# KShop Management — Project Status

**Dernière mise à jour :** 12 août 2026  
**État global :** le projet est en cours de développement. Le module de ventes est fonctionnel jusqu'à la création d'une vente et la mise à jour du stock ; l'annulation de vente est la prochaine fonctionnalité à réaliser.

## Vue d'ensemble

KShop Management est une application de gestion de boutique avec un frontend Angular, une API REST Spring Boot, PostgreSQL, JPA/Hibernate et une authentification JWT avec contrôle des rôles.

Architecture en place :

```text
Angular → API REST → Controller → Service → Repository → JPA/Hibernate → PostgreSQL
```

## Progression globale

Les fondations backend, l'authentification/sécurité, la gestion des produits et du stock sont en place. Le module **Sales** est avancé : création de ventes multi-produits, contrôles métier et sorties de stock sont validés.

> La progression exacte de l'ensemble du projet n'est pas chiffrée ici ; ce document sert de référence opérationnelle pour poursuivre le développement sans perdre l'état validé.

## Sécurité et accès

| Élément | Statut |
|---|---|
| Authentification JWT | Validé |
| Filtre JWT et chargement de l'utilisateur | Validé |
| Rôles `ADMIN` / `MANAGER` | Validés |
| Protection des endpoints selon les rôles | Validée |
| Vérification des accès refusés (`403 Forbidden`) | Validée |

## J7 — Module Sales

**Avancement : 7 étapes validées sur 13.**

| Étape | Contenu | Statut |
|---|---|---|
| J7.1 | Entités `Sale` et `SaleItem` | Validé |
| J7.2 | Relations JPA | Validé |
| J7.3 | `SaleRequestDTO` | Validé |
| J7.4 | `SaleResponseDTO` | Validé |
| J7.5 | `SaleService` | Validé |
| J7.6 | `SaleController` | Validé |
| J7.7 | Tests métier et mise à jour du stock | Validé |
| J7.8 | Annulation de vente | À faire |
| J7.9 | Restauration automatique du stock | À faire |
| J7.10 | Transaction et rollback | À faire |
| J7.11 | Tests API complets | À faire |
| J7.12 | Sécurité `ADMIN` / `MANAGER` sur les actions de vente | À faire |
| J7.13 | Validation finale du module | À faire |

## Fonctionnalités Sales validées

- Enregistrement d'une vente et de ses lignes (`SaleItem`).
- Vente de plusieurs produits dans une seule transaction métier.
- Calcul des sous-totaux, de la remise et du total.
- Diminution du stock lors de la vente.
- Création des mouvements de stock de type `SORTIE`.
- Attribution de la vente et des mouvements à l'utilisateur connecté.
- Accès sécurisé pour les rôles autorisés.

## Tests métier validés (J7.7)

| Cas testé | Résultat attendu | Statut |
|---|---|---|
| Vente normale | Vente enregistrée et stock diminué | Validé |
| Stock insuffisant | Vente refusée | Validé |
| Produit inexistant | Vente refusée | Validé |
| Quantité nulle ou négative | Vente refusée | Validé |
| Produit désactivé | Vente refusée | Validé |
| Remise supérieure au total | Vente refusée | Validé |
| Vente multi-produits | Lignes, total et stocks cohérents | Validé |

## Vérification des mouvements de stock — Vente #2

La vente multi-produits **#2** a été vérifiée directement dans `stock_movement`.

| Produit | Quantité vendue | Mouvement créé | Utilisateur |
|---|---:|---|---:|
| `PRD001` (`product_id=2`) | 2 | `SORTIE 2.00` | 4 |
| `PRD002` (`product_id=3`) | 1 | `SORTIE 1.00` | 4 |

Conclusion : les lignes de vente, la diminution de stock et les `StockMovement` associés sont cohérents. La vente #2 a créé les deux sorties attendues, attribuées à l'utilisateur `4`.

## Prochaine étape — J7.8 : Annulation de vente

Implémenter l'annulation d'une vente terminée via un endpoint du type :

```text
POST /api/sales/{id}/cancel
```

Comportement attendu :

1. Vérifier que la vente existe et qu'elle est encore annulable (par exemple `COMPLETED`).
2. Changer son statut vers `CANCELLED`.
3. Restaurer le stock de chaque ligne de vente.
4. Créer un `StockMovement` de type `ENTREE` pour chaque produit restauré.
5. Empêcher une seconde annulation de la même vente.
6. Encadrer l'opération par une transaction afin qu'un échec restaure l'état initial.

Exemple pour la vente #2 :

```text
PRD001 : +2 en stock et mouvement ENTREE 2
PRD002 : +1 en stock et mouvement ENTREE 1
```

## Historique de validation

| Date | Élément validé | Notes |
|---|---|---|
| 11 août 2026 | Entrées, sorties et ajustements de stock | Mouvements de stock enregistrés dans la base. |
| 12 août 2026 | J7.1 à J7.6 | Modèle Sales, DTO, service et contrôleur opérationnels. |
| 12 août 2026 | J7.7 | Tests métier passés, dont la vente multi-produits. |
| 12 août 2026 | Vente #2 | Deux mouvements `SORTIE` vérifiés : PRD001 × 2 et PRD002 × 1, utilisateur 4. |
| 12 août 2026 | JWT et rôles | Authentification JWT et autorisations `ADMIN` / `MANAGER` validées. |

## Règle pour les prochaines mises à jour

À la fin de chaque étape, mettre à jour ce fichier avec : l'étape validée, les tests exécutés, les décisions métier prises, les endpoints concernés et les éventuels points bloquants.
