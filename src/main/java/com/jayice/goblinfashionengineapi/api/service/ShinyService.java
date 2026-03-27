package com.jayice.goblinfashionengineapi.api.service;

import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.mapper.ShinyFirestoreMapper;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.model.ShinyDocument;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.repository.DuplicateShinyException;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.repository.ShinyDocumentNotFoundException;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.repository.ShinyFirestoreGateway;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ShinyService {
    private final ShinyFirestoreGateway shinyFirestoreGateway;
    private final ShinyFirestoreMapper shinyFirestoreMapper;

    public ShinyService(ShinyFirestoreGateway shinyFirestoreGateway, ShinyFirestoreMapper shinyFirestoreMapper) {
        this.shinyFirestoreGateway = shinyFirestoreGateway;
        this.shinyFirestoreMapper = shinyFirestoreMapper;
    }

    public List<Shiny> getShiniesByGoblinIdAndHoardId(String goblinId, String hoardId) {
        List<ShinyDocument> shinyDocuments = shinyFirestoreGateway.findByGoblinIdAndHoardId(goblinId, hoardId);
        return shinyFirestoreMapper.toCanonicalList(shinyDocuments);
    }

    public Shiny createShiny(String goblinId, String hoardId, Shiny shiny) {
        if (!StringUtils.hasText(goblinId) || !StringUtils.hasText(hoardId)) {
            throw new IllegalArgumentException("goblinId and hoardId are required.");
        }
        if (shiny == null) {
            throw new IllegalArgumentException("Shiny create payload is required.");
        }

        ShinyDocument shinyDocument = shinyFirestoreMapper.toDocument(shiny);
        try {
            ShinyDocument createdShiny = shinyFirestoreGateway.createShiny(goblinId, hoardId, shinyDocument);
            return shinyFirestoreMapper.toCanonical(createdShiny);
        } catch (DuplicateShinyException duplicateShinyException) {
            throw new ShinyAlreadyExistsException(
                    "A shiny with the same id already exists for this goblin and hoard.",
                    duplicateShinyException
            );
        }
    }

    public Shiny updateShiny(String goblinId, String hoardId, String shinyId, Shiny shiny) {
        if (!StringUtils.hasText(goblinId) || !StringUtils.hasText(hoardId) || !StringUtils.hasText(shinyId)) {
            throw new IllegalArgumentException("goblinId, hoardId, and shinyId are required.");
        }
        if (shiny == null) {
            throw new IllegalArgumentException("Shiny update payload is required.");
        }

        ShinyDocument shinyDocument = shinyFirestoreMapper.toDocument(shiny);
        try {
            ShinyDocument updatedShiny = shinyFirestoreGateway.updateShiny(goblinId, hoardId, shinyId, shinyDocument);
            return shinyFirestoreMapper.toCanonical(updatedShiny);
        } catch (ShinyDocumentNotFoundException shinyDocumentNotFoundException) {
            throw new ShinyNotFoundException(
                    "Shiny not found for this goblin, hoard, and shiny id.",
                    shinyDocumentNotFoundException
            );
        }
    }

    public void deleteShiny(String goblinId, String hoardId, String shinyId) {
        if (!StringUtils.hasText(goblinId) || !StringUtils.hasText(hoardId) || !StringUtils.hasText(shinyId)) {
            throw new IllegalArgumentException("goblinId, hoardId, and shinyId are required.");
        }

        try {
            shinyFirestoreGateway.deleteShiny(goblinId, hoardId, shinyId);
        } catch (ShinyDocumentNotFoundException shinyDocumentNotFoundException) {
            throw new ShinyNotFoundException(
                    "Shiny not found for this goblin, hoard, and shiny id.",
                    shinyDocumentNotFoundException
            );
        }
    }
}
