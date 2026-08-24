import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Category } from '../../core/models/category.model';
import { Product, ProductRequest } from '../../core/models/product.model';
import { CategoryService } from '../../core/services/category';
import { ProductService } from '../../core/services/product';



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
  private readonly categoryService = inject(CategoryService);
  private readonly cdr = inject(ChangeDetectorRef);

  // =========================================================
  // DONNÉES & FILTRES
  // =========================================================

  products: Product[] = [];
  filteredProducts: Product[] = [];
  categories: Category[] = [];

  searchTerm = '';
  statusFilter: string = 'all'; // 'all' | 'active' | 'inactive'
  
  loading = false;
  errorMessage = '';
  successMessage = '';

  // =========================================================
  // FORMULAIRE
  // =========================================================

  formVisible = false;
  editing = false;
  editingId: number | null = null;

  productForm: ProductRequest = {
    code: '',
    barcode: '',
    name: '',
    description: '',
    purchasePrice: 0,
    sellingPrice: 0,
    quantity: 0,
    minimumStock: 0,
    unit: '',
    active: true,
    categoryId: 0
  };

  saving = false;

  // =========================================================
  // INITIALISATION
  // =========================================================

  ngOnInit(): void {
    this.loadProducts();
    this.loadCategories();
  }

  // =========================================================
  // CHARGEMENT DES DONNÉES
  // =========================================================

  loadProducts(): void {
    this.loading = true;
    this.errorMessage = '';

    this.productService.findAll().subscribe({
      next: (data) => {
        this.products = data;
        this.filterProducts();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des produits :', error);
        this.errorMessage = 'Impossible de charger les produits.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadCategories(): void {
    this.categoryService.findAll().subscribe({
      next: (data) => {
        this.categories = data;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des catégories :', error);
      }
    });
  }

  refresh(): void {
    this.loadProducts();
    this.loadCategories();
  }

  // =========================================================
  // CALCULS STATISTIQUES (GETTERS POUR LES CARDS)
  // =========================================================

  get totalProductsCount(): number {
    return this.products.length;
  }

  get outOfStockCount(): number {
    return this.products.filter(p => p.quantity <= p.minimumStock).length;
  }

  get inactiveProductsCount(): number {
    return this.products.filter(p => !p.active).length;
  }

  get categoriesCount(): number {
    // Nombre unique de catégories associées aux produits chargés
    const uniqueCategoryIds = new Set(
      this.products
        .filter(p => p.category && p.category.id)
        .map(p => p.category!.id)
    );
    return uniqueCategoryIds.size;
  }

  // =========================================================
  // FILTRES & RÉINITIALISATION
  // =========================================================

  filterProducts(): void {
    const term = this.searchTerm.toLowerCase().trim();

    this.filteredProducts = this.products.filter(p => {
      // 1. Filtre par recherche texte
      const matchesSearch = !term || 
        p.name.toLowerCase().includes(term) ||
        p.code.toLowerCase().includes(term) ||
        (p.category && p.category.name.toLowerCase().includes(term));

      // 2. Filtre par statut
      let matchesStatus = true;
      if (this.statusFilter === 'active') {
        matchesStatus = p.active === true;
      } else if (this.statusFilter === 'inactive') {
        matchesStatus = p.active === false;
      }

      return matchesSearch && matchesStatus;
    });
  }

  resetFilters(): void {
    this.searchTerm = '';
    this.statusFilter = 'all';
    this.filterProducts();
  }

  // =========================================================
  // OUVRIR / FERMER FORMULAIRE
  // =========================================================

  openCreateForm(): void {
    this.editing = false;
    this.editingId = null;

    this.productForm = {
      code: '',
      barcode: '',
      name: '',
      description: '',
      purchasePrice: 0,
      sellingPrice: 0,
      quantity: 0,
      minimumStock: 0,
      unit: '',
      active: true,
      categoryId: this.categories.length > 0 ? this.categories[0].id : 0
    };

    this.errorMessage = '';
    this.successMessage = '';
    this.formVisible = true;
  }

  closeForm(): void {
    if (this.saving) {
      return;
    }
    this.formVisible = false;
    this.editing = false;
    this.editingId = null;
    this.errorMessage = '';
  }

  // =========================================================
  // ENREGISTRER (CRÉATION / MODIFICATION)
  // =========================================================

  saveProduct(): void {
    this.errorMessage = '';
    this.successMessage = '';

    const code = this.productForm.code.trim();
    const name = this.productForm.name.trim();

    if (!code) {
      this.errorMessage = 'Le code du produit est obligatoire.';
      return;
    }

    if (!name) {
      this.errorMessage = 'Le nom du produit est obligatoire.';
      return;
    }

    if (!this.productForm.categoryId || Number(this.productForm.categoryId) <= 0) {
      this.errorMessage = 'Veuillez sélectionner une catégorie valide.';
      return;
    }

    if (this.saving) {
      return;
    }

    this.saving = true;

    if (!this.editing) {
      this.productService.create(this.productForm).subscribe({
        next: () => {
          this.showSuccess('Produit créé avec succès.');
          this.saving = false;
          this.formVisible = false;
          this.loadProducts();
        },
        error: (error) => {
          console.error('Erreur création produit :', error);
          this.handleSaveError(error);
        }
      });
      return;
    }

    if (this.editingId === null) {
      this.saving = false;
      this.errorMessage = 'Identifiant de produit invalide.';
      return;
    }

    this.productService.update(this.editingId, this.productForm).subscribe({
      next: () => {
        this.showSuccess('Produit modifié avec succès.');
        this.saving = false;
        this.formVisible = false;
        this.loadProducts();
      },
      error: (error) => {
        console.error('Erreur modification produit :', error);
        this.handleSaveError(error);
      }
    });
  }

  private handleSaveError(error: any): void {
    this.saving = false;
    if (error?.error?.message) {
      this.errorMessage = error.error.message;
    } else if (typeof error?.error === 'string') {
      this.errorMessage = error.error;
    } else {
      this.errorMessage = 'Impossible d’enregistrer le produit.';
    }
    this.cdr.detectChanges();
  }

  private showSuccess(message: string): void {
    this.successMessage = message;
    setTimeout(() => {
      this.successMessage = '';
      this.cdr.detectChanges();
    }, 3000);
  }

  // =========================================================
  // ACTIONS SUR LE PRODUIT (SUPPRESSION & TOGGLE STATUS)
  // =========================================================

  toggleProductStatus(product: Product): void {
    const newStatus = !product.active;
    const actionLabel = newStatus ? 'activer' : 'désactiver';

    const confirmed = window.confirm(`Voulez-vous vraiment ${actionLabel} le produit "${product.name}" ?`);
    if (!confirmed) {
      return;
    }

    const updatePayload: ProductRequest = {
      code: product.code,
      barcode: product.barcode,
      name: product.name,
      description: product.description,
      purchasePrice: product.purchasePrice,
      sellingPrice: product.sellingPrice,
      quantity: product.quantity,
      minimumStock: product.minimumStock,
      unit: product.unit,
      active: newStatus,
      categoryId: product.category ? product.category.id : 0
    };

    this.productService.update(product.id, updatePayload).subscribe({
      next: () => {
        this.showSuccess(`Produit ${newStatus ? 'activé' : 'désactivé'} avec succès.`);
        this.loadProducts();
      },
      error: (error) => {
        console.error('Erreur changement de statut :', error);
        this.errorMessage = error?.error?.message || 'Impossible de modifier le statut du produit.';
        this.cdr.detectChanges();
      }
    });
  }

  deleteProduct(product: Product): void {
    if (!product.id) {
      return;
    }

    const confirmed = window.confirm(`Voulez-vous vraiment supprimer définitivement le produit "${product.name}" ?`);
    if (!confirmed) {
      return;
    }

    this.productService.delete(product.id).subscribe({
      next: () => {
        this.showSuccess('Produit supprimé avec succès.');
        this.loadProducts();
      },
      error: (error) => {
        console.error('Erreur suppression produit :', error);
        this.errorMessage = error?.error?.message || 'Impossible de supprimer ce produit.';
        this.cdr.detectChanges();
      }
    });
  }
}