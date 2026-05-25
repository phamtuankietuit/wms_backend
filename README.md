# Warehouse Management System Backend

## Overview

Warehouse Management System (WMS) is a backend service designed to support
warehouse, product, and inventory management in a clear, structured, and
traceable way. The project helps organizations track stock levels across
multiple warehouses, manage product information, variants, images, users,
roles, permissions, and consistently record inventory-related changes.

The system focuses on core warehouse operations:

- Manage warehouses, products, attributes, attribute values, and product variants.
- Track inventory by warehouse and item.
- Handle import, export, and adjustment stock transactions with status history.
- Record inventory movement history to support accuracy and traceability.
- Control access through users, roles, permission groups, and permission codes.
- Support authentication, refresh tokens, forgot password flow, email templates, and media uploads.

The project aims to provide a practical backend API for common warehouse
management workflows, with a focus on accuracy, traceability, scalability, and
a stable experience for clients such as admin dashboards or frontend
applications.

## Tech Stack

- Language: Java 21
- Framework: Spring Boot 4.0.2, Spring Web MVC, Spring Data JPA, Hibernate,
  Spring Security
- Database: PostgreSQL
- Migration: Flyway
- Documentation: Springdoc OpenAPI / Swagger UI
- Mapping: MapStruct
- Mail templates: Thymeleaf
- Storage: Cloudinary
- Containerization: Docker, Docker Compose
- Build tool: Maven Wrapper

The project documentation also defines the frontend ecosystem as TypeScript,
Next.js, Shadcn UI, Zustand, TanStack Query, Axios, Zod, and React Hook Form.
This repository is responsible for the backend API.

## Main Modules

Main source code is located in `src/main/java/com/kit/wmsbackend`.
Feature-specific code is organized by domain under `feature/<domain>`,
including:

- `auth`: login, refresh token, forgot password, reset password, and current user information.
- `warehouse`: warehouse management.
- `product`, `variant`, `attribute`, `attributevalue`: product catalog and variant management.
- `inventory`, `inventorymovement`: inventory tracking and inventory movement history.
- `stocktransaction`, `stocktransactionhistory`: import, export, adjustment transactions, and processing history.
- `user`, `role`, `permission`, `permissiongroup`, `userwarehouse`: user management and authorization.
- `media`: media asset management through Cloudinary.
- `mail`: email delivery and template rendering.

## API

By default, controllers annotated with `@ApiPrefix` use the `/api` prefix.
This can be configured with the `API_PREFIX` environment variable.

Key endpoint groups include:

- `/api/warehouses`
- `/api/products`
- `/api/inventories`
- `/api/stock-transactions`
- `/api/users`
- `/api/roles`
- `/api/permissions`

Authentication endpoints are currently declared separately:

- `/login`
- `/refresh-token`
- `/forgot-password`
- `/reset-password`
- `/me`

Swagger UI is available when the application is running with the Springdoc
dependency:

```text
http://localhost:8080/swagger-ui/index.html
```

## Project Structure

```text
src/main/java/com/kit/wmsbackend
├── config              # Spring, Security, OpenAPI, and Cloudinary configuration
├── entity              # Shared JPA entities
├── exception           # Shared error and exception handling
├── feature             # Domain-based business modules
├── mapper              # MapStruct mappers
├── repository          # Shared repositories
├── security            # JWT, filters, user details, and permission handling
├── service             # Shared services
├── specification       # Query specifications
└── validator           # Bean validation and custom validators
```

Application resources are located in `src/main/resources`:

- `application.yaml`, `application-dev.yaml`, `application-prod.yaml`: profile-based configuration.
- `db/migration`: Flyway migrations.
- `templates`: Thymeleaf email templates.
- `static`: static assets.

## Run Locally

Requirements:

- Java 21
- Docker and Docker Compose

Start the local PostgreSQL database:

```powershell
docker compose up -d db
```

Run tests:

```powershell
.\mvnw.cmd test
```

Build the artifact:

```powershell
.\mvnw.cmd clean package
```

Run the application:

```powershell
.\mvnw.cmd spring-boot:run
```

The server runs at:

```text
http://localhost:8080
```

## Configuration

Main configuration values are externalized through YAML profiles and
environment variables:

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET`, `JWT_EXPIRATION`, `JWT_REFRESH_EXPIRATION`
- `CLIENT_URL`, `SERVER_URL`, `API_PREFIX`
- `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`
- `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`

Do not commit real secrets to the repository. Local configuration can be set in
`.env` or environment variables depending on the runtime environment.
