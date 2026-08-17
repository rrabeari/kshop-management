import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CreateUserRequest, UpdateUserRequest, User } from '../../core/models/user.model';
import { UserService } from '../../core/services/user';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './users.html',
  styleUrl: './users.css'
})
export class Users implements OnInit {

  private readonly userService = inject(UserService);
  private readonly cdr = inject(ChangeDetectorRef);

  users: User[] = [];

  loading = false;

  errorMessage = '';

  // ============================
  // FILTRES
  // ============================

  searchTerm = '';

  roleFilter = 'ALL';

  statusFilter = 'ALL';

   // ============================
  // Creation
  // ============================


  showCreateForm = false;

  saving = false;

  successMessage = '';

  createErrorMessage = '';

  newUser: CreateUserRequest = {
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    enabled: true
  };

selectedRoleId: number | null = null;

  // ============================
  // Modification
  // ==========================

  showEditForm = false;

  editingUserId: number | null = null;

  editUser: User = {
    id: 0,
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    enabled: true,
    role: ''
  };

  editRoleId: number | null = null;

  editSaving = false;

  editErrorMessage = '';



  // ============================
  // INITIALISATION
  // ============================

  ngOnInit(): void {
    this.loadUsers();
  }

  // ============================
  // CHARGEMENT
  // ============================

