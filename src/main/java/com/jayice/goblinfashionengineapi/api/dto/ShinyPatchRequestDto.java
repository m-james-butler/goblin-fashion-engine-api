package com.jayice.goblinfashionengineapi.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jayice.goblinfashionengineapi.api.domain.enums.Attention;
import com.jayice.goblinfashionengineapi.api.domain.enums.ShinyStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShinyPatchRequestDto {
    private ShinyStatus status;
    private String imagePath;
    private String notes;
    private Boolean includeInEngine;
    private Attention attentionLevel;
}
