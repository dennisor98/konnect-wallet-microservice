package net.sasakonnect.wallet.beans;

import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;

@Component
public class BankWebClientBean {
	public WebClient webClient = WebClient.builder().baseUrl(ChoiceEndpointsConstants.BASE_URL)
			.codecs(configurer -> configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder()))

			.build();
	public Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

}
