/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.service;

import com.kshop.backend.dto.request.ProductRequestDTO;
import com.kshop.backend.dto.response.ProductResponseDTO;
import com.kshop.backend.entity.Category;
import com.kshop.backend.entity.Product;
import com.kshop.backend.mapper.ProductMapper;
import com.kshop.backend.repository.CategoryRepository;
import com.kshop.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Iris-PC
 */

/**
 * Service métier pour la gestion des produits.
 * 
 * Il fait l'intermédiaire entre le Controller et le Repository, 
 * et utilise le ProductMapper statique pour transformer les entités en DTOs.
 */
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Injection du ProductRepository et du CategoryRepository.
     */
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Récupère tous les produits sous forme de DTO.
     *
     * @return liste de tous les produits (DTO)
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recherche un produit par son ID et retourne son DTO.
     *
     * @param id identifiant du produit
     * @return produit trouvé (DTO)
     */
    @Transactional(readOnly = true)
    public ProductResponseDTO findByIdDTO(Long id) {
        Product product = findEntityById(id);
        return ProductMapper.toResponseDTO(product);
    }

    /**
     * Méthode interne utilitaire pour récupérer l'entité brute si besoin dans le service.
     */
    @Transactional(readOnly = true)
    public Product findEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Produit introuvable avec l'id : " + id
                        )
                );
    }

    /**
     * Recherche un produit par son code.
     *
     * @param code code du produit
     * @return produit trouvé (DTO)
     */
    @Transactional(readOnly = true)
    public ProductResponseDTO findByCode(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Produit introuvable avec le code : " + code
                        )
                );
        return ProductMapper.toResponseDTO(product);
    }

    /**
     * Recherche un produit par son code-barres.
     *
     * @param barcode code-barres
     * @return produit trouvé (DTO)
     */
    @Transactional(readOnly = true)
    public ProductResponseDTO findByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Produit introuvable avec le code-barres : " + barcode
                        )
                );
        return ProductMapper.toResponseDTO(product);
    }

    /**
     * Récupère tous les produits d'une catégorie.
     *
     * @param categoryId ID de la catégorie
     * @return liste des produits de la catégorie (DTO)
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère uniquement les produits actifs.
     *
     * @return liste des produits actifs (DTO)
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findActiveProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les produits actifs d'une catégorie.
     *
     * @param categoryId ID de la catégorie
     * @return produits actifs de la catégorie (DTO)
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findActiveProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crée un nouveau produit à partir d'un ProductRequestDTO.
     *
     * @param requestDTO données du produit à créer
     * @return produit enregistré (DTO)
     */
    public ProductResponseDTO create(ProductRequestDTO requestDTO) {

        if (requestDTO.getCode() == null || requestDTO.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Le code du produit est obligatoire.");
        }

        if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit est obligatoire.");
        }

        if (productRepository.existsByCode(requestDTO.getCode())) {
            throw new IllegalArgumentException("Le code produit existe déjà : " + requestDTO.getCode());
        }

        if (requestDTO.getBarcode() != null && !requestDTO.getBarcode().trim().isEmpty()) {
            if (productRepository.existsByBarcode(requestDTO.getBarcode())) {
                throw new IllegalArgumentException("Le code-barres existe déjà : " + requestDTO.getBarcode());
            }
        }

        // Récupération de la catégorie via son ID
        Category category = null;
        if (requestDTO.getCategoryId() != null) {
            category = categoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec l'id : " + requestDTO.getCategoryId()));
        }

        // Conversion du DTO vers l'entité via le mapper
        Product product = ProductMapper.toEntity(requestDTO, category);

        if (product.getActive() == null) {
            product.setActive(true);
        }

        Product savedProduct = productRepository.save(product);
        return ProductMapper.toResponseDTO(savedProduct);
    }

    /**
     * Modifie un produit existant à partir d'un ProductRequestDTO.
     *
     * @param id ID du produit à modifier
     * @param requestDTO données du nouveau produit
     * @return produit modifié (DTO)
     */
    public ProductResponseDTO update(Long id, ProductRequestDTO requestDTO) {

        Product existingProduct = findEntityById(id);

        if (requestDTO.getCode() == null || requestDTO.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Le code du produit est obligatoire.");
        }

        if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit est obligatoire.");
        }

        if (!requestDTO.getCode().equals(existingProduct.getCode())
                && productRepository.existsByCode(requestDTO.getCode())) {
            throw new IllegalArgumentException("Le code produit existe déjà : " + requestDTO.getCode());
        }

        if (requestDTO.getBarcode() != null
                && !requestDTO.getBarcode().trim().isEmpty()
                && !requestDTO.getBarcode().equals(existingProduct.getBarcode())
                && productRepository.existsByBarcode(requestDTO.getBarcode())) {
            throw new IllegalArgumentException("Le code-barres existe déjà : " + requestDTO.getBarcode());
        }

        // Récupération de la catégorie si l'ID est fourni
        Category category = existingProduct.getCategory();
        if (requestDTO.getCategoryId() != null) {
            category = categoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec l'id : " + requestDTO.getCategoryId()));
        }

        existingProduct.setCode(requestDTO.getCode());
        existingProduct.setBarcode(requestDTO.getBarcode());
        existingProduct.setName(requestDTO.getName());
        existingProduct.setDescription(requestDTO.getDescription());
        existingProduct.setPurchasePrice(requestDTO.getPurchasePrice());
        existingProduct.setSellingPrice(requestDTO.getSellingPrice());
        existingProduct.setQuantity(requestDTO.getQuantity());
        existingProduct.setMinimumStock(requestDTO.getMinimumStock());
        existingProduct.setUnit(requestDTO.getUnit());
        existingProduct.setActive(requestDTO.getActive() != null ? requestDTO.getActive() : existingProduct.getActive());
        existingProduct.setCategory(category);

        Product updatedProduct = productRepository.save(existingProduct);
        return ProductMapper.toResponseDTO(updatedProduct);
    }

    /**
     * Supprime un produit.
     *
     * @param id ID du produit
     */
    public void delete(Long id) {
        Product product = findEntityById(id);
        productRepository.delete(product);
    }

    /**
     * Désactive un produit.
     *
     * @param id ID du produit
     * @return produit désactivé (DTO)
     */
    public ProductResponseDTO deactivate(Long id) {
        Product product = findEntityById(id);
        product.setActive(false);
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toResponseDTO(savedProduct);
    }

    /**
     * Réactive un produit.
     *
     * @param id ID du produit
     * @return produit réactivé (DTO)
     */
    public ProductResponseDTO activate(Long id) {
        Product product = findEntityById(id);
        product.setActive(true);
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toResponseDTO(savedProduct);
    }

    /**
     * Vérifie si un produit est en stock faible.
     *
     * @param product produit à vérifier
     * @return true si le stock est faible
     */
    public boolean isLowStock(Product product) {
        if (product.getQuantity() == null
                || product.getMinimumStock() == null) {
            return false;
        }

        return product.getQuantity()
                .compareTo(product.getMinimumStock()) <= 0;
    }
}