export interface Product {
  id: number;
  name: string;
  sku: string;
  price: number;
  quantity: number;
  orderQty?: number;
}
