# JWT Auth & Inventory Management API

A Spring Boot application providing JWT-based authentication and inventory management APIs. Includes user registration, login, email verification, and secure CRUD operations for inventory items.

## Features
- **JWT Authentication**: Secure login, registration, and token-based authentication
- **Email Verification**: Email-based verification during registration
- **Inventory Management**: CRUD operations for items
- **Global Exception Handling**
- **RESTful API**
- **Test Coverage**

## Technologies Used
- Java 17+
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- Maven
- Docker Compose
- **PostgreSQL** (as the database)

## API Endpoints

### Auth
- `POST /api/auth/register` — Register new user
- `POST /api/auth/login` — Login and receive JWT
- `POST /api/auth/verify` — Verify email code

### Inventory
- `GET /api/inventory/items` — List items
- `POST /api/inventory/items` — Create item
- `PUT /api/inventory/items/{id}` — Update item
- `DELETE /api/inventory/items/{id}` — Delete item


## Configuration
- **JWT & Security**: See `src/main/java/com/yusufelkaan/jwt_auth/auth/config/`
- **Inventory**: See `src/main/java/com/yusufelkaan/jwt_auth/inventory/`
- **Exception Handling**: See `src/main/java/com/yusufelkaan/jwt_auth/shared/config/GlobalExceptionHandler.java`

