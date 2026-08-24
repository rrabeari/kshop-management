/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.dto.request;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Iris-PC
 */

@Setter
@Getter
public class ProductRequestDTO {
    private String code;
    private String barcode;
    private String name;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private BigDecimal quantity;
    private BigDecimal minimumStock;
    private String unit;
    private Boolean active;
    private Long categoryId; // Juste l'ID de la catégorie !
}
