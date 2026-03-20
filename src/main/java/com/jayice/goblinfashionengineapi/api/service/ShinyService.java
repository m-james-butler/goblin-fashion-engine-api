package com.jayice.goblinfashionengineapi.api.service;

import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.legacy.mapper.ShinyMapper;
import com.jayice.goblinfashionengineapi.api.legacy.model.LegacyShiny;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShinyService {
    private final LegacyInventoryService legacyInventoryService;
    private final ShinyMapper shinyMapper;

    public ShinyService(LegacyInventoryService legacyInventoryService, ShinyMapper shinyMapper) {
        this.legacyInventoryService = legacyInventoryService;
        this.shinyMapper = shinyMapper;
    }

    public List<Shiny> getShiniesByHoardId(String hoardId) {
        List<LegacyShiny> legacyShinies = legacyInventoryService.loadInventory();
        return shinyMapper.toCanonicalList(legacyShinies);
    }
}
