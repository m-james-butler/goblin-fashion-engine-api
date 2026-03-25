package com.jayice.goblinfashionengineapi.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jayice.goblinfashionengineapi.api.domain.enums.Attention;
import com.jayice.goblinfashionengineapi.api.domain.enums.Color;
import com.jayice.goblinfashionengineapi.api.domain.enums.Context;
import com.jayice.goblinfashionengineapi.api.domain.enums.EngineInclusionPolicy;
import com.jayice.goblinfashionengineapi.api.domain.enums.Formality;
import com.jayice.goblinfashionengineapi.api.domain.enums.Layer;
import com.jayice.goblinfashionengineapi.api.domain.enums.Pattern;
import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyCategory;
import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShinyCreateRequestDto {

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @Min(1)
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
}
