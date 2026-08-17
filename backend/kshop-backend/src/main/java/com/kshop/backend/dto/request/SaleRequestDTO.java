/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Iris-PC
 */


/**
 * DTO utilisé pour créer une nouvelle vente.
 *
 * Le frontend envoie uniquement les informations
 * nécessaires à la création de la vente.
 *
 * Exemple :
 *
 * {
 *     "discount": 500,
 *     "items": [
 *         {
 *             "productId": 2,
 *             "quantity": 3
 *         }
 *     ]
 * }
 *
 * Le backend calculera automatiquement :
 *
 * - le prix unitaire
 * - le sous-total de chaque ligne
 * - le montant total
 * - la date de vente
 * - l'utilisateur connecté
 * - la diminution du stock
 * - le mouvement de stock SORTIE
 *
 * IMPORTANT :
 *
 * Le frontend ne doit jamais pouvoir imposer
 * le prix de vente final.
 */
@Getter
@Setter
public class SaleRequestDTO {
    // ============================================================
    // REMISE
    // ============================================================

    /**
     * Remise globale appliquée à la vente.
     *
     * La remise est optionnelle.
     *
     * Si aucune remise n'est fournie,
     * le service pourra considérer sa valeur comme zéro.
     */
    @DecimalMin(
            value = "0.00",
            message = "La remise ne peut pas être négative."
    )
    private BigDecimal discount = BigDecimal.ZERO;

    // ============================================================
    // LIGNES DE VENTE
    // ============================================================

    /**
     * Liste des produits composant la vente.
     *
     * Une vente doit contenir au moins une ligne.
     *
     * @Valid permet à Spring de vérifier également
     * chaque SaleItemRequestDTO contenu dans cette liste.
     */
    @NotEmpty(message = "Une vente doit contenir au moins un produit.")
    @Valid
    private List<SaleItemRequestDTO> items;

    // ============================================================
    // CONSTRUCTEUR
    // ============================================================

    /**
     * Constructeur vide nécessaire pour que Spring Boot
     * puisse créer automatiquement l'objet à partir du JSON.
     */
    public SaleRequestDTO() {
    }
}
