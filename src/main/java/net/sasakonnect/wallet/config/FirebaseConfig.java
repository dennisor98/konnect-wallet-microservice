package net.sasakonnect.wallet.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
@Component
public class FirebaseConfig {
//	@Value("classpath:static/wallet-e3a3d-firebase-adminsdk-ivrv5-cb27fb44d0.json")
//	Resource SERVICE_ACC_OBJ;
//	private static final List<String> SCOPES = Arrays.asList(
//			"https://www.googleapis.com/auth/firebase.messaging"
//			);
//	@PostConstruct
//	FirebaseApp initialize() {  
//		try {  
//			InputStream serviceAccount =  
//					this.SERVICE_ACC_OBJ.getInputStream();  
//			FirebaseOptions options = new FirebaseOptions.Builder()  
//					.setCredentials(GoogleCredentials.fromStream(serviceAccount))  
//					.setDatabaseUrl("https://wallet-app.firebaseio.com")  
//					.build();  
//			return FirebaseApp.initializeApp(options);  
//		} catch (Exception e) {  
//			e.printStackTrace();  
//		}
//		return null;
//	}
	
//	@Bean
//	private  String getAccessToken() throws IOException {
//		try (InputStream credentialsStream = this.SERVICE_ACC_OBJ.getInputStream())  {
//			if (credentialsStream == null) {
//				throw new IOException("Resource not found: wallet-e3a3d-firebase-adminsdk-ivrv5-cb27fb44d0.json");
//			}
//			GoogleCredentials googleCredentials = GoogleCredentials
//					.fromStream(credentialsStream)
//					.createScoped(SCOPES);
//			googleCredentials.refresh();
//			return googleCredentials.getAccessToken().getTokenValue();
//		}
//	}
}
