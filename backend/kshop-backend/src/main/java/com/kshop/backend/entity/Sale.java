package com.kshop.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Entité représentant une vente.
 *
 * Une vente :
 * - appartient à un utilisateur ;
 * - contient plusieurs SaleItem ;
 * - possède un montant total ;
 * - peut avoir une remise ;
 * - possède un statut.
 */
@Entity
@Getter
@Setter
public class Sale extends BaseEntity {

    /**
     * Date et heure de la vente.
     */
    @Column(nullable = false)
    private LocalDateTime saleDate;

    /**
     * Montant total réellement calculé pour la vente.
     */
    @Column(
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal totalAmount;

    /**
     * Remise accordée sur la vente.
     */
    @Column(
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal discount = BigDecimal.ZERO;

    /**
     * Statut de la vente.
     *
     * Valeurs prévues :
     * COMPLETED
     * CANCELLED
     */
    @Column(
        nullable = false,
        length = 20
    )
    private String status;

    /**
     * Utilisateur ayant effectué la vente.
     */
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    private User user;

    /**
     * Articles contenus dans la vente.
     */
    @OneToMany(
        mappedBy = "sale",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<SaleItem> items = new ArrayList<>();

    /**
     * Constructeur vide requis par JPA.
     */
    public Sale() {
    }

    /**
     * Ajoute un article à la vente.
     *
     * La relation SaleItem → Sale est également
     * automatiquement configurée.
     *
     * @param item article à ajouter
     */
    public void addItem(SaleItem item) {

        items.add(item);

        item.setSale(this);
    }

    /**
     * Retire un article de la vente.
     *
     * @param item article à retirer
     */
    public void removeItem(SaleItem item) {

        items.remove(item);

        item.setSale(null);
    }
}