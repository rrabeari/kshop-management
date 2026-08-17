/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kshop.backend.repository;

import com.kshop.backend.entity.SaleItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Iris-PC
 */

/**
 * Repository permettant de gérer les lignes de vente.
 *
 * Une ligne de vente correspond à un produit vendu
 * dans une vente.
 *
 * Architecture :
 *
 * Controller
 *      ↓
 * SaleService
 *      ↓
 * SaleItemRepository
 *      ↓
 * PostgreSQL
 *
 * JpaRepository fournit automatiquement les opérations CRUD.
 */
@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    /**
     * Recherche toutes les lignes appartenant
     * à une vente donnée.
     *
     * Exemple :
     *
     * Une vente possède :
     *
     * Sale ID = 10
     *
     * Les SaleItem associés peuvent être :
     *
     * Produit A → quantité 2
     * Produit B → quantité 5
     * Produit C → quantité 1
     *
     * @param saleId identifiant de la vente
     * @return liste des lignes de cette vente
     */
    List<SaleItem> findBySaleId(Long saleId);

    /**
     * Recherche toutes les ventes dans lesquelles
     * un produit donné apparaît.
     *
     * Cela pourra être utilisé plus tard pour obtenir
     * l'historique des ventes d'un produit.
     *
     * Exemple :
     *
     * Produit ID = 5
     *
     * → Vente 1
     * → Vente 8
     * → Vente 15
     *
     * @param productId identifiant du produit
     * @return liste des lignes de vente du produit
     */
    List<SaleItem> findByProductId(Long productId);
}
