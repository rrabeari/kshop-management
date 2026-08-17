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