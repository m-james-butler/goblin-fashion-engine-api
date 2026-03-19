package com.jayice.goblinfashionengineapi.api.legacy.model;

import lombok.Data;

@Data
public class LegacyShiny {

    private String id;
    private String name;
    private Integer count;

    private String category;
    private String subcategory;

    private String primaryContext;
    private String secondaryContext;

    private String formality;
    private String attentionLevel;

    private String colorPrimary;
    private String colorSecondary;

    private String pattern;

    private String fabric;
    private String fit;

    private Integer warmth;

    private Boolean officeOk;
    private Boolean publicWear;
    private Boolean includeInEngine;

    private String imagePath;
    private String status;

    private String notes;
}
