package com.jayice.goblinfashionengineapi.api.persistence.firestore.bootstrap;

import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.legacy.mapper.ShinyMapper;
import com.jayice.goblinfashionengineapi.api.legacy.model.LegacyShiny;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.mapper.ShinyFirestoreMapper;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.model.ShinyDocument;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.repository.ShinyFirestoreGateway;
import com.jayice.goblinfashionengineapi.api.service.LegacyInventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Optional bootstrap importer for moving legacy JSON inventory into Firestore.
 */
@Component
public class LegacyInventoryFirestoreBootstrapImporter implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyInventoryFirestoreBootstrapImporter.class);

    private final LegacyInventoryService legacyInventoryService;
    private final ShinyMapper legacyShinyMapper;
    private final ShinyFirestoreMapper shinyFirestoreMapper;
    private final ShinyFirestoreGateway shinyFirestoreGateway;

    @Value("${app.bootstrap.legacy-inventory.enabled:false}")
    private boolean importEnabled;

    @Value("${app.bootstrap.legacy-inventory.only-if-empty:true}")
    private boolean importOnlyIfEmpty;

    @Value("${app.bootstrap.legacy-inventory.goblin-id:}")
    private String targetGoblinId;

    @Value("${app.bootstrap.legacy-inventory.hoard-id:}")
    private String targetHoardId;

    public LegacyInventoryFirestoreBootstrapImporter(
            LegacyInventoryService legacyInventoryService,
            ShinyMapper legacyShinyMapper,
            ShinyFirestoreMapper shinyFirestoreMapper,
            ShinyFirestoreGateway shinyFirestoreGateway
    ) {
        this.legacyInventoryService = legacyInventoryService;
        this.legacyShinyMapper = legacyShinyMapper;
        this.shinyFirestoreMapper = shinyFirestoreMapper;
        this.shinyFirestoreGateway = shinyFirestoreGateway;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!importEnabled) {
            LOGGER.info("Legacy inventory bootstrap import disabled.");
            return;
        }

        if (!StringUtils.hasText(targetGoblinId) || !StringUtils.hasText(targetHoardId)) {
            LOGGER.warn("Legacy inventory bootstrap import skipped: goblinId and hoardId must both be configured.");
            return;
        }

        if (importOnlyIfEmpty && !shinyFirestoreGateway.isShinyCollectionEmpty(targetGoblinId, targetHoardId)) {
            LOGGER.info(
                    "Legacy inventory bootstrap import skipped: Firestore already has shinies for goblin '{}' and hoard '{}'.",
                    targetGoblinId,
                    targetHoardId
            );
            return;
        }

        List<LegacyShiny> legacyShinies = legacyInventoryService.loadInventory();
        if (legacyShinies.isEmpty()) {
            LOGGER.info("Legacy inventory bootstrap import skipped: no legacy shinies found.");
            return;
        }

        List<Shiny> canonicalShinies = legacyShinyMapper.toCanonicalList(legacyShinies);
        List<ShinyDocument> shinyDocuments = shinyFirestoreMapper.toDocumentList(canonicalShinies).stream()
                .peek(document -> {
                    document.setGoblinId(targetGoblinId);
                    document.setHoardId(targetHoardId);
                })
                .toList();

        shinyFirestoreGateway.saveAllForGoblinAndHoard(targetGoblinId, targetHoardId, shinyDocuments);
        LOGGER.info(
                "Legacy inventory bootstrap import wrote {} shiny documents to goblin '{}' and hoard '{}'.",
                shinyDocuments.size(),
                targetGoblinId,
                targetHoardId
        );
    }
}
