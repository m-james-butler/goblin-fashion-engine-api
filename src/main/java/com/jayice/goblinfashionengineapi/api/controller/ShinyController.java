package com.jayice.goblinfashionengineapi.api.controller;

import com.jayice.goblinfashionengineapi.api.auth.context.AuthenticatedGoblinRequestContext;
import com.jayice.goblinfashionengineapi.api.auth.model.AuthenticatedGoblin;
import com.jayice.goblinfashionengineapi.api.dto.ShinyResponseDto;
import com.jayice.goblinfashionengineapi.api.mapper.ShinyDtoMapper;
import com.jayice.goblinfashionengineapi.api.service.ShinyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
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
            @PathVariable String hoardId,
            HttpServletRequest request
    ) {
        AuthenticatedGoblin authenticatedGoblin = AuthenticatedGoblinRequestContext.getRequired(request);
        if (!authenticatedGoblin.goblinId().equals(goblinId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Authenticated goblin does not match path goblinId."
            );
        }
        return shinyDtoMapper.toResponseDtoList(shinyService.getShiniesByGoblinIdAndHoardId(goblinId, hoardId));
    }

    @GetMapping("/hoards/{hoardId}/shinies")
    public List<ShinyResponseDto> getShiniesByHoardId(@PathVariable String hoardId, HttpServletRequest request) {
        String resolvedGoblinId = AuthenticatedGoblinRequestContext.get(request)
                .map(AuthenticatedGoblin::goblinId)
                .orElse(defaultGoblinId);
        if (!StringUtils.hasText(resolvedGoblinId)) {
            return List.of();
        }
        return shinyDtoMapper.toResponseDtoList(shinyService.getShiniesByGoblinIdAndHoardId(resolvedGoblinId, hoardId));
    }
}
