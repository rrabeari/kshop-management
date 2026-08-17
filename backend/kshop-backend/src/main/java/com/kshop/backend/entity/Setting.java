/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Iris-PC
 */
@Entity
@Getter
@Setter
public class Setting extends BaseEntity{
    private String shopName;

    private String address;

    private String phone;

    private String email;

    private String currency;

    private String logo;
}
