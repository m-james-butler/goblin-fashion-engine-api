package com.jayice.goblinfashionengineapi.api.persistence.firestore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Firebase configuration properties for runtime credentials and project selection.
 */
@ConfigurationProperties(prefix = "firebase")
public class FirebaseProperties {
    private String credentialsPath;
    private String projectId;

    public String getCredentialsPath() {
        return credentialsPath;
    }

    public void setCredentialsPath(String credentialsPath) {
        this.credentialsPath = credentialsPath;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
