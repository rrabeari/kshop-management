/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kshop.backend.repository;

import com.kshop.backend.entity.StockMovement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Iris-PC
 */


/**
 * Repository permettant d'effectuer les opérations d'accès
 * à la table stock_movement.
 *
 * Spring Data JPA fournit automatiquement les méthodes
 * CRUD de base grâce à JpaRepository :
 *
 * - save()
 * - findById()
 * - findAll()
 * - deleteById()
 * - existsById()
 * etc.
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    /**
     * Récupère tous les mouvements associés à un produit.
     *
     * Exemple :
     * findByProductId(1L)
     *
     * retournera tous les mouvements du produit ayant l'id 1.
     */
    List<StockMovement> findByProductId(Long productId);

    /**
     * Récupère tous les mouvements effectués par un utilisateur.
     *
     * Exemple :
     * findByUserId(1L)
     *
     * retournera tous les mouvements effectués
     * par l'utilisateur ayant l'id 1.
     */
    List<StockMovement> findByUserId(Long userId);

    /**
     * Récupère les mouvements selon leur type.
     *
     * Exemples :
     * "ENTREE"
     * "SORTIE"
     * "AJUSTEMENT"
     */
    List<StockMovement> findByMovementType(String movementType);

    /**
     * Récupère les mouvements d'un produit
     * selon leur type.
     *
     * Exemple :
     * findByProductIdAndMovementType(1L, "ENTREE")
     */
    List<StockMovement> findByProductIdAndMovementType(
            Long productId,
            String movementType
    );
}
