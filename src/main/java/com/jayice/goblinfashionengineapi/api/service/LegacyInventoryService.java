package com.jayice.goblinfashionengineapi.api.service;

import com.jayice.goblinfashionengineapi.api.legacy.model.LegacyShiny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

@Service
public class LegacyInventoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyInventoryService.class);
    private static final String INVENTORY_FILE_PATH = "data/inventory.json";

    private final ObjectMapper objectMapper;

    public LegacyInventoryService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<LegacyShiny> loadInventory() {
        try {
            ClassPathResource resource = new ClassPathResource(INVENTORY_FILE_PATH);
            try (InputStream inputStream = resource.getInputStream()) {
                List<LegacyShiny> legacyShinies = objectMapper.readValue(
                        inputStream,
                        new TypeReference<List<LegacyShiny>>() {}
                );
                return legacyShinies == null ? List.of() : legacyShinies;
            }
        } catch (Exception exception) {
            LOGGER.error(
                    "Failed to load legacy inventory from '{}' due to '{}'. Returning empty list.",
                    INVENTORY_FILE_PATH,
                    exception.getMessage(),
                    exception
            );
            return List.of();
        }
    }
}
