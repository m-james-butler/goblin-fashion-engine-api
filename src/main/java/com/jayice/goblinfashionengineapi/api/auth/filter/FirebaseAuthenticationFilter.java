package com.jayice.goblinfashionengineapi.api.auth.filter;

import com.jayice.goblinfashionengineapi.api.auth.context.AuthenticatedGoblinRequestContext;
import com.jayice.goblinfashionengineapi.api.auth.model.AuthenticatedGoblin;
import com.jayice.goblinfashionengineapi.api.auth.service.FirebaseTokenVerifier;
import com.jayice.goblinfashionengineapi.api.auth.service.InvalidFirebaseTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Firebase bearer-token filter for goblin tenancy endpoints.
 */
@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REQUIRED_AUTH_PATH_PATTERN = "/api/goblins/*/hoards/*/shinies";
    private static final String REQUIRED_AUTH_ITEM_PATH_PATTERN = "/api/goblins/*/hoards/*/shinies/*";

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final FirebaseTokenVerifier firebaseTokenVerifier;

    /**
     * Creates Firebase auth filter.
     *
     * @param firebaseTokenVerifier token verifier service
     */
    public FirebaseAuthenticationFilter(FirebaseTokenVerifier firebaseTokenVerifier) {
        this.firebaseTokenVerifier = firebaseTokenVerifier;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !requiresAuthentication(request) && !hasBearerToken(request);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<String> bearerToken = extractBearerToken(request);
        if (bearerToken.isEmpty()) {
            if (requiresAuthentication(request)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing bearer token.");
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        try {
            AuthenticatedGoblin authenticatedGoblin = firebaseTokenVerifier.verify(bearerToken.get());
            AuthenticatedGoblinRequestContext.set(request, authenticatedGoblin);
            filterChain.doFilter(request, response);
        } catch (InvalidFirebaseTokenException invalidFirebaseTokenException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid bearer token.");
        }
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        return pathMatcher.match(REQUIRED_AUTH_PATH_PATTERN, requestPath)
                || pathMatcher.match(REQUIRED_AUTH_ITEM_PATH_PATTERN, requestPath);
    }

    private boolean hasBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        return StringUtils.hasText(authorizationHeader)
                && authorizationHeader.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length());
    }

    private Optional<String> extractBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(authorizationHeader)) {
            return Optional.empty();
        }

        if (!authorizationHeader.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return Optional.empty();
        }

        String idToken = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(idToken)) {
            return Optional.empty();
        }
        return Optional.of(idToken);
    }
}
