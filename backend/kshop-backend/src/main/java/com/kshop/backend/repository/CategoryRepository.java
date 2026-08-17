/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kshop.backend.repository;

import com.kshop.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *
 * @author Iris-PC
 */

/**
 * Repository permettant d'accéder à la table "category".
 *
 * Spring Data JPA génère automatiquement les requêtes
 * correspondant aux méthodes héritées de JpaRepository.
 *
 * Exemple :
 *
 * findAll()      -> SELECT * FROM category
 * findById(id)   -> SELECT ... WHERE id = ?
 * save(category) -> INSERT ou UPDATE
 * deleteById(id) -> DELETE
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Recherche une catégorie par son nom.
     *
     * Cette méthode est générée automatiquement par Spring Data JPA.
     *
     * Elle permet notamment d'éviter de créer deux catégories
     * portant exactement le même nom.
     */
    Optional<Category> findByNameIgnoreCase(String name);

    /**
     * Vérifie si une catégorie existe déjà avec ce nom.
     */
    boolean existsByNameIgnoreCase(String name);
}
