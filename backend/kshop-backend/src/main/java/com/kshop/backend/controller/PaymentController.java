/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.controller;

import com.kshop.backend.dto.request.PaymentRequestDTO;
import com.kshop.backend.dto.response.PaymentResponseDTO;
import com.kshop.backend.dto.response.PaymentStatisticsDTO;
import com.kshop.backend.service.PaymentService;
import com.kshop.backend.service.PaymentStatisticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Iris-PC
 */

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentStatisticsService statisticsService;

    // ============================================================
    // CRÉATION
    // ============================================================

    /**
     * POST /api/payments
     * 
     * Crée un nouveau paiement pour une vente.
     * 
     * Requête exemple :
     * {
     *     "saleId": 1,
     *     "amount": 100.00,
     *     "paymentMethod": "ESPECES",
     *     "reference": "CHQ-12345",
     *     "comment": "Paiement en espèces"
     * }
     * 
     * @param request données du paiement
     * @return paiement créé
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CAISSIER')")
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @Valid @RequestBody PaymentRequestDTO request) {

        PaymentResponseDTO payment = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    // ============================================================
    // LECTURE
    // ============================================================

    /**
     * GET /api/payments
     * 
     * Récupère la liste de tous les paiements.
     * 
     * @return liste des paiements
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> findAll() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    /**
     * GET /api/payments/{id}
     * 
     * Récupère un paiement par son identifiant.
     * 
     * @param id identifiant du paiement
     * @return paiement trouvé
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CAISSIER')")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    /**
     * GET /api/payments/sale/{saleId}
     * 
     * Récupère tous les paiements d'une vente.
     * 
     * @param saleId identifiant de la vente
     * @return liste des paiements de la vente
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CAISSIER')")
    @GetMapping("/sale/{saleId}")
    public ResponseEntity<List<PaymentResponseDTO>> findBySaleId(@PathVariable Long saleId) {
        return ResponseEntity.ok(paymentService.findBySaleId(saleId));
    }

    /**
     * GET /api/payments/user/{userId}
     * 
     * Récupère les paiements effectués par un utilisateur (caissier).
     * 
     * Règles de sécurité :
     * - ADMIN et MANAGER peuvent voir les paiements de n'importe quel utilisateur
     * - CAISSIER ne peut voir que ses propres paiements
     * 
     * @param userId identifiant de l'utilisateur
     * @return liste des paiements de l'utilisateur
     */
    @PreAuthorize("""
        hasAnyRole('ADMIN', 'MANAGER')
        or (hasRole('CAISSIER') and @currentUserSecurity.isCurrentUser(#userId))
    """)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponseDTO>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.findByUserId(userId));
    }

    /**
     * GET /api/payments/date-range
     * 
     * Récupère les paiements entre deux dates.
     * 
     * Exemple :
     * GET /api/payments/date-range?start=2026-01-01T00:00:00&end=2026-01-31T23:59:59
     * 
     * @param start date de début (ISO 8601)
     * @param end date de fin (ISO 8601)
     * @return liste des paiements
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/date-range")
    public ResponseEntity<List<PaymentResponseDTO>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return ResponseEntity.ok(paymentService.findByDateRange(start, end));
    }

    // ============================================================
    // ANNULATION
    // ============================================================

    /**
     * PATCH /api/payments/{id}/cancel
     * 
     * Annule un paiement (remboursement).
     * La restauration du stock est automatique.
     * 
     * Exemple :
     * PATCH /api/payments/1/cancel?reason=Paiement en double
     * 
     * @param id identifiant du paiement
     * @param reason motif de l'annulation (optionnel)
     * @return paiement annulé
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponseDTO> cancelPayment(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {

        return ResponseEntity.ok(paymentService.cancelPayment(id, reason));
    }

    // ============================================================
    // STATISTIQUES
    // ============================================================

    /**
     * GET /api/payments/statistics
     * 
     * Récupère les statistiques globales des paiements.
     * 
     * Retourne :
     * - Totaux par période (jour, semaine, mois, année)
     * - Nombre de paiements par statut
     * - Répartition par méthode de paiement
     * 
     * @return statistiques des paiements
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/statistics")
    public ResponseEntity<PaymentStatisticsDTO> getStatistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
}
