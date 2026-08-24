import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Product, ProductRequest } from '../models/product.model';
import { API_CONFIG } from './api.config';

/**
 * Service Angular gérant toutes les opérations CRUD et les requêtes HTTP 
 * en lien avec l'API REST des produits (/api/products).
 */
@Injectable({
  providedIn: 'root'
})
export class ProductService {

  /** Instance du client HTTP injectée via la nouvelle fonction inject() */
  private readonly http = inject(HttpClient);

  /** URL de base pour l'API des produits, construite dynamiquement depuis la configuration */
  private readonly apiUrl = `${API_CONFIG.baseUrl}/products`;

  /**
   * Récupère la liste de tous les produits enregistrés dans le système.
   * 
   * @returns Un Observable contenant un tableau d'objets ProductResponseDTO (mappés en Product)
   */
  findAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  /**
   * Récupère les détails d'un produit spécifique à l'aide de son identifiant unique.
   * 
   * @param id L'identifiant numérique du produit recherché
   * @returns Un Observable contenant le produit correspondant
   */
  findById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  /**
   * Envoie une requête POST pour créer un nouveau produit dans la base de données.
   * 
   * @param product Les données du formulaire sous forme de ProductRequest (correspondant au ProductRequestDTO du backend)
   * @returns Un Observable contenant le produit créé et retourné par le serveur (ProductResponseDTO)
   */
  create(product: ProductRequest): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  /**
   * Met à jour les informations d'un produit existant via une requête PUT.
   * 
   * @param id L'identifiant du produit à modifier
   * @param product Les nouvelles données du produit sous forme de ProductRequest
   * @returns Un Observable contenant le produit mis à jour
   */
  update(id: number, product: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product);
  }

  /**
   * Désactive un produit (passe son statut 'active' à false) via une requête PATCH.
   * 
   * @param id L'identifiant du produit à désactiver
   * @returns Un Observable contenant le produit mis à jour
   */
  deactivate(id: number): Observable<Product> {
    return this.http.patch<Product>(`${this.apiUrl}/${id}/deactivate`, {});
  }

  /**
   * Réactive un produit (passe son statut 'active' à true) via une requête PATCH.
   * 
   * @param id L'identifiant du produit à réactiver
   * @returns Un Observable contenant le produit mis à jour
   */
  activate(id: number): Observable<Product> {
    return this.http.patch<Product>(`${this.apiUrl}/${id}/activate`, {});
  }

  /**
   * Supprime définitivement un produit de la base de données via une requête DELETE.
   * 
   * @param id L'identifiant du produit à supprimer
   * @returns Un Observable vide (Void) une fois la suppression confirmée
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}