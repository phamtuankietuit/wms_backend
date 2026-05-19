# Repository Guidelines

## Project Structure & Module Organization

This is a Java 21 Spring Boot backend built with Maven. Application code lives under `src/main/java/com/kit/wmsbackend`. Shared infrastructure is split into packages such as `config`, `security`, `exception`, `entity`, `repository`, `service`, `mapper`, `validator`, and `specification`. Domain modules live under `feature/<domain>` with local `controller`, `dto`, `repository`, and `service` subpackages.

Resources are in `src/main/resources`: profile configuration in `application.yaml`, `application-dev.yaml`, and `application-prod.yaml`; Flyway migrations in `db/migration`; Thymeleaf email templates in `templates`; static assets in `static`. Tests mirror the main package under `src/test/java/com/kit/wmsbackend`.

## Build, Test, and Development Commands

- `.\mvnw.cmd clean package` builds, runs tests, and creates the artifact in `target/`.
- `.\mvnw.cmd test` runs the JUnit test suite.
- `.\mvnw.cmd spring-boot:run` starts the API locally using the active Spring profile.
- `docker compose up -d db` starts the local PostgreSQL service defined in `docker-compose.yml`.
- `docker compose down` stops the local database container.

Use the Maven wrapper so builds use the expected Maven version.

## Coding Style & Naming Conventions

Use 4-space indentation for Java and keep package names lowercase. Classes should use role suffixes such as `WarehouseController`, `WarehouseService`, `WarehouseServiceImpl`, `WarehouseRepository`, `WarehouseRequest`, and `WarehouseResponse`. Prefer constructor injection, `private final` dependencies, DTOs at API boundaries, Bean Validation on request DTOs, and MapStruct mappers where mappings already exist. Keep feature-specific code inside `feature/<domain>` unless it is genuinely shared.

## Testing Guidelines

Tests use JUnit 5 with Spring Boot test support and Spring Security test utilities. Name test classes after the unit under test, such as `StockTransactionServiceImplTest`. Add unit tests for service validation and state transitions, and use Spring test slices or `@SpringBootTest` only when the Spring context is needed. Run `.\mvnw.cmd test` before opening a PR.

## Commit & Pull Request Guidelines

Recent history uses short Conventional Commit-style subjects, especially `feat:` and `fix:`. Keep commits focused and use subjects like `feat: add warehouse activation endpoint` or `fix: validate stock transaction quantities`.

PRs should include a concise description, linked issue or ticket when available, test evidence, and notes for database or configuration changes. Include migration details when adding files under `src/main/resources/db/migration`.

## Security & Configuration Tips

Do not commit real secrets. Local database values are read from `.env` by Docker Compose and Spring configuration should remain externalized through YAML profiles or environment variables. Review security-sensitive changes in `config`, `security`, permissions, JWT handling, and validators carefully.
