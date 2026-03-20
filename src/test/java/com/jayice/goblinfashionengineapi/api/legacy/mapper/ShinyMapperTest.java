package com.jayice.goblinfashionengineapi.api.legacy.mapper;

import com.jayice.goblinfashionengineapi.api.domain.enums.*;
import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.legacy.model.LegacyShiny;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShinyMapperTest {

    private final ShinyMapper shinyMapper = new ShinyMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void mapsLegacyShinyToCanonicalShiny() {
        LegacyShiny legacy = new LegacyShiny();
        legacy.setId("legacy-1");
        legacy.setName("Silver Ring");
        legacy.setCount(2);
        legacy.setCategory("Acessories");
        legacy.setSubcategory("Rings");
        legacy.setPrimaryContext("Accessory");
        legacy.setSecondaryContext("Date");
        legacy.setFormality("Smart Casual");
        legacy.setAttentionLevel("High");
        legacy.setColorPrimary("Silver");
        legacy.setColorSecondary("Black");
        legacy.setPattern("MicroPattern");
        legacy.setFabric("Sterling");
        legacy.setFit("Regular");
        legacy.setWarmth(0);
        legacy.setOfficeOk(true);
        legacy.setPublicWear(true);
        legacy.setIncludeInEngine(true);
        legacy.setImagePath("/images/ring.png");
        legacy.setStatus("Owned");
        legacy.setNotes("Test note");

        Shiny canonical = shinyMapper.toCanonical(legacy);

        assertNotNull(canonical);
        assertEquals("legacy-1", canonical.getId());
        assertEquals("Silver Ring", canonical.getName());
        assertEquals(2, canonical.getCount());
        assertEquals(ShinyCategory.ACCESSORY, canonical.getCategory());
        assertEquals("Rings", canonical.getSubcategory());
        assertEquals(Layer.ACCESSORY, canonical.getLayer());
        assertEquals(List.of(Context.ACCESSORY, Context.DATE), canonical.getContexts());
        assertEquals(Formality.SMART_CASUAL, canonical.getFormality());
        assertEquals(Attention.HIGH, canonical.getAttention());
        assertEquals(Color.SILVER, canonical.getColorPrimary());
        assertEquals(Color.BLACK, canonical.getColorSecondary());
        assertEquals(Pattern.MICRO_PATTERN, canonical.getPattern());
        assertEquals("Sterling", canonical.getFabric());
        assertEquals("Regular", canonical.getFit());
        assertEquals(0, canonical.getWarmth());
        assertTrue(canonical.isOfficeOk());
        assertTrue(canonical.isPublicWear());
        assertTrue(canonical.isIncludeInEngine());
        assertEquals(EngineInclusionPolicy.NORMAL, canonical.getEngineInclusionPolicy());
        assertEquals("/images/ring.png", canonical.getImagePath());
        assertEquals(ShinyStatus.OWNED, canonical.getStatus());
        assertEquals("Test note", canonical.getNotes());
    }

    @Test
    void mapsNameToIdWhenNameIsNull() {
        LegacyShiny legacy = new LegacyShiny();
        legacy.setId("fallback-id");
        legacy.setName(null);

        Shiny canonical = shinyMapper.toCanonical(legacy);

        assertNotNull(canonical);
        assertEquals("fallback-id", canonical.getName());
    }

    @Test
    void handlesUnknownEnumValuesSafely() {
        LegacyShiny legacy = new LegacyShiny();
        legacy.setCategory("does-not-exist");
        legacy.setPrimaryContext("mystery");
        legacy.setFormality("not-real");
        legacy.setAttentionLevel("not-real");
        legacy.setColorPrimary("not-real");
        legacy.setPattern("not-real");
        legacy.setStatus("not-real");

        Shiny canonical = shinyMapper.toCanonical(legacy);

        assertNotNull(canonical);
        assertNull(canonical.getCategory());
        assertNull(canonical.getLayer());
        assertNull(canonical.getContexts());
        assertNull(canonical.getFormality());
        assertNull(canonical.getAttention());
        assertNull(canonical.getColorPrimary());
        assertNull(canonical.getPattern());
        assertNull(canonical.getStatus());
    }

    @Test
    void mapsListAndHandlesNullInputs() {
        assertEquals(List.of(), shinyMapper.toCanonicalList(null));

        LegacyShiny first = new LegacyShiny();
        first.setId("1");
        first.setCategory("Shirts");

        LegacyShiny second = null;

        LegacyShiny third = new LegacyShiny();
        third.setId("3");
        third.setCategory("Shoes");

        List<Shiny> mapped = shinyMapper.toCanonicalList(Arrays.asList(first, second, third));

        assertEquals(2, mapped.size());
        assertEquals("1", mapped.get(0).getId());
        assertEquals(ShinyCategory.TOP, mapped.get(0).getCategory());
        assertEquals("3", mapped.get(1).getId());
        assertEquals(ShinyCategory.SHOES, mapped.get(1).getCategory());
    }

    @Test
    void mapsInventoryJsonWithoutDataLoss() throws Exception {
        ClassPathResource resource = new ClassPathResource("data/inventory.json");
        InputStream inputStream = resource.getInputStream();
        List<LegacyShiny> legacyShinies = objectMapper.readValue(inputStream, new TypeReference<>() {});

        List<Shiny> canonicalShinies = shinyMapper.toCanonicalList(legacyShinies);

        assertEquals(legacyShinies.size(), canonicalShinies.size());
        for (int i = 0; i < legacyShinies.size(); i++) {
            LegacyShiny legacy = legacyShinies.get(i);
            Shiny canonical = canonicalShinies.get(i);
            assertEquals(legacy.getId(), canonical.getId());
            assertEquals(legacy.getName() == null ? legacy.getId() : legacy.getName(), canonical.getName());
            assertEquals(legacy.getCount() == null ? 0 : legacy.getCount(), canonical.getCount());
            assertEquals(legacy.getSubcategory(), canonical.getSubcategory());
            assertEquals(legacy.getFabric(), canonical.getFabric());
            assertEquals(legacy.getFit(), canonical.getFit());
            assertEquals(legacy.getWarmth(), canonical.getWarmth());
            assertEquals(legacy.getImagePath(), canonical.getImagePath());
            assertEquals(legacy.getNotes(), canonical.getNotes());

            assertNotNull(canonical.getCategory());
            assertNotNull(canonical.getFormality());
            assertNotNull(canonical.getAttention());
            assertNotNull(canonical.getColorPrimary());
            assertNotNull(canonical.getPattern());
            assertNotNull(canonical.getStatus());
        }
    }
}
