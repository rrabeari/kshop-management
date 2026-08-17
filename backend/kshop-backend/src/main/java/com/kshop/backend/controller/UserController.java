/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.controller;

import com.kshop.backend.dto.response.UserResponseDTO;
import com.kshop.backend.entity.User;
import com.kshop.backend.service.UserService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
/**
 *
 * @author Iris-PC
 */

/**
 * Controller REST pour la gestion des utilisateurs.
 *
 * Routes :
 *
 * GET    /api/users
 * GET    /api/users/{id}
 * POST   /api/users
 * PUT    /api/users/{id}
 * DELETE /api/users/{id}
 *
 * IMPORTANT :
 * Les réponses utilisent UserResponseDTO.
 *
 * Le password n'est donc jamais retourné.
 */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;

    /**
     * Injection du service.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ============================================================
    // GET ALL
    // ============================================================

    /**
     * GET /api/users
     *
     * Récupère tous les utilisateurs.
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {

        return ResponseEntity.ok(
                userService.findAll()
        );
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    /**
     * GET /api/users/{id}
     *
     * Récupère un utilisateur.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userService.findById(id)
        );
    }

    // ============================================================
    // CREATE
    // ============================================================

    /**
     * POST /api/users?roleId=1
     *
     * Crée un utilisateur.
     *
     * Le password est accepté dans la requête
     * mais n'est jamais retourné dans la réponse.
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(
            @RequestParam Long roleId,
            @RequestBody User user) {

        UserResponseDTO createdUser =
                userService.create(
                        user,
                        roleId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    // ============================================================
    // UPDATE
    // ============================================================

    /**
     * PUT /api/users/{id}?roleId=1
     *
     * Modifie un utilisateur.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @RequestParam(required = false) Long roleId,
            @RequestBody User user) {

        UserResponseDTO updatedUser =
                userService.update(
                        id,
                        user,
                        roleId
                );

        return ResponseEntity.ok(
                updatedUser
        );
    }

    // ============================================================
    // DELETE
    // ============================================================

    /**
     * DELETE /api/users/{id}
     *
     * Supprime un utilisateur.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        userService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
