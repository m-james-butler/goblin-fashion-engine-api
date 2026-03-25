package com.jayice.goblinfashionengineapi.api.persistence.firestore.repository;

public class DuplicateShinyException extends RuntimeException {

    public DuplicateShinyException(String message, Throwable cause) {
        super(message, cause);
    }
}
