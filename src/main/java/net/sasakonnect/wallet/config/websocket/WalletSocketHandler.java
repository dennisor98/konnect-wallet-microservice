package net.sasakonnect.wallet.config.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WalletSocketHandler implements WebSocketHandler {
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
// Handle incoming messages here
		String receivedMessage = (String) message.getPayload();
		log.info(receivedMessage);
// Process the message and send a response if needed
//session.sendMessage(new TextMessage("Received: " + receivedMessage));
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
// Perform actions when a new WebSocket connection is established
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
// Perform actions when a WebSocket connection is closed
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