import { Category } from "./category.model";


export interface Product {
  id: number;

  code: string;

  barcode: string;

  name: string;

  description: string;

  purchasePrice: number;

  sellingPrice: number;

  quantity: number;

  minimumStock: number;

  unit: string;

  active: boolean;

  category: Category;

  createdAt: string;

  updatedAt: string;
}

/**
 * Modèle représentant les données envoyées au serveur (Backend) 
 * lors de la création ou de la mise à jour d'un produit.
 * 
 * Il correspond au `ProductRequestDTO` côté Spring Boot.
 */
export interface ProductRequest {
  
  /** 
   * Code interne unique du produit (obligatoire). 
   */
  code: string;

  /** 
   * Code-barres du produit (optionnel mais doit être unique s'il est renseigné). 
   */
  barcode?: string;

  /** 
   * Nom ou libellé du produit (obligatoire). 
   */
  name: string;

  /** 
   * Description détaillée du produit (optionnel). 
   */
  description?: string;

  /** 
   * Prix d'achat du produit (obligatoire pour les calculs de marge). 
   */
  purchasePrice: number;

  /** 
   * Prix de vente du produit (obligatoire). 
   */
  sellingPrice: number;

  /** 
   * Quantité actuellement en stock (obligatoire). 
   */
  quantity: number;

  /** 
   * Seuil d'alerte pour le stock minimum (obligatoire). 
   */
  minimumStock: number;

  /** 
   * Unité de mesure (ex: 'kg', 'litre', 'pièce') (optionnel). 
   */
  unit?: string;

  /** 
   * Statut d'activation du produit. Par défaut à true côté backend si non précisé. 
   */
  active?: boolean;

  /** 
   * Identifiant de la catégorie à laquelle appartient le produit (obligatoire).
   * C'est ce champ qui permet au backend de lier le produit à sa catégorie.
   */
  categoryId: number;
}