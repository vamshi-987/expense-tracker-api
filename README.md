# Smart Expense Tracker API

A production-ready RESTful API for personal expense management built with **Java 21**, **Spring Boot 3**, **Spring Data JPA**, **H2 In-Memory Database**, **Lombok**, and **MapStruct**.

---

## Features

- **Add Expense**: Create an expense with title, positive amount, valid category, and past/present date.
- **View All Expenses**: Retrieve all recorded expenses.
- **Filter Expenses by Category**: Case-insensitive filtering of expenses by category name (`food`, `Food`, `FOOD` match the same category).
- **Calculate Totals (Database Aggregated)**:
  - Overall total sum of all expenses via DB `SUM` query.
  - Category-specific total sum via DB `SUM` query.
- **Delete Expense**: Delete an expense by ID (returns 404 if not found).
- **Category Management & Normalization**: Dedicated category entity management. Category names are automatically normalized to lowercase (`food`) for storage. Missing category lookups when creating expenses throw an explicit `CategoryNotFoundException` (404 Not Found).
- **OpenAPI / Swagger UI Docs**: Interactive API documentation at `/swagger-ui.html`.

---

## Tech Stack & Prerequisites

- **Java Development Kit (JDK)**: Java 21 or higher
- **Build Tool**: Apache Maven (included via Maven Wrapper `./mvnw`)
- **Framework**: Spring Boot 3.5.5
- **Database**: H2 (In-Memory)

---

## How to Install & Build

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd expense-tracker
   ```

2. **Build the Application**:
   ```bash
   ./mvnw clean package -DskipTests
   ```

---

## How to Run the Server

Start the Spring Boot application using Maven:

```bash
./mvnw spring-boot:run
```

The application runs locally on `http://localhost:8080`.

- **Swagger UI / OpenAPI Documentation**:  
  [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **H2 Database Console**:  
  [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
  *JDBC URL*: `jdbc:h2:mem:expense_db` | *Username*: `sa` | *Password*: (empty)

---

## How to Run Tests

Run the complete automated test suite (19 unit and integration tests):

```bash
./mvnw test
```

### Test Directory Location
- Standard Maven Test Suite: `src/test/`
- Root test directory alias: `tests/` (symlinked to `src/test`)

---

## API Reference & Endpoints

### Expense Endpoints

| Method | Endpoint | Description | Sample Request Payload |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/expenses` | Add a new expense (requires pre-created category) | `{"title":"Lunch","amount":250.50,"category":"food","date":"2026-07-31"}` |
| `GET` | `/api/v1/expenses` | Get all expenses | N/A |
| `GET` | `/api/v1/expenses/category/{category}` | Filter expenses by category (case-insensitive) | N/A |
| `GET` | `/api/v1/expenses/total` | Get overall total sum | N/A |
| `GET` | `/api/v1/expenses/total/{category}` | Get total sum by category (case-insensitive) | N/A |
| `DELETE` | `/api/v1/expenses/{id}` | Delete expense by ID | N/A |

### Category Endpoints

| Method | Endpoint | Description | Sample Request Payload |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/categories` | Add a new category (stored in lowercase) | `{"name":"food"}` |
| `GET` | `/api/v1/categories` | Get all categories | N/A |
| `GET` | `/api/v1/categories/{id}` | Get category by ID | N/A |
