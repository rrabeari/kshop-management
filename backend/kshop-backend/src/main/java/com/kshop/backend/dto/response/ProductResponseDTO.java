/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.dto.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Iris-PC
 */

/**
 * DTO utilisé pour retourner les informations d'un produit.
 *
 * IMPORTANT :
 * On ne retourne pas directement l'entité Product.
 *
 * Cela permet :
 * - de contrôler les données exposées par l'API ;
 * - d'éviter les problèmes de sérialisation JPA ;
 * - d'éviter les boucles dans les relations ;
 * - de préparer proprement l'API pour le frontend Angular.
 */
@Getter
@Setter
public class ProductResponseDTO {
    /**
     * Identifiant unique du produit.
     */
    private Long id;

    /**
     * Code interne du produit.
     *
     * Exemple : PRD001
     */
    private String code;

    /**
     * Code-barres du produit.
     */
    private String barcode;

    /**
     * Nom du produit.
     */
    private String name;

    /**
     * Description du produit.
     */
    private String description;

    /**
     * Prix d'achat.
     */
    private BigDecimal purchasePrice;

    /**
     * Prix de vente.
     */
    private BigDecimal sellingPrice;

    /**
     * Quantité actuellement disponible en stock.
     */
    private BigDecimal quantity;

    /**
     * Seuil minimum de stock.
     */
    private BigDecimal minimumStock;

    /**
     * Unité de mesure.
     *
     * Exemple :
     * piece
     * kg
     * litre
     */
    private String unit;

    /**
     * Indique si le produit est actif.
     */
    private Boolean active;

    /**
     * Catégorie du produit.
     *
     * On utilise CategoryResponseDTO au lieu
     * de retourner directement l'entité Category.
     */
    private CategoryResponseDTO category;

    /**
     * Constructeur vide.
     *
     * Nécessaire notamment pour Jackson.
     */
    public ProductResponseDTO() {
    }

    /**
     * Constructeur complet.
     *
     * @param id identifiant du produit
     * @param code code interne
     * @param barcode code-barres
     * @param name nom du produit
     * @param description description
     * @param purchasePrice prix d'achat
     * @param sellingPrice prix de vente
     * @param quantity quantité disponible
     * @param minimumStock seuil minimum
     * @param unit unité
     * @param active état du produit
     * @param category catégorie du produit
     */
    public ProductResponseDTO(
            Long id,
            String code,
            String barcode,
            String name,
            String description,
            BigDecimal purchasePrice,
            BigDecimal sellingPrice,
            BigDecimal quantity,
            BigDecimal minimumStock,
            String unit,
            Boolean active,
            CategoryResponseDTO category) {

        this.id = id;
        this.code = code;
        this.barcode = barcode;
        this.name = name;
        this.description = description;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.minimumStock = minimumStock;
        this.unit = unit;
        this.active = active;
        this.category = category;
    }
}
