package com.example.demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.service-account-file:firebase-service-account.json}")
    private String serviceAccountFile;

    @PostConstruct
    public void init() {
        try {
            // Check if already initialized (hot reload protection)
            if (!FirebaseApp.getApps().isEmpty()) {
                logger.info("Firebase already initialized, skipping...");
                return;
            }

            InputStream serviceAccount = null;
            
            // Try to load from classpath first
            serviceAccount = getClass().getClassLoader().getResourceAsStream(serviceAccountFile);
            
            // If not in classpath, try absolute path
            if (serviceAccount == null) {
                try {
                    serviceAccount = new FileInputStream(serviceAccountFile);
                } catch (Exception e) {
                    logger.warn("Could not load service account from file: {}", serviceAccountFile);
                }
            }

            if (serviceAccount == null) {
                logger.error("Firebase service account file not found: {}. " +
                           "Download it from Firebase Console > Project Settings > Service Accounts", 
                           serviceAccountFile);
                logger.warn("Push notifications will NOT work until Firebase is configured!");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            logger.info("Firebase initialized successfully");

        } catch (Exception e) {
            logger.error("Failed to initialize Firebase: {}", e.getMessage(), e);
            logger.warn("Push notifications will NOT work!");
        }
    }
}
