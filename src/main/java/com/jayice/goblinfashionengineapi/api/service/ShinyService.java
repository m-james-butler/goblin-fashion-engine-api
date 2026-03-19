package com.jayice.goblinfashionengineapi.api.service;

import com.jayice.goblinfashionengineapi.api.domain.enums.*;
import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.legacy.model.LegacyShiny;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShinyService {
    private final LegacyInventoryService legacyInventoryService;

    public ShinyService(LegacyInventoryService legacyInventoryService) {
        this.legacyInventoryService = legacyInventoryService;
    }

    public List<LegacyShiny> getShiniesByHoardId(String hoardId) {
        return legacyInventoryService.loadInventory();
    }
}
