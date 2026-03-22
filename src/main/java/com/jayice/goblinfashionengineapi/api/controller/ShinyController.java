package com.jayice.goblinfashionengineapi.api.controller;

import com.jayice.goblinfashionengineapi.api.dto.ShinyResponseDto;
import com.jayice.goblinfashionengineapi.api.mapper.ShinyDtoMapper;
import com.jayice.goblinfashionengineapi.api.service.ShinyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ShinyController {
    private final ShinyService shinyService;
    private final ShinyDtoMapper shinyDtoMapper;

    @Value("${app.default-goblin-id:}")
    private String defaultGoblinId;

    @GetMapping("/goblins/{goblinId}/hoards/{hoardId}/shinies")
    public List<ShinyResponseDto> getShiniesByGoblinIdAndHoardId(
            @PathVariable String goblinId,
            @PathVariable String hoardId
    ) {
        return shinyDtoMapper.toResponseDtoList(shinyService.getShiniesByGoblinIdAndHoardId(goblinId, hoardId));
    }

    @GetMapping("/hoards/{hoardId}/shinies")
    public List<ShinyResponseDto> getShiniesByHoardId(@PathVariable String hoardId) {
        if (!StringUtils.hasText(defaultGoblinId)) {
            return List.of();
        }
        return shinyDtoMapper.toResponseDtoList(shinyService.getShiniesByGoblinIdAndHoardId(defaultGoblinId, hoardId));
    }
}
