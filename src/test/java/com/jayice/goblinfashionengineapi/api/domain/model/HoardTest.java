package com.jayice.goblinfashionengineapi.api.domain.model;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

class HoardTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesAndDeserializesIsPrefixedBooleanFields() throws Exception {
        Hoard hoard = Hoard.builder()
                .id("h-1")
                .isDefault(true)
                .isActive(false)
                .build();

        JsonNode serialized = objectMapper.readTree(objectMapper.writeValueAsString(hoard));

        assertTrue(serialized.has("isDefault"));
        assertTrue(serialized.has("isActive"));
        assertFalse(serialized.has("default"));
        assertFalse(serialized.has("active"));

        Hoard deserialized = objectMapper.readValue(
                "{\"id\":\"h-1\",\"isDefault\":true,\"isActive\":false}",
                Hoard.class
        );

        assertTrue(deserialized.isDefault());
        assertFalse(deserialized.isActive());
    }
}
