package com.jayice.goblinfashionengineapi.api.controller;

import com.jayice.goblinfashionengineapi.api.auth.context.AuthenticatedGoblinRequestContext;
import com.jayice.goblinfashionengineapi.api.auth.model.AuthenticatedGoblin;
import com.jayice.goblinfashionengineapi.api.dto.ShinyCreateRequestDto;
import com.jayice.goblinfashionengineapi.api.dto.ShinyPatchRequestDto;
import com.jayice.goblinfashionengineapi.api.dto.ShinyResponseDto;
import com.jayice.goblinfashionengineapi.api.dto.ShinyUpdateRequestDto;
import com.jayice.goblinfashionengineapi.api.mapper.ShinyDtoMapper;
import com.jayice.goblinfashionengineapi.api.service.ShinyAlreadyExistsException;
import com.jayice.goblinfashionengineapi.api.service.ShinyNotFoundException;
import com.jayice.goblinfashionengineapi.api.service.ShinyService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;

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

    @GetMapping("/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}")
    public ShinyResponseDto getShinyByGoblinIdAndHoardIdAndShinyId(
            @PathVariable String goblinId,
            @PathVariable String hoardId,
            @PathVariable String shinyId,
            HttpServletRequest request
    ) {
        AuthenticatedGoblin authenticatedGoblin = AuthenticatedGoblinRequestContext.getRequired(request);
        if (!authenticatedGoblin.goblinId().equals(goblinId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Authenticated goblin does not match path goblinId."
            );
        }

        try {
            return shinyDtoMapper.toResponseDto(shinyService.getShiny(goblinId, hoardId, shinyId));
        } catch (ShinyNotFoundException shinyNotFoundException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, shinyNotFoundException.getMessage());
        }
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

    @PostMapping("/goblins/{goblinId}/hoards/{hoardId}/shinies")
    @ResponseStatus(HttpStatus.CREATED)
    public ShinyResponseDto createShiny(
            @PathVariable String goblinId,
            @PathVariable String hoardId,
            @Valid @RequestBody ShinyCreateRequestDto shinyCreateRequestDto,
            HttpServletRequest request
    ) {
        AuthenticatedGoblin authenticatedGoblin = AuthenticatedGoblinRequestContext.getRequired(request);
        if (!authenticatedGoblin.goblinId().equals(goblinId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Authenticated goblin does not match path goblinId."
            );
        }

        try {
            return shinyDtoMapper.toResponseDto(
                    shinyService.createShiny(
                            goblinId,
                            hoardId,
                            shinyDtoMapper.toCanonicalForCreate(shinyCreateRequestDto)
                    )
            );
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, illegalArgumentException.getMessage());
        } catch (ShinyAlreadyExistsException shinyAlreadyExistsException) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, shinyAlreadyExistsException.getMessage());
        }
    }

    @PutMapping("/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}")
    public ShinyResponseDto updateShiny(
            @PathVariable String goblinId,
            @PathVariable String hoardId,
            @PathVariable String shinyId,
            @Valid @RequestBody ShinyUpdateRequestDto shinyUpdateRequestDto,
            HttpServletRequest request
    ) {
        AuthenticatedGoblin authenticatedGoblin = AuthenticatedGoblinRequestContext.getRequired(request);
        if (!authenticatedGoblin.goblinId().equals(goblinId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Authenticated goblin does not match path goblinId."
            );
        }
        if (!shinyId.equals(shinyUpdateRequestDto.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path shinyId must match payload shiny id.");
        }

        try {
            return shinyDtoMapper.toResponseDto(
                    shinyService.updateShiny(
                            goblinId,
                            hoardId,
                            shinyId,
                            shinyDtoMapper.toCanonicalForUpdate(shinyUpdateRequestDto)
                    )
            );
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, illegalArgumentException.getMessage());
        } catch (ShinyNotFoundException shinyNotFoundException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, shinyNotFoundException.getMessage());
        }
    }

    @DeleteMapping("/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShiny(
            @PathVariable String goblinId,
            @PathVariable String hoardId,
            @PathVariable String shinyId,
            HttpServletRequest request
    ) {
        AuthenticatedGoblin authenticatedGoblin = AuthenticatedGoblinRequestContext.getRequired(request);
        if (!authenticatedGoblin.goblinId().equals(goblinId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Authenticated goblin does not match path goblinId."
            );
        }

        try {
            shinyService.deleteShiny(goblinId, hoardId, shinyId);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, illegalArgumentException.getMessage());
        } catch (ShinyNotFoundException shinyNotFoundException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, shinyNotFoundException.getMessage());
        }
    }

    @PatchMapping("/goblins/{goblinId}/hoards/{hoardId}/shinies/{shinyId}")
    public ShinyResponseDto patchShiny(
            @PathVariable String goblinId,
            @PathVariable String hoardId,
            @PathVariable String shinyId,
            @RequestBody ShinyPatchRequestDto shinyPatchRequestDto,
            HttpServletRequest request
    ) {
        AuthenticatedGoblin authenticatedGoblin = AuthenticatedGoblinRequestContext.getRequired(request);
        if (!authenticatedGoblin.goblinId().equals(goblinId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Authenticated goblin does not match path goblinId."
            );
        }

        try {
            return shinyDtoMapper.toResponseDto(
                    shinyService.patchShiny(
                            goblinId,
                            hoardId,
                            shinyId,
                            shinyDtoMapper.toCanonicalPatch(shinyPatchRequestDto)
                    )
            );
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, illegalArgumentException.getMessage());
        } catch (ShinyNotFoundException shinyNotFoundException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, shinyNotFoundException.getMessage());
        }
    }
}
