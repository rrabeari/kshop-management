/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 *
 * @author Iris-PC
 */
@Getter
@Setter
@Builder
public class PaymentStatisticsDTO {
    // ============================================================
    // TOTAUX GÉNÉRAUX
    // ============================================================

    /**
     * Total global des paiements
     */
    private BigDecimal totalAmount;

    /**
     * Total des paiements du jour
     */
    private BigDecimal totalToday;

    /**
     * Total des paiements de la semaine
     */
    private BigDecimal totalThisWeek;

    /**
     * Total des paiements du mois
     */
    private BigDecimal totalThisMonth;

    /**
     * Total des paiements de l'année
     */
    private BigDecimal totalThisYear;

    // ============================================================
    // NOMBRE DE PAIEMENTS
    // ============================================================

    /**
     * Nombre total de paiements
     */
    private Long totalPayments;

    /**
     * Nombre de paiements du jour
     */
    private Long totalTodayPayments;

    /**
     * Nombre de paiements en attente
     */
    private Long totalPendingPayments;

    /**
     * Nombre de paiements payés
     */
    private Long totalPaidPayments;

    /**
     * Nombre de paiements remboursés
     */
    private Long totalRefundedPayments;

    // ============================================================
    // STATISTIQUES PAR MÉTHODE
    // ============================================================

    /**
     * Montant total par méthode de paiement
     * Clé : ESPECES, CARTE_BANCAIRE, etc.
     * Valeur : montant total
     */
    private Map<String, BigDecimal> amountByMethod;

    /**
     * Nombre de paiements par méthode de paiement
     * Clé : ESPECES, CARTE_BANCAIRE, etc.
     * Valeur : nombre de paiements
     */
    private Map<String, Long> countByMethod;

    // ============================================================
    // DATE DE GÉNÉRATION
    // ============================================================

    /**
     * Date et heure de génération des statistiques
     */
    private LocalDateTime generatedAt;
}
