package za.co.cajones.bankx;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class BankXApplication {

	public static void main(String[] args) {
    	log.info("**** Bank X Application Started");
	    SpringApplication.run(BankXApplication.class, args);
	}

	@Bean
	FirebaseMessaging firebaseMessaging() throws IOException {
	    GoogleCredentials googleCredentials = GoogleCredentials
	            .fromStream(new ClassPathResource("firebase-service-account.json").getInputStream());
	    FirebaseOptions firebaseOptions = FirebaseOptions
	            .builder()
	            .setCredentials(googleCredentials)
	            .build();
	    FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "FirebaseApp");
	    return FirebaseMessaging.getInstance(app);
	}
}