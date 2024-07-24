package net.sasakonnect.wallet.provider;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class FirebaseWrapper implements Serializable {
	@Value("${FIREBASE_ADMIN_CONFIG_FILE}")
	private String serviceAccountFilePath;
	private static final List<String> SCOPES = Arrays.asList(
			"https://www.googleapis.com/auth/firebase.messaging"
			);
	
	@Bean
	FirebaseApp initialize() throws IOException {  
	     ClassLoader classLoader = getClass().getClassLoader();
	        InputStream inputStream = classLoader.getResourceAsStream(serviceAccountFilePath);
        

				FirebaseOptions options = new FirebaseOptions.Builder()
				  .setCredentials(GoogleCredentials.fromStream(inputStream))
				  .build();
				return FirebaseApp.initializeApp(options);
		

	}

	
	
}
