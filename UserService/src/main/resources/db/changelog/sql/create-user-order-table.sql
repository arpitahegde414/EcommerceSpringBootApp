CREATE TABLE user_orders(
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        order_id BIGINT NOT NULL,
                        order_date DATE NOT NULL,
                        total_amount INT NOT NULL,
                        CONSTRAINT fk_user_order FOREIGN KEY (user_id) REFERENCES users(user_id)
);