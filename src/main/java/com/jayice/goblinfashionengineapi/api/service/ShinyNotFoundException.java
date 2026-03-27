package com.jayice.goblinfashionengineapi.api.service;

public class ShinyNotFoundException extends RuntimeException {

    public ShinyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShinyNotFoundException(String message) {
        super(message);
    }
}
