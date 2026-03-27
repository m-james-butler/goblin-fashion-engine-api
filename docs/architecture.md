# Goblin Fashion Engine API - Backend Architecture

## Overview

This document reflects the backend architecture as of March 27, 2026.

The backend is now designed around Firestore as the primary persistence path for shiny CRUD operations, with Firebase Authentication as the target auth system and Cloud Run as the deployment target.

Legacy JSON inventory is retained only for optional bootstrap import into Firestore.

## Deployment and Platform

- Google Cloud Platform (GCP)
- Cloud Run deployment target
- Cloud Build + Artifact Registry for build and image pipeline
- Firestore as primary database
- Firebase Authentication for user auth
- Cloud Storage reserved for future image storage

## Runtime API Paths (Primary)

```text
GET /api/goblins/{goblinId}/hoards/{hoardId}/shinies
    -> FirebaseAuthenticationFilter (Authorization: Bearer <id-token>)
    -> FirebaseTokenVerifier (FirebaseAuth.verifyIdToken)
    -> AuthenticatedGoblin(uid)
    -> ShinyController
    -> ownership check: uid == path goblinId
    -> ShinyService
    -> ShinyFirestoreGateway
    -> Firestore path: goblins/{goblinId}/hoards/{hoardId}/shinies
    -> List<ShinyDocument>
    -> ShinyFirestoreMapper
    -> List<Shiny> (canonical domain)
    -> ShinyDtoMapper
    -> List<ShinyResponseDto>
```

```text
POST /api/goblins/{goblinId}/hoards/{hoardId}/shinies
PUT /api/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}
PATCH /api/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}
DELETE /api/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}
    -> FirebaseAuthenticationFilter (Authorization: Bearer <id-token>)
    -> FirebaseTokenVerifier (FirebaseAuth.verifyIdToken)
    -> AuthenticatedGoblin(uid)
    -> ShinyController
    -> ownership check: uid == path goblinId
    -> ShinyService
    -> ShinyFirestoreGateway
    -> Firestore path: goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}
```

This is the canonical API/data boundary flow:

```text
Filter -> AuthenticatedGoblin -> Controller -> Service -> Firestore gateway -> ShinyDocument -> ShinyFirestoreMapper -> Shiny -> ShinyDtoMapper -> ShinyResponseDto
```

### Transitional Endpoint Support

A transitional endpoint still exists:

- `GET /api/hoards/{hoardId}/shinies`

For authenticated requests, it resolves goblin ownership from Firebase UID first.
`app.default-goblin-id` remains only as an unauthenticated fallback during transition.

## Endpoint Strategy (Now vs Next)

Current primary endpoints:

- `GET /api/goblins/{goblinId}/hoards/{hoardId}/shinies`
- `POST /api/goblins/{goblinId}/hoards/{hoardId}/shinies`
- `PUT /api/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}`
- `PATCH /api/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}`
- `DELETE /api/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}`

Current compatibility endpoint:

- `GET /api/hoards/{hoardId}/shinies` (transitional only)

Current first-stage Firebase enforcement:

- keep goblin ownership authoritative
- verify `Authorization: Bearer <firebase-id-token>` with Firebase Admin SDK
- derive authoritative `goblinId` from Firebase UID
- enforce on goblin-aware shiny routes (`GET/POST/PUT/PATCH/DELETE`):
- missing token -> `401`
- invalid token -> `401`
- token uid mismatch with path goblinId -> `403`
- uid/path match -> continue
- create duplicate id -> `409` (create only)
- patch/update/delete missing shiny -> `404` (item routes)
- after frontend migration, remove transitional compatibility endpoint

This keeps ownership constraints explicit while enabling a clean move to auth-derived tenancy.

### PATCH Semantics (v1)

- endpoint: `PATCH /api/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}`
- typed DTO (`ShinyPatchRequestDto`), not JSON Patch / JSON Merge Patch
- omitted field -> unchanged
- explicit `null` -> unchanged
- non-null provided field -> updated
- explicit clearing is not supported in v1
- patch merge is owned by `ShinyService` (not controller/gateway)
- service loads existing shiny, merges patch into canonical domain object, validates merged result, and writes full merged shiny back through gateway

## Firestore Data Model

Current shiny reads use nested ownership-first pathing:

```text
goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}
```

Planned hierarchy remains:

```text
goblins/{goblinId}
goblins/{goblinId}/hoards/{hoardId}
goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}
goblins/{goblinId}/clutters/{clutterId}
goblins/{goblinId}/quirks/{quirkId}
```

Design intent in code:

- goblin ownership is first-class
- persistence is not modeled around hoard id alone
- canonical domain model remains internal
- DTO model remains external API boundary

## Firebase / Firestore Configuration

Firestore initialization is under:

- `api.persistence.firestore.config.FirebaseConfig`
- `api.persistence.firestore.config.FirebaseProperties`

Credential strategy:

- Cloud Run: Application Default Credentials (ADC)
- Local: ADC via `GOOGLE_APPLICATION_CREDENTIALS` or explicit `firebase.credentials-path`

## Legacy JSON Role (Bootstrap Only)

Legacy components are still present:

- `LegacyInventoryService`
- `legacy.mapper.ShinyMapper`

They are no longer the primary read path.

Optional bootstrap importer:

- `LegacyInventoryFirestoreBootstrapImporter`

Importer behavior:

- opt-in via `app.bootstrap.legacy-inventory.enabled=true`
- can be restricted to empty target collection (`only-if-empty=true`)
- mapping chain: `LegacyShiny -> Shiny -> ShinyDocument`
- writes to `goblins/{goblinId}/hoards/{hoardId}/shinies`

## Package Boundaries

```text
api
|-- auth
|   |-- context
|   |-- filter
|   |-- model
|   `-- service
|-- controller
|-- dto
|-- domain
|-- mapper
|-- legacy
|   |-- mapper
|   `-- model
|-- persistence
|   `-- firestore
|       |-- bootstrap
|       |-- config
|       |-- mapper
|       |-- model
|       `-- repository
`-- service
```

## Authentication Direction

Authentication is intentionally staged:

- frontend will send Firebase ID token in `Authorization: Bearer <token>`
- backend verifies Firebase ID token in a lightweight `OncePerRequestFilter`
- verified identity is stored as internal principal model (`AuthenticatedGoblin`) in request context
- full role/authority and method-level security are intentionally deferred
- token revocation checks are not enforced yet, but verifier flow is structured to add `checkRevoked=true` later

## Testing Focus

Current tests cover:

- Firebase token verification service behavior (`uid` mapping and invalid-token handling)
- controller auth responses for `401`, `403`, and success paths on goblin-aware shiny endpoints
- ownership mismatch blocking before service execution
- service behavior using mocked Firestore gateway
- Firestore mapper (`ShinyDocument <-> Shiny`) behavior
- controller DTO request/response mapping for create and update
- controller patch response mapping and patch status handling
- create behavior (`201`, `400`, `409`)
- update behavior (`200`, `400`, `404`)
- patch behavior (`200`, `400`, `404`)
- delete behavior (`204`, `404`)
- legacy mapper/service tests retained for bootstrap path safety
