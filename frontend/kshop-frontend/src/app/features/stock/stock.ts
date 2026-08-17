import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StockMovementService } from '../../core/services/stock-movement.service';
import { ProductService } from '../../core/services/product';
import { StockMovement } from '../../core/models/stock-movement.model';
import { Product } from '../../core/models/product.model';
import { StockMovementRequest } from '../../core/models/stock-movement-request.model';



@Component({
  selector: 'app-stock',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './stock.html',
  styleUrls: ['./stock.css']
})
export class StockComponent implements OnInit {

  private readonly stockService = inject(StockMovementService);
  private readonly productService = inject(ProductService);
  private readonly cdr = inject(ChangeDetectorRef);

  movements: StockMovement[] = [];
  filteredMovements: StockMovement[] = [];
  products: Product[] = [];
  filteredProducts: Product[] = [];

  loading = false;
  productsLoading = false;
  movementCreating = false;
  errorMessage = '';
  movementFormError = '';
  movementSuccessMessage = '';

  showNewMovement = false;

  selectedProduct: Product | null = null;
  movementRequest: StockMovementRequest = {
    movementType: 'ENTREE',
    quantity: 1,
    reason: ''
  };

  searchTerm = '';
  typeFilter = 'ALL';
  productSearchTerm = '';
  selectedMovement: StockMovement | null = null;

  ngOnInit(): void {
    this.loadMovements();
    this.loadProducts();
  }

  loadMovements(): void {
    this.loading = true;
    this.errorMessage = '';

    this.stockService.findAll().subscribe({
      next: (data) => {
        this.movements = data;
        this.applyFilters();
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Erreur chargement mouvements', err);
        this.errorMessage = 'Impossible de charger l\'historique des mouvements de stock.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadProducts(): void {
    this.productsLoading = true;
    this.productService.findAll().subscribe({
      next: (data) => {
        this.products = data;
        this.filteredProducts = data;
        this.productsLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Erreur chargement produits', err);
        this.productsLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  refresh(): void {
    this.loadMovements();
    this.loadProducts();
  }

  openNewMovement(): void {
    this.showNewMovement = true;
    this.movementFormError = '';
    this.selectedProduct = null;
    this.movementRequest = {
      movementType: 'ENTREE',
      quantity: 1,
      reason: ''
    };
    this.cdr.markForCheck();
  }

  cancelNewMovement(): void {
    this.showNewMovement = false;
    this.movementFormError = '';
    this.cdr.markForCheck();
  }

  selectProductForMovement(product: Product): void {
    this.selectedProduct = product;
    this.cdr.markForCheck();
  }

  onProductSearchChange(): void {
    const term = this.productSearchTerm.toLowerCase().trim();
    if (!term) {
      this.filteredProducts = this.products;
      this.cdr.markForCheck();
      return;
    }
    this.filteredProducts = this.products.filter(p =>
      p.name.toLowerCase().includes(term) ||
      p.code.toLowerCase().includes(term) ||
      (p.barcode && p.barcode.toLowerCase().includes(term))
    );
    this.cdr.markForCheck();
  }

  createMovement(): void {
    if (!this.selectedProduct) {
      this.movementFormError = 'Veuillez sélectionner un produit.';
      return;
    }

    if (this.movementRequest.quantity <= 0) {
      this.movementFormError = 'La quantité doit être supérieure à 0.';
      return;
    }

    this.movementCreating = true;
    this.movementFormError = '';

    this.stockService.create(this.selectedProduct.id, this.movementRequest).subscribe({
      next: () => {
        this.movementCreating = false;
        this.showNewMovement = false;
        this.movementSuccessMessage = 'Mouvement de stock enregistré avec succès.';
        setTimeout(() => {
          this.movementSuccessMessage = '';
          this.cdr.markForCheck();
        }, 4000);
        this.loadMovements();
        this.loadProducts();
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Erreur création mouvement', err);
        this.movementCreating = false;
        this.movementFormError = err.error?.message || 'Erreur lors de l\'enregistrement du mouvement.';
        this.cdr.markForCheck();
      }
    });
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onTypeChange(): void {
    this.applyFilters();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.typeFilter = 'ALL';
    this.applyFilters();
  }

  applyFilters(): void {
    let result = [...this.movements];

    if (this.typeFilter !== 'ALL') {
      result = result.filter(m => m.movementType === this.typeFilter);
    }

    const term = this.searchTerm.toLowerCase().trim();
    if (term) {
      result = result.filter(m =>
        m.id.toString().includes(term) ||
        m.product?.name?.toLowerCase().includes(term) ||
        m.product?.code?.toLowerCase().includes(term) ||
        m.user?.username?.toLowerCase().includes(term) ||
        m.reason?.toLowerCase().includes(term)
      );
    }

    this.filteredMovements = result;
    this.cdr.markForCheck();
  }

  get entriesCount(): number {
    return this.movements.filter(m => m.movementType === 'ENTREE').length;
  }

  get exitsCount(): number {
    return this.movements.filter(m => m.movementType === 'SORTIE').length;
  }

  get adjustmentsCount(): number {
    return this.movements.filter(m => m.movementType === 'AJUSTEMENT').length;
  }

  selectMovement(movement: StockMovement): void {
    this.selectedMovement = movement;
    this.cdr.markForCheck();
  }

  closeMovementDetail(): void {
    this.selectedMovement = null;
    this.cdr.markForCheck();
  }

  deleteMovement(movement: StockMovement, event: Event): void {
    event.stopPropagation();
    if (!confirm(`Êtes-vous sûr de vouloir supprimer le mouvement #${movement.id} ?`)) {
      return;
    }

    this.stockService.delete(movement.id).subscribe({
      next: () => {
        this.movementSuccessMessage = `Mouvement #${movement.id} supprimé avec succès.`;
        setTimeout(() => {
          this.movementSuccessMessage = '';
          this.cdr.markForCheck();
        }, 4000);
        this.loadMovements();
        this.loadProducts();
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Erreur suppression', err);
        alert(err.error?.message || 'Erreur lors de la suppression.');
      }
    });
  }
}