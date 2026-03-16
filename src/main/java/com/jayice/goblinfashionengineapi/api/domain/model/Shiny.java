package com.jayice.goblinfashionengineapi.api.domain.model;

import com.jayice.goblinfashionengineapi.api.domain.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shiny {
    private String id;
    private String goblinId;
    private String hoardId;

    private String name;
    private String filename;
    private String notes;

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

    private String createdAt;
    private String updatedAt;
}
