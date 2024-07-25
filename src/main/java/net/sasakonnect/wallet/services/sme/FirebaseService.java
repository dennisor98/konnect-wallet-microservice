package net.sasakonnect.wallet.services.sme;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import net.sasakonnect.wallet.domain.Notifications;

@Service
public class FirebaseService {

	
 public void sendMessage(Notifications notification) {
	 String token = notification.getTargetUser().getFirebaseTokens().get(0).getToken();
	 Message message = Message.builder()
	     .putData("title",notification.getTitle())
	     .putData("message",notification.getMessage())
	     .setToken(token)
	     .build();

	 // Send a message to the device corresponding to the provided
	 // registration token.
	 String response;
	try {
		response = FirebaseMessaging.getInstance().send(message);
		 System.out.println("Successfully sent message to firebase: " + response);
	} catch (FirebaseMessagingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
 }
 
 public void sendBroadCastMessage(Notifications notification) {
	 Message message = Message.builder()
		     .putData("title",notification.getTitle())
		     .putData("message",notification.getMessage())
		     .setTopic("konnect-broadcast")
		     .build();

		 // Send a message to the device corresponding to the provided
		 // registration token.
		 String response;
		try {
			response = FirebaseMessaging.getInstance().send(message);
			 System.out.println("Successfully sent message: " + response);
		} catch (FirebaseMessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 }
 
 
}
