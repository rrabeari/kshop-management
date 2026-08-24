/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.entity;

import com.kshop.backend.entity.enums.PaymentMethod;
import com.kshop.backend.entity.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Iris-PC
 */
@Entity
@Getter
@Setter
@Table(name = "payment", indexes = {
    @Index(name = "idx_payment_sale", columnList = "sale_id"),
    @Index(name = "idx_payment_status", columnList = "status"),
    @Index(name = "idx_payment_method", columnList = "payment_method"),
    @Index(name = "idx_payment_date", columnList = "payment_date")
})
public class Payment extends BaseEntity{
    /**
     * Montant du paiement
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /**
     * Méthode de paiement (ESPECES, CARTE_BANCAIRE, etc.)
     */
    @Column(name = "payment_method", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    /**
     * Statut du paiement (PENDING, PAID, REFUNDED)
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    /**
     * Date du paiement
     */
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    /**
     * Référence externe (numéro de transaction, chèque, etc.)
     */
    @Column(name = "reference", length = 100)
    private String reference;

    /**
     * Commentaire sur le paiement
     */
    @Column(length = 500)
    private String comment;

    /**
     * Vente associée
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    /**
     * Utilisateur qui a enregistré le paiement (caissier)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ============================================================
    // CONSTRUCTEURS
    // ============================================================

    public Payment() {
        this.paymentDate = LocalDateTime.now();
    }

    public Payment(BigDecimal amount, PaymentMethod paymentMethod, Sale sale, User user) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.sale = sale;
        this.user = user;
        this.paymentDate = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    // ============================================================
    // MÉTHODES UTILITAIRES
    // ============================================================

    /**
     * Marquer le paiement comme payé
     */
    public void markAsPaid() {
        this.status = PaymentStatus.PAID;
    }

    /**
     * Marquer le paiement comme remboursé
     */
    public void refund() {
        this.status = PaymentStatus.REFUNDED;
    }

    /**
     * Vérifier si le paiement est payé
     * @return 
     */
    public boolean isPaid() {
        return this.status == PaymentStatus.PAID;
    }

    /**
     * Vérifier si le paiement est en attente
     * @return 
     */
    public boolean isPending() {
        return this.status == PaymentStatus.PENDING;
    }

    /**
     * Vérifier si le paiement est remboursé
     * @return 
     */
    public boolean isRefunded() {
        return this.status == PaymentStatus.REFUNDED;
    }
}
