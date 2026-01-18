# Project Setup Instructions
### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/ecommerce-microservices.git
cd ecommerce-microservices
```

### 2. Build the Projects

```bash
# Build Inventory Service
cd inventory-service
mvn clean install

# Build Order Service
cd ../order-service
mvn clean install
```

### 3. Run the Services

**Terminal 1 - Inventory Service:**
```bash
cd inventory-service
mvn spring-boot:run
```
The service will start on `http://localhost:8081`

**Terminal 2 - Order Service:**
```bash
cd order-service
mvn spring-boot:run
```
The service will start on `http://localhost:8080`

# Technologies Used

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database
- Liquibase
- Lombok
- JUnit 5
- Mockito
- Maven
- Swagger/OpenAPI

## Prerequisites

- JDK 17 or higher
- Maven 3.6+
- Git

## API Documentation

### Inventory Service (Port 8081)

#### 1. Get Inventory by Product ID
```http
GET http://localhost:8081/inventory/{productId}
```

**Response:**
```json
{
  "productId": 1001,
  "productName": "Laptop",
  "batches": [
    {
      "batchId": 1,
      "quantity": 68,
      "expiryDate": "2026-06-25"
    }
  ]
}
```

#### 2. Update Inventory
```http
POST http://localhost:8081/inventory/update
Content-Type: application/json

{
  "batchId": 1,
  "quantityToDeduct": 10
}
```

**Response:**
```json
{
  "message": "Inventory updated successfully",
  "batchId": 1,
  "remainingQuantity": 58
}
```

### Order Service (Port 8080)

#### Place Order
```http
POST http://localhost:8080/order
Content-Type: application/json

{
  "productId": 1002,
  "quantity": 3
}
```

**Response:**
```json
{
  "orderId": 11,
  "productId": 1002,
  "productName": "Smartphone",
  "quantity": 3,
  "status": "PLACED",
  "reservedFromBatchIds": [9],
  "message": "Order placed. Inventory reserved."
}
```

## Swagger UI

- Inventory Service: http://localhost:8081/swagger-ui.html
- Order Service: http://localhost:8080/swagger-ui.html

## H2 Console

- Inventory Service: http://localhost:8081/h2-console
    - JDBC URL: `jdbc:h2:mem:inventorydb`
    - Username: `sa`
    - Password: (empty)

- Order Service: http://localhost:8080/h2-console
    - JDBC URL: `jdbc:h2:mem:orderdb`
    - Username: `sa`
    - Password: (empty)

## Testing

### Run All Tests

```bash
# Inventory Service
cd inventory-service
mvn test

# Order Service
cd order-service
mvn test
```