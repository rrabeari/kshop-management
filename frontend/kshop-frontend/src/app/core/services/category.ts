import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Category } from '../models/category.model';
import { API_CONFIG } from './api.config';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =
    `${API_CONFIG.baseUrl}/categories`;


  /**
   * Récupère toutes les catégories.
   */
  findAll(): Observable<Category[]> {

    return this.http.get<Category[]>(
      this.apiUrl
    );
  }


  /**
   * Récupère une catégorie par son ID.
   */
  findById(id: number): Observable<Category> {

    return this.http.get<Category>(
      `${this.apiUrl}/${id}`
    );
  }


  /**
   * Crée une catégorie.
   */
  create(category: Partial<Category>): Observable<Category> {

    return this.http.post<Category>(
      this.apiUrl,
      category
    );
  }


  /**
   * Modifie une catégorie.
   */
  update(
    id: number,
    category: Partial<Category>
  ): Observable<Category> {

    return this.http.put<Category>(
      `${this.apiUrl}/${id}`,
      category
    );
  }


  /**
   * Supprime une catégorie.
   */
  delete(id: number): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}