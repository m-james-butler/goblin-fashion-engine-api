package com.jayice.goblinfashionengineapi.api.domain.model;

import com.jayice.goblinfashionengineapi.api.domain.enums.Attention;
import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShinyPatch {
    private ShinyStatus status;
    private String imagePath;
    private String notes;
    private Boolean includeInEngine;
    private Attention attentionLevel;
}
