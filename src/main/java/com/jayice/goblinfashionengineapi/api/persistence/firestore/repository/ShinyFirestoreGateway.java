package com.jayice.goblinfashionengineapi.api.persistence.firestore.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.model.ShinyDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Firestore gateway for shiny documents under a goblin hoard path.
 */
@Component
public class ShinyFirestoreGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShinyFirestoreGateway.class);

    private static final String GOBLINS_COLLECTION = "goblins";
    private static final String HOARDS_COLLECTION = "hoards";
    private static final String SHINIES_COLLECTION = "shinies";

    private final Firestore firestore;

    public ShinyFirestoreGateway(Firestore firestore) {
        this.firestore = firestore;
    }

    public List<ShinyDocument> findByGoblinIdAndHoardId(String goblinId, String hoardId) {
        if (!StringUtils.hasText(goblinId) || !StringUtils.hasText(hoardId)) {
            return List.of();
        }

        try {
            QuerySnapshot querySnapshot = shinyCollection(goblinId, hoardId).get().get();
            return querySnapshot.getDocuments().stream()
                    .map(snapshot -> toShinyDocument(snapshot, goblinId, hoardId))
                    .filter(item -> item != null)
                    .toList();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            LOGGER.error(
                    "Interrupted while reading shinies for goblin '{}' and hoard '{}'. Returning empty list.",
                    goblinId,
                    hoardId,
                    exception
            );
            return List.of();
        } catch (ExecutionException exception) {
            LOGGER.error(
                    "Failed Firestore read for goblin '{}' and hoard '{}'. Returning empty list.",
                    goblinId,
                    hoardId,
                    exception
            );
            return List.of();
        }
    }

    public boolean isShinyCollectionEmpty(String goblinId, String hoardId) {
        if (!StringUtils.hasText(goblinId) || !StringUtils.hasText(hoardId)) {
            return true;
        }

        try {
            QuerySnapshot querySnapshot = shinyCollection(goblinId, hoardId).limit(1).get().get();
            return querySnapshot.isEmpty();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while checking Firestore collection emptiness.", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Failed to check Firestore collection emptiness.", exception);
        }
    }

    public void saveAllForGoblinAndHoard(String goblinId, String hoardId, List<ShinyDocument> shinyDocuments) {
        if (!StringUtils.hasText(goblinId) || !StringUtils.hasText(hoardId)) {
            throw new IllegalArgumentException("goblinId and hoardId are required for Firestore writes.");
        }
        if (shinyDocuments == null || shinyDocuments.isEmpty()) {
            return;
        }

        WriteBatch writeBatch = firestore.batch();
        CollectionReference collectionReference = shinyCollection(goblinId, hoardId);
        for (ShinyDocument shinyDocument : shinyDocuments) {
            if (shinyDocument == null || !StringUtils.hasText(shinyDocument.getId())) {
                continue;
            }
            shinyDocument.setGoblinId(goblinId);
            shinyDocument.setHoardId(hoardId);

            DocumentReference documentReference = collectionReference.document(shinyDocument.getId());
            writeBatch.set(documentReference, shinyDocument);
        }

        try {
            writeBatch.commit().get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while writing shinies to Firestore.", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Failed writing shinies to Firestore.", exception);
        }
    }

    private CollectionReference shinyCollection(String goblinId, String hoardId) {
        return firestore.collection(GOBLINS_COLLECTION)
                .document(goblinId)
                .collection(HOARDS_COLLECTION)
                .document(hoardId)
                .collection(SHINIES_COLLECTION);
    }

    private ShinyDocument toShinyDocument(DocumentSnapshot snapshot, String goblinId, String hoardId) {
        ShinyDocument shinyDocument = snapshot.toObject(ShinyDocument.class);
        if (shinyDocument == null) {
            return null;
        }
        if (!StringUtils.hasText(shinyDocument.getId())) {
            shinyDocument.setId(snapshot.getId());
        }
        if (!StringUtils.hasText(shinyDocument.getGoblinId())) {
            shinyDocument.setGoblinId(goblinId);
        }
        if (!StringUtils.hasText(shinyDocument.getHoardId())) {
            shinyDocument.setHoardId(hoardId);
        }
        return shinyDocument;
    }
}
