package com.jayice.goblinfashionengineapi.api.service;

import com.jayice.goblinfashionengineapi.api.legacy.model.LegacyShiny;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LegacyInventoryServiceTest {

    @Test
    void loadInventoryReturnsEmptyListWhenJsonReadFails() {
        ObjectMapper objectMapper = new ObjectMapper() {
            @Override
            public <T> T readValue(InputStream source, TypeReference<T> valueTypeRef) {
                throw new IllegalArgumentException("bad json");
            }
        };

        LegacyInventoryService service = new LegacyInventoryService(objectMapper);

        List<LegacyShiny> result = service.loadInventory();

        assertEquals(List.of(), result);
    }
}
