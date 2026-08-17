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

DTO utilisé pour retourner les informations publiques d'un utilisateur.



IMPORTANT :

Ce DTO ne contient volontairement PAS le champ "password".



L'objectif est d'éviter de retourner le mot de passe,

même lorsqu'un User est inclus dans la réponse d'une autre API

comme StockMovement.*/
@Getter
@Setter
public class UserResponseDTO {
    
    /**
     * Identifiant unique de l'utilisateur.
     */
    private Long id;

    /**
     * Nom d'utilisateur utilisé pour la connexion.
     */
    private String username;

    /**
     * Prénom de l'utilisateur.
     */
    private String firstName;

    /**
     * Nom de famille de l'utilisateur.
     */
    private String lastName;

    /**
     * Adresse email de l'utilisateur.
     */
    private String email;

    /**
     * Indique si le compte est autorisé à se connecter.
     */
    private Boolean enabled;

    /**
     * Nom du rôle de l'utilisateur.
     *
     * Exemples :
     * ADMIN
     * MANAGER
     * CAISSIER
     * STOCK
     */
    private String role;
}
