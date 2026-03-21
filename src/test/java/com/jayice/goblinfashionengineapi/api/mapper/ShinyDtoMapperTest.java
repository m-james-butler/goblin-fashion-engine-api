package com.jayice.goblinfashionengineapi.api.mapper;

import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyCategory;
import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.dto.ShinyResponseDto;
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
    }
}
