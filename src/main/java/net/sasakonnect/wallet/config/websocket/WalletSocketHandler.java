package net.sasakonnect.wallet.config.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.config.websocket.defination.MessagePayload;
import net.sasakonnect.wallet.events.MessageEvent;

@Slf4j
public class WalletSocketHandler implements WebSocketHandler {
	@Autowired
	private RedisWebSocketSessionStore sessionStore;

	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private SockectManager socketManager;

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		if (message instanceof BinaryMessage) {

		} else {
			var messagecocnent = MessagePayload.builder().build().buildFromJson((String) message.getPayload());
			var messageevent = new MessageEvent();

			messageevent.setMessagePayload(messagecocnent);
			this.publisher.publishEvent(messageevent);
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