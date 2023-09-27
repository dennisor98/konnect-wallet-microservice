package net.sasakonnect.wallet.beans;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import net.sasakonnect.wallet.constant.EndpointsConstants;

@Component
public class BankWebClientBean {
	public WebClient webClient = WebClient.builder().baseUrl(EndpointsConstants.BASE_URL).build();
	public Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

}
