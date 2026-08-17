import { Injectable, inject } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  Router
} from '@angular/router';

import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class roleGuard implements CanActivate {

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  canActivate(
    route: ActivatedRouteSnapshot
  ): boolean {

    /*
     * Récupération des rôles autorisés
     * configurés dans la route.
     *
     * Exemple :
     *
     * data: {
     *   roles: ['ADMIN', 'MANAGER']
     * }
     */
    const allowedRoles =
      route.data['roles'] as string[] | undefined;

    /*
     * Récupération du rôle enregistré
     * lors de la connexion.
     */
    const userRole =
      this.authService.getRole();

    /*
     * Aucun token :
     * l'utilisateur n'est pas connecté.
     */
    if (!this.authService.isAuthenticated()) {

      this.router.navigate(['/login']);

      return false;
    }

    /*
     * Si aucun rôle n'est défini sur la route,
     * on autorise l'accès.
     */
    if (!allowedRoles || allowedRoles.length === 0) {
      return true;
    }

    /*
     * Vérification du rôle.
     */
    if (
      userRole &&
      allowedRoles.includes(userRole)
    ) {

      return true;
    }

    /*
     * L'utilisateur est connecté,
     * mais son rôle n'est pas autorisé.
     */
    this.router.navigate(['/access-denied']);

    return false;
  }
}