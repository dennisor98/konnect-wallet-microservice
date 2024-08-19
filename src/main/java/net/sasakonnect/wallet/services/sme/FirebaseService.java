package net.sasakonnect.wallet.services.sme;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.domain.Notifications;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.repository.FirebaseTokenRepository;

@Service
@Slf4j
public class FirebaseService {
@Autowired
FirebaseTokenRepository firebaseTokenRepository;

@Value("${firebaseBroadcastTopic}")
String firrebaseBrodcastTopic;
 public void sendMessage(Notifications notification) {
	 Message message = Message.builder()
	     .putData("title",notification.getTitle())
	     .putData("message",notification.getMessage())
	     .putData("caption",notification.getCaption())
	     .setTopic(notification.getTargetUser().getId())
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
		     .putData("caption",notification.getCaption())
		     .setTopic(firrebaseBrodcastTopic)
		     .build();
	 
  log.info(firrebaseBrodcastTopic);
		 // Send a message to the device corresponding to the provided
		 // registration token.
		 String response;
		try {
			Map<String,Object> map = new HashMap<>();
			map.put("notifications", map);
			response = FirebaseMessaging.getInstance().send(message);
			 System.out.println("Successfully sent message: " + response);
		} catch (FirebaseMessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 }
 
 @Transactional
 public void deleteTokensByUser(User user) {
	 this.firebaseTokenRepository.deleteByUser(user);
 }
 
 
}
