package net.sasakonnect.wallet.config.websocket;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketMessage;

import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.config.websocket.defination.MessagePayload;

@Component
@Slf4j
public class SockectManager {
	@Autowired
	private RedisWebSocketSessionStore sessionStore;

	public void sendMessage(MessagePayload message) {
		log.info("send message to : {} ", message.receiverId);
		var sessions = sessionStore.getUserWebSession(message.receiverId);
		sessions.forEach((webSession) -> {

			WebSocketMessage<?> webSocketMessage = new BinaryMessage(message.toBytes());
			try {
				webSession.sendMessage(webSocketMessage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
	}
}
