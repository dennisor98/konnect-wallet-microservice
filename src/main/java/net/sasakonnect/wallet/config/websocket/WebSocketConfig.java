package net.sasakonnect.wallet.config.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import net.sasakonnect.wallet.config.JwtAuthenticationFilter;

@Configuration
@EnableWebSocket
@Controller

public class WebSocketConfig implements WebSocketConfigurer {
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// Register the endpoint and allow certain origins to connect to it.
		registry.addHandler(getWebSockeHandler(), "/test").setAllowedOrigins("*")

				.addInterceptors(jwtAuthenticationFilter);
	}

	@Bean
	public HandshakeInterceptor httpSessionHandshakeInterceptor() {
		return new HttpSessionHandshakeInterceptor();
	}

	@Bean
	WalletSocketHandler getWebSockeHandler() {
		return new WalletSocketHandler();
	}

	@Bean
	public WebSocketTransportRegistration customWebSocketTransportRegistration() {
		WebSocketTransportRegistration registration = new WebSocketTransportRegistration();
		// Set an effectively infinite session timeout
		registration.setSendTimeLimit(0).setSendBufferSizeLimit(512 * 1024);
		return registration;
	}

}