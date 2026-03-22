package com.jayice.goblinfashionengineapi.api.persistence.firestore.model;

import com.jayice.goblinfashionengineapi.api.domain.enums.Attention;
import com.jayice.goblinfashionengineapi.api.domain.enums.Color;
import com.jayice.goblinfashionengineapi.api.domain.enums.Context;
import com.jayice.goblinfashionengineapi.api.domain.enums.EngineInclusionPolicy;
import com.jayice.goblinfashionengineapi.api.domain.enums.Formality;
import com.jayice.goblinfashionengineapi.api.domain.enums.Layer;
import com.jayice.goblinfashionengineapi.api.domain.enums.Pattern;
import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyCategory;
import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Firestore representation for a shiny document.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShinyDocument {
    private String id;
    private String goblinId;
    private String hoardId;

    private String name;
    private int count;

    private ShinyCategory category;
    private String subcategory;
    private Layer layer;

    private List<Context> contexts;
    private Formality formality;
    private Attention attention;

    private Color colorPrimary;
    private Color colorSecondary;
    private Pattern pattern;

    private String fabric;
    private String fit;
    private Integer warmth;

    private boolean officeOk;
    private boolean publicWear;
    private boolean includeInEngine;
    private EngineInclusionPolicy engineInclusionPolicy;

    private String imagePath;
    private ShinyStatus status;
    private String notes;

    private String createdAt;
    private String updatedAt;
}
