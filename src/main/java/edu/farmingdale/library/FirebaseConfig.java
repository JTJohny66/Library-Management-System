package edu.farmingdale.library;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;

import java.io.InputStream;

public class FirebaseConfig {

    private static Firestore db;

    public static Firestore getDB() {
        if (db == null) {
            try {
                InputStream serviceAccount = FirebaseConfig.class.getResourceAsStream("/firebase-key.json");

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                db = FirestoreClient.getFirestore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return db;
    }
}
