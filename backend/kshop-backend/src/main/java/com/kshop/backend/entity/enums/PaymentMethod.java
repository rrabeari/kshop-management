/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.kshop.backend.entity.enums;

/**
 *
 * @author Iris-PC
 */
public enum PaymentMethod {
    
    ESPECES("Espèces"),
    CARTE_BANCAIRE("Carte Bancaire"),
    CHEQUE("Chèque"),
    VIREMENT("Virement"),
    MOBILE_MONEY("Mobile Money"),
    CREDIT("Crédit");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static PaymentMethod fromString(String value) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.name().equalsIgnoreCase(value) || 
                method.label.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException(
            "Méthode de paiement invalide. Méthodes autorisées : " +
            "ESPECES, CARTE_BANCAIRE, CHEQUE, VIREMENT, MOBILE_MONEY, CREDIT"
        );
    }
}
