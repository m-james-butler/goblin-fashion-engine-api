# Goblin Fashion Engine API - Backend Architecture

Last verified: March 28, 2026

## Overview

`goblin-fashion-engine-api` is a Spring Boot backend using Firestore for shiny persistence and Firebase Authentication for tenancy/authentication.

Primary design goal:
- enforce goblin ownership from verified Firebase identity
- keep canonical domain internal
- expose DTOs only at API boundary

## Runtime Request Flow

Primary endpoint shape:
- `/api/goblins/{goblinId}/hoards/{hoardId}/shinies`
- `/api/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}`

Execution flow:

`ApiRequestLoggingFilter -> FirebaseAuthenticationFilter -> ShinyController -> ShinyService -> ShinyFirestoreGateway -> Firestore -> ShinyFirestoreMapper -> domain Shiny -> ShinyDtoMapper -> response DTO`

## Authentication and Tenancy

- Bearer token source: `Authorization: Bearer <firebase-id-token>`
- Verification: `FirebaseTokenVerifier` (Firebase Admin SDK)
- Request context principal: `AuthenticatedGoblin`
- Controller ownership enforcement: path `goblinId` must equal authenticated UID

Auth responses on goblin-aware routes:
- missing token -> `401`
- invalid token -> `401`
- UID/path mismatch -> `403`

## Endpoint Inventory

Primary CRUD endpoints:
- `GET /api/goblins/{goblinId}/hoards/{hoardId}/shinies`
- `GET /api/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}`
- `POST /api/goblins/{goblinId}/hoards/{hoardId}/shinies`
- `PUT /api/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}`
- `PATCH /api/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}`
- `DELETE /api/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}`

Transitional compatibility endpoint (still present):
- `GET /api/hoards/{hoardId}/shinies`

## Data and Mapping Boundaries

Internal canonical model:
- `api.domain.model.Shiny`

Persistence model:
- `api.persistence.firestore.model.ShinyDocument`

External API model:
- `api.dto.ShinyResponseDto` and request DTOs

Mapping layers:
- `ShinyFirestoreMapper`: Firestore document <-> canonical domain
- `ShinyDtoMapper`: canonical domain <-> API DTO

## Firestore Structure

Current ownership-first pathing:

`goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}`

This keeps tenancy explicit and avoids hoard-only global addressing.

## Logging and Observability

Server logging is structured around request correlation and privacy-safe fields:

- Request lifecycle filter: `ApiRequestLoggingFilter`
  - generates/propagates `X-Request-Id`
  - adds request id to MDC
  - logs API method/path/status/duration
- Auth filter logs: `FirebaseAuthenticationFilter`
  - logs auth outcomes with sanitized path and masked identifiers
  - never logs raw token value
- Service logs: `ShinyService`
  - logs operation lifecycle (fetch/create/update/patch/delete)
  - logs masked identifiers only
- Sanitization helper: `LoggingSanitizer`

Logging config in `application.properties`:
- `logging.pattern.console` includes request id (`%X{requestId}`)
- app logging level defaults and auth-filter debug override are configured

## Legacy Inventory Role

Legacy JSON inventory remains bootstrap-only support, not primary runtime data source:
- `LegacyInventoryService`
- `LegacyInventoryFirestoreBootstrapImporter`

Bootstrapping writes into canonical Firestore tenancy path and is controlled by app properties.

## Testing Focus

Current automated tests cover:
- auth filter behavior (`401`/`403`/success)
- shiny controller CRUD and patch scenarios
- service behavior with mocked persistence
- mapper behavior
- Firebase token verifier behavior

Note: local test runs on Java 24 may require `-Djacoco.skip=true` due JaCoCo instrumentation limitations in this environment.
