import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Product } from '../../models/product.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css']
})
export class ProductCardComponent {
  @Input() product!: Product;
  @Output() increase = new EventEmitter<Product>();
  @Output() decrease = new EventEmitter<Product>();

  onIncrease(): void {
    this.increase.emit(this.product);
  }

  onDecrease(): void {
    this.decrease.emit(this.product);
  }
}
