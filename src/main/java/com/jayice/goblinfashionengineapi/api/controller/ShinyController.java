package com.jayice.goblinfashionengineapi.api.controller;

import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.service.ShinyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/hoards")
@RequiredArgsConstructor
public class ShinyController {
    private final ShinyService shinyService;

    @GetMapping("/{hoardId}/shinies")
    public List<Shiny> getShiniesByHoardId(@PathVariable String hoardId) {
        return shinyService.getShiniesByHoardId(hoardId);
    }
}
