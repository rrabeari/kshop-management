/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.kshop.backend.entity.enums;

/**
 *
 * @author Iris-PC
 */
public enum PaymentStatus {
    PENDING("En attente"),
    PAID("Payé"),
    REFUNDED("Remboursé");

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
