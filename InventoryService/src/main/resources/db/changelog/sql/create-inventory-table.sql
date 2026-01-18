CREATE TABLE inventory_batch (
                                 batch_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 product_id BIGINT NOT NULL,
                                 quantity INT NOT NULL,
                                 expiry_date DATE NOT NULL,
    CONSTRAINT fk_inventory_prodct FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE INDEX idx_product_id ON inventory_batch(product_id);
CREATE INDEX idx_expiry_date ON inventory_batch(expiry_date);