package com.jayice.goblinfashionengineapi.api.service;

import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.mapper.ShinyFirestoreMapper;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.model.ShinyDocument;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.repository.ShinyFirestoreGateway;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShinyService {
    private final ShinyFirestoreGateway shinyFirestoreGateway;
    private final ShinyFirestoreMapper shinyFirestoreMapper;

    public ShinyService(ShinyFirestoreGateway shinyFirestoreGateway, ShinyFirestoreMapper shinyFirestoreMapper) {
        this.shinyFirestoreGateway = shinyFirestoreGateway;
        this.shinyFirestoreMapper = shinyFirestoreMapper;
    }

    public List<Shiny> getShiniesByGoblinIdAndHoardId(String goblinId, String hoardId) {
        List<ShinyDocument> shinyDocuments = shinyFirestoreGateway.findByGoblinIdAndHoardId(goblinId, hoardId);
        return shinyFirestoreMapper.toCanonicalList(shinyDocuments);
    }
}
