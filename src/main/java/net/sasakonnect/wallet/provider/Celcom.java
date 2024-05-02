package net.sasakonnect.wallet.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.jobs.SmsProvider;
import net.sasakonnect.wallet.tools.redis.Queueable;
import net.sasakonnect.wallet.workers.FailedSmsJob;
import reactor.core.publisher.Mono;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Service
@PropertySource("classpath:application.properties")

public class Celcom extends SmsProvider {
	private static final long serialVersionUID = 1L;
	@Value("${CELCOM_API_KEY}")
	private String celcomKey;

	@Value("${CELCOM_URL}")
	private String celcomUrl;

	@Value("${CELCOM_PARTNER_ID}")
	private String partnerID;

	@Value("${SMS_SENDER_ID}")
	private String shortcode;
	@Value("${WALLET_TEMPLATE_LOGIN}")
	private String template;

	@Value("${CELCOM_COOKIE}")
	private String celcom_cookie;

	@Value("${OTP_TTL}")
	private String otp_ttl;

	@Override
	public void executeJob() {
		WebClient.Builder webClient = WebClient.builder();

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Cookie", celcom_cookie);
			headers.setContentType(MediaType.APPLICATION_JSON);
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode requestBody = objectMapper.createObjectNode();
			requestBody.put("apikey", celcomKey);
			requestBody.put("partnerID", partnerID);
			requestBody.put("mobile", this.getPhoneNumber());
			requestBody.put("message", this.getTemplate());
			requestBody.put("shortcode", shortcode);

			Mono<ObjectNode> responseMono = webClient.baseUrl(this.celcomUrl).build().post()

					.uri("/api/services/sendsms").headers(httpHeaders -> httpHeaders.addAll(headers)) // Add custom

					.body(BodyInserters.fromValue(requestBody)).exchangeToMono(response -> {
						System.out.println(response.statusCode());

						HttpStatusCode httpStatus = response.statusCode();
						if (httpStatus.equals(HttpStatus.OK)) {

							return response.bodyToMono(ObjectNode.class);
						} else {
							// Handle other status codes if needed.
							return Mono.error(new RuntimeException("Unexpected Status Code: " + httpStatus));
						}
					});
			responseMono.subscribe(response -> {
				// Handle the response here
				System.out.println("Received response: " + response);
			}, error -> {
				FailedSmsJob smsFailedJob = new FailedSmsJob();
				smsFailedJob.setFailedSmsProvider(this);
				smsFailedJob.setPhoneNumber(this.getPhoneNumber());
				smsFailedJob.setRetryCount(this.getRetryCount() + 1);
				smsFailedJob.setMessageTemplate(this.template);
				SmsManager.addFailedJob(smsFailedJob);
				// Handle any errors here
			}, () -> {
				// Handle completion (optional)
				System.out.println("Request completed.");
			});

		} catch (Exception e) {
			// Handle exceptions here
			e.printStackTrace();
		}
	}

}
