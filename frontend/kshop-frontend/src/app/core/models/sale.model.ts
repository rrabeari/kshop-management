/**
 * Article d'une vente.
 */
export interface SaleItem {

  id: number;

  productCode: string;

  productId: number;

  productName: string;

  quantity: number;

  unitPrice: number;

  subtotal: number;
}


/**
 * Vente retournée par l'API.
 */
export interface Sale {

  id: number;

  saleDate: string;

  totalAmount: number;

  discount: number;

  status: 'COMPLETED' | 'CANCELLED';

  userId: number;

  username: string;

  firstName: string;

  lastName: string;

  role: string;

  items: SaleItem[];
}

/**
 * Ligne envoyée au backend lors de la création
 * d'une nouvelle vente.
 *
 * IMPORTANT :
 * Le frontend envoie uniquement :
 * - productId
 * - quantity
 *
 * Le prix est déterminé par le backend.
 */
export interface SaleItemRequest {

  /**
   * Identifiant du produit.
   */
  productId: number;

  /**
   * Quantité vendue.
   */
  quantity: number;
}


/**
 * Données envoyées au backend pour créer une vente.
 *
 * IMPORTANT :
 * Le frontend ne fournit pas :
 * - userId
 * - unitPrice
 * - subtotal
 * - totalAmount
 * - saleDate
 * - status
 *
 * Le backend calcule ces informations.
 */
export interface SaleRequest {

  /**
   * Remise globale.
   */
  discount: number;

  /**
   * Articles composant la vente.
   */
  items: SaleItemRequest[];
}