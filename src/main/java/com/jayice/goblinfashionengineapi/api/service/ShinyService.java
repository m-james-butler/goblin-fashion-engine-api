package com.jayice.goblinfashionengineapi.api.service;

import com.jayice.goblinfashionengineapi.api.domain.enums.*;
import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShinyService {
    public List<Shiny> getShiniesByHoardId(String hoardId) {
        return List.of(
                Shiny.builder()
                        .id("SHN-001")
                        .goblinId("GBL-001")
                        .hoardId(hoardId)
                        .name("Black Polo")
                        .filename("black-polo.jpg")
                        .notes("Mock shiny for initial API testing")
                        .count(1)
                        .category(ShinyCategory.TOP)
                        .subcategory("Polo")
                        .layer(Layer.BASE)
                        .contexts(List.of(Context.CASUAL, Context.OFFICE))
                        .formality(Formality.SMART_CASUAL)
                        .attention(Attention.LOW)
                        .colorPrimary(Color.BLACK)
                        .pattern(Pattern.SOLID)
                        .fabric("Cotton")
                        .fit("Regular")
                        .warmth(1)
                        .officeOk(true)
                        .publicWear(true)
                        .includeInEngine(true)
                        .engineInclusionPolicy(EngineInclusionPolicy.NORMAL)
                        .imagePath("/resources/images/mock/black-polo.jpg")
                        .status(ShinyStatus.OWNED)
                        .build()
        );
    }
}
