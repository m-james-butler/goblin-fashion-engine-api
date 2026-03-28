package com.jayice.goblinfashionengineapi.api.logging;

public final class LoggingSanitizer {
    private LoggingSanitizer() {
    }

    public static String sanitizePath(String requestUri) {
        if (requestUri == null || requestUri.isBlank()) {
            return "<empty>";
        }

        return requestUri
                .replaceAll("/goblins/[^/]+", "/goblins/{goblinId}")
                .replaceAll("/hoards/[^/]+", "/hoards/{hoardId}")
                .replaceAll("/shinies/[^/]+", "/shinies/{shinyId}");
    }

    public static String maskIdentifier(String value) {
        if (value == null || value.isBlank()) {
            return "<blank>";
        }
        if (value.length() <= 4) {
            return "***";
        }
        return value.substring(0, 2) + "***" + value.substring(value.length() - 2);
    }
}
