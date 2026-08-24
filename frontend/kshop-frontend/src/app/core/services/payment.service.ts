import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Payment, PaymentRequest, PaymentStatistics } from '../models/payment.model';
import { API_CONFIG } from './api.config';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_CONFIG.baseUrl}/payments`;

  findAll(): Observable<Payment[]> {
    return this.http.get<Payment[]>(this.apiUrl);
  }

  findById(id: number): Observable<Payment> {
    return this.http.get<Payment>(`${this.apiUrl}/${id}`);
  }

  findBySaleId(saleId: number): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.apiUrl}/sale/${saleId}`);
  }

  getStatistics(): Observable<PaymentStatistics> {
    return this.http.get<PaymentStatistics>(`${this.apiUrl}/statistics`);
  }

  create(payment: PaymentRequest): Observable<Payment> {
    return this.http.post<Payment>(this.apiUrl, payment);
  }
}