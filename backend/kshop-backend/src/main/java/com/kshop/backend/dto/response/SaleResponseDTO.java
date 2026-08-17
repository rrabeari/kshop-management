/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Iris-PC
 */

/**
 * DTO de réponse représentant une vente.
 *
 * Ce DTO permet de retourner les informations
 * nécessaires au frontend sans exposer directement
 * l'entité JPA Sale.
 *
 * Architecture :
 *
 * Sale
 *  ↓
 * SaleResponseDTO
 *  ↓
 * Controller
 *  ↓
 * Frontend
 *
 * IMPORTANT :
 *
 * Nous n'exposons jamais directement l'entité User.
 * Les informations retournées concernant l'utilisateur
 * sont limitées aux données nécessaires à l'affichage.
 *
 * Le password n'est donc jamais exposé.
 */
@Getter
@Setter
public class SaleResponseDTO {
    // ============================================================
    // IDENTIFIANT
    // ============================================================

    /**
     * Identifiant unique de la vente.
     */
    private Long id;

    // ============================================================
    // DATE
    // ============================================================

    /**
     * Date et heure auxquelles la vente a été enregistrée.
     */
    private LocalDateTime saleDate;

    // ============================================================
    // MONTANTS
    // ============================================================

    /**
     * Montant total de la vente après application
     * de la remise.
     */
    private BigDecimal totalAmount;

    /**
     * Remise appliquée à la vente.
     */
    private BigDecimal discount;

    // ============================================================
    // STATUT
    // ============================================================

    /**
     * Statut de la vente.
     *
     * Exemples :
     *
     * COMPLETED
     * CANCELLED
     * PENDING
     */
    private String status;

    // ============================================================
    // UTILISATEUR
    // ============================================================

    /**
     * Identifiant de l'utilisateur ayant effectué
     * la vente.
     */
    private Long userId;

    /**
     * Nom d'utilisateur de la personne ayant effectué
     * la vente.
     */
    private String username;

    /**
     * Prénom de l'utilisateur.
     */
    private String firstName;

    /**
     * Nom de famille de l'utilisateur.
     */
    private String lastName;

    /**
     * Rôle de l'utilisateur.
     *
     * Exemple :
     *
     * ADMIN
     * MANAGER
     */
    private String role;

    // ============================================================
    // LIGNES DE VENTE
    // ============================================================

    /**
     * Liste des produits composant la vente.
     *
     * Chaque élément est converti en
     * SaleItemResponseDTO.
     */
    private List<SaleItemResponseDTO> items;
}
