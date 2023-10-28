package net.sasakonnect.wallet.config.websocket;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.domain.User;

@Slf4j
public class WalletSocketHandler implements WebSocketHandler {
	@Autowired
	private RedisWebSocketSessionStore sessionStore;

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		// Handle incoming messages here
		String receivedMessage = (String) message.getPayload();

		// Access the user details from the session attributes
		User user = (User) session.getAttributes().get("principal");

		if (user != null) {
			log.info("User ID: {}, First Name: {}", user.getId(), user.getFirstName());

			// Iterate through all WebSocket sessions and send the message
			sessionStore.getAllSessions().forEach(sessionList -> {
				for (WebSocketSession targetSession : sessionList) {
					try {
						targetSession.sendMessage(message);
					} catch (IOException e) {
						// Handle the exception, e.g., remove the session from the list
						log.error("Error sending message to session ID: {}", targetSession.getId(), e);

						// Check if the session is closed and remove it
						if (!targetSession.isOpen()) {
							sessionList.remove(targetSession);
						}
					}
				}
			});
		} else {
			log.warn("User not authenticated. User details are not available.");
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		this.sessionStore.addUserSessions(session);
		// this.sessionStore.addSession(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		this.sessionStore.removeUserSession(session);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean supportsPartialMessages() {
		// TODO Auto-generated method stub
		return false;
	}
}