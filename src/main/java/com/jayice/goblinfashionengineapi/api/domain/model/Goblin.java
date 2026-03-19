package com.jayice.goblinfashionengineapi.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goblin {
    private String id;
    private String displayName;
    private String email;
    private String defaultHoardId;
    private String createdAt;
    private String updatedAt;
}
