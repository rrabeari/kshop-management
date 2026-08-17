/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.controller;

import com.kshop.backend.entity.Product;
import com.kshop.backend.service.ProductService;
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
 * Controller REST pour la gestion des produits.
 *
 * Toutes les routes commencent par :
 *
 * /api/products
 *
 * Architecture :
 *
 * Client / Angular
 *        ↓
 * ProductController
 *        ↓
 * ProductService
 *        ↓
 * ProductRepository
 *        ↓
 * PostgreSQL
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {
    /*
     * Service responsable de la logique métier
     * concernant les produits.
     */
    private final ProductService productService;

    /**
     * Injection du ProductService.
     *
     * Spring injecte automatiquement le service
     * grâce au constructeur.
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // =========================================================
    // GET ALL
    // =========================================================

    /**
     * Récupère tous les produits.
     *
     * GET /api/products
     *
     * Exemple de réponse :
     *
     * [
     *   {
     *     "id": 1,
     *     "code": "PRD001",
     *     "barcode": "123456789",
     *     "name": "Produit 1"
     *   }
     * ]
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {

        List<Product> products = productService.findAll();

        return ResponseEntity.ok(products);
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    /**
     * Recherche un produit par son ID.
     *
     * GET /api/products/{id}
     *
     * Exemple :
     *
     * GET /api/products/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    // =========================================================
    // GET BY CODE
    // =========================================================

    /**
     * Recherche un produit par son code.
     *
     * GET /api/products/code/{code}
     *
     * Exemple :
     *
     * GET /api/products/code/PRD001
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<Product> getProductByCode(@PathVariable String code) {
        Product product = productService.findByCode(code);
        return ResponseEntity.ok(product);
    }

    // =========================================================
    // GET BY BARCODE
    // =========================================================

    /**
     * Recherche un produit par son code-barres.
     *
     * GET /api/products/barcode/{barcode}
     *
     * Exemple :
     *
     * GET /api/products/barcode/1234567890123
     */
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<Product> getProductByBarcode(@PathVariable String barcode) {
        Product product = productService.findByBarcode(barcode);
        return ResponseEntity.ok(product);
    }

    // =========================================================
    // GET BY CATEGORY
    // =========================================================

    /**
     * Récupère les produits d'une catégorie.
     *
     * GET /api/products/category/{categoryId}
     *
     * Exemple :
     *
     * GET /api/products/category/1
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        List<Product> products =productService.findByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    // =========================================================
    // GET ACTIVE PRODUCTS
    // =========================================================

    /**
     * Récupère uniquement les produits actifs.
     *
     * GET /api/products/active
     *
     * active = true
     */
    @GetMapping("/active")
    public ResponseEntity<List<Product>> getActiveProducts() {
        List<Product> products =productService.findActiveProducts();
        return ResponseEntity.ok(products);
    }

    // =========================================================
    // GET ACTIVE PRODUCTS BY CATEGORY
    // =========================================================

    /**
     * Récupère les produits actifs d'une catégorie.
     *
     * GET /api/products/category/{categoryId}/active
     *
     * Exemple :
     *
     * GET /api/products/category/1/active
     */
    @GetMapping("/category/{categoryId}/active")
    public ResponseEntity<List<Product>>getActiveProductsByCategory( @PathVariable Long categoryId) {

        List<Product> products =productService.findActiveProductsByCategory(
                        categoryId
                );

        return ResponseEntity.ok(products);
    }

    // =========================================================
    // CREATE
    // =========================================================

    /**
     * Crée un nouveau produit.
     *
     * POST /api/products
     *
     * Content-Type :
     * application/json
     *
     * Exemple de JSON :
     *
     * {
     *   "code": "PRD001",
     *   "barcode": "1234567890123",
     *   "name": "Eau Vive",
     *   "description": "Bouteille 1.5L",
     *   "purchasePrice": 1000,
     *   "sellingPrice": 1500,
     *   "quantity": 50,
     *   "minimumStock": 10,
     *   "unit": "piece",
     *   "active": true,
     *   "category": {
     *     "id": 1
     *   }
     * }
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STOCK')")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {

        Product createdProduct =productService.create(product);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdProduct);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    /**
     * Modifie un produit existant.
     *
     * PUT /api/products/{id}
     *
     * Exemple :
     *
     * PUT /api/products/1
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STOCK')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,@RequestBody Product product) {
        Product updatedProduct =productService.update(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    // =========================================================
    // DELETE
    // =========================================================

    /**
     * Supprime définitivement un produit.
     *
     * DELETE /api/products/{id}
     *
     * Exemple :
     *
     * DELETE /api/products/1
     *
     * ATTENTION :
     * Cette opération supprime réellement le produit.
     *
     * Pour conserver l'historique des ventes,
     * il est généralement préférable d'utiliser
     * /deactivate.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // DEACTIVATE
    // =========================================================

    /**
     * Désactive un produit sans le supprimer.
     *
     * PATCH /api/products/{id}/deactivate
     *
     * Le produit reste dans PostgreSQL,
     * mais active devient false.
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Product> deactivateProduct(@PathVariable Long id) {

        Product product =productService.deactivate(id);

        return ResponseEntity.ok(product);
    }

    // =========================================================
    // ACTIVATE
    // =========================================================

    /**
     * Réactive un produit.
     *
     * PATCH /api/products/{id}/activate
     *
     * active devient true.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STOCK')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Product> activateProduct(@PathVariable Long id) {
        Product product =productService.activate(id);
        return ResponseEntity.ok(product);
    }

    // =========================================================
    // LOW STOCK
    // =========================================================

    /**
     * Vérifie si un produit est en stock faible.
     *
     * GET /api/products/{id}/low-stock
     *
     * Exemple :
     *
     * quantity = 5
     * minimumStock = 10
     *
     * Résultat :
     *
     * true
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STOCK')")
    @GetMapping("/{id}/low-stock")
    public ResponseEntity<Boolean> isLowStock(@PathVariable Long id) {
        Product product = productService.findById(id);
        boolean lowStock =productService.isLowStock(product);
        return ResponseEntity.ok(lowStock);
    }
}
