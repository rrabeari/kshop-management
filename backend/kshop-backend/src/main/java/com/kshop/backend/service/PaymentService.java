/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.service;

import com.kshop.backend.dto.request.PaymentRequestDTO;
import com.kshop.backend.dto.response.PaymentResponseDTO;
import com.kshop.backend.entity.Payment;
import com.kshop.backend.entity.Product;
import com.kshop.backend.entity.Sale;
import com.kshop.backend.entity.SaleItem;
import com.kshop.backend.entity.StockMovement;
import com.kshop.backend.entity.User;
import com.kshop.backend.entity.enums.PaymentMethod;
import com.kshop.backend.repository.PaymentRepository;
import com.kshop.backend.repository.ProductRepository;
import com.kshop.backend.repository.SaleRepository;
import com.kshop.backend.repository.StockMovementRepository;
import com.kshop.backend.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Iris-PC
 */

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    // ============================================================
    // CRÉATION D'UN PAIEMENT
    // ============================================================

    /**
     * Crée un nouveau paiement pour une vente existante.
     * Le stock n'est pas modifié (la vente a déjà sorti le stock).
     */
    public PaymentResponseDTO createPayment(PaymentRequestDTO request) {

        // 1. Récupération de la vente
        Sale sale = saleRepository.findById(request.getSaleId())
                .orElseThrow(() -> new RuntimeException(
                        "Vente introuvable avec l'ID : " + request.getSaleId()
                ));

        // 2. Vérification de la vente
        if ("CANCELLED".equalsIgnoreCase(sale.getStatus())) {
            throw new IllegalArgumentException(
                    "Impossible de payer une vente annulée."
            );
        }

        // 3. Vérification du montant
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Le montant doit être supérieur à 0."
            );
        }

        // 4. Calcul du reste à payer
        BigDecimal totalPaid = paymentRepository.sumPaidPaymentsBySaleId(sale.getId());
        if (totalPaid == null) {
            totalPaid = BigDecimal.ZERO;
        }
        BigDecimal remaining = sale.getTotalAmount().subtract(totalPaid);

        if (request.getAmount().compareTo(remaining) > 0) {
            throw new IllegalArgumentException(
                    "Le montant du paiement (" + request.getAmount() +
                    ") dépasse le reste à payer (" + remaining + ")."
            );
        }

        // 5. Récupération de l'utilisateur connecté (caissier)
        User cashier = getAuthenticatedUser();

        // 6. Conversion de la méthode de paiement
        PaymentMethod method;
        try {
            method = PaymentMethod.fromString(request.getPaymentMethod());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Méthode de paiement invalide. Méthodes autorisées : " +
                    "ESPECES, CARTE_BANCAIRE, CHEQUE, VIREMENT, MOBILE_MONEY, CREDIT"
            );
        }

        // 7. Création du paiement
        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(method);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setReference(request.getReference());
        payment.setComment(request.getComment());
        payment.setSale(sale);
        payment.setUser(cashier);
        payment.markAsPaid(); // ✅ Payé immédiatement

        // 8. Sauvegarde
        Payment savedPayment = paymentRepository.save(payment);

        return toResponseDTO(savedPayment);
    }

    // ============================================================
    // ANNULATION D'UN PAIEMENT (Remboursement)
    // ============================================================

    /**
     * Annule un paiement et restaure le stock.
     * Un paiement ne peut être annulé que s'il est PAID.
     */
    public PaymentResponseDTO cancelPayment(Long paymentId, String reason) {

        // 1. Récupération du paiement
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException(
                        "Paiement introuvable avec l'ID : " + paymentId
                ));

        // 2. Vérification du statut
        if (!payment.isPaid()) {
            throw new IllegalArgumentException(
                    "Seuls les paiements payés peuvent être annulés."
            );
        }

        if (payment.isRefunded()) {
            throw new IllegalArgumentException(
                    "Ce paiement est déjà remboursé."
            );
        }

        // 3. Récupération de la vente et du caissier
        Sale sale = payment.getSale();
        User cashier = getAuthenticatedUser();

        // 4. ✅ RESTAURER LE STOCK POUR CHAQUE PRODUIT
        for (SaleItem item : sale.getItems()) {
            Product product = item.getProduct();

            // Restaurer le stock
            BigDecimal currentStock = product.getQuantity() != null ? product.getQuantity() : BigDecimal.ZERO;
            product.setQuantity(currentStock.add(item.getQuantity()));
            productRepository.save(product);

            // ✅ Créer un mouvement ENTREE
            StockMovement movement = new StockMovement();
            movement.setMovementType("ENTREE");
            movement.setQuantity(item.getQuantity());
            movement.setReason("Annulation du paiement #" + paymentId + " - " + product.getName());
            movement.setMovementDate(LocalDateTime.now());
            movement.setProduct(product);
            movement.setUser(cashier);
            stockMovementRepository.save(movement);
        }

        // 5. Marquer le paiement comme remboursé
        payment.refund();
        payment.setComment(
                (payment.getComment() != null ? payment.getComment() + " | " : "") +
                "Annulé : " + (reason != null ? reason : "Annulation manuelle")
        );
        sale.setStatus("CANCELLED");

        Sale cancelledSale =saleRepository.save(sale);
        Payment cancelledPayment = paymentRepository.save(payment);

        return toResponseDTO(cancelledPayment);
    }

    // ============================================================
    // LECTURE
    // ============================================================

    /**
     * Récupère tous les paiements
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> findAll() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un paiement par son ID
     */
    @Transactional(readOnly = true)
    public PaymentResponseDTO findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Paiement introuvable avec l'ID : " + id
                ));
        return toResponseDTO(payment);
    }

    /**
     * Récupère tous les paiements d'une vente
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> findBySaleId(Long saleId) {
        // Vérifier que la vente existe
        if (!saleRepository.existsById(saleId)) {
            throw new RuntimeException("Vente introuvable avec l'ID : " + saleId);
        }

        return paymentRepository.findBySaleId(saleId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les paiements d'un utilisateur (caissier)
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> findByUserId(Long userId) {
        // Vérifier que l'utilisateur existe
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Utilisateur introuvable avec l'ID : " + userId);
        }

        return paymentRepository.findByUserId(userId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les paiements entre deux dates
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> findByDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires.");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin.");
        }

        return paymentRepository.findByPaymentDateBetween(start, end)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ============================================================
    // UTILITAIRES PRIVÉS
    // ============================================================

    /**
     * Récupère l'utilisateur connecté (caissier)
     */
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Utilisateur non authentifié.");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(
                        "Utilisateur connecté introuvable : " + username
                ));
    }

    /**
     * Convertit Payment en PaymentResponseDTO
     */
    private PaymentResponseDTO toResponseDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();

        // Informations du paiement
        dto. setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod().name());
        dto.setPaymentMethodLabel(payment.getPaymentMethod().getLabel());
        dto.setStatus(payment.getStatus().name());
        dto.setStatusLabel(payment.getStatus().getLabel());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setReference(payment.getReference());
        dto.setComment(payment.getComment());

        // Informations sur la vente
        Sale sale = payment.getSale();
        if (sale != null) {
            dto.setSaleId(sale.getId());
            dto.setSaleTotal(sale.getTotalAmount());
            dto.setSaleStatus(sale.getStatus());

            // Calcul des montants cumulés
            BigDecimal totalPaid = paymentRepository.sumPaidPaymentsBySaleId(sale.getId());
            if (totalPaid == null) {
                totalPaid = BigDecimal.ZERO;
            }

            dto.setTotalPaid(totalPaid);
            dto.setRemainingAmount(sale.getTotalAmount().subtract(totalPaid));
            dto.setFullyPaid(dto.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0);
        }

        // Informations sur l'utilisateur (caissier)
        User user = payment.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setUsername(user.getUsername());

            String fullName = "";
            if (user.getFirstName() != null) {
                fullName += user.getFirstName() + " ";
            }
            if (user.getLastName() != null) {
                fullName += user.getLastName();
            }
            dto.setUserFullName(fullName.trim());
        }

        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());

        return dto;
    }
}
