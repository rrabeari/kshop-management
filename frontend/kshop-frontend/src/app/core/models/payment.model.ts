export type PaymentMethod = 'CASH' | 'MOBILE_MONEY' | 'CREDIT_CARD' | 'BANK_TRANSFER';
export type PaymentStatus = 'COMPLETED' | 'PENDING' | 'FAILED';

export interface Payment {
  id: number;
  saleId: number;
  amount: number;
  method: PaymentMethod;
  status: PaymentStatus;
  transactionReference?: string;
  userId: number;
  createdAt: string;
}

export interface PaymentRequest {
  saleId: number;
  amount: number;
  paymentMethod: PaymentMethod;
  transactionReference?: string;
}

export interface PaymentStatistics {
  totalAmount: number;
  totalTransactions: number;
  amountByMethod: Record<PaymentMethod, number>;
  countByStatus: Record<PaymentStatus, number>;
}