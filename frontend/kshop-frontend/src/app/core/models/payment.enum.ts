export enum PaymentMethod {
  ESPECES = 'ESPECES',
  CARTE_BANCAIRE = 'CARTE_BANCAIRE',
  CHEQUE = 'CHEQUE',
  VIREMENT = 'VIREMENT',
  MOBILE_MONEY = 'MOBILE_MONEY',
  CREDIT = 'CREDIT'
}

export const PaymentMethodLabels: Record<PaymentMethod, string> = {
  [PaymentMethod.ESPECES]: 'Espèces',
  [PaymentMethod.CARTE_BANCAIRE]: 'Carte Bancaire',
  [PaymentMethod.CHEQUE]: 'Chèque',
  [PaymentMethod.VIREMENT]: 'Virement',
  [PaymentMethod.MOBILE_MONEY]: 'Mobile Money',
  [PaymentMethod.CREDIT]: 'Crédit'
};

export const PaymentMethodColors: Record<PaymentMethod, string> = {
  [PaymentMethod.ESPECES]: '#28a745',      // Vert
  [PaymentMethod.CARTE_BANCAIRE]: '#007bff', // Bleu
  [PaymentMethod.CHEQUE]: '#6f42c1',        // Violet
  [PaymentMethod.VIREMENT]: '#fd7e14',      // Orange
  [PaymentMethod.MOBILE_MONEY]: '#20c997',  // Turquoise
  [PaymentMethod.CREDIT]: '#dc3545'         // Rouge
};

export enum PaymentStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  REFUNDED = 'REFUNDED'
}

export const PaymentStatusLabels: Record<PaymentStatus, string> = {
  [PaymentStatus.PENDING]: 'En attente',
  [PaymentStatus.PAID]: 'Payé',
  [PaymentStatus.REFUNDED]: 'Remboursé'
};

export const PaymentStatusColors: Record<PaymentStatus, string> = {
  [PaymentStatus.PENDING]: 'warning',
  [PaymentStatus.PAID]: 'success',
  [PaymentStatus.REFUNDED]: 'danger'
};

export const PaymentStatusIcons: Record<PaymentStatus, string> = {
  [PaymentStatus.PENDING]: 'bi-clock-history',
  [PaymentStatus.PAID]: 'bi-check-circle',
  [PaymentStatus.REFUNDED]: 'bi-arrow-counterclockwise'
};

export const PaymentMethodIcons: Record<PaymentMethod, string> = {
  [PaymentMethod.ESPECES]: 'bi-cash',
  [PaymentMethod.CARTE_BANCAIRE]: 'bi-credit-card',
  [PaymentMethod.CHEQUE]: 'bi-receipt',
  [PaymentMethod.VIREMENT]: 'bi-bank',
  [PaymentMethod.MOBILE_MONEY]: 'bi-phone',
  [PaymentMethod.CREDIT]: 'bi-clock'
};