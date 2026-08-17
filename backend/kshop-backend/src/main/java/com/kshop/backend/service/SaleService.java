/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.service;

import com.kshop.backend.dto.request.SaleItemRequestDTO;
import com.kshop.backend.dto.request.SaleRequestDTO;
import com.kshop.backend.dto.request.StockMovementRequestDTO;
import com.kshop.backend.dto.response.SaleItemResponseDTO;
import com.kshop.backend.dto.response.SaleResponseDTO;
import com.kshop.backend.entity.Product;
import com.kshop.backend.entity.Sale;
import com.kshop.backend.entity.SaleItem;
import com.kshop.backend.entity.StockMovement;
import com.kshop.backend.entity.User;
import com.kshop.backend.repository.ProductRepository;
import com.kshop.backend.repository.SaleRepository;
import com.kshop.backend.repository.StockMovementRepository;
import com.kshop.backend.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Iris-PC
 */


/**
 * Service métier pour la gestion des ventes.
 *
 * Architecture :
 *
 * Controller
 *      ↓
 * SaleService
 *      ↓
 * ┌─────────────────────────────┐
 * │ SaleRepository              │
 * │ ProductRepository           │
 * │ UserRepository              │
 * │ StockMovementService        │
 * └─────────────────────────────┘
 *      ↓
 * PostgreSQL
 *
 * Lorsqu'une vente est créée :
 *
 * 1. L'utilisateur connecté est identifié.
 * 2. Les produits sont recherchés.
 * 3. Le stock disponible est vérifié.
 * 4. Le prix de vente du produit est récupéré.
 * 5. Le sous-total de chaque ligne est calculé.
 * 6. Le montant total est calculé.
 * 7. La vente est enregistrée.
 * 8. Le stock est diminué.
 * 9. Un mouvement SORTIE est créé.
 *
 * IMPORTANT :
 *
 * Toute la création de la vente est transactionnelle.
 *
 * Si une opération échoue, la transaction est annulée.
 *
 * Cela évite d'avoir par exemple :
 *
 * - une vente enregistrée
 * - mais un stock non diminué
 *
 * ou :
 *
 * - un stock diminué
 * - mais une vente non enregistrée.
 */
@Service
@Transactional
public class SaleService {
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StockMovementService stockMovementService;
    private final StockMovementRepository stockMovementRepository;

    /**
     * Injection des dépendances nécessaires
     * à la gestion des ventes.
     */
    public SaleService(
            SaleRepository saleRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            StockMovementService stockMovementService,
            StockMovementRepository stockMovementRepository) {

        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.stockMovementService = stockMovementService;
        this.stockMovementRepository=stockMovementRepository;
    }

    // ============================================================
    // LECTURE
    // ============================================================

    /**
     * Récupère toutes les ventes.
     *
     * Les entités JPA sont converties en DTO
     * avant d'être retournées au Controller.
     *
     * @return liste des ventes
     */
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> findAll() {

        return saleRepository
                .findAllByOrderBySaleDateDesc()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Recherche une vente par son identifiant.
     *
     * @param id identifiant de la vente
     * @return vente sous forme de DTO
     */
    @Transactional(readOnly = true)
    public SaleResponseDTO findById(Long id) {

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vente introuvable avec l'id : " + id
                        )
                );

