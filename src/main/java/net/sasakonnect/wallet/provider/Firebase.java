package net.sasakonnect.wallet.provider;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import net.sasakonnect.wallet.jobs.PushProvider;
import net.sasakonnect.wallet.tools.redis.Queueable;

public class Firebase extends PushProvider<List<FirebaseMessage>> {
	private static final long serialVersionUID = 1L;
	@Autowired
	FirebaseWrapper firebaseProvider;

	public Firebase(FirebaseWrapper firebaseProvider) {
		this.firebaseProvider = firebaseProvider;
	}

	@Override
	public void executeJob(Queueable<List<FirebaseMessage>> job) {
		try {
			var messages = job.params.stream().map(element -> Message.builder().setNotification(Notification.builder()

					.setTitle("hello").setBody("body").build()).setToken("kkk").build()).collect(Collectors.toList());
			if (job.params.size() == 1) {

				FirebaseMessaging.getInstance(firebaseProvider.getFirebaseApp()).send(messages.get(0));

			} else {
				FirebaseMessaging.getInstance(firebaseProvider.getFirebaseApp()).sendAll(messages);

			}
		} catch (FirebaseMessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
