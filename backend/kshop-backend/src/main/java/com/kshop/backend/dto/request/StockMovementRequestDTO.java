/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.dto.request;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Iris-PC
 */

/**
 * DTO utilisé pour créer un mouvement de stock.
 *
 * IMPORTANT :
 *
 * Ce DTO est utilisé uniquement pour recevoir
 * les données envoyées par le frontend.
 *
 * L'entité JPA StockMovement n'est jamais
 * reçue directement par le Controller.
 *
 * Le produit est identifié par productId.
 *
 * L'utilisateur n'est volontairement PAS présent
 * dans ce DTO.
 *
 * Il est récupéré automatiquement depuis le JWT.
 */
@Getter
@Setter
public class StockMovementRequestDTO {
    /**
     * Type du mouvement.
     *
     * Valeurs autorisées :
     *
     * ENTREE
     * SORTIE
     * AJUSTEMENT
     */
    private String movementType;

    /**
     * Quantité du mouvement.
     */
    private BigDecimal quantity;

    /**
     * Motif du mouvement.
     */
    private String reason;
}
