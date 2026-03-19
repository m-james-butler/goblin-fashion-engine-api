package com.jayice.goblinfashionengineapi.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuirkEffect {

    private String action;
    private String shinyId;
    private String targetField;
    private Object value;
}