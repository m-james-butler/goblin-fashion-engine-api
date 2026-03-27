package com.jayice.goblinfashionengineapi.api.mapper;

import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.dto.ShinyCreateRequestDto;
import com.jayice.goblinfashionengineapi.api.dto.ShinyResponseDto;
import com.jayice.goblinfashionengineapi.api.dto.ShinyUpdateRequestDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShinyDtoMapper {

    public Shiny toCanonicalForCreate(ShinyCreateRequestDto shinyCreateRequestDto) {
        if (shinyCreateRequestDto == null) {
            return null;
        }

        return Shiny.builder()
                .id(shinyCreateRequestDto.getId())
                .name(shinyCreateRequestDto.getName())
                .count(shinyCreateRequestDto.getCount())
                .category(shinyCreateRequestDto.getCategory())
                .subcategory(shinyCreateRequestDto.getSubcategory())
                .layer(shinyCreateRequestDto.getLayer())
                .contexts(shinyCreateRequestDto.getContexts())
                .formality(shinyCreateRequestDto.getFormality())
                .attention(shinyCreateRequestDto.getAttention())
                .colorPrimary(shinyCreateRequestDto.getColorPrimary())
                .colorSecondary(shinyCreateRequestDto.getColorSecondary())
                .pattern(shinyCreateRequestDto.getPattern())
                .fabric(shinyCreateRequestDto.getFabric())
                .fit(shinyCreateRequestDto.getFit())
                .warmth(shinyCreateRequestDto.getWarmth())
                .officeOk(shinyCreateRequestDto.isOfficeOk())
                .publicWear(shinyCreateRequestDto.isPublicWear())
                .includeInEngine(shinyCreateRequestDto.isIncludeInEngine())
                .engineInclusionPolicy(shinyCreateRequestDto.getEngineInclusionPolicy())
                .imagePath(shinyCreateRequestDto.getImagePath())
                .status(shinyCreateRequestDto.getStatus())
                .notes(shinyCreateRequestDto.getNotes())
                .build();
    }

    public Shiny toCanonicalForUpdate(ShinyUpdateRequestDto shinyUpdateRequestDto) {
        if (shinyUpdateRequestDto == null) {
            return null;
        }

        return Shiny.builder()
                .id(shinyUpdateRequestDto.getId())
                .name(shinyUpdateRequestDto.getName())
                .count(shinyUpdateRequestDto.getCount())
                .category(shinyUpdateRequestDto.getCategory())
                .subcategory(shinyUpdateRequestDto.getSubcategory())
                .layer(shinyUpdateRequestDto.getLayer())
                .contexts(shinyUpdateRequestDto.getContexts())
                .formality(shinyUpdateRequestDto.getFormality())
                .attention(shinyUpdateRequestDto.getAttention())
                .colorPrimary(shinyUpdateRequestDto.getColorPrimary())
                .colorSecondary(shinyUpdateRequestDto.getColorSecondary())
                .pattern(shinyUpdateRequestDto.getPattern())
                .fabric(shinyUpdateRequestDto.getFabric())
                .fit(shinyUpdateRequestDto.getFit())
                .warmth(shinyUpdateRequestDto.getWarmth())
                .officeOk(shinyUpdateRequestDto.isOfficeOk())
                .publicWear(shinyUpdateRequestDto.isPublicWear())
                .includeInEngine(shinyUpdateRequestDto.isIncludeInEngine())
                .engineInclusionPolicy(shinyUpdateRequestDto.getEngineInclusionPolicy())
                .imagePath(shinyUpdateRequestDto.getImagePath())
                .status(shinyUpdateRequestDto.getStatus())
                .notes(shinyUpdateRequestDto.getNotes())
                .build();
    }

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
