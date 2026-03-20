package com.jayice.goblinfashionengineapi.api.domain.model;

import com.jayice.goblinfashionengineapi.api.domain.enums.QuirkOperator;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuirkTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesAndDeserializesIsActiveField() throws Exception {
        Quirk quirk = Quirk.builder()
                .id("q-1")
                .isActive(true)
                .priority(5)
                .build();

        JsonNode serialized = objectMapper.readTree(objectMapper.writeValueAsString(quirk));

        assertTrue(serialized.has("isActive"));
        assertFalse(serialized.has("active"));

        Quirk deserialized = objectMapper.readValue(
                "{\"id\":\"q-1\",\"isActive\":true,\"priority\":5}",
                Quirk.class
        );

        assertTrue(deserialized.isActive());
        assertEquals(5, deserialized.getPriority());
    }

    @Test
    void roundTripsNestedConditionsAndEffect() throws Exception {
        Quirk quirk = Quirk.builder()
                .id("q-1")
                .name("No Novelty")
                .isActive(true)
                .priority(100)
                .conditions(QuirkConditionGroup.builder()
                        .all(List.of(
                                QuirkCondition.builder().field("contexts").op(QuirkOperator.CONTAINS).value("OFFICE").build()
                        ))
                        .none(List.of(
                                QuirkCondition.builder().field("pattern").op(QuirkOperator.EQUALS).value("NOVELTY").build()
                        ))
                        .build())
                .effect(QuirkEffect.builder()
                        .action("SET")
                        .targetField("includeInEngine")
                        .value(true)
                        .build())
                .build();

        String json = objectMapper.writeValueAsString(quirk);
        Quirk roundTripped = objectMapper.readValue(json, Quirk.class);

        assertEquals(quirk, roundTripped);
    }
}
