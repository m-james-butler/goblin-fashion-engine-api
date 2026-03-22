package com.jayice.goblinfashionengineapi.api.persistence.firestore.mapper;

import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyCategory;
import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.model.ShinyDocument;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShinyFirestoreMapperTest {

    private final ShinyFirestoreMapper mapper = new ShinyFirestoreMapper();

    @Test
    void mapsDocumentToCanonicalShiny() {
        ShinyDocument document = ShinyDocument.builder()
                .id("item-1")
                .goblinId("GBL-001")
                .hoardId("HRD-001")
                .name("Blue Tee")
                .count(2)
                .category(ShinyCategory.TOP)
                .officeOk(true)
                .build();

        Shiny result = mapper.toCanonical(document);

        assertEquals("item-1", result.getId());
        assertEquals("GBL-001", result.getGoblinId());
        assertEquals("HRD-001", result.getHoardId());
        assertEquals("Blue Tee", result.getName());
        assertEquals(2, result.getCount());
        assertEquals(ShinyCategory.TOP, result.getCategory());
        assertTrue(result.isOfficeOk());
    }

    @Test
    void mapsCanonicalShinyToDocument() {
        Shiny shiny = Shiny.builder()
                .id("item-2")
                .goblinId("GBL-002")
                .hoardId("HRD-002")
                .name("Gray Hoodie")
                .count(1)
                .category(ShinyCategory.OUTERWEAR)
                .publicWear(true)
                .build();

        ShinyDocument result = mapper.toDocument(shiny);

        assertEquals("item-2", result.getId());
        assertEquals("GBL-002", result.getGoblinId());
        assertEquals("HRD-002", result.getHoardId());
        assertEquals("Gray Hoodie", result.getName());
        assertEquals(1, result.getCount());
        assertEquals(ShinyCategory.OUTERWEAR, result.getCategory());
        assertTrue(result.isPublicWear());
    }

    @Test
    void returnsEmptyListsForNullOrEmptyInputs() {
        assertEquals(List.of(), mapper.toCanonicalList(null));
        assertEquals(List.of(), mapper.toCanonicalList(List.of()));
        assertEquals(List.of(), mapper.toDocumentList(null));
        assertEquals(List.of(), mapper.toDocumentList(List.of()));
    }

    @Test
    void returnsNullWhenSingleInputIsNull() {
        assertNull(mapper.toCanonical(null));
        assertNull(mapper.toDocument(null));
    }
}
