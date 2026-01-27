import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { UserOrder } from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = environment.apiUrls.userService;

  constructor(private http: HttpClient) {}

  getUserOrders(userId: number): Observable<UserOrder[]> {
    return this.http.get<UserOrder[]>(`${this.apiUrl}/${userId}/orders`);
  }

  assignOrderToUser(userId: number, orderId: number, totalAmount: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/assign-order`, {
      userId,
      orderId,
      totalAmount
    });
  }
}
