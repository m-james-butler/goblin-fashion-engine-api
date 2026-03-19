package com.jayice.goblinfashionengineapi.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hoard {

    private String id;
    private String goblinId;
    private String name;
    private String description;
    private boolean isDefault;
    private boolean isActive;
    private String createdAt;
    private String updatedAt;
}
