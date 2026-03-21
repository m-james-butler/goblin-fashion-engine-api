package com.jayice.goblinfashionengineapi.api.service;

import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyCategory;
import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.legacy.mapper.ShinyMapper;
import com.jayice.goblinfashionengineapi.api.legacy.model.LegacyShiny;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShinyServiceTest {

    @Test
    void validHoardIdReturnsMappedCanonicalShinies() {
        LegacyShiny legacy = new LegacyShiny();
        legacy.setId("item-1");
        legacy.setName("Blue Tee");
        legacy.setCount(1);
        legacy.setCategory("Shirts");
        legacy.setFormality("Casual");
        legacy.setAttentionLevel("Low");
        legacy.setColorPrimary("Blue");
        legacy.setPattern("Solid");
        legacy.setStatus("Owned");

        LegacyInventoryService inventoryService = new LegacyInventoryService(new ObjectMapper()) {
            @Override
            public List<LegacyShiny> loadInventory() {
                return List.of(legacy);
            }
        };

        ShinyService shinyService = new ShinyService(inventoryService, new ShinyMapper());

        List<Shiny> result = shinyService.getShiniesByHoardId("HRD-001");

        assertEquals(1, result.size());
        assertEquals("item-1", result.getFirst().getId());
        assertEquals(ShinyCategory.TOP, result.getFirst().getCategory());
    }

    @Test
    void unknownHoardIdReturnsEmptyList() {
        LegacyShiny legacy = new LegacyShiny();
        legacy.setId("item-1");

        LegacyInventoryService inventoryService = new LegacyInventoryService(new ObjectMapper()) {
            @Override
            public List<LegacyShiny> loadInventory() {
                return List.of(legacy);
            }
        };

        ShinyService shinyService = new ShinyService(inventoryService, new ShinyMapper());

        List<Shiny> result = shinyService.getShiniesByHoardId("UNKNOWN");

        assertEquals(List.of(), result);
    }
}
