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

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit, OnDestroy {

  private readonly saleService =
    inject(SaleService);

  private readonly productService =
    inject(ProductService);

  private readonly cdr =
    inject(ChangeDetectorRef);


  @ViewChild('salesChart')
  salesChartRef!: ElementRef<HTMLCanvasElement>;


  private salesChart?: Chart;


  sales: Sale[] = [];

  products: Product[] = [];

  loading = false;

  errorMessage = '';


  ngOnInit(): void {

    this.loadDashboard();

  }


  // =====================================================
  // CHARGEMENT
  // =====================================================

  loadDashboard(): void {

    this.loading = true;

    this.errorMessage = '';

    let salesLoaded = false;

    let productsLoaded = false;


    this.saleService.findAll().subscribe({

      next: (data) => {

        console.log(
          'Dashboard SALES :',
          data
        );

        this.sales = data;

        salesLoaded = true;

        this.finishLoading(
          salesLoaded,
          productsLoaded
        );

      },

      error: (error) => {

        console.error(
          'Erreur SALES Dashboard :',
          error
        );

        this.errorMessage =
          'Impossible de charger les ventes.';

        this.loading = false;

        this.cdr.detectChanges();

      }

    });


    this.productService.findAll().subscribe({

      next: (data) => {

        console.log(
          'Dashboard PRODUCTS :',
          data
        );

        this.products = data;

        productsLoaded = true;

        this.finishLoading(
          salesLoaded,
          productsLoaded
        );

      },

      error: (error) => {

        console.error(
          'Erreur PRODUCTS Dashboard :',
          error
        );

        this.errorMessage =
          'Impossible de charger les produits.';

        this.loading = false;

        this.cdr.detectChanges();

      }

    });

  }


  private finishLoading(
    salesLoaded: boolean,
    productsLoaded: boolean
  ): void {

    if (
      salesLoaded &&
      productsLoaded
    ) {

      this.loading = false;

      this.cdr.detectChanges();

      /*
       * Angular doit avoir rendu le canvas
       * avant de créer le graphique.
       */
      setTimeout(() => {

        this.createSalesChart();

      });

    }

  }


  // =====================================================
  // GRAPHIQUE
  // =====================================================

  private createSalesChart(): void {

    if (!this.salesChartRef) {
      return;
    }


    /*
     * Détruire l'ancien graphique avant
     * d'en créer un nouveau.
     */
    if (this.salesChart) {

      this.salesChart.destroy();

    }


    const completedSales =
      this.sales.filter(
        sale =>
          sale.status === 'COMPLETED'
      );


    /*
     * Regroupement du chiffre d'affaires
     * par jour.
     */
    const dailySales =
      new Map<string, number>();


    completedSales.forEach(sale => {

      const date =
        new Date(sale.saleDate);


      const key =
        date.toLocaleDateString(
          'fr-FR',
          {
            day: '2-digit',
            month: '2-digit'
          }
        );


      const current =
        dailySales.get(key) ?? 0;


      dailySales.set(
        key,
        current + Number(sale.totalAmount)
      );

    });


    const labels =
      Array.from(
        dailySales.keys()
      );


    const values =
      Array.from(
        dailySales.values()
      );


    this.salesChart =
      new Chart(
        this.salesChartRef.nativeElement,
        {
          type: 'line',

          data: {

            labels,

            datasets: [
              {
                label:
                  "Chiffre d'affaires",

                data: values,

                borderWidth: 2,

                tension: 0.3,

                fill: true,

                pointRadius: 4,

                pointHoverRadius: 6
              }
            ]

          },

          options: {

            responsive: true,

            maintainAspectRatio: false,

            plugins: {

              legend: {
                display: false
              },

              tooltip: {

                callbacks: {

                  label: (context) => {

                    return (
                      Number(context.raw)
                        .toLocaleString('fr-FR')
                      + ' Ar'
                    );

                  }

                }

              }

            },

            scales: {

              y: {

                beginAtZero: true,

                ticks: {

                  callback: (value) => {

                    return (
                      Number(value)
                        .toLocaleString('fr-FR')
                      + ' Ar'
                    );

                  }

                }

              }

            }

          }

        }
      );

  }


  // =====================================================
  // ACTUALISER
  // =====================================================

  refresh(): void {

    this.loadDashboard();

  }


  // =====================================================
  // STATISTIQUES
  // =====================================================

  get totalSales(): number {

    return this.sales.length;

  }


  get completedSalesCount(): number {

    return this.sales.filter(
      sale =>
        sale.status === 'COMPLETED'
    ).length;

  }


  get completedSalesTotal(): number {

    return this.sales
      .filter(
        sale =>
          sale.status === 'COMPLETED'
      )
      .reduce(
        (total, sale) =>
          total + Number(sale.totalAmount),
        0
      );

  }


  get activeProductsCount(): number {

    return this.products.filter(
      product =>
        product.active
    ).length;

  }


  get lowStockCount(): number {

    return this.products.filter(
      product =>
        product.active &&
        Number(product.quantity) <=
        Number(product.minimumStock) &&
        Number(product.quantity) > 0
    ).length;

  }


  get outOfStockCount(): number {

    return this.products.filter(
      product =>
        product.active &&
        Number(product.quantity) <= 0
    ).length;

  }


  get recentSales(): Sale[] {

    return [...this.sales]
      .sort(
        (a, b) =>
          new Date(b.saleDate).getTime() -
          new Date(a.saleDate).getTime()
      )
      .slice(0, 5);

  }


  get lowStockProducts(): Product[] {

    return this.products
      .filter(
        product =>
          product.active &&
          Number(product.quantity) <=
          Number(product.minimumStock)
      )
      .slice(0, 5);

  }


  // =====================================================
  // DESTROY
  // =====================================================

  ngOnDestroy(): void {

    if (this.salesChart) {

      this.salesChart.destroy();

    }

  }

}