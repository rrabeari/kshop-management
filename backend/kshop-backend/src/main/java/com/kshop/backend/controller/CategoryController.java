/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.controller;

import com.kshop.backend.entity.Category;
import com.kshop.backend.service.CategoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 *
 * @author Iris-PC
 */


/**
 * Controller REST permettant de gérer les catégories.
 *
 * URL principale :
 *
 * /api/categories
 *
 * Les opérations disponibles sont :
 *
 * GET    /api/categories
 * GET    /api/categories/{id}
 * POST   /api/categories
 * PUT    /api/categories/{id}
 * DELETE /api/categories/{id}
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    /*
     * Service contenant la logique métier.
     */
    private final CategoryService categoryService;


    /**
     * GET /api/categories
     *
     * Récupère toutes les catégories.
     */
    @GetMapping
    public ResponseEntity<List<Category>> findAll() {

        List<Category> categories =
                categoryService.findAll();

        return ResponseEntity.ok(categories);
    }


    /**
     * GET /api/categories/{id}
     *
     * Récupère une catégorie spécifique.
     *
     * Exemple :
     *
     * GET /api/categories/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> findById(@PathVariable Long id) {
        Category category =categoryService.findById(id);
        return ResponseEntity.ok(category);
    }


    /**
     * POST /api/categories
     *
     * Crée une nouvelle catégorie.
     *
     * Exemple JSON :
     *
     * {
     *     "name": "Boissons",
     *     "description": "Boissons et produits liquides"
     * }
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STOCK')")
    @PostMapping
    public ResponseEntity<Category> create(@RequestBody Category category) {

        Category createdCategory =categoryService.create(category);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdCategory);
    }


    /**
     * PUT /api/categories/{id}
     *
     * Modifie une catégorie existante.
     *
     * Exemple :
     *
     * PUT /api/categories/1
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STOCK')")
    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Long id,@RequestBody Category category) {
        Category updatedCategory =categoryService.update(id, category);
        return ResponseEntity.ok(updatedCategory);
    }


    /**
     * DELETE /api/categories/{id}
     *
     * Supprime une catégorie.
     *
     * Exemple :
     *
     * DELETE /api/categories/1
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        categoryService.delete(id);

        /*
         * HTTP 204 signifie :
         *
         * "La requête a réussi mais il n'y a
         * aucun contenu à retourner."
         */
        return ResponseEntity.noContent().build();
    }
}
