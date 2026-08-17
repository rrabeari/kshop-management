package com.kshop.backend.controller;

import com.kshop.backend.dto.request.SaleRequestDTO;
import com.kshop.backend.dto.response.SaleResponseDTO;
import com.kshop.backend.service.SaleService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST pour la gestion des ventes.
 *
 * Routes :
 *
 * GET    /api/sales
 * GET    /api/sales/{id}
 * GET    /api/sales/user/{userId}
 * POST   /api/sales
 * PATCH  /api/sales/{id}/cancel
 *
 * IMPORTANT :
 *
 * Le Controller ne contient pas la logique métier.
 *
 * Il reçoit les requêtes HTTP et délègue le traitement
 * au SaleService.
 *
 * Sécurité :
 *
 * ADMIN
 * MANAGER
 * CAISSIER
 *
 * peuvent consulter et créer des ventes.
 */
@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    /**
     * Injection du service de gestion des ventes.
     *
     * @param saleService service métier des ventes
     */
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    // ============================================================
    // GET ALL
    // ============================================================

    /**
     * GET /api/sales
     *
     * Récupère toutes les ventes.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CAISSIER')")
    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> findAll() {

        return ResponseEntity.ok(
                saleService.findAll()
        );
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    /**
     * GET /api/sales/{id}
     *
     * Récupère une vente par son identifiant.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CAISSIER')")
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                saleService.findById(id)
        );
    }

    // ============================================================
    // GET BY USER
    // ============================================================

    /**
    * GET /api/sales/user/{userId}
    *
    * Récupère les ventes d'un utilisateur.
    *
    * ADMIN et MANAGER :
    * peuvent consulter les ventes de n'importe quel utilisateur.
    *
    * CAISSIER :
    * peut uniquement consulter ses propres ventes.
    */
   @PreAuthorize("""
       hasAnyRole('ADMIN', 'MANAGER')
       or (hasRole('CAISSIER') and @currentUserSecurity.isCurrentUser(#userId))
   """)
   @GetMapping("/user/{userId}")
   public ResponseEntity<List<SaleResponseDTO>> findByUserId(
           @PathVariable Long userId) {

       return ResponseEntity.ok(
               saleService.findByUserId(userId)
       );
   }
    // ============================================================
    // CREATE
    // ============================================================

    /**
     * POST /api/sales
     *
     * Crée une nouvelle vente.
     *
     * L'utilisateur connecté est récupéré automatiquement
     * par le SaleService grâce au JWT.
     *
     * Le frontend ne doit donc PAS envoyer userId.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CAISSIER')")
    @PostMapping
    public ResponseEntity<SaleResponseDTO> create(
            @RequestBody SaleRequestDTO request) {

        SaleResponseDTO createdSale =
                saleService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdSale);
    }

    // ============================================================
    // CANCEL
    // ============================================================

    /**
     * PATCH /api/sales/{id}/cancel
     *
     * Annule une vente.
     *
     * L'utilisateur connecté est identifié automatiquement
     * grâce au JWT.
     *
     * Le frontend ne fournit donc PAS userId.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CAISSIER')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SaleResponseDTO> cancelSale(
            @PathVariable Long id) {

        SaleResponseDTO cancelledSale =
                saleService.cancelSale(id);

        return ResponseEntity.ok(cancelledSale);
    }
}