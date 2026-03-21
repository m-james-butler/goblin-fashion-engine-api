package com.jayice.goblinfashionengineapi.api.mapper;

import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.dto.ShinyResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShinyDtoMapper {

    public ShinyResponseDto toResponseDto(Shiny shiny) {
        if (shiny == null) {
            return null;
        }

        return ShinyResponseDto.builder()
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

    public List<ShinyResponseDto> toResponseDtoList(List<Shiny> shinies) {
        if (shinies == null || shinies.isEmpty()) {
            return List.of();
        }

        return shinies.stream()
                .filter(item -> item != null)
                .map(this::toResponseDto)
                .toList();
    }
}
