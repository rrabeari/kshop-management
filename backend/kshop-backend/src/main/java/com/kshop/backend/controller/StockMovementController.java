package com.kshop.backend.controller;

import com.kshop.backend.dto.request.StockMovementRequestDTO;
import com.kshop.backend.dto.response.StockMovementResponseDTO;
import com.kshop.backend.entity.StockMovement;
import com.kshop.backend.mapper.StockMovementMapper;
import com.kshop.backend.service.StockMovementService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST pour la gestion des mouvements de stock.
 *
 * Autorisations :
 *
 * ADMIN
 * MANAGER
 * STOCK
 *
 * peuvent consulter et créer des mouvements.
 *
 * Seul ADMIN peut supprimer un mouvement.
 */
@RestController
@RequestMapping("/api/stock-movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    /**
     * Injection du service.
     */
    public StockMovementController(
            StockMovementService stockMovementService) {

        this.stockMovementService = stockMovementService;
    }

    // ============================================================
    // GET ALL
    // ============================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STOCK')")
    @GetMapping
    public ResponseEntity<List<StockMovementResponseDTO>> findAll() {

        return ResponseEntity.ok(
                stockMovementService.findAll()
        );
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STOCK')")
    @GetMapping("/{id}")
    public ResponseEntity<StockMovementResponseDTO> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                stockMovementService.findById(id)
        );
    }

    // ============================================================
    // GET BY PRODUCT
    // ============================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STOCK')")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockMovementResponseDTO>> findByProduct(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                stockMovementService.findByProductId(productId)
        );
    }

    // ============================================================
    // GET BY USER
    // ============================================================

    @PreAuthorize("""
        hasAnyRole('ADMIN', 'MANAGER')
        or (hasRole('STOCK') and @currentUserSecurity.isCurrentUser(#userId))
    """)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StockMovementResponseDTO>> findByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                stockMovementService.findByUserId(userId)
        );
    }

    // ============================================================
    // GET BY TYPE
    // ============================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STOCK')")
    @GetMapping("/type/{movementType}")
    public ResponseEntity<List<StockMovementResponseDTO>>
            findByMovementType(
                    @PathVariable String movementType) {

        return ResponseEntity.ok(
                stockMovementService.findByMovementType(
                        movementType
                )
        );
    }

    // ============================================================
    // GET BY PRODUCT + TYPE
    // ============================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STOCK')")
    @GetMapping("/product/{productId}/type/{movementType}")
    public ResponseEntity<List<StockMovementResponseDTO>>
            findByProductAndType(
                    @PathVariable Long productId,
                    @PathVariable String movementType) {

        return ResponseEntity.ok(
                stockMovementService
                        .findByProductIdAndMovementType(
                                productId,
                                movementType
                        )
        );
    }

    // ============================================================
    // CREATE
    // ============================================================

    /**
    * Crée un mouvement de stock.
    *
    * IMPORTANT :
    *
    * Le frontend ne fournit pas userId.
    *
    * L'utilisateur est automatiquement récupéré
    * depuis le JWT.
    */
   @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STOCK')")
   @PostMapping
   public ResponseEntity<StockMovementResponseDTO> create(
           @RequestParam Long productId,
           @RequestBody StockMovementRequestDTO request) {

       StockMovementResponseDTO response =
               stockMovementService.create(
                       request,
                       productId
               );

       return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(response);
   }

    // ============================================================
    // DELETE
    // ============================================================

    /**
     * Seul ADMIN peut supprimer un mouvement.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        stockMovementService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}