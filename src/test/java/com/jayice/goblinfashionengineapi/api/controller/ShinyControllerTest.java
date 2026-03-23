package com.jayice.goblinfashionengineapi.api.controller;

import com.jayice.goblinfashionengineapi.api.auth.filter.FirebaseAuthenticationFilter;
import com.jayice.goblinfashionengineapi.api.auth.model.AuthenticatedGoblin;
import com.jayice.goblinfashionengineapi.api.auth.service.FirebaseTokenVerifier;
import com.jayice.goblinfashionengineapi.api.auth.service.InvalidFirebaseTokenException;
import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyCategory;
import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.dto.ShinyResponseDto;
import com.jayice.goblinfashionengineapi.api.mapper.ShinyDtoMapper;
import com.jayice.goblinfashionengineapi.api.service.ShinyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShinyController.class)
@Import(FirebaseAuthenticationFilter.class)
@TestPropertySource(properties = "app.default-goblin-id=DEFAULT-GBL")
class ShinyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShinyService shinyService;

    @MockitoBean
    private ShinyDtoMapper shinyDtoMapper;

    @MockitoBean
    private FirebaseTokenVerifier firebaseTokenVerifier;

    @Test
    void getShiniesWithoutBearerTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/goblins/GBL-001/hoards/HRD-001/shinies"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getShiniesWithInvalidTokenReturnsUnauthorized() throws Exception {
        when(firebaseTokenVerifier.verify("bad-token"))
                .thenThrow(new InvalidFirebaseTokenException("Token verification failed."));

        mockMvc.perform(get("/api/goblins/GBL-001/hoards/HRD-001/shinies")
                        .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getShiniesWithOwnershipMismatchReturnsForbidden() throws Exception {
        when(firebaseTokenVerifier.verify("valid-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-999"));

        mockMvc.perform(get("/api/goblins/GBL-001/hoards/HRD-001/shinies")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isForbidden());

        verify(shinyService, never()).getShiniesByGoblinIdAndHoardId("GBL-001", "HRD-001");
    }

    @Test
    void getShiniesWithMatchingOwnershipReturnsResponseDtoArray() throws Exception {
        Shiny shiny = Shiny.builder()
                .id("item-1")
                .goblinId("GBL-001")
                .hoardId("HRD-001")
                .name("Canonical Name")
                .category(ShinyCategory.TOP)
                .build();
        ShinyResponseDto shinyResponseDto = ShinyResponseDto.builder()
                .id("item-1")
                .goblinId("GBL-001")
                .hoardId("HRD-001")
                .name("DTO Name")
                .category(ShinyCategory.TOP)
                .build();

        List<Shiny> canonicalShinies = List.of(shiny);
        when(firebaseTokenVerifier.verify("good-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-001"));
        when(shinyService.getShiniesByGoblinIdAndHoardId("GBL-001", "HRD-001")).thenReturn(canonicalShinies);
        when(shinyDtoMapper.toResponseDtoList(canonicalShinies)).thenReturn(List.of(shinyResponseDto));

        mockMvc.perform(get("/api/goblins/GBL-001/hoards/HRD-001/shinies")
                        .header("Authorization", "Bearer good-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("item-1"))
                .andExpect(jsonPath("$[0].goblinId").value("GBL-001"))
                .andExpect(jsonPath("$[0].hoardId").value("HRD-001"))
                .andExpect(jsonPath("$[0].name").value("DTO Name"))
                .andExpect(jsonPath("$[0].category").value("TOP"));
    }

    @Test
    void transitionalEndpointUsesAuthenticatedGoblinId() throws Exception {
        List<Shiny> canonicalShinies = List.of();
        when(firebaseTokenVerifier.verify("transition-token"))
                .thenReturn(new AuthenticatedGoblin("AUTH-GBL"));
        when(shinyService.getShiniesByGoblinIdAndHoardId("AUTH-GBL", "HRD-002")).thenReturn(canonicalShinies);
        when(shinyDtoMapper.toResponseDtoList(canonicalShinies)).thenReturn(List.of());

        mockMvc.perform(get("/api/hoards/HRD-002/shinies")
                        .header("Authorization", "Bearer transition-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(shinyService, never()).getShiniesByGoblinIdAndHoardId("DEFAULT-GBL", "HRD-002");
    }
}
