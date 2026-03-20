package com.jayice.goblinfashionengineapi.api.domain.model;

import com.jayice.goblinfashionengineapi.api.domain.enums.QuirkOperator;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuirkConditionTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesArrayValueAsList() throws Exception {
        QuirkCondition condition = objectMapper.readValue(
                "{\"field\":\"contexts\",\"op\":\"IN\",\"value\":[\"OFFICE\",\"CASUAL\"]}",
                QuirkCondition.class
        );

        assertInstanceOf(List.class, condition.getValue());
        assertEquals(List.of("OFFICE", "CASUAL"), condition.getValue());
    }

    @Test
    void roundTripsNumericScalarValue() throws Exception {
        QuirkCondition condition = QuirkCondition.builder()
                .field("priority")
                .op(QuirkOperator.GREATER_THAN)
                .value(10)
                .build();

        String json = objectMapper.writeValueAsString(condition);
        QuirkCondition roundTripped = objectMapper.readValue(json, QuirkCondition.class);

        assertEquals(condition, roundTripped);
    }
}
