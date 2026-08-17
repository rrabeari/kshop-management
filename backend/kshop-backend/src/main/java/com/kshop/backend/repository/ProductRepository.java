/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kshop.backend.repository;

import com.kshop.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Iris-PC
 */

/**
 * Repository de l'entité Product.
 *
 * Cette interface permet à Spring Data JPA
 * de communiquer avec la table "product".
 *
 * Nous n'avons pas besoin d'écrire nous-mêmes
 * les requêtes CRUD de base.
 *
 * JpaRepository fournit automatiquement :
 *
 * - findAll()
 * - findById()
 * - save()
 * - delete()
 * - deleteById()
 * - count()
 * - existsById()
 * - etc.
 */
public interface ProductRepository extends JpaRepository<Product, Long>{
    /**
     * Recherche un produit par son code.
     *
     * Exemple :
     *
     * code = "PRD001"
     */
    Optional<Product> findByCode(String code);

    /**
     * Vérifie si un produit existe déjà
     * avec le même code.
     *
     * Cette méthode sera utile lors de la création
     * d'un nouveau produit.
     */
    boolean existsByCode(String code);

    /**
     * Recherche un produit par son code-barres.
     *
     * Exemple :
     *
     * barcode = "1234567890123"
     */
    Optional<Product> findByBarcode(String barcode);

    /**
     * Vérifie si un code-barres existe déjà.
     */
    boolean existsByBarcode(String barcode);

    /**
     * Recherche les produits appartenant
     * à une catégorie donnée.
     *
     * Exemple :
     *
     * categoryId = 1
     *
     * retourne tous les produits dont
     * category_id = 1.
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Recherche uniquement les produits actifs.
     *
     * active = true
     */
    List<Product> findByActiveTrue();

    /**
     * Recherche les produits actifs
     * d'une catégorie donnée.
     *
     * Exemple :
     *
     * categoryId = 1
     * active = true
     */
    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);
}
