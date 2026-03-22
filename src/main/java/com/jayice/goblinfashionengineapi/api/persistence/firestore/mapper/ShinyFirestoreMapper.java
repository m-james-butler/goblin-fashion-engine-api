package com.jayice.goblinfashionengineapi.api.persistence.firestore.mapper;

import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.model.ShinyDocument;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps canonical domain shiny objects to Firestore document shape and back.
 */
@Component
public class ShinyFirestoreMapper {

    public Shiny toCanonical(ShinyDocument shinyDocument) {
        if (shinyDocument == null) {
            return null;
        }

        return Shiny.builder()
                .id(shinyDocument.getId())
                .goblinId(shinyDocument.getGoblinId())
                .hoardId(shinyDocument.getHoardId())
                .name(shinyDocument.getName())
                .count(shinyDocument.getCount())
                .category(shinyDocument.getCategory())
                .subcategory(shinyDocument.getSubcategory())
                .layer(shinyDocument.getLayer())
                .contexts(shinyDocument.getContexts())
                .formality(shinyDocument.getFormality())
                .attention(shinyDocument.getAttention())
                .colorPrimary(shinyDocument.getColorPrimary())
                .colorSecondary(shinyDocument.getColorSecondary())
                .pattern(shinyDocument.getPattern())
                .fabric(shinyDocument.getFabric())
                .fit(shinyDocument.getFit())
                .warmth(shinyDocument.getWarmth())
                .officeOk(shinyDocument.isOfficeOk())
                .publicWear(shinyDocument.isPublicWear())
                .includeInEngine(shinyDocument.isIncludeInEngine())
                .engineInclusionPolicy(shinyDocument.getEngineInclusionPolicy())
                .imagePath(shinyDocument.getImagePath())
                .status(shinyDocument.getStatus())
                .notes(shinyDocument.getNotes())
                .createdAt(shinyDocument.getCreatedAt())
                .updatedAt(shinyDocument.getUpdatedAt())
                .build();
    }

    public ShinyDocument toDocument(Shiny shiny) {
        if (shiny == null) {
            return null;
        }

        return ShinyDocument.builder()
                .id(shiny.getId())
                .goblinId(shiny.getGoblinId())
                .hoardId(shiny.getHoardId())
                .name(shiny.getName())
                .count(shiny.getCount())
                .category(shiny.getCategory())
                .subcategory(shiny.getSubcategory())
                .layer(shiny.getLayer())
                .contexts(shiny.getContexts())
                .formality(shiny.getFormality())
                .attention(shiny.getAttention())
                .colorPrimary(shiny.getColorPrimary())
                .colorSecondary(shiny.getColorSecondary())
                .pattern(shiny.getPattern())
                .fabric(shiny.getFabric())
                .fit(shiny.getFit())
                .warmth(shiny.getWarmth())
                .officeOk(shiny.isOfficeOk())
                .publicWear(shiny.isPublicWear())
                .includeInEngine(shiny.isIncludeInEngine())
                .engineInclusionPolicy(shiny.getEngineInclusionPolicy())
                .imagePath(shiny.getImagePath())
                .status(shiny.getStatus())
                .notes(shiny.getNotes())
                .createdAt(shiny.getCreatedAt())
                .updatedAt(shiny.getUpdatedAt())
                .build();
    }

    public List<Shiny> toCanonicalList(List<ShinyDocument> shinyDocuments) {
        if (shinyDocuments == null || shinyDocuments.isEmpty()) {
            return List.of();
        }

        return shinyDocuments.stream()
                .filter(item -> item != null)
                .map(this::toCanonical)
                .toList();
    }

    public List<ShinyDocument> toDocumentList(List<Shiny> shinies) {
        if (shinies == null || shinies.isEmpty()) {
            return List.of();
        }

        return shinies.stream()
                .filter(item -> item != null)
                .map(this::toDocument)
                .toList();
    }
}
