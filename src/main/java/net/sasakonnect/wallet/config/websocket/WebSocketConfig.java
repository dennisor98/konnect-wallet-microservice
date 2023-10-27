package net.sasakonnect.wallet.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// Register the endpoint and allow certain origins to connect to it.
		registry.addHandler(getWebSockeHandler(), "/wallet").setAllowedOrigins("*");

	}

	@Bean
	WalletSocketHandler getWebSockeHandler() {
		return new WalletSocketHandler();
	}
}