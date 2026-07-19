package org.sopt.poti.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
@Profile("!test")
public class FirebaseConfig {

  @Value("${firebase.credentials-path}")
  private String credentialsPath;

  private final ResourceLoader resourceLoader;

  public FirebaseConfig(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @PostConstruct
  public void initialize() throws IOException {
    if (FirebaseApp.getApps().isEmpty()) {
      Resource resource = resourceLoader.getResource(credentialsPath);
      FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
          .build();
      FirebaseApp.initializeApp(options);
    }
  }
}
