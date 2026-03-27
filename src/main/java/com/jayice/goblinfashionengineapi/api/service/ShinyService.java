package com.jayice.goblinfashionengineapi.api.service;

import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.domain.model.ShinyPatch;
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

    public Shiny patchShiny(String goblinId, String hoardId, String shinyId, ShinyPatch shinyPatch) {
        if (!StringUtils.hasText(goblinId) || !StringUtils.hasText(hoardId) || !StringUtils.hasText(shinyId)) {
            throw new IllegalArgumentException("goblinId, hoardId, and shinyId are required.");
        }
        if (shinyPatch == null) {
            throw new IllegalArgumentException("Shiny patch payload is required.");
        }
        if (shinyPatch.getStatus() == null
                && shinyPatch.getImagePath() == null
                && shinyPatch.getNotes() == null
                && shinyPatch.getIncludeInEngine() == null
                && shinyPatch.getAttentionLevel() == null) {
            throw new IllegalArgumentException("At least one patch field must be provided.");
        }

        try {
            ShinyDocument existingDocument = shinyFirestoreGateway.getShiny(goblinId, hoardId, shinyId);
            Shiny existingShiny = shinyFirestoreMapper.toCanonical(existingDocument);
            Shiny mergedShiny = mergePatch(existingShiny, shinyPatch, goblinId, hoardId, shinyId);
            validateMergedShiny(mergedShiny);
            ShinyDocument updatedShiny = shinyFirestoreGateway.updateShiny(
                    goblinId,
                    hoardId,
                    shinyId,
                    shinyFirestoreMapper.toDocument(mergedShiny)
            );
            return shinyFirestoreMapper.toCanonical(updatedShiny);
        } catch (ShinyDocumentNotFoundException shinyDocumentNotFoundException) {
            throw new ShinyNotFoundException(
                    "Shiny not found for this goblin, hoard, and shiny id.",
                    shinyDocumentNotFoundException
            );
        }
    }

    private Shiny mergePatch(Shiny existingShiny, ShinyPatch shinyPatch, String goblinId, String hoardId, String shinyId) {
        Shiny.ShinyBuilder mergedBuilder = Shiny.builder()
                .id(shinyId)
                .goblinId(goblinId)
                .hoardId(hoardId)
                .name(existingShiny.getName())
                .count(existingShiny.getCount())
                .category(existingShiny.getCategory())
                .subcategory(existingShiny.getSubcategory())
                .layer(existingShiny.getLayer())
                .contexts(existingShiny.getContexts())
                .formality(existingShiny.getFormality())
                .attention(existingShiny.getAttention())
                .colorPrimary(existingShiny.getColorPrimary())
                .colorSecondary(existingShiny.getColorSecondary())
                .pattern(existingShiny.getPattern())
                .fabric(existingShiny.getFabric())
                .fit(existingShiny.getFit())
                .warmth(existingShiny.getWarmth())
                .officeOk(existingShiny.isOfficeOk())
                .publicWear(existingShiny.isPublicWear())
                .includeInEngine(existingShiny.isIncludeInEngine())
                .engineInclusionPolicy(existingShiny.getEngineInclusionPolicy())
                .imagePath(existingShiny.getImagePath())
                .status(existingShiny.getStatus())
                .notes(existingShiny.getNotes())
                .createdAt(existingShiny.getCreatedAt())
                .updatedAt(existingShiny.getUpdatedAt());

        if (shinyPatch.getStatus() != null) {
            mergedBuilder.status(shinyPatch.getStatus());
        }
        if (shinyPatch.getImagePath() != null) {
            mergedBuilder.imagePath(shinyPatch.getImagePath());
        }
        if (shinyPatch.getNotes() != null) {
            mergedBuilder.notes(shinyPatch.getNotes());
        }
        if (shinyPatch.getIncludeInEngine() != null) {
            mergedBuilder.includeInEngine(shinyPatch.getIncludeInEngine());
        }
        if (shinyPatch.getAttentionLevel() != null) {
            mergedBuilder.attention(shinyPatch.getAttentionLevel());
        }

        return mergedBuilder.build();
    }

    private void validateMergedShiny(Shiny mergedShiny) {
        if (!StringUtils.hasText(mergedShiny.getId())
                || !StringUtils.hasText(mergedShiny.getGoblinId())
                || !StringUtils.hasText(mergedShiny.getHoardId())) {
            throw new IllegalArgumentException("Merged shiny must include id, goblinId, and hoardId.");
        }
        if (!StringUtils.hasText(mergedShiny.getName())) {
            throw new IllegalArgumentException("Merged shiny must include a non-blank name.");
        }
        if (mergedShiny.getCount() < 1) {
            throw new IllegalArgumentException("Merged shiny count must be at least 1.");
        }
    }
}
