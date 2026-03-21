# Goblin Fashion Engine API - Current Backend Architecture

## Overview

This document reflects the current implementation in this repository as of March 20, 2026.

The backend is a Spring Boot service that currently provides:

- application startup and HTTP API exposure
- a simple health endpoint
- a hoard shinies endpoint backed by classpath JSON data
- canonical domain model and enum definitions used by the live shiny response path
- a legacy-to-canonical mapper layer for inventory normalization

The service is still in a transitional phase because source data remains legacy-shaped, but API output is now canonical.

## Technology Stack (Current)

- Java 21
- Spring Boot 4.0.3
- Maven
- Spring Web MVC
- Spring Boot Actuator
- Spring Validation
- Lombok
- Jackson (via `tools.jackson.*` packages in code)
- JaCoCo (coverage report plugin configured in `pom.xml`)

## Current Package Structure

```text
com.jayice.goblinfashionengineapi
|-- GoblinFashionEngineApiApplication
`-- api
    |-- controller
    |   |-- HealthController
    |   `-- ShinyController
    |-- domain
    |   |-- enums
    |   `-- model
    |-- legacy
    |   |-- mapper
    |   `-- model
    `-- service
        |-- LegacyInventoryService
        `-- ShinyService
```

Not currently present in the implementation:

- repository layer
- dto layer
- exception package
- config package

## Runtime Architecture (Current Flow)

The implemented shiny data flow is:

```text
GET /api/hoards/{hoardId}/shinies
    -> ShinyController
    -> ShinyService
    -> LegacyInventoryService
    -> src/main/resources/data/inventory.json
    -> List<LegacyShiny>
    -> ShinyMapper (legacy.mapper)
    -> List<Shiny> response
```

Important current behavior:

- Transitional hoard filtering is implemented in `ShinyService` with a single valid hoard id: `HRD-001`.
- If `hoardId` matches `HRD-001` (case-insensitive), the endpoint returns the full mapped inventory.
- Any other `hoardId` returns an empty list.
- Unknown `hoardId` currently returns `200` with `[]` (no 404 handling yet).
- The endpoint returns canonical `Shiny` objects.
- `ShinyMapper` normalizes and maps legacy string fields to canonical enums with fail-safe handling.
- Optional `Shiny` fields with `null` values are omitted from JSON output via `@JsonInclude(JsonInclude.Include.NON_NULL)`.
- Mapper fallback behavior: if legacy `name` is `null`, canonical `name` is mapped from legacy `id`.

## Implemented Endpoints

### `GET /health`

Implemented in `HealthController`.

Current response shape:

```json
{
  "status": "ok",
  "service": "Goblin Fashion Engine API",
  "version": "0.0.1"
}
```

### `GET /api/hoards/{hoardId}/shinies`

Implemented in `ShinyController` and `ShinyService`.

Current behavior:

- loads all records from `data/inventory.json`
- maps `List<LegacyShiny>` to `List<Shiny>` through `ShinyMapper`
- returns mapped `List<Shiny>` only for `hoardId=HRD-001` (case-insensitive)
- returns `[]` for any other `hoardId`

## Domain Model (Implemented)

Canonical model classes currently defined under `api/domain/model`:

- `Goblin`
- `Hoard`
- `Shiny`
- `Clutter`
- `ClutterItem`
- `Quirk`
- `QuirkCondition`
- `QuirkConditionGroup`
- `QuirkEffect`

Notes:

- `Hoard` and `Quirk` include explicit `@JsonProperty` handling for `is...` boolean fields.
- `Shiny` includes `@JsonInclude(JsonInclude.Include.NON_NULL)` so optional null fields are omitted from responses.
- These canonical models are tested for JSON serialization behavior in unit tests.

## Enum Model (Implemented)

Enums currently defined under `api/domain/enums`:

- `Attention`
- `ClutterItemRole`
- `ClutterSource`
- `ClutterStatus`
- `Color`
- `Context`
- `EngineInclusionPolicy`
- `Formality`
- `Layer`
- `Pattern`
- `QuirkOperator`
- `QuirkRuleType`
- `QuirkScopeType`
- `ShinyCategory`
- `ShinyStatus`

## Legacy Data Path

Current legacy ingestion implementation:

```text
src/main/resources/data/inventory.json
    -> LegacyInventoryService.loadInventory()
    -> ObjectMapper.readValue(..., List<LegacyShiny>)
```

`LegacyShiny` currently includes string-based fields such as:

- category and context strings
- formality and attention-level strings
- color and pattern strings
- booleans and metadata fields

## Resource Layout

```text
src/main/resources
|-- application.properties
`-- data
    `-- inventory.json
```

`application.properties` currently sets:

- `spring.application.name=goblin-fashion-engine-api`
- exposed actuator endpoints: `health,info`
- health detail visibility: `always`

## Testing State (Current)

Current test coverage includes:

- application context load test
- domain model JSON round-trip tests (`Shiny`, `Clutter`, `Quirk`, `QuirkCondition`, `QuirkEffect`)
- boolean `is...` JSON field behavior tests (`Hoard`, `Quirk`)
- invalid enum value rejection test (`Shiny`)
- legacy mapper unit tests (`ShinyMapperTest`) including normalization, null safety, and inventory mapping checks
- service tests (`ShinyServiceTest`) verifying:
  - valid transitional hoard id (`HRD-001`) returns mapped canonical shinies
  - unknown hoard id returns empty list
- controller endpoint tests (`ShinyControllerTest`) verifying:
  - `GET /api/hoards/HRD-001/shinies` returns `200` with a canonical shiny JSON array
  - `GET /api/hoards/UNKNOWN/shinies` returns `200` with `[]`

Not currently covered by tests:

- legacy inventory loading error scenarios

## Current Gaps / Transitional Areas

Not yet implemented in code:

- persistence layer (Firestore or other repository)
- authentication/authorization enforcement
- DTO boundary for public API contracts
- quirk rule evaluation engine
- clutter generation/orchestration
- OpenAI integration
- image processing/storage integration

## Near-Term Recommended Architecture Steps

1. Introduce DTOs before external contract stabilization.
2. Add legacy data quality reporting for unmapped/unknown enum values.
3. Evaluate upgrading JaCoCo/JDK test tooling compatibility to stabilize full test execution.
4. Add legacy inventory loading error scenario tests.
5. Replace transitional hoard-id gate with repository-backed hoard ownership filtering.
