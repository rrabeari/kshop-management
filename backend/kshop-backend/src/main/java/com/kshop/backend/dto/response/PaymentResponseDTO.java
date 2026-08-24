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
@Getter
@Setter
public class PaymentResponseDTO {
    private Long id;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentMethodLabel;
    private String status;
    private String statusLabel;
    private LocalDateTime paymentDate;
    private String reference;
    private String comment;

    private Long saleId;
    private BigDecimal saleTotal;
    private String saleStatus;

    private Long userId;
    private String username;
    private String userFullName;

    private BigDecimal totalPaid;
    private BigDecimal remainingAmount;
    private boolean fullyPaid;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    
}
