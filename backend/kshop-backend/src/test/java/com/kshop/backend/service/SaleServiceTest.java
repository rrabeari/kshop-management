/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.service;


import com.kshop.backend.dto.request.SaleItemRequestDTO;
import com.kshop.backend.dto.request.SaleRequestDTO;
import com.kshop.backend.dto.request.StockMovementRequestDTO;
import com.kshop.backend.dto.response.SaleResponseDTO;
import com.kshop.backend.dto.response.StockMovementResponseDTO;
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
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires du SaleService.
 *
 * Ces tests vérifient :
 *
 * - la création d'une vente ;
 * - le calcul du montant ;
 * - la vérification du stock ;
 * - la vérification du produit actif ;
 * - la validation des quantités ;
 * - la gestion des remises ;
 * - l'annulation d'une vente ;
 * - la restauration du stock ;
 * - la création des mouvements de stock.
 *
 * Les repositories et StockMovementService sont mockés.
 *
 * PostgreSQL n'est donc pas utilisé pendant ces tests.
 */
@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {
    

    /**
     * Repository des ventes.
     */
    @Mock
    private SaleRepository saleRepository;

    /**
     * Repository des produits.
     */
    @Mock
    private ProductRepository productRepository;

    /**
     * Repository des utilisateurs.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Service de gestion des mouvements de stock.
     */
    @Mock
    private StockMovementService stockMovementService;

    /**
     * Repository des mouvements de stock.
     */
    @Mock
    private StockMovementRepository stockMovementRepository;

    /**
     * Service testé.
     */
    @InjectMocks
    private SaleService saleService;

    /**
     * Utilisateur de test.
     */
    private User manager;

    /**
     * Produit de test.
     */
    private Product product;

    /**
     * Préparation des données communes.
     */
    @BeforeEach
    void setUp() {

        // ========================================================
        // UTILISATEUR
        // ========================================================

        manager = new User();

        manager.setId(4L);
        manager.setUsername("manager2");
        manager.setFirstName("Paul");
        manager.setLastName("Manager");
        manager.setEnabled(true);

        // ========================================================
        // PRODUIT
        // ========================================================

        product = new Product();

        product.setId(2L);
        product.setCode("PRD001");
        product.setName("Eau Vive 1.5L");

        product.setSellingPrice(
                new BigDecimal("1500.00")
        );

        product.setQuantity(
                new BigDecimal("10.00")
        );

        product.setActive(true);

        // ========================================================
        // UTILISATEUR AUTHENTIFIÉ
        // ========================================================

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "manager2",
                        null,
                        List.of()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }

    /**
     * Nettoyage du SecurityContext.
     */
    @AfterEach
    void tearDown() {

        SecurityContextHolder.clearContext();
    }

    // ============================================================
    // TEST 1
    // CRÉATION D'UNE VENTE
    // ============================================================

    @Test
    void createSale_shouldCreateSaleSuccessfully() {

        when(userRepository.findByUsername("manager2"))
                .thenReturn(Optional.of(manager));

        SaleItemRequestDTO item =
                new SaleItemRequestDTO();

        item.setProductId(2L);
        item.setQuantity(
                new BigDecimal("2")
        );

        SaleRequestDTO request =
                new SaleRequestDTO();

        request.setDiscount(BigDecimal.ZERO);
        request.setItems(List.of(item));

        when(productRepository.findById(2L))
                .thenReturn(Optional.of(product));

        when(saleRepository.save(any(Sale.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );
        
        StockMovementResponseDTO movementResponse =
        new StockMovementResponseDTO();

        when(stockMovementService.create(
                any(StockMovementRequestDTO.class),
                eq(2L)
        )).thenReturn(movementResponse);

        SaleResponseDTO response =
                saleService.create(request);

        assertNotNull(response);

        assertEquals(
                new BigDecimal("3000.00"),
                response.getTotalAmount()
        );

        assertEquals(
                "COMPLETED",
                response.getStatus()
        );

        assertEquals(
                BigDecimal.ZERO,
                response.getDiscount()
        );

        verify(saleRepository)
                .save(any(Sale.class));

        verify(stockMovementService)
                .create(
                        any(StockMovementRequestDTO.class),
                        eq(2L)
                );
    }

    // ============================================================
    // TEST 2
    // STOCK INSUFFISANT
    // ============================================================

    @Test
    void createSale_shouldRejectInsufficientStock() {

        when(userRepository.findByUsername("manager2"))
                .thenReturn(Optional.of(manager));

        product.setQuantity(
                new BigDecimal("10")
        );

        SaleItemRequestDTO item =
                new SaleItemRequestDTO();

        item.setProductId(2L);
        item.setQuantity(
                new BigDecimal("100")
        );

        SaleRequestDTO request =
                new SaleRequestDTO();

        request.setItems(List.of(item));

        when(productRepository.findById(2L))
                .thenReturn(Optional.of(product));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> saleService.create(request)
                );

        assertTrue(
                exception.getMessage()
                        .contains("Stock insuffisant")
        );

        verify(saleRepository, never())
                .save(any(Sale.class));

        verify(stockMovementService, never())
                .create(
                        any(StockMovementRequestDTO.class),
                        anyLong()
                );
    }

    // ============================================================
    // TEST 3
    // PRODUIT DÉSACTIVÉ
    // ============================================================

    @Test
    void createSale_shouldRejectInactiveProduct() {

        when(userRepository.findByUsername("manager2"))
                .thenReturn(Optional.of(manager));

        product.setActive(false);

        SaleItemRequestDTO item =
                new SaleItemRequestDTO();

        item.setProductId(2L);
        item.setQuantity(
                new BigDecimal("2")
        );

        SaleRequestDTO request =
                new SaleRequestDTO();

        request.setItems(List.of(item));

        when(productRepository.findById(2L))
                .thenReturn(Optional.of(product));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> saleService.create(request)
                );

        assertTrue(
                exception.getMessage()
                        .contains("désactivé")
        );

        verify(saleRepository, never())
                .save(any(Sale.class));

        verify(stockMovementService, never())
                .create(
                        any(StockMovementRequestDTO.class),
                        anyLong()
                );
    }

    // ============================================================
    // TEST 4
    // QUANTITÉ INVALIDE
    // ============================================================

    @Test
    void createSale_shouldRejectInvalidQuantity() {

        when(userRepository.findByUsername("manager2"))
                .thenReturn(Optional.of(manager));

        SaleItemRequestDTO item =
                new SaleItemRequestDTO();

        item.setProductId(2L);
        item.setQuantity(BigDecimal.ZERO);

        SaleRequestDTO request =
                new SaleRequestDTO();

        request.setItems(List.of(item));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> saleService.create(request)
                );

        assertTrue(
                exception.getMessage()
                        .contains("supérieure à zéro")
        );

        verify(productRepository, never())
                .findById(anyLong());

        verify(saleRepository, never())
                .save(any(Sale.class));
    }

    // ============================================================
    // TEST 5
    // REMISE SUPÉRIEURE AU TOTAL
    // ============================================================

    @Test
    void createSale_shouldRejectInvalidDiscount() {

        when(userRepository.findByUsername("manager2"))
                .thenReturn(Optional.of(manager));

        SaleItemRequestDTO item =
                new SaleItemRequestDTO();

        item.setProductId(2L);
        item.setQuantity(
                new BigDecimal("2")
        );

        SaleRequestDTO request =
                new SaleRequestDTO();

        request.setDiscount(
                new BigDecimal("4000")
        );

        request.setItems(List.of(item));

        when(productRepository.findById(2L))
                .thenReturn(Optional.of(product));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> saleService.create(request)
                );

        assertTrue(
                exception.getMessage()
                        .contains("supérieure")
        );

        verify(saleRepository, never())
                .save(any(Sale.class));

        verify(stockMovementService, never())
                .create(
                        any(StockMovementRequestDTO.class),
                        anyLong()
                );
    }

    // ============================================================
    // TEST 6
    // REMISE NORMALE
    // ============================================================

    @Test
    void createSale_shouldApplyDiscountCorrectly() {

        when(userRepository.findByUsername("manager2"))
                .thenReturn(Optional.of(manager));

        SaleItemRequestDTO item =
                new SaleItemRequestDTO();

        item.setProductId(2L);
        item.setQuantity(
                new BigDecimal("2")
        );

        SaleRequestDTO request =
                new SaleRequestDTO();

        request.setDiscount(
                new BigDecimal("500")
        );

        request.setItems(List.of(item));

        when(productRepository.findById(2L))
                .thenReturn(Optional.of(product));

        when(saleRepository.save(any(Sale.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );
        
        StockMovementResponseDTO movementResponse =
        new StockMovementResponseDTO();

        when(stockMovementService.create(
                any(StockMovementRequestDTO.class),
                eq(2L)
        )).thenReturn(movementResponse);

        SaleResponseDTO response =
                saleService.create(request);

        assertEquals(
                new BigDecimal("2500.00"),
                response.getTotalAmount()
        );

        assertEquals(
                new BigDecimal("500"),
                response.getDiscount()
        );

        verify(saleRepository)
                .save(any(Sale.class));

        verify(stockMovementService)
                .create(
                        any(StockMovementRequestDTO.class),
                        eq(2L)
                );
    }

    // ============================================================
    // TEST 7
    // ANNULATION + RESTAURATION DU STOCK
    // ============================================================

    @Test
    void cancelSale_shouldRestoreStock() {

        // --------------------------------------------------------
        // Stock avant annulation
        // --------------------------------------------------------

        product.setQuantity(
                new BigDecimal("8")
        );

        // --------------------------------------------------------
        // Vente
        // --------------------------------------------------------

        Sale sale =
                new Sale();

        sale.setId(10L);
        sale.setStatus("COMPLETED");
        sale.setUser(manager);

        // --------------------------------------------------------
        // Ligne de vente
        // --------------------------------------------------------

        SaleItem item =
                new SaleItem();

        item.setId(1L);
        item.setProduct(product);

        item.setQuantity(
                new BigDecimal("2")
        );

        item.setUnitPrice(
                new BigDecimal("1500")
        );

        item.setSubtotal(
                new BigDecimal("3000")
        );

        item.setSale(sale);

        sale.setItems(
                List.of(item)
        );

        // --------------------------------------------------------
        // Mocks
        // --------------------------------------------------------

        when(saleRepository.findById(10L))
                .thenReturn(Optional.of(sale));

        /*
         * IMPORTANT :
         *
         * SaleService.getAuthenticatedUser()
         * utilise findByUsername().
         *
         * Il ne faut donc PAS utiliser findById()
         * ici.
         */
        when(userRepository.findByUsername("manager2"))
                .thenReturn(Optional.of(manager));

        when(saleRepository.save(any(Sale.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        // --------------------------------------------------------
        // Exécution
        // --------------------------------------------------------
        when(stockMovementService.create(
                any(StockMovementRequestDTO.class),
                eq(product.getId())
        )).thenReturn(new StockMovementResponseDTO());
        
        SaleResponseDTO response =
                saleService.cancelSale(10L);

        // --------------------------------------------------------
        // Vérification stock
        // --------------------------------------------------------

        assertEquals(
                new BigDecimal("10"),
                product.getQuantity()
        );

        // --------------------------------------------------------
        // Vérification statut
        // --------------------------------------------------------

        assertEquals(
                "CANCELLED",
                response.getStatus()
        );

        // --------------------------------------------------------
        // Vérification produit
        // --------------------------------------------------------

        verify(productRepository)
                .saveAndFlush(product);

        // --------------------------------------------------------
        // Vérification mouvement
        // --------------------------------------------------------

        verify(stockMovementService)
        .create(
                any(StockMovementRequestDTO.class),
                eq(product.getId())
        );

        // --------------------------------------------------------
        // Vérification vente
        // --------------------------------------------------------

        verify(saleRepository)
                .save(sale);
    }

    // ============================================================
    // TEST 8
    // DOUBLE ANNULATION
    // ============================================================

    @Test
    void cancelSale_shouldRejectAlreadyCancelledSale() {

        Sale sale =
                new Sale();

        sale.setId(10L);
        sale.setStatus("CANCELLED");

        when(saleRepository.findById(10L))
                .thenReturn(Optional.of(sale));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> saleService.cancelSale(10L)
                );

        assertTrue(
                exception.getMessage()
                        .contains("Impossible d'annuler")
        );

        verify(productRepository, never())
                .saveAndFlush(any(Product.class));

        verify(stockMovementRepository, never())
                .saveAndFlush(
                        any(StockMovement.class)
                );

        verify(saleRepository, never())
                .save(any(Sale.class));
    }

    // ============================================================
    // TEST 9
    // VENTE INEXISTANTE
    // ============================================================

    @Test
    void cancelSale_shouldRejectUnknownSale() {

        when(saleRepository.findById(9999L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> saleService.cancelSale(9999L)
                );

        assertTrue(
                exception.getMessage()
                        .contains("Vente introuvable")
        );

        verify(saleRepository, never())
                .save(any(Sale.class));

        verify(productRepository, never())
                .saveAndFlush(any(Product.class));

        verify(stockMovementRepository, never())
                .saveAndFlush(
                        any(StockMovement.class)
                );
    }
}
