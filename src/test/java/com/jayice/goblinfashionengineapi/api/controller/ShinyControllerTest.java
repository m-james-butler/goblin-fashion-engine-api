package com.jayice.goblinfashionengineapi.api.controller;

import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyCategory;
import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
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

    @Test
    void getShiniesForValidHoardIdReturnsCanonicalArray() throws Exception {
        Shiny shiny = Shiny.builder()
                .id("item-1")
                .name("Blue Tee")
                .category(ShinyCategory.TOP)
                .build();

        when(shinyService.getShiniesByHoardId("HRD-001")).thenReturn(List.of(shiny));

        mockMvc.perform(get("/api/hoards/HRD-001/shinies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("item-1"))
                .andExpect(jsonPath("$[0].name").value("Blue Tee"))
                .andExpect(jsonPath("$[0].category").value("TOP"));
    }

    @Test
    void getShiniesForUnknownHoardIdReturnsEmptyArray() throws Exception {
        when(shinyService.getShiniesByHoardId("UNKNOWN")).thenReturn(List.of());

        mockMvc.perform(get("/api/hoards/UNKNOWN/shinies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
