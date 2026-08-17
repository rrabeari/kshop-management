import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {

    private readonly authService = inject(AuthService);

    /**
     * Username de l'utilisateur connecté.
     */
    get username(): string {
      return this.authService.getUsername() ?? '';
    }

    /**
     * Rôle de l'utilisateur connecté.
     */
    get role(): string {
    return this.authService.getRole() ?? '';
    }

    /**
    * Déconnexion.
    */
      logout(): void {
        this.authService.logout();
        
      }

      /**
       * Vérifie si l'utilisateur possède un rôle.
       */
      hasRole(...roles: string[]): boolean {
        return this.authService.hasRole(...roles);
      }
}