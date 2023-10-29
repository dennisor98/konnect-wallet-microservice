package net.sasakonnect.wallet.config.websocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.WebSocketSession;

import net.sasakonnect.wallet.domain.User;

@Configuration
public class RedisWebSocketSessionStore {
	private HashMap<String, List<WebSocketSession>> webSockets = new HashMap<String, List<WebSocketSession>>();

	private RedisTemplate<String, WebSocketSessionInfo> redisTemplate;

	public RedisWebSocketSessionStore(RedisTemplate<String, WebSocketSessionInfo> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void addSession(WebSocketSession session) {
		String sessionId = session.getId();
		Map<String, Object> attributes = session.getAttributes();
		WebSocketSessionInfo sessionInfo = new WebSocketSessionInfo(sessionId, attributes);
		redisTemplate.opsForValue().set(sessionId, sessionInfo);
	}

	public WebSocketSessionInfo getSession(String sessionId) {
		return redisTemplate.opsForValue().get(sessionId);
	}

	public void removeSession(String sessionId) {
		redisTemplate.delete(sessionId);
	}

	public void addUserSessions(WebSocketSession session) {
		var user = ((User) session.getAttributes().get("principal"));
		if (webSockets.containsKey(user.getId())) {
			webSockets.get(user.getId()).add(session);
		} else {
			webSockets.put(user.getId(), new ArrayList<>(Arrays.asList(session)));
		}
	}

	public Collection<List<WebSocketSession>> getAllSessions() {
		return webSockets.values();
	}

	public List<WebSocketSession> getUserWebSession(String user_id) {
		List<WebSocketSession> sessions = this.webSockets.get(user_id);
		return sessions != null ? sessions : new ArrayList<>();
	}

	public void removeUserSession(WebSocketSession session) {
		var user = ((User) session.getAttributes().get("principal"));

		List<WebSocketSession> userSessions = webSockets.get(user.getId());
		if (userSessions != null) {
			userSessions.remove(session);
			if (userSessions.isEmpty()) {
				webSockets.remove(user.getId());
			}
		}
		// TODO Auto-generated method stub

	}
}