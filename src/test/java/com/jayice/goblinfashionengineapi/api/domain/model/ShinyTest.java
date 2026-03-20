package com.jayice.goblinfashionengineapi.api.domain.model;

import com.jayice.goblinfashionengineapi.api.domain.enums.*;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShinyTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void roundTripsWithEnumAndNestedListFields() throws Exception {
        Shiny shiny = Shiny.builder()
                .id("s-1")
                .goblinId("g-1")
                .hoardId("h-1")
                .name("Blue Blazer")
                .count(1)
                .category(ShinyCategory.OUTERWEAR)
                .layer(Layer.OUTER)
                .contexts(List.of(Context.OFFICE, Context.DINNER))
                .formality(Formality.BUSINESS)
                .attention(Attention.MEDIUM)
                .colorPrimary(Color.NAVY)
                .colorSecondary(Color.WHITE)
                .pattern(Pattern.SOLID)
                .officeOk(true)
                .publicWear(true)
                .includeInEngine(true)
                .engineInclusionPolicy(EngineInclusionPolicy.NORMAL)
                .status(ShinyStatus.OWNED)
                .build();

        String json = objectMapper.writeValueAsString(shiny);
        Shiny roundTripped = objectMapper.readValue(json, Shiny.class);

        assertEquals(shiny, roundTripped);
    }

    @Test
    void rejectsInvalidEnumValues() {
        assertThrows(InvalidFormatException.class, () -> objectMapper.readValue(
                "{\"id\":\"s-1\",\"name\":\"Bad\",\"count\":1,\"category\":\"NOT_A_CATEGORY\"}",
                Shiny.class
        ));
    }

    @Test
    void omitsNullOptionalFieldsFromJson() throws Exception {
        Shiny shiny = Shiny.builder()
                .id("s-1")
                .name("Basic")
                .count(1)
                .build();

        String json = objectMapper.writeValueAsString(shiny);

        assertFalse(json.contains("\"colorSecondary\""));
        assertFalse(json.contains("\"fit\""));
        assertFalse(json.contains("\"imagePath\""));
    }
}
