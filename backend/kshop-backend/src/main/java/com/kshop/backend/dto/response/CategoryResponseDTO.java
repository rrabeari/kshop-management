/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Iris-PC
 */

/**
 * DTO utilisé pour retourner les informations publiques d'un utilisateur.
 *
 * IMPORTANT :
 * Ce DTO ne contient volontairement PAS le champ "password".
 *
 * L'objectif est d'éviter de retourner le mot de passe,
 * même lorsqu'un User est inclus dans la réponse d'une autre API
 * comme StockMovement.
 */
@Getter
@Setter
public class CategoryResponseDTO {
    /**
     * Identifiant unique de la catégorie.
     */
    private Long id;

    /**
     * Nom de la catégorie.
     *
     * Exemple :
     * Boissons
     * Alimentaire
     * Hygiène
     */
    private String name;

    /**
     * Description de la catégorie.
     */
    private String description;

    /**
     * Constructeur vide nécessaire pour Jackson.
     */
    public CategoryResponseDTO() {
    }

    /**
     * Constructeur pratique pour créer rapidement le DTO.
     *
     * @param id identifiant de la catégorie
     * @param name nom de la catégorie
     * @param description description de la catégorie
     */
    public CategoryResponseDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
