# Goblin Fashion Engine Bruno Smoke Collection

## Folder and Request Order
- `Shiny CRUD Smoke/01 - Create Shiny`
- `Shiny CRUD Smoke/02 - Read Shiny (After Create)`
- `Shiny CRUD Smoke/03 - Put Replace Shiny`
- `Shiny CRUD Smoke/04 - Read Shiny (After Put)`
- `Shiny CRUD Smoke/05 - Patch Shiny`
- `Shiny CRUD Smoke/06 - Read Shiny (After Patch)`
- `Shiny CRUD Smoke/07 - Delete Shiny`
- `Shiny CRUD Smoke/08 - Confirm Deleted`

## Environment Variables
Use `environments/local.bru`:
- `baseUrl`
- `firebaseToken`
- `goblinId`
- `hoardId`
- `shinyId`

All requests include:
- `Authorization: Bearer {{firebaseToken}}`

## Important Endpoint Note
The backend now exposes item-read at:
`GET /api/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}`

Read/confirm smoke steps call the item endpoint directly.

## Payload Shape Note
The create/put/patch requests use your exact payload shapes.
If your current backend validation requires additional fields (for example `name`), add them to the two full payloads and keep the same lifecycle sequence.

## How to Run
1. Open the `bruno/goblin-fashion-engine-smoke` collection in Bruno.
2. Select the `local` environment and fill in token + IDs.
3. Run requests `01` through `08` in sequence (or run the folder in order).
