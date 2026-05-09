# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 4.0.2 warehouse management system API with Java 21, PostgreSQL, JWT auth. Feature-based package structure with layered architecture (controller → service → repository).

## Build Commands

```bash
mvn spring-boot:run              # Run application
mvn test                         # Run all tests
mvn test -Dtest=Class#method     # Run single test
mvn clean install                # Build
```

## Architecture

### Feature-Based Structure
Code organized by domain under `src/main/java/com/kit/wmsbackend/feature/`:
- `auth/` - JWT authentication
- `user/`, `role/`, `permission/` - User management & RBAC
- `product/`, `warehouse/`, `inventory/` - Catalog & stock
- `stocktransaction/`, `stocktransactionhistory/` - Transactions & audit
- `inventorymovement/` - Movement tracking

Each feature has: `controller/`, `service/`, `repository/`, `dto/`, `listqueryfieldconfig/`

### Core Layers
- **Controllers** (`@RestController` + `@ApiPrefix`): DTOs only, delegate to services
- **Services** (`@Service`): Business logic, `@Transactional`, constructor injection with `private final` fields
- **Repositories** (extend `BaseAuditRepository<T>`): Spring Data JPA, soft deletes via `deletedAt`
- **DTOs**: `*Request`/`*Response` with Bean Validation, mapped via MapStruct

### Key Patterns

**List Query Pattern** - Unified filtering/sorting/searching/pagination
- Each feature implements `ListQueryFieldConfig<T>` defining searchable/sortable/filterable fields
- `QueryService<T>` orchestrates via `FilterSpecification<T>` using JPA Criteria API
- Filter operators: `equals`, `greaterThan`, `lessThan`, `greaterThanOrEqualTo`, `lessThanOrEqualTo`

**Soft Delete** - `BaseAuditEntity` with `deletedAt`/`deletedBy`, queries auto-exclude via `BaseSpecification.notDeleted()`

**Audit Trail** - `BaseAuditEntity` tracks `createdAt`/`updatedAt`/`deletedAt` and `*By` fields via Spring Data Auditing

**Stock Transactions** - Types: IMPORT/EXPORT/ADJUSTMENT, status: DRAFT → CONFIRMED → COMPLETED, codes: `IMP-000001`/`EXP-000001`/`ADJ-000001`

### Key Relationships
- `StockTransaction` → `StockTransactionItem` (1:N)
- `StockTransaction` → `StockTransactionHistory` (1:N) for audit
- `StockTransaction` → `Inventory` (N:1)
- `User` → `UserWarehouse` (1:N)

## Tech Stack

Spring Boot 4.0.2, Spring Data JPA, Spring Security, JWT (JJWT 0.13.0), MapStruct 1.6.3, Flyway, Lombok, SpringDoc OpenAPI 3.0.2, PostgreSQL

## Configuration

### Required Environment Variables
```yaml
DB_URL, DB_USERNAME, DB_PASSWORD           # PostgreSQL
JWT_SECRET                                  # Min 32 chars
JWT_EXPIRATION, JWT_REFRESH_EXPIRATION     # Token TTLs (15min, 7days)
API_PREFIX                                  # Default: /api
SERVER_URL, CLIENT_URL                      # For links & CORS
MAIL_HOST, MAIL_USERNAME, MAIL_PASSWORD    # SMTP
SEQUENCE_*                                  # Code prefixes (IMP, EXP, ADJ)
```

### Security
- JWT in cookies (ACCESS_TOKEN, REFRESH_TOKEN), stateless sessions
- Method-level: `@PreAuthorize("hasRole('ROLE_NAME')")` or `hasAuthority('PERMISSION_NAME')`
- BCrypt strength 12

## Database

### Migrations
Flyway in `src/main/resources/db/migration/` (V1__*.sql, V2__*.sql, etc.). Never modify existing migrations.

### Key Tables
`users`, `roles`, `permissions`, `user_warehouses`, `products`, `inventories`, `stock_transactions`, `stock_transaction_items`, `stock_transaction_history`, `inventory_movements`

## Development Tasks

### Adding a Feature
1. Create `feature/{name}/` with subdirs: `controller/`, `service/`, `repository/`, `dto/`, `listqueryfieldconfig/`
2. Entity extends `BaseAuditEntity` in `entity/`
3. Repository extends `BaseAuditRepository<T>`
4. Service interface + impl with `@Transactional`
5. DTOs with validation annotations
6. Implement `ListQueryFieldConfig<T>` for list operations
7. Controller with `@ApiPrefix`
8. Flyway migration for tables
9. MapStruct mapper for entity ↔ DTO

### Adding List Query Fields
Update `ListQueryFieldConfig`: add to `searchableFields()`, `sortableFields()`, `filterableFields()` with appropriate strategies

### Authorization
`@PreAuthorize("hasRole('ROLE_NAME')")` or `hasAuthority('PERMISSION_NAME')` on controller methods

### Database Migrations
Create `V{N}__description.sql` in `src/main/resources/db/migration/`, PostgreSQL syntax, auto-runs on startup

## Conventions

- **DTOs**: `*Request`, `*Response`, `*CreateRequest`, `*UpdateRequest`
- **Dependencies**: Constructor injection, `private final`
- **Validation**: Bean Validation on DTOs
- **Transactions**: `@Transactional` at service method level
- **Errors**: Throw `AppException` with `ErrorCode` enum
- **Comments**: Only when WHY is non-obvious

## API Response Format

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { }
}
```

## Key Files

- Main: `WmsBackendApplication.java`
- Security: `config/SecurityConfig.java`
- Base entities: `entity/BaseAuditEntity.java`, `entity/BaseEntity.java`
- Specifications: `specification/FilterSpecification.java`, `specification/SearchSpecification.java`
- Config: `src/main/resources/application.yaml`
