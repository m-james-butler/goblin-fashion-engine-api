package com.jayice.goblinfashionengineapi.api.auth.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.jayice.goblinfashionengineapi.api.auth.model.AuthenticatedGoblin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FirebaseTokenVerifierTest {

    @Mock
    private FirebaseAuth firebaseAuth;

    @Mock
    private FirebaseToken firebaseToken;

    @InjectMocks
    private FirebaseTokenVerifier firebaseTokenVerifier;

    @Test
    void verifyMapsFirebaseUidToAuthenticatedGoblin() throws Exception {
        when(firebaseAuth.verifyIdToken("good-token", false)).thenReturn(firebaseToken);
        when(firebaseToken.getUid()).thenReturn("GBL-777");

        AuthenticatedGoblin authenticatedGoblin = firebaseTokenVerifier.verify("good-token");

        assertThat(authenticatedGoblin.goblinId()).isEqualTo("GBL-777");
        verify(firebaseAuth).verifyIdToken("good-token", false);
    }

    @Test
    void verifyWrapsFirebaseAuthExceptionAsInvalidToken() throws Exception {
        FirebaseAuthException firebaseAuthException = org.mockito.Mockito.mock(FirebaseAuthException.class);
        when(firebaseAuth.verifyIdToken("bad-token", false)).thenThrow(firebaseAuthException);

        assertThatThrownBy(() -> firebaseTokenVerifier.verify("bad-token"))
                .isInstanceOf(InvalidFirebaseTokenException.class)
                .hasMessageContaining("Token verification failed");
    }
}
