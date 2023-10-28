package net.sasakonnect.wallet.config.websocket;

import java.io.Serializable;
import java.util.Map;

public class WebSocketSessionInfo implements Serializable {
	private static final long serialVersionUID = 6215275259644666154L;
	private String sessionId;
	private Map<String, Object> attributes;

	public WebSocketSessionInfo(String sessionId, Map<String, Object> attributes) {
		this.sessionId = sessionId;
		this.attributes = attributes;
	}

	public String getSessionId() {
		return sessionId;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}
//	public  WebSocketSession fromWebSocketSessionInfo(WebSocketSessionInfo sessionInfo) {
//        WebSocketSession newSession = new StandardWebSocketSession(null, attributes, null, null);
//        newSession.setSessionId(sessionInfo.getSessionId());
//        newSession.setUsername(sessionInfo.getUsername());
//        // Copy other attributes as needed
//        return newSession;
//    }
}
