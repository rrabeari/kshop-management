/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.kshop.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author Iris-PC
 */
public record LoginRequest(@NotBlank
        String username,

        @NotBlank
        String password) {
    
}
