import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { Navbar } from './shared/components/navbar/navbar';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, Navbar],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('kshop-frontend');
  
  private readonly router = inject(Router);
  private currentUrl = '';

  constructor() {
    // Écoute les changements de route pour masquer la navbar sur le login
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.currentUrl = event.urlAfterRedirects;
    });
  }

  // Retourne false si l'URL contient '/login', true sinon
  get showNavbar(): boolean {
    return !this.currentUrl.includes('/login');
  }
}