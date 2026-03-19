package com.jayice.goblinfashionengineapi.api.domain.model;

import com.jayice.goblinfashionengineapi.api.domain.enums.Attention;
import com.jayice.goblinfashionengineapi.api.domain.enums.ClutterSource;
import com.jayice.goblinfashionengineapi.api.domain.enums.ClutterStatus;
import com.jayice.goblinfashionengineapi.api.domain.enums.Context;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Clutter {

    private String id;
    private String goblinId;
    private String hoardId;

    private String name;
    private String description;

    private ClutterSource source;
    private ClutterStatus status;

    private List<Context> targetContexts;
    private Attention targetAttention;

    private List<ClutterItem> items;

    private String createdAt;
    private String updatedAt;
}