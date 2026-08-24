/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 *
 * @author Iris-PC
 */
@Getter
@Setter
public class PaymentRequestDTO {
    /**
     * ID de la vente concernée
     */
    @NotNull(message = "L'ID de la vente est obligatoire")
    @Positive(message = "L'ID de la vente doit être positif")
    private Long saleId;

    /**
     * Montant du paiement
     */
    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal amount;

    /**
     * Méthode de paiement
     * Valeurs autorisées : ESPECES, CARTE_BANCAIRE, CHEQUE, VIREMENT, MOBILE_MONEY, CREDIT
     */
    @NotNull(message = "La méthode de paiement est obligatoire")
    private String paymentMethod;

    /**
     * Référence externe (optionnelle)
     * Exemple : numéro de chèque, numéro de transaction, etc.
     */
    private String reference;

    /**
     * Commentaire (optionnel)
     */
    private String comment;
}
