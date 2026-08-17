import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  Sale,
  SaleRequest
} from '../models/sale.model';

import { API_CONFIG } from './api.config';

/**
 * Service Angular permettant de communiquer
 * avec l'API REST des ventes.
 *
 * Endpoints utilisés :
 *
 * GET    /api/sales
 * GET    /api/sales/{id}
 * GET    /api/sales/user/{userId}
 * POST   /api/sales
 * PATCH  /api/sales/{id}/cancel
 *
 * IMPORTANT :
 * Le frontend ne fournit jamais userId
 * lors de la création ou de l'annulation.
 *
 * Le backend récupère l'utilisateur connecté
 * grâce au JWT.
 */
@Injectable({
  providedIn: 'root'
})
export class SaleService {

  /**
   * Client HTTP Angular.
   */
  private readonly http = inject(HttpClient);

  /**
   * URL principale de l'API Sales.
   *
   * Exemple :
   * http://localhost:8080/api/sales
   */
  private readonly apiUrl =
    `${API_CONFIG.baseUrl}/sales`;

  // ============================================================
  // GET ALL
  // ============================================================

  /**
   * Récupère toutes les ventes.
   *
   * GET /api/sales
   *
   * Utilisé notamment par l'historique des ventes.
   */
  findAll(): Observable<Sale[]> {

    return this.http.get<Sale[]>(
      this.apiUrl
    );
  }

  // ============================================================
  // GET BY ID
  // ============================================================

  /**
   * Récupère une vente par son identifiant.
   *
   * GET /api/sales/{id}
   *
   * @param id identifiant de la vente
   */
  findById(id: number): Observable<Sale> {

    return this.http.get<Sale>(
      `${this.apiUrl}/${id}`
    );
  }

  // ============================================================
  // GET BY USER
  // ============================================================

  /**
   * Récupère les ventes d'un utilisateur.
   *
   * GET /api/sales/user/{userId}
   *
   * IMPORTANT :
   * Le backend contrôle les permissions.
   *
   * ADMIN et MANAGER peuvent consulter
   * les ventes d'un utilisateur.
   *
   * CAISSIER peut uniquement consulter
   * ses propres ventes selon la règle
   * définie dans SaleController.
   *
   * @param userId identifiant de l'utilisateur
   */
  findByUserId(userId: number): Observable<Sale[]> {

    return this.http.get<Sale[]>(
      `${this.apiUrl}/user/${userId}`
    );
  }

  // ============================================================
  // CREATE
  // ============================================================

  /**
   * Crée une nouvelle vente.
   *
   * POST /api/sales
   *
   * Exemple de données envoyées :
   *
   * {
   *   "discount": 500,
   *   "items": [
   *     {
   *       "productId": 2,
   *       "quantity": 2
   *     }
   *   ]
   * }
   *
   * IMPORTANT :
   *
   * Le frontend n'envoie PAS :
   *
   * - userId
   * - username
   * - unitPrice
   * - subtotal
   * - totalAmount
   * - saleDate
   * - status
   *
   * Le backend détermine ces informations.
   *
   * @param request données nécessaires à la création
   */
  create(request: SaleRequest): Observable<Sale> {

    return this.http.post<Sale>(
      this.apiUrl,
      request
    );
  }

  // ============================================================
  // CANCEL
  // ============================================================

  /**
   * Annule une vente.
   *
   * PATCH /api/sales/{id}/cancel
   *
   * IMPORTANT :
   *
   * Aucun userId n'est envoyé.
   *
   * Le backend récupère automatiquement
   * l'utilisateur connecté depuis le JWT.
   *
   * Le backend :
   *
   * - vérifie la vente ;
   * - vérifie les permissions ;
   * - change le statut ;
   * - restaure le stock ;
   * - crée le mouvement ENTREE.
   *
   * @param id identifiant de la vente
   */
  cancel(id: number): Observable<Sale> {

    return this.http.patch<Sale>(
      `${this.apiUrl}/${id}/cancel`,
      {}
    );
  }

  // ============================================================
  // REFRESH
  // ============================================================

  /**
   * Alias pratique pour recharger toutes les ventes.
   *
   * Cette méthode permet au composant de demander
   * explicitement un rafraîchissement de la liste.
   */
  refresh(): Observable<Sale[]> {

    return this.findAll();
  }
}