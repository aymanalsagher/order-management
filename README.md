# Microservices: Order Service and Inventory Service

This project consists of two microservices:
1. **Order Service**: Handles order creation and checks item availability.
2. **Inventory Service**: Manages stock levels and updates inventory.

## Prerequisites
- Docker and Docker Compose installed.
- Java 21 and Maven installed for local builds.

---

## Order Service

**Description**:  
The Order Service allows users to:
- Place an order.
- Retrieve an order.

**Endpoints**:
- `POST /v1/orders?itemId={itemId}&quantity={quantity}`: Place an order.
- `GET /v1/orders/{orderId}`: Get order details.

---

## Inventory Service

**Description**:  
The Inventory Service allows:
- Retrieving stock levels.
- Decreasing stock on order placement.

**Endpoints**:
- `GET /v1/inventory/{itemId}`: Get item stock level.
- `POST /v1/inventory/{itemId}/decrease`: Decrease item stock.

---

## How to Run Locally

1. **Build Services**:
   ```sh
   mvn clean package -DskipTests
   ```
2. **Run Services**
   ```sh
   java -jar target/order-service.jar
   java -jar target/inventory-service.jar
   ```

---

## Running with Docker Compose

At the root of the project, run the following commands:

- Build local artifacts `mvn clean package -DskipTests`
- Build docker images `docker compose build`
- Start services `docker compose up -d`
- Shutdown services `docker compose down`

### Verifying Services

- Order Service: Accessible at http://localhost:8080.
- Inventory Service: Accessible at http://localhost:8081.

Swagger API documentation for each service will be available at `/swagger-ui/index.html`.
