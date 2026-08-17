import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  Sale,
  SaleRequest
} from '../../../core/models/sale.model';

import { Product } from '../../../core/models/product.model';

import { SaleService } from '../../../core/services/sale';
import { ProductService } from '../../../core/services/product';


/**
 * Élément présent dans le panier.
 */
interface CartItem {

  /**
   * Produit sélectionné.
   */
  product: Product;

  /**
   * Quantité vendue.
   */
  quantity: number;
}


@Component({
  selector: 'app-sales',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './sales.html',
  styleUrl: './sales.css'
})
export class Sales implements OnInit {

  private readonly saleService =
    inject(SaleService);

  private readonly productService =
    inject(ProductService);

  private readonly cdr =
    inject(ChangeDetectorRef);


  // ==========================================================
  // HISTORIQUE DES VENTES
  // ==========================================================

  sales: Sale[] = [];

  /**
   * Liste réellement affichée.
   */
  filteredSales: Sale[] = [];

  /**
   * Recherche historique.
   */
  searchTerm = '';

  /**
   * Filtre statut.
   */
  statusFilter = 'ALL';

  /**
   * Chargement historique.
   */
  loading = false;

  /**
   * Erreur historique.
   */
  errorMessage = '';

  /**
   * Vente sélectionnée.
   */
  selectedSale: Sale | null = null;


  // ==========================================================
  // NOUVELLE VENTE
  // ==========================================================

  /**
   * Indique si la caisse est affichée.
   */
  showNewSale = false;

  /**
   * Liste complète des produits.
   */
  products: Product[] = [];

  /**
   * Produits affichés après recherche.
   */
  filteredProducts: Product[] = [];

  /**
   * Recherche produit.
   */
  productSearchTerm = '';

  /**
   * Panier.
   */
  cartItems: CartItem[] = [];

  /**
   * Remise globale.
   */
  discount = 0;

  /**
   * Chargement des produits.
   */
  productsLoading = false;

  /**
   * Chargement lors de la validation.
   */
  saleCreating = false;

  /**
   * Message de succès.
   */
  saleSuccessMessage = '';

  /**
   * Erreur de création.
   */
  saleFormError = '';


  // ==========================================================
  // INITIALISATION
  // ==========================================================

  ngOnInit(): void {

    this.loadSales();
  }


  // ==========================================================
  // HISTORIQUE
  // ==========================================================

