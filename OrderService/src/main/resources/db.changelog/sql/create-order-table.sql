CREATE TABLE orders (
                        order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        product_id BIGINT NOT NULL,
                        quantity INT NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        order_date DATE NOT NULL,
                        reserved_batch_ids VARCHAR(500),
                        CONSTRAINT fk_order_product FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE INDEX idx_order_product_id ON orders(product_id);
CREATE INDEX idx_order_status ON orders(status);