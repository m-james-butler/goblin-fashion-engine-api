package com.jayice.goblinfashionengineapi.api.service;

import com.jayice.goblinfashionengineapi.api.legacy.model.LegacyShiny;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

@Service
public class LegacyInventoryService {

    private final ObjectMapper objectMapper;

    public LegacyInventoryService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<LegacyShiny> loadInventory() {
        try {
            ClassPathResource resource = new ClassPathResource("data/inventory.json");
            InputStream inputStream = resource.getInputStream();

            return objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<LegacyShiny>>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to load inventory.json", e);
        }
    }
}