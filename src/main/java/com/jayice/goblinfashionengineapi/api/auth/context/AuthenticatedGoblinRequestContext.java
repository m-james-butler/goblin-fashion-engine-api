package com.jayice.goblinfashionengineapi.api.auth.context;

import com.jayice.goblinfashionengineapi.api.auth.model.AuthenticatedGoblin;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Request-scoped authenticated goblin storage.
 */
public final class AuthenticatedGoblinRequestContext {
    private static final String AUTHENTICATED_GOBLIN_ATTRIBUTE =
            AuthenticatedGoblinRequestContext.class.getName() + ".AUTHENTICATED_GOBLIN";

    private AuthenticatedGoblinRequestContext() {
    }

    /**
     * Stores authenticated goblin identity on the current request.
     *
     * @param request             HTTP request
     * @param authenticatedGoblin authenticated goblin principal
     */
    public static void set(HttpServletRequest request, AuthenticatedGoblin authenticatedGoblin) {
        request.setAttribute(AUTHENTICATED_GOBLIN_ATTRIBUTE, authenticatedGoblin);
    }

    /**
     * Reads authenticated goblin identity from the current request.
     *
     * @param request HTTP request
     * @return optional authenticated goblin principal
     */
    public static Optional<AuthenticatedGoblin> get(HttpServletRequest request) {
        Object attribute = request.getAttribute(AUTHENTICATED_GOBLIN_ATTRIBUTE);
        if (attribute instanceof AuthenticatedGoblin authenticatedGoblin) {
            return Optional.of(authenticatedGoblin);
        }
        return Optional.empty();
    }

    /**
     * Reads authenticated goblin identity from request or throws unauthorized status.
     *
     * @param request HTTP request
     * @return authenticated goblin principal
     */
    public static AuthenticatedGoblin getRequired(HttpServletRequest request) {
        return get(request).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Missing authenticated goblin context."
                )
        );
    }
}
