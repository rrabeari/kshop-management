/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.service;

import com.kshop.backend.dto.response.PaymentStatisticsDTO;
import com.kshop.backend.entity.enums.PaymentMethod;
import com.kshop.backend.entity.enums.PaymentStatus;
import com.kshop.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Iris-PC
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentStatisticsService {
    private final PaymentRepository paymentRepository;

    /**
     * Récupère les statistiques globales des paiements
     */
    public PaymentStatisticsDTO getStatistics() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime startOfWeek = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN);
        LocalDateTime startOfMonth = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime startOfYear = LocalDateTime.of(LocalDate.now().withDayOfYear(1), LocalTime.MIN);

        // ============================================================
        // 1. TOTAUX PAR PÉRIODE
        // ============================================================

        BigDecimal totalAll = paymentRepository.sumPaidPaymentsBetweenDates(
                LocalDateTime.of(1900, 1, 1, 0, 0),
                now
        );

        BigDecimal totalToday = paymentRepository.sumPaidPaymentsBetweenDates(
                startOfDay,
                now
        );

        BigDecimal totalWeek = paymentRepository.sumPaidPaymentsBetweenDates(
                startOfWeek,
                now
        );

        BigDecimal totalMonth = paymentRepository.sumPaidPaymentsBetweenDates(
                startOfMonth,
                now
        );

        BigDecimal totalYear = paymentRepository.sumPaidPaymentsBetweenDates(
                startOfYear,
                now
        );

        // ============================================================
        // 2. NOMBRE DE PAIEMENTS
        // ============================================================

        Long totalPayments = paymentRepository.count();

        Long totalTodayPayments = (long) paymentRepository
                .findByPaymentDateBetween(startOfDay, endOfDay)
                .size();

        Long totalPending = (long) paymentRepository
                .findByStatus(PaymentStatus.PENDING)
                .size();

        Long totalPaid = (long) paymentRepository
                .findByStatus(PaymentStatus.PAID)
                .size();

        Long totalRefunded = (long) paymentRepository
                .findByStatus(PaymentStatus.REFUNDED)
                .size();

        // ============================================================
        // 3. STATISTIQUES PAR MÉTHODE
        // ============================================================

        Map<String, BigDecimal> amountByMethod = new HashMap<>();
        Map<String, Long> countByMethod = new HashMap<>();

        paymentRepository.sumPaymentsByMethod().forEach(result -> {
            PaymentMethod method = (PaymentMethod) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            amountByMethod.put(method.name(), amount);
        });

        paymentRepository.countPaymentsByMethod().forEach(result -> {
            PaymentMethod method = (PaymentMethod) result[0];
            Long count = (Long) result[1];
            countByMethod.put(method.name(), count);
        });

        // ============================================================
        // 4. CONSTRUCTION DU DTO
        // ============================================================

        return PaymentStatisticsDTO.builder()
                .totalAmount(totalAll != null ? totalAll : BigDecimal.ZERO)
                .totalToday(totalToday != null ? totalToday : BigDecimal.ZERO)
                .totalThisWeek(totalWeek != null ? totalWeek : BigDecimal.ZERO)
                .totalThisMonth(totalMonth != null ? totalMonth : BigDecimal.ZERO)
                .totalThisYear(totalYear != null ? totalYear : BigDecimal.ZERO)
                .totalPayments(totalPayments)
                .totalTodayPayments(totalTodayPayments)
                .totalPendingPayments(totalPending)
                .totalPaidPayments(totalPaid)
                .totalRefundedPayments(totalRefunded)
                .amountByMethod(amountByMethod)
                .countByMethod(countByMethod)
                .generatedAt(LocalDateTime.now())
                .build();
    }
}
