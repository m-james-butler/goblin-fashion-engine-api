package com.jayice.goblinfashionengineapi.api.controller;

import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyCategory;
import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.dto.ShinyResponseDto;
import com.jayice.goblinfashionengineapi.api.mapper.ShinyDtoMapper;
import com.jayice.goblinfashionengineapi.api.service.ShinyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShinyController.class)
class ShinyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShinyService shinyService;

    @MockitoBean
    private ShinyDtoMapper shinyDtoMapper;

    @Test
    void getShiniesForValidHoardIdReturnsResponseDtoArray() throws Exception {
        Shiny shiny = Shiny.builder()
                .id("item-1")
                .name("Canonical Name")
                .category(ShinyCategory.TOP)
                .build();
        ShinyResponseDto shinyResponseDto = ShinyResponseDto.builder()
                .id("item-1")
                .name("DTO Name")
                .category(ShinyCategory.TOP)
                .build();

        List<Shiny> canonicalShinies = List.of(shiny);
        when(shinyService.getShiniesByHoardId("HRD-001")).thenReturn(canonicalShinies);
        when(shinyDtoMapper.toResponseDtoList(canonicalShinies)).thenReturn(List.of(shinyResponseDto));

        mockMvc.perform(get("/api/hoards/HRD-001/shinies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("item-1"))
                .andExpect(jsonPath("$[0].name").value("DTO Name"))
                .andExpect(jsonPath("$[0].category").value("TOP"));
    }

    @Test
    void getShiniesForUnknownHoardIdReturnsEmptyArray() throws Exception {
        List<Shiny> canonicalShinies = List.of();
        when(shinyService.getShiniesByHoardId("UNKNOWN")).thenReturn(canonicalShinies);
        when(shinyDtoMapper.toResponseDtoList(canonicalShinies)).thenReturn(List.of());

        mockMvc.perform(get("/api/hoards/UNKNOWN/shinies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
