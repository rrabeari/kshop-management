

// ============================================
// CONSTANTES PRINCIPALES
// ============================================

import { PaymentMethod, PaymentStatus } from "./payment.enum";

export const PAYMENT_CONSTANTS = {
    
  // ==========================================
  // MÉTHODES AUTORISÉES
  // ==========================================
  ALLOWED_METHODS: [
    PaymentMethod.ESPECES,
    PaymentMethod.CARTE_BANCAIRE,
    PaymentMethod.CHEQUE,
    PaymentMethod.VIREMENT,
    PaymentMethod.MOBILE_MONEY,
    PaymentMethod.CREDIT
  ] as PaymentMethod[],

  // ==========================================
  // STATUTS AUTORISÉS
  // ==========================================
  ALLOWED_STATUSES: [
    PaymentStatus.PENDING,
    PaymentStatus.PAID,
    PaymentStatus.REFUNDED
  ] as PaymentStatus[],

  // ==========================================
  // SEUILS ET LIMITES
  // ==========================================
  MIN_PAYMENT_AMOUNT: 0.01,
  MAX_PAYMENT_AMOUNT: 9999999.99,
  DEFAULT_DISCOUNT: 0,

  // ==========================================
  // FORMATS DE DATE
  // ==========================================
  DATE_FORMAT: 'YYYY-MM-DD',
  TIME_FORMAT: 'HH:mm:ss',
  DATETIME_FORMAT: 'YYYY-MM-DDTHH:mm:ss',
  DISPLAY_DATE_FORMAT: 'dd/MM/yyyy',
  DISPLAY_DATETIME_FORMAT: 'dd/MM/yyyy HH:mm',

  // ==========================================
  // PAGINATION
  // ==========================================
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZE_OPTIONS: [5, 10, 20, 50, 100],
  MAX_PAGE_SIZE: 100,

  // ==========================================
  // DASHBOARD
  // ==========================================
  REFRESH_INTERVAL: 30, // secondes
  RECENT_PAYMENTS_LIMIT: 10,
  TIME_SERIES_DAYS: 7,
  TIME_SERIES_WEEKS: 4,
  TIME_SERIES_MONTHS: 12,

  // ==========================================
  // CACHE
  // ==========================================
  CACHE_DURATION: 60000, // 1 minute en millisecondes
  STATISTICS_CACHE_DURATION: 120000, // 2 minutes

  // ==========================================
  // EXPORT
  // ==========================================
  EXPORT_COLUMNS: [
    { key: 'id', label: 'ID' },
    { key: 'amount', label: 'Montant' },
    { key: 'paymentMethodLabel', label: 'Méthode' },
    { key: 'statusLabel', label: 'Statut' },
    { key: 'paymentDate', label: 'Date' },
    { key: 'userFullName', label: 'Caissier' },
    { key: 'saleId', label: 'Vente ID' },
    { key: 'saleTotal', label: 'Total Vente' },
    { key: 'reference', label: 'Référence' },
    { key: 'comment', label: 'Commentaire' }
  ],

  // ==========================================
  // TYPES DE PÉRIODES
  // ==========================================
  PERIODS: {
    TODAY: 'today',
    WEEK: 'week',
    MONTH: 'month',
    YEAR: 'year',
    CUSTOM: 'custom'
  } as const,

  // ==========================================
  // FILTRES PAR DÉFAUT
  // ==========================================
  DEFAULT_FILTER: {
    status: null,
    paymentMethod: null,
    startDate: null,
    endDate: null,
    userId: null,
    saleId: null,
    minAmount: null,
    maxAmount: null,
    reference: null
  },

  // ==========================================
  // COULEURS (pour graphiques)
  // ==========================================
  CHART_COLORS: {
    [PaymentMethod.ESPECES]: '#28a745',
    [PaymentMethod.CARTE_BANCAIRE]: '#007bff',
    [PaymentMethod.CHEQUE]: '#6f42c1',
    [PaymentMethod.VIREMENT]: '#fd7e14',
    [PaymentMethod.MOBILE_MONEY]: '#20c997',
    [PaymentMethod.CREDIT]: '#dc3545',
    [PaymentStatus.PENDING]: '#ffc107',
    [PaymentStatus.PAID]: '#198754',
    [PaymentStatus.REFUNDED]: '#dc3545'
  },

  // ==========================================
  // ICÔNES
  // ==========================================
  ICONS: {
    [PaymentMethod.ESPECES]: 'bi-cash',
    [PaymentMethod.CARTE_BANCAIRE]: 'bi-credit-card',
    [PaymentMethod.CHEQUE]: 'bi-receipt',
    [PaymentMethod.VIREMENT]: 'bi-bank',
    [PaymentMethod.MOBILE_MONEY]: 'bi-phone',
    [PaymentMethod.CREDIT]: 'bi-clock',
    [PaymentStatus.PENDING]: 'bi-clock-history',
    [PaymentStatus.PAID]: 'bi-check-circle',
    [PaymentStatus.REFUNDED]: 'bi-arrow-counterclockwise'
  }
};

