package com.jayice.goblinfashionengineapi.api.domain.model;

import com.jayice.goblinfashionengineapi.api.domain.enums.*;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClutterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void roundTripsWithNestedItemsAndEnums() throws Exception {
        Clutter clutter = Clutter.builder()
                .id("c-1")
                .goblinId("g-1")
                .hoardId("h-1")
                .name("Office Fit")
                .source(ClutterSource.ENGINE)
                .status(ClutterStatus.ACTIVE)
                .targetContexts(List.of(Context.OFFICE, Context.BUSINESS))
                .targetAttention(Attention.LOW)
                .items(List.of(
                        ClutterItem.builder().shinyId("s-1").role(ClutterItemRole.TOP).slotOrder(1).build(),
                        ClutterItem.builder().shinyId("s-2").role(ClutterItemRole.BOTTOM).slotOrder(2).build()
                ))
                .build();

        String json = objectMapper.writeValueAsString(clutter);
        Clutter roundTripped = objectMapper.readValue(json, Clutter.class);

        assertEquals(clutter, roundTripped);
    }
}
