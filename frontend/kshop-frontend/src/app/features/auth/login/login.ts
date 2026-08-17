import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/login-request.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  username = '';
  password = '';

  loading = false;
  errorMessage = '';

  /**
   * Soumission du formulaire.
   */
  onSubmit(): void {

    this.errorMessage = '';

    /*
     * Vérification simple.
     */
    if (!this.username || !this.password) {

      this.errorMessage =
        'Veuillez saisir votre nom d’utilisateur et votre mot de passe.';

      return;
    }

    const credentials: LoginRequest = {
      username: this.username,
      password: this.password
    };

    this.loading = true;

    this.authService.login(credentials).subscribe({

      next: response => {

        console.log('Connexion réussie');
        console.log('Utilisateur :', response.username);
        console.log('Rôle :', response.role);

        this.loading = false;

        /*
         * Redirection vers le dashboard.
         */
        this.router.navigate(['/dashboard']);
      },

      error: error => {

        console.error(
          'Erreur de connexion :',
          error
        );

        this.loading = false;

        if (error.status === 401) {

          this.errorMessage =
            'Nom d’utilisateur ou mot de passe incorrect.';

        } else if (error.status === 403) {

          this.errorMessage =
            'Accès refusé.';

        } else {

          this.errorMessage =
            'Impossible de contacter le serveur.';
        }
      }
    });
  }
}