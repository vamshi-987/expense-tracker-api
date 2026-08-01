# AI Usage Notes & Evaluation Record

This document outlines how AI tools (including Antigravity and Copilot) were utilized during the design, development, refactoring, and testing of the **Smart Expense Tracker API**.

---

## 1. Code Breakdown: AI-Generated vs. Written / Refactored

### AI-Generated Components
- **Boilerplate DTO & Entity Structures**: Initial skeleton layout for `Expense.java`, `Category.java`, `ExpenseRequest.java`, `ExpenseResponse.java`, `CategoryRequest.java`, and `CategoryResponse.java`.
- **OpenAPI Configuration**: `OpenApiConfig.java` configuration bean and `@Operation` / `@Schema` annotations on controller endpoints and DTO properties.
- **Initial Unit & Integration Test Templates**: Test structure for `ExpenseServiceImplTest.java` and `ExpenseControllerIntegrationTest.java`.

### Written / Refactored by Developer
- **Relational Category Entity & Lowercase Normalization**: Designed and implemented the `@ManyToOne` relationship between `Expense` and `Category`. Configured category name normalization to convert stored categories to lowercase (`food`) and enforce case-insensitive lookups across all queries.
- **Strict Category Validation**: Enforced business rule requiring category pre-creation before expense addition, throwing an explicit `CategoryNotFoundException` when a category is absent.
- **Database Aggregation Queries**: Implemented native JPQL `@Query` sum operations (`sumTotalExpenses` and `sumTotalByCategory`) in `ExpenseRepository` to eliminate JVM heap stream memory overhead.
- **Service & Transaction Management**: Defined transaction boundaries (`@Transactional(readOnly = true)` at class level and write `@Transactional` on `addExpense`, `addCategory`, and `deleteExpense`).
- **Structured Logging**: Implemented `@Slf4j` with informative info, debug, and error log statements.

---

## 2. Validations, Tests, and Modifications to AI Output

1. **Relational Category Integration with Case-Insensitive Normalization**:
   - *AI Output*: Suggested auto-creating categories implicitly or requiring numeric `categoryId` in requests.
   - *Modification*: Enhanced `ExpenseServiceImpl` to perform case-insensitive category lookups (`food`, `Food`, `FOOD`), enforce category pre-creation validation, and return structured `validationErrors` when a category does not exist.
   - *Rationale*: Maintains high data integrity while keeping API request payloads simple and consistent.

2. **Incomplete Dependency Declarations in Maven `pom.xml`**:
   - *AI Output*: Included duplicate declarations for `spring-boot-starter-test`.
   - *Modification*: Cleaned `pom.xml` dependency declarations and verified clean compilation with Maven.
   - *Rationale*: Eliminates build warnings and prevents future dependency resolution issues.

3. **Validation Error Structure Enhancement**:
   - *AI Output*: Left `validationErrors` as `null` for business exception handlers.
   - *Modification*: Populated `validationErrors` map (e.g., `{"category": "Category not found: food"}`) in `GlobalExceptionHandler` for both `CategoryNotFoundException` and `CategoryAlreadyExistsException`.
   - *Rationale*: Guarantees client applications receive explicit field-level error mapping whether validation fails at the DTO layer or the service layer.

---

## 3. Rejected AI Suggestions & Rationale

1. **Rejected: `@ResponseStatus` Annotations in Favor of `ResponseEntity<ErrorResponse>`**
   - *AI Suggestion*: AI suggested annotating custom exceptions directly with `@ResponseStatus(HttpStatus.NOT_FOUND)` or returning raw model objects from controller methods.
   - *Developer Choice*: Preferred returning explicit `ResponseEntity<ErrorResponse>` instances from a centralized `@RestControllerAdvice` (`GlobalExceptionHandler`).
   - *Rationale*: Using `@ResponseStatus` alone delegates error formatting to Spring's default error attributes, which lacks customization and field-level error mapping. Returning `ResponseEntity<ErrorResponse>` provides full control over dynamic HTTP status codes, explicit response headers, and uniform structured error JSON payloads (including timestamp, status code, error phrase, request path, and a populated `validationErrors` map).

2. **Rejected: Complex Database Migration Scripts (Flyway / Liquibase)**
   - *AI Suggestion*: AI recommended adding Flyway migrations for schema management.
   - *Reason for Rejection*: Unnecessary overhead for an in-memory H2 database assignment. Standard Spring Data JPA `ddl-auto=update` is cleaner and meets all requirements.

3. **Rejected: Soft Deletion (`is_deleted` column)**
   - *AI Suggestion*: AI suggested setting `@Where(clause = "is_deleted = false")` and soft-deleting records.
   - *Reason for Rejection*: Hard delete (`repository.delete(expense)`) is standard and expected for this assignment scope.
