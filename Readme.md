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