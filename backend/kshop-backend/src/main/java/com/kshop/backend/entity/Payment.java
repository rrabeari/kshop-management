/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
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
public class Payment extends BaseEntity{
    private String paymentMethod;

    private BigDecimal amount;

    private LocalDateTime paymentDate;

    @ManyToOne
    private Sale sale;
}