  loadUsers(): void {

    this.loading = true;
    this.errorMessage = '';

    this.userService.findAll().subscribe({

      next: (data) => {

        console.log('Réponse API USERS :', data);

        this.users = data;

        this.loading = false;

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Erreur lors du chargement des utilisateurs :',
          error
        );

        this.users = [];

        this.errorMessage =
          'Impossible de charger les utilisateurs.';

        this.loading = false;

        this.cdr.detectChanges();
      }

    });
  }

  // ============================
  // USERS FILTRÉS
  // ============================

  get filteredUsers(): User[] {

    const search = this.searchTerm
      .trim()
      .toLowerCase();

    return this.users.filter(user => {

      // ----------------------------
      // RECHERCHE
      // ----------------------------

      const fullName =
        `${user.firstName ?? ''} ${user.lastName ?? ''}`
          .toLowerCase();

      const username =
        (user.username ?? '').toLowerCase();

      const email =
        (user.email ?? '').toLowerCase();


      const matchesSearch =
        !search ||
        username.includes(search) ||
        fullName.includes(search) ||
        email.includes(search);

      // ----------------------------
      // FILTRE ROLE
      // ----------------------------

      const role =
        (user.role ?? '').toUpperCase();

      const matchesRole =
        this.roleFilter === 'ALL' ||
        role === this.roleFilter;

      // ----------------------------
      // FILTRE STATUT
      // ----------------------------

      const isActive =
        user.enabled !== false;

      const matchesStatus =
        this.statusFilter === 'ALL' ||
        (this.statusFilter === 'ACTIVE' && isActive) ||
        (this.statusFilter === 'INACTIVE' && !isActive);

      return (
        matchesSearch &&
        matchesRole &&
        matchesStatus
      );
    });
  }

  // ============================
  // CHANGEMENT RECHERCHE
  // ============================

  onSearchChange(): void {
    // Le getter filteredUsers
    // recalcule automatiquement la liste.
  }

  // ============================
  // CHANGEMENT ROLE
  // ============================

  onRoleChange(): void {
    // Le getter filteredUsers
    // recalcule automatiquement la liste.
  }

  // ============================
  // CHANGEMENT STATUT
  // ============================

  onStatusChange(): void {
    // Le getter filteredUsers
    // recalcule automatiquement la liste.
  }

  // ============================
  // RESET
  // ============================

  clearFilters(): void {

    this.searchTerm = '';

    this.roleFilter = 'ALL';

    this.statusFilter = 'ALL';

    this.cdr.detectChanges();
  }

  // ============================
  // ACTUALISATION
  // ============================

  refresh(): void {
    this.loadUsers();
  }

  openCreateForm(): void {

    this.showCreateForm = true;

    this.successMessage = '';

    this.createErrorMessage = '';

    this.resetNewUser();
  }

  closeCreateForm(): void {

    if (this.saving) {
      return;
    }

    this.showCreateForm = false;

    this.createErrorMessage = '';
  }

  resetNewUser(): void {

    this.newUser = {
      username: '',
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      enabled: true,
    };

    this.selectedRoleId = null;
  }


  createUser(): void {

    this.createErrorMessage = '';

    this.successMessage = '';

    // ============================
    // VALIDATION
    // ============================

    if (!this.newUser.username?.trim()) {

      this.createErrorMessage =
        'Le nom d’utilisateur est obligatoire.';

      return;
    }

    if (!this.newUser.firstName?.trim()) {

      this.createErrorMessage =
        'Le prénom est obligatoire.';

      return;
    }

    if (!this.newUser.lastName?.trim()) {

      this.createErrorMessage =
        'Le nom est obligatoire.';

      return;
    }

    if (!this.newUser.email?.trim()) {

      this.createErrorMessage =
        'L’adresse email est obligatoire.';

      return;
    }

    if (!this.newUser.password?.trim()) {

      this.createErrorMessage =
        'Le mot de passe est obligatoire.';

      return;
    }

    if (!this.selectedRoleId) {

      this.createErrorMessage =
        'Veuillez sélectionner un rôle.';

      return;
    }

    // ============================
    // ENREGISTREMENT
    // ============================

    this.saving = true;

    this.userService
      .create(
        this.newUser,
        this.selectedRoleId
      )
      .subscribe({

        next: (createdUser) => {

          console.log(
            'Utilisateur créé :',
            createdUser
          );

          this.saving = false;

          this.showCreateForm = false;

          this.successMessage =
            'Utilisateur créé avec succès.';

          this.loadUsers();

          this.resetNewUser();
        },

        error: (error) => {

          console.error(
            'Erreur création utilisateur :',
            error
          );

          this.saving = false;

          this.createErrorMessage =
            this.extractErrorMessage(error);
        }

      });
  }


  private extractErrorMessage(error: any): string {

    if (error?.error?.message) {
      return error.error.message;
    }

    if (typeof error?.error === 'string') {
      return error.error;
    }

    if (error?.message) {
      return error.message;
    }

    return 'Impossible de créer l’utilisateur.';
  }



  openEditForm(user: User): void {

    if (!user.id) {
      return;
    }

    this.editingUserId = user.id;

    this.editErrorMessage = '';

    this.successMessage = '';

    this.editUser = {
      id: user.id,
      username: user.username,
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      enabled: user.enabled,
      role: user.role ?? ''
    };

    this.editRoleId = this.getRoleId(user.role);

    this.showEditForm = true;
  }

  private getRoleId(role?: string): number | null {

    if (!role) {
      return null;
    }

    const roleIds: Record<string, number> = {
      ADMIN: 1,
      MANAGER: 2,
      CAISSIER: 3,
      STOCK: 4
    };

    return roleIds[role.toUpperCase()] ?? null;
  }

  closeEditForm(): void {

    if (this.editSaving) {
      return;
    }

    this.showEditForm = false;

    this.editingUserId = null;

    this.editErrorMessage = '';
  }



  updateUser(): void {

    this.editErrorMessage = '';

    if (!this.editingUserId) {

      this.editErrorMessage =
        'Utilisateur invalide.';

      return;
    }

    if (!this.editUser.username.trim()) {

      this.editErrorMessage =
        'Le nom d’utilisateur est obligatoire.';

      return;
    }

    if (!this.editUser.firstName.trim()) {

      this.editErrorMessage =
        'Le prénom est obligatoire.';

      return;
    }

    if (!this.editUser.lastName.trim()) {

      this.editErrorMessage =
        'Le nom est obligatoire.';

      return;
    }

    if (!this.editUser.email.trim()) {

      this.editErrorMessage =
        'L’adresse email est obligatoire.';

      return;
    }

    if (
      this.editRoleId === null ||
      this.editRoleId === undefined
    ) {

      this.editErrorMessage =
        'Veuillez sélectionner un rôle.';

      return;
    }

    const userToUpdate: UpdateUserRequest = {

      username: this.editUser.username.trim(),

      firstName: this.editUser.firstName.trim(),

      lastName: this.editUser.lastName.trim(),

      email: this.editUser.email.trim(),

      enabled: this.editUser.enabled

    };

    console.log(
      'JSON envoyé :',
      userToUpdate
    );

    console.log(
      'Role ID :',
      this.editRoleId
    );

    this.editSaving = true;

    this.userService
      .update(
        this.editingUserId,
        userToUpdate,
        this.editRoleId
      )
      .subscribe({

        next: (updatedUser) => {

          console.log(
            'Utilisateur modifié :',
            updatedUser
          );

          this.editSaving = false;

          this.showEditForm = false;

          this.editingUserId = null;

          this.successMessage =
            'Utilisateur modifié avec succès.';

          this.loadUsers();
        },

        error: (error) => {

          console.error(
            'Erreur modification :',
            error
          );

          this.editSaving = false;

          this.editErrorMessage =
            this.extractErrorMessage(error);
        }

      });
  }

}