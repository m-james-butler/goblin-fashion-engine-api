package com.jayice.goblinfashionengineapi.api.domain.model;

import com.jayice.goblinfashionengineapi.api.domain.model.QuirkCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuirkConditionGroup {

    private List<QuirkCondition> all;
    private List<QuirkCondition> any;
    private List<QuirkCondition> none;
}
