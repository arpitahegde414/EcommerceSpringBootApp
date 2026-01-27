import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { InventoryService } from '../../services/inventory.service';
import { OrderService } from '../../services/order.service';
import { UserService } from '../../services/user.service';
import { Product } from '../../models/product.model';
import { Order } from '../../models/order.model';
import { CommonModule } from '@angular/common';
import { ProductCardComponent } from '../product-card/product-card.component';

@Component({
  selector: 'app-place-order',
  standalone: true,
    imports: [CommonModule, ProductCardComponent],
  templateUrl: './place-order.component.html',
  styleUrls: ['./place-order.component.css']
})
export class PlaceOrderComponent implements OnInit {
  products: Product[] = [];
  errorMessage = '';
  successMessage = '';
  isLoading = false;

  constructor(
    private authService: AuthService,
    private inventoryService: InventoryService,
    private orderService: OrderService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.isLoading = true;
    this.inventoryService.getAllProducts().subscribe({
      next: (products) => {
        this.products = products.map(p => ({ ...p, orderQty: 0 }));
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load products';
        this.isLoading = false;
        console.error('Error loading products:', error);
      }
    });
  }

  increaseQuantity(product: Product): void {
    if (!product.orderQty) product.orderQty = 0;
    if (product.orderQty < product.quantity) {
      product.orderQty++;
    }
  }

  decreaseQuantity(product: Product): void {
    if (product.orderQty && product.orderQty > 0) {
      product.orderQty--;
    }
  }

  getCartItems(): Product[] {
    return this.products.filter(p => p.orderQty && p.orderQty > 0);
  }

  getCartTotal(): number {
    return this.getCartItems().reduce((sum, item) =>
      sum + (item.price * (item.orderQty || 0)), 0
    );
  }

  placeOrder(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      this.errorMessage = 'Please login first';
      return;
    }

    const orderItems = this.getCartItems().map(p => ({
      productId: p.id,
      quantity: p.orderQty || 0,
      price: p.price
    }));

    const order: Order = {
      userId: currentUser.userId!,
      items: orderItems,
      totalAmount: this.getCartTotal()
    };

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.orderService.placeOrder(order).subscribe({
      next: (createdOrder) => {
        return this.userService.assignOrderToUser(
          currentUser.userId!,
          createdOrder.orderId!,
          createdOrder.totalAmount
        ).subscribe({
          next: () => {
            this.isLoading = false;
            this.successMessage = 'Order placed successfully!';
            this.products.forEach(p => p.orderQty = 0);
            setTimeout(() => {
              this.router.navigate(['/my-orders']);
            }, 2000);
          },
          error: (error) => {
            this.isLoading = false;
            this.errorMessage = 'Order created but failed to assign to user';
            console.error('Error assigning order:', error);
          }
        });
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to place order. Please try again.';
        console.error('Order error:', error);
      }
    });
  }
}
