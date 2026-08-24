import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Payment, PaymentRequest, PaymentStatistics, PaymentMethod, PaymentStatus } from '../../core/models/payment.model';
import { Sale } from '../../core/models/sale.model';
import { PaymentService } from '../../core/services/payment.service';
import { SaleService } from '../../core/services/sale';


@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payments.html',
  styleUrl: './payments.css'
})
export class Payments implements OnInit {
  private readonly paymentService = inject(PaymentService);
  private readonly saleService = inject(SaleService);
  private readonly cdr = inject(ChangeDetectorRef);

  // DONNÉES
  payments: Payment[] = [];
  filteredPayments: Payment[] = [];
  sales: Sale[] = [];
  filteredSales: Sale[] = [];
  statistics: PaymentStatistics | null = null;

  // FILTRES & RECHERCHE
  searchTerm = '';
  statusFilter: string = 'all';
  saleSearchTerm = '';

  // ÉTATS
  loading = false;
  saving = false;
  errorMessage = '';
  successMessage = '';
  formVisible = false;

  // SÉLECTION DE VENTE & FORMULAIRE
  selectedSale: Sale | null = null;
  paymentForm: PaymentRequest = {
    saleId: 0,
    amount: 0,
    paymentMethod: 'MOBILE_MONEY',
    transactionReference: ''
  };

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.loadPayments();
    this.loadStatistics();
    this.loadSales();
  }

  loadPayments(): void {
    this.loading = true;
    this.errorMessage = '';
    this.paymentService.findAll().subscribe({
      next: (data) => {
        this.payments = data;
        this.filterPayments();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur chargement paiements:', err);
        this.errorMessage = 'Impossible de charger la liste des paiements.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadStatistics(): void {
    this.paymentService.getStatistics().subscribe({
      next: (data) => {
        this.statistics = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erreur chargement statistiques:', err)
    });
  }

  loadSales(): void {
    this.saleService.findAll().subscribe({
      next: (data) => {
        this.sales = data.filter(s => s.status === 'COMPLETED');
        this.filteredSales = [...this.sales];
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erreur chargement ventes:', err)
    });
  }

  filterPayments(): void {
    const term = this.searchTerm.toLowerCase().trim();
    this.filteredPayments = this.payments.filter(p => {
      const matchesSearch = !term ||
        p.id.toString().includes(term) ||
        p.saleId.toString().includes(term) ||
        (p.transactionReference && p.transactionReference.toLowerCase().includes(term));

      let matchesStatus = true;
      if (this.statusFilter !== 'all') {
        matchesStatus = p.status === this.statusFilter;
      }

      return matchesSearch && matchesStatus;
    });
  }

  filterSales(): void {
    const term = this.saleSearchTerm.toLowerCase().trim();
    this.filteredSales = this.sales.filter(s =>
      s.id.toString().includes(term) ||
      (s.username && s.username.toLowerCase().includes(term)) ||
      s.totalAmount.toString().includes(term)
    );
  }

  resetFilters(): void {
    this.searchTerm = '';
    this.statusFilter = 'all';
    this.filterPayments();
  }

  openCreateForm(): void {
    this.selectedSale = null;
    this.paymentForm = {
      saleId: 0,
      amount: 0,
      paymentMethod: 'MOBILE_MONEY',
      transactionReference: ''
    };
    this.saleSearchTerm = '';
    this.filteredSales = [...this.sales];
    this.errorMessage = '';
    this.formVisible = true;
  }

  closeForm(): void {
    if (this.saving) return;
    this.formVisible = false;
    this.selectedSale = null;
  }

  selectSale(sale: Sale): void {
    this.selectedSale = sale;
    this.paymentForm.saleId = sale.id;
    this.paymentForm.amount = sale.totalAmount;
  }

  savePayment(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.paymentForm.saleId || this.paymentForm.saleId <= 0) {
      this.errorMessage = 'Veuillez sélectionner une vente dans la liste de droite.';
      return;
    }

    if (!this.paymentForm.amount || this.paymentForm.amount <= 0) {
      this.errorMessage = 'Le montant du paiement doit être supérieur à zéro.';
      return;
    }

    this.saving = true;
    this.paymentService.create(this.paymentForm).subscribe({
      next: () => {
        this.showSuccess('Paiement enregistré avec succès.');
        this.saving = false;
        this.formVisible = false;
        this.refresh();
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err?.error?.message || 'Impossible d’enregistrer le paiement.';
        this.cdr.detectChanges();
      }
    });
  }

  private showSuccess(msg: string): void {
    this.successMessage = msg;
    setTimeout(() => {
      this.successMessage = '';
      this.cdr.detectChanges();
    }, 3000);
  }
}