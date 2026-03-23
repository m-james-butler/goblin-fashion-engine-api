package com.jayice.goblinfashionengineapi;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class GoblinFashionEngineApiApplicationTests {
    @MockitoBean
    private FirebaseApp firebaseApp;

    @MockitoBean
    private Firestore firestore;

    @MockitoBean
    private FirebaseAuth firebaseAuth;

    @Test
    void contextLoads() {
    }

}
