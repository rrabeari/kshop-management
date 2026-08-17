/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Iris-PC
 */

/**
 * DTO utilisé pour retourner les informations d'un mouvement de stock.
 *
 * IMPORTANT :
 * On ne retourne pas directement l'entité StockMovement.
 *
 * Les relations Product et User sont également converties
 * vers leurs DTO respectifs.
 *
 * Cela permet notamment d'éviter de retourner le password
 * de l'utilisateur dans la réponse JSON.
 */
@Getter
@Setter
public class StockMovementResponseDTO {
    /**
     * Identifiant du mouvement.
     */
    private Long id;

    /**
     * Date et heure du mouvement.
     */
    private LocalDateTime movementDate;

    /**
     * Type de mouvement.
     *
     * Exemples :
     * ENTREE
     * SORTIE
     */
    private String movementType;

    /**
     * Quantité concernée par le mouvement.
     */
    private BigDecimal quantity;

    /**
     * Motif du mouvement.
     */
    private String reason;

    /**
     * Produit concerné par le mouvement.
     *
     * On utilise ProductResponseDTO
     * au lieu de retourner directement Product.
     */
    private ProductResponseDTO product;

    /**
     * Utilisateur ayant effectué le mouvement.
     *
     * On utilise UserResponseDTO afin de ne jamais
     * retourner le mot de passe.
     */
    private UserResponseDTO user;

    /**
     * Constructeur vide.
     *
     * Nécessaire notamment pour Jackson.
     */
    public StockMovementResponseDTO() {
    }

    /**
     * Constructeur complet.
     *
     * @param id identifiant du mouvement
     * @param movementDate date du mouvement
     * @param movementType type de mouvement
     * @param quantity quantité
     * @param reason raison du mouvement
     * @param product produit concerné
     * @param user utilisateur ayant effectué le mouvement
     */
    public StockMovementResponseDTO(
            Long id,
            LocalDateTime movementDate,
            String movementType,
            BigDecimal quantity,
            String reason,
            ProductResponseDTO product,
            UserResponseDTO user) {

        this.id = id;
        this.movementDate = movementDate;
        this.movementType = movementType;
        this.quantity = quantity;
        this.reason = reason;
        this.product = product;
        this.user = user;
    }
}
