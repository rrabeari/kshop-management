import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [

    {
        path: 'login',
        loadComponent: () =>
        import('./features/auth/login/login')
            .then(m => m.LoginComponent)
    },

    {
        path: 'dashboard',
        canActivate: [authGuard],
        loadComponent: () =>
        import('./features/dashboard/dashboard/dashboard')
            .then(m => m.Dashboard)
    },
 
    {
        path: 'sales',
        canActivate: [
            authGuard,
            roleGuard
        ],
        data: {
            roles: [
            'ADMIN',
            'MANAGER',
            'CAISSIER'
            ]
        },
        loadComponent: () =>
        import('./features/sales/sales/sales')
            .then(m => m.Sales)
    },
    {
        path: 'products',
        canActivate: [
            authGuard,
            roleGuard
        ],
        data: {
            roles: [
            'ADMIN',
            'MANAGER',
            'STOCK'
            ]
        },
        loadComponent: () =>
        import('./features/products/products')
            .then(m => m.Products)
    },
    {
        path: 'categories',
        loadComponent: () =>
            import('./features/categories/categories')
            .then(m => m.Categories),
        canActivate: [authGuard,roleGuard],
        data: {
            roles: [
            'ADMIN',
            'MANAGER'
            ]
        }
    },
    {
        path: 'users',
        canActivate: [authGuard, roleGuard],
        data: {
            roles: ['ADMIN']
        },
        loadComponent: () =>
        import('./features/users/users')
            .then(m => m.Users)
    },
    {
        path: 'access-denied',
        loadComponent: () =>
        import('./features/access-denied/access-denied')
        .then(m => m.AccessDenied)
    },

    {
        path: 'stock',
        canActivate: [
            authGuard,
            roleGuard
        ],
        data: {
            roles: [
            'ADMIN',
            'MANAGER',
            'STOCK'
            ]
        },
        loadComponent: () =>
        import('./features/stock/stock')
            .then(m => m.StockComponent)
    },

    {
        path: 'not-found',
        loadComponent: () =>
            import('./features/not-found/not-found')
            .then(m => m.NotFound)
    },

    {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
    },

    {
        path: '**',
        redirectTo: 'not-found'
    }

];
