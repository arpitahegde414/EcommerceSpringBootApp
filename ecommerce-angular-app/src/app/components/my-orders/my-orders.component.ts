import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { OrderService } from '../../services/order.service';
import { Order } from '../../models/order.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-my-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-orders.component.html',
  styleUrls: ['./my-orders.component.css']
})
export class MyOrdersComponent implements OnInit {
  orders: Order[] = [];
  isLoading = false;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) return;

    this.isLoading = true;
    this.userService.getUserOrders(currentUser.userId!).subscribe({
      next: (userOrders) => {
        const orderPromises = userOrders.map(uo =>
          this.orderService.getOrderById(uo.orderId).toPromise()
        );

        Promise.all(orderPromises).then(orders => {
          this.orders = orders.filter(o => o !== undefined) as Order[];
          this.isLoading = false;
        });
      },
      error: (error) => {
        console.error('Error loading orders:', error);
        this.isLoading = false;
      }
    });
  }
}

//npm install @angular/platform-browser-dynamic@<same-version> --save
//npm install @angular/platform-browser-dynamic@~21.1.1 --save


