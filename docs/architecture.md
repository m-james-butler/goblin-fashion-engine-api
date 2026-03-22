# Goblin Fashion Engine API - Backend Architecture

## Overview

This document reflects the backend architecture as of March 22, 2026.

The backend is now designed around Firestore as the primary persistence path for shiny reads, with Firebase Authentication as the target auth system and Cloud Run as the deployment target.

Legacy JSON inventory is retained only for optional bootstrap import into Firestore.

## Deployment and Platform

- Google Cloud Platform (GCP)
- Cloud Run deployment target
- Cloud Build + Artifact Registry for build and image pipeline
- Firestore as primary database
- Firebase Authentication for user auth
- Cloud Storage reserved for future image storage

## Runtime Read Path (Primary)

```text
GET /api/goblins/{goblinId}/hoards/{hoardId}/shinies
    -> ShinyController
    -> ShinyService
    -> ShinyFirestoreGateway
    -> Firestore path: goblins/{goblinId}/hoards/{hoardId}/shinies
    -> List<ShinyDocument>
    -> ShinyFirestoreMapper
    -> List<Shiny> (canonical domain)
    -> ShinyDtoMapper
    -> List<ShinyResponseDto>
```

This is the canonical API/data boundary flow:

```text
Controller -> Service -> Firestore gateway -> ShinyDocument -> ShinyFirestoreMapper -> Shiny -> ShinyDtoMapper -> ShinyResponseDto
```

### Transitional Endpoint Support

A transitional endpoint still exists:

- `GET /api/hoards/{hoardId}/shinies`

It resolves the goblin id through `app.default-goblin-id` and delegates to the same Firestore-backed service path.

## Endpoint Strategy (Now vs Next)

Current primary endpoint:

- `GET /api/goblins/{goblinId}/hoards/{hoardId}/shinies`

Current compatibility endpoint:

- `GET /api/hoards/{hoardId}/shinies` (transitional only)

Near-term target with Firebase token verification:

- keep goblin ownership authoritative
- derive `goblinId` from verified Firebase token claims where possible
- compare path `goblinId` (if provided) against token-derived ownership and reject mismatches
- after frontend migration, remove transitional compatibility endpoint

This keeps ownership constraints explicit while enabling a clean move to auth-derived tenancy.

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
- backend is now Firebase Admin-ready for clean token verification integration
- full auth enforcement/filter chain is intentionally deferred

## Testing Focus

Current tests cover:

- service behavior using mocked Firestore gateway
- Firestore mapper (`ShinyDocument <-> Shiny`) behavior
- controller DTO response shape for goblin-aware shiny endpoint
- legacy mapper/service tests retained for bootstrap path safety
