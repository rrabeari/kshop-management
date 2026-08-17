/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.mapper;

import com.kshop.backend.dto.response.ProductResponseDTO;
import com.kshop.backend.dto.response.StockMovementResponseDTO;
import com.kshop.backend.dto.response.UserResponseDTO;
import com.kshop.backend.entity.Product;
import com.kshop.backend.entity.StockMovement;
import com.kshop.backend.entity.User;

/**
 *
 * @author Iris-PC
 */

/**
 * Mapper permettant de convertir StockMovement
 * vers StockMovementResponseDTO.
 *
 * IMPORTANT :
 * Cette classe permet de contrôler précisément
 * les données retournées par l'API.
 *
 * Elle empêche notamment de retourner directement
 * l'entité User avec son password.
 */
public class StockMovementMapper {
    /**
     * Constructeur privé.
     *
     * Cette classe contient uniquement des méthodes statiques.
     */
    private StockMovementMapper() {
    }

    /**
     * Convertit StockMovement en StockMovementResponseDTO.
     *
     * @param movement mouvement de stock
     * @return DTO du mouvement ou null
     */
    public static StockMovementResponseDTO toResponseDTO(StockMovement movement) {

        /*
         * Si aucun mouvement n'est fourni,
         * on retourne null.
         */
        if (movement == null) {
            return null;
        }

        /*
         * Conversion du produit.
         *
         * On réutilise le ProductMapper déjà créé.
         */
        ProductResponseDTO productDTO =
                ProductMapper.toResponseDTO(movement.getProduct());

        /*
         * Conversion de l'utilisateur.
         *
         * Cette méthode ne copie volontairement
         * jamais le password.
         */
        UserResponseDTO userDTO =
                toUserResponseDTO(movement.getUser());

        /*
         * Création du DTO final.
         */
        return new StockMovementResponseDTO(
                movement.getId(),
                movement.getMovementDate(),
                movement.getMovementType(),
                movement.getQuantity(),
                movement.getReason(),
                productDTO,
                userDTO
        );
    }

    /**
     * Convertit User vers UserResponseDTO.
     *
     * IMPORTANT :
     * Le champ password de User n'est jamais copié.
     *
     * Cette méthode est adaptée exactement à ton
     * User.java actuel.
     *
     * @param user entité User
     * @return UserResponseDTO ou null
     */
    private static UserResponseDTO toUserResponseDTO(User user) {

        /*
         * Aucun utilisateur associé.
         */
        if (user == null) {
            return null;
        }

        /*
         * Création du DTO utilisateur.
         */
        UserResponseDTO dto = new UserResponseDTO();

        /*
         * Informations publiques de l'utilisateur.
         */
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEnabled(user.isEnabled());

        /*
         * Récupération du rôle.
         *
         * User possède :
         *
         * private Role role;
         *
         * Nous retournons uniquement le nom du rôle.
         */
        if (user.getRole() != null) {
            dto.setRole(user.getRole().getName());
        }

        /*
         * IMPORTANT :
         *
         * On ne fait volontairement PAS :
         *
         * dto.setPassword(user.getPassword());
         *
         * Le password reste uniquement dans l'entité
         * et n'est jamais envoyé au frontend.
         */
        return dto;
    }
}
