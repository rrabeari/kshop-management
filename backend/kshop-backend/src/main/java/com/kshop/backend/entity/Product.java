/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Iris-PC
 */
@Entity
@Getter
@Setter
public class Product extends BaseEntity{
     private String code;

    private String barcode;

    private String name;

    private String description;

    private BigDecimal purchasePrice;

    private BigDecimal sellingPrice;

    private BigDecimal quantity;

    private BigDecimal minimumStock;

    private String unit;

    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
