/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kshop.backend.repository;

import com.kshop.backend.entity.Sale;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Iris-PC
 */

/**
 * Repository permettant d'accéder aux données des ventes.
 *
 * Architecture :
 *
 * Controller
 *      ↓
 * SaleService
 *      ↓
 * SaleRepository
 *      ↓
 * PostgreSQL
 *
 * JpaRepository fournit automatiquement les opérations CRUD :
 *
 * - save()
 * - findAll()
 * - findById()
 * - existsById()
 * - deleteById()
 *
 * Aucun SQL manuel n'est nécessaire pour ces opérations.
 */
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    /**
     * Récupère toutes les ventes triées
     * de la plus récente à la plus ancienne.
     *
     * Spring Data JPA construit automatiquement
     * la requête SQL correspondante grâce au nom
     * de la méthode.
     *
     * Exemple :
     *
     * findAllByOrderBySaleDateDesc()
     *
     * signifie :
     *
     * SELECT *
     * FROM sales
     * ORDER BY sale_date DESC
     *
     * @return liste des ventes triées par date décroissante
     */
    List<Sale> findAllByOrderBySaleDateDesc();

    List<Sale> findByUserIdOrderBySaleDateDesc(Long userId);
}
