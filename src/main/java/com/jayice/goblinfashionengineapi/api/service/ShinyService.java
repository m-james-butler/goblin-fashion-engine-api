package com.jayice.goblinfashionengineapi.api.service;

import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.legacy.mapper.ShinyMapper;
import com.jayice.goblinfashionengineapi.api.legacy.model.LegacyShiny;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShinyService {
    private static final String TRANSITIONAL_HOARD_ID = "HRD-001";

    private final LegacyInventoryService legacyInventoryService;
    private final ShinyMapper shinyMapper;

    public ShinyService(LegacyInventoryService legacyInventoryService, ShinyMapper shinyMapper) {
        this.legacyInventoryService = legacyInventoryService;
        this.shinyMapper = shinyMapper;
    }

    public List<Shiny> getShiniesByHoardId(String hoardId) {
        if (!TRANSITIONAL_HOARD_ID.equalsIgnoreCase(hoardId)) {
            return List.of();
        }

        List<LegacyShiny> legacyShinies = legacyInventoryService.loadInventory();
        return shinyMapper.toCanonicalList(legacyShinies);
    }
}
