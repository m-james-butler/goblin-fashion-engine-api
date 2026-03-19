package com.jayice.goblinfashionengineapi.api.domain.model;

import com.jayice.goblinfashionengineapi.api.domain.enums.QuirkRuleType;
import com.jayice.goblinfashionengineapi.api.domain.enums.QuirkScopeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quirk {

    private String id;

    private QuirkScopeType scopeType;
    private String scopeId;

    private String name;
    private String description;

    private boolean isActive;
    private int priority;

    private QuirkRuleType ruleType;

    private QuirkConditionGroup conditions;
    private QuirkEffect effect;

    private String createdAt;
    private String updatedAt;
}
