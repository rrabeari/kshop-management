import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {

  const authService = inject(AuthService);
  const router = inject(Router);

  /*
   * Vérifie si un token JWT existe.
   */
  if (authService.isAuthenticated()) {
    return true;
  }

  /*
   * Aucun token :
   * redirection vers la page de connexion.
   */
  return router.createUrlTree(['/login']);
};