  /**
   * Charge toutes les ventes.
   */
  loadSales(): void {

    this.loading = true;

    this.errorMessage = '';

    this.saleService.findAll().subscribe({

      next: (data: Sale[]) => {

        console.log(
          'Réponse API SALES :',
          data
        );

        this.sales = data;

        this.applyFilters();

        this.loading = false;

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Erreur lors du chargement des ventes :',
          error
        );

        this.errorMessage =
          'Impossible de charger les ventes.';

        this.loading = false;

        this.cdr.detectChanges();
      }
    });
  }


  /**
   * Actualise l'historique.
   */
  refresh(): void {

    this.loadSales();
  }


  // ==========================================================
  // STATISTIQUES
  // ==========================================================

  get completedSalesCount(): number {

    return this.sales.filter(
      sale => sale.status === 'COMPLETED'
    ).length;
  }


  get cancelledSalesCount(): number {

    return this.sales.filter(
      sale => sale.status === 'CANCELLED'
    ).length;
  }


  get completedSalesTotal(): number {

    return this.sales
      .filter(
        sale => sale.status === 'COMPLETED'
      )
      .reduce(
        (total, sale) =>
          total + Number(sale.totalAmount),
        0
      );
  }


  // ==========================================================
  // HISTORIQUE - RECHERCHE
  // ==========================================================

  applyFilters(): void {

    const search =
      this.searchTerm
        .trim()
        .toLowerCase();

    this.filteredSales =
      this.sales.filter(sale => {

        const matchesSearch =
          !search ||
          String(sale.id).includes(search) ||
          (sale.username ?? '')
            .toLowerCase()
            .includes(search) ||
          (sale.firstName ?? '')
            .toLowerCase()
            .includes(search) ||
          (sale.lastName ?? '')
            .toLowerCase()
            .includes(search) ||
          sale.items.some(item =>
            (item.productName ?? '')
              .toLowerCase()
              .includes(search) ||
            (item.productCode ?? '')
              .toLowerCase()
              .includes(search)
          );

        const matchesStatus =
          this.statusFilter === 'ALL' ||
          sale.status === this.statusFilter;

        return matchesSearch && matchesStatus;
      });
  }


  onSearchChange(): void {

    this.applyFilters();
  }


  onStatusChange(): void {

    this.applyFilters();
  }


  clearFilters(): void {

    this.searchTerm = '';

    this.statusFilter = 'ALL';

    this.applyFilters();
  }


  // ==========================================================
  // DÉTAIL VENTE
  // ==========================================================

  selectSale(sale: Sale): void {

    this.selectedSale = sale;
  }


  closeSaleDetail(): void {

    this.selectedSale = null;
  }


  // ==========================================================
  // NOUVELLE VENTE
  // ==========================================================

  /**
   * Ouvre l'écran de création de vente.
   */
  openNewSale(): void {

    this.showNewSale = true;

    this.saleSuccessMessage = '';

    this.saleFormError = '';

    this.productSearchTerm = '';

    this.discount = 0;

    this.cartItems = [];

    this.loadProducts();
  }


  /**
   * Ferme l'écran de création
   * et réinitialise le panier.
   */
  cancelNewSale(): void {

    this.showNewSale = false;

    this.cartItems = [];

    this.discount = 0;

    this.productSearchTerm = '';

    this.saleSuccessMessage = '';

    this.saleFormError = '';
  }


  // ==========================================================
  // PRODUITS
  // ==========================================================

  /**
   * Charge les produits disponibles.
   */
  loadProducts(): void {

    this.productsLoading = true;

    this.productService.findAll().subscribe({

      next: (data: Product[]) => {

        /*
         * Pour la caisse, on affiche uniquement
         * les produits actifs.
         */
        this.products =
          data.filter(product => product.active);

        this.applyProductFilter();

        this.productsLoading = false;

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Erreur lors du chargement des produits :',
          error
        );

        this.saleFormError =
          'Impossible de charger les produits.';

        this.productsLoading = false;

        this.cdr.detectChanges();
      }
    });
  }


  /**
   * Recherche les produits.
   */
  applyProductFilter(): void {

    const search =
      this.productSearchTerm
        .trim()
        .toLowerCase();

    this.filteredProducts =
      this.products.filter(product => {

        if (!search) {
          return true;
        }

        return (
          String(product.id)
            .includes(search) ||

          (product.code ?? '')
            .toLowerCase()
            .includes(search) ||

          (product.barcode ?? '')
            .toLowerCase()
            .includes(search) ||

          (product.name ?? '')
            .toLowerCase()
            .includes(search) ||

          (product.category?.name ?? '')
            .toLowerCase()
            .includes(search)
        );
      });
  }


  onProductSearchChange(): void {

    this.applyProductFilter();
  }


  // ==========================================================
  // PANIER
  // ==========================================================

  /**
   * Ajoute un produit au panier.
   */
  addToCart(product: Product): void {

    this.saleFormError = '';

    /*
     * Vérifie le stock.
     */
    if (Number(product.quantity) <= 0) {

      this.saleFormError =
        `Le produit "${product.name}" est en rupture de stock.`;

      return;
    }

    const existingItem =
      this.cartItems.find(
        item =>
          item.product.id === product.id
      );

    if (existingItem) {

      /*
       * Vérifie que la nouvelle quantité
       * ne dépasse pas le stock disponible.
       */
      if (
        existingItem.quantity >=
        Number(product.quantity)
      ) {

        this.saleFormError =
          `Stock insuffisant pour "${product.name}".`;

        return;
      }

      existingItem.quantity++;

      return;
    }

    this.cartItems.push({
      product,
      quantity: 1
    });
  }


  /**
   * Augmente la quantité d'un article.
   */
  increaseQuantity(item: CartItem): void {

    this.saleFormError = '';

    if (
      item.quantity >=
      Number(item.product.quantity)
    ) {

      this.saleFormError =
        `Stock insuffisant pour "${item.product.name}".`;

      return;
    }

    item.quantity++;
  }


  /**
   * Diminue la quantité.
   */
  decreaseQuantity(item: CartItem): void {

    this.saleFormError = '';

    if (item.quantity <= 1) {

      this.removeFromCart(item);

      return;
    }

    item.quantity--;
  }


  /**
   * Supprime un article du panier.
   */
  removeFromCart(item: CartItem): void {

    this.cartItems =
      this.cartItems.filter(
        cartItem =>
          cartItem.product.id !==
          item.product.id
      );
  }


  // ==========================================================
  // CALCULS
  // ==========================================================

  /**
   * Sous-total avant remise.
   */
  get subtotal(): number {

    return this.cartItems.reduce(
      (total, item) =>
        total +
        (
          Number(item.product.sellingPrice) *
          item.quantity
        ),
      0
    );
  }


  /**
   * Remise sécurisée.
   */
  get validDiscount(): number {

    const value =
      Number(this.discount);

    if (isNaN(value) || value < 0) {
      return 0;
    }

    return Math.min(
      value,
      this.subtotal
    );
  }


  /**
   * Total final.
   */
  get total(): number {

    return Math.max(
      0,
      this.subtotal -
      this.validDiscount
    );
  }


  /**
   * Sous-total d'un article.
   */
  getItemSubtotal(item: CartItem): number {

    return (
      Number(item.product.sellingPrice) *
      item.quantity
    );
  }


  // ==========================================================
  // CRÉATION VENTE
  // ==========================================================

  /**
   * Envoie la vente au backend.
   */
  createSale(): void {

    this.saleFormError = '';

    this.saleSuccessMessage = '';

    /*
     * Une vente doit contenir
     * au moins un article.
     */
    if (this.cartItems.length === 0) {

      this.saleFormError =
        'Ajoutez au moins un produit à la vente.';

      return;
    }

    /*
     * Vérification finale des quantités.
     */
    for (const item of this.cartItems) {

      if (
        item.quantity <= 0 ||
        item.quantity >
        Number(item.product.quantity)
      ) {

        this.saleFormError =
          `Quantité invalide pour "${item.product.name}".`;

        return;
      }
    }

    /*
     * Construction exacte du DTO attendu
     * par le backend.
     *
     * IMPORTANT :
     * aucun userId n'est envoyé.
     */
    const request: SaleRequest = {

      discount: this.validDiscount,

      items: this.cartItems.map(item => ({

        productId: item.product.id,

        quantity: item.quantity

      }))
    };


    this.saleCreating = true;


    this.saleService.create(request).subscribe({

      next: (createdSale: Sale) => {

        console.log(
          'Vente créée :',
          createdSale
        );

        this.saleCreating = false;

        this.saleSuccessMessage =
          `Vente #${createdSale.id} créée avec succès.`;

        /*
         * Recharge l'historique.
         */
        this.loadSales();

        /*
         * Ferme la caisse après
         * une création réussie.
         */
        this.cartItems = [];

        this.discount = 0;

        this.showNewSale = false;

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Erreur création vente :',
          error
        );

        this.saleCreating = false;

        /*
         * Message selon le statut HTTP.
         */
        if (error.status === 400) {

          this.saleFormError =
            error.error?.message ??
            'Données de vente invalides.';

        } else if (error.status === 401) {

          this.saleFormError =
            'Votre session a expiré. Veuillez vous reconnecter.';

        } else if (error.status === 403) {

          this.saleFormError =
            'Vous n’avez pas l’autorisation de créer une vente.';

        } else {

          this.saleFormError =
            error.error?.message ??
            'Impossible de créer la vente.';
        }

        this.cdr.detectChanges();
      }
    });
  }


  // ==========================================================
  // ANNULATION
  // ==========================================================

  /**
   * Annule une vente existante.
   */
  cancelSale(sale: Sale): void {

    if (sale.status !== 'COMPLETED') {
      return;
    }

    const confirmed =
      window.confirm(
        `Voulez-vous vraiment annuler la vente #${sale.id} ?`
      );

    if (!confirmed) {
      return;
    }

    this.saleService.cancel(
      sale.id
    ).subscribe({

      next: () => {

        this.loadSales();
      },

      error: (error) => {

        console.error(
          'Erreur lors de l’annulation :',
          error
        );

        this.errorMessage =
          error.error?.message ??
          'Impossible d’annuler la vente.';

        this.cdr.detectChanges();
      }
    });
  }
}