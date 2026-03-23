package com.jayice.goblinfashionengineapi.api.auth.model;

/**
 * Internal authenticated principal for goblin tenancy.
 *
 * @param goblinId authoritative goblin identity derived from Firebase UID
 */
public record AuthenticatedGoblin(String goblinId) {
}
