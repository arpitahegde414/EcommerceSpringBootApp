import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { AppComponent } from './app/app.component';
import { AuthGuard } from './app/guards/auth.guard';

const routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' as const},
  { path: 'home', loadComponent: () => import('./app/app.component').then(m => m.AppComponent) },
  { path: 'login', loadComponent: () => import('./app/components/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./app/components/register/register.component').then(m => m.RegisterComponent) },
  { path: 'place-order', loadComponent: () => import('./app/components/place-order/place-order.component').then(m => m.PlaceOrderComponent) },
  { path: 'my-orders', loadComponent: () => import('./app/components/my-orders/my-orders.component').then(m => m.MyOrdersComponent), canActivate: [AuthGuard] },
  { path: '**', redirectTo: '/home' }
];

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    // Add other providers here
  ]
}).catch((err: any) => console.error(err));
