package com.jayice.goblinfashionengineapi.api.service;

import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyCategory;
import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.mapper.ShinyFirestoreMapper;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.model.ShinyDocument;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.repository.ShinyFirestoreGateway;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShinyServiceTest {

    @Test
    void validGoblinAndHoardIdReturnsMappedCanonicalShinies() {
        ShinyDocument document = ShinyDocument.builder()
                .id("item-1")
                .goblinId("GBL-001")
                .hoardId("HRD-001")
                .name("Blue Tee")
                .count(1)
                .category(ShinyCategory.TOP)
                .build();

        ShinyFirestoreGateway gateway = Mockito.mock(ShinyFirestoreGateway.class);
        when(gateway.findByGoblinIdAndHoardId("GBL-001", "HRD-001")).thenReturn(List.of(document));

        ShinyService shinyService = new ShinyService(gateway, new ShinyFirestoreMapper());

        List<Shiny> result = shinyService.getShiniesByGoblinIdAndHoardId("GBL-001", "HRD-001");

        assertEquals(1, result.size());
        assertEquals("item-1", result.getFirst().getId());
        assertEquals(ShinyCategory.TOP, result.getFirst().getCategory());
    }

    @Test
    void unknownGoblinOrHoardReturnsEmptyList() {
        ShinyFirestoreGateway gateway = Mockito.mock(ShinyFirestoreGateway.class);
        when(gateway.findByGoblinIdAndHoardId("UNKNOWN", "UNKNOWN")).thenReturn(List.of());

        ShinyService shinyService = new ShinyService(gateway, new ShinyFirestoreMapper());

        List<Shiny> result = shinyService.getShiniesByGoblinIdAndHoardId("UNKNOWN", "UNKNOWN");

        assertEquals(List.of(), result);
    }

    @Test
    void emptyFirestoreResultReturnsEmptyList() {
        ShinyFirestoreGateway gateway = Mockito.mock(ShinyFirestoreGateway.class);
        when(gateway.findByGoblinIdAndHoardId("GBL-001", "HRD-001")).thenReturn(List.of());

        ShinyService shinyService = new ShinyService(gateway, new ShinyFirestoreMapper());

        List<Shiny> result = shinyService.getShiniesByGoblinIdAndHoardId("GBL-001", "HRD-001");

        assertEquals(List.of(), result);
    }

    @Test
    void createShinyDelegatesToGatewayAndReturnsCreatedCanonicalShiny() {
        Shiny shinyToCreate = Shiny.builder()
                .id("SH-001")
                .name("Battle Jacket")
                .count(1)
                .category(ShinyCategory.OUTERWEAR)
                .build();
        ShinyDocument createdDocument = ShinyDocument.builder()
                .id("SH-001")
                .goblinId("GBL-001")
                .hoardId("HRD-001")
                .name("Battle Jacket")
                .count(1)
                .category(ShinyCategory.OUTERWEAR)
                .build();

        ShinyFirestoreGateway gateway = Mockito.mock(ShinyFirestoreGateway.class);
        when(gateway.createShiny(eq("GBL-001"), eq("HRD-001"), any(ShinyDocument.class))).thenReturn(createdDocument);

        ShinyService shinyService = new ShinyService(gateway, new ShinyFirestoreMapper());

        Shiny createdShiny = shinyService.createShiny("GBL-001", "HRD-001", shinyToCreate);

        assertEquals("SH-001", createdShiny.getId());
        assertEquals("GBL-001", createdShiny.getGoblinId());
        assertEquals("HRD-001", createdShiny.getHoardId());
        assertEquals(ShinyCategory.OUTERWEAR, createdShiny.getCategory());
        verify(gateway).createShiny(eq("GBL-001"), eq("HRD-001"), any(ShinyDocument.class));
    }

    @Test
    void updateShinyDelegatesToGatewayAndReturnsUpdatedCanonicalShiny() {
        Shiny shinyToUpdate = Shiny.builder()
                .id("SH-001")
                .name("Updated Jacket")
                .count(2)
                .category(ShinyCategory.OUTERWEAR)
                .build();
        ShinyDocument updatedDocument = ShinyDocument.builder()
                .id("SH-001")
                .goblinId("GBL-001")
                .hoardId("HRD-001")
                .name("Updated Jacket")
                .count(2)
                .category(ShinyCategory.OUTERWEAR)
                .build();

        ShinyFirestoreGateway gateway = Mockito.mock(ShinyFirestoreGateway.class);
        when(gateway.updateShiny(eq("GBL-001"), eq("HRD-001"), eq("SH-001"), any(ShinyDocument.class)))
                .thenReturn(updatedDocument);

        ShinyService shinyService = new ShinyService(gateway, new ShinyFirestoreMapper());

        Shiny updatedShiny = shinyService.updateShiny("GBL-001", "HRD-001", "SH-001", shinyToUpdate);

        assertEquals("SH-001", updatedShiny.getId());
        assertEquals("GBL-001", updatedShiny.getGoblinId());
        assertEquals("HRD-001", updatedShiny.getHoardId());
        assertEquals("Updated Jacket", updatedShiny.getName());
        assertEquals(2, updatedShiny.getCount());
        verify(gateway).updateShiny(eq("GBL-001"), eq("HRD-001"), eq("SH-001"), any(ShinyDocument.class));
    }

    @Test
    void deleteShinyDelegatesToGateway() {
        ShinyFirestoreGateway gateway = Mockito.mock(ShinyFirestoreGateway.class);
        ShinyService shinyService = new ShinyService(gateway, new ShinyFirestoreMapper());

        shinyService.deleteShiny("GBL-001", "HRD-001", "SH-001");

        verify(gateway).deleteShiny("GBL-001", "HRD-001", "SH-001");
    }
}
