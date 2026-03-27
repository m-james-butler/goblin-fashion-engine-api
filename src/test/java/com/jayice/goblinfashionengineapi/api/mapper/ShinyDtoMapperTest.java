package com.jayice.goblinfashionengineapi.api.mapper;

import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyCategory;
import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.dto.ShinyCreateRequestDto;
import com.jayice.goblinfashionengineapi.api.dto.ShinyResponseDto;
import com.jayice.goblinfashionengineapi.api.dto.ShinyUpdateRequestDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShinyDtoMapperTest {

    private final ShinyDtoMapper shinyDtoMapper = new ShinyDtoMapper();

    @Test
    void mapsCanonicalShinyToResponseDto() {
        Shiny shiny = Shiny.builder()
                .id("item-1")
                .name("Blue Tee")
                .count(2)
                .category(ShinyCategory.TOP)
                .officeOk(true)
                .build();

        ShinyResponseDto result = shinyDtoMapper.toResponseDto(shiny);

        assertEquals("item-1", result.getId());
        assertEquals("Blue Tee", result.getName());
        assertEquals(2, result.getCount());
        assertEquals(ShinyCategory.TOP, result.getCategory());
        assertTrue(result.isOfficeOk());
    }

    @Test
    void mapsCreateRequestDtoToCanonicalShiny() {
        ShinyCreateRequestDto shinyCreateRequestDto = new ShinyCreateRequestDto();
        shinyCreateRequestDto.setId("item-9");
        shinyCreateRequestDto.setName("Steel Boots");
        shinyCreateRequestDto.setCount(1);
        shinyCreateRequestDto.setCategory(ShinyCategory.SHOES);
        shinyCreateRequestDto.setOfficeOk(true);

        Shiny result = shinyDtoMapper.toCanonicalForCreate(shinyCreateRequestDto);

        assertEquals("item-9", result.getId());
        assertEquals("Steel Boots", result.getName());
        assertEquals(1, result.getCount());
        assertEquals(ShinyCategory.SHOES, result.getCategory());
        assertTrue(result.isOfficeOk());
    }

    @Test
    void mapsUpdateRequestDtoToCanonicalShiny() {
        ShinyUpdateRequestDto shinyUpdateRequestDto = new ShinyUpdateRequestDto();
        shinyUpdateRequestDto.setId("item-11");
        shinyUpdateRequestDto.setName("Updated Boots");
        shinyUpdateRequestDto.setCount(2);
        shinyUpdateRequestDto.setCategory(ShinyCategory.SHOES);
        shinyUpdateRequestDto.setPublicWear(true);

        Shiny result = shinyDtoMapper.toCanonicalForUpdate(shinyUpdateRequestDto);

        assertEquals("item-11", result.getId());
        assertEquals("Updated Boots", result.getName());
        assertEquals(2, result.getCount());
        assertEquals(ShinyCategory.SHOES, result.getCategory());
        assertTrue(result.isPublicWear());
    }

    @Test
    void mapsCanonicalShinyListToResponseDtoList() {
        Shiny shiny = Shiny.builder()
                .id("item-1")
                .name("Blue Tee")
                .build();

        List<ShinyResponseDto> result = shinyDtoMapper.toResponseDtoList(List.of(shiny));

        assertEquals(1, result.size());
        assertEquals("item-1", result.getFirst().getId());
    }

    @Test
    void returnsEmptyListForNullOrEmptyInput() {
        assertEquals(List.of(), shinyDtoMapper.toResponseDtoList(null));
        assertEquals(List.of(), shinyDtoMapper.toResponseDtoList(List.of()));
    }

    @Test
    void returnsNullForNullShiny() {
        assertNull(shinyDtoMapper.toResponseDto(null));
        assertNull(shinyDtoMapper.toCanonicalForCreate(null));
        assertNull(shinyDtoMapper.toCanonicalForUpdate(null));
    }
}
