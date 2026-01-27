CREATE TABLE users (
                        user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) NOT NULL,
                        password VARCHAR(20) NOT NULL,
                        email VARCHAR(20) NOT NULL,
                        created_at DATE NOT NULL
);