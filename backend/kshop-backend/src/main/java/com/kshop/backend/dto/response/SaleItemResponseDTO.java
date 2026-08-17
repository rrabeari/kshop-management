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
 * DTO de réponse représentant une ligne de vente.
 *
 * Ce DTO est utilisé pour retourner les informations
 * d'un produit vendu sans exposer directement l'entité
 * JPA SaleItem.
 *
 * Architecture :
 *
 * Sale
 *  ↓
 * SaleItem
 *  ↓
 * SaleItemResponseDTO
 *  ↓
 * Controller
 *  ↓
 * Frontend
 *
 * IMPORTANT :
 *
 * On retourne uniquement les informations nécessaires
 * à l'affichage de la vente.
 */
@Getter
@Setter
public class SaleItemResponseDTO {
    // ============================================================
    // IDENTIFIANT
    // ============================================================

    /**
     * Identifiant de la ligne de vente.
     */
    private Long id;

    // ============================================================
    // PRODUIT
    // ============================================================

    /**
     * Identifiant du produit vendu.
     */
    private Long productId;

    /**
     * Code interne du produit.
     *
     * Exemple :
     *
     * PRD001
     */
    private String productCode;

    /**
     * Nom du produit.
     *
     * Exemple :
     *
     * Eau Vive 1.5L
     */
    private String productName;

    // ============================================================
    // QUANTITÉ
    // ============================================================

    /**
     * Quantité vendue.
     */
    private BigDecimal quantity;

    // ============================================================
    // PRIX
    // ============================================================

    /**
     * Prix unitaire appliqué lors de la vente.
     *
     * Ce prix provient du backend et non
     * directement de la requête du client.
     */
    private BigDecimal unitPrice;

    /**
     * Montant total de cette ligne.
     *
     * subtotal = quantity × unitPrice
     */
    private BigDecimal subtotal;
}
