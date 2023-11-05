package net.sasakonnect.wallet.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BankWebClientBean {

	@Value("${BASE_URL}")
	private String baseUrl;
	public WebClient webClient = WebClient.builder().baseUrl(baseUrl)
			.codecs(configurer -> configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder()))

			.build();
	public Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

}
