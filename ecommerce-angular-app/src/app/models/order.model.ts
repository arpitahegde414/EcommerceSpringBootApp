export interface OrderItem {
  productId: number;
  quantity: number;
  price: number;
}

export interface Order {
  orderId?: number;
  userId: number;
  items: OrderItem[];
  totalAmount: number;
  status?: string;
  orderDate?: Date;
}

export interface UserOrder {
  id: number;
  orderId: number;
  orderDate: Date;
  totalAmount: number;
}
