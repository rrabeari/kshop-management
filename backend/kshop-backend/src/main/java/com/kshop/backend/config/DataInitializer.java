/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.config;

import com.kshop.backend.entity.Role;
import com.kshop.backend.entity.User;
import com.kshop.backend.repository.RoleRepository;
import com.kshop.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Iris-PC
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // 1. Création des rôles par défaut
            createRoleIfNotExists(roleRepository, "ADMIN");
            createRoleIfNotExists(roleRepository, "MANAGER");
            createRoleIfNotExists(roleRepository, "CAISSIER");
            createRoleIfNotExists(roleRepository, "STOCK");

            // 2. Création d'un utilisateur ADMIN par défaut si la table est vide
            if (userRepository.count() == 0) {
                Role adminRole = roleRepository.findByName("ADMIN")
                        .orElseThrow(() -> new RuntimeException("Rôle ADMIN introuvable"));

                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@kshop.com");
                admin.setFirstName("Super");
                admin.setLastName("Admin");
                admin.setEnabled(true);
                admin.setRole(adminRole);
                
                // Le mot de passe "admin123" est haché proprement avec BCrypt
                admin.setPassword(passwordEncoder.encode("admin123"));

                userRepository.save(admin);
                System.out.println(">>> Utilisateur admin par défaut créé avec succès (username: admin / password: admin123)");
            }
        };
    }

    private void createRoleIfNotExists(
            RoleRepository roleRepository,
            String roleName
    ) {

        if (!roleRepository.existsByName(roleName)) {

            Role role = new Role();
            role.setName(roleName);

            roleRepository.save(role);

            System.out.println(
                    "Rôle créé : " + roleName
            );
        }
    }
}