package com.jayice.goblinfashionengineapi.api.domain.model;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QuirkEffectTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesObjectValueAsMap() throws Exception {
        QuirkEffect effect = objectMapper.readValue(
                "{\"action\":\"SET\",\"targetField\":\"warmth\",\"value\":{\"min\":2,\"max\":5}}",
                QuirkEffect.class
        );

        assertInstanceOf(Map.class, effect.getValue());
        assertEquals(Map.of("min", 2, "max", 5), effect.getValue());
    }

    @Test
    void roundTripsScalarValue() throws Exception {
        QuirkEffect effect = QuirkEffect.builder()
                .action("SET")
                .targetField("attention")
                .value("HIGH")
                .build();

        String json = objectMapper.writeValueAsString(effect);
        QuirkEffect roundTripped = objectMapper.readValue(json, QuirkEffect.class);

        assertEquals(effect, roundTripped);
    }
}
