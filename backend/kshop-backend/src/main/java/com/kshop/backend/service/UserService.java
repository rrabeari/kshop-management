/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.service;

import com.kshop.backend.dto.response.UserResponseDTO;
import com.kshop.backend.entity.Role;
import com.kshop.backend.entity.User;
import com.kshop.backend.repository.RoleRepository;
import com.kshop.backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Iris-PC
 */


/**
 * Service métier pour la gestion des utilisateurs.
 *
 * Architecture :
 *
 * Controller
 *      ↓
 * UserService
 *      ↓
 * UserRepository / RoleRepository
 *      ↓
 * PostgreSQL
 *
 * IMPORTANT :
 * Le password n'est jamais retourné par ce service.
 */
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Injection des dépendances.
     */
    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================================================
    // GET ALL
    // ============================================================

    /**
     * Récupère tous les utilisateurs.
     *
     * @return liste des utilisateurs sous forme de DTO
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    /**
     * Recherche un utilisateur par son ID.
     *
     * @param id identifiant de l'utilisateur
     * @return utilisateur sous forme de DTO
     */
    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Utilisateur introuvable avec l'id : " + id
                        )
                );

        return toResponseDTO(user);
    }

    // ============================================================
    // CREATE
    // ============================================================

    /**
     * Crée un nouvel utilisateur.
     *
     * Le password est encodé avant l'enregistrement.
     *
     * @param user utilisateur à créer
     * @param roleId identifiant du rôle
     * @return utilisateur créé sous forme de DTO
     */
    public UserResponseDTO create(
            User user,
            Long roleId) {

        // --------------------------------------------------------
        // Validation username
        // --------------------------------------------------------

        if (user.getUsername() == null
                || user.getUsername().isBlank()) {

            throw new IllegalArgumentException(
                    "Le nom d'utilisateur est obligatoire."
            );
        }

        // --------------------------------------------------------
        // Validation email
        // --------------------------------------------------------

        if (user.getEmail() == null
                || user.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "L'adresse email est obligatoire."
            );
        }

        // --------------------------------------------------------
        // Validation password
        // --------------------------------------------------------

        if (user.getPassword() == null
                || user.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "Le mot de passe est obligatoire."
            );
        }

        // --------------------------------------------------------
        // Vérification username
        // --------------------------------------------------------

        if (userRepository.existsByUsername(user.getUsername())) {

            throw new IllegalArgumentException(
                    "Le nom d'utilisateur existe déjà."
            );
        }

        // --------------------------------------------------------
        // Vérification email
        // --------------------------------------------------------

        if (userRepository.existsByEmail(user.getEmail())) {

            throw new IllegalArgumentException(
                    "L'adresse email existe déjà."
            );
        }

        // --------------------------------------------------------
        // Recherche du rôle
        // --------------------------------------------------------

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rôle introuvable avec l'id : " + roleId
                        )
                );

        // --------------------------------------------------------
        // Association du rôle
        // --------------------------------------------------------

        user.setRole(role);

        // --------------------------------------------------------
        // Activation par défaut
        // --------------------------------------------------------

        user.setEnabled(true);

        // --------------------------------------------------------
        // Encodage du password
        // --------------------------------------------------------

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        // --------------------------------------------------------
        // Sauvegarde
        // --------------------------------------------------------

        User savedUser = userRepository.save(user);

        return toResponseDTO(savedUser);
    }

    // ============================================================
    // UPDATE
    // ============================================================

    /**
     * Modifie un utilisateur.
     *
     * Le password n'est modifié que s'il est fourni.
     *
     * @param id identifiant de l'utilisateur
     * @param userData nouvelles données
     * @param roleId nouvel identifiant du rôle
     * @return utilisateur modifié
     */
    public UserResponseDTO update(
            Long id,
            User userData,
            Long roleId) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Utilisateur introuvable avec l'id : " + id
                        )
                );

        // --------------------------------------------------------
        // Username
        // --------------------------------------------------------

        if (userData.getUsername() != null
                && !userData.getUsername().isBlank()
                && !userData.getUsername()
                        .equals(user.getUsername())) {

            if (userRepository.existsByUsername(
                    userData.getUsername())) {

                throw new IllegalArgumentException(
                        "Le nom d'utilisateur existe déjà."
                );
            }

            user.setUsername(userData.getUsername());
        }

        // --------------------------------------------------------
        // Email
        // --------------------------------------------------------

        if (userData.getEmail() != null
                && !userData.getEmail().isBlank()
                && !userData.getEmail()
                        .equals(user.getEmail())) {

            if (userRepository.existsByEmail(
                    userData.getEmail())) {

                throw new IllegalArgumentException(
                        "L'adresse email existe déjà."
                );
            }

            user.setEmail(userData.getEmail());
        }

        // --------------------------------------------------------
        // Prénom
        // --------------------------------------------------------

        if (userData.getFirstName() != null) {
            user.setFirstName(userData.getFirstName());
        }

        // --------------------------------------------------------
        // Nom
        // --------------------------------------------------------

        if (userData.getLastName() != null) {
            user.setLastName(userData.getLastName());
        }

        // --------------------------------------------------------
        // Enabled
        // --------------------------------------------------------

        user.setEnabled(userData.isEnabled());

        // --------------------------------------------------------
        // Role
        // --------------------------------------------------------

        if (roleId != null) {

            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Rôle introuvable avec l'id : "
                                    + roleId
                            )
                    );

            user.setRole(role);
        }

        // --------------------------------------------------------
        // Password
        // --------------------------------------------------------

        if (userData.getPassword() != null
                && !userData.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(
                            userData.getPassword()
                    )
            );
        }

        // --------------------------------------------------------
        // Sauvegarde
        // --------------------------------------------------------

        User updatedUser = userRepository.save(user);

        return toResponseDTO(updatedUser);
    }

    // ============================================================
    // DELETE
    // ============================================================

    /**
     * Supprime un utilisateur.
     *
     * @param id identifiant de l'utilisateur
     */
    public void delete(Long id) {

        if (!userRepository.existsById(id)) {

            throw new RuntimeException(
                    "Utilisateur introuvable avec l'id : " + id
            );
        }

        userRepository.deleteById(id);
    }

    // ============================================================
    // MAPPER
    // ============================================================

    /**
     * Convertit User en UserResponseDTO.
     *
     * IMPORTANT :
     * Le password n'est jamais copié.
     */
    private UserResponseDTO toResponseDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEnabled(user.isEnabled());

        if (user.getRole() != null) {
            dto.setRole(user.getRole().getName());
        }

        return dto;
    }
}
