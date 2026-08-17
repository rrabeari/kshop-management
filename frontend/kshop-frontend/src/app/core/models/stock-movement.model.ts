import { Product } from './product.model';
import { User } from './user.model';

/**
 * Modèle représentant un mouvement de stock retourné
 * par l'API backend.
 *
 * Correspond au :
 * StockMovementResponseDTO
 *
 * Backend :
 * - StockMovementResponseDTO
 *   ├── id
 *   ├── movementDate
 *   ├── movementType
 *   ├── quantity
 *   ├── reason
 *   ├── product
 *   └── user
 */
export interface StockMovement {

  /**
   * Identifiant unique du mouvement.
   *
   * Correspond à :
   * StockMovementResponseDTO.id
   */
  id: number;

  /**
   * Date et heure auxquelles le mouvement
   * a été enregistré.
   *
   * Le backend utilise LocalDateTime.
   * Angular reçoit cette valeur sous forme de chaîne
   * dans la réponse JSON.
   */
  movementDate: string;

  /**
   * Type du mouvement de stock.
   *
   * Valeurs actuellement autorisées par le backend :
   * - ENTREE
   * - SORTIE
   * - AJUSTEMENT
   */
  movementType: string;

  /**
   * Quantité concernée par le mouvement.
   *
   * Correspond au BigDecimal du backend.
   */
  quantity: number;

  /**
   * Motif ou raison du mouvement.
   *
   * Exemple :
   * - Réception marchandise
   * - Vente
   * - Correction d'inventaire
   */
  reason: string;

  /**
   * Produit concerné par le mouvement.
   *
   * Le backend retourne un ProductResponseDTO.
   * Angular utilise donc le modèle Product.
   */
  product: Product;

  /**
   * Utilisateur ayant effectué le mouvement.
   *
   * Le backend retourne un UserResponseDTO.
   * Le mot de passe n'est jamais transmis.
   */
  user: User;
}