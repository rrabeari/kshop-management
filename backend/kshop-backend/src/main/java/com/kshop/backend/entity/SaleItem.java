package com.kshop.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * Entité représentant une ligne d'une vente.
 *
 * Une ligne contient :
 * - un produit ;
 * - une quantité ;
 * - le prix unitaire au moment de la vente ;
 * - le sous-total de la ligne.
 */
@Entity
@Getter
@Setter
public class SaleItem extends BaseEntity {

    /**
     * Quantité vendue.
     */
    @Column(
        nullable = false,
        precision = 12,
        scale = 3
    )
    private BigDecimal quantity;

    /**
     * Prix unitaire du produit au moment de la vente.
     *
     * IMPORTANT :
     * On conserve le prix historique.
     */
    @Column(
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal unitPrice;

    /**
     * Sous-total de la ligne.
     *
     * quantity × unitPrice
     */
    @Column(
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal subtotal;

    /**
     * Vente à laquelle appartient cette ligne.
     */
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    private Sale sale;

    /**
     * Produit vendu.
     */
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    private Product product;

    /**
     * Constructeur vide requis par JPA.
     */
    public SaleItem() {
    }
}