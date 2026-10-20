# Order Processing Platform

A RESTful order processing backend built with Java, Spring Boot, and PostgreSQL. The project provides APIs for managing products and creating and retrieving customer orders, with stock validation and automated tests.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven
- Docker and Docker Compose
- JUnit and Spring Boot testing

## Features

- Create, retrieve, update, and delete products.
- Create customer orders.
- Validate product availability and requested quantities.
- Deduct stock when an order is successfully created.
- Retrieve orders by ID or list all orders.
- Handle API errors and request validation.
- Run the application and database using Docker Compose.
- Test application behavior with automated tests.

## API Endpoints

Base URL: `http://localhost:8080`

### Products

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/products` | Retrieve all products |
| GET | `/api/products/{id}` | Retrieve a product by ID |
| POST | `/api/products` | Create a product |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Delete a product |

### Orders

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Create an order |
| GET | `/api/orders` | Retrieve all orders |
| GET | `/api/orders/{id}` | Retrieve an order by ID |

### Example: Retrieve Products


curl http://localhost:8080/api/products


### Example: Create a Product


curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Mouse",
    "price": 29.99,
    "stock": 25
  }'


### Example: Create an Order

This example uses product ID `3`, which existed in the development database during testing. Make sure the product exists and has sufficient stock before running the request.


curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 3,
    "quantity": 2
  }'


**Note:** Creating an order changes product stock. These examples are intended for a development environment.

## Getting Started

### Prerequisites

- Docker Desktop
- Git
- Java 21, if running or testing the application outside Docker

Docker Compose is included with current Docker Desktop installations.

### Run with Docker Compose

1. Clone the repository:


   git clone https://github.com/Mayank212514/order-processing-platform.git
   cd order-processing-platform


2. Start the application and database:


   docker compose up --build -d


3. Check the running containers:


   docker compose ps


4. Retrieve the products:


   curl http://localhost:8080/api/products


### Stop the Application


docker compose stop


This stops the services without intentionally removing their containers or database volume.

### Run Tests

Run the automated tests using the Maven wrapper:

./mvnw clean test


The project includes tests for product endpoints, order processing, validation, and error handling.

## Project Structure

src/
├── main/
│   └── java/com/orderplatform/orderservice/
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── exception/
│       ├── repository/
│       └── service/
└── test/
    └── java/

Dockerfile
compose.yaml
pom.xml
README.md


## Database Configuration

The application uses PostgreSQL. Local database configuration is supplied through environment variables, with development defaults configured locally.

Never upload passwords, database credentials, or other sensitive information to GitHub.

## Future Improvements

- Continuous integration with GitHub Actions
- Python-based analytics service
- Additional monitoring and deployment improvements