// ============================================
// MESSAGES D'ERREUR ET INFORMATIONS
// ============================================

export const PAYMENT_MESSAGES = {
  // ==========================================
  // SUCCÈS
  // ==========================================
  CREATE_SUCCESS: 'Paiement créé avec succès',
  CANCEL_SUCCESS: 'Paiement annulé avec succès',
  REFUND_SUCCESS: 'Paiement remboursé avec succès',
  UPDATE_SUCCESS: 'Paiement mis à jour avec succès',
  DELETE_SUCCESS: 'Paiement supprimé avec succès',

  // ==========================================
  // ERREURS - VALIDATION
  // ==========================================
  INVALID_AMOUNT: 'Le montant doit être supérieur à 0',
  AMOUNT_EXCEEDS_REMAINING: 'Le montant dépasse le reste à payer',
  AMOUNT_TOO_HIGH: 'Le montant est trop élevé',
  AMOUNT_TOO_LOW: 'Le montant est trop bas',
  INVALID_METHOD: 'Méthode de paiement invalide',
  INVALID_STATUS: 'Statut de paiement invalide',
  INVALID_DATE: 'Date de paiement invalide',
  REFERENCE_REQUIRED: 'La référence est obligatoire pour cette méthode',
  COMMENT_TOO_LONG: 'Le commentaire est trop long (maximum 500 caractères)',

  // ==========================================
  // ERREURS - RESSOURCES
  // ==========================================
  SALE_NOT_FOUND: 'Vente introuvable',
  PAYMENT_NOT_FOUND: 'Paiement introuvable',
  USER_NOT_FOUND: 'Utilisateur introuvable',
  METHOD_NOT_FOUND: 'Méthode de paiement non trouvée',

  // ==========================================
  // ERREURS - ÉTAT
  // ==========================================
  SALE_ALREADY_PAID: 'Cette vente est déjà entièrement payée',
  SALE_CANCELLED: 'Impossible de payer une vente annulée',
  ALREADY_REFUNDED: 'Ce paiement est déjà remboursé',
  ALREADY_CANCELLED: 'Ce paiement est déjà annulé',
  PAYMENT_PENDING: 'Ce paiement est en attente et ne peut pas être modifié',
  PAYMENT_PAID: 'Ce paiement est déjà payé',
  NOT_PAID: 'Seuls les paiements payés peuvent être remboursés',

  // ==========================================
  // ERREURS - PERMISSIONS
  // ==========================================
  NOT_AUTHORIZED: 'Vous n\'êtes pas autorisé à effectuer cette action',
  NOT_AUTHORIZED_VIEW: 'Vous n\'êtes pas autorisé à consulter ce paiement',
  NOT_AUTHORIZED_CREATE: 'Vous n\'êtes pas autorisé à créer des paiements',
  NOT_AUTHORIZED_CANCEL: 'Vous n\'êtes pas autorisé à annuler ce paiement',

  // ==========================================
  // ERREURS - TECHNIQUES
  // ==========================================
  NETWORK_ERROR: 'Erreur de connexion au serveur',
  SERVER_ERROR: 'Erreur interne du serveur',
  TIMEOUT_ERROR: 'La requête a expiré',
  UNKNOWN_ERROR: 'Une erreur inconnue est survenue',

  // ==========================================
  // INFORMATIONS
  // ==========================================
  NO_PAYMENTS: 'Aucun paiement trouvé',
  NO_PAYMENTS_FILTER: 'Aucun paiement ne correspond à vos filtres',
  LOADING: 'Chargement des paiements...',
  LOADING_STATS: 'Chargement des statistiques...',
  LOADING_DETAILS: 'Chargement des détails du paiement...',
  UPDATED_AT: 'Dernière mise à jour',
  TOTAL_PAID: 'Total payé',
  REMAINING: 'Reste à payer',

  // ==========================================
  // CONFIRMATIONS
  // ==========================================
  CONFIRM_CANCEL: 'Êtes-vous sûr de vouloir annuler ce paiement ?',
  CONFIRM_CANCEL_DETAIL: 'Cette action est irréversible et restaurera le stock',
  CONFIRM_REFUND: 'Êtes-vous sûr de vouloir rembourser ce paiement ?',
  CONFIRM_DELETE: 'Êtes-vous sûr de vouloir supprimer ce paiement ?',
  CONFIRM_AMOUNT: 'Vérifiez le montant avant de valider',

  // ==========================================
  // FORMULAIRE
  // ==========================================
  FORM_TITLE_CREATE: 'Nouveau paiement',
  FORM_TITLE_EDIT: 'Modifier le paiement',
  FORM_TITLE_VIEW: 'Détails du paiement',
  FORM_SALE_ID: 'ID de la vente',
  FORM_AMOUNT: 'Montant',
  FORM_METHOD: 'Méthode de paiement',
  FORM_REFERENCE: 'Référence',
  FORM_COMMENT: 'Commentaire',
  FORM_DATE: 'Date de paiement',
  FORM_SUBMIT: 'Enregistrer',
  FORM_CANCEL: 'Annuler',
  FORM_RESET: 'Réinitialiser'
};

