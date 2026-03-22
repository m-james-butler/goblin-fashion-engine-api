package com.jayice.goblinfashionengineapi.api.persistence.firestore.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Firebase Admin and Firestore bean configuration.
 *
 * <p>Cloud Run should use Application Default Credentials. Local development can use either
 * GOOGLE_APPLICATION_CREDENTIALS or firebase.credentials-path.
 */
@Configuration
@EnableConfigurationProperties(FirebaseProperties.class)
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp(FirebaseProperties firebaseProperties) throws IOException {
        List<FirebaseApp> existingApps = FirebaseApp.getApps();
        if (!existingApps.isEmpty()) {
            return existingApps.getFirst();
        }

        GoogleCredentials credentials = loadCredentials(firebaseProperties);
        FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder().setCredentials(credentials);
        if (StringUtils.hasText(firebaseProperties.getProjectId())) {
            optionsBuilder.setProjectId(firebaseProperties.getProjectId());
        }

        return FirebaseApp.initializeApp(optionsBuilder.build());
    }

    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        return FirestoreClient.getFirestore(firebaseApp);
    }

    private GoogleCredentials loadCredentials(FirebaseProperties firebaseProperties) throws IOException {
        if (StringUtils.hasText(firebaseProperties.getCredentialsPath())) {
            try (InputStream inputStream = new FileInputStream(firebaseProperties.getCredentialsPath())) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }
        return GoogleCredentials.getApplicationDefault();
    }
}
