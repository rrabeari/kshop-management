import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  StockMovement
} from '../models/stock-movement.model';

import { API_CONFIG } from './api.config';
import { StockMovementRequest } from '../models/stock-movement-request.model';

/**
 * Service Angular permettant de communiquer
 * avec l'API REST des mouvements de stock.
 *
 * Endpoints actuellement supportés par le backend :
 *
 * GET    /api/stock-movements
 * GET    /api/stock-movements/{id}
 * GET    /api/stock-movements/product/{productId}
 * GET    /api/stock-movements/user/{userId}
 * GET    /api/stock-movements/type/{movementType}
 * GET    /api/stock-movements/product/{productId}/type/{movementType}
 * POST   /api/stock-movements?productId={productId}
 * DELETE /api/stock-movements/{id}
 *
 * IMPORTANT :
 *
 * Le frontend ne fournit jamais userId lors
 * de la création d'un mouvement.
 *
 * Le backend récupère automatiquement
 * l'utilisateur connecté grâce au JWT.
 *
 * Le backend reste également la source de vérité
 * pour la modification réelle du stock.
 */
@Injectable({
  providedIn: 'root'
})
export class StockMovementService {

  /**
   * Client HTTP Angular.
   */
  private readonly http = inject(HttpClient);

  /**
   * URL principale de l'API Stock Movement.
   *
   * Exemple :
   * http://localhost:8080/api/stock-movements
   */
  private readonly apiUrl =
    `${API_CONFIG.baseUrl}/stock-movements`;

  // ============================================================
  // GET ALL
  // ============================================================

  /**
   * Récupère tous les mouvements de stock.
   *
   * GET /api/stock-movements
   *
   * @returns liste des mouvements
   */
  findAll(): Observable<StockMovement[]> {

    return this.http.get<StockMovement[]>(
      this.apiUrl
    );
  }

  // ============================================================
  // GET BY ID
  // ============================================================

  /**
   * Récupère un mouvement par son identifiant.
   *
   * GET /api/stock-movements/{id}
   *
   * @param id identifiant du mouvement
   * @returns mouvement demandé
   */
  findById(id: number): Observable<StockMovement> {

    return this.http.get<StockMovement>(
      `${this.apiUrl}/${id}`
    );
  }

  // ============================================================
  // GET BY PRODUCT
  // ============================================================

  /**
   * Récupère tous les mouvements d'un produit.
   *
   * GET /api/stock-movements/product/{productId}
   *
   * @param productId identifiant du produit
   * @returns mouvements associés au produit
   */
  findByProduct(
    productId: number
  ): Observable<StockMovement[]> {

    return this.http.get<StockMovement[]>(
      `${this.apiUrl}/product/${productId}`
    );
  }

  // ============================================================
  // GET BY USER
  // ============================================================

  /**
   * Récupère les mouvements effectués par un utilisateur.
   *
   * GET /api/stock-movements/user/{userId}
   *
   * IMPORTANT :
   *
   * Les permissions sont contrôlées par le backend.
   *
   * ADMIN et MANAGER peuvent consulter
   * les mouvements d'autres utilisateurs.
   *
   * STOCK peut uniquement consulter
   * ses propres mouvements.
   *
   * @param userId identifiant de l'utilisateur
   * @returns mouvements effectués par l'utilisateur
   */
  findByUser(
    userId: number
  ): Observable<StockMovement[]> {

    return this.http.get<StockMovement[]>(
      `${this.apiUrl}/user/${userId}`
    );
  }

  // ============================================================
  // GET BY TYPE
  // ============================================================

  /**
   * Récupère les mouvements selon leur type.
   *
   * GET /api/stock-movements/type/{movementType}
   *
   * Types actuellement supportés :
   *
   * ENTREE
   * SORTIE
   * AJUSTEMENT
   *
   * @param movementType type du mouvement
   * @returns mouvements correspondant au type
   */
  findByType(
    movementType: string
  ): Observable<StockMovement[]> {

    return this.http.get<StockMovement[]>(
      `${this.apiUrl}/type/${movementType}`
    );
  }

  // ============================================================
  // GET BY PRODUCT + TYPE
  // ============================================================

  /**
   * Récupère les mouvements d'un produit
   * selon leur type.
   *
   * GET /api/stock-movements/product/{productId}/type/{movementType}
   *
   * @param productId identifiant du produit
   * @param movementType type du mouvement
   * @returns mouvements correspondant aux critères
   */
  findByProductAndType(
    productId: number,
    movementType: string
  ): Observable<StockMovement[]> {

    return this.http.get<StockMovement[]>(
      `${this.apiUrl}/product/${productId}/type/${movementType}`
    );
  }

  // ============================================================
  // CREATE
  // ============================================================

  /**
   * Crée un nouveau mouvement de stock.
   *
   * POST /api/stock-movements?productId={productId}
   *
   * Exemple de données envoyées :
   *
   * {
   *   "movementType": "ENTREE",
   *   "quantity": 10,
   *   "reason": "Réapprovisionnement"
   * }
   *
   * IMPORTANT :
   *
   * Le frontend n'envoie PAS :
   *
   * - userId
   * - user
   * - movementDate
   * - product
   *
   * Le backend récupère automatiquement
   * l'utilisateur connecté depuis le JWT
   * et met à jour le stock du produit.
   *
   * @param productId identifiant du produit
   * @param request données du mouvement
   * @returns mouvement créé
   */
  create(
    productId: number,
    request: StockMovementRequest
  ): Observable<StockMovement> {

    return this.http.post<StockMovement>(
      `${this.apiUrl}?productId=${productId}`,
      request
    );
  }

  // ============================================================
  // DELETE
  // ============================================================

  /**
   * Supprime un mouvement de stock.
   *
   * DELETE /api/stock-movements/{id}
   *
   * IMPORTANT :
   *
   * Le backend autorise cette opération
   * uniquement au rôle ADMIN.
   *
   * @param id identifiant du mouvement
   * @returns Observable sans contenu
   */
  delete(id: number): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}