package com.jayice.goblinfashionengineapi.api.service;

public class ShinyAlreadyExistsException extends RuntimeException {

    public ShinyAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShinyAlreadyExistsException(String message) {
        super(message);
    }
}
