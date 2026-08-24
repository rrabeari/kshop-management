/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.controller;

import com.kshop.backend.dto.request.ProductRequestDTO;
import com.kshop.backend.dto.response.ProductResponseDTO;
import com.kshop.backend.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des produits.
 * 
 * Il communique uniquement avec le ProductService et manipule
 * les objets DTO pour les échanges avec le client (Frontend Angular).
 *
 * @author Iris-PC
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Récupère la liste de tous les produits.
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    /**
     * Récupère un produit par son ID.
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.findByIdDTO(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Récupère un produit par son code.
     * GET /api/products/code/{code}
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ProductResponseDTO> getProductByCode(@PathVariable String code) {
        ProductResponseDTO product = productService.findByCode(code);
        return ResponseEntity.ok(product);
    }

    /**
     * Récupère un produit par son code-barres.
     * GET /api/products/barcode/{barcode}
     */
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ProductResponseDTO> getProductByBarcode(@PathVariable String barcode) {
        ProductResponseDTO product = productService.findByBarcode(barcode);
        return ResponseEntity.ok(product);
    }

    /**
     * Récupère les produits d'une catégorie spécifique.
     * GET /api/products/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductResponseDTO> products = productService.findByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    /**
     * Récupère uniquement les produits actifs.
     * GET /api/products/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<ProductResponseDTO>> getActiveProducts() {
        List<ProductResponseDTO> products = productService.findActiveProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Crée un nouveau produit.
     * POST /api/products
     */
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO createdProduct = productService.create(requestDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * Modifie un produit existant.
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO updatedProduct = productService.update(id, requestDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Désactive un produit.
     * PATCH /api/products/{id}/deactivate
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ProductResponseDTO> deactivateProduct(@PathVariable Long id) {
        ProductResponseDTO deactivatedProduct = productService.deactivate(id);
        return ResponseEntity.ok(deactivatedProduct);
    }

    /**
     * Réactive un produit.
     * PATCH /api/products/{id}/activate
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ProductResponseDTO> activateProduct(@PathVariable Long id) {
        ProductResponseDTO activatedProduct = productService.activate(id);
        return ResponseEntity.ok(activatedProduct);
    }

    /**
     * Supprime physiquement un produit.
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}