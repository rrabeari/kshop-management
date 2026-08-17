/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.security;

import com.kshop.backend.entity.User;
import com.kshop.backend.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 *
 * @author Iris-PC
 */

/**
 * Composant utilisé par @PreAuthorize
 * pour vérifier l'identité de l'utilisateur connecté.
 *
 * Exemple :
 *
 * @currentUserSecurity.isCurrentUser(#userId)
 */
@Component("currentUserSecurity")
public class CurrentUserSecurity {
    private final UserRepository userRepository;

    public CurrentUserSecurity(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Vérifie si l'utilisateur connecté
     * correspond à l'identifiant fourni.
     *
     * @param userId identifiant à vérifier
     * @return true si c'est l'utilisateur connecté
     */
    public boolean isCurrentUser(Long userId) {

        if (userId == null) {
            return false;
        }

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return false;
        }

        String username = authentication.getName();

        User user = userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) {
            return false;
        }

        return user.getId().equals(userId);
    }
}
