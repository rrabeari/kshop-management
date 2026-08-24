import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild,
  inject
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { SaleService } from '../../../core/services/sale';
import { ProductService } from '../../../core/services/product';
import { Sale } from '../../../core/models/sale.model';
import { Product } from '../../../core/models/product.model';

Chart.register(...registerables);

export interface TopProductStat {
  name: string;
  code: string;
  quantity: number;
  totalRevenue: number;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit, OnDestroy {
  private readonly saleService = inject(SaleService);
  private readonly productService = inject(ProductService);
  private readonly cdr = inject(ChangeDetectorRef);

  @ViewChild('salesChart') salesChartRef!: ElementRef<HTMLCanvasElement>;

  private salesChart?: Chart;

  sales: Sale[] = [];
  products: Product[] = [];
  loading = false;
  errorMessage = '';

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;
    this.errorMessage = '';
    let salesLoaded = false;
    let productsLoaded = false;

    this.saleService.findAll().subscribe({
      next: (data) => {
        this.sales = data;
        salesLoaded = true;
        this.finishLoading(salesLoaded, productsLoaded);
      },
      error: (error) => {
        console.error('Erreur SALES Dashboard :', error);
        this.errorMessage = 'Impossible de charger les ventes.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });

    this.productService.findAll().subscribe({
      next: (data) => {
        this.products = data;
        productsLoaded = true;
        this.finishLoading(salesLoaded, productsLoaded);
      },
      error: (error) => {
        console.error('Erreur PRODUCTS Dashboard :', error);
        this.errorMessage = 'Impossible de charger les produits.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  private finishLoading(salesLoaded: boolean, productsLoaded: boolean): void {
    if (salesLoaded && productsLoaded) {
      this.loading = false;
      this.cdr.detectChanges();
      setTimeout(() => {
        this.createSalesChart();
      });
    }
  }

  private createSalesChart(): void {
    if (!this.salesChartRef) return;
    if (this.salesChart) this.salesChart.destroy();

    const completedSales = this.sales.filter(s => s.status === 'COMPLETED');
    const dailySales = new Map<string, number>();

    completedSales.forEach(sale => {
      const date = new Date(sale.saleDate);
      const key = date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' });
      const current = dailySales.get(key) ?? 0;
      dailySales.set(key, current + Number(sale.totalAmount));
    });

    this.salesChart = new Chart(this.salesChartRef.nativeElement, {
      type: 'line',
      data: {
        labels: Array.from(dailySales.keys()),
        datasets: [{
          label: "Chiffre d'affaires",
          data: Array.from(dailySales.values()),
          borderColor: '#2563eb',
          backgroundColor: 'rgba(37, 99, 235, 0.1)',
          borderWidth: 2,
          tension: 0.3,
          fill: true,
          pointRadius: 4,
          pointHoverRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (context) => `${Number(context.raw).toLocaleString('fr-FR')} Ar`
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              callback: (value) => `${Number(value).toLocaleString('fr-FR')} Ar`
            }
          }
        }
      }
    });
  }

  refresh(): void {
    this.loadDashboard();
  }

  // =====================================================
  // CALCULS STATISTIQUES ENRICHIS
  // =====================================================

  get totalSales(): number {
    return this.sales.length;
  }

  get completedSalesCount(): number {
    return this.sales.filter(s => s.status === 'COMPLETED').length;
  }

  get cancelledSalesCount(): number {
    return this.sales.filter(s => s.status === 'CANCELLED').length;
  }

  get completedSalesTotal(): number {
    return this.sales
      .filter(s => s.status === 'COMPLETED')
      .reduce((total, sale) => total + Number(sale.totalAmount), 0);
  }

  get averageBasket(): number {
    const count = this.completedSalesCount;
    return count > 0 ? this.completedSalesTotal / count : 0;
  }

  get activeProductsCount(): number {
    return this.products.filter(p => p.active).length;
  }

  get lowStockCount(): number {
    return this.products.filter(
      p => p.active && Number(p.quantity) <= Number(p.minimumStock)
    ).length;
  }

  get recentSales(): Sale[] {
    return [...this.sales]
      .sort((a, b) => new Date(b.saleDate).getTime() - new Date(a.saleDate).getTime())
      .slice(0, 5);
  }

  get lowStockProducts(): Product[] {
    return this.products
      .filter(p => p.active && Number(p.quantity) <= Number(p.minimumStock))
      .slice(0, 5);
  }

  get topSellingProducts(): TopProductStat[] {
    const map = new Map<string, TopProductStat>();

    this.sales
      .filter(s => s.status === 'COMPLETED')
      .forEach(sale => {
        sale.items?.forEach(item => {
          const key = item.productName;
          const current = map.get(key) || { name: item.productName, code: item.productCode || '', quantity: 0, totalRevenue: 0 };
          map.set(key, {
            ...current,
            quantity: current.quantity + item.quantity,
            totalRevenue: current.totalRevenue + Number(item.subtotal)
          });
        });
      });

    return Array.from(map.values())
      .sort((a, b) => b.quantity - a.quantity)
      .slice(0, 5);
  }

  ngOnDestroy(): void {
    if (this.salesChart) {
      this.salesChart.destroy();
    }
  }
}