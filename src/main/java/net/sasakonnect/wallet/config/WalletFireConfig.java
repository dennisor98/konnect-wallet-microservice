package net.sasakonnect.wallet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.WebClient;

import net.sasakonnect.wallet.tools.OpenFireClient;

@Configuration
public class WalletFireConfig {
	@Value("${OPEN_FIRE_XMMP_HOST}")
	private String host;
	@Value("${OPEN_FIRE_PORT}")
	private Integer port;
	@Value("${OPEN_FIRE_SHARE_SECRET}")
	private String secret;
	@Value("${OPEN_FIRE_ADMIN_NAME}")
	private String username;
	@Value("${OPEN_FIRE_PASSWORD}")
	private String password;

	@Bean
	OpenFireClient getWalletOpenFire() {
		HttpHeaders commonHeaders = new HttpHeaders();
		commonHeaders.setContentType(MediaType.APPLICATION_JSON);
		commonHeaders.set("Authorization", secret);
		var client = WebClient.builder().baseUrl(host + ":" + port)
				.codecs(configurer -> configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder()))
				.defaultHeaders(httpHeaders -> httpHeaders.addAll(commonHeaders))

				.build();
		return OpenFireClient.builder().webClient(client).build();

	}

}
