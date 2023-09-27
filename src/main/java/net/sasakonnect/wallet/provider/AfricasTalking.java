package net.sasakonnect.wallet.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.sasakonnect.wallet.domain.Otp;
import net.sasakonnect.wallet.jobs.SmsProvider;
import net.sasakonnect.wallet.tools.redis.Queueable;
import reactor.core.publisher.Mono;

@Service

public class AfricasTalking extends SmsProvider {
	private static final long serialVersionUID = 1L;
	@Value("${AFRICAS_TAKING_API_KEY}")
	private String africasTalkingKey;

	@Value("${AFRICAS_TAKING_URL}")
	private String africasTalkingUrl;

	@Value("${WALLET_TEMPLATE_LOGIN}")
	private String template;

	@Value("AFRICAS_TALKING_USERNAME")
	private String username;

	@Value("AFRICAS_TALKING_FROM")
	private String from;

	@Value("${OTP_TTL}")
	private String otp_ttl;

	@Override
	public void executeJob(Queueable job) {
		WebClient.Builder webClient = WebClient.builder();
		Otp savedOtp = (Otp) job.params;

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("apiKey", africasTalkingKey);
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode requestBody = objectMapper.createObjectNode();
			requestBody.put("mobile", this.getPhoneNumber());
			requestBody.put("message", this.template);

			Mono<ObjectNode> responseMono = webClient.baseUrl(this.africasTalkingUrl).build().post()
					.uri("/version1/messaging").contentType(MediaType.APPLICATION_FORM_URLENCODED)

					.body(BodyInserters.fromFormData("message", this.template).with("to", this.getPhoneNumber())
							.with("from", from).with("username", username)

					).retrieve().bodyToMono(ObjectNode.class);
			responseMono.subscribe(response -> {
				// Handle the response here
				System.out.println("Received response: " + response);
			}, error -> {
				// Handle any errors here
				System.err.println("Error: " + error.getMessage());
			}, () -> {
				// Handle completion (optional)
				System.out.println("Request completed.");
			});

		} catch (Exception e) {
			// Handle exceptions here
			e.printStackTrace();
		}
		// TODO Auto-generated method stub

	}

}
