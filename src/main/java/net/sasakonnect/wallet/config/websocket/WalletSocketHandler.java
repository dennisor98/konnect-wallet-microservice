package net.sasakonnect.wallet.config.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
			MessagePayload messagecocnent;
			try {
				messagecocnent = MessagePayload.builder().build().buildFromJson((String) message.getPayload());
				var messageevent = new MessageEvent();

				messageevent.setMessagePayload(messagecocnent);
				this.publisher.publishEvent(messageevent);
			} catch (JsonProcessingException e) {
				try {
					Map<String, Object> data = new HashMap<>();
					data.put("message", "message could not be understood");
					data.put("status", 422);
					ObjectMapper objectMapper = new ObjectMapper();

					WebSocketMessage<?> ws = new BinaryMessage(objectMapper.writeValueAsString(data).getBytes());

					session.sendMessage(ws);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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