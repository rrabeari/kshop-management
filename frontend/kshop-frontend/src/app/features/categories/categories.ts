import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Category } from '../../core/models/category.model';
import { CategoryService } from '../../core/services/category';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './categories.html',
  styleUrl: './categories.css'
})
export class Categories implements OnInit {

  private readonly categoryService = inject(CategoryService);

  private readonly cdr = inject(ChangeDetectorRef);


  // =========================================================
  // DONNÉES
  // =========================================================

  categories: Category[] = [];

  loading = false;

  errorMessage = '';

  successMessage = '';

  deletingId: number | null = null;


  // =========================================================
  // FORMULAIRE
  // =========================================================

  formVisible = false;

  editing = false;

  editingId: number | null = null;


  categoryForm = {
    name: '',
    description: ''
  };


  saving = false;


  // =========================================================
  // INITIALISATION
  // =========================================================

  ngOnInit(): void {

    this.loadCategories();

  }


  // =========================================================
  // CHARGEMENT
  // =========================================================

  loadCategories(): void {

    this.loading = true;

    this.errorMessage = '';

    this.categoryService.findAll().subscribe({

      next: (data) => {

        console.log(
          'Réponse API CATEGORIES :',
          data
        );

        this.categories = data;

        this.loading = false;

        this.cdr.detectChanges();

      },

      error: (error) => {

        console.error(
          'Erreur lors du chargement des catégories :',
          error
        );

        this.errorMessage =
          'Impossible de charger les catégories.';

        this.loading = false;

        this.cdr.detectChanges();

      }

    });

  }


  // =========================================================
  // ACTUALISER
  // =========================================================

  refresh(): void {

    this.loadCategories();

  }


  // =========================================================
  // OUVRIR FORMULAIRE
  // =========================================================

  openCreateForm(): void {

    this.editing = false;

    this.editingId = null;

    this.categoryForm = {
      name: '',
      description: ''
    };

    this.errorMessage = '';

    this.successMessage = '';

    this.formVisible = true;

  }


  // =========================================================
  // OUVRIR MODIFICATION
  // =========================================================

  openEditForm(category: Category): void {

    this.editing = true;

    this.editingId = category.id;

    this.categoryForm = {

      name: category.name,

      description:
        category.description ?? ''

    };

    this.errorMessage = '';

    this.successMessage = '';

    this.formVisible = true;

  }


  // =========================================================
  // FERMER FORMULAIRE
  // =========================================================

  closeForm(): void {

    if (this.saving) {
      return;
    }

    this.formVisible = false;

    this.editing = false;

    this.editingId = null;

    this.categoryForm = {
      name: '',
      description: ''
    };

    this.errorMessage = '';

  }


  // =========================================================
  // ENREGISTRER
  // =========================================================

  saveCategory(): void {

    this.errorMessage = '';

    this.successMessage = '';


    // -------------------------------------------------------
    // VALIDATION
    // -------------------------------------------------------

    const name =
      this.categoryForm.name.trim();

    const description =
      this.categoryForm.description.trim();


    if (!name) {

      this.errorMessage =
        'Le nom de la catégorie est obligatoire.';

      return;

    }


    // -------------------------------------------------------
    // PROTECTION
    // -------------------------------------------------------

    if (this.saving) {
      return;
    }


    this.saving = true;


    const categoryData = {

      name,

      description

    };


    // -------------------------------------------------------
    // CRÉATION
    // -------------------------------------------------------

    if (!this.editing) {

      this.categoryService
        .create(categoryData)
        .subscribe({

          next: (category) => {

            console.log(
              'Catégorie créée :',
              category
            );

            this.successMessage =
              'Catégorie créée avec succès.';
            this.showSuccess(
              'Catégorie créée avec succès.'
            );

            this.saving = false;

            this.formVisible = false;

            this.loadCategories();

          },

          error: (error) => {

            console.error(
              'Erreur création catégorie :',
              error
            );

            this.handleSaveError(error);

          }

        });

      return;

    }


    // -------------------------------------------------------
    // MODIFICATION
    // -------------------------------------------------------

    if (this.editingId === null) {

      this.saving = false;

      this.errorMessage =
        'Identifiant de catégorie invalide.';

      return;

    }


    this.categoryService
      .update(
        this.editingId,
        categoryData
      )
      .subscribe({

        next: (category) => {

          console.log(
            'Catégorie modifiée :',
            category
          );

          this.successMessage =
            'Catégorie modifiée avec succès.';

          this.showSuccess(
            'Catégorie modifiée avec succès.'
          );

          this.saving = false;

          this.formVisible = false;

          this.loadCategories();

        },

        error: (error) => {

          console.error(
            'Erreur modification catégorie :',
            error
          );

          this.handleSaveError(error);

        }

      });

  }


  // =========================================================
  // GESTION ERREUR SAUVEGARDE
  // =========================================================

  private handleSaveError(error: any): void {

    this.saving = false;


    if (
      error?.error?.message
    ) {

      this.errorMessage =this.getErrorMessage(error);

    } else {

      this.errorMessage =
        'Impossible d’enregistrer la catégorie.';

    }

    this.cdr.detectChanges();

  }


  // =========================================================
  // SUPPRESSION
  // =========================================================

  deleteCategory(category: Category): void {

    if (!category.id) {
      return;
    }

    if (this.deletingId !== null) {
      return;
    }

    const confirmed = window.confirm(
      `Voulez-vous vraiment supprimer la catégorie "${category.name}" ?`
    );

    if (!confirmed) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.deletingId = category.id;

    this.categoryService
      .delete(category.id)
      .subscribe({

        next: () => {

          console.log(
            'Catégorie supprimée :',
            category.id
          );

          this.successMessage =
            'Catégorie supprimée avec succès.';

          this.deletingId = null;

          this.loadCategories();
        },

        error: (error) => {

          console.error(
            'Erreur suppression catégorie :',
            error
          );

          this.deletingId = null;

          if (error?.error?.message) {

            this.errorMessage =this.getCategoryErrorMessage(error);

          } else {

            this.errorMessage =
              'Impossible de supprimer cette catégorie.';

          }

          this.cdr.detectChanges();
        }

      });
  }

  private getErrorMessage(error: any): string {

    if (typeof error?.error === 'string') {
      return error.error;
    }

    if (error?.error?.message) {
      return error.error.message;
    }

    if (error?.message) {
      return error.message;
    }

    if (error?.status === 409) {
      return 'Cette catégorie ne peut pas être supprimée car elle est utilisée.';
    }

    return 'Une erreur est survenue. Veuillez réessayer.';
  }

  private showSuccess(message: string): void {

    this.successMessage = message;

    setTimeout(() => {

      this.successMessage = '';

      this.cdr.detectChanges();

    }, 3000);
  }

  private getCategoryErrorMessage(error: any): string {

    const message =
      error?.error?.message ||
      error?.error ||
      error?.message ||
      '';

    // Catégorie encore utilisée par un produit
    if (
      message.includes('violates foreign key constraint') ||
      message.includes('viole la contrainte de clé étrangère') ||
      message.includes('product') ||
      message.includes('category')
    ) {

      return 'Impossible de supprimer cette catégorie car elle est utilisée par un ou plusieurs produits.';
    }

    return 'Une erreur est survenue. Veuillez réessayer.';
  }

}