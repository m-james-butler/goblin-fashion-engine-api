package com.jayice.goblinfashionengineapi.api.auth.service;

/**
 * Raised when Firebase ID token verification fails.
 */
public class InvalidFirebaseTokenException extends RuntimeException {

    /**
     * Creates an invalid-token exception.
     *
     * @param message error message
     */
    public InvalidFirebaseTokenException(String message) {
        super(message);
    }

    /**
     * Creates an invalid-token exception with cause.
     *
     * @param message error message
     * @param cause   root cause
     */
    public InvalidFirebaseTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
