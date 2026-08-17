import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Product } from '../models/product.model';
import { API_CONFIG } from './api.config';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =
    `${API_CONFIG.baseUrl}/products`;

  /**
   * Récupère tous les produits.
   */
  findAll(): Observable<Product[]> {

    return this.http.get<Product[]>(
      this.apiUrl
    );
  }

}