import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import { LoginRequest } from '../models/login-request.model';
import { LoginResponse } from '../models/login-response.model';
import { API_CONFIG } from './api.config';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly http = inject(HttpClient);

  private readonly tokenKey = 'kshop_token';
  private readonly usernameKey = 'kshop_username';
  private readonly roleKey = 'kshop_role';

  /**
   * Connexion.
   *
   * Réponse attendue :
   *
   * {
   *   "token": "...",
   *   "username": "admin",
   *   "role": "ADMIN"
   * }
   */
  login(credentials: LoginRequest): Observable<LoginResponse> {

    return this.http
      .post<LoginResponse>(
        `${API_CONFIG.baseUrl}/auth/login`,
        credentials
      )
      .pipe(
        tap(response => {

          /*
           * JWT
           */
          localStorage.setItem(
            this.tokenKey,
            response.token
          );

          /*
           * Username
           */
          localStorage.setItem(
            this.usernameKey,
            response.username
          );

          /*
           * Role
           */
          localStorage.setItem(
            this.roleKey,
            response.role
          );
        })
      );
  }

  /**
   * JWT.
   */
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  /**
   * Username.
   */
  getUsername(): string | null {
    return localStorage.getItem(this.usernameKey);
  }

  /**
   * Role.
   */
  getRole(): string | null {
    return localStorage.getItem(this.roleKey);
  }

  /**
   * Vérifie si un JWT est présent.
   *
   * Cette méthode ne fait aucun appel HTTP.
   */
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  /**
   * Vérifie si l'utilisateur possède l'un des rôles indiqués.
   */
  hasRole(...roles: string[]): boolean {

    const currentRole = this.getRole();

    if (!currentRole) {
      return false;
    }

    return roles.includes(currentRole);
  }





  /**
   * Déconnexion.
   */
  logout(): void {

    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.usernameKey);
    localStorage.removeItem(this.roleKey);
  }
}