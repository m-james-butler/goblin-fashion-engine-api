package com.jayice.goblinfashionengineapi.api.controller;

import com.jayice.goblinfashionengineapi.api.auth.filter.FirebaseAuthenticationFilter;
import com.jayice.goblinfashionengineapi.api.auth.model.AuthenticatedGoblin;
import com.jayice.goblinfashionengineapi.api.auth.service.FirebaseTokenVerifier;
import com.jayice.goblinfashionengineapi.api.auth.service.InvalidFirebaseTokenException;
import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyCategory;
import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.dto.ShinyCreateRequestDto;
import com.jayice.goblinfashionengineapi.api.dto.ShinyPatchRequestDto;
import com.jayice.goblinfashionengineapi.api.dto.ShinyUpdateRequestDto;
import com.jayice.goblinfashionengineapi.api.dto.ShinyResponseDto;
import com.jayice.goblinfashionengineapi.api.mapper.ShinyDtoMapper;
import com.jayice.goblinfashionengineapi.api.service.ShinyAlreadyExistsException;
import com.jayice.goblinfashionengineapi.api.service.ShinyNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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

    @Test
    void createShinyWithoutBearerTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/goblins/GBL-001/hoards/HRD-001/shinies")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"id":"SH-001","name":"Battle Jacket","count":1}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createShinyWithOwnershipMismatchReturnsForbidden() throws Exception {
        when(firebaseTokenVerifier.verify("valid-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-999"));

        mockMvc.perform(post("/api/goblins/GBL-001/hoards/HRD-001/shinies")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"id":"SH-001","name":"Battle Jacket","count":1}
                                """))
                .andExpect(status().isForbidden());

        verify(shinyService, never()).createShiny(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(Shiny.class));
    }

    @Test
    void createShinyWithInvalidRequestReturnsBadRequest() throws Exception {
        when(firebaseTokenVerifier.verify("good-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-001"));

        mockMvc.perform(post("/api/goblins/GBL-001/hoards/HRD-001/shinies")
                        .header("Authorization", "Bearer good-token")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"id":"","name":"Battle Jacket","count":0}
                                """))
                .andExpect(status().isBadRequest());

        verify(shinyService, never()).createShiny(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(Shiny.class));
    }

    @Test
    void createShinyWithMatchingOwnershipReturnsCreatedResponseDto() throws Exception {
        Shiny canonicalRequest = Shiny.builder()
                .id("SH-001")
                .name("Battle Jacket")
                .count(1)
                .category(ShinyCategory.OUTERWEAR)
                .build();
        Shiny canonicalCreated = Shiny.builder()
                .id("SH-001")
                .goblinId("GBL-001")
                .hoardId("HRD-001")
                .name("Battle Jacket")
                .count(1)
                .category(ShinyCategory.OUTERWEAR)
                .build();
        ShinyResponseDto responseDto = ShinyResponseDto.builder()
                .id("SH-001")
                .goblinId("GBL-001")
                .hoardId("HRD-001")
                .name("Battle Jacket")
                .count(1)
                .category(ShinyCategory.OUTERWEAR)
                .build();

        when(firebaseTokenVerifier.verify("good-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-001"));
        when(shinyDtoMapper.toCanonicalForCreate(org.mockito.ArgumentMatchers.any(ShinyCreateRequestDto.class)))
                .thenReturn(canonicalRequest);
        when(shinyService.createShiny("GBL-001", "HRD-001", canonicalRequest))
                .thenReturn(canonicalCreated);
        when(shinyDtoMapper.toResponseDto(canonicalCreated)).thenReturn(responseDto);

        mockMvc.perform(post("/api/goblins/GBL-001/hoards/HRD-001/shinies")
                        .header("Authorization", "Bearer good-token")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"id":"SH-001","name":"Battle Jacket","count":1,"category":"OUTERWEAR"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("SH-001"))
                .andExpect(jsonPath("$.goblinId").value("GBL-001"))
                .andExpect(jsonPath("$.hoardId").value("HRD-001"))
                .andExpect(jsonPath("$.name").value("Battle Jacket"))
                .andExpect(jsonPath("$.category").value("OUTERWEAR"));
    }

    @Test
    void createShinyWhenDuplicateReturnsConflict() throws Exception {
        Shiny canonicalRequest = Shiny.builder()
                .id("SH-001")
                .name("Battle Jacket")
                .count(1)
                .build();

        when(firebaseTokenVerifier.verify("good-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-001"));
        when(shinyDtoMapper.toCanonicalForCreate(org.mockito.ArgumentMatchers.any(ShinyCreateRequestDto.class)))
                .thenReturn(canonicalRequest);
        when(shinyService.createShiny("GBL-001", "HRD-001", canonicalRequest))
                .thenThrow(new ShinyAlreadyExistsException("Duplicate shiny id."));

        mockMvc.perform(post("/api/goblins/GBL-001/hoards/HRD-001/shinies")
                        .header("Authorization", "Bearer good-token")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"id":"SH-001","name":"Battle Jacket","count":1}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void updateShinyWithoutBearerTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(put("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-001")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"id":"SH-001","name":"Updated Jacket","count":1}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateShinyWithOwnershipMismatchReturnsForbidden() throws Exception {
        when(firebaseTokenVerifier.verify("valid-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-999"));

        mockMvc.perform(put("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-001")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"id":"SH-001","name":"Updated Jacket","count":1}
                                """))
                .andExpect(status().isForbidden());

        verify(shinyService, never()).updateShiny(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(Shiny.class)
        );
    }

    @Test
    void updateShinyWithPathBodyIdMismatchReturnsBadRequest() throws Exception {
        when(firebaseTokenVerifier.verify("good-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-001"));

        mockMvc.perform(put("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-123")
                        .header("Authorization", "Bearer good-token")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"id":"SH-999","name":"Updated Jacket","count":1}
                                """))
                .andExpect(status().isBadRequest());

        verify(shinyService, never()).updateShiny(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(Shiny.class)
        );
    }

    @Test
    void updateShinyWithInvalidRequestReturnsBadRequest() throws Exception {
        when(firebaseTokenVerifier.verify("good-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-001"));

        mockMvc.perform(put("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-001")
                        .header("Authorization", "Bearer good-token")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"id":"SH-001","name":"","count":0}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShinyWithMatchingOwnershipReturnsUpdatedResponseDto() throws Exception {
        Shiny canonicalRequest = Shiny.builder()
                .id("SH-001")
                .name("Updated Jacket")
                .count(2)
                .category(ShinyCategory.OUTERWEAR)
                .build();
        Shiny canonicalUpdated = Shiny.builder()
                .id("SH-001")
                .goblinId("GBL-001")
                .hoardId("HRD-001")
                .name("Updated Jacket")
                .count(2)
                .category(ShinyCategory.OUTERWEAR)
                .build();
        ShinyResponseDto responseDto = ShinyResponseDto.builder()
                .id("SH-001")
                .goblinId("GBL-001")
                .hoardId("HRD-001")
                .name("Updated Jacket")
                .count(2)
                .category(ShinyCategory.OUTERWEAR)
                .build();

        when(firebaseTokenVerifier.verify("good-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-001"));
        when(shinyDtoMapper.toCanonicalForUpdate(org.mockito.ArgumentMatchers.any(ShinyUpdateRequestDto.class)))
                .thenReturn(canonicalRequest);
        when(shinyService.updateShiny("GBL-001", "HRD-001", "SH-001", canonicalRequest))
                .thenReturn(canonicalUpdated);
        when(shinyDtoMapper.toResponseDto(canonicalUpdated)).thenReturn(responseDto);

        mockMvc.perform(put("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-001")
                        .header("Authorization", "Bearer good-token")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"id":"SH-001","name":"Updated Jacket","count":2,"category":"OUTERWEAR"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("SH-001"))
                .andExpect(jsonPath("$.goblinId").value("GBL-001"))
                .andExpect(jsonPath("$.hoardId").value("HRD-001"))
                .andExpect(jsonPath("$.name").value("Updated Jacket"))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.category").value("OUTERWEAR"));
    }

    @Test
    void updateShinyWhenMissingReturnsNotFound() throws Exception {
        Shiny canonicalRequest = Shiny.builder()
                .id("SH-001")
                .name("Updated Jacket")
                .count(2)
                .build();

        when(firebaseTokenVerifier.verify("good-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-001"));
        when(shinyDtoMapper.toCanonicalForUpdate(org.mockito.ArgumentMatchers.any(ShinyUpdateRequestDto.class)))
                .thenReturn(canonicalRequest);
        when(shinyService.updateShiny("GBL-001", "HRD-001", "SH-001", canonicalRequest))
                .thenThrow(new ShinyNotFoundException("Shiny not found."));

        mockMvc.perform(put("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-001")
                        .header("Authorization", "Bearer good-token")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"id":"SH-001","name":"Updated Jacket","count":2}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShinyWithoutBearerTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-001"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteShinyWithOwnershipMismatchReturnsForbidden() throws Exception {
        when(firebaseTokenVerifier.verify("valid-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-999"));

        mockMvc.perform(delete("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-001")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isForbidden());

        verify(shinyService, never()).deleteShiny(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString()
        );
    }

    @Test
    void deleteShinyWithMatchingOwnershipReturnsNoContent() throws Exception {
        when(firebaseTokenVerifier.verify("good-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-001"));

        mockMvc.perform(delete("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-001")
                        .header("Authorization", "Bearer good-token"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShinyWhenMissingReturnsNotFound() throws Exception {
        when(firebaseTokenVerifier.verify("good-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-001"));
        org.mockito.Mockito.doThrow(new ShinyNotFoundException("Shiny not found."))
                .when(shinyService)
                .deleteShiny("GBL-001", "HRD-001", "SH-001");

        mockMvc.perform(delete("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-001")
                        .header("Authorization", "Bearer good-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchShinyWithoutBearerTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(patch("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-001")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"notes":"new notes"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchShinyWithOwnershipMismatchReturnsForbidden() throws Exception {
        when(firebaseTokenVerifier.verify("valid-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-999"));

        mockMvc.perform(patch("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-001")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"notes":"new notes"}
                                """))
                .andExpect(status().isForbidden());

        verify(shinyService, never()).patchShiny(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void patchShinyWhenMissingReturnsNotFound() throws Exception {
        com.jayice.goblinfashionengineapi.api.domain.model.ShinyPatch shinyPatch =
                com.jayice.goblinfashionengineapi.api.domain.model.ShinyPatch.builder().notes("new notes").build();

        when(firebaseTokenVerifier.verify("good-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-001"));
        when(shinyDtoMapper.toCanonicalPatch(org.mockito.ArgumentMatchers.any(ShinyPatchRequestDto.class)))
                .thenReturn(shinyPatch);
        when(shinyService.patchShiny("GBL-001", "HRD-001", "SH-001", shinyPatch))
                .thenThrow(new ShinyNotFoundException("Shiny not found."));

        mockMvc.perform(patch("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-001")
                        .header("Authorization", "Bearer good-token")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"notes":"new notes"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchShinyWithMatchingOwnershipReturnsUpdatedResponseDto() throws Exception {
        com.jayice.goblinfashionengineapi.api.domain.model.ShinyPatch shinyPatch =
                com.jayice.goblinfashionengineapi.api.domain.model.ShinyPatch.builder()
                        .notes("new notes")
                        .build();
        Shiny patchedShiny = Shiny.builder()
                .id("SH-001")
                .goblinId("GBL-001")
                .hoardId("HRD-001")
                .name("Battle Jacket")
                .count(1)
                .notes("new notes")
                .build();
        ShinyResponseDto responseDto = ShinyResponseDto.builder()
                .id("SH-001")
                .goblinId("GBL-001")
                .hoardId("HRD-001")
                .name("Battle Jacket")
                .count(1)
                .notes("new notes")
                .build();

        when(firebaseTokenVerifier.verify("good-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-001"));
        when(shinyDtoMapper.toCanonicalPatch(org.mockito.ArgumentMatchers.any(ShinyPatchRequestDto.class)))
                .thenReturn(shinyPatch);
        when(shinyService.patchShiny("GBL-001", "HRD-001", "SH-001", shinyPatch))
                .thenReturn(patchedShiny);
        when(shinyDtoMapper.toResponseDto(patchedShiny)).thenReturn(responseDto);

        mockMvc.perform(patch("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-001")
                        .header("Authorization", "Bearer good-token")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"notes":"new notes"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("SH-001"))
                .andExpect(jsonPath("$.notes").value("new notes"));
    }

    @Test
    void patchShinyWithEmptyPatchReturnsBadRequest() throws Exception {
        com.jayice.goblinfashionengineapi.api.domain.model.ShinyPatch shinyPatch =
                com.jayice.goblinfashionengineapi.api.domain.model.ShinyPatch.builder().build();

        when(firebaseTokenVerifier.verify("good-token"))
                .thenReturn(new AuthenticatedGoblin("GBL-001"));
        when(shinyDtoMapper.toCanonicalPatch(org.mockito.ArgumentMatchers.any(ShinyPatchRequestDto.class)))
                .thenReturn(shinyPatch);
        when(shinyService.patchShiny("GBL-001", "HRD-001", "SH-001", shinyPatch))
                .thenThrow(new IllegalArgumentException("At least one patch field must be provided."));

        mockMvc.perform(patch("/api/goblins/GBL-001/hoards/HRD-001/shinies/SH-001")
                        .header("Authorization", "Bearer good-token")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