        return toResponseDTO(sale);
    }

    /**
    * Récupère les ventes d'un utilisateur.
    *
    * Règles de sécurité :
    *
    * ADMIN / MANAGER :
    *     peuvent consulter les ventes de n'importe quel utilisateur.
    *
    * CAISSIER :
    *     peut uniquement consulter ses propres ventes.
    *
    * @param userId identifiant de l'utilisateur recherché
    * @return liste des ventes
    */
   @Transactional(readOnly = true)
   public List<SaleResponseDTO> findByUserId(Long userId) {

       User authenticatedUser = getAuthenticatedUser();

       String role = authenticatedUser.getRole() != null
               ? authenticatedUser.getRole().getName()
               : null;

       /*
        * ADMIN et MANAGER peuvent consulter
        * les ventes de n'importe quel utilisateur.
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
        * CAISSIER :
        * il ne peut consulter que ses propres ventes.
        */
       else if ("CAISSIER".equalsIgnoreCase(role)) {

           if (!authenticatedUser.getId().equals(userId)) {
               throw new org.springframework.security.access.AccessDeniedException(
                       "Vous ne pouvez consulter que vos propres ventes."
               );
           }
       }

       /*
        * Sécurité supplémentaire :
        * un rôle non prévu ne doit pas pouvoir utiliser
        * cet endpoint.
        */
       else {

           throw new org.springframework.security.access.AccessDeniedException(
                   "Vous n'êtes pas autorisé à consulter ces ventes."
           );
       }

       return saleRepository
               .findByUserIdOrderBySaleDateDesc(userId)
               .stream()
               .map(this::toResponseDTO)
               .toList();
   }

    // ============================================================
    // CRÉATION
    // ============================================================

    /**
     * Crée une nouvelle vente.
     *
     * Le prix n'est jamais fourni par le frontend.
     *
     * Le backend récupère automatiquement :
     *
     * Product.sellingPrice
     *
     * pour calculer le montant de la vente.
     *
     * @param request données de la vente
     * @return vente créée
     */
    public SaleResponseDTO create(SaleRequestDTO request) {

        // ========================================================
        // 1. VALIDATION DE LA REQUÊTE
        // ========================================================

        if (request == null) {

            throw new IllegalArgumentException(
                    "Les données de la vente sont obligatoires."
            );
        }

        if (request.getItems() == null
                || request.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "Une vente doit contenir au moins un produit."
            );
        }

        // ========================================================
        // 2. RÉCUPÉRATION DE L'UTILISATEUR CONNECTÉ
        // ========================================================

        User user = getAuthenticatedUser();

        // ========================================================
        // 3. INITIALISATION DE LA VENTE
        // ========================================================

        Sale sale = new Sale();

        sale.setSaleDate(LocalDateTime.now());
        sale.setUser(user);
        sale.setStatus("COMPLETED");

        // ========================================================
        // 4. GESTION DE LA REMISE
        // ========================================================

        BigDecimal discount = request.getDiscount();

        if (discount == null) {
            discount = BigDecimal.ZERO;
        }

        if (discount.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "La remise ne peut pas être négative."
            );
        }

        // ========================================================
        // 5. INITIALISATION DU TOTAL
        // ========================================================

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<SaleItem> saleItems = new ArrayList<>();

        // ========================================================
        // 6. TRAITEMENT DE CHAQUE PRODUIT
        // ========================================================

        for (SaleItemRequestDTO itemRequest : request.getItems()) {

            // ----------------------------------------------------
            // Validation de la ligne
            // ----------------------------------------------------

            if (itemRequest == null) {

                throw new IllegalArgumentException(
                        "Une ligne de vente est invalide."
                );
            }

            if (itemRequest.getProductId() == null) {

                throw new IllegalArgumentException(
                        "L'identifiant du produit est obligatoire."
                );
            }

            if (itemRequest.getQuantity() == null
                    || itemRequest.getQuantity()
                            .compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        "La quantité doit être supérieure à zéro."
                );
            }

            // ----------------------------------------------------
            // Recherche du produit
            // ----------------------------------------------------

            Product product =
                    productRepository.findById(
                            itemRequest.getProductId()
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Produit introuvable avec l'id : "
                                    + itemRequest.getProductId()
                            )
                    );

            // ----------------------------------------------------
            // Vérification du produit actif
            // ----------------------------------------------------

            if (Boolean.FALSE.equals(product.getActive())) {

                throw new IllegalArgumentException(
                        "Le produit '"
                                + product.getName()
                                + "' est désactivé."
                );
            }

            // ----------------------------------------------------
            // Vérification du prix
            // ----------------------------------------------------

            if (product.getSellingPrice() == null
                    || product.getSellingPrice()
                            .compareTo(BigDecimal.ZERO) < 0) {

                throw new IllegalArgumentException(
                        "Le prix de vente du produit '"
                                + product.getName()
                                + "' est invalide."
                );
            }

            // ----------------------------------------------------
            // Vérification du stock
            // ----------------------------------------------------

            BigDecimal currentStock =
                    product.getQuantity();

            if (currentStock == null) {
                currentStock = BigDecimal.ZERO;
            }

            if (itemRequest.getQuantity()
                    .compareTo(currentStock) > 0) {

                throw new IllegalArgumentException(
                        "Stock insuffisant pour le produit '"
                                + product.getName()
                                + "'. Stock disponible : "
                                + currentStock
                                + ", quantité demandée : "
                                + itemRequest.getQuantity()
                );
            }

            // ----------------------------------------------------
            // Récupération du prix de vente
            // ----------------------------------------------------

            BigDecimal unitPrice =
                    product.getSellingPrice();

            // ----------------------------------------------------
            // Calcul du sous-total
            // ----------------------------------------------------

            BigDecimal subtotal =
                    unitPrice.multiply(
                            itemRequest.getQuantity()
                    );

            // ----------------------------------------------------
            // Création de la ligne de vente
            // ----------------------------------------------------

            SaleItem saleItem = new SaleItem();

            saleItem.setProduct(product);
            saleItem.setQuantity(
                    itemRequest.getQuantity()
            );
            saleItem.setUnitPrice(unitPrice);
            saleItem.setSubtotal(subtotal);
            saleItem.setSale(sale);

            saleItems.add(saleItem);

            // ----------------------------------------------------
            // Ajout au total
            // ----------------------------------------------------

            totalAmount =
                    totalAmount.add(subtotal);
        }

        // ========================================================
        // 7. APPLICATION DE LA REMISE
        // ========================================================

        if (discount.compareTo(totalAmount) > 0) {

            throw new IllegalArgumentException(
                    "La remise ne peut pas être supérieure "
                    + "au montant total de la vente."
            );
        }

        totalAmount =
                totalAmount.subtract(discount);

        // ========================================================
        // 8. ASSOCIATION DES LIGNES À LA VENTE
        // ========================================================

        sale.setItems(saleItems);
        sale.setDiscount(discount);
        sale.setTotalAmount(totalAmount);

        // ========================================================
        // 9. SAUVEGARDE DE LA VENTE
        // ========================================================

        Sale savedSale =
                saleRepository.save(sale);

        // ========================================================
        // 10. CRÉATION DES MOUVEMENTS DE STOCK
        // ========================================================

        /*
         * Chaque ligne de vente provoque une sortie
         * de stock.
         *
         * On réutilise ici StockMovementService.create()
         * afin de conserver une seule logique de gestion
         * du stock dans l'application.
         */
        for (SaleItem saleItem : saleItems) {

            // ========================================================
            // CRÉATION DU MOUVEMENT DE STOCK
            // ========================================================

            StockMovementRequestDTO movementRequest =
                    new StockMovementRequestDTO();

            movementRequest.setMovementType("SORTIE");

            movementRequest.setQuantity(
                    saleItem.getQuantity()
            );

            movementRequest.setReason(
                    "Vente #" + savedSale.getId()
            );

            // L'utilisateur est récupéré automatiquement
            // depuis le JWT par StockMovementService.
            stockMovementService.create(
                    movementRequest,
                    saleItem.getProduct().getId()
            );
        }

        // ========================================================
        // 11. RETOUR DE LA VENTE
        // ========================================================

        return toResponseDTO(savedSale);
    }

    // ============================================================
    // UTILISATEUR CONNECTÉ
    // ============================================================

    /**
     * Récupère l'utilisateur actuellement authentifié
     * grâce au SecurityContext de Spring Security.
     *
     * Le frontend n'a donc pas besoin d'envoyer userId
     * dans la requête de création de vente.
     *
     * Cela évite qu'un utilisateur puisse créer une vente
     * au nom d'un autre utilisateur.
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
    // MAPPING RESPONSE
    // ============================================================

    /**
     * Convertit une entité Sale en SaleResponseDTO.
     *
     * Cette méthode permet de ne jamais retourner
     * directement l'entité JPA au frontend.
     *
     * @param sale vente à convertir
     * @return DTO sécurisé
     */
    private SaleResponseDTO toResponseDTO(Sale sale) {

        SaleResponseDTO dto =
                new SaleResponseDTO();

        // --------------------------------------------------------
        // Informations générales
        // --------------------------------------------------------

        dto.setId(sale.getId());
        dto.setSaleDate(sale.getSaleDate());
        dto.setTotalAmount(sale.getTotalAmount());
        dto.setDiscount(sale.getDiscount());
        dto.setStatus(sale.getStatus());

        // --------------------------------------------------------
        // Informations utilisateur
        // --------------------------------------------------------

        if (sale.getUser() != null) {

            dto.setUserId(
                    sale.getUser().getId()
            );

            dto.setUsername(
                    sale.getUser().getUsername()
            );

            dto.setFirstName(
                    sale.getUser().getFirstName()
            );

            dto.setLastName(
                    sale.getUser().getLastName()
            );

            if (sale.getUser().getRole() != null) {

                dto.setRole(
                        sale.getUser()
                                .getRole()
                                .getName()
                );
            }
        }

        // --------------------------------------------------------
        // Lignes de vente
        // --------------------------------------------------------

        List<SaleItemResponseDTO> itemDTOs =
                new ArrayList<>();

        if (sale.getItems() != null) {

            for (SaleItem item : sale.getItems()) {

                SaleItemResponseDTO itemDTO =
                        new SaleItemResponseDTO();

                itemDTO.setId(item.getId());

                itemDTO.setQuantity(
                        item.getQuantity()
                );

                itemDTO.setUnitPrice(
                        item.getUnitPrice()
                );

                itemDTO.setSubtotal(
                        item.getSubtotal()
                );

                if (item.getProduct() != null) {

                    itemDTO.setProductId(
                            item.getProduct().getId()
                    );

                    itemDTO.setProductCode(
                            item.getProduct().getCode()
                    );

                    itemDTO.setProductName(
                            item.getProduct().getName()
                    );
                }

                itemDTOs.add(itemDTO);
            }
        }

        dto.setItems(itemDTOs);

        return dto;
    }
    
    /**
    * Annule une vente et restaure automatiquement
    * les quantités des produits concernés.
    *
    * Toute erreur pendant l'opération provoque
    * un rollback complet de la transaction.
    *
    * @param saleId identifiant de la vente
    * @return vente annulée
    */
   @Transactional(rollbackFor = Exception.class)
   public SaleResponseDTO cancelSale(Long saleId) {

       // ============================================================
       // 1. RECHERCHE DE LA VENTE
       // ============================================================

       Sale sale = saleRepository.findById(saleId)
               .orElseThrow(() ->
                       new RuntimeException(
                               "Vente introuvable avec l'id : " + saleId
                       )
               );

       // ============================================================
       // 2. VERIFICATION DU STATUT
       // ============================================================

       if (!"COMPLETED".equalsIgnoreCase(sale.getStatus())) {

           throw new IllegalArgumentException(
                   "Impossible d'annuler cette vente. "
                   + "Son statut actuel est : "
                   + sale.getStatus()
           );
       }

       // ============================================================
       // 3. RECHERCHE DE L'UTILISATEUR
       // ============================================================

       /*
        * L'utilisateur est récupéré depuis le JWT.
        *
        * On ne fait donc jamais confiance à un userId
        * envoyé par le frontend.
        */
       User user = getAuthenticatedUser();

       // ============================================================
       // 4. RESTAURATION DU STOCK
       // ============================================================

       for (SaleItem item : sale.getItems()) {

           Product product = item.getProduct();

           if (product == null) {

               throw new RuntimeException(
                       "Produit introuvable pour la ligne de vente."
               );
           }

           BigDecimal quantityToRestore =
                   item.getQuantity();

           if (quantityToRestore == null
                   || quantityToRestore.compareTo(BigDecimal.ZERO) <= 0) {

               throw new IllegalArgumentException(
                       "Quantité invalide pour le produit : "
                       + product.getName()
               );
           }

           BigDecimal currentStock =
                   product.getQuantity();

           if (currentStock == null) {
               currentStock = BigDecimal.ZERO;
           }

           // ========================================================
           // RESTAURATION
           // ========================================================

           product.setQuantity(
                   currentStock.add(quantityToRestore)
           );

           productRepository.saveAndFlush(product);

           // ========================================================
           // CREATION DU MOUVEMENT
           // ========================================================

           // ========================================================
            // CRÉATION DU MOUVEMENT D'ANNULATION
            // ========================================================

            StockMovementRequestDTO movementRequest =
                    new StockMovementRequestDTO();

            movementRequest.setMovementType("ENTREE");
            movementRequest.setQuantity(quantityToRestore);
            movementRequest.setReason(
                    "Annulation de la vente #" + sale.getId()
            );

            // StockMovementService récupère automatiquement
            // l'utilisateur connecté depuis le JWT.
            stockMovementService.create(
                    movementRequest,
                    product.getId()
            );
       }

       // ============================================================
       // 5. ANNULATION
       // ============================================================

       sale.setStatus("CANCELLED");

       Sale cancelledSale =
               saleRepository.save(sale);

       // ============================================================
       // 6. RETOUR
       // ============================================================

       return toResponseDTO(cancelledSale);
   }
}
