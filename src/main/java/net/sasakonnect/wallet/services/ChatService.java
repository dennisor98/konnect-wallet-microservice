package net.sasakonnect.wallet.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.tools.OpenFireClient;
import reactor.core.publisher.Mono;

@Service
public class ChatService {
	@Autowired
	OpenFireClient openFileClient;

	public Mono<String> registerUserToOpenFire(User user) {
		System.err.println("calling user");
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("username", user.getId());
		requestBody.put("password", "p4ssword");
		requestBody.put("name", user.getFirstName() + " " + user.getLastName());
		requestBody.put("email", user.getMobile());
		return this.openFileClient.getWebClient().post().uri("plugins/restapi/v1/users")
				.contentType(MediaType.APPLICATION_JSON).bodyValue(requestBody).retrieve()

				.bodyToMono(String.class);

	}
}