// ============================================
// CONFIGURATION DU TABLEAU DE BORD
// ============================================

export const PAYMENT_DASHBOARD_CONFIG = {
  // Widgets à afficher
  widgets: {
    showCards: true,
    showMethodDistribution: true,
    showStatusDistribution: true,
    showTimeSeries: true,
    showRecentPayments: true,
    showSummary: true
  },

  // Ordre des widgets
  widgetOrder: [
    'cards',
    'timeSeries',
    'methodDistribution',
    'statusDistribution',
    'recentPayments'
  ],

  // Périodes disponibles pour le dashboard
  periods: ['today', 'week', 'month', 'year'],

  // Couleurs par défaut pour les graphiques
  chartColors: {
    method: [
      '#28a745', // Espèces - Vert
      '#007bff', // Carte - Bleu
      '#6f42c1', // Chèque - Violet
      '#fd7e14', // Virement - Orange
      '#20c997', // Mobile Money - Turquoise
      '#dc3545'  // Crédit - Rouge
    ],
    status: [
      '#ffc107', // Pending - Jaune
      '#198754', // Paid - Vert
      '#dc3545'  // Refunded - Rouge
    ],
    timeSeries: '#0d6efd'
  },

  // Valeurs par défaut
  defaults: {
    period: 'today',
    refreshInterval: 30,
    showRecentPayments: true,
    recentPaymentsLimit: 10
  }
};

// ============================================
// CONFIGURATION DU MODULE PAYMENT
// ============================================

export const PAYMENT_MODULE_CONFIG = {
  // Routes
  routes: {
    list: '/payments',
    create: '/payments/create',
    detail: '/payments/:id',
    statistics: '/payments/statistics'
  },

  // Permissions par rôle
  permissions: {
    ADMIN: {
      view: true,
      create: true,
      edit: true,
      cancel: true,
      delete: true,
      viewStatistics: true,
      viewAll: true
    },
    MANAGER: {
      view: true,
      create: true,
      edit: true,
      cancel: true,
      delete: false,
      viewStatistics: true,
      viewAll: true
    },
    CAISSIER: {
      view: true,
      create: true,
      edit: false,
      cancel: false,
      delete: false,
      viewStatistics: false,
      viewAll: false
    },
    STOCK: {
      view: false,
      create: false,
      edit: false,
      cancel: false,
      delete: false,
      viewStatistics: false,
      viewAll: false
    }
  },

  // Statuts autorisés pour les actions
  allowedActions: {
    cancel: [PaymentStatus.PAID],
    refund: [PaymentStatus.PAID],
    edit: [PaymentStatus.PENDING]
  }
};

