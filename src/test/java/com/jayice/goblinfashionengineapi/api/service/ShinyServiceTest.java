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
}
