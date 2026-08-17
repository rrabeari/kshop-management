/**
 * Modèle utilisé pour envoyer une demande de création
 * d'un mouvement de stock vers l'API backend.
 *
 * Correspond au :
 * StockMovementRequestDTO
 *
 * Le backend attend uniquement :
 * - movementType
 * - quantity
 * - reason
 *
 * IMPORTANT :
 * Le productId n'est pas présent dans ce modèle.
 * Il est envoyé séparément par le service Angular
 * lors de l'appel POST.
 *
 * Le userId n'est également pas présent.
 * Le backend récupère automatiquement l'utilisateur
 * connecté à partir du JWT.
 */
export interface StockMovementRequest {

  /**
   * Type du mouvement de stock.
   *
   * Valeurs autorisées par le backend :
   * - ENTREE       : augmentation du stock
   * - SORTIE       : diminution du stock
   * - AJUSTEMENT   : remplacement du stock actuel
   */
  movementType: string;

  /**
   * Quantité concernée par le mouvement.
   *
   * La quantité doit être strictement supérieure à zéro.
   */
  quantity: number;

  /**
   * Motif du mouvement.
   *
   * Exemple :
   * - Réception de marchandise
   * - Vente
   * - Correction d'inventaire
   */
  reason: string;
}