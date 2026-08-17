package com.kshop.backend.service;

import com.kshop.backend.dto.request.StockMovementRequestDTO;
import com.kshop.backend.dto.response.StockMovementResponseDTO;
import com.kshop.backend.entity.Product;
import com.kshop.backend.entity.StockMovement;
import com.kshop.backend.entity.User;
import com.kshop.backend.mapper.StockMovementMapper;
import com.kshop.backend.repository.ProductRepository;
import com.kshop.backend.repository.StockMovementRepository;
import com.kshop.backend.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service métier pour la gestion des mouvements de stock.
 *
 * IMPORTANT :
 *
 * L'utilisateur qui effectue un mouvement est récupéré
 * automatiquement depuis le JWT.
 *
 * Le frontend ne doit donc jamais envoyer userId
 * pour créer un mouvement.
 */
@Service
@Transactional
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Injection des repositories.
     */
    public StockMovementService(
            StockMovementRepository stockMovementRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // ============================================================
    // LECTURE
    // ============================================================

    /**
     * Récupère tous les mouvements.
     *
     * @return liste des mouvements sous forme de DTO
     */
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> findAll() {

        return stockMovementRepository.findAll()
                .stream()
                .map(StockMovementMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recherche un mouvement par son ID.
     *
     * @param id identifiant du mouvement
     * @return mouvement sous forme de DTO
     */
    @Transactional(readOnly = true)
    public StockMovementResponseDTO findById(Long id) {

        StockMovement movement =
                stockMovementRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Mouvement de stock introuvable "
                                        + "avec l'id : " + id
                                )
                        );

        return StockMovementMapper.toResponseDTO(movement);
    }

    
    
    
    
    
    
    /**
     * Récupère les mouvements d'un produit.
     *
     * @param productId identifiant du produit
     * @return liste des mouvements
     */
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> findByProductId(
            Long productId) {

        if (!productRepository.existsById(productId)) {

            throw new RuntimeException(
                    "Produit introuvable avec l'id : " + productId
            );
        }

        return stockMovementRepository
                .findByProductId(productId)
                .stream()
                .map(StockMovementMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
    * Récupère les mouvements effectués par un utilisateur.
    *
    * Règles :
    *
    * ADMIN / MANAGER :
    *     peuvent consulter n'importe quel utilisateur.
    *
    * STOCK :
    *     peut uniquement consulter ses propres mouvements.
    *
    * @param userId identifiant de l'utilisateur
    * @return liste des mouvements
    */
   @Transactional(readOnly = true)
   public List<StockMovementResponseDTO> findByUserId(
           Long userId) {

       User authenticatedUser = getAuthenticatedUser();

       String role = authenticatedUser.getRole() != null
               ? authenticatedUser.getRole().getName()
               : null;

       /*
        * ADMIN et MANAGER peuvent consulter
        * les mouvements de n'importe quel utilisateur.
        */
       if ("ADMIN".equalsIgnoreCase(role)
               || "MANAGER".equalsIgnoreCase(role)) {

           if (!userRepository.existsById(userId)) {
               throw new RuntimeException(
                       "Utilisateur introuvable avec l'id : " + userId
               );
           }
       }

       /*
        * STOCK :
        * uniquement ses propres mouvements.
        */
       else if ("STOCK".equalsIgnoreCase(role)) {

           if (!authenticatedUser.getId().equals(userId)) {
               throw new org.springframework.security.access.AccessDeniedException(
                       "Vous ne pouvez consulter que vos propres mouvements."
               );
           }
       }

       else {

           throw new org.springframework.security.access.AccessDeniedException(
                   "Vous n'êtes pas autorisé à consulter ces mouvements."
           );
       }

       return stockMovementRepository
               .findByUserId(userId)
               .stream()
               .map(StockMovementMapper::toResponseDTO)
               .toList();
   }

    /**
     * Recherche par type.
     *
     * @param movementType type du mouvement
     * @return liste des mouvements
     */
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> findByMovementType(
            String movementType) {

        return stockMovementRepository
                .findByMovementType(movementType)
                .stream()
                .map(StockMovementMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recherche les mouvements d'un produit selon leur type.
     *
     * @param productId identifiant du produit
     * @param movementType type du mouvement
     * @return liste des mouvements
     */
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO>
            findByProductIdAndMovementType(
                    Long productId,
                    String movementType) {

        if (!productRepository.existsById(productId)) {

            throw new RuntimeException(
                    "Produit introuvable avec l'id : " + productId
            );
        }

        return stockMovementRepository
                .findByProductIdAndMovementType(
                        productId,
                        movementType
                )
                .stream()
                .map(StockMovementMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ============================================================
    // CRÉATION
    // ============================================================

    /**
     * Crée un mouvement de stock.
     *
     * IMPORTANT :
     *
     * userId n'est plus reçu en paramètre.
     *
     * L'utilisateur est récupéré automatiquement
     * depuis le SecurityContext de Spring Security.
     *
     * @param movement mouvement demandé
     * @param productId identifiant du produit
     * @return mouvement créé
     */
    public StockMovementResponseDTO create(
            StockMovementRequestDTO request,
            Long productId) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Les données du mouvement sont obligatoires."
            );
        }

        if (request.getQuantity() == null
                || request.getQuantity()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "La quantité doit être supérieure à zéro."
            );
        }

        if (request.getMovementType() == null
                || request.getMovementType().isBlank()) {

            throw new IllegalArgumentException(
                    "Le type de mouvement est obligatoire."
            );
        }

        String movementType =
                request.getMovementType()
                        .trim()
                        .toUpperCase();

        if (!movementType.equals("ENTREE")
                && !movementType.equals("SORTIE")
                && !movementType.equals("AJUSTEMENT")) {

            throw new IllegalArgumentException(
                    "Type de mouvement invalide. "
                    + "Types autorisés : ENTREE, SORTIE, AJUSTEMENT."
            );
        }

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Produit introuvable avec l'id : "
                                        + productId
                                )
                        );

        User user = getAuthenticatedUser();

        BigDecimal currentStock = product.getQuantity();

        if (currentStock == null) {
            currentStock = BigDecimal.ZERO;
        }

        BigDecimal movementQuantity =
                request.getQuantity();

        // Traitement du stock
        switch (movementType) {

            case "ENTREE":

                product.setQuantity(
                        currentStock.add(movementQuantity)
                );

                break;

            case "SORTIE":

                if (movementQuantity.compareTo(currentStock) > 0) {

                    throw new IllegalArgumentException(
                            "Stock insuffisant. "
                            + "Stock disponible : "
                            + currentStock
                            + ", quantité demandée : "
                            + movementQuantity
                    );
                }

                product.setQuantity(
                        currentStock.subtract(movementQuantity)
                );

                break;

            case "AJUSTEMENT":

                product.setQuantity(
                        movementQuantity
                );

                break;

            default:

                throw new IllegalArgumentException(
                        "Type de mouvement non supporté."
                );
        }

        // Création de l'entité JPA
        StockMovement movement = new StockMovement();

        movement.setMovementType(movementType);
        movement.setQuantity(movementQuantity);
        movement.setReason(request.getReason());
        movement.setProduct(product);
        movement.setUser(user);
        movement.setMovementDate(LocalDateTime.now());

        productRepository.save(product);

        StockMovement savedMovement =
                stockMovementRepository.save(movement);

        return StockMovementMapper.toResponseDTO(
                savedMovement
        );
    }

    // ============================================================
    // UTILISATEUR CONNECTÉ
    // ============================================================

    /**
     * Récupère l'utilisateur actuellement connecté
     * à partir du JWT.
     *
     * Spring Security place l'utilisateur authentifié
     * dans le SecurityContext.
     *
     * @return utilisateur connecté
     */
    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalArgumentException(
                    "Utilisateur non authentifié."
            );
        }

        String username =
                authentication.getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Utilisateur connecté introuvable : "
                                + username
                        )
                );
    }

    // ============================================================
    // SUPPRESSION
    // ============================================================

    /**
     * Supprime un mouvement.
     *
     * La protection ADMIN est réalisée dans le Controller
     * avec @PreAuthorize.
     *
     * @param id identifiant du mouvement
     */
    public void delete(Long id) {

        if (!stockMovementRepository.existsById(id)) {

            throw new RuntimeException(
                    "Mouvement de stock introuvable avec l'id : "
                    + id
            );
        }

        stockMovementRepository.deleteById(id);
    }
}