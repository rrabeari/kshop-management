import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { CreateUserRequest, UpdateUserRequest, User } from '../models/user.model';
import { API_CONFIG } from './api.config';



@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =
    `${API_CONFIG.baseUrl}/users`;

  /**
   * Récupère tous les utilisateurs.
   */
  findAll(): Observable<User[]> {

    return this.http.get<User[]>(
      this.apiUrl
    );
  }

  /**
   * Récupère un utilisateur par son ID.
   */
  findById(id: number): Observable<User> {

    return this.http.get<User>(
      `${this.apiUrl}/${id}`
    );
  }

  /**
   * Crée un utilisateur.
   */
  create(
    user: CreateUserRequest,
    roleId: number
  ): Observable<User> {

    return this.http.post<User>(
      `${this.apiUrl}?roleId=${roleId}`,
      user
    );
  }

  /**
   * Modifie un utilisateur.
   */
  update(
    id: number,
    user: UpdateUserRequest,
    roleId?: number
  ): Observable<User> {

    let url = `${this.apiUrl}/${id}`;

    if (roleId !== undefined) {
      url += `?roleId=${roleId}`;
    }

    return this.http.put<User>(
      url,
      user
    );
  }

  /**
   * Supprime un utilisateur.
   */
  delete(id: number): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}