package net.sasakonnect.wallet.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import net.sasakonnect.wallet.tools.JwtService;

@Service
public class RestClientService extends JwtService {
	@Value("${BASE_URL}")
	private String baseUrl;
	protected WebClient webClient;
	protected Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	public RestClientService() {
		this.webClient = WebClient.builder().baseUrl(baseUrl).build();
	}

//	    public Mono<String> fetchData() {
//	        return webClient.get()
//	            .uri("/your-endpoint") // Replace with your API endpoint
//	            .retrieve()
//	            .bodyToMono(String.class);
//	    }
}