// ============================================
// CONSTANTES D'EXPORT
// ============================================

export const EXPORT_CONFIG = {
  // Formats disponibles
  formats: ['csv', 'excel', 'pdf'],

  // Nom du fichier par défaut
  defaultFilename: 'paiements',

  // Séparateur CSV
  csvSeparator: ',',

  // Encodage
  encoding: 'utf-8',

  // Colonnes par défaut
  defaultColumns: [
    'id',
    'amount',
    'paymentMethodLabel',
    'statusLabel',
    'paymentDate',
    'userFullName',
    'saleId',
    'saleTotal'
  ]
};

// ============================================
// CONSTANTES DE VALIDATION
// ============================================

export const PAYMENT_VALIDATION = {
  // Longueurs
  maxReferenceLength: 100,
  maxCommentLength: 500,
  minUsernameLength: 3,
  maxUsernameLength: 50,

  // Formats
  referencePattern: /^[a-zA-Z0-9\-_ ]+$/,
  amountPattern: /^\d+(\.\d{1,2})?$/,

  // Messages de validation
  messages: {
    referenceRequired: 'La référence est obligatoire',
    referenceMaxLength: 'La référence ne peut pas dépasser 100 caractères',
    referenceInvalid: 'La référence contient des caractères non autorisés',
    commentMaxLength: 'Le commentaire ne peut pas dépasser 500 caractères',
    amountRequired: 'Le montant est obligatoire',
    amountMin: 'Le montant doit être supérieur à 0',
    amountMax: 'Le montant est trop élevé',
    amountInvalid: 'Format de montant invalide',
    methodRequired: 'La méthode de paiement est obligatoire',
    saleIdRequired: 'L\'ID de la vente est obligatoire',
    saleIdPositive: 'L\'ID de la vente doit être positif'
  }
};

// ============================================
// CONSTANTES DE FILTRES
// ============================================

export const FILTER_OPTIONS = {
  // Options de période
  periodOptions: [
    { value: 'today', label: 'Aujourd\'hui' },
    { value: 'week', label: 'Cette semaine' },
    { value: 'month', label: 'Ce mois' },
    { value: 'year', label: 'Cette année' },
    { value: 'custom', label: 'Personnalisée' }
  ],

  // Options de tri
  sortOptions: [
    { value: 'paymentDate_desc', label: 'Date (plus récent)' },
    { value: 'paymentDate_asc', label: 'Date (plus ancien)' },
    { value: 'amount_desc', label: 'Montant (décroissant)' },
    { value: 'amount_asc', label: 'Montant (croissant)' },
    { value: 'status', label: 'Statut' },
    { value: 'paymentMethod', label: 'Méthode' }
  ],

  // Valeurs par défaut
  defaults: {
    period: 'today',
    sort: 'paymentDate_desc',
    status: null,
    method: null
  }
};

// ============================================
// CONSTANTES DES GRAPHIQUES
// ============================================

export const CHART_CONFIG = {
  // Type de graphiques
  types: {
    methodDistribution: 'doughnut',
    statusDistribution: 'pie',
    timeSeries: 'line'
  },

  // Options communes
  options: {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
        labels: {
          usePointStyle: true,
          padding: 20
        }
      }
    }
  },

  // Couleurs par défaut
  defaultColors: [
    '#0d6efd', '#6610f2', '#6f42c1', '#d63384',
    '#dc3545', '#fd7e14', '#ffc107', '#198754',
    '#20c997', '#0dcaf0', '#adb5bd', '#6c757d'
  ],

  // Taille des graphiques
  sizes: {
    small: 200,
    medium: 300,
    large: 400,
    xlarge: 500
  }
};