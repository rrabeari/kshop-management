import { HttpInterceptorFn } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  /*
   * Récupération du JWT.
   */
  const token = authService.getToken();

  /*
   * Si aucun token n'existe,
   * on envoie la requête normalement.
   */
  const authReq = token
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      })
    : req;

  return next(authReq).pipe(

    catchError((error: HttpErrorResponse) => {

      /*
       * =========================================================
       * 401 UNAUTHORIZED
       * =========================================================
       *
       * Le serveur indique que l'utilisateur
       * n'est pas correctement authentifié.
       */
      if (error.status === 401) {

        /*
         * Suppression du JWT local.
         */
        authService.logout();

        /*
         * Retour vers la page de connexion.
         */
        router.navigate(['/login']);

        return throwError(() => error);
      }


      /*
       * =========================================================
       * 403 FORBIDDEN
       * =========================================================
       *
       * L'utilisateur est authentifié,
       * mais son rôle ne permet pas d'effectuer
       * l'opération demandée.
       */
      if (error.status === 403) {

        console.warn(
          'Accès refusé : permissions insuffisantes.'
        );

        return throwError(() => error);
      }


      /*
       * =========================================================
       * AUTRES ERREURS
       * =========================================================
       *
       * On laisse l'erreur continuer vers
       * le service ou le composant appelant.
       */
      return throwError(() => error);
    })
  );
};