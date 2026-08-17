
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java
 * and open the template in the editor.
 */
package com.kshop.backend.security;

import com.kshop.backend.entity.User;
import com.kshop.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Iris-PC
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Utilisateur introuvable : "
                                + username
                        )
                );

        /*
         * Vérification du rôle.
         */
        if (user.getRole() == null
                || user.getRole().getName() == null
                || user.getRole().getName().isBlank()) {

            throw new UsernameNotFoundException(
                    "Aucun rôle configuré pour l'utilisateur : "
                    + username
            );
        }

        /*
         * Création du UserDetails Spring Security.
         *
         * .roles("ADMIN")
         *
         * produit automatiquement :
         *
         * ROLE_ADMIN
         *
         * ce qui permet d'utiliser :
         *
         * hasRole("ADMIN")
         */
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().getName())
                .disabled(!user.isEnabled())
                .build();
    }
}
