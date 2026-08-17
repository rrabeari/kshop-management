/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.mapper;

import com.kshop.backend.dto.response.CategoryResponseDTO;
import com.kshop.backend.dto.response.ProductResponseDTO;
import com.kshop.backend.entity.Category;
import com.kshop.backend.entity.Product;

/**
 *
 * @author Iris-PC
 */

/**
 * Mapper permettant de convertir les entités JPA
 * vers les DTO utilisés dans les réponses de l'API.
 *
 * IMPORTANT :
 * Ce mapper ne modifie jamais l'entité Product.
 *
 * Il crée simplement un ProductResponseDTO
 * contenant uniquement les informations que nous
 * voulons exposer au frontend.
 */
public class ProductMapper {
    /**
     * Constructeur privé.
     *
     * Cette classe contient uniquement des méthodes statiques.
     * Il n'est donc pas nécessaire de créer une instance.
     */
    private ProductMapper() {
    }

    /**
     * Convertit une entité Product en ProductResponseDTO.
     *
     * @param product entité Product provenant de la base de données
     * @return ProductResponseDTO ou null si product est null
     */
    public static ProductResponseDTO toResponseDTO(Product product) {

        // Protection contre une valeur null
        if (product == null) {
            return null;
        }

        /*
         * Conversion de la catégorie.
         *
         * Product contient :
         * private Category category;
         *
         * Mais notre DTO contient :
         * private CategoryResponseDTO category;
         *
         * Nous devons donc effectuer la conversion.
         */
        CategoryResponseDTO categoryDTO = toCategoryResponseDTO(
                product.getCategory()
        );

        /*
         * Création du DTO Product.
         *
         * On récupère uniquement les champs nécessaires.
         */
        return new ProductResponseDTO(
                product.getId(),
                product.getCode(),
                product.getBarcode(),
                product.getName(),
                product.getDescription(),
                product.getPurchasePrice(),
                product.getSellingPrice(),
                product.getQuantity(),
                product.getMinimumStock(),
                product.getUnit(),
                product.getActive(),
                categoryDTO
        );
    }

    /**
     * Convertit une entité Category en CategoryResponseDTO.
     *
     * @param category entité Category
     * @return CategoryResponseDTO ou null si category est null
     */
    private static CategoryResponseDTO toCategoryResponseDTO(Category category) {

        // Si le produit n'a pas de catégorie
        if (category == null) {
            return null;
        }

        /*
         * On ne retourne pas directement Category.
         * On crée un DTO contenant uniquement :
         * - id
         * - name
         * - description
         */
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
