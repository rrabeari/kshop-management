/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kshop.backend.repository;

import com.kshop.backend.entity.Payment;
import com.kshop.backend.entity.enums.PaymentMethod;
import com.kshop.backend.entity.enums.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Iris-PC
 */
public interface PaymentRepository extends JpaRepository<Payment, Long>{
    /**
     * Recherche les paiements d'une vente
     */
    List<Payment> findBySaleId(Long saleId);

    /**
     * Recherche les paiements par statut
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Recherche les paiements par méthode
     */
    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);

    /**
     * Recherche les paiements par utilisateur (caissier)
     */
    List<Payment> findByUserId(Long userId);

    /**
     * Recherche les paiements entre deux dates
     */
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Recherche les paiements par référence
     */
    List<Payment> findByReferenceContainingIgnoreCase(String reference);

    // ============================================================
    // REQUÊTES AVEC CALCULS
    // ============================================================

    /**
     * Total des paiements payés pour une vente
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.sale.id = :saleId AND p.status = 'PAID'")
    BigDecimal sumPaidPaymentsBySaleId(@Param("saleId") Long saleId);

    /**
     * Total des paiements payés entre deux dates
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PAID' AND p.paymentDate BETWEEN :start AND :end")
    BigDecimal sumPaidPaymentsBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Vérifier si une vente a des paiements payés
     */
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.sale.id = :saleId AND p.status = 'PAID'")
    boolean hasPaidPayments(@Param("saleId") Long saleId);

    /**
     * Vérifier si une vente a des paiements remboursés
     */
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.sale.id = :saleId AND p.status = 'REFUNDED'")
    boolean hasRefundedPayments(@Param("saleId") Long saleId);

    /**
     * Nombre de paiements par méthode
     */
    @Query("SELECT p.paymentMethod, COUNT(p) FROM Payment p WHERE p.status = 'PAID' GROUP BY p.paymentMethod")
    List<Object[]> countPaymentsByMethod();

    /**
     * Total des paiements par méthode
     */
    @Query("SELECT p.paymentMethod, SUM(p.amount) FROM Payment p WHERE p.status = 'PAID' GROUP BY p.paymentMethod")
    List<Object[]> sumPaymentsByMethod();

    /**
     * Vérifier si un paiement existe par ID et statut
     */
    boolean existsByIdAndStatus(Long id, PaymentStatus status);
}
