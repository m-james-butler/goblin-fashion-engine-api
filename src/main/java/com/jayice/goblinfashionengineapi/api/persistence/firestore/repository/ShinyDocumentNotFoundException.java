package com.jayice.goblinfashionengineapi.api.persistence.firestore.repository;

public class ShinyDocumentNotFoundException extends RuntimeException {

    public ShinyDocumentNotFoundException(String message) {
        super(message);
    }
}
