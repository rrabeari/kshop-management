/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Iris-PC
 */

/**
 * DTO représentant une ligne lors de la création d'une vente.
 *
 * Ce DTO contient uniquement les informations nécessaires
 * pour demander la vente d'un produit.
 *
 * IMPORTANT :
 *
 * Le client ne fournit PAS :
 *
 * - le prix unitaire
 * - le sous-total
 * - le produit complet
 * - la vente
 *
 * Ces informations seront déterminées par le backend.
 *
 * Architecture :
 *
 * Frontend
 *      ↓
 * SaleItemRequestDTO
 *      ↓
 * SaleService
 *      ↓
 * Product
 *      ↓
 * Calcul du prix et du sous-total
 */
@Getter
@Setter
public class SaleItemRequestDTO {
    // ============================================================
    // PRODUIT
    // ============================================================

    /**
     * Identifiant du produit vendu.
     *
     * Le backend utilisera cet identifiant pour rechercher
     * le produit dans PostgreSQL.
     */
    @NotNull(message = "L'identifiant du produit est obligatoire.")
    private Long productId;

    // ============================================================
    // QUANTITÉ
    // ============================================================

    /**
     * Quantité du produit à vendre.
     *
     * La quantité doit être strictement supérieure à zéro.
     */
    @NotNull(message = "La quantité est obligatoire.")
    @DecimalMin(
            value = "0.01",
            message = "La quantité doit être supérieure à zéro."
    )
    private BigDecimal quantity;

    // ============================================================
    // CONSTRUCTEUR
    // ============================================================

    /**
     * Constructeur vide nécessaire pour la désérialisation
     * automatique du JSON par Spring Boot.
     */
    public SaleItemRequestDTO() {
    }

    
    
}
