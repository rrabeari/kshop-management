import {
  Component,
  OnInit,
  inject,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../core/services/product';
import { Product } from '../../core/models/product.model';



@Component({
  selector: 'app-products',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './products.html',
  styleUrl: './products.css'
})
export class Products implements OnInit {

  private readonly productService = inject(ProductService);

  private readonly cdr = inject(ChangeDetectorRef);

  products: Product[] = [];

  loading = false;

  errorMessage = '';


  // =====================================================
  // FILTRES
  // =====================================================

  searchTerm = '';

  statusFilter = 'ALL';


  ngOnInit(): void {

    this.loadProducts();

  }


  // =====================================================
  // CHARGEMENT
  // =====================================================

  loadProducts(): void {

    this.loading = true;

    this.errorMessage = '';

    this.productService.findAll().subscribe({

      next: (data) => {

        console.log(
          'Réponse API PRODUCTS :',
          data
        );

        console.log(
          'Nombre de produits :',
          data.length
        );

        this.products = data;

        this.loading = false;

        this.cdr.detectChanges();

      },

      error: (error) => {

        console.error(
          'Erreur lors du chargement des produits :',
          error
        );

        this.errorMessage =
          'Impossible de charger les produits.';

        this.loading = false;

        this.cdr.detectChanges();

      }

    });

  }


  // =====================================================
  // ACTUALISATION
  // =====================================================

  refresh(): void {

    this.loadProducts();

  }


  // =====================================================
  // STATISTIQUES
  // =====================================================

  get totalProducts(): number {

    return this.products.length;

  }


  get activeProductsCount(): number {

    return this.products.filter(
      product => product.active
    ).length;

  }


  get lowStockProductsCount(): number {

    return this.products.filter(
      product =>
        product.quantity <= product.minimumStock
    ).length;

  }


  get inactiveProductsCount(): number {

    return this.products.filter(
      product => !product.active
    ).length;

  }


  // =====================================================
  // PRODUITS FILTRÉS
  // =====================================================

  get filteredProducts(): Product[] {

    const search =
      this.searchTerm
        .trim()
        .toLowerCase();

    return this.products.filter(product => {

      // -----------------------------
      // Recherche
      // -----------------------------

      const matchesSearch =
        !search ||

        product.code
          ?.toLowerCase()
          .includes(search) ||

        product.name
          ?.toLowerCase()
          .includes(search) ||

        product.barcode
          ?.toLowerCase()
          .includes(search) ||

        product.description
          ?.toLowerCase()
          .includes(search) ||

        product.category?.name
          ?.toLowerCase()
          .includes(search);


      // -----------------------------
      // Filtre statut
      // -----------------------------

      const matchesStatus =
        this.statusFilter === 'ALL' ||

        (
          this.statusFilter === 'ACTIVE' &&
          product.active
        ) ||

        (
          this.statusFilter === 'INACTIVE' &&
          !product.active
        );


      return matchesSearch && matchesStatus;

    });

  }


  // =====================================================
  // RESET FILTRES
  // =====================================================

  clearFilters(): void {

    this.searchTerm = '';

    this.statusFilter = 'ALL';

  }


  // =====================================================
  // ÉVÉNEMENTS
  // =====================================================

  onSearchChange(): void {
    // Le getter filteredProducts
    // recalcule automatiquement la liste.
  }


  onStatusChange(): void {
    // Le getter filteredProducts
    // recalcule automatiquement la liste.
  }

  isOutOfStock(product: Product): boolean {
    return product.quantity <= 0;
  }

  isLowStock(product: Product): boolean {
    return product.quantity <= product.minimumStock;
  }

}