package com.jayice.goblinfashionengineapi.api.auth.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.jayice.goblinfashionengineapi.api.auth.model.AuthenticatedGoblin;
import org.springframework.stereotype.Service;

/**
 * Verifies Firebase ID tokens and maps them to internal authenticated goblin principals.
 */
@Service
public class FirebaseTokenVerifier {
    private static final boolean CHECK_REVOKED_DEFAULT = false;
    private final FirebaseAuth firebaseAuth;

    /**
     * Creates a token verifier using Firebase Admin SDK auth client.
     *
     * @param firebaseAuth Firebase auth client
     */
    public FirebaseTokenVerifier(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    /**
     * Verifies Firebase ID token without revocation checks.
     *
     * @param idToken Firebase ID token from bearer header
     * @return authenticated goblin principal
     */
    public AuthenticatedGoblin verify(String idToken) {
        return verify(idToken, CHECK_REVOKED_DEFAULT);
    }

    /**
     * Verifies Firebase ID token and maps UID to authoritative goblin identity.
     *
     * @param idToken      Firebase ID token from bearer header
     * @param checkRevoked whether revocation checks should be applied
     * @return authenticated goblin principal
     */
    public AuthenticatedGoblin verify(String idToken, boolean checkRevoked) {
        try {
            FirebaseToken verifiedToken = firebaseAuth.verifyIdToken(idToken, checkRevoked);
            return new AuthenticatedGoblin(verifiedToken.getUid());
        } catch (FirebaseAuthException firebaseAuthException) {
            throw new InvalidFirebaseTokenException("Token verification failed.", firebaseAuthException);
        }
    }
}
