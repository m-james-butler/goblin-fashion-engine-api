package com.jayice.goblinfashionengineapi.api.persistence.firestore.repository;

import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.jayice.goblinfashionengineapi.api.persistence.firestore.model.ShinyDocument;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShinyFirestoreGatewayTest {

    @Test
    void createShinyWritesToGoblinHoardShiniesPathAndReturnsDocument() throws Exception {
        Firestore firestore = mock(Firestore.class);
        CollectionReference goblinsCollection = mock(CollectionReference.class);
        DocumentReference goblinDocument = mock(DocumentReference.class);
        CollectionReference hoardsCollection = mock(CollectionReference.class);
        DocumentReference hoardDocument = mock(DocumentReference.class);
        CollectionReference shiniesCollection = mock(CollectionReference.class);
        DocumentReference shinyDocumentReference = mock(DocumentReference.class);

        when(firestore.collection("goblins")).thenReturn(goblinsCollection);
        when(goblinsCollection.document("GBL-001")).thenReturn(goblinDocument);
        when(goblinDocument.collection("hoards")).thenReturn(hoardsCollection);
        when(hoardsCollection.document("HRD-001")).thenReturn(hoardDocument);
        when(hoardDocument.collection("shinies")).thenReturn(shiniesCollection);
        when(shiniesCollection.document("SH-001")).thenReturn(shinyDocumentReference);
        when(shinyDocumentReference.create(any(ShinyDocument.class))).thenReturn(ApiFutures.immediateFuture(null));

        ShinyFirestoreGateway gateway = new ShinyFirestoreGateway(firestore);
        ShinyDocument shinyDocument = ShinyDocument.builder()
                .id("SH-001")
                .name("Battle Jacket")
                .count(1)
                .build();

        ShinyDocument createdShiny = gateway.createShiny("GBL-001", "HRD-001", shinyDocument);

        assertNotNull(createdShiny);
        assertEquals("SH-001", createdShiny.getId());
        assertEquals("GBL-001", createdShiny.getGoblinId());
        assertEquals("HRD-001", createdShiny.getHoardId());

        verify(firestore).collection("goblins");
        verify(goblinsCollection).document("GBL-001");
        verify(goblinDocument).collection("hoards");
        verify(hoardsCollection).document("HRD-001");
        verify(hoardDocument).collection("shinies");
        verify(shiniesCollection).document("SH-001");
        verify(shinyDocumentReference).create(shinyDocument);
    }

    @Test
    void createShinyRejectsBlankOrNullShinyId() {
        Firestore firestore = mock(Firestore.class);
        ShinyFirestoreGateway gateway = new ShinyFirestoreGateway(firestore);

        ShinyDocument nullId = ShinyDocument.builder().id(null).build();
        ShinyDocument blankId = ShinyDocument.builder().id("   ").build();

        assertThrows(IllegalArgumentException.class, () -> gateway.createShiny("GBL-001", "HRD-001", nullId));
        assertThrows(IllegalArgumentException.class, () -> gateway.createShiny("GBL-001", "HRD-001", blankId));
    }

    @Test
    void createShinyTranslatesAlreadyExistsFailureToDuplicateShinyException() throws Exception {
        Firestore firestore = mock(Firestore.class);
        CollectionReference goblinsCollection = mock(CollectionReference.class);
        DocumentReference goblinDocument = mock(DocumentReference.class);
        CollectionReference hoardsCollection = mock(CollectionReference.class);
        DocumentReference hoardDocument = mock(DocumentReference.class);
        CollectionReference shiniesCollection = mock(CollectionReference.class);
        DocumentReference shinyDocumentReference = mock(DocumentReference.class);

        when(firestore.collection("goblins")).thenReturn(goblinsCollection);
        when(goblinsCollection.document("GBL-001")).thenReturn(goblinDocument);
        when(goblinDocument.collection("hoards")).thenReturn(hoardsCollection);
        when(hoardsCollection.document("HRD-001")).thenReturn(hoardDocument);
        when(hoardDocument.collection("shinies")).thenReturn(shiniesCollection);
        when(shiniesCollection.document("SH-001")).thenReturn(shinyDocumentReference);
        when(shinyDocumentReference.create(any(ShinyDocument.class)))
                .thenReturn(ApiFutures.immediateFailedFuture(new RuntimeException("ALREADY_EXISTS")));

        ShinyFirestoreGateway gateway = new ShinyFirestoreGateway(firestore);
        ShinyDocument shinyDocument = ShinyDocument.builder()
                .id("SH-001")
                .name("Battle Jacket")
                .count(1)
                .build();

        assertThrows(
                DuplicateShinyException.class,
                () -> gateway.createShiny("GBL-001", "HRD-001", shinyDocument)
        );
    }
}
